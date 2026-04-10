# 04. Singly Linked List Basics Template

## What

This template captures the first linked-list coding skeletons from DS04:

- `Node`
- `LinkedList`
- `first` and `last`
- `isEmpty`
- `insertAtFront`
- `deleteFirst`
- `insertAfter`
- `insertAtEnd`
- and traversal with `displayAll`

It is based on [04. Linked Lists Introduction](/Users/minkyu/Documents/mlsys/foundations/data-structures/04-linked-lists-introduction.md).

## Core Invariants

```text
first is the entry point of the list.
If the list is empty, first == null and last == null.
If the list is nonempty, last points to the final node.
The final node always has next == null.
```

If these four facts stay true after every mutation, the implementation is usually still correct.

## Canonical Java Skeleton

Code:
- [04-singly-linked-list-basics-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/04-singly-linked-list-basics-template.java)

The key point is not memorizing every line.
It is knowing which references must change, and in what order.

## Operation Patterns

### Insert at front

Repair order:

1. allocate node
2. set its data
3. point `newNode.next` to old `first`
4. move `first` to `newNode`
5. if list was empty, also set `last = newNode`

### Delete first

Repair order:

1. fail immediately if empty
2. save `first.data`
3. move `first` to `first.next`
4. if list became empty, set `last = null`
5. return saved value

### Insert after a known node

Repair order:

1. fail immediately if target node is `null`
2. allocate node
3. set `newNode.next = n.next`
4. set `n.next = newNode`
5. if inserted after old last node, update `last`

### Insert at end

Repair order:

1. allocate node
2. set `newNode.next = null`
3. if empty, set both `first` and `last`
4. otherwise link `last.next`
5. advance `last`

## Why InsertBefore Is Not Symmetric

In a singly linked list, `insertAfter` is local because the target node is already known.

`insertBefore` is harder because we do not have a direct link to the previous node.

That asymmetry is one of the main conceptual points of DS04.

## Pressure Checklist

1. Did I preserve `first` as the entry point?
2. If the list is empty, did I keep `first == null` and `last == null` together?
3. If I changed the first node, did I re-check the empty-list case?
4. If I inserted or deleted at the tail, did I update `last`?
5. Does the final node still have `next == null`?

## Common Mistakes

- Forgetting to update `last` when the list changes from empty to nonempty.
- Forgetting to reset `last` when deleting the only node.
- Treating `insertAfter` as if it also solves `insertBefore`.
- Traversing without starting from `first`.
- Updating references in the wrong order and losing part of the list.

## Short Takeaway

For DS04, the main linked-list hand-coding template is invariant repair. `first` is the entry point, `last` tracks the tail when present, and each mutation is a small sequence of pointer updates whose only job is to preserve the list invariants.
