# CUDA Memory Types, Tiling, Boundary Checks, and Occupancy

This note organizes a standard introductory CUDA performance sequence into one causal story:

1. why naive matrix multiplication is memory-bound,
2. how arithmetic intensity and the Roofline model explain that limitation,
3. what CUDA memory types exist and why they differ,
4. how tiling with shared memory reduces global-memory traffic,
5. why boundary checks are required for arbitrary matrix sizes,
6. and how shared-memory and register usage can in turn reduce occupancy.

The central theme is simple:

```text
good CUDA performance depends on reducing expensive global-memory traffic without over-consuming limited on-chip resources
```

## 1. Why Memory Access Efficiency Matters

Consider the naive inner loop of matrix multiplication.

For one iteration of the dot product, the kernel does:
- one global load from `M`
- one global load from `N`
- one floating-point multiply
- one floating-point add

If these are single-precision values, each load fetches one 4-byte float.

So per iteration:

- memory traffic = `8 B`
- compute = `2 FLOP`

That gives:

```text
arithmetic intensity = 2 FLOP / 8 B = 0.25 FLOP/B
```

This quantity is also called:
- compute-to-global-memory-access ratio,
- arithmetic intensity,
- computational intensity.

## 2. Why Low Arithmetic Intensity Is Bad

If arithmetic intensity is low, the kernel does very little work per byte fetched from global memory.

That means performance is likely limited by memory bandwidth rather than by the arithmetic units.

Using the A100 numbers from the text:

- peak global memory bandwidth = `1555 GB/s`
- arithmetic intensity = `0.25 FLOP/B`

Maximum memory-limited throughput:

```text
1555 GB/s x 0.25 FLOP/B = 388.75 GFLOP/s
```

approximately:

```text
389 GFLOP/s
```

That is far below:
- the A100 FP32 peak of about `19,500 GFLOP/s`
- and far below tensor-core peak numbers

So the kernel is memory-bound:

```text
its speed is limited by data delivery, not by available arithmetic throughput
```

## 3. Roofline Model Intuition

The Roofline model is a visual way to interpret this.

### x-axis

Arithmetic intensity in `FLOP/B`

### y-axis

Achieved throughput in `GFLOP/s`

### Two hardware ceilings

- a horizontal line for peak compute throughput
- a diagonal line for peak memory-bandwidth-limited throughput

The diagonal line follows:

```text
throughput = bandwidth x arithmetic intensity
```

Applications on the left with low arithmetic intensity are memory-bound.
Applications on the right with high arithmetic intensity are compute-bound.

The intersection point between the diagonal and horizontal ceilings is the transition point.

At `0.25 FLOP/B`, naive matrix multiplication lies far left in the memory-bound regime.
So simply having more arithmetic hardware does not help much if the data cannot arrive fast enough.

## 4. Why We Need Tiling

The only way to escape strong memory-bound behavior is to increase arithmetic intensity.

That means:

```text
do more useful floating-point work per byte fetched from global memory
```

For matrix multiplication, this is possible because the computation has data reuse:
- one value of `M` can contribute to multiple outputs
- one value of `N` can contribute to multiple outputs

If those reused values keep getting loaded from global memory again and again, bandwidth is wasted.
If they are loaded once and reused from faster on-chip memory, arithmetic intensity improves.

That is exactly what tiling is for.

## 5. CUDA Memory Types

CUDA exposes several memory spaces that differ in:
- scope,
- lifetime,
- latency,
- bandwidth,
- sharing behavior.

### Global memory

Properties:
- large
- off-chip
- implemented in DRAM
- accessible by all threads
- readable and writable by both host and device
- high latency compared with on-chip memory

Use it for:
- large arrays
- inputs and outputs
- data that must persist across kernels
- data visible across blocks

### Constant memory

Properties:
- read-only from device code
- writable by host
- application lifetime
- cached for efficient access
- small capacity

Use it for:
- small read-only inputs
- broadcast-style constants
- coefficient tables

### Local memory

Properties:
- private to each thread
- but actually backed by global memory
- similar latency class to global memory

Use it for:
- automatic arrays
- spilled registers
- parts of thread-private stack state

This is a crucial conceptual point:

```text
local memory means thread-private scope, not fast memory
```

### Registers

Properties:
- on-chip
- private to each thread
- very low latency
- very high bandwidth
- directly used by arithmetic instructions

Use them for:
- hot scalar values
- loop counters
- temporary arithmetic results
- per-thread accumulators

Registers are the most desirable storage for thread-private hot data.

### Shared memory

Properties:
- on-chip
- visible to all threads in one block
- much faster than global memory
- slower than registers because it is still accessed via load/store operations
- ideal for collaboration and reuse

Use it for:
- tiles of input data
- block-level collaboration
- reused intermediate values
- explicit software-managed caching

## 6. Why Registers Are Better Than Global Memory

There are at least three reasons.

### 1. Lower latency and higher bandwidth

Registers are on-chip and extremely fast.

### 2. No extra load instruction is needed

Arithmetic instructions typically use register operands directly.

If a value is already in a register, the ALU can consume it immediately.
If the value is in global memory, an explicit load must bring it into a register first.

So global-memory-resident operands cost:
- extra instructions
- extra latency
- extra bandwidth

### 3. Lower energy

Accessing registers is much more energy-efficient than accessing global memory.

So registers help:
- speed
- bandwidth efficiency
- energy efficiency

## 7. Why Shared Memory Is Useful

Shared memory is not as fast as registers, but it offers something registers cannot:

```text
block-wide data sharing
```

That is why it is ideal for tiling.

Threads in a block can:
- collaboratively load a tile once from global memory,
- store it in shared memory,
- reuse it many times.

This reduces off-chip traffic dramatically.

In architecture language, shared memory is a programmable on-chip scratchpad.

## 8. CPU vs GPU Register Architecture

The text makes an important contrast.

### CPU

A CPU commonly saves and restores register state across context switches.

### GPU

A GPU wants warp scheduling to have near-zero overhead.

So the register state of all resident threads is kept in the SM register file.

That means:
- switching between warps is very fast,
- the register file must be very large,
- register allocation per thread directly affects how many threads can fit on the SM.

So GPU registers are not just a convenience.
They are part of the occupancy model.

## 9. Tiling for Reduced Memory Traffic

The basic tiling strategy is:

1. partition input matrices into tiles
2. make each block compute one output tile of `P`
3. have threads in the block cooperatively load one tile of `M` and one tile of `N` into shared memory
4. reuse those shared-memory values many times
5. repeat in phases until the dot product is complete

The tile size is chosen so the tiles fit in shared memory.

This works only because matrix multiplication has data reuse and because the dot product can be decomposed into phases.

## 10. Why Naive Matrix Multiplication Wastes Traffic

Neighboring threads often need overlapping input data.

Examples:
- two nearby threads computing adjacent outputs may need the same row elements of `M`
- two nearby threads computing vertically adjacent outputs may need the same column elements of `N`

Without tiling, these repeated needs cause repeated global loads of the same values.

That is the waste.

## 11. What a Tiled Matrix Multiplication Kernel Does

Each thread computes one output element:

```text
P[Row, Col]
```

where:

```text
Row = by * TILE_WIDTH + ty
Col = bx * TILE_WIDTH + tx
```

Here:
- `(bx, by)` is the block position
- `(tx, ty)` is the thread position inside the block

The dot-product dimension is broken into phases:

```text
number of phases = Width / TILE_WIDTH
```

In each phase:
- every thread loads one `M` element into `Mds`
- every thread loads one `N` element into `Nds`
- all threads synchronize
- each thread accumulates one partial dot-product contribution using shared-memory values
- all threads synchronize again before the next phase overwrites shared memory

## 12. Why Two Barriers Are Needed

The first `__syncthreads()` protects a read-after-write dependence.

Threads must wait until all tile elements are fully loaded before any thread starts reading shared memory.

The second `__syncthreads()` protects a write-after-read dependence.

Threads must wait until all tile values are fully consumed before any thread starts loading the next phase's tile into the same shared-memory locations.

So the barriers protect:
- correctness of tile loading
- correctness of tile reuse

## 13. Why Tiling Helps So Much

A shared-memory tile lets one global-memory load serve multiple arithmetic operations.

For an `N x N` tile, global-traffic reduction can scale roughly with `N`.

Example:
- tile size `16 x 16`
- potential global-memory traffic reduction of about `16x`

This raises arithmetic intensity.

The text's example:

- naive kernel: `0.25 OP/B`
- tiled kernel with `TILE_WIDTH = 16`: `4 OP/B`

Then a bandwidth-limited throughput estimate becomes:

```text
1555 GB/s x 4 OP/B = 6220 GFLOP/s
```

That is far higher than the naive `389 GFLOP/s`.

## 14. Locality

Tiling works because each phase focuses on a small subset of data and reuses it heavily.

That focused reuse is called locality.

Locality is the real reason small fast memories are useful.

This is not a GPU-only concept.
It is also crucial on CPUs, where caches depend on locality to be effective.

The difference is:
- CPU tiling often relies on caches implicitly
- GPU tiling often relies on shared memory explicitly

## 15. Boundary Checks

The clean tiled kernel often assumes:
- square matrices
- matrix width is a multiple of `TILE_WIDTH`

That assumption is not safe in general.

If matrix dimensions are not exact multiples of tile size, edge tiles are only partially full.
Some threads will then attempt to load elements that do not exist.

These invalid accesses can:
- accidentally fetch wrong in-range values because of linearized layout,
- read garbage,
- or access out-of-bounds memory illegally.

## 16. Why One Global Check Is Not Enough

There are separate index spaces for:
- loading `M`
- loading `N`
- storing `P`

So one thread can be:
- invalid for writing `P`,
- but still necessary for loading part of an `M` or `N` tile

or:
- valid for writing `P`,
- but invalid for one particular tile load in some phase

Therefore:

```text
every memory access needs its own bounds check
```

## 17. Correct Boundary Handling

When loading from `M`, check whether:

```text
Row < Width
and
(ph * TILE_WIDTH + tx) < Width
```

When loading from `N`, check whether:

```text
(ph * TILE_WIDTH + ty) < Width
and
Col < Width
```

If the access is valid:
- load the true matrix element

If invalid:
- place `0.0` into the shared-memory slot instead

This works because:

```text
0.0 * anything = 0.0
```

So the invalid contribution becomes harmless in the dot product.

When storing `P`, check:

```text
Row < Width
and
Col < Width
```

Only valid output elements should be written.

## 18. Generalization Beyond Square Matrices

General matrix multiplication is:

```text
M is j x k
N is k x l
P is j x l
```

So a fully general tiled kernel replaces one `Width` parameter with:
- `j`
- `k`
- `l`

Then each bounds check must use the correct dimension for the matrix being accessed.

## 19. Occupancy and Memory Usage

So far shared memory and registers sound purely beneficial.
But they are limited resources on each SM.

If a kernel uses too much shared memory or too many registers per thread or per block, fewer threads and fewer blocks can reside on the SM at once.

That reduces occupancy.

### Why this matters

High occupancy is often useful because it helps the SM tolerate long-latency operations by switching among many ready warps.

So:
- more on-chip storage use can improve reuse
- but too much can reduce latency hiding

This is the tradeoff.

## 20. Shared Memory as an Occupancy Limiter

Example from the text:

- A100 shared memory per SM: up to `164 KB`
- maximum threads per SM: `2048`

Average shared-memory budget for full occupancy:

```text
164 KB / 2048 ~= 82 B/thread
```

For tiled matrix multiplication:

- one `Mds` tile uses `TILE_WIDTH^2 * 4 B`
- one `Nds` tile uses `TILE_WIDTH^2 * 4 B`

Total:

```text
8 * TILE_WIDTH^2 bytes per block
```

Since the block has `TILE_WIDTH^2` threads:

```text
shared memory per thread = 8 B/thread
```

So this kernel is not shared-memory-limited in occupancy.

## 21. Example of Shared-Memory-Limited Occupancy

Suppose a block has:
- `256` threads
- `32 KB` shared memory usage

Then shared memory per thread is about:

```text
32 KB / 256 = 128 B/thread
```

That exceeds the rough full-occupancy budget.

So the SM cannot host the maximum `2048` threads.
Occupancy drops.

This example makes the optimization lesson concrete:

```text
using more shared memory is not free
```

## 22. Dynamic Shared Memory

Shared-memory capacity differs across devices.

So it is useful for host code to choose shared-memory size dynamically at runtime.

Static declarations like:

```cpp
__shared__ float Mds[TILE_WIDTH][TILE_WIDTH];
```

hardwire the tile size at compile time.

To make shared-memory size adjustable, CUDA supports:

```cpp
extern __shared__ float shared[];
```

This creates one dynamically sized shared-memory region whose size is supplied at kernel launch.

Then the programmer manually partitions it into sections, for example:
- first part for `Mds`
- second part for `Nds`

This allows the host to adapt memory usage to device capability.

## 23. Scope and Lifetime Summary of CUDA Variable Types

The CUDA memory-space rules can be summarized as follows.

### Automatic scalar variables

- usually placed in registers
- thread scope
- grid lifetime

### Automatic array variables

- usually placed in local memory
- thread scope
- grid lifetime

### `__shared__`

- shared memory
- block scope
- grid lifetime

### `__device__`

- global memory
- grid-wide visibility
- application lifetime

### `__constant__`

- constant memory
- grid-wide visibility
- application lifetime
- read-only from device code

## 24. Deepest Summary

This whole sequence from 5.1 to 5.6 teaches one unified performance story.

Naive matrix multiplication is memory-bound because it performs too few FLOPs per byte fetched from global memory.
CUDA exposes multiple memory types with radically different latency, bandwidth, scope, and visibility.
Shared memory and registers are the main tools for reducing global-memory traffic.
Tiling exploits locality by forcing threads in a block to cooperatively load reusable data into shared memory phase by phase.
Boundary checks are needed to make the tiled kernel correct for arbitrary matrix sizes.
Finally, because shared memory and registers are limited SM resources, aggressive use of them can reduce occupancy, so kernel optimization is always a balance between:
- reuse,
- arithmetic intensity,
- resident-thread capacity.

This is the architectural heart of practical CUDA optimization.
