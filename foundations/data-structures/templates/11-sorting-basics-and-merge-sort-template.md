# 11. Sorting Basics and Merge Sort Template

## What

This template captures the main hand-coding material from DS09:

- bubble sort
- selection sort
- insertion sort
- and merge sort

It is based on [10. Sorting Basics and Divide-and-Conquer](/Users/minkyu/Documents/mlsys/foundations/data-structures/10-sorting-basics-and-divide-and-conquer.md).

Quick sort is only at the setup stage in this lecture, so the real partition template is intentionally deferred to the next note.

## Core Rules

```text
bubble sort fixes the back;
selection sort fixes the front;
insertion sort grows a sorted prefix;
merge sort sorts halves and then merges them.
```

Those four lines are the easiest way to keep the algorithms from blending together under pressure.

## Canonical Java Skeleton

Code:
- [11-sorting-basics-and-merge-sort-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/11-sorting-basics-and-merge-sort-template.java)

The file includes the lecture's basic implementations for:

- `bubbleSort`
- `selectionSort`
- `insertionSort`
- `mergeSort`
- `doMergeSort`

## Bubble Sort Invariant

The lecture's invariant is:

```text
at the end of the k-th iteration,
the last k elements are in their final places
```

This is the key memory hook for reconstructing bubble sort.

Also remember:

- best case `Theta(n)` with the early-stop flag
- worst case `Theta(n^2)`

## Selection Sort Invariant

The key idea is:

```text
after iteration i,
position i contains the correct minimum for the remaining suffix
```

This is the "fix the front one place at a time" algorithm.

## Insertion Sort Invariant

The lecture's invariant is:

```text
at the beginning of the k-th iteration,
the first k - 1 elements are already sorted
```

Then the current element is inserted into that sorted prefix.

This is usually the easiest of the three simple sorts to reason about under exam pressure.

## Stability Reminder

From this lecture:

- bubble sort is naturally stable
- insertion sort is naturally stable

That is a useful comparison point when the question is not only about running time.

## Merge Sort Skeleton

The lecture's split is:

1. allocate `aux` once in `mergeSort`
2. call `doMergeSort(a, 0, n - 1, aux)`
3. base case: `left >= right`
4. recurse on left half
5. recurse on right half
6. merge the two sorted halves into `aux`
7. copy merged range back into `a`

That is the full reconstruction skeleton to remember.

## Merge Loop Invariant

During the main merge loop:

```text
aux[left ... k-1] already contains the smallest processed elements
from the two sorted halves, in sorted order
```

That is the real reason the merge step works.

## Merge Sort Running-Time Reminder

The lecture explicitly warns against the bad analysis:

- `2n - 1` calls
- each call `O(n)`
- therefore `O(n^2)`

That is a loose upper bound, not the tight one.

The correct lecture conclusion is:

- merge sort runs in `O(n log n)`

because deeper calls work on much smaller ranges.

## Pressure Checklist

1. Am I clear on which region each simple sort is fixing?
2. For bubble sort, did I remember the `sorted` flag and swap-based early stop?
3. For selection sort, am I tracking the index of the minimum before swapping?
4. For insertion sort, am I shifting larger elements rightward before placing `tmp`?
5. For merge sort, did I allocate `aux` once and pass it down?
6. Did I keep the base case `left >= right`?
7. After merging into `aux`, did I copy the range back into `a`?

## Common Mistakes

- Mixing up the invariants of bubble, selection, and insertion sort.
- Forgetting the early-stop role of `sorted` in bubble sort.
- Swapping too early in selection sort instead of waiting until the inner scan finishes.
- Forgetting that insertion sort shifts first and writes `tmp` after the loop.
- Allocating a fresh auxiliary array inside every merge call.
- Treating the `2n - 1` call count as the tight merge-sort running time argument.

## Short Takeaway

For DS09, the key hand-coding template is not one sorting algorithm but a family of distinct invariants. Bubble sort fixes the back, selection sort fixes the front, insertion sort grows a sorted prefix, and merge sort recursively sorts halves and merges them with an auxiliary array.
