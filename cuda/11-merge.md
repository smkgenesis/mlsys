# 11. Merge

## What

Merge takes two sorted input arrays and produces one sorted output array containing all of their elements.

If:

- `A` has `m` elements,
- `B` has `n` elements,

then merge produces:

- `C` with `m + n` elements,

such that `C` is also sorted.

In the stable version, if two keys are equal, the element from `A` appears before the equal-key element from `B`.

## Why It Matters

Merge is not just a utility routine.
It is a core building block for:

- merge sort,
- distributed reduce and map-reduce pipelines,
- ordered set intersection,
- ordered set union,
- and many data-dependent partitioning tasks.

The systems reason this chapter matters is:

```text
merge is a parallel pattern whose input ranges are data-dependent,
so thread work assignment cannot be determined by simple index arithmetic
```

That makes merge a useful CUDA case study for irregular access patterns.

## Sequential Merge Is Simple and Work-Optimal

The sequential merge algorithm keeps three pointers:

- `i` into `A`,
- `j` into `B`,
- `k` into `C`.

At each step, it compares:

- `A[i]`
- and `B[j]`

and writes the smaller one into `C[k]`.

When one input is exhausted, the remainder of the other input is copied directly to `C`.

Its total work is:

```text
O(m + n)
```

because every input element is visited once and every output location is written once.

This matters because parallel merge is not trying to reduce total work below linear.
It is trying to expose parallelism without destroying memory efficiency.

## Why Merge Is Harder to Parallelize Than Reduction or Scan

In previous CUDA patterns, the input range for each thread was usually determined by a simple formula.

Examples:

- thread `t` reads input `t`,
- or block `b` reads a contiguous chunk of the input.

Merge breaks that regularity.

If a thread is responsible for some output section of `C`, the corresponding input sections of `A` and `B` depend on the actual values stored in those arrays.

So merge is hard because:

- output ownership is easy to assign,
- input ownership is data-dependent,
- and locality is therefore much harder to preserve.

## The Output-First Parallelization Strategy

The standard parallel merge strategy is:

1. partition the output array `C`,
2. use those output boundaries to identify matching input boundaries in `A` and `B`,
3. and let each thread or block merge only its local subarrays.

This works because for any output rank `k`, there is a unique way to split the first `k` output elements into:

- `i` elements from `A`
- and `j` elements from `B`

with:

```text
k = i + j
```

The values `i` and `j` are the co-ranks of `k`.

## Co-Rank

For an output boundary `k`, co-rank identifies the unique input boundary pair `(i, j)` such that:

- `k = i + j`,
- the first `k` output elements are exactly the merge of `A[0..i-1]` and `B[0..j-1]`.

The key boundary conditions are:

- `A[i-1] <= B[j]`
- `B[j-1] < A[i]`

These conditions encode both:

- sortedness across the boundary,
- and stability when equal keys are split across `A` and `B`.

Because `A` and `B` are sorted, we can find the correct `i` with binary search and derive:

```text
j = k - i
```

So co-rank has:

```text
O(log N)
```

search cost, where `N` is on the order of the input size.

## A Basic Parallel Merge Kernel

The simplest kernel is a direct implementation of the output-first idea.

Each thread:

1. computes its output range `[k_curr, k_next)`,
2. calls co-rank on `k_curr`,
3. calls co-rank on `k_next`,
4. derives its local `A` and `B` subarrays,
5. runs a small sequential merge on those subarrays.

This design is elegant because it reuses the sequential merge logic inside each thread.

But it performs poorly on a GPU.

## Why the Basic Kernel Performs Poorly

The main problem is memory access efficiency.

### 1. The local merge accesses are irregular

Adjacent threads in a warp do not generally read adjacent elements of `A` or `B`, and they do not generally write adjacent elements of `C`.

So:

- global reads are often not coalesced,
- global writes are often not coalesced,
- and memory bandwidth is poorly utilized.

### 2. The co-rank searches are also irregular

Each co-rank call performs a binary search over sorted inputs.

Those accesses:

- jump around,
- are data-dependent,
- and are unlikely to coalesce.

So even the setup phase of the kernel puts pressure on global memory efficiency.

## Tiling for Merge

To improve coalescing, the chapter moves from thread-level irregular access to block-level staging.

The key observation is:

```text
although adjacent threads use irregular input slices,
the union of all slices needed by a block forms larger contiguous ranges in A and B
```

So the block-level design is:

1. partition `C` into block-level output subarrays,
2. compute block-level co-ranks,
3. cooperatively load larger contiguous input tiles from `A` and `B` into shared memory,
4. and perform the thread-level irregular accesses inside shared memory instead of global memory.

This greatly improves coalescing and reduces the number of global-memory co-rank accesses.

## Why Merge Tiling Is Harder Than Earlier Tiling Patterns

If the shared-memory tiles `A_S` and `B_S` each hold `x` elements, then loading:

- `x` elements from `A`
- and `x` elements from `B`

guarantees only:

- `x` output elements

not `2x`.

That is because in the worst case, all `x` output elements for that iteration may come entirely from one input tile.

So merge tiling has an uncertainty that earlier patterns did not:

```text
we know how many output elements we want,
but not in advance how many will come from A versus B
```

This makes merge tiling structurally more difficult than convolution, stencil, reduction, or scan.

## The Basic Tiled Kernel Still Wastes Bandwidth

The tiled kernel improves coalescing, but it still has a major deficiency.

In each iteration, it loads:

- `x` new `A` elements
- `x` new `B` elements

into shared memory.

But only about half of those loaded elements are actually consumed before the next iteration.

The remaining elements stay logically useful, but the naive tiled kernel simply reloads new tiles from global memory starting again at `A_S[0]` and `B_S[0]`, overwriting those leftovers.

So the kernel improves coalescing but wastes roughly half of the loaded shared-memory tile contents.

## Circular Buffer Design

The circular-buffer merge kernel fixes that waste.

Instead of treating `A_S` and `B_S` as fresh tiles starting at index 0 every iteration, it treats them as circular buffers.

Two new logical pointers are maintained:

- `A_S_start`
- `B_S_start`

These track where the current live tile begins inside the shared-memory buffers.

At the end of each iteration, the kernel also tracks how many `A` and `B` elements were consumed:

- `A_S_consumed`
- `B_S_consumed`

The next iteration then:

- keeps the unused elements already present in shared memory,
- loads only enough new elements to refill the buffers,
- and wraps around to the beginning of the shared arrays when necessary.

So the shared-memory tiles become reusable instead of disposable.

## The Simplified Circular-Buffer Model

Circular buffers improve bandwidth efficiency, but they make indexing much more complex.

If the code directly manipulated wrapped indices everywhere, then:

- end indices could become smaller than start indices,
- lengths would need wrap-aware formulas,
- and both co-rank and local merge logic would become cluttered.

The chapter therefore introduces a simplified logical model:

- threads and library routines reason as if the tile were a contiguous segment,
- while the actual wrap-around is handled only when mapping logical offsets into physical shared-memory indices.

This is a useful systems design lesson:

```text
complex low-level data structures should expose a simpler logical interface whenever possible
```

The complexity is concentrated inside:

- `co_rank_circular`
- and `merge_sequential_circular`

rather than spreading into the whole kernel.

## Thread Coarsening Is Essential for Merge

Merge pays a significant fixed cost per worker:

- each worker must perform co-rank searches,
- and those searches are not cheap.

If one thread were assigned just one output element, then each output element would effectively pay for its own binary search.

That is too expensive.

So merge relies heavily on thread coarsening:

- each thread produces multiple output elements,
- one pair of co-rank searches is amortized across that local output chunk,
- and the cost of dynamic input-range identification becomes tolerable.

In merge, thread coarsening is not just a minor optimization.
It is close to a practical necessity.

## The Main Tradeoff of This Chapter

Merge is a memory-bandwidth-bound pattern whose main algorithmic difficulty is not the merge itself, but finding the right input boundaries for each worker.

That creates a layered optimization story:

1. co-rank gives correctness for data-dependent partitioning,
2. block-level tiling improves coalescing,
3. circular buffers eliminate redundant tile reloads,
4. and thread coarsening amortizes the co-rank search cost.

Every step improves performance, but every step also increases implementation complexity.

This chapter is therefore one of the clearest CUDA examples of:

```text
high-performance irregular kernels require both algorithm design
and memory-management design
```

## Why This Pattern Matters Beyond Merge

The chapter explicitly notes that the same ideas matter for:

- ordered set intersection,
- ordered set union,
- and other computations where input ownership is determined dynamically from sorted data.

So merge is not only a sorting primitive.
It is also a pattern case study for:

- data-dependent work partitioning,
- boundary search,
- and shared-memory management for irregular parallel workloads.

## Short Takeaway

Parallel merge works by partitioning the output and then using co-rank search to discover the matching input ranges in `A` and `B`.
That makes correctness possible, but it also makes the pattern irregular and memory-sensitive.
The full optimization path in this chapter is:

- output-first partitioning,
- co-rank search,
- block-level tiling,
- circular-buffer reuse,
- and thread coarsening to amortize search overhead.

That is why merge is one of the clearest CUDA case studies for turning an irregular, data-dependent pattern into a bandwidth-efficient GPU kernel.
