# Mathematical Induction

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
