# 09. Reduction

## What

Reduction is a parallel pattern that derives a single summary value from a collection of values.

Typical examples include:

- sum
- max
- min
- product

Even though the output is only one value, reduction is one of the most important CUDA patterns because it forces many threads to collaborate on one logical result while avoiding synchronization and memory bottlenecks.

## Why It Matters

Reduction appears everywhere in GPU and ML workloads:

- loss accumulation
- norm computation
- score maxima
- statistics summaries
- block-level partial aggregation
- gradient and metric accumulation

Like a histogram, reduction turns a large amount of data into a compact summary.
But unlike a histogram, its structure is not many threads updating many bins.
Its structure is:

```text
many values must be merged into one result through repeated pairwise combination
```

That makes reduction a clean vehicle for learning:

- tree-structured parallelism
- warp divergence
- memory coalescing
- shared-memory staging
- synchronization overhead
- multi-block composition
- and thread coarsening

## Core Idea

The sequential version of reduction is simple:

1. initialize an accumulator to the operator's identity value
2. visit every input element
3. combine the accumulator with the current element

For a sum reduction:

```text
sum = 0
sum = sum + a[0]
sum = sum + a[1]
...
```

Parallel reduction changes only the execution structure.
Instead of one accumulator processing all values in sequence, the values are combined in rounds:

- first pairwise
- then pairwise over partial results
- then pairwise again
- until one final result remains

This structure is the reduction tree.

## Reductions Need an Operator with an Identity

Mathematically, reduction is defined over a binary operator that has an identity value.

Examples:

- sum -> identity `0`
- product -> identity `1`
- min -> identity `+∞`
- max -> identity `-∞`

This matters because every partial result must be combinable using exactly the same operator as the final result.

So a reduction is not just “loop over values.”
It is a disciplined accumulation process with a valid neutral starting value.

## Reduction Trees

The parallel pattern is easiest to understand as a tree:

- leaves are the original input values
- internal nodes are partial reductions
- the root is the final result

For `N` values:

- the total amount of work remains about `N - 1` operator applications
- but the critical-path depth falls from `O(N)` to `O(log N)` steps

That is the main appeal of tree reduction:

```text
same overall work, much shorter dependency depth
```

For example, reducing `1024` values takes only `log2(1024) = 10` tree levels, assuming enough execution resources exist.

## Associativity and Commutativity

Parallel reduction changes the order in which operations are grouped.
So the operator must be associative:

```text
(a ⊙ b) ⊙ c = a ⊙ (b ⊙ c)
```

Examples:

- sum is associative
- max is associative
- min is associative
- subtraction is not associative

Later optimizations may also rearrange operand order rather than only regroup parentheses.
That requires commutativity:

```text
a ⊙ b = b ⊙ a
```

So the practical rule is:

- basic tree reduction requires associativity
- some optimized layouts additionally rely on commutativity

Strict floating-point addition is not perfectly associative, but in practice GPU reduction code often treats it as effectively associative within acceptable numerical tolerance.

## A Simple Single-Block Reduction Kernel

The first CUDA reduction kernel usually starts with a single block.

Why:

- threads inside a block can synchronize with `__syncthreads()`
- threads across the whole grid cannot perform block-style synchronization inside one ordinary kernel

So the simple version performs a reduction tree within one block only.

The basic design is:

- launch `N/2` threads for `N` input values
- give each thread ownership of one even-indexed location
- let each thread repeatedly add another partial sum into its owned location
- synchronize between tree levels

This follows the owner-computes pattern:

- each owned location is written by only one thread
- so atomics are not needed

The tree is built in place:

- round 1 combines adjacent pairs
- round 2 combines pairwise partial sums
- round 3 combines larger partial sums
- eventually thread 0 writes the final result

This kernel is useful pedagogically because it makes the reduction tree explicit.
But it is not yet a good high-performance kernel.

## Why the Simple Kernel Is Slow

The simple kernel has two major performance problems:

- control divergence
- memory divergence

### Control divergence

As the tree progresses, fewer threads remain active:

- first many threads work
- then only half
- then only one quarter
- and eventually only one thread in a warp is active

That means most lanes in a warp consume execution resources without contributing useful work.

### Memory divergence

The simple indexing scheme also causes adjacent threads to access strided, non-adjacent global-memory locations.

So even when useful work is happening, memory accesses are not well coalesced.
That wastes DRAM bandwidth and increases the number of global memory requests.

So the simple kernel is correct, but poor at using both:

- execution resources
- and global-memory bandwidth

## Reducing Control and Memory Divergence

The first big optimization is to rearrange ownership so that active threads stay contiguous as the reduction progresses.

Instead of:

- assigning threads to separated even locations
- and increasing stride over time

the improved kernel:

- assigns threads to adjacent owner locations in the first half
- and decreases stride over time

This has two important effects.

### Less control divergence

Whole warps remain active early on, and later whole warps drop out completely.

That is much better than having every warp carry only a few scattered active threads.

### Better memory coalescing

Because active threads are contiguous:

- adjacent threads read adjacent memory locations
- writes are also contiguous
- accesses become much more coalesced

So one structural change improves both:

- control behavior
- memory behavior

That is one of the nicest CUDA lessons in the chapter:

```text
better thread assignment can improve compute efficiency and bandwidth efficiency at the same time
```

## Using Shared Memory to Reduce Global Traffic

Even the convergent kernel still has an obvious inefficiency:

- each reduction round writes partial sums to global memory
- the next round reads them back

But those partial sums are:

- block-local
- short-lived
- and reused almost immediately

That makes them ideal for shared memory.

The standard optimization is:

1. each thread loads two original values from global memory
2. it adds them immediately
3. it stores the first partial sum in shared memory
4. the remaining reduction rounds happen entirely in shared memory
5. thread 0 writes the final result out

This dramatically cuts global-memory traffic.
It also preserves the original input array, which is useful when the unreduced values are needed later.

So the memory placement rule is:

```text
global memory for input/output,
shared memory for block-local intermediate partial sums
```

## Scaling Beyond One Block

The single-block kernel only works up to the amount of data one block can cooperatively reduce.
To handle larger inputs, the reduction must be segmented across multiple blocks.

The standard multi-block idea is:

- each block reduces its own segment
- each block writes a block-level partial result
- a later stage combines those block-level results

Once multiple blocks contribute partial outputs, atomics or additional reduction stages may be needed depending on how the final accumulation is organized.

This is the point where reduction stops being just a toy block-level example and becomes a full scalable GPU pattern.

The chapter's own summary makes the practical point clearly:

- segmented reduction with atomic operations is one of the tools needed to handle large inputs well

## Thread Coarsening

The kernels so far maximize thread count aggressively:

- for `N` inputs, they launch about `N/2` threads

That sounds good, but it can become wasteful when the hardware cannot execute all those thread blocks simultaneously.

If surplus blocks are serialized anyway, then each block pays its own reduction-tree overhead:

- underutilized late rounds
- barrier synchronization
- shared-memory traffic

Thread coarsening addresses this by giving each thread more input elements to sum locally before entering the collaborative reduction tree.

For example:

- without coarsening: two elements per thread
- with coarsening factor 2: four elements per thread

During the local accumulation phase:

- all threads remain active
- no synchronization is needed
- no shared-memory traffic is needed yet

Only after those local sums are formed do threads enter the tree reduction.

This reduces:

- number of thread blocks
- number of synchronization-heavy tree stages
- shared-memory overhead
- and hardware underutilization

But coarsening has a limit.
If we coarsen too much:

- block count drops too far
- available parallelism falls
- and the GPU can no longer stay fully occupied

So the best coarsening factor balances:

- lower reduction overhead
- against enough block-level parallelism to keep the device busy

## What This Chapter Is Really Teaching

The chapter starts with a conceptually simple pattern:

- combine many values into one

But the real lesson is that high-performance reduction is not about the arithmetic.
It is about the execution structure.

A high-performance CUDA reduction needs several coordinated ideas:

- tree-structured aggregation
- associative operators
- warp-friendly thread assignment
- coalesced memory access
- shared-memory staging
- scalable multi-block organization
- and the right amount of coarsening

That is why reduction is such a central CUDA teaching pattern.
It compresses many of the core optimization themes into one computation.

## Common Mistakes

- Thinking reduction is trivial because the sequential version is just one loop.
- Forgetting that parallel reduction assumes at least associativity, and often commutativity for more aggressive rearrangements.
- Treating a reduction tree as if it were a tree data structure rather than a conceptual information-flow structure.
- Focusing only on arithmetic count and ignoring warp divergence.
- Focusing only on divergence and ignoring memory coalescing.
- Keeping intermediate partial sums in global memory when they are block-local and immediately reused.
- Assuming maximum thread count is always optimal and ignoring thread coarsening tradeoffs.
- Forgetting that single-block reductions do not scale to arbitrary input size without a multi-block strategy.

## Why This Matters for ML Systems

Reduction is everywhere in ML systems, but its importance goes beyond any one operation.

This chapter teaches the habit of asking:

- where are the dependencies?
- which threads actually remain useful at each stage?
- what data should stay on chip?
- and where is the real bottleneck: arithmetic, control flow, or memory traffic?

Those questions recur constantly in:

- attention score summaries
- loss and metric aggregation
- normalization statistics
- and many block-level or kernel-level accumulation patterns

Reduction is also the conceptual foundation for prefix sum (scan), which the book explicitly points to as the next major pattern.

## Short Takeaway

Reduction turns many values into one through a tree of partial combinations. On CUDA, the hard part is not the operator itself but the execution pattern: good reductions require associative operators, warp-friendly thread assignment, coalesced accesses, shared-memory staging, scalable multi-block structure, and enough thread coarsening to reduce overhead without starving the hardware of parallel work.
