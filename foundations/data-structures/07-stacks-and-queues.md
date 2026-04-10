# 07. Stacks and Queues

## What

This note records the branch's move from list representations to two core abstract data types:

- queues
- stacks

The lecture introduces:

- FIFO versus LIFO behavior,
- queues and stacks as algorithmic building blocks,
- linked-list implementations,
- array implementations,
- the need for a circular queue when a plain array queue wastes space,
- and a concrete stack application through matching parentheses.

## Why It Matters

This is the first point in the branch where the course clearly separates:

- a data structure's behavior,
- from the representation used to implement it.

A queue is not "a linked list."
A stack is not "an array."

Instead:

- queue means FIFO access discipline,
- stack means LIFO access discipline,
- and we can realize those disciplines with different underlying structures.

That shift is important because later data-structure work depends on keeping interface and implementation conceptually separate.

## Core Idea

The lecture's central lesson is:

```text
stacks and queues are usage disciplines first,
and implementation choices second
```

So the interesting questions become:

- what operations define the ADT,
- and which representation makes those operations simple and efficient?

## Queues

The lecture defines a queue as:

- homogeneous items forming a line,
- with first-in, first-out behavior.

The basic operations are:

- `enqueue`: add at the rear,
- `dequeue`: remove from the front.

The lecture also frames queues as:

- a real-world model such as print jobs,
- and an algorithmic building block.

## Implementing a Queue with a Linked List

The first queue implementation wraps a linked list.

The lecture uses:

- `insertAtEnd(x)` for `enqueue`,
- `deleteFirst()` for `dequeue`,
- `isEmpty()` for emptiness checking.

This aligns the queue discipline with the linked-list strengths:

- rear insertion,
- front deletion,
- no shifting of elements.

It is one of the branch's clearest examples of using one structure to implement another abstraction.

## Stacks

The lecture defines a stack as:

- homogeneous items forming a stack with one end open,
- and last-in, first-out behavior.

The basic operations are:

- `push`: add at the top,
- `pop`: remove from the top.

The lecture again presents the stack as:

- an intuitive physical metaphor,
- and an algorithmic building block.

It also explicitly mentions the call stack.

## The Call Stack as Motivation

The lecture includes a nested Java method-call example to show that stacks are not just textbook abstractions.

The point is:

- function calls create nested execution states,
- those states must be resumed in reverse order,
- and that is exactly LIFO behavior.

So the call stack is one of the most important concrete reasons stacks matter.

## Implementing a Stack with a Linked List

The linked-list implementation of a stack is especially simple:

- `push(x)` uses `insertAtFront(x)`,
- `pop()` uses `deleteFirst()`,
- `isEmpty()` delegates to the list.

This works well because a stack only needs one exposed end.
The front of the linked list naturally becomes the top of the stack.

## Implementing a Stack with an Array

The lecture then asks:

```text
what if we knew the maximum size in advance?
```

That is the array-stack setup.

The representation is:

- an array `data`,
- a fixed `size`,
- and an integer `top`.

The key invariant is:

- `top == -1` means empty,
- otherwise `top` marks the current top element.

From that, the operations become:

- `isEmpty()`: `top == -1`
- `isFull()`: `top == size - 1`
- `push(x)`: increment `top`, then write
- `pop()`: read, then decrement `top`

## Why Array Stacks Work Well

For stacks, arrays are a natural fit when a maximum size is known.

Why:

- access is only needed at one end,
- no middle insertion or deletion is required,
- and the active region stays contiguous from index `0` through `top`.

So unlike general array updates, stack operations do not require shifting elements.

## Implementing a Queue with an Array

The lecture then asks the natural next question:

```text
can we implement a queue with an array?
```

Yes, but the first version reveals a problem.

The representation uses:

- an array `data`,
- fixed `size`,
- and indices `front` and `rear`.

Operations are:

- `enqueue`: write at the rear,
- `dequeue`: read from the front and increment `front`.

At first glance this seems symmetric to the array stack.
But the lecture shows that it runs out of usable space even when the number of stored items is small.

## Why the Plain Array Queue Wastes Space

The issue is that `front` keeps moving rightward after dequeues.

So even if old elements are gone, the vacated slots at the beginning are no longer reused.
Eventually:

- `rear` reaches the physical end of the array,
- and the queue appears full,
- even though there may be free cells at the front.

This is one of the lecture's best representation-failure examples:

- the ADT is correct,
- but the naive representation wastes capacity.

## Circular Queues

The fix is to make the array queue circular.

The lecture updates the operations so that:

- `rear = (rear + 1) % size`
- `front = (front + 1) % size`

Now the front and rear wrap around instead of drifting only to the right.
That allows freed cells to be reused.

## Empty and Full in a Circular Queue

Once the queue wraps around, empty/full detection becomes more delicate.

The lecture's final version uses one intentionally unused slot.

The constructor effectively enlarges the array by one cell, and the conditions become:

- empty when `((rear + 1) % size) == front`
- full when `((rear + 2) % size) == front`

This is a classic circular-buffer trick:

- sacrifice one slot,
- gain simple state tests.

## Example of Using a Stack: Matching Parentheses

The lecture then gives the first fully algorithmic use of a stack:

- decide whether a string has matching parentheses

The examples include:

- balanced inputs such as `(abc)` and nested bracket patterns,
- and unbalanced inputs such as a lone opening parenthesis or mismatched closing order.

The stack logic is the important part:

1. scan the string left to right,
2. push each opening delimiter,
3. on a closing delimiter, check whether the stack top has the matching opener,
4. fail immediately if it does not,
5. and succeed only if the stack is empty at the end.

This is the lecture's clearest illustration that stacks are not just storage containers.
They are algorithmic control structures.

## Why the Parentheses Example Matters

This example is simple but deep.

It shows that LIFO behavior naturally matches nested structure:

- the most recently opened delimiter
- must be the first one closed

That "most recent unfinished thing gets handled first" pattern appears constantly in parsing, expression processing, and runtime control flow.

## What This Note Is Really Teaching

This lecture is not just introducing several implementations.
It is teaching a deeper pattern:

```text
an abstract access discipline can often be implemented in multiple ways,
and the right representation depends on the operation pattern
```

That is the same tradeoff mindset from arrays and linked lists, now expressed at the ADT level.

The parentheses example adds one more lesson:

```text
the right data structure often becomes visible from the dependency order in the task
```

Nested matching needs LIFO, so a stack is the natural fit.

## Common Mistakes

- Treating stacks and queues as specific concrete structures instead of access disciplines.
- Forgetting that queue means FIFO and stack means LIFO.
- Assuming a queue implemented with an array behaves like a stack implemented with an array.
- Forgetting that the plain array queue can waste space after repeated dequeues.
- Missing the invariant role of `top`, `front`, and `rear`.
- Forgetting that the circular queue's empty/full logic depends on reserved capacity.
- Treating the parentheses-matching example as a memorized trick rather than as an application of LIFO dependency handling.

## Why This Matters for CS / Systems

This note matters because stacks and queues appear everywhere:

- runtime systems,
- traversal algorithms,
- buffering,
- scheduling,
- and asynchronous pipelines.

The lecture's real contribution is to connect those familiar behaviors to representation choices that control correctness, space use, and update cost.

## Short Takeaway

Queues and stacks are abstract access patterns, not single concrete structures. A queue is FIFO and naturally fits linked-list rear insertion plus front deletion, while a stack is LIFO and maps cleanly to either linked-list front operations or an array with a `top` index. A naive array queue wastes space after dequeues, which is why the lecture introduces the circular queue and its modular `front`/`rear` invariants. The parentheses example then shows what these ideas are for: stacks solve problems whose unfinished work must be resumed in reverse order.
