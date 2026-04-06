# Triton Launch Configuration and Kernel Reading

## What

This note explains the first Triton concepts that matter after basic syntax:

- `@triton.jit`
- `tl.constexpr`
- launch grids
- `num_warps`
- `num_stages`
- and a practical step-by-step method for reading simple Triton kernels

The goal is not to catalog every Triton feature. The goal is to make the execution model concrete enough that beginner kernels can be read without treating them like compressed magic.

## Why It Matters

Once you understand:

- `tl.program_id`
- `tl.arange`
- pointer arithmetic
- loads, stores, and masks

the next obstacle is usually not syntax. It is launch structure.

Many beginners can read individual lines but still cannot answer:

- what one program instance owns
- how many program instances exist
- why compile-time constants appear
- what `num_warps` and `num_stages` are really tuning

Without that layer, Triton kernels still feel fragmented.

## Core Mechanism

The simplest useful Triton mental model is:

1. a Triton kernel describes what one **program instance** does
2. the **launch grid** decides how many program instances exist
3. each program instance owns one output chunk or tile
4. the kernel computes offsets for that tile
5. those offsets are turned into pointers
6. values are loaded, transformed, and stored

So the most important first question when reading a Triton kernel is:

```text
what output tile does one program instance own?
```

Everything else is easier once that is clear.

## `@triton.jit`

```python
@triton.jit
def kernel(...):
    ...
```

This decorator tells Triton to treat the function as kernel code and JIT-compile it for GPU execution.

That means the body should not be read as ordinary Python control flow.

A better reading is:

```text
this function describes what one Triton program instance does,
and Triton will compile that description into GPU kernel code
```

This is the boundary between:

- host-side Python logic
- and device-side Triton kernel logic

## `tl.constexpr`

When you see:

```python
BLOCK_SIZE: tl.constexpr
```

it means:

```text
this parameter is known at compile time and shapes the compiled kernel
```

This is different from ordinary runtime arguments such as:

- pointers
- strides
- tensor sizes

`tl.constexpr` is commonly used for:

- tile sizes
- static loop structure
- vector widths from `tl.arange`
- accumulator shapes
- compile-time specialization choices

So `tl.constexpr` is not just “a constant.” It is:

```text
a kernel-shaping compile-time parameter
```

## Launch Grid

The launch grid tells Triton how many program instances to create.

If:

```python
grid = (triton.cdiv(n, BLOCK_SIZE),)
```

then Triton launches enough 1D program instances to cover all `n` outputs in chunks of `BLOCK_SIZE`.

This is a key distinction:

- the grid is not the output shape directly
- it is the number of tiles/chunks needed to cover the output

### Example

If:

- `n = 1000`
- `BLOCK_SIZE = 256`

then:

```python
grid = (triton.cdiv(1000, 256),) = (4,)
```

So there are 4 program instances:

- instance 0 covers about `0..255`
- instance 1 covers `256..511`
- instance 2 covers `512..767`
- instance 3 covers `768..1023`

The last chunk overshoots the true range, so masking is needed.

## `tl.program_id` and the Grid

These two ideas must always be read together.

Inside the kernel:

```python
pid = tl.program_id(axis=0)
```

means:

- `grid` says how many possible `pid` values exist
- `pid` says which chunk the current program instance owns

Then code like:

```python
offs = pid * BLOCK_SIZE + tl.arange(0, BLOCK_SIZE)
```

means:

- choose the chunk start from `pid`
- choose local positions within the chunk using `tl.arange`

This is the standard 1D Triton ownership pattern.

## `num_warps`

`num_warps` is a launch-time tuning parameter that tells Triton roughly how many GPU warps should cooperate to execute one program instance.

That means it controls:

- local execution resources per instance

not:

- how many program instances exist globally

So:

- `grid` controls instance count
- `num_warps` controls execution width/resources per instance

This is usually tuned based on:

- tile size
- compute intensity
- register pressure
- and occupancy tradeoffs

A larger tile often benefits from more warps, but more is not automatically better.

## `num_stages`

`num_stages` is another launch-time tuning parameter.

It controls roughly how deeply Triton pipelines load-and-compute stages inside one program instance.

This matters most in kernels with repeated tiled loops, especially:

- matrix multiplication
- attention-style tiled kernels
- reduction loops over a `K` dimension

The easiest mental model is:

- `num_warps` = how many workers help execute one instance
- `num_stages` = how many pipeline stages of tile work are kept in flight

Like `num_warps`, this is a tuning knob:

- more stages can help overlap memory and compute
- but more stages can also increase resource pressure

## The 9-Step Reading Workflow

The most effective way to read a simple Triton kernel is not top-to-bottom as plain Python.

Instead, read it in this order.

### 1. What output does one program instance own?

Ask:

```text
what chunk or tile of the output is assigned to one program instance?
```

Examples:

- one vector block
- one token position
- one row chunk
- one 2D tile

### 2. What is the launch grid?

Ask:

```text
how many program instances are launched to cover the whole output?
```

This gives the global decomposition.

### 3. How does `tl.program_id` choose the current tile?

Ask:

```text
which logical tile coordinate does this program instance represent?
```

### 4. What local offsets are created?

Look for:

```python
tl.arange(...)
```

Ask:

```text
what positions inside the tile do these offsets represent?
```

### 5. How are pointers built?

Look for pointer arithmetic like:

```python
base_ptr + row * stride_row + col * stride_col
```

Ask:

```text
which tensor is this accessing, and which logical indices map to these addresses?
```

### 6. What does the mask protect?

Ask:

```text
which part of this tile may go out of bounds?
```

This usually identifies the true valid region near edges.

### 7. What gets loaded?

Look at `tl.load(...)` and identify:

- input tensor
- load shape
- contiguous vs gather-like pattern

### 8. What computation happens locally?

Only after ownership and addresses are clear should you focus on:

- elementwise math
- reductions
- `tl.dot`
- normalization
- accumulation

### 9. What gets stored?

Finally ask:

```text
what result tile is written back, and where?
```

That completes the load-compute-store loop for one program instance.

## Example: Embedding Lookup Through the Workflow

Using the embedding lookup kernel:

### Ownership

One program instance owns:

- one token position
- and one chunk of the embedding dimension

### Grid

Conceptually:

- one program instance per token position

### `program_id`

```python
pid_tok = tl.program_id(0)
```

chooses the token position.

### Offsets

```python
cols = tl.arange(0, BLOCK_D)
```

chooses local positions inside the embedding row.

### Pointers

```python
embed_ptrs = embed_ptr + token_id * stride_embed_row + cols * stride_embed_col
out_ptrs = out_ptr + pid_tok * stride_out_row + cols * stride_out_col
```

These pick:

- the source embedding row
- the destination hidden-state row

### Mask

```python
mask = cols < d_model
```

protects the final partial chunk if `BLOCK_D` exceeds the remaining valid dimension.

### Load

```python
vals = tl.load(embed_ptrs, mask=mask, other=0.0)
```

loads the embedding values.

### Compute

There is almost no arithmetic here.
The kernel is mostly a gather.

### Store

```python
tl.store(out_ptrs, vals, mask=mask)
```

writes the hidden-state output for that token position.

## Common Mistakes

- Reading the function body as ordinary Python before identifying tile ownership.
- Confusing the grid with the output shape itself.
- Treating `num_warps` as global parallelism instead of per-instance execution width.
- Treating `num_stages` as loop count instead of pipeline depth.
- Treating `tl.constexpr` as just “constant” rather than compile-time specialization.
- Trying to understand the arithmetic before understanding the pointers and masks.

## ML Systems Connection

These ideas are not just Triton syntax trivia.

They are the first step toward reading real kernels for:

- embedding lookup
- softmax
- RMSNorm
- attention
- and tiled matrix multiplication

Most practical Triton kernels are still variations on the same pattern:

```text
program instance -> tile ownership -> offsets -> pointers -> load -> local compute -> store
```

Once that pattern becomes natural, real kernels become much less intimidating.

## Short Takeaway

To read Triton confidently, separate global launch structure from local kernel work: the grid decides how many tile-owning program instances exist, `@triton.jit` marks the compiled kernel body, `tl.constexpr` shapes it at compile time, `num_warps` and `num_stages` tune how one instance executes, and the kernel itself should be read as ownership, offsets, pointers, loads, compute, and stores.
