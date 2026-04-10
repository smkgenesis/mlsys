# 06. Circular Linked Lists

## What

This note records the lecture's next structural variation on linked lists:

- circular linked lists,
- header nodes,
- and iterators adapted to circular structure.

At the end of the lecture, it also briefly positions:

- doubly linked lists,
- and when singly linked lists are still the better choice.

The motivation is simple:

```text
sometimes there is no inherent first and last item
```

The lecture uses round-robin scheduling as the guiding example.

## Why It Matters

Up to this point, the branch has treated a linked list as a structure with:

- a front,
- an end marked by `null`,
- and special empty-list cases built around `first` and `last`.

Circular linked lists change that picture.

They matter because they show that:

- list structure is not fixed,
- "end of list" is a design choice,
- and introducing a header node can simplify update logic by removing special cases.

## Core Idea

A circular linked list replaces the terminal `null` link with a loop.

Instead of:

```text
... -> last -> null
```

we have:

```text
... -> node -> node -> ... -> back again
```

The lecture then introduces a header node to make this structure easier to manage.

## Why Circularity Can Help

The lecture's motivation is not just visual.
It points to situations where no item is naturally "first" or "last," such as:

- round-robin scheduling

That is the right mental model.

If the workload naturally cycles, then a cyclic structure can match it more directly than a linear structure ending in `null`.

## Why the Code Can Become Simpler

The lecture explicitly says:

- the code can become simpler
- header node

That is the key design move here.

Instead of having many methods reason about:

- empty list,
- first node,
- last node,
- and `null`,

the header node absorbs much of that edge-case logic.

## Header Node Representation

In the lecture's implementation:

- the list owns a single `header` node,
- `header.next` points to the first real node when the list is nonempty,
- and in the empty case `header.next == header`.

So the header is a permanent structural anchor.
It is not real user data.
It is there to stabilize the representation.

## Empty Circular List

The lecture's empty-list invariant is:

```text
header.next == header
```

This is one of the cleanest representation choices in the branch so far.

Instead of:

- `first == null`

the structure always has a node.
The question is just whether the header points back to itself.

That is why `isEmpty()` becomes especially simple.

## CircularLinkedList Class

The lecture's class shape is:

- one private final `header` field,
- a constructor that creates the header node,
- and initialization with `header.next = header`.

This representation says:

- there is always a stable entry object,
- and every traversal decision is made relative to that header.

## Circular Iterator State

The lecture's `CircularIterator` stores:

- `header`
- `prev`
- `cur`

and initializes them as:

- `prev = header`
- `cur = header.next`

This mirrors the singly linked deletion iterator, but the header now replaces both:

- the old `null` boundary,
- and much of the first-node special casing.

## atEnd in a Circular List

In the circular iterator, the stopping condition is no longer:

- `cur == null`

It becomes:

- `cur == header`

That is a very important conceptual shift.

The header acts as the sentinel that marks traversal completion.

## getData and next

The remaining traversal operations keep the same overall roles:

- `getData()` reads the current node's data,
- `next()` advances by updating `prev` and `cur`.

But their meaning is now defined relative to the header sentinel rather than `null`.

That is the real structural difference.

## deleteCurrent

The circular iterator's `deleteCurrent()` also becomes cleaner.

The lecture's case split is:

1. if `cur == header`, there is no real current node to delete,
2. otherwise save the current data,
3. bypass the current node with `prev.next = cur.next`,
4. move `cur` forward,
5. return the saved value.

Notice what disappeared:

- no `first == null` case,
- no explicit `last = null` repair,
- no special branch for deleting the first node.

That is exactly the simplification the header node was meant to buy.

## insertAfter

The circular iterator's `insertAfter(int x)` also becomes a straightforward local rewiring operation:

1. create a new node,
2. point it to `cur.next`,
3. set `cur.next` to the new node.

Again, the header-based circular representation absorbs much of the boundary complexity.

## What This Note Is Really Teaching

This lecture is not just introducing another list variant.
It is teaching a deeper design lesson:

```text
we can simplify algorithms by changing the representation
```

The header node is not there because users asked for one.
It is there because it reduces special-case logic in the implementation.

That is a very important data-structures idea.

## Doubly Linked Lists as the Next Contrast

The lecture closes by introducing doubly linked lists through one simple motivation:

- we may want to traverse backwards

That adds a `prev` link to each node.

The course does not fully develop the implementation here.
Instead, it uses doubly linked lists as a contrast point:

- singly linked lists are lighter,
- doubly linked lists are more symmetric,
- and richer structure should be justified by actual needs.

## When a Singly Linked List Is Still Better

The lecture ends with a practical comparison question:

```text
when would you use a singly linked list instead of a doubly linked one?
```

The listed answers are straightforward:

- when traversal is typically one-directional,
- when we want less memory overhead,
- and when the extra backward link is not worth the additional structural cost.

This is a useful closing reminder for the linked-list sequence:

- more structure can do more,
- but it also costs more.

## Common Mistakes

- Thinking circularity is only a visual change rather than a representation change.
- Forgetting that the header node is not ordinary data.
- Using `null` as the traversal boundary in a header-based circular list.
- Forgetting that `atEnd()` is now `cur == header`.
- Forgetting to initialize the empty structure with `header.next = header`.
- Treating round-robin or cyclic workloads as if they naturally require a linear null-terminated list.
- Assuming doubly linked lists are always the best default.

## Why This Matters for CS / Systems

This note matters because it sharpens a core data-structures instinct:

- choose the representation that makes the common operations simple,
- even if that means adding an extra structural object like a header node.

Circular linked lists and sentinels show how much implementation complexity can be removed by a smarter invariant. The lecture's brief doubly linked list comparison adds the balancing lesson that extra structural power should still be paid for only when the workload truly needs it.

## Short Takeaway

Circular linked lists remove the terminal `null` and loop the structure back on itself, which fits cyclic workloads such as round-robin scheduling. With a header node, the representation becomes even cleaner: the empty list is `header.next == header`, traversal ends when `cur == header`, and insertion and deletion logic lose many of the first-node and empty-list special cases that complicated singly linked lists. The lecture then closes by contrasting this with doubly linked lists, reinforcing that added structure should be justified by actual traversal needs.
