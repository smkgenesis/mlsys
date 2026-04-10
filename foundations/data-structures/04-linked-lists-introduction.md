# 04. Linked Lists Introduction

## What

This note introduces the linked list as the first alternative to array-based storage.

The lecture focuses on a small set of ideas:

- arrays use contiguous storage,
- linked lists can use noncontiguous blocks,
- each element becomes a node,
- each node stores data plus a link to the next node,
- and this changes the cost profile of access, insertion, and deletion.

## Why It Matters

The linked list appears immediately after arrays because it answers the weakness that arrays just exposed.

Arrays are great when:

- indexed access matters,
- and moving elements is rare.

But they are awkward when:

- elements should be inserted or removed frequently,
- or when we do not want storage to depend on one contiguous block.

So the linked list is not just "another structure."
It is a direct response to the array tradeoffs.

## Core Idea

The lecture's central move is simple:

```text
what if we store elements in noncontiguous blocks and connect them explicitly?
```

That question produces the linked list.

Each element is no longer found by arithmetic on an index.
Instead, each node explicitly points to the next node.

## From Contiguous Blocks to Linked Nodes

The lecture first reminds us of the array model:

- contiguous block in memory,
- `O(1)` access when the index is known,
- but expensive insertion and deletion in the middle.

Then it asks:

- what if we use noncontiguous blocks?
- how can we still locate all the elements?

The answer is the link itself.

## Nodes

The lecture introduces the node as the fundamental linked-list element.

Each node contains:

- `data`
- `next`

and the final node points to `null`.

The list itself is entered through a distinguished reference such as `first`.

So a linked list is not just "several nodes."
It is:

- an entry point,
- followed by nodes,
- each of which tells us where to go next.

## Empty List

The lecture also shows the empty list:

- `first`
- `null`

This matters because the empty case is part of the structure, not something outside it.

That detail will matter later when insertion and deletion rules are written more explicitly.

## Cost Profile

The lecture gives the high-level cost summary:

- `O(n)` access*
- `O(1)` insertion*
- `O(1)` deletion*

The asterisks matter because these costs depend on what location or node reference you already have.

The big contrast with arrays is the intended lesson:

- linked lists give up fast index-based access,
- but gain cheap local structural updates.

## Why Access Becomes Slower

Arrays can jump directly to an indexed position.
Linked lists cannot.

To reach a later node, we must follow links from the front one step at a time.

That is why access is linear rather than constant time.

So the linked list does not beat the array in everything.
It wins by changing what is cheap.

## What This Note Is Really Teaching

This introductory linked-list lecture is not yet about full implementation templates.
It is about the first representation shift after arrays.

The key lesson is:

```text
you can give up contiguous storage and direct indexing
to gain flexible structural updates
```

That is why linked lists are one of the first major alternative structures in most data-structures courses.

## Common Mistakes

- Thinking linked lists are just arrays drawn differently.
- Forgetting that the `next` reference is what makes the structure navigable.
- Assuming `O(1)` insertion or deletion without asking what pointer or node position is already available.
- Ignoring the empty-list case.
- Comparing linked lists and arrays using only one operation instead of the whole workload.

## Why This Matters for CS / Systems

This note matters because it introduces one of the most durable design moves in computer science:

- stop relying on one contiguous block,
- add explicit structural links,
- and accept slower access in exchange for more flexible updates.

That pattern reappears in many later structures.

## Short Takeaway

Linked lists replace contiguous storage and direct indexing with nodes connected by explicit links. That makes indexed access slower, but it makes structural updates more flexible and begins the shift from array-style representation to pointer-style representation.
