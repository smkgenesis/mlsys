# 11. Quick Sort and In-Place Partitioning

## What

This note develops the quick-sort material introduced at the end of the previous sorting lecture.

The lecture focuses on:

- pivot-based partitioning,
- recursive quick sort,
- the in-place partition procedure that rearranges the array around the pivot,
- quick-sort running-time analysis,
- pivot choice,
- and radix sort as a contrast to comparison-based sorting.

## Why It Matters

Merge sort already showed one divide-and-conquer sorting strategy:

- divide first,
- sort recursively,
- merge later.

Quick sort matters because it shows a different design:

- choose one pivot,
- partition the array in place around it,
- and then recurse on the two resulting regions.

That makes it one of the cleanest examples of how a local invariant can define a recursive algorithm.

## Core Idea

The lecture's quick-sort story is:

```text
pick a pivot,
move smaller elements to its left,
move larger-or-equal elements to its right,
put the pivot into its final position,
then recurse on the two sides
```

So quick sort does its key rearrangement before the recursive calls rather than after them.

## Quick Sort Structure

The recursive structure in the lecture is:

1. if the subarray has size 0 or 1, stop,
2. choose the pivot,
3. partition the subarray,
4. recursively sort the left side,
5. recursively sort the right side.

The base case `left >= right` is what prevents the recursion from continuing forever.

## The Partition Postcondition

The lecture makes the partition goal very explicit.

After partitioning:

- `a[left] ... a[j - 1]` are smaller than the pivot,
- `a[j]` holds the pivot,
- `a[j + 1] ... a[right]` are greater than or equal to the pivot.

That postcondition is the heart of the algorithm.
The recursive calls are only correct because partitioning establishes that structure first.

## In-Place Partitioning

The lecture then gives the in-place partition code.

Its shape is:

1. store the pivot from `a[left]`,
2. maintain a boundary index `j`,
3. scan the remaining elements with `i`,
4. whenever `a[i] < pivot`, enlarge the "smaller than pivot" region and swap that element inward,
5. after the scan, swap the pivot into index `j`.

This is the key implementation idea:

- use the original array itself as the workspace,
- maintain a moving boundary,
- and avoid a separate merge buffer.

## The Meaning of j

The partition loop becomes much easier to understand if `j` is interpreted correctly.

During the scan:

- `a[left + 1] ... a[j]` are already known to be smaller than the pivot,
- `a[j + 1] ... a[i - 1]` are scanned but not smaller than the pivot,
- `a[i] ... a[right]` have not yet been examined.

That is the invariant the loop preserves.

## Why the Final Pivot Swap Matters

The pivot begins at the left end only temporarily.

After the scan finishes, it must be swapped into `a[j]`.
That matters because:

- every smaller element is then to its left,
- every larger-or-equal element is to its right,
- and the pivot has reached its final place for the current subarray.

Only then do the recursive calls make sense.

## In Place Versus Merge Sort

The lecture explicitly notes:

- partitioning can be done in place

That is the major contrast with merge sort.

Merge sort needed:

- an auxiliary array,
- plus a separate merge phase.

Quick sort instead tries to do the important reorganization directly inside the input array.

That is one of the central practical differences between the two recursive sorting strategies.

## Quick Sort Running Time

The lecture then turns to running time.

The first tempting argument is:

- partitioning a subarray of length `l` takes `O(l)` time,
- and if every recursive call splits nicely in half,
- quick sort should run in `O(n log n)` time.

The lecture explicitly points out that this is not a valid worst-case assumption.

## Why Balanced Splits Are Not Guaranteed

Quick sort only gets a balanced recursion tree when pivot choices produce balanced partitions.

The lecture gives the standard counterexample:

- a sorted list can be a worst case

if the pivot rule is unlucky, such as always choosing the first element.

Then one side is tiny while the other side is almost the whole array again.

## Worst-Case Quick Sort

That leads to the lecture's key worst-case conclusion:

- quick sort runs in `O(n^2)` time in the asymptotic worst case.

This is the main contrast with merge sort:

- merge sort's `O(n log n)` bound is structurally guaranteed,
- quick sort's depends on the quality of the partitioning.

## Pivot Choice Matters

The lecture makes the dependence explicit:

- efficiency depends on the choice of pivot

and mentions strategies such as:

- random choice
- median-of-median

The point here is not yet a full analysis of every pivot rule.
It is to make one design fact visible:

```text
quick sort is only as good as the partitions its pivots create
```

## Randomized Quick Sort

The lecture then shows a version that chooses a random index, swaps it into `a[left]`, and uses that as the pivot.

That small change is conceptually important.

It turns pivot choice from:

- deterministic and highly input-sensitive

into:

- randomized and much harder for a bad input order to defeat systematically.

The lecture's resulting claim is:

- quick sort runs in `O(n log n)` time in expectation.

## Comparison-Based Sorting Boundary

The lecture also states a broader fact:

- `O(n log n)` is the best asymptotic running time achievable by a comparison-based sorting algorithm.

This gives useful context for both merge sort and quick sort.
They are asymptotically optimal within the comparison model.

## Radix Sort as the Contrast

The lecture then introduces radix sort to show what happens when we move beyond comparison-based sorting.

The examples use:

- digit bins,
- stable redistribution,
- and both MSD and LSD viewpoints.

The lecture emphasizes:

- linked-list based bucket implementation,
- stability,
- and the running time of LSD radix sort:

`O(d(n + r))`

where:

- `d` is the number of digits,
- `r` is the radix.

This is the important contrast:

- comparison sorts meet the `O(n log n)` frontier,
- radix sort can follow a different complexity path because it exploits internal digit structure rather than pure pairwise comparisons.

## What This Note Is Really Teaching

This lecture is not only about quick sort as a name.
It is teaching a broader design move:

```text
use a pivot and maintain a partition invariant in place,
then let recursion finish the job on the two resulting regions
```

That is a very different sorting instinct from:

- bubbling adjacent inversions away,
- selecting minima,
- or recursively merging sorted halves.

The radix-sort section adds one more lesson:

```text
asymptotic limits depend on the computational model
```

If we are not restricted to comparison-based sorting, the frontier can change.

## Common Mistakes

- Treating quick sort as if recursion alone explains it.
- Ignoring the partition postcondition that makes the recursive calls valid.
- Losing track of what `j` represents during partitioning.
- Forgetting the final pivot swap.
- Confusing quick sort's in-place partitioning with merge sort's auxiliary-array merge.
- Assuming the balanced-partition case is automatically the worst-case analysis.
- Forgetting that randomized pivot choice changes expected behavior without removing the worst case entirely.
- Treating radix sort as if it were just another comparison sort.

## Why This Matters for CS / Systems

This note matters because in-place partitioning is a useful systems idea as well as a sorting idea.

It shows how to:

- reorganize data locally,
- preserve a strong invariant,
- and avoid extra workspace when the representation permits it.

That mindset appears far beyond quick sort.
The radix-sort comparison adds a second systems lesson: if the representation exposes more structure than ordering-by-comparison alone, the achievable algorithmic frontier can change.

## Short Takeaway

Quick sort works by choosing a pivot, partitioning the array in place around that pivot, and then recursively sorting the two resulting sides. Its worst case is `O(n^2)` when partitions are bad, but randomized pivot choice gives `O(n log n)` expected time. The lecture then uses radix sort to show the bigger lesson: `O(n log n)` is the comparison-sorting frontier, not the universal frontier for all sorting models.
