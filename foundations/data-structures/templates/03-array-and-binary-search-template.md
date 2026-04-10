# 03. Array and Binary Search Template

## What

This template captures the first real data-structure hand-coding material from DS03:

- the array as a fixed-size indexed structure,
- the "homebrew array" idea,
- binary search on a sorted array,
- and the binary-search variant that returns an insertion position.

It is based on [03. Arrays and Sorted Arrays](/Users/minkyu/Documents/mlsys/foundations/data-structures/03-arrays-and-sorted-arrays.md).

## Core Rules

```text
array access is O(1) when the index is given;
sortedness enables binary search;
binary search works only because the candidate interval keeps shrinking.
```

For DS03, that shrinking-interval invariant is the main thing to remember under pressure.

## Minimal Fixed-Array Skeleton

```java
class IntArray {
    private int[] a;

    public IntArray(int n) {
        a = new int[n];
    }

    public void setValue(int idx, int val) {
        a[idx] = val;
    }

    public int getValue(int idx) {
        return a[idx];
    }
}
```

This is the practical form of the lecture's array model:

- homogeneous data,
- consecutive indices,
- fixed size decided at creation time.

## Why the Homebrew Array Matters

The lecture's `IntArray5` example is not a template to memorize literally.
It is there to teach the representation idea:

- an array exposes indexed access,
- and the client should think in terms of `idx -> element`.

The real exam-facing template is the fixed-array skeleton above, together with the structural facts:

- index range is `0 ... n-1`,
- access is `O(1)` when index is already known,
- size is fixed once created.

## Canonical Binary Search Template

```java
int left = 0;
int right = n - 1;
boolean found = false;
int foundPos = -1;

while (left < right) {
    int mid = (left + right) / 2;
    if (a[mid] < x) {
        left = mid + 1;
    } else {
        right = mid;
    }
}

if ((left == right) && (a[left] == x)) {
    found = true;
    foundPos = left;
}
```

This is the lecture's core binary-search shape:

- maintain `left` and `right`,
- test `mid`,
- discard half the search interval,
- then do a final equality check.

## Binary Search Invariant

The key reasoning template is:

```text
if x exists in the array,
then it must still be inside a[left ... right]
```

Every update to `left` or `right` must preserve that claim.

That is the main thing to explain if the exam asks "why does this work?"

## Canonical Insertion-Position Template

The lecture also gives the variant that distinguishes:

- found position,
- or insertion position when the target is absent.

```java
int left = 0;
int right = n - 1;
boolean found;
int foundPos = -1;
int insertPos;

while (left < right) {
    int mid = (left + right) / 2;
    if (a[mid] < x) {
        left = mid + 1;
    } else {
        right = mid;
    }
}

if (left == right) {
    if (a[left] == x) {
        found = true;
        foundPos = left;
        insertPos = left;
    } else {
        found = false;
        if (a[left] < x) {
            insertPos = left + 1;
        } else {
            insertPos = left;
        }
    }
} else {
    found = false;
    insertPos = left;
}
```

This is the version to remember when the question is not just "is `x` present?" but:

- where should `x` go if we want to keep the array sorted?

## Sequential Search Reminder

For an unsorted array, the fallback template is still:

```java
boolean found = false;
int foundPos = -1;

for (int i = 0; i < n; i++) {
    if (a[i] == x) {
        found = true;
        foundPos = i;
        break;
    }
}
```

This is the comparison point for DS03:

- unsorted array search: `O(n)`
- sorted array binary search: `O(log n)`

## Pressure Checklist

1. Is the array sorted? If not, do not use binary search.
2. Did I initialize `left = 0` and `right = n - 1`?
3. Does each loop step shrink the candidate interval?
4. Am I preserving the "if present, x is still inside `[left, right]`" invariant?
5. Did I handle the final equality test after the loop?
6. If the question asks for insertion position, did I distinguish `foundPos` from `insertPos`?

## Common Mistakes

- Using binary search on an unsorted array.
- Memorizing the code but not the shrinking-interval invariant.
- Forgetting the final equality check after the loop.
- Updating `left` or `right` in a way that does not shrink the interval.
- Confusing "found position" with "insertion position."
- Forgetting that sorted arrays gain faster search but pay more to maintain order.

## Short Takeaway

For DS03, the first true structure template is the fixed array plus binary search. Arrays give `O(1)` indexed access, and once the array is sorted, binary search works by maintaining a shrinking candidate interval until only one possible position remains.
