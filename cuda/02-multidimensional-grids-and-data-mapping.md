# 02. Multidimensional Grids and Data Mapping

## What

This document explains how CUDA organizes grids and blocks in multiple dimensions and how those multidimensional thread coordinates are mapped onto multidimensional data such as:

- images,
- matrices,
- and volumes.

It also covers the practical need to flatten multidimensional array accesses into 1D row-major offsets in CUDA C, and uses image blur and matrix multiplication as representative examples of increasing kernel complexity.

## Why It Matters

Once CUDA programs move beyond one-dimensional vector operations, the key difficulty is no longer merely launching many threads. The real problem becomes:

- how to organize those threads,
- how to map them to structured data,
- and how to translate multidimensional logical coordinates into correct memory addresses.

These ideas are foundational for:

- image-processing kernels,
- matrix and tensor kernels,
- convolution-style workloads,
- and most practical ML operators.

They are also the conceptual bridge between introductory CUDA kernels and later optimized CUDA or Triton programs.

## Core Mechanism

### Multidimensional grids and blocks

CUDA grids and blocks can each use up to three dimensions.

At launch time:

- the grid dimensions are specified in number of blocks,
- the block dimensions are specified in number of threads.

The host typically uses values of type `dim3` to describe these shapes.

Within a kernel, built-in variables expose these shapes and coordinates:

- `gridDim`
- `blockDim`
- `blockIdx`
- `threadIdx`

Each has fields:

- `.x`
- `.y`
- `.z`

This means a grid is a 3D array of blocks, and each block is a 3D array of threads.

### Why multidimensional organization exists

The natural reason is that many data structures are multidimensional.

Examples:

- vectors are usually 1D,
- images and matrices are usually 2D,
- volumes and simulation grids are often 3D.

When the thread organization matches the data organization, the indexing logic becomes clearer and more direct.

## Mapping Threads to 2D Data

For a 2D block and 2D grid, the standard global coordinates are:

```c
row = blockIdx.y * blockDim.y + threadIdx.y;
col = blockIdx.x * blockDim.x + threadIdx.x;
```

These are the direct 2D extension of the 1D global index formula from vector-add kernels.

Each thread uses:

- `row` to identify a vertical position,
- `col` to identify a horizontal position.

This is a natural fit for image and matrix processing.

### Example: covering a 62 x 76 image

Suppose an image has:

- height = 62
- width = 76

and the kernel uses:

```text
blockDim = (16, 16, 1)
```

Then the grid must cover:

- `ceil(76 / 16) = 5` blocks in the x direction
- `ceil(62 / 16) = 4` blocks in the y direction

So:

```text
gridDim = (5, 4, 1)
```

This launches threads for a logical coverage of:

- 80 columns
- 64 rows

which is larger than the image.

Therefore the kernel must use bounds checks such as:

```c
if (row < height && col < width) { ... }
```

so that extra threads outside the valid image region do no work.

## Flattening Multidimensional Arrays

Although a picture or matrix is conceptually 2D, memory is flat.

In C and CUDA C, multidimensional arrays are typically stored in row-major order.

That means rows are laid out one after another in memory.

For an element at:

```text
P[row, col]
```

in a row-major array of width `width`, the equivalent 1D offset is:

```text
row * width + col
```

This is the key flattening rule for 2D CUDA indexing.

### Example

If:

- `row = 16`
- `col = 0`
- `width = 76`

then the flattened offset is:

```text
16 * 76 + 0 = 1216
```

So the 2D element `P[16,0]` is stored at the 1D location `P[1216]`.

### Why flattening matters in CUDA C

For dynamically allocated arrays, the compiler does not automatically provide rich multidimensional indexing syntax in the same way it does for many statically sized arrays.

As a result, programmers often compute flat offsets manually in CUDA kernels.

This is not an arbitrary CUDA inconvenience. It reflects the reality that the underlying memory space is already one-dimensional.

## Image Kernel Example: Color to Grayscale

For a color image, each thread computes one grayscale output pixel.

The thread first computes:

```c
row = blockIdx.y * blockDim.y + threadIdx.y;
col = blockIdx.x * blockDim.x + threadIdx.x;
```

Then, if the coordinates are valid, it computes a flat offset:

```text
grayOffset = row * width + col
```

If the input image is RGB with 3 bytes per pixel, then the corresponding input offset begins at:

```text
rgbOffset = (row * width + col) * 3
```

The thread then reads the three consecutive values:

- red
- green
- blue

and computes luminance:

```text
L = 0.21r + 0.72g + 0.07b
```

So the mapping is:

```text
one thread -> one output pixel
```

with the main additional complexity being the conversion from 2D coordinates to flat memory offsets.

## Image Kernel Example: Blur

Blur is more complex because one output pixel depends on a patch of neighboring input pixels rather than only one input pixel.

For a blur radius `BLUR_SIZE`, the patch width is:

```text
2 * BLUR_SIZE + 1
```

If `BLUR_SIZE = 1`, each thread averages a `3 x 3` patch centered at its output pixel.

### Thread mapping stays the same

The output mapping is unchanged:

```text
one thread -> one output pixel
```

The thread still computes its own:

- `row`
- `col`

from `blockIdx`, `blockDim`, and `threadIdx`.

### What changes

Instead of reading one input location, the thread iterates over a neighborhood:

- rows from `row - BLUR_SIZE` to `row + BLUR_SIZE`
- columns from `col - BLUR_SIZE` to `col + BLUR_SIZE`

and accumulates valid pixels into:

- a running sum
- and a valid-pixel counter

The valid-pixel counter is necessary because edge pixels do not always have a full patch available.

### Edge handling

At the top-left corner of an image, for example, a `3 x 3` patch would partly lie outside the image.

The kernel must therefore test whether each candidate neighbor lies within the valid image bounds before adding it to the sum.

This produces several execution regions:

- fully interior threads that use the full patch,
- right-edge threads with partial horizontal coverage,
- bottom-edge threads with partial vertical coverage,
- corner-region threads with both row and column truncation.

Blur is important because it introduces a more realistic CUDA pattern:

- one output per thread remains true,
- but each thread now performs a nontrivial local computation over neighboring inputs.

This is an early example of a neighborhood or stencil-style access pattern.

## Matrix Multiplication

Matrix multiplication is a fundamental CUDA example because it is:

- an important linear algebra primitive,
- central to many numerical algorithms,
- and deeply relevant to deep learning.

If:

- `M` has shape `i x j`
- `N` has shape `j x k`

then:

- `P` has shape `i x k`

and each output element is:

```text
P[row, col] = sum over t of M[row, t] * N[t, col]
```

### Thread mapping

The same 2D thread mapping strategy is used:

```text
one thread -> one output element P[row, col]
```

The thread computes:

```c
row = blockIdx.y * blockDim.y + threadIdx.y;
col = blockIdx.x * blockDim.x + threadIdx.x;
```

Then it performs the inner product between:

- row `row` of `M`
- column `col` of `N`

### Row-major indexing for M

The element:

```text
M[row, t]
```

in row-major storage becomes:

```text
M[row * Width + t]
```

because:

- `row * Width` skips all earlier rows,
- `t` selects the element within the current row.

### Row-major indexing for N

The element:

```text
N[t, col]
```

becomes:

```text
N[t * Width + col]
```

because:

- `t * Width` moves to row `t`,
- `col` selects the required column in that row.

### Per-thread computation

So the thread’s loop effectively does:

```text
Pvalue += M[row, t] * N[t, col]
```

for all values of `t` across the shared inner dimension.

After the loop, the thread writes:

```text
P[row, col]
```

using the flattened output index:

```text
row * Width + col
```

This is a useful progression in complexity:

- vector addition:
  one output element, one simple arithmetic step
- grayscale conversion:
  one output pixel, one local RGB computation
- blur:
  one output pixel, neighborhood aggregation
- matrix multiplication:
  one output matrix element, full inner-product loop

## 3D Extension

The same ideas extend naturally to 3D data.

Threads can compute:

- `plane`
- `row`
- `col`

using the `.z`, `.y`, and `.x` fields of block and thread indices.

A 3D row-major flattening pattern takes the form:

```text
offset = plane * (height * width) + row * width + col
```

The conceptual difficulty is unchanged:

- map thread coordinates to logical data coordinates,
- then flatten those coordinates into one-dimensional memory offsets.

## Tradeoffs

- Multidimensional grids and blocks make the data mapping more natural, but they also make indexing formulas easier to get wrong.
- Flattened row-major indexing is flexible and general, but it puts more burden on the programmer than direct multidimensional syntax.
- Using one thread per output element keeps the mapping simple, but it may not be the most efficient strategy for very large or highly optimized kernels.
- Blur and matrix multiplication are conceptually clean examples, but they already hint at overlapping memory accesses and performance issues that will need later optimization.

## Common Mistakes

- Mixing up the x and y dimensions when mapping threads to rows and columns.
- Forgetting that C uses row-major layout.
- Using the wrong width in flattened index calculations.
- Forgetting bounds checks when grid coverage exceeds the valid data size.
- Assuming multidimensional data automatically implies multidimensional memory layout at the source-code level for dynamically allocated arrays.
- Treating each thread as if it only ever performs one trivial arithmetic operation.

## ML Systems Connection

These ideas are directly relevant to ML systems.

- Images, feature maps, activation tensors, and matrices are naturally multidimensional.
- Convolution, blur-like neighborhood operations, and stencil-like access patterns show up throughout vision and scientific ML workloads.
- Matrix multiplication is one of the core computational patterns behind linear layers, attention projections, and many tensor operations in deep learning.
- Row-major flattening and coordinate mapping are foundational for understanding how CUDA kernels and Triton kernels actually access tensor memory.

This document therefore represents a key transition:

- from one-dimensional introductory CUDA thinking
- to multidimensional data mapping and kernel construction that more closely resembles real ML computation.
