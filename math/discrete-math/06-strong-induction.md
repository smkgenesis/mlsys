# 06. Strong Induction

## What

Strong induction is a proof method for statements indexed by integers, usually natural numbers.

To prove `P(n)` for all `n >= n0`, strong induction works by:

1. proving enough base cases at the beginning,
2. assuming all earlier cases up to `n`,
3. and using that larger assumption set to prove `P(n + 1)`.

Its standard form is:

- Base cases: prove `P(m)` for every `m` in some starting range `n0 <= m <= n1`
- Induction hypothesis: assume `P(m)` holds for every `m` with `n0 <= m <= n`
- Induction step: prove `P(n + 1)`

## Why It Matters

Ordinary induction only gives you:

```text
assume P(k), prove P(k + 1)
```

Strong induction gives you:

```text
assume P(n0), P(n0 + 1), ..., P(k), prove P(k + 1)
```

That makes it useful when the next case depends on:

- more than one earlier case,
- any smaller case,
- or a recursive decomposition into smaller subproblems.

This is common in:

- recurrence relations,
- number theory,
- game arguments,
- recursive algorithms,
- and proofs where objects split into smaller parts.

## Ordinary Induction vs Strong Induction

The difference is not in the overall goal. Both aim to prove infinitely many cases.

The difference is in the induction hypothesis.

### Ordinary induction

You assume only:

```text
P(k)
```

and prove:

```text
P(k + 1)
```

### Strong induction

You assume:

```text
P(n0), P(n0 + 1), ..., P(k)
```

and prove:

```text
P(k + 1)
```

So strong induction gives you a wider memory inside the proof step.

## Strong Induction Is Still About Implication

The lecture emphasizes that strong induction is still an implication proof.

What you prove is:

```text
P(n0) ∧ P(n0 + 1) ∧ ... ∧ P(k) -> P(k + 1)
```

That means:

- the earlier cases are assumed only temporarily,
- and only for the purpose of proving the next case.

So strong induction is not guessing from examples. It is a formal proof of a conditional statement.

## Why Multiple Base Cases May Be Needed

Because the induction step may depend on several earlier cases, one base case is often not enough.

Examples:

- if a recurrence depends on `a_(n-1)` and `a_(n-2)`, you usually need two base cases
- if a game state depends on two earlier positions, you may need to classify both first

The number of base cases should match the amount of “history” the induction step needs to get started.

## Example: Coin Game

The lecture studies a game:

- there is a pile of `n` coins,
- each move removes 1 or 2 coins,
- the player who takes the last coin loses.

The observed pattern is:

- `1`: lose
- `2`: win
- `3`: win
- `4`: lose
- `5`: win
- `6`: win

This suggests the conjecture:

```text
the starting position with n coins is losing iff n = 3k + 1
```

### Why strong induction fits

To classify position `n + 1`, the next player may leave:

- `n` coins, or
- `n - 1` coins

So the proof of `P(n + 1)` depends on more than just `P(n)`.

That is exactly when strong induction is natural.

### Hidden recursive structure

Game proofs often use these two rules:

- a position is losing if every legal move gives the opponent a winning position
- a position is winning if some legal move gives the opponent a losing position

Those definitions are recursive in terms of smaller positions. That is why strong induction appears so naturally in impartial games.

## Example: Linear Recurrence

The lecture also gives:

```text
a1 = 3, a2 = 5, and an = 3a_(n-1) - 2a_(n-2) for n > 2
```

and asks you to prove:

```text
a_n = 2n + 1
```

for all `n >= 1`.

This needs:

- `P(1)` and `P(2)` as base cases,
- and then an induction step using both earlier values.

This is a standard strong-induction pattern:

- the recurrence itself tells you how much history the proof needs.

## Example: Chocolate-Bar Game

The chocolate example is useful because it shows strong induction in a more structural setting.

A move:

- splits a rectangle into two smaller rectangles,
- one part is eaten,
- the other part is passed on.

The current position can therefore depend on many smaller rectangle sizes, not just one immediately previous state.

That makes strong induction a better fit than ordinary induction.

This is an important lesson:

- strong induction is especially natural when a problem decomposes into smaller subproblems of different sizes.

## Strong Induction in Number Theory

The lecture ends by linking strong induction to the Fundamental Theorem of Arithmetic and the well-ordering principle.

That is a classic connection.

For statements like:

```text
every integer n > 1 has a prime factorization
```

the proof often works by:

- taking a number `n`,
- breaking it into smaller factors,
- and applying the theorem to those smaller numbers.

That requires access to all smaller cases, not only `n - 1`.

So strong induction is one of the main tools in elementary number theory.

## Practical Template

When solving a strong-induction problem, it helps to ask:

1. What is the exact statement `P(n)`?
2. What is the correct starting range of base cases?
3. In the induction step, which earlier cases might `P(n + 1)` depend on?
4. Does the problem naturally split into cases?
5. Is the dependency on all smaller values, or only on a few of them?

These questions usually tell you whether strong induction is the right tool.

## Common Signs That Strong Induction Is the Right Choice

- A recurrence uses `n - 1` and `n - 2`, or more.
- A game position depends on several smaller positions.
- A proof decomposes an object into smaller parts whose sizes vary.
- A theorem about `n` uses facts about divisors or factors smaller than `n`.

If the next case depends on more than the immediate predecessor, strong induction should be one of the first things you try.

## Common Pitfalls

- Using only one base case when the step needs more.
- Assuming “all previous cases” but then failing to use the full strength carefully.
- Forgetting that the induction hypothesis applies only to earlier values in the allowed range.
- Mixing ordinary induction language with a proof that actually depends on several earlier cases.
- Not restating the theorem properly as `P(n)` before starting.

## Strong Induction and Ordinary Induction Are Equivalent

The lecture’s final point is important:

- strong induction is not logically stronger than ordinary induction,
- but it is often more convenient.

So the distinction is practical, not foundational.

Use ordinary induction when the proof step naturally needs only one previous case.

Use strong induction when the proof step naturally needs more memory.

## Why This Matters Beyond Pure Math

Strong induction maps naturally onto computer science reasoning:

- recursive algorithms often depend on correctness of smaller inputs,
- divide-and-conquer proofs depend on subproblems of varying sizes,
- and game or state-transition arguments often depend on many earlier positions.

So this is not just a discrete-math trick. It is one of the cleanest ways to reason about recursive structure.

## Short Takeaway

Strong induction is ordinary induction with a larger induction hypothesis: instead of assuming only the previous case, you assume all earlier cases up to `k`, which makes the method fit recurrences, games, factorization, and recursive decomposition much more naturally.
