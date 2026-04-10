# 05. Linked List Iterator Template

## What

This template captures the iterator layer from DS04:

- traversal with `cur`
- list-aware iterator construction
- `atEnd`
- `getData`
- `next`
- iterator-based `insertAfter`
- and deletion with `prev` and `cur`

It is based on [05. Linked List Iterators](/Users/minkyu/Documents/mlsys/foundations/data-structures/05-linked-list-iterators.md).

## Core Invariants

Traversal iterator:

```text
cur is the current node.
atEnd() is exactly cur == null.
```

Deletion-capable iterator:

```text
prev is the node immediately before cur,
except when cur is the first node, where prev == null.
```

That `prev/cur` relationship is the whole reason deletion works.

## Canonical Java Skeleton

Code:
- [05-linked-list-iterator-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/05-linked-list-iterator-template.java)

The iterator stores:

- `prev`
- `cur`
- `llist`

The owning list reference matters because iterator mutation still has to preserve list-level invariants such as `first` and `last`.

## Traversal Pattern

The lecture's usage pattern is:

```java
Iterator i;

for (i = list.getIterator(); !i.atEnd(); i.next()) {
    System.out.println(i.getData());
}
```

That is the linked-list replacement for array-style index loops.

## next() Repair Rule

When the iterator supports deletion, `next()` must update in this order:

1. `prev = cur`
2. `cur = cur.next`

If you move `cur` first, you lose the old current node and break the iterator invariant.

## Iterator-Based InsertAfter

The lecture's cleaner version is:

```text
iterator owns position;
list owns structural mutation
```

So `insertAfter(int x)` on the iterator delegates to:

- `llist.insertAfter(cur, x)`

That keeps pointer-repair logic centralized in the list class.

## DeleteCurrent Case Split

`deleteCurrent()` works by reconnecting around `cur`.

Repair order:

1. fail immediately if `cur == null`
2. save `cur.data`
3. move `cur` forward
4. if `prev == null`, update `llist.first`
5. otherwise set `prev.next = cur`
6. if `cur == null`, update `llist.last = prev`
7. return saved value

This is the most important DS04 pointer-repair template.

## Why `prev` Is Necessary

In a singly linked list, deleting the current node is not local if you only know `cur`.

You also need the node before it so that:

```text
previous node -> current node -> next node
```

can become:

```text
previous node -> next node
```

That is why the iterator evolves from only `cur` to both `prev` and `cur`.

## Pressure Checklist

1. Does `atEnd()` mean exactly `cur == null`?
2. In `next()`, did I update `prev` before `cur`?
3. In `deleteCurrent()`, did I distinguish the first-node case with `prev == null`?
4. If deletion removes the last node, did I update `llist.last`?
5. Am I delegating list-structure mutation to the list when appropriate?

## Common Mistakes

- Treating a raw node reference as if it were a full iterator abstraction.
- Updating `cur` before `prev` in `next()`.
- Forgetting the `prev == null` case in deletion.
- Forgetting to update `last` when deleting the final node.
- Writing iterator mutation code that silently breaks list invariants.

## Short Takeaway

For DS04, the iterator template is really a state template: `cur` supports traversal, and `prev + cur` supports deletion. The reason it works is simple but strict: the iterator must always know the current node and, when deletion is allowed, the node immediately before it.
