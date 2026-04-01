# Triton Embedding Lookup Kernel Walkthrough

## What

This document explains a tiny Triton embedding lookup kernel line by line.

The goal is not to present a production kernel. The goal is to make the control flow, pointer arithmetic, and data movement concrete enough that the kernel can be read without guessing.

The kernel takes:

- token IDs already on GPU,
- an embedding table already on GPU,
- and an output tensor for the initial hidden states,

and performs the basic gather:

```text
token IDs -> rows of W_embed -> X^(0)
```

## Why It Matters

Embedding lookup is one of the best first Triton kernels to study because it uses the core syntax without introducing heavy math.

It makes several important things visible:

- how one program instance is assigned one token position,
- how a token ID selects one row of the embedding table,
- how `tl.arange` selects positions within that row,
- how pointers are built from row and column offsets,
- and how loads and stores move data from the embedding table into the hidden-state output.

This is also a transformer-relevant operator rather than an artificial toy.

## Kernel

```python
import triton
import triton.language as tl


@triton.jit
def embedding_lookup_kernel(
    token_ids_ptr,
    embed_ptr,
    out_ptr,
    stride_embed_row,
    stride_embed_col,
    stride_out_row,
    stride_out_col,
    d_model,
    BLOCK_D: tl.constexpr,
):
    pid_tok = tl.program_id(0)

    token_id = tl.load(token_ids_ptr + pid_tok)
    cols = tl.arange(0, BLOCK_D)
    mask = cols < d_model

    embed_ptrs = embed_ptr + token_id * stride_embed_row + cols * stride_embed_col
    out_ptrs = out_ptr + pid_tok * stride_out_row + cols * stride_out_col

    vals = tl.load(embed_ptrs, mask=mask, other=0.0)
    tl.store(out_ptrs, vals, mask=mask)
```

## Core Mechanism

This kernel follows the standard Triton pattern:

1. identify which program instance is running,
2. decide which token position that instance owns,
3. load the token ID for that position,
4. compute which embedding-table columns to fetch,
5. build pointers into the embedding table and output tensor,
6. load embedding values,
7. store those values into the output hidden-state tensor.

## Data Usage Example

Assume the prompt is:

```text
"cat sat"
```

Assume the tokenizer has already converted it into:

```text
input_ids = [7, 3]
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

The output tensor `X^(0)` will have shape `[2, 4]` because there are two token positions and each token becomes a hidden vector of length 4.

Assume standard row-major layout:

- `stride_embed_row = 4`
- `stride_embed_col = 1`
- `stride_out_row = 4`
- `stride_out_col = 1`

Assume:

```text
BLOCK_D = 4
```

Then the kernel launches one program instance per token position:

- program instance `pid_tok = 0` handles token position 0
- program instance `pid_tok = 1` handles token position 1

### Program instance for token position 0

For:

```text
pid_tok = 0
```

the kernel does:

```python
token_id = tl.load(token_ids_ptr + pid_tok)
```

This reads:

```text
token_id = input_ids[0] = 7
```

Then:

```python
cols = tl.arange(0, BLOCK_D)
```

gives:

```text
cols = [0, 1, 2, 3]
```

The embedding pointers become:

```python
embed_ptrs = embed_ptr + token_id * stride_embed_row + cols * stride_embed_col
```

Substitute the values:

```text
embed_ptrs = embed_ptr + 7 * 4 + [0, 1, 2, 3] * 1
           = embed_ptr + [28, 29, 30, 31]
```

Those addresses contain:

```text
[0.7, 0.7, 0.7, 0.7]
```

So:

```python
vals = tl.load(embed_ptrs, mask=mask, other=0.0)
```

returns:

```text
vals = [0.7, 0.7, 0.7, 0.7]
```

Now the output pointers:

```python
out_ptrs = out_ptr + pid_tok * stride_out_row + cols * stride_out_col
```

become:

```text
out_ptrs = out_ptr + 0 * 4 + [0, 1, 2, 3]
         = out_ptr + [0, 1, 2, 3]
```

So:

```python
tl.store(out_ptrs, vals, mask=mask)
```

writes:

```text
X^(0)[0] = [0.7, 0.7, 0.7, 0.7]
```

### Program instance for token position 1

For:

```text
pid_tok = 1
```

the kernel loads:

```text
token_id = input_ids[1] = 3
```

The embedding pointers become:

```text
embed_ptrs = embed_ptr + 3 * 4 + [0, 1, 2, 3]
           = embed_ptr + [12, 13, 14, 15]
```

These addresses contain:

```text
[0.3, 0.3, 0.3, 0.3]
```

The output pointers become:

```text
out_ptrs = out_ptr + 1 * 4 + [0, 1, 2, 3]
         = out_ptr + [4, 5, 6, 7]
```

So the kernel writes:

```text
X^(0)[1] = [0.3, 0.3, 0.3, 0.3]
```

Final output:

```text
X^(0) =
[
  [0.7, 0.7, 0.7, 0.7],
  [0.3, 0.3, 0.3, 0.3]
]
```

## Line-by-Line Explanation

### `@triton.jit`

```python
@triton.jit
```

This tells Triton to compile the function as a GPU kernel.

### Pointer arguments

```python
token_ids_ptr,
embed_ptr,
out_ptr,
```

These are base pointers:

- `token_ids_ptr` points to the token ID tensor on GPU,
- `embed_ptr` points to the embedding table on GPU,
- `out_ptr` points to the output tensor on GPU.

### Stride arguments

```python
stride_embed_row,
stride_embed_col,
stride_out_row,
stride_out_col,
```

These describe memory layout.

For a standard contiguous row-major embedding table of shape `[vocab_size, d_model]`:

- row stride is `d_model`
- column stride is `1`

The same idea applies to the output tensor.

### Hidden size

```python
d_model
```

This is the embedding dimension, also the initial hidden dimension.

It tells the kernel how many values exist in one embedding row.

### Compile-time block size

```python
BLOCK_D: tl.constexpr
```

This is how many embedding-dimension positions one program instance handles at once.

In this tiny example, `BLOCK_D = d_model = 4`, so each program instance handles the whole row in one shot.

In larger real kernels, `BLOCK_D` may be smaller than `d_model`, so one row is processed in chunks.

### Which token position does this program own?

```python
pid_tok = tl.program_id(0)
```

This gives the program-instance ID along axis 0.

In this kernel, that ID is interpreted as the token position in the prompt or batch sequence.

So:

- `pid_tok = 0` means token position 0
- `pid_tok = 1` means token position 1

### Load the token ID

```python
token_id = tl.load(token_ids_ptr + pid_tok)
```

This reads one integer token ID from the input token-ID tensor.

The program instance does not directly know which embedding row to fetch until this load happens.

### Build embedding-dimension offsets

```python
cols = tl.arange(0, BLOCK_D)
```

This creates the local positions inside the embedding row.

If `BLOCK_D = 4`, then:

```text
cols = [0, 1, 2, 3]
```

### Create the validity mask

```python
mask = cols < d_model
```

This protects the kernel from loading or storing beyond the end of the row when `BLOCK_D` is larger than the remaining valid dimension positions.

In the tiny example with `d_model = 4` and `cols = [0, 1, 2, 3]`, all mask values are `True`.

### Compute embedding-table addresses

```python
embed_ptrs = embed_ptr + token_id * stride_embed_row + cols * stride_embed_col
```

This is the 2D address formula:

```text
base + row * stride_row + col * stride_col
```

Here:

- row is `token_id`
- col is each value in `cols`

So this line computes the addresses of the selected embedding row.

### Compute output addresses

```python
out_ptrs = out_ptr + pid_tok * stride_out_row + cols * stride_out_col
```

This computes where the hidden vector for this token position should be written in the output tensor.

The important distinction is:

- `token_id` selects the row in `W_embed`
- `pid_tok` selects the row in the output sequence tensor

Those are not the same thing.

For `"cat sat"`:

- token position 0 contains token ID 7
- token position 1 contains token ID 3

So output row and embedding row are chosen by different values.

### Load embedding values

```python
vals = tl.load(embed_ptrs, mask=mask, other=0.0)
```

This reads the selected chunk of the embedding row.

`other=0.0` says what value to use for masked-off lanes.

### Store output values

```python
tl.store(out_ptrs, vals, mask=mask)
```

This writes the loaded embedding values into the output hidden-state tensor.

After this, the chosen token position has become its dense hidden vector.

## Tradeoffs

- This kernel is intentionally simple and easy to read, but real embedding kernels may use more complicated launch structures, vectorization, or chunking across the hidden dimension.
- One-program-per-token is conceptually clean, but production kernels may partition work differently to improve bandwidth use.
- The walkthrough clarifies syntax and memory layout, but it does not yet cover cache behavior, coalescing quality, or gather irregularity in detail.

## Common Mistakes

- Confusing token position with token ID. The program instance usually owns a token position, not a vocabulary row directly.
- Forgetting that `token_id` is loaded from memory, while `pid_tok` comes from the launch grid.
- Treating `d_model` and `BLOCK_D` as always identical. In real kernels, one is the true hidden dimension and the other is the chunk size chosen by the kernel.
- Forgetting that output-row selection and embedding-row selection use different indices.
- Thinking embedding lookup is mostly arithmetic. In practice it is often dominated by memory access.

## ML Systems Connection

This kernel is the first transformer-side step that turns symbolic model input into dense numeric state.

From a systems perspective, it is useful because it shows:

- how token IDs become rows of a large GPU-resident table,
- why embedding lookup behaves like a gather rather than a dense matrix multiply,
- why memory layout and bandwidth matter immediately,
- and how Triton expresses the path from token IDs to `X^(0)` using only a small set of core constructs.

That same kernel-reading pattern carries into later transformer operators:

- RMSNorm adds reductions and scaling,
- attention adds more complicated indexing and softmax,
- QKV and MLP layers add tiled matrix multiplication.

Embedding lookup is therefore a small but accurate first example of the larger Triton mental model:

```text
program instance -> offsets -> pointers -> loads -> local work -> stores
```
