# 02. Basic Proof Techniques

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

## Worked Exercise Patterns from Chapter 2

The following exercise set has already been worked through from the Chapter 2 material.

### 2.1 Negative odd integers still satisfy the odd definition

- `-1` is odd because it can be written as `2k + 1` with `k = -1`.

Main lesson:

- definition checking comes before intuition about what “usually” counts as odd.

### 2.2 Inverse, converse, and contrapositive

For `P -> Q`:

- inverse: `not P -> not Q`
- converse: `Q -> P`
- contrapositive: `not Q -> not P`

Main lesson:

- these are fixed logical rewrites, not free paraphrases.

### 2.3 The product of two odd integers is odd

- write `p = 2m + 1` and `q = 2n + 1`
- then `pq = 2(2mn + m + n) + 1`

Main pattern:

- direct proof by substituting the definition and rewriting into the target form.

### 2.4 `sqrt(3)` is irrational

- assume `sqrt(3) = a/b` in lowest terms
- square to get `a^2 = 3b^2`
- conclude both `a` and `b` are divisible by `3`
- contradiction

Main pattern:

- irrationality by lowest-terms contradiction.

### 2.5 `cuberoot(2)` is irrational

- assume `cuberoot(2) = a/b` in lowest terms
- cube both sides: `a^3 = 2b^3`
- conclude `a` is even, then `b` is even
- contradiction

Main lesson:

- the irrationality template extends to higher roots by matching the power in the equation.

### 2.6 For any positive integer `n`, `sqrt(n)` is either an integer or irrational

- assume `sqrt(n) = a/b` in lowest terms
- square to get `a^2 = nb^2`
- deduce the denominator must be `1`

Main pattern:

- eliminate the middle case “rational but non-integer.”

### 2.7 Fair seven-sided die

- the argument is not a strict symbolic proof
- use symmetry and continuous deformation of a fair cube into a seven-faced shape

Main lesson:

- some early existence arguments rely on geometric intuition rather than exact algebra.

### 2.8 Even square numbers are divisible by 4

- if `n = 2k`, then `n^2 = 4k^2`

Main pattern:

- use the definition of even and factor out the needed divisor directly.

### 2.9 Perfect-square claims: proof versus counterexample

- `(a)` true: if `c = m^2` and `d = n^2`, then `cd = (mn)^2`
- `(b)` true: if `c = d`, then `cd = c^2`, so both are perfect squares
- `(c)` false: square roots lose sign information, so `c > d` does not force chosen integer roots `x > y`

Main lesson:

- when square roots appear, keep track of sign choices explicitly.

### 2.10 Contradiction proof for a parity implication

Claim:

- if `17n + 2` is odd, then `n` is odd

Worked method:

- assume `17n + 2` is odd and `n` is not odd
- since `n` is an integer, “not odd” means even
- derive that `17n + 2` is even, contradiction

Main lesson:

- contradiction and contrapositive can feel similar, but contradiction starts by assuming the premise and negation of the conclusion together.

### 2.11 Critiquing a bad proof

Faulty step:

- `x > y` does not imply `x^2 > y^2` for all real numbers

Counterexample:

- `x = -1`, `y = -2`

Main lesson:

- in proof critique, identify the first unjustified transformation, especially around inequalities.

### 2.12 Converse of the contrapositive

Starting from `p -> q`:

- contrapositive: `not q -> not p`
- converse of that: `not p -> not q`

This is the inverse.

Main pattern:

- compute logical relatives symbolically before translating back into words.

### 2.13 Writing claims with quantifiers and implications

Key translation habits:

- “every” becomes `forall`
- “there exists” becomes `exists`
- “distinct” becomes inequality such as `x != y`
- “even” should be written explicitly, for example `exists k (n = 2k)`

Main lesson:

- translation problems are about exact logical structure, not only mathematical meaning.

### 2.14 Nonconstructive existence of irrational `x, y` with `x^y` rational

Let:

- `a = sqrt(2)^sqrt(2)`

Then split into two cases:

- if `a` is rational, take `x = sqrt(2)`, `y = sqrt(2)`
- if `a` is irrational, take `x = a`, `y = sqrt(2)`, giving `x^y = 2`

Main pattern:

- prove existence by showing that every possible case yields a witness.

### 2.15 Reusing Chapter 1 inside a Chapter 2 proof

Once one person `X` is fixed in the six-person argument:

- each of the remaining five people is either known by `X`
- or not known by `X`

By the pigeonhole principle, one category contains at least three people.

Main lesson:

- later proof chapters still rely on earlier classification arguments.

## Chapter 2 Pattern Library

The recurring patterns in this chapter are:

- use definitions first for parity, squares, and implication forms
- choose deliberately between direct proof, contrapositive, and contradiction
- for irrationality, assume lowest terms and force a common factor
- for critique problems, locate the first invalid step rather than only the wrong conclusion
- for quantifier translation, make every hidden condition explicit
- for nonconstructive existence, split into cases and make each branch produce the desired object
