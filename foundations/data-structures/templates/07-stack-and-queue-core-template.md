# 07. Stack and Queue Core Template

## What

This template captures the first stack/queue implementations from DS06:

- queue as FIFO
- stack as LIFO
- queue implemented with a linked list
- stack implemented with a linked list
- stack implemented with an array
- and matching parentheses as the first canonical stack use

It is based on [07. Stacks and Queues](/Users/minkyu/Documents/mlsys/foundations/data-structures/07-stacks-and-queues.md).

## Core ADT Rules

```text
queue = enqueue at rear, dequeue at front
stack = push at top, pop from top
```

The main exam trap here is mixing up the access discipline with the underlying implementation.

## Canonical Java Skeleton

Code:
- [07-stack-and-queue-core-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/07-stack-and-queue-core-template.java)

This file gives the three basic implementation forms used in the lecture:

- linked-list queue
- linked-list stack
- array stack

## Queue with a Linked List

The lecture's queue wrapper is conceptually simple:

- `enqueue(x)` -> `insertAtEnd(x)`
- `dequeue()` -> `deleteFirst()`
- `isEmpty()` -> `llist.isEmpty()`

That works because a queue needs:

- rear insertion
- front deletion

which matches the linked list well.

## Stack with a Linked List

The linked-list stack uses:

- `push(x)` -> `insertAtFront(x)`
- `pop()` -> `deleteFirst()`

This works because a stack only needs one exposed end.

The front of the linked list becomes the top of the stack.

## Stack with an Array

The array-stack invariant is the most important part:

```text
top == -1 means empty
top is the index of the current top element otherwise
```

From that invariant:

- `isEmpty()` checks `top == -1`
- `isFull()` checks `top == size - 1`
- `push(x)` does `data[++top] = x`
- `pop()` returns `data[top--]`

## Why the Array Stack Is Easy

The lecture's point is that stacks only mutate one end.

So unlike a general array structure:

- no middle shifting is needed
- the active region stays contiguous
- and `top` is enough to describe the state

## Matching Parentheses Template

The lecture's first algorithmic use of stacks is:

- push opening delimiters
- when a closing delimiter arrives, check whether the matching opener is on top
- fail early if not
- succeed only if the stack is empty at the end

Minimal shape:

```java
for (int i = 0; i < s.length(); i++) {
    char c = s.charAt(i);
    if (c == '(' || c == '{') {
        st.push(c);
    } else if (c == ')') {
        if (st.isEmpty() || st.pop() != '(') return false;
    } else if (c == '}') {
        if (st.isEmpty() || st.pop() != '{') return false;
    }
}
return st.isEmpty();
```

The real invariant is:

```text
the stack contains the unmatched opening delimiters seen so far
```

## Pressure Checklist

1. Did I keep FIFO and LIFO separate in my head?
2. For the linked-list queue, am I inserting at end and deleting at front?
3. For the linked-list stack, am I using only the front as the top?
4. For the array stack, did I keep `top == -1` as the empty invariant?
5. In parentheses matching, does the stack represent unmatched open delimiters?

## Common Mistakes

- Reversing queue and stack operation order.
- Accidentally implementing queue behavior with stack operations.
- Forgetting the `top == -1` empty invariant in the array stack.
- Using array-stack code but updating `top` in the wrong order.
- Forgetting the final `st.isEmpty()` check in parentheses matching.

## Short Takeaway

For DS06, the first stack/queue template is about access discipline. A queue maps naturally to linked-list rear insertion plus front deletion, and a stack maps naturally to one exposed end, either as a linked-list front or an array top tracked by `top`.
