# 05. Memory Coalescing and Latency Hiding

This note integrates the main performance ideas from Chapter 6 into one causal story:

1. why off-chip DRAM is fundamentally slow,
2. why GPUs prefer burst-friendly, adjacent accesses,
3. how memory coalescing aligns warp accesses with DRAM behavior,
4. how channels and banks hide memory latency through parallelism,
5. when thread coarsening helps,
6. and why optimization should start from the actual bottleneck rather than from habit.

The central theme is:

```text
good CUDA performance depends on matching thread behavior to the structure of the memory system,
while avoiding optimization choices that relieve the wrong resource.
```

## 1. Why DRAM Is Slow

DRAM latency is not mainly about poor engineering. It comes from how DRAM cells are built.

A DRAM cell stores a bit as a very small amount of electrical charge in a tiny capacitor. Reading the cell means:

- enabling the row through decoder logic,
- letting the cell share its tiny charge with a long bit line,
- and waiting for a sense amplifier to detect the resulting voltage difference.

This is difficult because:

- the stored signal is tiny,
- the bit line is long and has large capacitance,
- and the sense amplifier must detect a very small perturbation reliably.

The book’s basic intuition is:

```text
the memory cell is weak, the wires are large, and the sensing process is delicate
```

That makes random DRAM access slow.

### Why latency did not improve much over time

DRAM cells have become smaller and denser so chips can store more bits.

But smaller cells mean:

- smaller capacitors,
- weaker stored signals,
- and less improvement in access latency than one might expect from raw semiconductor scaling.

So DRAM capacity and bandwidth improved dramatically, while access latency improved much less.

## 2. DRAM Bursts and Spatial Locality

Modern DRAM is optimized for bursts rather than isolated random accesses.

When a location is accessed, a range of nearby consecutive locations is also activated and made available quickly.

This means:

- random scattered accesses are inefficient,
- nearby consecutive accesses are much more favorable.

That is the hardware reason behind memory coalescing.

## 3. Memory Coalescing

When all threads in a warp execute a load or store, the hardware checks whether their addresses are favorable to combine.

The best pattern is:

- consecutive threads,
- accessing consecutive memory locations.

If thread 0 loads `X`, thread 1 loads `X + 1`, thread 2 loads `X + 2`, and so on, those accesses can often be combined into one coalesced transaction aligned with a DRAM burst or cache line.

The big benefit is:

```text
many per-thread accesses collapse into fewer off-chip memory transactions
```

That reduces:

- global-memory traffic,
- effective latency pressure,
- and bandwidth waste.

## 4. Why Row-Major Layout Matters

CUDA C arrays follow row-major layout.

That means:

- adjacent elements in the same row are adjacent in memory
- elements in the same column are usually separated by a stride

So if adjacent threads walk across a row, memory accesses are naturally coalesced.

If adjacent threads walk down a column of a row-major matrix, accesses are typically strided and uncoalesced.

This is why thread-to-data mapping matters so much.

### Coalesced matrix access pattern

In matrix multiplication, if adjacent threads access:

```text
M[k * Width + col]
```

and `col` is consecutive across threads, then accesses are favorable:

- same `k`
- same `Width`
- consecutive `col`

So adjacent threads access adjacent memory locations.

### Uncoalesced pattern

If the matrix is effectively viewed in column-major order, access may look like:

```text
M[col * Width + k]
```

Now consecutive threads vary `col`, but `col` is multiplied by `Width`.

So neighboring threads access elements far apart in memory.

That is bad for coalescing.

## 5. Corner Turning

One important CUDA trick for fixing awkward global-memory patterns is:

```text
load globally in a coalesced pattern,
then rearrange in shared memory
```

The chapter’s matrix example uses this when matrix `B` is stored in column-major layout.

Instead of having each thread load the logically natural element directly from global memory, threads cooperatively load a tile of `B` in a memory-friendly order and place it into shared memory.

Then later per-thread accesses happen in shared memory, where coalescing is not required.

This strategy is often called **corner turning**.

It is one of the most important CUDA optimization patterns because it separates:

- the global-memory access pattern
- from the later computation access pattern

## 6. Hiding Memory Latency with Banks and Channels

Bursts alone are not enough to provide modern GPU memory bandwidth.

DRAM systems add two more layers of parallelism:

- **channels**
- **banks**

### Channels

A channel is a memory-controller path with its own bus connecting DRAM to the processor.

More channels mean:

- more independent transfer paths
- more total bandwidth

### Banks

Each channel connects to multiple banks.

A bank contains:

- DRAM cell arrays,
- sensing circuitry,
- and the local logic needed to serve bursts.

The key idea is:

```text
while one bank is paying internal DRAM access latency,
another bank can be transferring burst data on the bus
```

So latency is hidden by overlapping many independent accesses.

## 7. Why One Bank Is Not Enough

The chapter emphasizes that DRAM access latency is much larger than burst transfer time.

If one bank were attached to a channel, the bus would spend most of its time idle, waiting for the next access to become ready.

So even if the raw bus bandwidth looks high, actual utilization would be terrible.

Adding banks improves this by pipelining:

- bank access latency
- and data transfer

The rough rule of thumb is:

```text
if DRAM array latency is R times the burst transfer time,
you need about R + 1 banks to keep the bus fully utilized
```

This is a memory-system version of latency hiding.

## 8. Bank Conflicts

The benefit of many banks depends on accesses being spread across them.

If too many requests target the same bank, they serialize. This is a **bank conflict**.

So good bandwidth utilization requires:

- many requests in flight,
- spread across channels and banks,
- with each request itself as coalesced as possible.

## 9. Interleaved Data Distribution

The chapter shows a toy example where consecutive memory chunks are distributed across:

- channel 0, bank 0
- channel 1, bank 0
- channel 2, bank 0
- channel 3, bank 0
- then channel 0, bank 1
- and so on

This interleaving is designed so that linear arrays naturally spread across the DRAM parallel structure.

That helps even simple coalesced accesses make use of:

- multiple banks
- and multiple channels

instead of overloading just one path.

## 10. Occupancy Helps Memory Too

Earlier chapters introduced occupancy mainly as a way to hide:

- core pipeline latency
- through many resident warps

Chapter 6 adds another role:

```text
high occupancy also helps generate enough simultaneous memory accesses
to utilize channels and banks effectively
```

So maximizing occupancy supports both:

- compute-side latency hiding
- memory-system utilization

This is a very important connection.

## 11. Thread Coarsening

So far, many CUDA kernels use the finest possible parallelism:

- one thread per vector element,
- one thread per pixel,
- one thread per matrix entry.

That is attractive because it exposes maximal parallelism and preserves transparent scalability.

But sometimes this fine-grained decomposition has a real cost.

The chapter calls this the **price of parallelism**.

Examples of that price include:

- redundant data loads,
- redundant work,
- synchronization overhead,
- or repeated per-block setup.

If hardware truly runs many blocks simultaneously, the price may be worth paying.

If hardware serializes blocks anyway, that price may be wasted.

### What thread coarsening does

Thread coarsening assigns multiple work items to each thread.

So instead of:

```text
1 thread -> 1 output
```

you may do:

```text
1 thread -> several outputs
```

usually through a small per-thread coarsening loop.

### Why it helps

In the tiled matrix multiplication example, adjacent output tiles reuse the same tile of matrix `M`.

If two different blocks compute those tiles independently, each block loads its own copy of `M` into shared memory.

That duplicated loading is the price of parallelizing those output tiles separately.

Thread coarsening reduces this by having:

- one block cover multiple neighboring output tiles
- one thread compute multiple outputs
- one loaded tile of `M` reused across more work

So coarsening is best understood as:

```text
intentional partial serialization to reduce unnecessary parallel overhead
```

## 12. When Thread Coarsening Helps

Thread coarsening is most useful when fine-grained parallelism causes:

- redundant loading of shared inputs,
- redundant computation,
- extra synchronization,
- excess divergence,
- or other overheads that can be reduced by grouping work.

It is much less useful when work items are already independent and cheap.

For example:

- vector addition usually does not pay much price for fine-grained parallelism
- so coarsening often gives little benefit there

## 13. Thread Coarsening Pitfalls

The chapter emphasizes three major pitfalls.

### 1. Coarsening without a real parallelism cost

If there is no redundant work or reuse opportunity, coarsening may just add complexity.

### 2. Coarsening too much

Too much coarsening reduces the amount of exposed parallelism.

If you go too far:

- there may not be enough warps or blocks to keep the GPU busy
- transparent scalability gets worse

### 3. Hurting occupancy

Coarsened threads often need:

- more registers per thread
- and sometimes more shared memory per block

That can reduce occupancy.

If occupancy falls too much, the lost latency hiding may outweigh the benefit of reduced redundancy.

So coarsening is always a tradeoff, not a free win.

## 14. The Optimization Checklist

Chapter 6 consolidates the first universal CUDA optimization checklist.

### 1. Maximize occupancy

Benefit:

- more work to hide pipeline latency
- more memory requests to hide DRAM latency

Main tuning knobs:

- threads per block
- registers per thread
- shared memory per block

### 2. Enable coalesced global-memory accesses

Benefit:

- fewer pipeline stalls on global loads
- less global-memory traffic
- better use of bursts and cache lines

Main strategies:

- global-to-shared transfers in coalesced form
- remap threads to data
- rearrange data layout

### 3. Minimize control divergence

Benefit:

- higher SIMD efficiency
- fewer idle warp lanes

Main strategies:

- remap work
- remap data
- group similar workloads into the same warp

### 4. Tile reused data

Benefit:

- lower global-memory traffic
- fewer stalls from repeatedly loading reused values

Main strategy:

- put reused block-local data in shared memory or registers

### 5. Privatize shared outputs

Benefit:

- less atomic contention
- less serialization on shared global structures

Main strategy:

- accumulate partial results privately
- merge later into the universal structure

### 6. Use thread coarsening when fine-grained parallelism is too expensive

Benefit:

- less redundant work
- less redundant memory traffic
- less synchronization or divergence overhead in some kernels

Main strategy:

- assign several logical work items to one thread

## 15. Knowing the Bottleneck

The final and most practical lesson of the chapter is:

```text
do not optimize blindly; first identify the bottleneck
```

A bottleneck is the resource currently limiting performance.

Examples:

- global-memory bandwidth
- DRAM latency
- occupancy
- shared-memory capacity
- register pressure
- divergence
- atomic contention
- instruction throughput

Optimizations usually trade one resource for another.

For example:

- tiling spends more shared memory to reduce global-memory traffic
- coarsening may spend more registers to reduce redundant loads

If you relieve a non-bottleneck resource, you may get no speedup.

Worse, you may hurt performance by increasing pressure on the real bottleneck.

### Important example

Shared-memory tiling is excellent when:

- the kernel is memory-bandwidth-bound
- and loaded data is reused

But if occupancy is already constrained by shared memory, then adding more tiling may make the kernel slower rather than faster.

That is why profiling matters.

## 16. Profiling and Hardware Dependence

The same kernel can have different bottlenecks on different devices.

Why?

- different GPUs have different memory bandwidth
- different cache structures
- different SM resources
- different register/shared-memory limits
- different occupancy behavior

So optimization is not a static recipe.

It is a reasoning process:

1. profile the kernel
2. identify the real bottleneck
3. apply the optimization that targets that bottleneck
4. measure again

That is the correct CUDA optimization loop.

## 17. The Full Chapter 6 Story

The chapter’s integrated logic is:

1. DRAM is slow and burst-oriented.
2. Coalescing aligns warp accesses with burst-friendly memory behavior.
3. Channels and banks provide memory-level parallelism.
4. Occupancy helps generate enough parallel work to utilize both compute and memory.
5. Thread coarsening reduces the price of over-parallelization when that price is real.
6. A reusable optimization checklist captures the first major CUDA tuning levers.
7. The right optimization always depends on the actual bottleneck.

## 18. Common Mistakes

- Assuming more arithmetic hardware matters even when the kernel is memory-bound.
- Treating coalescing as optional for global memory.
- Forgetting that row-major layout strongly affects access quality.
- Assuming bursts alone provide enough bandwidth without bank/channel parallelism.
- Applying thread coarsening when no meaningful price of parallelism exists.
- Coarsening so much that occupancy or exposed parallelism collapses.
- Applying an optimization without checking whether it targets the actual bottleneck.

## 19. Why This Matters Beyond CUDA

Although the examples are CUDA-specific, the chapter’s core performance ideas are broader:

- memory systems prefer locality and bursts
- parallel hardware relies on enough independent work in flight
- restructuring work can trade parallelism for reuse
- and optimization must be bottleneck-driven rather than aesthetic

Those ideas transfer naturally to CPUs, accelerators, and ML systems more generally.

## Short Takeaway

Chapter 6’s core lesson is that CUDA performance depends on matching thread behavior to memory-system structure: use coalesced accesses to fit DRAM bursts, use enough parallel requests to hide bank and channel latency, use thread coarsening only when fine-grained parallelism is wasteful, and choose every optimization by first identifying the kernel’s real bottleneck.
