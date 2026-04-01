# Triton Syntax Primer: Program IDs, Offsets, Loads, Stores, Masks, and Tiles

## What

This document is a first-pass syntax guide for reading simple Triton kernels.

The goal is not full coverage of the language. The goal is to make the most common beginner-level Triton expressions concrete:

- `tl.program_id`
- `tl.arange`
- pointer arithmetic
- `tl.load`
- `tl.store`
- masks
- broadcasting with `[:, None]` and `[None, :]`
- reductions such as `tl.sum` and `tl.max`
- elementwise math such as `tl.exp`, `tl.sqrt`, and `tl.rsqrt`
- `tl.dot`
- `tl.zeros`

These are enough to start reading many simple kernels used for vector operations, embedding lookup, normalization, and tiled matrix multiplication.

## Why It Matters

Triton code looks short, but the syntax compresses several ideas at once:

- which program instance is running,
- which chunk of the tensor that instance owns,
- which memory addresses it should read,
- which values are valid near tensor boundaries,
- and whether the kernel is doing elementwise math, a reduction, or a tiled matrix multiply.

Without a concrete mental model for these expressions, Triton code can look much more mysterious than it really is.

For ML systems work, Triton matters because it sits between high-level tensor code and lower-level CUDA execution. It is often the most direct way to express how a GPU kernel reads tiles, computes on them, and writes results back.

## Core Mechanism

The simplest useful model of a Triton kernel is:

1. identify the current program instance,
2. compute the indices that program instance owns,
3. convert those indices into memory addresses,
4. load data from those addresses,
5. do local math,
6. store the result.

Most beginner-level Triton syntax is just a compact way to express one of those six steps.

### `tl.program_id(axis=0)`: which chunk of work is this?

```python
pid = tl.program_id(axis=0)
```

This gives the current Triton program instance ID along one launch axis.

It is easiest to read `pid` as:

> which chunk of work does this program instance own?

If the kernel processes a vector in blocks of 4:

```python
BLOCK = 4
offs = pid * BLOCK + tl.arange(0, BLOCK)
```

then:

- `pid = 0` gives offsets `[0, 1, 2, 3]`
- `pid = 1` gives offsets `[4, 5, 6, 7]`
- `pid = 2` gives offsets `[8, 9, 10, 11]`

So `pid` is not a pointer and not data from the tensor. It is the chunk number assigned to the current program instance.

### `tl.arange(start, end)`: local offsets inside a chunk

```python
cols = tl.arange(0, 4)
```

This creates a vector of consecutive indices:

```text
[0, 1, 2, 3]
```

It does not read memory. It only creates offsets.

In practice, `tl.arange` is usually combined with `pid`:

```python
offs = pid * BLOCK + tl.arange(0, BLOCK)
```

This means:

- `pid * BLOCK` chooses the chunk start,
- `tl.arange(0, BLOCK)` chooses local positions inside that chunk,
- and `offs` becomes the final vector of global indices.

### Pointer arithmetic: how Triton finds data

For a 1D tensor:

```python
ptrs = X_ptr + offs
```

means:

- `X_ptr` is the base pointer of tensor `X`,
- `offs` is a vector of element offsets,
- and `ptrs` becomes a vector of addresses.

For a 2D tensor, the standard address formula is:

```python
ptr = X_ptr + row * stride_row + col * stride_col
```

This is the address of `X[row, col]`.

If a matrix is row-major with shape `[3, 4]`:

```text
X =
[
  [10, 11, 12, 13],
  [20, 21, 22, 23],
  [30, 31, 32, 33]
]
```

then:

- `stride_row = 4`
- `stride_col = 1`

because moving by one row skips 4 elements, while moving by one column skips 1 element.

So `X[2, 3]` is found at:

```text
base + 2 * 4 + 3 * 1 = base + 11
```

### `tl.load(...)`: read values from those addresses

```python
vals = tl.load(ptrs)
```

This reads values from the addresses in `ptrs`.

If:

```text
X = [10, 20, 30, 40, 50, 60, 70, 80]
offs = [0, 1, 2, 3]
```

then:

```python
vals = tl.load(X_ptr + offs)
```

returns:

```text
[10, 20, 30, 40]
```

The important shift from scalar assembly is that Triton usually loads several addresses at once.

### `tl.store(...)`: write values back

```python
tl.store(ptrs, vals)
```

This writes `vals` to the addresses in `ptrs`.

If:

```text
offs = [4, 5, 6, 7]
vals = [50, 60, 70, 80]
```

then:

```python
tl.store(Y_ptr + offs, vals)
```

writes those values into `Y[4]`, `Y[5]`, `Y[6]`, and `Y[7]`.

### Masks: safe handling of edge tiles

If tensor length is not an exact multiple of block size, the last chunk may contain out-of-bounds indices.

That is why kernels often compute:

```python
mask = offs < n
```

For example, if:

```text
n = 10
offs = [8, 9, 10, 11]
```

then:

```text
mask = [True, True, False, False]
```

This can be passed into loads and stores:

```python
vals = tl.load(X_ptr + offs, mask=mask, other=0.0)
tl.store(Y_ptr + offs, vals, mask=mask)
```

Meaning:

- only valid positions are read,
- invalid positions are replaced with `other`,
- only valid positions are written back.

### `[:, None]` and `[None, :]`: turn 1D offsets into a 2D tile

For matrix tiles, Triton often starts from two 1D offset vectors:

```python
offs_m = tl.arange(0, BLOCK_M)
offs_n = tl.arange(0, BLOCK_N)
```

Then:

```python
offs_m[:, None]
```

turns `offs_m` into a column-shaped array, and:

```python
offs_n[None, :]
```

turns `offs_n` into a row-shaped array.

This is useful because:

```python
ptrs = X_ptr + offs_m[:, None] * stride_row + offs_n[None, :] * stride_col
```

creates the full 2D grid of addresses for a matrix tile.

Conceptually:

- `[:, None]` inserts a new trailing axis,
- `[None, :]` inserts a new leading axis,
- and broadcasting creates all row-column combinations.

### `tl.sum(...)` and `tl.max(...)`: reductions

These reduce values along an axis.

Example:

```python
s = tl.sum(x, axis=0)
m = tl.max(x, axis=0)
```

If:

```text
x = [2, 7, 4, 1]
```

then:

- `tl.sum(x, axis=0)` gives `14`
- `tl.max(x, axis=0)` gives `7`

These are important in normalization and softmax.

### `tl.sqrt(...)`, `tl.rsqrt(...)`, and `tl.exp(...)`: common elementwise math

These operate elementwise:

```python
tl.sqrt(x)
tl.rsqrt(x)
tl.exp(x)
```

Meanings:

- `tl.sqrt(x)` computes square root,
- `tl.rsqrt(x)` computes reciprocal square root,
- `tl.exp(x)` computes the exponential.

These show up repeatedly in transformer kernels:

- `tl.rsqrt` in RMSNorm,
- `tl.exp` in softmax,
- `tl.max` followed by `tl.exp` in stable softmax.

### `tl.maximum(x, y)`: elementwise max, not reduction max

```python
z = tl.maximum(x, y)
```

This compares `x` and `y` elementwise and keeps the larger value at each position.

That is different from:

```python
tl.max(x, axis=0)
```

which reduces across an axis.

### `tl.dot(a, b)` and `tl.zeros(...)`: tiled matrix multiplication

Triton matrix multiply kernels usually create an accumulator tile:

```python
acc = tl.zeros((BLOCK_M, BLOCK_N), dtype=tl.float32)
```

Then repeatedly update it:

```python
acc += tl.dot(a, b)
```

This means:

- `a` is a tile from one input matrix,
- `b` is a tile from the other input matrix,
- `tl.dot(a, b)` computes a partial matrix product,
- and `acc` stores the running output tile.

`tl.dot` is not elementwise multiplication. It performs multiply-and-sum across a reduction dimension.

## Data Usage Example

### Embedding lookup from token ID to hidden vector

Suppose the tokenizer converts:

```text
"cat sat"
```

into token IDs:

```text
[7, 3]
```

Assume the embedding table has shape `[8, 4]`:

```text
W_embed =
0 -> [0.0, 0.0, 0.0, 0.0]
1 -> [0.1, 0.1, 0.1, 0.1]
2 -> [0.2, 0.2, 0.2, 0.2]
3 -> [0.3, 0.3, 0.3, 0.3]
4 -> [0.4, 0.4, 0.4, 0.4]
5 -> [0.5, 0.5, 0.5, 0.5]
6 -> [0.6, 0.6, 0.6, 0.6]
7 -> [0.7, 0.7, 0.7, 0.7]
```

This means:

- vocabulary size is 8,
- embedding size is 4,
- each token row stores 4 learned weights,
- and in standard row-major layout the row stride is also 4 elements.

For token ID `7`:

```python
token_id = 7
cols = tl.arange(0, 4)
ptrs = embed_ptr + token_id * stride_row + cols
vals = tl.load(ptrs)
```

With `stride_row = 4`, this becomes:

```text
ptrs = embed_ptr + 7 * 4 + [0, 1, 2, 3]
     = embed_ptr + [28, 29, 30, 31]
```

Those addresses contain:

```text
[0.7, 0.7, 0.7, 0.7]
```

So `vals` becomes the embedding vector for `"cat"`.

The same logic applies to token ID `3`, which loads:

```text
[0.3, 0.3, 0.3, 0.3]
```

The output hidden-state tensor after lookup is therefore:

```text
X^(0) =
[
  [0.7, 0.7, 0.7, 0.7],
  [0.3, 0.3, 0.3, 0.3]
]
```

This is a good first Triton example because it is mostly about addresses, loads, and stores rather than heavy math.

## Tradeoffs

- A syntax-first view makes Triton easier to parse, but it can hide hardware details such as warps, memory transactions, and occupancy.
- A pointer-arithmetic-first approach is excellent for clarity, but beginners may temporarily confuse tensor shape, tensor stride, and kernel launch structure.
- Embedding lookup is a good first example because it is simple and concrete, but it does not yet show reductions or Tensor Core-style matrix multiply behavior.

## Common Mistakes

- Treating `pid` as data instead of as the program-instance ID.
- Thinking `tl.arange` reads memory rather than creating index offsets.
- Confusing `ptr` with `pid`: pointers describe where data lives, while `pid` describes which chunk of work the current program owns.
- Forgetting that masks are essential for the final partial tile.
- Mixing up `tl.max(x, axis=...)` with `tl.maximum(x, y)`.
- Confusing conceptual dimensions with memory strides. A row stride often equals embedding size in standard row-major layout, but stride is a memory-layout property, not the definition of the embedding itself.
- Thinking `tl.dot(a, b)` is elementwise multiplication rather than multiply-and-sum across a reduction dimension.

## ML Systems Connection

These syntax patterns are the bridge between high-level transformer operations and hardware-conscious kernel reasoning.

For example:

- embedding lookup is mostly pointer arithmetic plus memory loads,
- RMSNorm combines loads, elementwise math, reduction, and reciprocal square root,
- softmax combines reduction max, exponentials, and reduction sum,
- QKV projection and MLP projections combine tiled loads, `tl.dot`, and tiled stores.

In other words, many transformer kernels can be understood as variations on the same small set of Triton building blocks:

- choose a program instance,
- compute offsets,
- build pointers,
- load tiles,
- reduce or multiply,
- and store results.

That is why a clear first-pass understanding of these few syntax forms goes much further than memorizing isolated code snippets.
