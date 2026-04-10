# 10. Sorting Basics and Divide-and-Conquer

## What

This note opens the sorting branch of the course.

The lecture covers:

- what sorting means,
- bubble sort,
- selection sort,
- insertion sort,
- stable sorting,
- merging two sorted lists,
- merge sort,
- and the core idea of quick sort.

## Why It Matters

Sorting is one of the most important recurring problems in computer science.

The lecture presents it in exactly the right way:

- not as a single algorithm,
- but as a family of design choices with different tradeoffs.

This matters because sorting is where several earlier themes meet:

- asymptotic reasoning,
- loop invariants,
- stability,
- recursion,
- and divide-and-conquer.

## Core Idea

The lecture starts from the basic task:

```text
given a list of items with keys,
reorder them by ascending or descending key value
```

From there, it compares several different ways to impose order:

- repeated adjacent swaps,
- repeated minimum selection,
- repeated local insertion,
- recursive splitting and merging,
- and recursive partitioning around a pivot.

So the real lesson is not "how to sort."
It is:

```text
different sorting strategies expose different structural ideas
```

## Bubble Sort

The lecture introduces bubble sort as:

- repeatedly checking whether each consecutive pair is in order,
- and swapping when it is not.

The important invariant highlighted in the lecture is:

- at the end of the `k`-th iteration, the last `k` elements are in their final places.

That is the structural reason the algorithm progresses.

## Bubble Sort: Correctness and Termination

The lecture explicitly asks two good questions:

- does this algorithm produce a sorted list?
- does this algorithm terminate?

That matters because the note is not just presenting code.
It is training the habit of checking:

- correctness,
- progress,
- and stopping behavior.

## Bubble Sort Running Time

The lecture points out:

- if the list is already sorted, the algorithm can stop after one linear pass,
- so the best case is `Theta(n)`,
- while the worst case is `Theta(n^2)`.

The inversion-based explanation is especially useful:

- each out-of-order pair is an inversion,
- each swap removes one inversion,
- and many inversions imply many swaps.

That gives a more structural understanding than just memorizing the bound.

## Selection Sort

Selection sort is introduced as:

- find the minimum element,
- move it to the beginning,
- and repeat on the rest.

Its key structural idea is different from bubble sort:

- bubble sort gradually fixes the back of the array,
- selection sort fixes the front one position at a time.

The lecture gives the straightforward implementation and records its time as `O(n^2)`.

## Insertion Sort

Insertion sort is introduced with a very useful invariant:

- at the beginning of the `k`-th iteration, the first `k - 1` elements are already sorted.

Then the `k`-th element is inserted into that sorted prefix.

This is one of the cleanest loop-invariant stories in the course so far.

## Why Insertion Sort Often Feels Better

The lecture notes that insertion sort is:

- `O(n^2)` in the worst case,
- but usually the fastest among the three simple quadratic sorts.

That is an important practical note.

Asymptotic class alone does not tell the whole story.
Within the same `O(n^2)` family, constant factors and data movement patterns still matter.

## Stability

The lecture then introduces the notion of a stable sort:

- when keys tie, the original relative order is preserved.

It specifically notes that:

- bubble sort and insertion sort are naturally stable.

This is an important refinement because not all sorting quality is captured by runtime alone.

Sometimes preserving preexisting order among equals is semantically important.

## Merging Two Sorted Lists

The lecture then shifts to a new building block:

- given two sorted lists,
- combine them into one sorted list.

The key result is:

- two lists of sizes `l1` and `l2` can be merged in `O(l1 + l2)` time.

This is the bridge to merge sort.

## Merge Sort

The lecture presents merge sort as:

- recursive,
- divide and conquer,
- split the list into two halves,
- sort each half recursively,
- merge the sorted halves.

This is the branch's first major sorting algorithm built around recursion rather than local adjacent updates.

## Merge Sort Running Time

The lecture walks through an important analysis mistake first:

- each call seems to take `O(n)` in the worst case,
- there are `2n - 1` calls,
- so a loose bound suggests `O(n^2)`.

Then it corrects that reasoning:

- later recursive calls work on much smaller subarrays,
- so the `O(n)` per call bound is not tight for the whole recursion tree.

The final result is:

- merge sort runs in `O(n log n)` time.

This is an excellent example of why recursive algorithms need recursion-tree style reasoning rather than flat call counting.

The lecture also makes the recursive call structure explicit:

- there are `n - 1` splitting calls,
- `n` terminal calls,
- and `2n - 1` total calls.

That count is useful, but the lecture's main warning is that total call count alone does not determine the tight running time.
The deeper calls do much less work than the top-level calls.

## Merge Sort Tradeoffs

The lecture also records two important practical facts:

- merge sort is easily made stable,
- but it uses `O(n)` auxiliary space.

That matters because it distinguishes merge sort from the earlier in-place quadratic sorts.

So the gain in asymptotic time comes with a space cost.

## Quick Sort as the Next Recursive Strategy

The lecture closes by introducing the core idea of quick sort, which is developed in the next note.

Quick sort is presented as:

1. choose a pivot,
2. partition the list into elements smaller and larger than the pivot,
3. recursively sort the two parts.

The important contrast with merge sort is conceptual:

- merge sort divides first and combines later,
- quick sort partitions around a pivot and then recursively cleans up the pieces.

The lecture is mainly setting up the next stage of the branch here, but the design idea is already clear.

## What This Note Is Really Teaching

This lecture is not only a catalog of sorting algorithms.
It is a compact survey of several algorithm design styles:

- repeated local repair,
- repeated global selection,
- incremental maintenance of a sorted prefix,
- divide and conquer through merging,
- and divide and conquer through partitioning.

That is why sorting is such a central teaching topic.

## Common Mistakes

- Treating all `O(n^2)` sorting algorithms as interchangeable.
- Memorizing code without the invariant that explains progress.
- Forgetting that bubble sort has a linear best case when implemented with an early-stop check.
- Ignoring stability when comparing algorithms.
- Using flat call counting to analyze merge sort and concluding `O(n^2)`.
- Forgetting that merge sort's better time bound comes with extra auxiliary space.
- Treating the introductory quick-sort slides as if they already explained the full partitioning implementation.

## Why This Matters for CS / Systems

This note matters because sorting is everywhere:

- databases,
- preprocessing pipelines,
- ranking,
- scheduling,
- indexing,
- and systems that depend on ordered data.

It is also one of the clearest places to see how algorithm design decisions trade off time, space, and structural simplicity.

## Short Takeaway

This lecture introduces sorting as a comparison of design ideas, not just named algorithms. Bubble, selection, and insertion sort all run in `O(n^2)` worst-case time but rely on different invariants and behaviors. Merge sort introduces recursive divide-and-conquer and improves the time bound to `O(n log n)` at the cost of extra space, while quick sort is introduced as the next recursive strategy based on pivot partitioning.
