# 12. Quick Sort and Partition Template

## What

This template captures the hand-coding core of DS10:

- quick sort as a recursive sorting method,
- pivot-based partitioning,
- the in-place partition loop,
- and the final pivot swap that makes the recursion valid.

It is based on [11. Quick Sort and In-Place Partitioning](/Users/minkyu/Documents/mlsys/foundations/data-structures/11-quick-sort-and-in-place-partitioning.md).

## Core Rules

```text
save the pivot;
grow the smaller-than-pivot region with j;
swap the pivot into j;
recurse on the two sides.
```

That is the shortest reliable reconstruction rule from the lecture.

## Canonical Java Skeleton

Code:
- [12-quick-sort-and-partition-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/12-quick-sort-and-partition-template.java)

The file keeps the lecture's core structure:

- `quickSort(int[] a, int left, int right)`
- pivot chosen from `a[left]`
- in-place partition with `i` and `j`
- final pivot swap
- recursive calls on the two subranges

## Partition Invariant

During the scan:

```text
a[left + 1 ... j] are smaller than the pivot;
a[j + 1 ... i - 1] have been scanned but are not smaller than the pivot;
a[i ... right] have not yet been examined.
```

This is the one invariant to remember if the code goes blank during the exam.

## What j Means

`j` is not a random index.

It marks the end of the region known to be smaller than the pivot.

So when `a[i] < pivot`:

1. extend that region by doing `j++`,
2. swap the newly found small element into `a[j]`.

That is the whole logic of the partition loop.

## Why the Final Pivot Swap Matters

The pivot starts in `a[left]` only temporarily.

After the scan, the pivot must be moved into `a[j]`.
Only then is the partition postcondition true:

- everything left of `j` is smaller than the pivot,
- `a[j]` is the pivot,
- everything right of `j` is greater than or equal to the pivot.

Without that swap, the recursive calls are working on the wrong structure.

## Recursive Structure

The lecture's recursive template is:

1. stop if `left >= right`,
2. partition the subarray,
3. recurse on `left ... j - 1`,
4. recurse on `j + 1 ... right`.

The pivot position itself is excluded because it is already final after partitioning.

## Pressure Checklist

1. Did I keep the base case `left >= right`?
2. Did I save the pivot before scanning?
3. Did I initialize `j = left`?
4. When `a[i] < pivot`, did I increment `j` before swapping?
5. Did I do the final swap between `a[left]` and `a[j]`?
6. Did I recurse on `j - 1` and `j + 1`, not on ranges that still include the pivot?

## Common Mistakes

- Forgetting what `j` represents and treating it like a second scan index.
- Swapping with `a[j]` before incrementing `j`.
- Forgetting the final pivot swap.
- Recursing on ranges that still include the pivot position.
- Mixing quick sort's in-place partitioning with merge sort's auxiliary-array mindset.

## Short Takeaway

For DS10, the hand-coding heart of quick sort is the partition invariant. Save the pivot, use `j` as the boundary of the smaller-than-pivot region, swap qualifying elements inward, place the pivot at `j`, and then recurse on the two sides.
