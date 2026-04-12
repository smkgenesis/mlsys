# Chapter 3. Proof by Mathematical Induction

## What

Mathematical induction is a proof technique for statements indexed by discrete integers, usually natural numbers.

Its standard form proves a statement `P(n)` for all `n >= n0` by showing:

1. the base case `P(n0)` is true,
2. and for every arbitrary but fixed `k >= n0`, `P(k) -> P(k + 1)` is true.

This is a rigorous logical method. It is different from ordinary everyday induction, where people guess a general rule from a few examples.

## Why It Matters

Induction is one of the first proof techniques that lets us prove infinitely many cases with a finite argument.

It appears constantly in:

- sums and products,
- divisibility statements,
- recursive definitions,
- loop invariants,
- correctness of algorithms,
- and structural arguments in discrete mathematics.

If direct proof handles one fixed case, induction handles an entire chain of cases.

## Everyday Induction vs Mathematical Induction

The lecture distinguishes two meanings of induction.

### Everyday induction

This is pattern-based reasoning from examples:

- every swan I have seen is white,
- so maybe all swans are white.

This is empirical and not logically guaranteed.

### Mathematical induction

This is a proof method.

You do not infer the rule from repeated observation. You prove:

- the first case is true,
- and truth passes from one case to the next.

That is why mathematical induction is valid proof rather than educated guessing.

## The Core Template

To prove `P(n)` for all `n >= n0`:

### 1. Base case

Prove `P(n0)`.

This anchors the chain.

### 2. Induction hypothesis

Let `k >= n0` be arbitrary but fixed, and assume `P(k)` is true.

This assumption is temporary and local to the proof of the next step.

### 3. Induction step

Using the induction hypothesis, prove `P(k + 1)`.

If both parts are done, then `P(n)` holds for all `n >= n0`.

## Why It Works

The lecture gives the right intuition:

- prove the first domino falls,
- prove each domino knocks over the next,
- then the whole line falls.

Or, in ladder language:

- prove you can reach the first rung,
- prove that from any rung you can reach the next,
- then you can climb all the way up.

Logically, induction proves an implication chain:

```text
P(n0) -> P(n0 + 1) -> P(n0 + 2) -> ...
```

Once the first statement is secured and each step implies the next one, every later case follows.

## Induction Is Still About Implication

The lecture connects induction back to implication:

```text
P(k) -> P(k + 1)
```

This matters because the induction step is not:

- proving `P(k + 1)` from nothing,
- or checking a few examples.

It is proving a conditional statement.

That is why the induction hypothesis is allowed: we assume `P(k)` only for the purpose of proving `P(k + 1)`.

## Example: Sum of the First Powers of 2

The lecture studies the claim:

```text
2^0 + 2^1 + ... + 2^(n - 1) = 2^n - 1
```

for all `n >= 0`.

This is a good first induction example because:

- examples suggest the pattern,
- but induction is what proves it for all `n`.

### Base case

When `n = 0`, the sum is empty, and by convention the empty sum is `0`.

So:

```text
0 = 2^0 - 1 = 0
```

Thus the base case holds.

### Induction step

Assume:

```text
2^0 + 2^1 + ... + 2^(k - 1) = 2^k - 1
```

Then:

```text
2^0 + 2^1 + ... + 2^(k - 1) + 2^k
= (2^k - 1) + 2^k
= 2^(k + 1) - 1
```

So `P(k + 1)` follows from `P(k)`.

This is the standard rhythm of many induction proofs:

- isolate the extra term,
- substitute the induction hypothesis,
- simplify to the target form.

## Empty Sum and Empty Product Conventions

The lecture explicitly introduces two conventions:

- an empty sum is `0`
- an empty product is `1`

These conventions matter because they make formulas work cleanly at boundary cases such as `n = 0`.

They are not arbitrary decorations. They help statements stay uniform and avoid messy special handling.

## What the Induction Hypothesis Is and Is Not

The induction hypothesis is often misunderstood.

It is:

- the assumption that `P(k)` is true,
- where `k` is arbitrary but fixed,
- used only inside the induction step.

It is not:

- assuming the whole theorem is true,
- checking one convenient example,
- or assuming `P(k + 1)` itself.

The discipline of induction is that you may use exactly the previous statement you are entitled to assume.

## Induction Can Prove More Than Numerical Identities

The lecture’s pigeonhole-principle proof is important because it shows induction is not restricted to sums or algebraic formulas.

The theorem does not naturally start as a statement like:

```text
P(n) = some equation
```

So the first real task is to choose the induction variable.

The lecture does this by defining:

```text
P(n): for any finite sets X and Y with |X| > |Y| and |X| = n,
every function f: X -> Y maps two distinct elements of X to the same value.
```

That is the right lesson:

- when a theorem is not already phrased in terms of `n`,
- you often need to restate it as a family of statements `P(n)`.

This is one of the most important practical skills in induction.

## Induction Plus Case Analysis

In the pigeonhole example, the induction step is not just algebra.

It uses:

- induction,
- set reduction,
- and case analysis.

That shows induction is often a framework rather than the whole proof.

Inside the induction step, you may still need:

- direct proof,
- contradiction,
- contrapositive,
- or case analysis.

So induction does not replace other proof methods. It organizes them across infinitely many cases.

## A Practical Template for Induction Problems

When you face a new induction problem, it helps to ask:

1. What exactly is `P(n)`?
2. What is the correct starting value `n0`?
3. What does the base case look like when written out concretely?
4. In the induction step, where is the old case hidden inside the new one?
5. After substituting the induction hypothesis, can the expression be simplified into the target form?

For non-numeric theorems, add one more question:

6. What quantity should I induct on?

That is often the hardest part.

## Common Pitfalls

- Forgetting to prove the base case.
- Proving the induction step only for one special value of `k`.
- Using the induction hypothesis in a way that assumes more than `P(k)`.
- Starting from the target expression and manipulating backward without justification.
- Choosing the wrong induction variable.
- Forgetting that some formulas require an empty-sum or empty-product convention at the boundary.

The lecture emphasizes two especially important checks:

- the base case must actually be true,
- and the induction step must work for every admissible `k`.

## Why This Matters Beyond Pure Math

Induction is foundational for computer science because many CS objects are defined recursively or grow one step at a time.

The same reasoning pattern appears in:

- recursive algorithm correctness,
- loop reasoning,
- recurrence relations,
- data structure properties,
- and proofs over input size.

For later ML systems study, the transfer is indirect but real:

- induction builds comfort with recursive structure,
- state transitions,
- and stepwise correctness arguments.

## Short Takeaway

Mathematical induction is a rigorous proof method for discrete infinite families of statements: prove the first case, prove that each case implies the next, and the entire sequence follows.

## Worked Exercise Patterns from Chapter 3

The following exercise set has already been worked through from the Chapter 3 material.

### 3.1 Closed form for the sum of the first odd integers

The sum

$$
\sum_{i=0}^{n-1}(2i+1)
$$

has closed form

$$
n^2
$$

Main lesson:

- the first $n$ odd integers sum to a perfect square, and this is a standard induction warm-up.

### 3.2 Pigeonhole Principle by induction on $\lvert Y \rvert$

Instead of inducting on the size of the domain $\lvert X \rvert$, one can induct on the size of the codomain $\lvert Y \rvert$:

- remove one bucket $y \in Y$
- if two objects already map to $y$, the collision is immediate
- otherwise remove at most one object from the domain and apply the induction hypothesis to the reduced function

Main lesson:

- choosing the induction variable is often the real problem.

### 3.3 Extended Pigeonhole Principle by induction

The extended principle says:

- if more than `kn` objects are placed into `n` boxes, then some box contains at least `k+1` objects

When forced into induction form, the cleanest induction variable is the number of boxes.

Main lesson:

- some theorems are more naturally proved directly, but can still be reframed into induction by choosing the right size parameter.

### 3.4 Odd powers of `2` and negative powers of `2`

Key formulas:

$$
\sum_{i=1}^{n} 2^{2i-1} = \frac{2}{3}(4^n - 1)
$$

and

$$
\prod_{i=1}^{n} 2^{-i} = 2^{-\,\frac{n(n+1)}{2}}
$$

Main pattern:

- for sums, write `S_{k+1} = S_k + new term`
- for products, write `P_{k+1} = P_k * new factor`

### 3.5 Sum of squares

The expression

$$
\sum_{i=0}^{n} i^2 = \frac{n(n+1)(2n+1)}{6}
$$

is a standard induction example.

Main lesson:

- many induction proofs are algebra-management exercises after the correct split
$$
\sum_{i=0}^{k+1} = \sum_{i=0}^{k} + \text{last term}
$$
has been written down.

### 3.6 Finite geometric sum approaching `2`

For

$$
S(n) = \sum_{i=0}^{n} 2^{-i}
$$

the worked values suggest the closed form

$$
S(n) = 2 - 2^{-n}.
$$

This can be proved by induction. Then

$$
\lvert 2 - S(n) \rvert = 2^{-n},
$$

so to be within $\varepsilon$ of $2$ it is enough that

$$
2^{-n} < \varepsilon.
$$

Main lesson:

- induction can establish a closed form, and the closed form can then answer an approximation question.

### 3.7 Sum of cubes

The classical identity is

$$
\sum_{i=0}^{n} i^3 = \left(\sum_{i=0}^{n} i\right)^2.
$$

Equivalently,

$$
\sum_{i=0}^{n} i^3 = \left(\frac{n(n+1)}{2}\right)^2.
$$

Main lesson:

- some induction targets become easier after rewriting the right-hand side into a familiar closed form.

### 3.8 Weighted sums with powers of `2`

Worked identities:

$$
\sum_{i=1}^{n} 2^{i-1} i = 2^n(n-1) + 1
$$

and

$$
\sum_{i=1}^{n} 2^{i-1} i^2 = 2^n(n^2 - 2n + 3) - 3
$$

Main pattern:

- isolate the last term,
- substitute the induction hypothesis,
- then simplify aggressively until the target form reappears.

### 3.9 The “all horses are the same color” fake proof

The flaw is in the induction step at `n = 1`.
The two subsets

- `A = {h_1}`
- `B = {h_2}`

have empty intersection, so there is no common horse connecting the colors of `A` and `B`.

Main lesson:

- in induction, the step must work for every admissible `k`, especially the first transition from the base case.

### 3.10 Why `512` cannot be the last term in the increasing-difference interpretation

The expression

```text
1 + 2 + 4 + 7 + 11 + 16 + ... + 512
```

can be interpreted as a sequence whose successive differences increase by `1`.
If `a_1 = 1` and `a_{n+1} = a_n + n`, then induction gives

$$
a_n = 1 + \frac{n(n-1)}{2}.
$$

by induction, set `a_n = 512` and observe there is no integer `n`.

Main lesson:

- induction often supplies the closed form that makes a later yes/no question easy.

### 3.11 Thue sequence

Two important recursive patterns were highlighted:

- recursive objects may require a stronger induction claim than the one first asked for
- string properties such as reverse and complement must be tracked structurally

Main lesson:

- not all induction problems are algebraic; some are recursive-structure problems.

### 3.12 Weak induction is sufficient

Any induction proof starting from an arbitrary base index `n0` can be shifted to a proof starting at `0` by defining

$$
Q(m) := P(m+n_0)
$$

Main lesson:

- many “more general” induction principles are equivalent after an index shift.

## Chapter 3 Pattern Library

The recurring patterns in this chapter are:

- always write the four-part induction skeleton explicitly:
  base case, induction hypothesis, induction step, conclusion
- look for the old case inside the new case before doing algebra
- for sums and products, split off the final term or factor first
- if the direct induction target does not close, consider a stronger claim
- when a proof by induction looks suspicious, test the first nontrivial step
