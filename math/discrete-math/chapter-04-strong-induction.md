# Chapter 4. Strong Induction

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

The most useful way to internalize strong induction is as a reusable proof skeleton.

## Strong Induction Template

To prove a statement $P(n)$ for all $n \ge n_0$:

### 1. State the claim clearly

Write the statement in the form:

$$
P(n): \text{ ... }
$$

This matters because strong induction is not a style choice first.
It is a proof about a family of indexed statements.

### 2. Verify all required base cases

Prove every initial case needed to start the recursion or decomposition.

Depending on the problem, this may mean:

- one base case,
- two base cases,
- or an initial block such as $P(n_0), P(n_0+1), \dots, P(n_1)$.

The number of base cases should match the amount of history the induction step requires.

### 3. Write the strong induction hypothesis

For some arbitrary but fixed $k \ge n_1$, assume:

$$
P(j) \text{ is true for every integer } j \text{ with } n_0 \le j \le k.
$$

This is the defining difference from ordinary induction.
You are allowed to use all earlier cases in the valid range, not only $P(k)$.

### 4. Prove the next case

Show that $P(k+1)$ is true.

This usually happens in one of three ways:

- a recurrence uses several earlier values such as $a_k$, $a_{k-1}$, or $a_{k-2}$,
- a number or object is decomposed into smaller pieces,
- or a game/recursive structure is reduced to smaller positions.

The central question is always:

```text
which earlier cases are needed to build the new one?
```

### 5. Conclude formally

Finish with:

> Therefore, by strong induction, $P(n)$ is true for all $n \ge n_0$.

This closing sentence matters because it is the point where:

- the verified base cases,
- the induction hypothesis,
- and the induction step

combine into the universal conclusion.

## Fast Answer Template

In exam conditions, the following compact structure is usually enough:

### Base cases

Verify the required initial values.

### Induction hypothesis

Assume that for some $k \ge n_1$, the statement $P(j)$ holds for every integer $j$ with $n_0 \le j \le k$.

### Induction step

Show that $P(k+1)$ holds by expressing the $(k+1)$st case in terms of one or more earlier cases and then applying the induction hypothesis.

### Conclusion

Therefore, by strong induction, $P(n)$ holds for all $n \ge n_0$.

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

## Worked Pattern: Divisibility in a Recurrence

Consider a recurrence such as:

$$
a_1 = 0,\quad a_2 = 6,\quad a_3 = 9,\quad a_n = a_{n-1} + a_{n-3} \text{ for } n > 3.
$$

To prove every $a_n$ is divisible by $3$:

- base cases: check $a_1, a_2, a_3$
- induction hypothesis: assume all earlier values up to $a_k$ are divisible by $3$
- induction step: use
  $$
  a_{k+1} = a_k + a_{k-2}
  $$
  and note both terms are divisible by $3$

This is the cleanest prototype of strong induction:

- the next case depends on multiple earlier cases,
- so the proof naturally wants more than just $P(k)$.

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

## Worked Exercise Patterns from Chapter 4

The following exercise set has already been worked through from the Chapter 4 material.

### 4.1 Divisibility in a recurrence

For the recurrence

$$
a_1 = 0,\quad a_2 = 6,\quad a_3 = 9,\quad a_n = a_{n-1} + a_{n-3} \text{ for } n > 3,
$$

the clean proof pattern is:

- verify the first three base cases,
- assume every earlier value up to $a_k$ is divisible by $3$,
- use
  $$
  a_{k+1} = a_k + a_{k-2}
  $$
  and note that both summands are divisible by $3$.

Main lesson:

- recurrence structure tells you how many base cases are needed.

### 4.2 Every $n \ge 8$ is of the form $3a + 5b$

The key move is not to build $k+1$ from $k$, but from $k-2$:

$$
k+1 = (k-2) + 3.
$$

So the proof works by:

- checking $8, 9, 10$ directly,
- assuming every value from $8$ through $k$ has the required form,
- applying the hypothesis to $k-2$,
- then adding one more $3$.

Main lesson:

- strong induction is often about finding the right smaller case, not necessarily the immediate predecessor.

### 4.3 L-tromino tiling of a $2^n \times 2^n$ square with one missing corner

The recursive tiling pattern is:

- split the large square into four equal quadrants,
- the missing corner lies in exactly one quadrant,
- place one L-shaped tile at the center so that the other three quadrants each now have one missing corner,
- apply the induction hypothesis to all four smaller quadrants.

Main lesson:

- geometric induction often works by decomposing one large object into several smaller copies of the same problem.

### 4.4 Integrality of $x^n + x^{-n}$

Given that

$$
x + \frac{1}{x}
$$

is an integer, define

$$
a_n = x^n + \frac{1}{x^n}.
$$

The useful recurrence comes from multiplying:

$$
\left(x + \frac{1}{x}\right)\left(x^n + \frac{1}{x^n}\right)
=
x^{n+1} + \frac{1}{x^{n+1}} + x^{n-1} + \frac{1}{x^{n-1}}.
$$

So

$$
a_{n+1}
=
\left(x + \frac{1}{x}\right)a_n - a_{n-1}.
$$

With base cases $a_0 = 2$ and $a_1 = x + 1/x$, strong induction shows every $a_n$ is an integer.

Main lesson:

- when the target does not obviously recurse, algebra often reveals a hidden recurrence first.

### 4.5 Bounding a three-term recurrence

For

$$
a_1 = 1,\quad a_2 = 2,\quad a_3 = 3,\quad a_n = a_{n-1} + a_{n-2} + a_{n-3},
$$

the right claim is

$$
a_n < 2^n.
$$

The induction step uses:

$$
a_{k+1} = a_k + a_{k-1} + a_{k-2}
< 2^k + 2^{k-1} + 2^{k-2}
= 7 \cdot 2^{k-2}
< 8 \cdot 2^{k-2}
= 2^{k+1}.
$$

Main lesson:

- strong induction is often combined with termwise bounding.

### 4.6 Strong induction can be reduced to ordinary induction

To show strong induction gives no extra proving power, define a stronger statement

$$
Q(n) := P(0) \land P(1) \land \cdots \land P(n).
$$

Then ordinary induction on $Q(n)$ reproduces the effect of strong induction on $P(n)$.

Main lesson:

- ordinary induction on a stronger statement can simulate strong induction on the original one.

### 4.7 Uniqueness of prime factorization

The proof structure is:

- suppose
  $$
  n = \prod_{i=1}^{k} p_i^{e_i} = \prod_{i=1}^{\ell} r_i^{f_i},
  $$
- use Euclid's lemma to show the first prime on the left must also appear on the right,
- show the corresponding exponents must match,
- divide out the common prime power,
- apply strong induction to the smaller remaining integer.

Main lesson:

- strong induction is natural when a proof repeatedly removes a smaller factor and recurses on what remains.

### 4.8 Induction and Well-Ordering are equivalent

Two conversions matter:

- induction implies well-ordering by inducting on the size of a nonempty finite set and showing it must have a minimum,
- well-ordering implies induction by assuming there is a counterexample set and choosing its least element.

Main lesson:

- the “least counterexample” method is the well-ordering version of induction logic.

### 4.9 Prime decomposition of $100$ by the FTA existence procedure

A worked decomposition path is:

- start with $S = \{100\}$ and split $100 = 2 \cdot 50$,
- then split $50 = 2 \cdot 25$,
- then split $25 = 5 \cdot 5$,
- now all factors are prime.

So

$$
100 = 2^2 \cdot 5^2.
$$

Main lesson:

- the proof of existence of prime factorization is also an actual recursive factorization procedure.

### 4.10 Postage using 2-cent and 3-cent stamps

To prove every $n \ge 2$ cents can be made with at most $n/2$ stamps:

- verify $n=2$ and $n=3$ directly,
- assume all values from $2$ through $k$ work,
- write
  $$
  k+1 = (k-1) + 2,
  $$
- apply the hypothesis to $k-1$ and then add one 2-cent stamp.

The stamp count becomes

$$
\frac{k-1}{2} + 1 = \frac{k+1}{2},
$$

so the bound is preserved.

Main lesson:

- once a representation theorem is true for a short initial block, adding one fixed piece can propagate it indefinitely.

## Chapter 4 Pattern Library

The recurring patterns in this chapter are:

- write enough base cases to support the earliest backward jump used in the step
- ask which smaller case actually builds the new case
- for recurrences, let the recurrence dictate the proof memory
- for decomposition problems, reduce the object to smaller instances of the same type
- for number theory, remove one prime or one factor and recurse on the smaller remainder
- when comparing induction principles, strengthen the proposition rather than the method

## Short Takeaway

Strong induction is ordinary induction with a larger induction hypothesis: instead of assuming only the previous case, you assume all earlier cases up to `k`, which makes the method fit recurrences, games, factorization, and recursive decomposition much more naturally.
