# Basic Proof Techniques

## What

This note introduces the basic language and methods of mathematical proof:

- quantifiers and predicates,
- implication and its related variants,
- constructive and nonconstructive proof,
- direct proof,
- proof by contrapositive,
- and proof by contradiction.

The goal is not only to know these terms, but to understand how they control the structure of mathematical arguments.

## Why It Matters

Examples and intuition help discover patterns, but they do not establish truth.

Mathematics requires proofs:

- explicit,
- precise,
- logically valid,
- and general.

Basic proof techniques matter because they are the first tools for turning:

- observed patterns

into

- correct universal arguments.

Without them, it is very easy to:

- confuse examples with evidence,
- misread logical statements,
- or prove the wrong thing.

## Mathematical Language Must Be Precise

Ordinary language is often ambiguous.

For example, these are not the same:

- for every person `A`, there exists a person `B` such that `A` loves `B`
- there exists a person `B` such that for every person `A`, `A` loves `B`

The difference comes from the order of the quantifiers.

This is why mathematical statements are written with explicit logical structure.

## Quantifiers and Predicates

### Quantifiers

- `∀` means “for all,” “for every,” or “for any”
- `∃` means “there exists” or “for some”

These specify the scope of a claim.

### Predicates

A predicate is a template for a proposition.

Example:

```text
P(A, B) := A loves B
```

By itself, a predicate has no truth value.

It becomes a full statement when:

- actual values are substituted,
- or quantifiers are applied.

This distinction matters because many proof mistakes come from not knowing whether a sentence is:

- a template,
- a fully formed statement,
- or a statement with different quantifier scope than intended.

## Examples Help Discovery but Do Not Prove

Trying examples is useful.

Examples can:

- suggest whether a statement may be true,
- reveal patterns,
- and hint at the right proof strategy.

But examples do not prove a universal claim.

The shift from examples to proof requires:

- introducing general variables,
- using definitions precisely,
- and proving the statement for arbitrary cases rather than a few sample cases.

## Direct Proof

Direct proof is the most basic method.

To prove an implication

```text
p -> q
```

you:

1. assume `p` is true,
2. use definitions and logical rules,
3. derive that `q` must be true.

### Example pattern

Claim:

> if `n` is odd, then `n^2` is odd.

Direct proof begins with the definition of odd:

```text
n = 2k + 1
```

for some integer `k`.

Then:

```text
n^2 = (2k + 1)^2 = 4k^2 + 4k + 1 = 2(2k^2 + 2k) + 1
```

which is odd.

This is a standard direct proof pattern:

- start from the assumption,
- rewrite using a definition,
- and reshape the result into the target form.

## Constructive Proof

A constructive proof does more than show that something exists.

It also shows how to find the object.

Example:

> every odd integer is the difference between squares of two integers

If an odd integer is:

```text
2k + 1
```

choose:

```text
a = k + 1
b = k
```

Then:

```text
a^2 - b^2 = (k + 1)^2 - k^2 = 2k + 1
```

This is constructive because it explicitly gives the integers whose squares produce the desired difference.

Constructive proofs often implicitly describe an algorithm for finding the object they claim exists.

## Nonconstructive Proof

A nonconstructive proof shows that something exists without telling you how to find it.

The pigeonhole principle is a standard example:

- it guarantees that some bucket contains multiple objects,
- but usually does not identify which bucket.

This distinction is important because:

- existence is weaker than construction,
- and construction is weaker than efficient construction.

## Implication

An implication

```text
p -> q
```

means:

- if `p` is true, then `q` must be true.

One useful interpretation is in terms of truth sets.

If:

- `P` is the set of values for which `p` is true,
- `Q` is the set of values for which `q` is true,

then:

```text
p -> q
```

means:

```text
P is a subset of Q
```

This is a good way to understand why an implication can be true even when the converse is false.

## Negation and Equivalence

### Negation

The negation of a statement `p` is “not `p`.”

Example:

- the negation of “`n` is odd” is effectively “`n` is even”

### Equivalence

Two statements are equivalent if each implies the other.

This is written:

```text
p <-> q
```

and read:

- “p if and only if q”
- or “p iff q”

To prove an iff statement, you must prove both directions:

- `p -> q`
- `q -> p`

## Converse, Inverse, and Contrapositive

Given:

```text
if p, then q
```

there are three closely related variants:

### Converse

```text
if q, then p
```

### Inverse

```text
if not p, then not q
```

### Contrapositive

```text
if not q, then not p
```

### The crucial fact

- a statement is equivalent to its contrapositive
- the converse and inverse are equivalent to each other
- but the converse is not generally equivalent to the original statement

This is one of the most important logic facts in early proof work.

## Proof by Contrapositive

Sometimes the direct form of a statement is awkward to prove, but the contrapositive is easy.

To prove:

```text
p -> q
```

you may instead prove:

```text
not q -> not p
```

because these are logically equivalent.

### Example

Claim:

> if `n^2` is odd, then `n` is odd

This can be hard to prove directly at first.

Instead prove the contrapositive:

> if `n` is even, then `n^2` is even

That is easy:

if `n = 2k`, then:

```text
n^2 = 4k^2 = 2(2k^2)
```

which is even.

So the contrapositive is true, and therefore the original statement is true.

## Proof by Contradiction

Proof by contradiction works like this:

1. assume the statement you want is false,
2. reason carefully from that assumption,
3. derive a contradiction,
4. conclude that the original statement must be true.

This is useful when direct proof is difficult or when the negation creates a rigid structure that can be shown impossible.

### Classic pattern

To prove:

> `sqrt(2)` is irrational

one assumes the opposite:

> `sqrt(2)` is rational

and then derives a contradiction about parity or common factors.

That contradiction shows the assumption must be false.

## Proof Style

Good mathematical proofs are:

- written in full sentences,
- logically structured,
- precise,
- explicit about assumptions,
- and convincing without hidden gaps.

Mathematical notation improves precision, but a proof is still an argument written in prose.

A proof should not be just a pile of equations with the reasoning left unstated.

## A Practical Decision Rule

When facing a new statement, it helps to ask:

1. Is this an implication?
2. If yes, is direct proof natural?
3. If not, is the contrapositive easier?
4. If the statement is existential, can I construct the object?
5. If direct construction is hard, is contradiction more natural?

This is often enough to select a proof strategy wisely.

## Common Mistakes

- Using examples as if they prove a universal statement.
- Ignoring the order of quantifiers.
- Confusing a statement with its converse.
- Claiming an iff statement is proved after showing only one direction.
- Writing equations without making the logical steps explicit.
- Using contradiction loosely without stating clearly what assumption is being negated.

## Why This Matters Beyond Pure Math

These proof techniques matter outside formal mathematics too.

They train habits that show up in:

- algorithm correctness arguments,
- impossibility results,
- complexity reasoning,
- invariant-based debugging,
- and precise systems thinking.

Even in engineering contexts, the same underlying skills matter:

- define the statement precisely,
- understand the logical form,
- choose an appropriate proof strategy,
- and make the reasoning explicit.
