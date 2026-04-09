# 08. Parallel Histograms

## What

Parallel histogram computation is a CUDA pattern in which each thread processes input data and updates a histogram bin determined by the data value itself.

That makes histograms different from the earlier CUDA patterns in this folder.

In convolution, stencil sweeps, and many basic mapping kernels:

- each thread owns one output element or tile,
- and writes can usually proceed without interference.

In histogram computation:

- any thread may need to update any histogram bin,
- so multiple threads can try to update the same output location at the same time.

This makes the histogram pattern a canonical example of **shared-output contention**.

## Why It Matters

Histograms are widely used for:

- feature extraction in computer vision,
- fraud detection,
- speech and signal analysis,
- recommendation and behavior summarization,
- and scientific data analysis more generally.

They are one of the simplest examples of a broader class of parallel patterns where the output location is data-dependent rather than statically owned by a thread.

That is why histograms matter beyond their immediate application.
They introduce some of the first serious CUDA questions about:

- race conditions,
- atomic operations,
- contention,
- privatization,
- coarsening,
- and local aggregation before committing to shared state.

## The Sequential Baseline Is Easy

A sequential histogram is straightforward:

1. scan the input data once
2. determine which bin each item belongs to
3. increment that bin

This gives:

- linear-time work
- good sequential locality over the input
- and a small output structure that often stays cache-friendly on a CPU

So the parallel challenge is not the algorithmic definition of a histogram.
The challenge is how to update shared bins correctly and efficiently when many threads are active.

## Why Owner-Computes Breaks Here

The important break from previous CUDA patterns is this:

```text
the output bin for a thread is chosen by the input value,
not by the thread's preassigned output coordinates
```

That means two or more threads may target the same bin.

So the histogram pattern is not naturally compatible with the owner-computes rule.
Instead, it requires explicit coordination among threads.

## Race Conditions and Atomic Operations

A histogram increment is a read-modify-write sequence:

1. read the current bin value
2. add one
3. write the new value back

If two threads do that on the same bin at the same time, one update can be lost.

That is a read-modify-write race condition.

CUDA solves correctness here with atomic operations such as:

```c
atomicAdd(&histo[bin], 1);
```

An atomic operation makes the read-modify-write sequence indivisible for that location.
The second thread can still run before or after the first, but the two updates to the same address cannot overlap.

This guarantees correctness.

## Why the Basic Atomic Kernel Is Slow

The simple atomic histogram kernel is:

- easy to understand,
- easy to implement,
- and correct.

But performance can be poor because heavy contention serializes updates to hot bins.

For an uncontended memory system, GPUs rely on:

- many accesses in flight,
- latency hiding,
- and high memory throughput.

For a single heavily contended atomic location, that breaks down.
Only one atomic update to that location can be in progress at a time.

So throughput is no longer limited by aggregate DRAM bandwidth.
It becomes limited by the latency of one serialized read-modify-write sequence.

This is why histograms can perform much worse than users expect from the GPU's raw memory specifications.

## Contention Depends on Data Distribution

Atomic histogram performance is not determined only by:

- number of threads,
- or number of bins.

It also depends strongly on input distribution.

If updates spread evenly across bins:

- contention is lower,
- and throughput improves.

If a few bins dominate:

- contention is severe,
- and execution becomes bottlenecked by serialized atomic updates.

So histogram performance is highly data-dependent.

## Privatization

The first major optimization is privatization.

The idea is:

```text
replicate the histogram so that subsets of threads update private copies
instead of all fighting over one global copy
```

The most useful form in CUDA is block-level privatization:

- each thread block gets its own private histogram,
- updates are performed on that private copy,
- and the private copies are merged later.

This dramatically reduces contention in the main update phase.

The tradeoff is that privatization introduces:

- extra storage,
- initialization cost,
- and merge cost.

So privatization is a classic exchange:

- less update contention now
- for extra combine work later

## Shared-Memory Privatization

If the histogram is small enough, the block-private copy can live in shared memory.

This is especially attractive because:

- shared memory has much lower latency than global memory,
- all threads in the block can access it,
- and block-local synchronization is easy with `__syncthreads()`.

So the pattern becomes:

1. initialize a block-private histogram in shared memory
2. let threads update it with much cheaper local atomics
3. synchronize
4. merge the result into a global histogram

This is one of the clearest examples of why shared memory is useful not only for reuse but also for fast block-local coordination on shared data.

## Coarsening

Privatization helps with update contention, but it introduces another cost:

- every block creates one private copy,
- and every private copy must be merged.

If there are many blocks, merge overhead can become unnecessarily large.

Thread coarsening helps by reducing the number of blocks and giving each thread multiple input elements.

That means:

- fewer blocks,
- fewer private histograms,
- and fewer block-level merges.

The chapter compares two ways to assign multiple inputs to a thread:

### Contiguous partitioning

Each thread gets one contiguous chunk of input.

This is intuitive and often good on CPUs, but it is usually weaker on GPUs because it does not preserve the best warp-level memory coalescing behavior.

### Interleaved partitioning

Each thread steps through the input in strides of the total grid size.

This is generally better for GPUs because in each iteration:

- neighboring threads access neighboring memory locations,
- so memory accesses stay coalesced.

So the CUDA lesson is that coarsening should preserve cooperative memory behavior, not just reduce thread count.

## Aggregation

Some inputs contain long local streaks of identical or same-bin values.

For those cases, another useful optimization is aggregation.

The idea is:

```text
if one thread sees several consecutive inputs that hit the same bin,
it should locally count them first and issue one atomic update later
```

So instead of:

- many repeated atomic adds to the same bin

the thread performs:

- one larger atomic add for the whole streak

This can reduce contention significantly when input values are locally repetitive.

The tradeoff is:

- more local state,
- more branching,
- and more code.

So aggregation only helps when there is enough local repetition to justify the extra control logic.

## The Full Optimization Ladder

The histogram chapter builds a very clear progression:

1. basic atomic kernel
2. privatization
3. shared-memory privatization
4. coarsening
5. aggregation

This progression is important because it shows how CUDA optimization usually develops:

- first guarantee correctness,
- then reduce contention,
- then reduce latency,
- then reduce overhead,
- then reduce the number of synchronization events themselves.

That is a reusable systems pattern, not just a histogram trick.

## Common Mistakes

- Assuming histograms fit the owner-computes rule like convolution or stencil sweeps do.
- Forgetting that increment is a read-modify-write sequence and therefore race-prone.
- Treating atomics as cheap just because they are built-in.
- Reasoning about histogram speed from peak memory bandwidth instead of contention behavior.
- Ignoring input distribution when estimating atomic performance.
- Using coarsening without preserving coalesced memory accesses.
- Adding aggregation logic even when the data has little or no local repetition.

## Why This Matters for ML Systems

Parallel histograms matter to ML systems because they teach how to reason about a whole class of patterns where:

- the output location is data-dependent,
- the output structure is shared,
- and correctness requires explicit synchronization.

That perspective matters for:

- feature counting,
- sparse or irregular reductions,
- routing statistics,
- token or category frequency analysis,
- and many other patterns where threads update shared summaries rather than private outputs.

More broadly, histogram optimization reinforces a durable CUDA lesson:

```text
when output interference is unavoidable,
performance depends on how cleverly contention is reshaped
```

Privatization, coarsening, and aggregation are all examples of that principle.

## Short Takeaway

Parallel histograms are the first major CUDA pattern in this folder where output ownership breaks down: many threads may need to update the same bin, so correctness requires atomics, and performance depends on reducing contention through privatization, shared-memory local copies, coarsening, and aggregation.
