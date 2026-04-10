# 04. Linked Lists Introduction

## What

This note extends the first linked-list introduction into a basic implementation note.

The lecture now makes the structure concrete through:

- a `Node` class with `data` and `next`,
- a `LinkedList` class with a `first` reference,
- the empty-list case,
- traversal,
- insertion at the front,
- deletion at the front,
- insertion after a node,
- and the use of a `last` reference to support insertion at the end.

## Why It Matters

The previous array note taught one core lesson:

```text
contiguous indexed storage gives fast access but expensive middle updates
```

This lecture answers that tradeoff with the first pointer-based structure that we actually manipulate.

It matters because the course is no longer just comparing access costs in the abstract.
We now have to maintain the structure ourselves:

- which reference identifies the list,
- how empty lists are represented,
- how links change during insertion and deletion,
- and which edge cases break the structure if we forget them.

## Core Idea

A linked list is entered through a distinguished reference, usually `first`.

Each node stores:

- one data value,
- and one link to the next node.

So the list is not addressed by index arithmetic.
It is followed step by step:

```text
first -> node -> node -> ... -> null
```

That representation gives up direct indexed access, but makes local structural edits simple once the right node is known.

## From Arrays to Linked Lists

The lecture explicitly contrasts the two structures.

Arrays:

- use a contiguous memory block,
- give `O(1)` access when the index is known,
- but have `O(n)` insertion and deletion,
- and require the size to be fixed at creation.

Linked lists instead ask:

```text
what if we use noncontiguous blocks and connect them explicitly?
```

That question produces nodes and links.

## Node and List Structure

The lecture's Java shape is simple:

- `Node` has `data` and `next`,
- `LinkedList` has `first`,
- and the constructor initializes `first = null`.

That already tells us the whole representation:

- `first` is the entry point,
- every node links to its successor,
- and `null` marks the end.

## Empty List

The empty-list case is not a special external state.
It is part of the representation:

- `first = null`

When a `last` reference is introduced later in the lecture, the empty list becomes:

- `first = null`
- `last = null`

That edge case controls several later updates, so it has to be treated as a first-class invariant.

## Basic Cost Profile

The lecture keeps the same high-level complexity summary:

- `O(n)` access*
- `O(1)` insertion*
- `O(1)` deletion*

The asterisks still matter.
These costs assume we already have the relevant node or position reference.

So linked lists do not make all operations fast.
They change which operations are naturally cheap.

## isEmpty

The first implementation-level method in the lecture is:

- `isEmpty()`

Its logic is just:

```text
the list is empty exactly when first == null
```

That method matters because later operations repeatedly branch on that same structural fact.

## Insert at the Front

The lecture's `InsertAtFront(int x)` captures the basic prepend pattern:

1. create a new node,
2. store `x` in it,
3. point its `next` to the current `first`,
4. move `first` to the new node.

This is the first clear example of why front insertion is cheap in a singly linked list.
Only a constant number of references change.

When the lecture later adds a `last` reference, it also fixes the empty-list edge case:

- if the list was empty before insertion, then `last` must also point to the new node.

## Delete the First Node

`DeleteFirst()` shows the symmetric front-removal pattern:

1. if the list is empty, fail immediately,
2. save the first node's data,
3. move `first` to `first.next`,
4. return the saved data.

Once `last` is part of the representation, one more edge case appears:

- if deleting the first node makes the list empty, then `last` must be set back to `null`.

This is one of the clearest examples in the lecture of how one extra field creates one extra invariant that must be maintained.

## Traversal with DisplayAll

The `DisplayAll()` method introduces the traversal pattern:

- start from `first`,
- keep a cursor such as `cur`,
- repeat until `cur == null`,
- then move with `cur = cur.next`.

This is the fundamental linked-list access discipline.

The lecture uses it for printing, but the real lesson is broader:

```text
if you want to inspect many nodes in a linked list,
you walk the links one by one
```

That is why linked-list access is linear.

## InsertAfter

The lecture then gives `InsertAfter(Node n, int x)`.

Its structure is:

1. reject `null`,
2. create a new node,
3. set the new node's `next` to `n.next`,
4. set `n.next` to the new node.

This is an important moment in the course because it makes the starred complexity claims concrete.

Insertion is `O(1)` here because the target node `n` is already known.
The list does not need to be searched first.

When `last` is present, one more condition must be checked:

- if the new node was inserted after the old last node, then `last` must move to the new node.

## Why InsertBefore Is Harder

The lecture immediately asks:

```text
InsertBefore() ?
```

That question is excellent because it exposes the directional nature of a singly linked list.

With only `next` links, we do not have a direct pointer to the previous node.
So inserting before a given node is not locally available in the same way that inserting after it is.

That is one of the first places where the structure's asymmetry becomes visible.

## Adding a last Reference

The lecture then introduces `last`.

That addition changes the representation from:

- `first`

to:

- `first`
- `last`

The motivation is practical:

```text
sometimes it is useful to insert at the end
```

Without `last`, appending requires traversal.
With `last`, the append operation can update the end directly.

## InsertAtEnd

The lecture shows two versions of `InsertAtEnd(int x)`.

The final correct version handles both cases:

- if the list is empty, both `first` and `last` become the new node,
- otherwise, `last.next` points to the new node and `last` advances to it.

This is the lecture's clearest example of a useful extra pointer:

- it makes a particular operation cheaper,
- but now every mutating method must preserve one more invariant.

## Structural Invariants to Remember

From this lecture, the key linked-list invariants are:

- the list starts at `first`,
- the final node has `next = null`,
- the empty list has `first = null`,
- if a `last` field is present, then the empty list must also have `last = null`,
- and if the list is nonempty, `last` must point to the final node.

These are the facts to reconstruct under pressure before writing code.

## Common Mistakes

- Treating `first` as optional rather than as the entry point of the whole structure.
- Forgetting that traversal must follow links one node at a time.
- Claiming `O(1)` insertion or deletion without stating what node reference is already known.
- Forgetting the empty-list case in `InsertAtFront`, `DeleteFirst`, or `InsertAtEnd`.
- Adding a `last` pointer without updating it consistently after mutations.
- Assuming `InsertBefore` is just as easy as `InsertAfter` in a singly linked list.

## Why This Matters for CS / Systems

This note matters because it is the first place where representation invariants and code structure fully meet.

The linked list is simple enough to hold in your head, but rich enough to force careful reasoning about:

- ownership of entry references,
- empty-structure behavior,
- mutation order,
- and the cost difference between local edits and global traversal.

That is the mindset later structures will keep building on.

## Short Takeaway

This lecture turns the linked list from a conceptual alternative to arrays into a real implemented structure. A singly linked list is entered through `first`, traversed through `next`, updated locally through pointer rewiring, and optionally extended with `last` to make append efficient. The price of that flexibility is that correctness now depends on maintaining explicit structural invariants.
