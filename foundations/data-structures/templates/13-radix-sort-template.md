# 13. Radix Sort Template

## What

This template captures the hand-coding core from the radix-sort part of DS11:

- digit-by-digit distribution into bins,
- linked-list bucket implementation,
- stable collection back into the array,
- and LSD radix sort as the main coding pattern.

It extends the sorting branch after [12. Quick Sort and Partition Template](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/12-quick-sort-and-partition-template.md).

## Core Rules

```text
distribute by one digit;
keep each bucket stable;
collect buckets in order;
repeat for the next digit.
```

That is the shortest reconstruction rule from the lecture.

## Canonical Java Skeleton

Code:
- [13-radix-sort-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/13-radix-sort-template.java)

The file keeps the lecture's main implementation ideas:

- linked-list buckets,
- `front[]` and `rear[]` arrays,
- least-significant-digit-first passes,
- and stable collection from bucket `0` to bucket `radix - 1`.

## Stable-Bucket Invariant

During one digit pass:

```text
within each bucket, elements stay in the same relative order
in which they entered that bucket.
```

That is why the lecture insists on linked lists and stability.
Without that property, the later passes do not preserve the work of the earlier ones.

## LSD Pass Structure

The lecture's LSD radix-sort pass is:

1. clear all bins,
2. examine the current digit of every element,
3. append each element to the end of its digit bucket,
4. collect the buckets back in increasing bucket order.

Then repeat for the next more significant digit.

## Why front and rear Both Matter

Each bucket needs:

- `front[i]` to know where the bucket starts,
- `rear[i]` to append in `O(1)` time.

If `rear` is missing, insertion at the bucket tail becomes clumsy and the stable-order idea gets much harder to code quickly.

## Pressure Checklist

1. Did I reset all buckets before each digit pass?
2. Did I compute the correct bucket from the current digit?
3. Did I append at the tail so stability is preserved?
4. Did I collect buckets in increasing bucket order?
5. Am I repeating from least significant digit upward?

## Common Mistakes

- Treating radix sort like a comparison sort.
- Forgetting that each bucket must preserve insertion order.
- Reusing bucket pointers across passes without clearing them.
- Collecting buckets in the wrong order.
- Mixing up LSD radix sort with MSD radix sort.

## Short Takeaway

For DS11, the radix-sort hand template is: distribute elements into stable linked-list buckets by the current digit, gather buckets back in order, and repeat from the least significant digit to the most significant digit.
