# 07. Stencil Sweeps

## What

Stencil sweep is a parallel pattern in which each output grid point is computed from the value at the same location and a fixed neighborhood of nearby grid points.

In CUDA, stencils are important because they combine:

- structured-grid computation,
- strong local data reuse,
- halo and ghost-cell handling,
- and heavy memory-pressure constraints, especially in 3D.

Stencil kernels look similar to convolution at first, but their numerical role and optimization trade-offs are different enough that they deserve their own pattern treatment.

## Why It Matters

Stencil methods are foundational in numerical computing for:

- fluid dynamics,
- heat transfer,
- combustion,
- weather and climate models,
- electromagnetics,
- and many PDE solvers more generally.

These are not just array transforms.
They are usually part of iterative numerical methods where continuous functions and equations are discretized onto grids and repeatedly updated.

That makes stencil sweeps one of the most important scientific-computing GPU patterns.

## From Continuous Functions to Grids

The first step in stencil-based numerical methods is discretization.

A continuous function over space is represented by its values on a structured grid:

- 1D -> line of grid points
- 2D -> rectangular grid
- 3D -> volumetric grid

The grid spacing controls fidelity:

- smaller spacing gives a more accurate approximation,
- but increases storage and computation.

In practice, these grid values are stored in arrays, and the stencil sweep updates or derives new values from those arrays.

## Why Stencils Exist

Stencils arise because derivatives can be approximated numerically using neighboring function values.

For example, a first derivative can be approximated using finite differences:

```text
f'(x) ≈ (f(x+h) - f(x-h)) / (2h)
```

Once the function is discretized on a grid, this becomes a weighted combination of nearby array values.

That weighted neighborhood pattern is the stencil.

So a stencil is best understood as:

```text
a geometric pattern of coefficients applied around a grid point
to approximate a derivative or update rule
```

## Stencil Order and Dimensionality

Stencil order reflects how far the neighborhood extends from the center point.

Examples include:

- 1D three-point stencil
- 1D five-point stencil
- 2D five-point stencil
- 2D nine-point stencil
- 3D seven-point stencil
- 3D 13-point stencil

Higher-order stencils:

- use more neighbors,
- often provide better numerical approximations,
- but increase memory traffic and halo overhead.

## How Stencils Resemble Convolution

Stencils and convolution share several properties:

- each output depends on a local neighborhood
- neighboring outputs reuse overlapping input data
- halo cells and ghost cells matter
- shared-memory tiling is a natural optimization idea

That is why many convolution tiling ideas transfer directly to stencil sweeps.

## How Stencils Differ from Convolution

The differences are just as important.

Stencil sweeps are usually:

- part of iterative PDE solvers,
- often performed on 3D grids,
- more sensitive to numerical precision,
- and based on sparse neighborhood patterns rather than dense local filters.

This leads to different optimization behavior:

- lower reuse than dense convolution
- much worse halo overhead in 3D
- stronger pressure from double-precision or other high-precision data
- and greater motivation for thread coarsening and register tiling

## The Basic Parallel Stencil Kernel

The simplest CUDA stencil kernel uses the obvious decomposition:

- one thread per output grid point
- one block per output tile

For the 3D seven-point stencil, each thread reads:

- the center point
- plus its six axis-aligned neighbors

then applies the corresponding coefficients and writes one output value.

The chapter assumes a useful simplifying boundary condition:

- boundary grid points already store boundary conditions
- those boundary values do not change from one sweep to the next

That means the kernel updates only the interior points.

## Why the Basic Kernel Is Memory-Bound

The straightforward stencil kernel has low arithmetic intensity.

For the seven-point stencil example:

- 13 floating-point operations
- 7 input loads
- 4 bytes per value

This gives:

```text
13 / (7 * 4) = 0.46 OP/B
```

That is far too low for the arithmetic hardware to be well utilized.

So, just as with naive convolution, the first stencil kernel is correct but heavily limited by memory bandwidth.

## Shared-Memory Tiling for Stencil Sweeps

The obvious next step is shared-memory tiling:

```text
load an input tile into shared memory once,
then let many threads reuse it while computing an output tile
```

This is structurally the same idea as tiled convolution.

But stencil sweeps have an important twist:

- their neighborhoods are often sparse,
- so the reuse benefit is lower.

## Input Tiles, Output Tiles, and Halo Cells

As in convolution:

- the output tile is what the block computes
- the input tile is the larger region needed to support those outputs

The extra border around the output tile is the halo.

In the shared-memory stencil kernel, the block size is matched to the larger input tile.

That means:

- all threads help load the input tile
- but only the interior threads compute output values

This is the same “explicit halo loading” structure used in tiled convolution.

## Why Tiling Helps Less for Stencils Than for Convolution

This is the most important quantitative lesson in the chapter.

Dense convolution uses many nearby input values for each output.

Stencil sweeps often use only a sparse axis-aligned pattern.

So even if the tiling structure is the same, each loaded input value is reused fewer times.

That means the achievable arithmetic-to-memory ratio is much lower.

Examples from the chapter:

- 2D five-point stencil has a much lower bound than 2D `3×3` convolution
- 2D nine-point stencil still lags well behind 2D `5×5` convolution
- in 3D, the gap becomes extremely large

So stencils are simply less profitable tiling targets than dense convolution if you measure only reuse.

## Why 3D Makes It Harder

Stencil methods are prominently used in 3D.
That creates two major problems.

### 1. Shared-memory growth

Tile storage grows like `T^3`, so larger tiles become expensive very quickly.

### 2. Halo overhead

For small 3D tiles, a huge fraction of the input tile is halo rather than useful interior.

This reduces:

- data reuse,
- arithmetic intensity,
- and the benefit of explicit shared-memory staging.

For practical 3D stencil tiles, halo overhead can dominate.

## The Shared-Memory Tiled Stencil Kernel

The shared-memory stencil kernel mirrors the explicit-halo convolution tiling approach:

1. block size matches the larger input tile
2. each thread loads one input-tile value into shared memory
3. ghost-cell and boundary checks prevent invalid accesses
4. synchronize
5. interior threads compute output values from the shared tile

This improves arithmetic intensity compared with the naive stencil kernel, but the benefit is limited by:

- small practical tile sizes,
- heavy halo overhead,
- and 3D resource constraints.

The section’s key result is that with realistic tile sizes the achieved OP/B is still much lower than the asymptotic bound.

## Why Small 3D Tiles Are Especially Bad

Small `T` values hurt stencil performance twice.

### Reuse gets worse

Halo cells occupy a larger fraction of the tile, so less of the loaded data is heavily reused.

### Coalescing gets worse

With small tiles such as `8×8×8`, a warp spans several short rows, so loads are less naturally coalesced than with larger tiles.

This means practical 3D stencil tiling can be constrained by both:

- weak reuse
- and poor memory-access regularity

That is why the chapter moves beyond shared memory tiling alone.

## Thread Coarsening in the z Direction

Thread coarsening solves the biggest structural problem of 3D tiling:

- a full `T^3` thread block becomes infeasible before `T` is large enough to give good reuse

The fix is:

- keep a 2D `x-y` plane of threads
- and let each thread compute a column of outputs through the `z` direction

So instead of:

- one thread -> one output grid point

we get:

- one thread -> multiple output points along `z`

This is partial serialization used to reduce the price of over-parallelization.

## How z-Direction Coarsening Works

The block keeps only three input planes active at a time:

- previous plane
- current plane
- next plane

For each output `z` plane:

1. load the next required input plane
2. synchronize
3. compute the current output plane
4. synchronize
5. slide the active window forward in `z`

This is effectively a rolling 3-plane window through the tile.

## Why Thread Coarsening Helps

It breaks the bad coupling between:

- tile size
- thread-block size

Now:

- block size becomes `T^2` instead of `T^3`
- shared-memory requirement becomes much more manageable
- larger effective tile sizes become possible

That leads to:

- better reuse
- lower halo fraction
- better arithmetic intensity

This is the main stencil-specific optimization step in the chapter.

## Register Tiling

The next refinement recognizes a subtle stencil property:

- not all active-plane data needs to be shared across threads

For the 3D seven-point stencil:

- the current plane must be shared across neighboring threads because of `x-y` accesses
- but the `z` neighbors above and below are private to each thread

That means:

- current plane should stay in shared memory
- previous and next `z` values can stay in registers

This is register tiling.

## Why Register Tiling Works Here

Registers are the best place for data that:

- is used by one thread only
- does not need cross-thread sharing

So replacing shared-memory `z` planes with per-thread registers:

- reduces shared-memory usage significantly
- speeds on-chip access
- and still preserves the same global-memory traffic pattern

The shared-memory tile is now only the truly shared part of the active window.

## What Register Tiling Improves

Register tiling does not reduce DRAM traffic further.

The number of global-memory accesses stays the same.

What improves is:

- shared-memory pressure
- on-chip access speed
- balance between shared memory and registers

This is an on-chip optimization, not a global-memory optimization.

## The Main Trade-off

Register tiling introduces the classic CUDA resource trade-off:

- less shared memory per block
- more registers per thread

If register pressure becomes too high, occupancy may suffer.

So the practical tuning question becomes:

```text
how much of the active stencil window should live in shared memory
and how much should live in registers?
```

That is a very common CUDA optimization trade-off.

## What the Chapter Really Teaches

The stencil chapter starts from the fact that stencils look like convolution, then shows why that analogy is not enough.

The most important differences are:

- stencils are often 3D
- stencil neighborhoods are sparse rather than dense
- precision requirements are often stricter
- and plain shared-memory tiling gives less benefit than for convolution

That is why the chapter’s most important optimizations are:

- shared-memory tiling
- thread coarsening in `z`
- register tiling for thread-private `z` data

The deeper lesson is:

```text
for 3D stencil sweeps, the best optimization is often not “tile harder,”
but “change the mapping so a 2D thread plane slides through the third dimension efficiently”
```

## Practical Rules to Keep

### Rule 1

Use the naive stencil kernel only as a correctness baseline; it is usually too memory-bound to be competitive.

### Rule 2

Shared-memory tiling helps stencil sweeps, but less than convolution, because stencil neighborhoods are usually sparse.

### Rule 3

In 3D, halo overhead and `T^3` resource growth are the dominant constraints.

### Rule 4

If full 3D tiling forces tiles to stay too small, coarsen one dimension rather than mapping all dimensions directly to threads.

### Rule 5

If data is used by only one thread, prefer registers over shared memory when register pressure allows it.

## Short Takeaway

CUDA stencil sweeps begin as local-neighborhood updates on structured grids, but unlike convolution they are usually 3D, numerically stricter, and based on sparser neighborhoods. Shared-memory tiling still helps, but the real gains come from thread coarsening in the `z` direction, which enables larger effective tiles, and register tiling, which keeps thread-private stencil data out of shared memory while preserving the same global-memory traffic.
