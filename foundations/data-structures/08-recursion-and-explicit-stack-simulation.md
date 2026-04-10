# 08. Recursion and Explicit Stack Simulation

## What

This note opens the recursion branch of the course.

The lecture introduces recursion through:

- recursive computation of binomial coefficients,
- the Tower of Hanoi,
- the call stack as the execution model behind recursion,
- and an explicit stack-based simulation of recursion without a recursive function.

## Why It Matters

This material belongs immediately after stacks for a reason.

The lecture is making a strong connection:

```text
recursion is not magic;
it runs because the program keeps a stack of pending work
```

That is one of the most important conceptual bridges in a data-structures course.

It ties together:

- recursive definitions,
- runtime behavior,
- and explicit stack-based control flow.

## Core Idea

The lecture uses recursion in two complementary ways:

1. as a direct way to express a problem in terms of smaller subproblems,
2. and as something that can be simulated explicitly with a stack of contexts.

That second step is the deeper one.
It shows that recursion and stacks are not separate topics.

## Recursive Binomial Coefficients

The lecture first revisits binomial coefficients.

The core identity is:

- base cases at the boundary of the triangle,
- plus a recurrence that expresses one value in terms of smaller ones.

The recursive Java function then mirrors that mathematical definition:

- reject out-of-range inputs,
- return `1` for boundary cases,
- otherwise recurse on smaller arguments and combine the results.

This is the classic recursive pattern:

- base case,
- recursive case,
- combine subresults.

## Why This Example Works Well

The binomial-coefficient example is useful because the recursive structure comes directly from the mathematics.

That makes it easy to see what recursion is really doing:

- not looping arbitrarily,
- but following the dependency structure of the formula itself.

## Tower of Hanoi

The lecture then turns to the Tower of Hanoi.

The problem is:

- `n` disks,
- 3 rods,
- move one disk at a time,
- never place a larger disk on a smaller one,
- move the whole stack to the target rod.

The recursive solution has the standard structure:

1. move `n - 1` disks to the auxiliary rod,
2. move the largest disk to the target rod,
3. move `n - 1` disks from the auxiliary rod to the target rod.

This is one of the canonical recursion examples because the recursive decomposition is almost forced by the problem constraints.

## The Call Stack

The lecture then returns to the call stack.

This is a crucial transition.

The point is:

- every recursive call suspends unfinished work,
- that work must be resumed later,
- and the suspended states are managed in LIFO order.

So recursion is not just a source-code pattern.
It is stack-managed control flow.

## Recursion Without a Recursive Function

The lecture then makes the idea concrete by simulating Hanoi without directly using recursion.

Instead, it introduces an explicit `Context` object holding:

- the current parameters,
- and a `whereToReturn` field describing what stage of the computation remains.

Then it uses:

- a `Stack`,
- a loop,
- and a `switch`

to mimic the recursive control flow.

This is one of the strongest conceptual moments in the branch so far.

## Why the Context Object Matters

The `Context` object is acting like a manual activation record.

In ordinary recursion, the runtime stores:

- parameters,
- local state,
- and where execution should resume after a recursive call.

The lecture makes that normally hidden mechanism explicit.

That is exactly why the example is so valuable.

## The Meaning of whereToReturn

The `whereToReturn` field captures the phase of the recursive procedure.

Conceptually it means:

- "which part of the original recursive function should run next when this subcall finishes?"

That is how the iterative simulation reconstructs the same control structure a language runtime normally provides automatically.

## What This Note Is Really Teaching

This lecture is not only about recursion syntax.
It is teaching the execution model behind recursive programs.

The real lesson is:

```text
recursive programs work because pending computations are stacked,
and that stack discipline can be made explicit
```

That is why this material belongs inside the data-structures branch rather than inside a general programming note.

## Common Mistakes

- Treating recursion as a mysterious language feature instead of stack-managed control flow.
- Writing recursive cases without first identifying the base cases.
- Forgetting that recursive decomposition must make progress toward a base case.
- Seeing the explicit-stack simulation as a different algorithm rather than the same control flow made visible.
- Ignoring the role of saved context and return location in recursive execution.

## Why This Matters for CS / Systems

This note matters because recursion appears everywhere:

- combinatorial generation,
- divide-and-conquer algorithms,
- tree traversal,
- parsing,
- and search.

Understanding recursion as explicit state plus a stack makes later algorithm design much more grounded.

## Short Takeaway

This lecture teaches recursion from both sides. Recursive formulas such as binomial coefficients and recursive procedures such as Tower of Hanoi show how problems can be expressed in terms of smaller subproblems. The explicit-stack Hanoi simulation then reveals what recursion really depends on: saved execution contexts managed in LIFO order by a stack.
