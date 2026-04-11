# 10. Prefix Sum (Scan)

## What

Prefix sum, usually called scan, is a parallel pattern that produces all prefix accumulations of an input array.

For addition:

- input: `[x0, x1, x2, ...]`
- inclusive scan: `[x0, x0+x1, x0+x1+x2, ...]`
- exclusive scan: `[0, x0, x0+x1, ...]`

Unlike reduction, which returns one final value, scan returns one output per input position.

## Why It Matters

Scan is one of the most important GPU primitives because it turns many seemingly sequential recurrences into parallel computation.

Typical uses include:

- resource allocation
- work assignment
- stream compaction
- radix sort
- quick-sort partition support
- polynomial evaluation
- recurrence solving
- tree operations

The systems reason scan matters is simple:

```text
many applications contain small sequential prefix dependencies,
and scan is one of the standard ways to remove them
```

## Inclusive and Exclusive Scan

Inclusive and exclusive scan differ only in alignment.

### Inclusive

Each output includes the corresponding input element.

For addition:

```text
y[i] = x0 + x1 + ... + xi
```

### Exclusive

Each output excludes the corresponding input element and starts from the operator's identity value.

For addition:

```text
y[0] = 0
y[i] = x0 + x1 + ... + x[i-1]
```

This distinction matters in practice:

- inclusive scan gives cut points
- exclusive scan gives starting offsets

That is why exclusive scan appears so often in memory allocation and work placement.

## Scan Needs an Associative Operator

Like reduction, scan depends on a binary associative operator.

Examples:

- sum
- product
- min
- max

Associativity matters because parallel scan rearranges how partial results are grouped.

```text
(a ⊙ b) ⊙ c = a ⊙ (b ⊙ c)
```

Without that property, the parallel regrouping would not preserve the sequential result.

## Sequential Scan Is Already Work-Optimal

The sequential inclusive scan is extremely simple:

1. set `y[0] = x[0]`
2. for each later position, add the current input to the previous output

This uses only `N - 1` additions for `N` elements.
So sequential scan is already `O(N)` and therefore work-optimal up to constant factors.

That fact becomes important immediately, because many parallel scan algorithms do more total work than the sequential baseline.

## Why Naive Parallel Scan Fails

A bad idea is to let each thread compute one output position by performing its own full prefix reduction.

That fails for two reasons:

- the slowest thread still takes `O(N)` time
- the total amount of work becomes `O(N^2)`

So the naive method gives:

- no useful critical-path improvement
- much worse work efficiency

This is the wrong way to parallelize scan.

## Kogge-Stone Scan

The first important parallel scan algorithm is Kogge-Stone.

Its idea is to reuse partial sums across multiple outputs instead of letting every output build its own separate reduction tree.

The algorithm evolves an in-place array in rounds with stride:

- `1`
- `2`
- `4`
- `8`
- ...

After each round, position `i` contains a prefix over a larger range ending at `i`.

### Why Kogge-Stone Is Attractive

- conceptually simple
- shallow parallel depth
- only `log2(N)` rounds

So with abundant execution resources, it can be very fast.

### Why Kogge-Stone Is Not Work-Efficient

Its total work is `O(N log N)`, not `O(N)`.

That is much better than the naive `O(N^2)` idea, but still worse than the sequential scan.

So Kogge-Stone is best understood as:

```text
speed-oriented, not work-oriented
```

## The Write-After-Read Hazard in Scan

Kogge-Stone scan is more subtle than reduction because in one round:

- one thread may want to overwrite `XY[i]`
- while another thread still needs the old value of `XY[i]`

That creates a write-after-read hazard.

The standard in-place fix is:

1. compute the updated value into a temporary register
2. synchronize
3. only then overwrite shared memory

So scan needs more careful intra-round coordination than reduction.

Double-buffering can also solve the hazard by separating read and write arrays, but that costs more shared memory.

## Brent-Kung Scan

Brent-Kung improves work efficiency by dividing scan into two phases:

1. a reduction tree phase
2. a reverse tree distribution phase

The first phase computes the necessary block totals and intermediate partial sums.
The second phase pushes those partial sums back into positions that need them to finish their prefixes.

### Why Brent-Kung Matters

Its total work is `O(N)`, not `O(N log N)`.

So Brent-Kung is work-efficient and data-scalable in a way Kogge-Stone is not.

### Tradeoff Against Kogge-Stone

Brent-Kung uses fewer total operations, but it requires more phases and therefore more steps.

So the comparison is:

- Kogge-Stone: less depth, more work
- Brent-Kung: more work-efficient, more steps

With abundant hardware, Kogge-Stone can still win on time.
With limited hardware, Brent-Kung often becomes more attractive.

## Work Efficiency as a First-Class Concern

Scan is one of the cleanest examples of why parallel algorithms cannot be judged only by how much parallelism they expose.

A good scan algorithm must be evaluated along at least three axes:

- parallel depth
- total work
- practical hardware utilization

This is why scan is such a useful CUDA case study.
It forces us to think beyond:

```text
more threads = better performance
```

## Thread Coarsening for Scan

Thread coarsening improves scan by giving each thread a contiguous subsection of input elements.

The coarsened design has three phases:

1. each thread performs a sequential scan on its own local subsection
2. threads collaboratively scan the last value from each subsection
3. each thread adds the predecessor-subsection offset back into its local scanned values

### Why This Helps

- the expensive global parallel scan is reduced to a much smaller problem
- local sequential scan is work-efficient
- synchronization-heavy tree work is applied only to subsection totals
- execution resources are used more effectively on limited hardware

This is scan's version of the same broad lesson we saw in reduction:

```text
sometimes the best GPU algorithm includes deliberate local serialization
to reduce global parallel overhead
```

## Segmented Hierarchical Scan for Large Inputs

Real scans often involve millions or billions of elements, so one block-local scan is not enough.

The standard large-input solution is hierarchical:

1. each block scans its own local section
2. the last output of each block, which equals that block's total sum, is gathered into a smaller array
3. that smaller array is scanned
4. the resulting block-level offsets are added back into later blocks

This turns block-local prefix results into the final global scan.

The key observation is:

```text
the last element of each scanned block is the block sum
```

So block boundaries can be connected through a second-level scan.

## Single-Pass Domino-Style Scan

The straightforward hierarchical method usually uses multiple kernels:

- one for local block scans
- one for the scan of block sums
- one for adding offsets back

That is correct, but it causes extra global-memory traffic and kernel-boundary overhead.

A more efficient approach is single-pass, domino-style scan.

The idea is:

- each block scans its own section locally
- then waits for the cumulative sum from its left neighbor
- adds that offset to its local results
- and passes the updated cumulative sum to its right neighbor

So block sums flow through the grid like dominoes.

### Why This Is Hard

CUDA has no ordinary block-to-block barrier inside one kernel.
So domino-style scan needs custom adjacent-block synchronization.

The standard mechanism uses:

- a global array for passed scan values
- a global flags array
- atomic operations to signal readiness
- `__threadfence()` to guarantee memory visibility

This is a producer-consumer chain implemented across thread blocks.

## Deadlock Risk and Dynamic Block Index Assignment

Domino-style scan has a subtle scheduling risk.

CUDA does not guarantee blocks launch in simple `blockIdx.x` order.
If later logical blocks are scheduled before their predecessors and occupy all SMs while waiting, deadlock can occur.

One way to prevent this is dynamic block index assignment:

- blocks acquire a logical block index from a global atomic counter at runtime

That guarantees logical block `i` was scheduled only after logical block `i - 1` had at least begun execution.

So dynamic assignment aligns producer-consumer ordering with actual scheduling order.

## Why Scan Is Harder Than Reduction

Reduction already exposed:

- divergence
- coalescing
- shared-memory staging
- synchronization
- multi-block composition

Scan adds one more major dimension:

- work efficiency

and then pushes further into:

- hierarchical prefix composition
- block-to-block dependency passing
- memory-ordering correctness
- and scheduler-dependent deadlock avoidance

So scan is best seen as a richer, harder cousin of reduction rather than as just “reduction with more outputs.”

## Kogge-Stone vs Brent-Kung vs Coarsened Scan

The practical comparison is:

### Kogge-Stone

- simple
- shallow depth
- `O(N log N)` work
- good for modest sections and abundant hardware

### Brent-Kung

- more complex
- deeper
- `O(N)` work
- often better when resources are limited

### Coarsened scan

- adds local sequential scan
- reduces synchronization-heavy parallel work
- improves practical efficiency further

No single variant is always best.
The right choice depends on:

- available execution resources
- latency tolerance
- memory hierarchy behavior
- and whether work efficiency or shallow depth is the more important bottleneck.

## Common Mistakes

- Treating scan as if it were just reduction with a different output shape.
- Ignoring the difference between inclusive and exclusive scan.
- Assuming the shallowest algorithm is always the fastest.
- Forgetting that Kogge-Stone is not work-efficient.
- Forgetting the write-after-read hazard in in-place scan.
- Assuming hierarchical multi-kernel scan is automatically memory-efficient.
- Ignoring deadlock risk in single-pass inter-block scan.

## Why This Matters for ML Systems

Scan appears across GPU systems work whenever irregular allocation, compacting, grouping, or routing needs prefix offsets.

It matters because many important workloads contain hidden recurrence structure that looks sequential at first glance.
Scan is one of the standard ways to break that sequential dependency while still preserving correct prefix ordering.

It is also a strong systems lesson:

```text
parallel speed, work efficiency, synchronization cost, and memory traffic
must all be balanced together
```

## Short Takeaway

Prefix sum, or scan, is the parallel pattern that computes all prefix accumulations and converts many recurrence-like sequential bottlenecks into parallel work. Kogge-Stone favors shallow depth, Brent-Kung favors work efficiency, coarsening improves practical resource use, and hierarchical or domino-style scan extends the pattern to large inputs while introducing inter-block synchronization and memory-ordering challenges.
