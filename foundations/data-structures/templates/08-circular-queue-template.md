# 08. Circular Queue Template

## What

This template captures the array-queue repair introduced in DS06:

- why the naive array queue wastes space
- modular wraparound with `front` and `rear`
- one intentionally unused slot
- and the final `isEmpty` / `isFull` formulas

It is based on [07. Stacks and Queues](/Users/minkyu/Documents/mlsys/foundations/data-structures/07-stacks-and-queues.md).

## Core Invariant

```text
front and rear move modulo size,
and one slot is intentionally left unused.
```

That one wasted slot is what makes the state tests simple enough to reconstruct reliably by hand.

## Canonical Java Skeleton

Code:
- [08-circular-queue-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/08-circular-queue-template.java)

The lecture's final circular-queue shape uses:

- `data = new int[s + 1]`
- `size = s + 1`
- `front = 0`
- `rear = -1 + s`

## Why the Plain Array Queue Fails

In the naive array queue:

- `rear` keeps moving rightward
- `front` also moves rightward after dequeues

So the queue can appear full even when there are empty cells at the beginning of the array.

That is the exact failure mode the circular queue fixes.

## Empty and Full Tests

These two formulas are the ones to memorize:

```java
public boolean isEmpty() {
    return ((rear + 1) % size) == front;
}

public boolean isFull() {
    return ((rear + 2) % size) == front;
}
```

The lecture's design choice is:

- give up one slot
- gain very simple boundary logic

## enqueue Repair Rule

Repair order:

1. check `!isFull()`
2. advance `rear = (rear + 1) % size`
3. write `data[rear] = x`

## dequeue Repair Rule

Repair order:

1. fail immediately if empty
2. save `data[front]`
3. advance `front = (front + 1) % size`
4. return saved value

## Pressure Checklist

1. Am I using the circular version, not the naive drifting queue?
2. Did I remember the one-unused-slot convention?
3. Are `front` and `rear` updated with modulo arithmetic?
4. Did I use the correct `isEmpty()` and `isFull()` formulas?
5. In `enqueue`, did I move `rear` before storing?
6. In `dequeue`, did I read first and advance `front` after?

## Common Mistakes

- Reusing the naive array-queue formulas in the circular version.
- Forgetting the extra slot in the constructor.
- Mixing up `front` and `rear`.
- Updating `rear` or `front` without modulo arithmetic.
- Forgetting that the design intentionally sacrifices one cell.

## Short Takeaway

For DS06, the circular queue is the key array-queue template. It fixes wasted space by wrapping `front` and `rear` modulo the array size, and it keeps the implementation manageable by leaving one slot intentionally unused.
