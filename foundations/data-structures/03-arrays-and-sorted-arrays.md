# 03. Arrays and Sorted Arrays

## What

This note introduces the array as the first concrete data structure in the course and then studies what changes when the array is kept sorted.

The lecture emphasizes:

- what an array is,
- why indexed access is fast,
- what fixed size means,
- how a sorted array enables binary search,
- and why faster search creates more expensive updates.

## Why It Matters

Until this point, the branch has focused on efficiency reasoning and workload tradeoffs in the abstract.
This lecture turns that reasoning into a real structure.

The array is important because it is simple enough to understand fully, but rich enough to show all of the following:

- access tradeoffs,
- update tradeoffs,
- ordering constraints,
- and the relationship between representation and algorithm choice.

## Core Idea

The lecture builds one clean story:

1. an array stores homogeneous data,
2. array indices give direct access,
3. keeping the array sorted enables binary search,
4. but sortedness must be maintained,
5. so search becomes cheaper while insertion and deletion become more expensive.

That is one of the first full tradeoff stories in data structures.

## The Array as a Data Structure

The lecture defines the array using a few core properties:

- it stores homogeneous data,
- it is indexed by consecutive integers,
- it supports `O(1)` access when the index is given,
- and its size must be specified at creation time.

Those points matter together.
An array is not just "a bunch of values."
It is a representation with a very specific access model.

## Why the Homebrew Array Matters

The `IntArray5` example is useful because it strips away the mystery.

The course shows a tiny hand-built "array" with fields like:

- `e0`,
- `e1`,
- `e2`,
- `e3`,
- `e4`,

and methods that pick the correct field based on the requested index.

This example is not presented as a good practical implementation.
It is there to make one question visible:

```text
what does it mean to access the k-th element?
```

The lecture's multiple-choice question is helpful because it shows that asymptotic questions only make sense when the data-structure model is already clear.
Once the structure is treated as an array with direct indexing, access is `O(1)` when the index is given.

## Indexing Is an Addressing Discipline

The lecture explicitly says that the index is just a way of addressing elements.

That matters because students often treat indexing as a syntactic convenience.
But the real point is structural:

- every element has a predictable position,
- and the representation makes that position directly usable.

This is the reason arrays give `O(1)` access when the index is already known.

## Contiguous Storage and Its Consequences

Later in the lecture, arrays are described as usually being implemented with a contiguous block in memory.

That representation explains the key tradeoff:

- direct index-based access is easy,
- but insertion and deletion in the middle are expensive because elements may need to be shifted.

So the array's strengths and weaknesses come from the same structural choice.

## Sorted Arrays

Once the array is sorted, search changes dramatically.

The lecture asks:

- if `a[300]` contains `5000`,
- then where could `6000` be?

That question is designed to force one insight:

```text
ordering lets us throw away part of the search space
```

That is what makes binary search possible.

## Binary Search

The lecture presents binary search for a sorted array.

Its basic mechanism is:

- keep a left boundary and a right boundary,
- inspect the middle,
- discard half of the remaining range,
- and continue until the interval collapses.

This is why binary search runs in `O(log n)` time rather than `O(n)`.

The exact code details matter less than the invariant:

- the target, if it exists, must still lie inside the current search interval.

Each iteration shrinks that interval substantially.

The lecture also uses a binary-search variant to determine insertion position, which is important because searching and maintaining order are tied together in sorted arrays.

## Sequential Search Versus Binary Search

The lecture contrasts:

- sequential search on an unsorted array,
- and binary search on a sorted array.

This gives the central comparison:

- unsorted array search: `O(n)`
- sorted array search: `O(log n)`

So sorting makes lookup much better.
But that is not the end of the story.

## Why Sorted Arrays Still Cost More to Maintain

The lecture immediately asks the right follow-up:

```text
good, but we have to maintain the array sorted
```

That leads to insertion and deletion.

Even if we can find the right position efficiently, insertion into a sorted array usually requires shifting elements to make room.
Deletion similarly requires shifting elements to close the gap.

So the lecture's summary is:

- unsorted array:
  - `O(n)` search
  - `O(1)` addition*
  - `O(1)` deletion*
- sorted array:
  - `O(log n)` search
  - `O(n)` addition
  - `O(n)` deletion

The stars matter because those constant-time update claims assume a permissive model of unsorted updates.
The broader lesson is the real one:

- sortedness improves search,
- but maintaining order makes updates expensive.

## What This Note Is Really Teaching

This lecture is not only teaching arrays.
It is teaching one of the first durable design principles in data structures:

```text
the property that helps one operation often hurts another
```

Arrays are the first place where this becomes very concrete:

- direct indexing helps access,
- sortedness helps search,
- but shifting hurts updates.

## Common Mistakes

- Treating an array as just a language feature rather than as a data-structure representation with specific guarantees.
- Forgetting that `O(1)` access assumes the index is already known.
- Thinking sorted arrays are always better because binary search is faster.
- Ignoring the cost of maintaining sorted order during insertion and deletion.
- Memorizing binary search as code without understanding the shrinking-interval invariant.
- Forgetting that fixed size is part of the array model used in the lecture.

## Why This Matters for CS / Systems

This note matters because it ties together representation and workload very directly.

The same collection of values can support:

- cheap random access,
- cheap search,
- or cheap updates,

but not always all at once.

That tradeoff mindset is what later structures will keep refining.

## Short Takeaway

Arrays are the first concrete data structure in the course: they store homogeneous data, support `O(1)` indexed access, and usually rely on contiguous storage. Keeping an array sorted enables `O(log n)` binary search, but maintaining that order makes insertion and deletion expensive.
