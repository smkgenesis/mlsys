# 05. Linked List Iterators

## What

This note captures the second half of the lecture, where the course asks an important follow-up question:

```text
if arrays have indices, what is the linked-list equivalent?
```

The lecture's answer is not "a pointer/reference" in the informal sense.
It is an iterator abstraction.

## Why It Matters

Arrays are easy to use in nested loops because indexed access is built into the representation.

A typical array pattern looks like:

- `a[i]`
- `a[j]`

That style does not transfer naturally to linked lists because a linked list does not support direct positional access.

So once we start using linked lists seriously, we need a disciplined way to move through the structure without pretending it has array indices.

## Core Idea

The lecture makes one clean distinction:

- a raw node reference is not the right conceptual replacement for an array index,
- an iterator is.

An iterator packages the current traversal position and gives controlled operations for:

- checking whether traversal is finished,
- reading the current data,
- and moving forward.

## Why a Node Reference Is Not the Same as an Index

The lecture explicitly asks:

```text
I want an "index" to a linked list
```

and then rejects:

- `Pointer/Reference? No!`

This is an important design point.

A raw node reference is just one implementation detail.
It does not by itself give a clean usage protocol.

The iterator is the course's way of turning:

- "here is some node pointer"

into:

- "here is how a user of the list moves through it safely and systematically."

## The Iterator Usage Pattern

The lecture shows a usage pattern like:

1. get an iterator from the list,
2. continue while it is not at the end,
3. inspect the current data,
4. advance to the next position.

That is the linked-list counterpart of a loop over array indices.

The important shift is:

- arrays move by index arithmetic,
- linked lists move by following links through an iterator state.

## Iterator State

The lecture's iterator class stores one field:

- `cur`

where `cur` is the current node.

The constructor receives the list's `first` node and initializes the iterator there.

So the iterator's entire job is to maintain one moving view of the traversal position.

## getIterator

The list exposes traversal by returning a fresh iterator:

- `getIterator()`

That method matters because it keeps traversal as a list-level service rather than forcing client code to reach directly into node links.

This is a small but important interface step.

## atEnd

The iterator method `atEnd()` expresses the traversal stopping condition:

- we are at the end exactly when `cur == null`

This gives a reusable way to talk about traversal state without repeatedly exposing the raw representation.

## getData

`getData()` returns the current node's data.

The lecture also shows that trying to read data at the end is an error case.
So the operation has a precondition:

- only call it when the iterator is not at the end.

That is the same kind of boundary reasoning we already saw with empty linked lists.

## next

`next()` advances the iterator by doing:

- `cur = cur.next`

when the iterator is not already at the end.

This is the fundamental traversal move of a singly linked list packaged into a method.

## Iterator-Based InsertAfter

The lecture then revisits insertion with:

- `InsertAfter(int x)` on the iterator

This is conceptually important.

Earlier, insertion after a known node was easy because the node reference was already known.
Now the iterator becomes the object that carries that current position.

So the course is showing that an iterator is not just for reading.
It can also serve as the handle for local structural updates.

## The last Problem Appears Again

After showing iterator-based `InsertAfter`, the lecture immediately asks:

- `last?`

That is the right follow-up.

If the iterator inserts after the current final node, then the list's `last` pointer must also be updated.

So the iterator abstraction does not erase structural invariants.
It still has to cooperate with them.

## What This Note Is Really Teaching

This lecture is not mainly about Java syntax.
It is teaching a change in access model.

Arrays teach:

- locate by index

Linked lists teach:

- locate by traversal state

The iterator is the lecture's abstraction for that traversal state.

## Common Mistakes

- Treating a linked list as if it had natural integer indexing like an array.
- Assuming a raw node reference is already the best public traversal abstraction.
- Forgetting that `getData()` only makes sense when the iterator is not at the end.
- Forgetting to advance the iterator in traversal loops.
- Adding iterator-based insertion without thinking about how it interacts with the list's `last` invariant.

## Why This Matters for CS / Systems

This note matters because it is one of the first places where the course separates:

- representation,
- interface,
- and usage protocol.

The linked list is still implemented with nodes and references, but the iterator gives client code a cleaner way to move through the structure.
That distinction becomes more important as data structures grow more complex.

## Short Takeaway

The lecture's answer to "what is the linked-list version of an array index?" is the iterator. An iterator stores the current traversal position, exposes operations like `atEnd`, `getData`, and `next`, and can also support local updates such as `InsertAfter`. It is the course's first explicit abstraction for separating traversal use from raw structural representation.
