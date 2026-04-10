# 06. Circular Linked List Template

## What

This template captures the new representation introduced in DS05:

- circular linked lists,
- a permanent header node,
- circular traversal,
- `atEnd()` as `cur == header`,
- `deleteCurrent()`,
- and `insertAfter()` in the circular setting.

It is based on [06. Circular Linked Lists](/Users/minkyu/Documents/mlsys/foundations/data-structures/06-circular-linked-lists.md).

## Core Invariants

```text
the list always has a header node;
the empty-list condition is header.next == header;
the traversal-end condition is cur == header.
```

That is the entire mental shift from the null-terminated linked lists in DS04.

## Canonical Java Skeleton

Code:
- [06-circular-linked-list-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/06-circular-linked-list-template.java)

The important point is not the exact class names.
It is the new boundary model:

- no terminal `null`
- header node as sentinel
- traversal and mutation relative to that sentinel

## Why the Header Node Matters

The header is not real user data.
It exists to simplify the implementation.

The main benefits are:

- empty list has a uniform representation,
- traversal has a fixed sentinel,
- deletion no longer needs a special "first node" branch in the same way,
- and many edge cases become local rewiring instead of null-case reasoning.

## Initialization Template

Repair rule:

1. allocate header
2. set `header.next = header`

That alone establishes the empty circular-list invariant.

## Traversal Template

The circular traversal pattern is:

```java
CircularIterator it = list.getIterator();
while (!it.atEnd()) {
    System.out.println(it.getData());
    it.next();
}
```

This is the circular equivalent of null-terminated traversal.

## Circular Iterator State

The lecture's iterator stores:

- `header`
- `prev`
- `cur`

with initialization:

- `prev = header`
- `cur = header.next`

That is the base state to reconstruct under pressure.

## deleteCurrent Repair Rule

Repair order:

1. fail if `cur == header`
2. save current data
3. bypass current node with `prev.next = cur.next`
4. move `cur` forward
5. return saved value

The key simplification is that the header sentinel absorbs much of the old null-boundary logic.

## insertAfter Repair Rule

Repair order:

1. allocate node
2. set its data
3. set `newNode.next = cur.next`
4. set `cur.next = newNode`

This is local rewiring again, just in a circular representation.

## What Changed from the DS04 Iterator

The most important shift is conceptual:

- before, end-of-traversal meant `cur == null`
- now, end-of-traversal means `cur == header`

That is why this needs its own template rather than just a small patch on the earlier linked-list iterator.

## Pressure Checklist

1. Did I remember that the header is always present?
2. Is the empty-list invariant `header.next == header`?
3. Does `atEnd()` check `cur == header`, not `cur == null`?
4. In deletion, am I bypassing `cur` through `prev.next`?
5. Am I treating the header as sentinel structure rather than real data?

## Common Mistakes

- Using `null` as the circular-list end condition.
- Forgetting to initialize `header.next = header`.
- Treating the header node as ordinary data.
- Forgetting that traversal starts from `header.next`.
- Copying singly linked list code without changing the end-of-list invariant.

## Short Takeaway

For DS05, the new hand-coding template is not "another linked list" but a new boundary model. A circular linked list with a header node replaces `null` with a sentinel, so emptiness, traversal, and mutation are all expressed relative to `header`.
