# 09. Recursion and Explicit Stack Template

## What

This template captures the new coding burden introduced in DS07:

- recursive binomial coefficients
- recursive Tower of Hanoi
- a `Context` object for saved execution state
- and explicit stack simulation of recursive control flow

It is based on [08. Recursion and Explicit Stack Simulation](/Users/minkyu/Documents/mlsys/foundations/data-structures/08-recursion-and-explicit-stack-simulation.md).

## Core Rules

```text
every recursive function needs base cases and smaller recursive calls;
every explicit stack simulation needs saved context and return-stage information.
```

That second line is the real leap in DS07.

## Canonical Java Skeleton

Code:
- [09-recursion-and-explicit-stack-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/09-recursion-and-explicit-stack-template.java)

The file includes:

- recursive `binom`
- recursive Hanoi
- `Context`
- iterative Hanoi with an explicit stack

## Recursive Binomial Template

The lecture's structure is:

1. reject out-of-range inputs
2. return `1` for boundary cases
3. recurse on smaller instances
4. combine the subresults

Minimal shape:

```java
if ((k < 0) || (k > n)) return 0;
if ((k == 0) || (k == n)) return 1;
return binom(n - 1, k) + binom(n - 1, k - 1);
```

The main exam habit here is:

- identify base cases first
- then make sure recursive calls move toward them

## Recursive Hanoi Template

The lecture's Hanoi shape is:

1. stop if `n <= 0`
2. compute auxiliary rod
3. move `n - 1` disks to auxiliary
4. print the current move
5. move `n - 1` disks to target

Minimal shape:

```java
if (n <= 0) return;
int aux = 6 - from - to;
Hanoi(n - 1, from, aux);
System.out.println(...);
Hanoi(n - 1, aux, to);
```

## Why the `Context` Object Exists

In the explicit-stack version, `Context` is the manual activation record.

It stores:

- current parameters
- and `whereToReturn`

The key mental model is:

```text
whereToReturn tells us which part of the original recursive function should run next
```

## Explicit Stack Simulation Template

The lecture's iterative Hanoi has this shape:

1. initialize a stack
2. initialize the current context with return stage `0`
3. run a loop
4. dispatch by `switch (ctx.whereToReturn)`
5. push suspended contexts before descending into subcalls
6. when a subcall finishes, pop the next saved context

That is the template to remember, not every exact line.

## Meaning of the Return Stages

For the lecture's Hanoi simulation:

- `0` means "before the first recursive subcall"
- `1` means "after the first subcall, before printing and second subcall"
- `2` means "after the second subcall, this frame is done"

This is the most important reconstruction idea in the whole template.

## Pressure Checklist

1. Did I identify the base cases before writing recursive calls?
2. Do recursive calls move toward the base cases?
3. If using explicit stack simulation, did I create a context object with all necessary state?
4. Does `whereToReturn` clearly correspond to stages of the original recursive function?
5. Before "descending" into a subcall, did I push the suspended current context?
6. When a call is done, do I either pop the next context or stop if the stack is empty?

## Common Mistakes

- Writing recursion without real base cases.
- Making recursive calls that do not shrink the problem.
- Treating the explicit-stack version as a totally different algorithm instead of the same control flow made visible.
- Forgetting to save enough context before descending into the next simulated call.
- Using `whereToReturn` values without a clear mapping to the original recursive stages.

## Short Takeaway

For DS07, the new hand-coding template is recursion as state management. The recursive versions need clear base cases and smaller subcalls, while the explicit-stack version needs a `Context` object plus a return-stage field that tells the loop exactly what suspended work remains.
