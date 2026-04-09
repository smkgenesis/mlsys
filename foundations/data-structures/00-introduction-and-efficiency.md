# 00. Introduction and Efficiency

## What

This note introduces data structures through two foundational ideas:

- computers store and process information,
- and the way information is organized affects processing efficiency.

It also introduces the first formal language for comparing programs:

- analytical running-time comparison,
- and asymptotic order-of-growth notation.

## Why It Matters

The lecture begins before specific structures like stacks or trees.
It begins with a broader question:

```text
why does the organization of data matter at all?
```

The answer is that storing the same information in different ways can make later operations dramatically easier or harder.

So data structures are not just containers.
They are design choices that shape the cost of processing.

## Core Idea

The note builds one continuous argument:

1. computers store and process information
2. storage organization affects processing efficiency
3. programs solving the same problem can have very different running costs
4. exact runtime formulas are possible, but often too detailed
5. order-of-growth notation keeps the dominant part and discards irrelevant constants

That is the conceptual foundation of the whole subject.

## Computers Store and Process Information

The lecture frames computers very broadly:

- they store information,
- and they process information.

The medical-record example is useful because it shows that storage and processing are linked.

If records are organized better, it becomes easier to:

- retrieve one patient’s record,
- modify it,
- and return it efficiently.

So the key point is:

```text
data organization in storage affects processing efficiency
```

That is the bottom-line motivation for learning data structures.

## Two Correct Programs Can Differ Greatly in Cost

The minimum-of-array example is the first concrete demonstration.

Two programs solve the same problem:

- one checks every candidate against the whole array
- the other keeps a tentative minimum and updates it when a smaller value appears

Both are correct.
But one does much more work.

This is one of the first important lessons in data structures and algorithms:

```text
correctness alone is not enough;
we also compare cost
```

## Empirical vs Analytical Comparison

The lecture presents two ways to compare efficiency.

### Empirical

- run the program
- measure execution time on different data

This is practical, but it depends on:

- platform,
- implementation details,
- and measurement conditions

### Analytical

- write running time as a formula in terms of input size

Example shape:

```text
3n^2 + 4n + 200
```

This abstracts away from one particular machine and focuses on how the cost grows with problem size.

The lecture chooses this second approach because it gives a more durable comparison.

## Exact Cost Formulas Are Limited

The lecture briefly shows exact formulas such as:

- first attempt: quadratic expression
- second attempt: linear expression

This is helpful because it makes the difference visible.

But it also raises the next question:

```text
how meaningful is this exact precision?
```

Different machines and implementations can change constant factors.

So exact formulas are useful, but they are not the final abstraction.

## Order of Growth

To compare algorithms more robustly, the lecture moves to order of growth.

The central idea is:

- treat `2n` and `3n` as essentially the same growth class
- focus on the dominant growth behavior
- ignore constant multipliers and lower-order terms

This is where asymptotic notation enters.

## Big-O

The lecture defines:

```text
f(n) = O(g(n))
```

when `f(n)` can be bounded above by a constant multiple of `g(n)` for all sufficiently large `n`.

So Big-O is an asymptotic upper bound.

Examples from the lecture:

- `2n = O(n)`
- `3n = O(n)`
- `.01n^2 + 100n = O(n^2)`

The purpose is to capture the dominating term in growth.

## Omega and Theta

The lecture also introduces:

- `Ω(g(n))` as an asymptotic lower bound
- `Θ(g(n))` when both upper and lower bounds match asymptotically

So:

```text
f(n) = Θ(g(n))
```

means:

```text
f(n)` grows at the same asymptotic rate as `g(n)
```

up to constant factors.

This is the notation that lets us compare the broad efficiency class of algorithms without pretending that exact nanosecond formulas are universally meaningful.

## What This Note Is Really Teaching

This lecture is not yet about one specific data structure.
It is teaching the mental frame required for the whole subject:

- organization affects efficiency,
- efficiency must be compared systematically,
- and asymptotic reasoning is the language of that comparison.

Without this frame, later structures can easily feel like a list of implementation tricks instead of answers to efficiency problems.

## Common Mistakes

- Thinking data structures are only about storage and not about processing cost.
- Treating two correct solutions as equally good without asking how work scales with input size.
- Overvaluing exact timing formulas even when constants and platforms vary.
- Using Big-O as if it preserves every constant and lower-order term.
- Forgetting that asymptotic notation is about behavior for sufficiently large input sizes.
- Confusing upper bound, lower bound, and tight bound.

## Why This Matters for CS / Systems

This note is foundational because it introduces a systems habit very early:

```text
representation choices change operational cost
```

That idea survives all the way into later topics:

- memory layout,
- indexing structure,
- search cost,
- update cost,
- and ultimately system-level performance tradeoffs.

## Short Takeaway

Data structures begin with a simple but powerful idea: the way information is organized changes how efficiently it can be processed. The first practical consequence is that program comparison must include efficiency, and asymptotic notation gives us the language for comparing growth while ignoring irrelevant constant details.
