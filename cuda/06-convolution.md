# 06. Convolution

## What

Convolution is a local weighted-sum operation in which each output element is computed from a neighborhood of input elements and a small filter.

In CUDA, convolution is an important optimization pattern because it combines:

- abundant output parallelism,
- strong input-data reuse,
- and awkward boundary and halo behavior.

That makes it a good case study for:

- thread mapping,
- constant memory,
- shared-memory tiling,
- cache-aware halo handling,
- and arithmetic-intensity analysis.

## Why It Matters

Convolution appears directly in:

- image processing,
- signal processing,
- computer vision,
- video processing,
- and deep learning.

But it also represents a broader parallel pattern:

- local neighborhood computation,
- overlapping input reuse,
- and edge-condition handling.

So the chapter is really teaching a reusable GPU optimization idea, not just one operator.

## The Core Mathematical Idea

Each output element is a weighted sum of nearby input elements.

For 1D convolution with radius `r`:

```text
y[i] = Σ_j f[j] * x[i + offset(j)]
```

where the filter size is `2r + 1`.

For 2D convolution, the same idea extends over both spatial dimensions:

- each output pixel depends on a local 2D patch of the input,
- multiplied elementwise by a 2D filter,
- then summed.

The clean intuition is:

```text
one output element
=
one local patch of input
dot
the filter
```

## Why Convolution Parallelizes Well

Each output element can be computed independently once the input and filter are available.

That means a natural CUDA mapping is:

- one thread per output element
- 2D thread blocks for 2D images
- 2D grids for full-image coverage

So convolution has strong output-side data parallelism.

## Why Naive Convolution Is Still Slow

The naive kernel is simple:

- each thread computes one output element
- it loads the local input neighborhood from global memory
- it multiplies by the filter
- and stores the result

The problem is not lack of parallelism.
The problem is memory reuse.

Neighboring output elements use heavily overlapping input neighborhoods, so neighboring threads repeatedly load many of the same input values from global memory.

That makes naive convolution strongly memory-bandwidth-bound.

## Boundary Conditions and Ghost Cells

Convolution depends on neighboring input elements, so boundaries naturally create missing values.

At the edges:

- some required input elements lie outside the valid array

These missing positions are often treated as:

- zero,
- copied edge values,
- or some other padding rule.

The chapter uses zero-padding and refers to these missing positions as ghost cells.

This matters because ghost-cell handling affects:

- control divergence,
- memory access logic,
- and tile design.

## The Basic Parallel Kernel

The first CUDA kernel uses the obvious mapping:

- a 2D block/grid layout
- one thread per output pixel

Each thread:

1. computes its `(outRow, outCol)`
2. iterates over the filter window
3. checks whether each needed input value is valid
4. skips ghost cells
5. accumulates into a register
6. writes one output element

This kernel is correct and easy to understand.

### Strength

- simple output mapping
- local register accumulation
- direct correspondence between threads and output pixels

### Weakness

- repeated global-memory loads of overlapping input patches
- low arithmetic intensity
- some divergence near edges

The chapter’s main point is that this kernel is parallel but inefficient.

## Constant Memory for the Filter

The filter is an ideal constant-memory object because it has three properties:

- it is small,
- it is read-only during kernel execution,
- and all threads access it in the same order.

That makes it a strong candidate for:

- `__constant__` storage
- and constant-cache broadcast behavior

### Why this helps

When all threads in a warp read the same filter element at the same time, the constant cache can serve that access very efficiently.

So instead of paying repeated DRAM traffic for filter reads, the kernel can effectively treat the filter as:

- small,
- shared,
- and almost always cache-resident.

### What changes in code

The algorithm barely changes:

- the filter becomes a global `__constant__` device variable
- host code copies it with `cudaMemcpyToSymbol`
- kernel code reads it directly without passing a pointer argument

This is a pure memory-placement optimization.

### What it does not solve

It reduces filter traffic, but it does not solve the bigger problem:

- repeated loading of overlapping input neighborhoods from global memory

That is why the chapter next moves to tiling.

## Tiled Convolution

The main shared-memory optimization is:

```text
load an input tile once into shared memory,
then reuse it to compute many output elements
```

This is similar in spirit to tiled matrix multiplication, but convolution has an extra complication:

- the input region needed for a block is larger than the output region it computes

## Output Tiles vs Input Tiles

This distinction is the heart of convolution tiling.

### Output tile

- the set of output elements computed by one block

### Input tile

- the set of input elements needed to compute that output tile

Because each output depends on neighbors, the input tile must extend outward by the filter radius in every direction.

So:

```text
IN_TILE_DIM = OUT_TILE_DIM + 2 * FILTER_RADIUS
```

The extra border region is the halo.

## Halo Cells

Halo cells are the extra surrounding input elements needed to compute outputs near the edge of the output tile.

This is what makes convolution tiling harder than tiled matrix multiplication:

- the compute tile and the loaded tile are not the same size

That mismatch creates design trade-offs in:

- block geometry,
- thread utilization,
- shared-memory footprint,
- and control flow.

## Tiling Design 1: Explicit Halo Loading

The first tiled design makes:

- block size match the larger input tile
- shared memory store the full input tile, including halos

### Why it is attractive

- loading is simple
- each thread loads one input-tile element
- once loaded, all needed values come from shared memory

### Why it is awkward

Because the input tile is larger than the output tile:

- some threads only help load halo data
- then become inactive during output computation

So this design improves memory reuse, but wastes some execution resources.

### Compute structure

The algorithm is:

1. each thread loads one input-tile element into shared memory
2. ghost cells are replaced with zero during loading
3. synchronize the block
4. only interior threads compute outputs
5. each active thread applies the filter over a shared-memory patch

This design introduces more control complexity, but dramatically reduces global-memory traffic.

## Arithmetic Intensity Improvement

This is one of the chapter’s most important quantitative lessons.

Without tiling, the convolution kernel has low arithmetic intensity because input elements are reloaded many times from global memory.

With shared-memory tiling:

- an input element is loaded once per block
- then reused by many output computations

So arithmetic-to-global-memory ratio rises substantially.

The key intuition is:

```text
the more times one loaded input value is reused on-chip,
the better the arithmetic intensity becomes
```

### Practical limit

Halo overhead prevents the achieved ratio from reaching the ideal asymptotic bound, especially when:

- tiles are small,
- filters are large,
- or dimensionality increases.

So tile size matters a great deal.

## Tiling Design 2: Use Cache for Halo Cells

The chapter then presents a cleaner refinement:

- keep only the tile interior in shared memory
- let halo accesses fall through to global memory
- rely on cache to serve many halo values efficiently

This works because halo cells for one block are often interior cells for neighboring blocks.

So by the time a block needs a halo element, there is a good chance that:

- a nearby block has already touched it,
- and it is already present in L2 cache.

## Why Cache-Assisted Halo Handling Helps

This design simplifies the geometry:

- input tile size and output tile size can match
- block dimensions can match tile dimensions
- all threads naturally correspond to output elements

That removes the need for:

- oversized blocks,
- deactivated outer thread layers,
- and some of the divergence created by the explicit-halo design

### New trade-off

The compute phase becomes more complex:

- if a needed input is inside the tile interior, read from shared memory
- if it is outside the tile but still valid, read from global memory and hope cache serves it
- if it is outside the image, treat it as a ghost cell and skip it

So complexity shifts from:

- load-time block geometry

to:

- per-access decision logic in compute

But the overall design is often cleaner.

## Memory-Hierarchy Strategy

The final optimized convolution picture is:

- filter `F` -> constant memory
- tile interior of input `N` -> shared memory
- halo values -> cache-backed global memory
- output accumulation -> registers

This is the chapter’s real systems lesson:

```text
different kinds of data reuse should be mapped to different memory spaces
```

not everything should be handled the same way.

## What the Chapter Really Teaches

The convolution chapter is not only about one operator.
It teaches how to reason about:

- thread-to-data mapping
- local neighborhood reuse
- on-chip staging
- halo overhead
- boundary handling
- and arithmetic-intensity improvement

That is why the chapter connects naturally to:

- stencil computations
- image and video processing
- PDE-style neighborhood updates
- and convolutional neural networks

## Practical Rules to Keep

### Rule 1

If a small object is:

- read-only,
- globally shared,
- and accessed uniformly,

consider constant memory.

### Rule 2

If neighboring threads repeatedly use overlapping input regions, consider shared-memory tiling.

### Rule 3

In stencil-like problems, halo overhead can dominate when tile size is too small.

### Rule 4

A cleaner tile geometry may be worth relying on cache for halos instead of staging every border value explicitly.

### Rule 5

Always evaluate the arithmetic-to-global-memory ratio, not just correctness.

## Short Takeaway

CUDA convolution is easy to parallelize at the output level but hard to optimize because neighboring outputs reuse overlapping input neighborhoods and boundary handling creates halo overhead. The best designs use constant memory for the small shared filter, shared memory for tile interiors, and cache-aware handling for halo cells so that global-memory traffic is reduced without making the block geometry unnecessarily awkward.
