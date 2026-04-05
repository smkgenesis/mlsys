# Relations and Functions

## What

A relation is a formal way to describe a connection between objects.

A function is a special kind of relation: each allowed input is associated with exactly one output.

This lecture matters because it turns earlier set ideas into a language for:

- structure,
- mappings,
- constraints,
- and computation.

## Why It Matters

Relations and functions appear everywhere in mathematics and computer science.

They underlie:

- graphs and edges,
- databases,
- state transitions,
- input-output behavior,
- probability random variables,
- and algorithms.

If sets tell us what collections exist, relations and functions tell us how objects are connected and transformed.

## Relations

A relation can be represented as a set of tuples.

For a binary relation between sets `A` and `B`, we write:

```text
R ⊆ A × B
```

This means:

- `R` is a subset of the Cartesian product `A × B`
- so each element of `R` is an ordered pair `(a, b)` with `a ∈ A` and `b ∈ B`

### Intuition

A relation answers the question:

```text
which pairs are related?
```

Examples from the lecture:

- student enrolled in course
- person born on date
- real numbers `x, y` satisfying `x^2 + y^2 = 1`

So a relation is not necessarily a formula. It is just a collection of ordered pairs that satisfy some rule.

## Binary Relations

A binary relation is a relation involving two positions.

If:

```text
R ⊆ A × B
```

then `R` is a binary relation from `A` to `B`.

Example:

```text
R = {(Aisha, Ec10), (Aisha, Cs20), (Ben, Cs20)}
```

This is a relation because it lists which student-course pairs are present.

The lecture also emphasizes that `A` and `B` do not need to be disjoint. For example:

```text
R = {(x, y) ∈ R × R : x^2 + y^2 = 1}
```

is a relation on real numbers.

## Inverse Relation

If:

```text
R ⊆ A × B
```

then the inverse relation is:

```text
R^(-1) ⊆ B × A
```

defined by:

```text
R^(-1) = {(y, x) : (x, y) ∈ R}
```

So the inverse relation just flips the order of every pair.

This is always valid for relations.

That point matters later because every function has an inverse relation, but the inverse relation is not always a function.

## Functions

A function `f: A -> B` is a special kind of binary relation.

It must satisfy:

```text
for every x ∈ A, there exists a unique y ∈ B such that (x, y) ∈ f
```

Formally:

```text
∀x ∈ A, ∃! y ∈ B such that (x, y) ∈ f
```

This means two things at once:

1. every input in the domain must have an output
2. that output must be unique

The unique output paired with `x` is written `f(x)`.

## Domain and Codomain

For a function:

```text
f: A -> B
```

- `A` is the domain
- `B` is the codomain

The domain is the set of allowed inputs.

The codomain is the set of allowed output targets.

This is important because the codomain is not always the same thing as the image.

## When a Relation Is Not a Function

The lecture shows several ways a relation can fail to be a function.

### Failure 1: missing output

If some `x ∈ A` is not paired with anything in `B`, the relation is not a function on `A`.

### Failure 2: multiple outputs

If some input `x` is paired with two different outputs, the relation is not a function.

This is the main structural rule:

```text
one input, exactly one output
```

## Partial Functions

The lecture briefly introduces the idea of a partial function.

A partial function is a relation that behaves like a function, but only on a subset of the intended domain.

Example:

```text
f(x) = 1/x
```

is not a function `R -> R`, because `f(0)` is undefined.

But it is a valid function on:

```text
R - {0}
```

So the real lesson is:

- whether something is a function depends on the chosen domain as well as the formula.

## Image

If `f: S -> T` and `A ⊆ S`, then the image of `A` under `f` is:

```text
f[A] = {f(x) : x ∈ A}
```

This is the set of actual outputs produced by elements of `A`.

If `A` is the whole domain, then `f[A]` is the image of the function.

The lecture emphasizes:

```text
image ⊆ codomain
```

The codomain is the allowed output set.
The image is the output set actually achieved.

### Examples

If:

```text
f: Z -> Z,   f(n) = n^2
```

then:

```text
f[Z] = {0, 1, 4, 9, ...}
```

which is a proper subset of `Z`.

If:

```text
g: Z -> Z,   g(n) = 2n
```

then:

```text
g[Z] = {all even integers}
```

## Injective Functions

A function is injective if different inputs always give different outputs.

Informally:

- no two distinct arguments map to the same value

Equivalent form:

```text
f(x1) = f(x2) -> x1 = x2
```

Injective means one-to-one.

The lecture notes an important consequence:

- if a function is injective, then its inverse relation becomes a function on the image

That is because each output in the image came from exactly one input.

## Surjective Functions

A function `f: A -> B` is surjective if every element of the codomain is actually hit by the function.

Formally:

```text
∀y ∈ B, ∃x ∈ A such that f(x) = y
```

So surjective means:

- no gaps in the codomain
- image equals codomain

This is also called onto.

## Bijective Functions

A function is bijective if it is both:

- injective
- surjective

So a bijection creates a perfect one-to-one correspondence between domain and codomain.

Equivalent form:

```text
∀y ∈ B, ∃! x ∈ A such that f(x) = y
```

Bijective functions are especially important because they have genuine inverse functions.

They also become the key way we compare sizes of sets, especially infinite sets.

## Inverse of a Function

Every function has an inverse relation.

But the inverse relation is a function only when the original function is injective.

If the original function is bijective, then:

- the inverse is a function,
- and it maps from the codomain back to the domain.

This is one of the main reasons injectivity matters.

## Finite-Set Consequences

The lecture gives several counting consequences when `A` and `B` are finite.

If `f: A -> B` is:

- injective, then `|A| <= |B|`
- surjective, then `|A| >= |B|`
- bijective, then `|A| = |B|`

These are very useful because they connect structural properties of functions to counting arguments.

They also foreshadow why bijections are the right definition of “same size” for infinite sets.

## Multi-Argument Functions

The lecture explains that a function with several arguments is still just a single-argument function whose domain is a Cartesian product.

Example:

```text
M: Z × Z -> Z
```

where:

```text
M(n, m) = n · m
```

This is a two-argument function, but formally it is just a function on ordered pairs.

More generally:

```text
f: X1 × X2 × ... × Xk -> Y
```

is a single function whose input is a `k`-tuple.

This is a very important conceptual simplification.

## How Relations and Functions Connect to Sets

This lecture really depends on the previous sets lecture.

The key bridge is:

- a relation is a subset of a Cartesian product
- a function is a relation with extra structure

So functions are not separate from sets. They are built from set ideas plus uniqueness and existence conditions.

## Common Mistakes

- Confusing codomain with image.
- Forgetting that a function must be defined for every input in its domain.
- Thinking every formula defines a function without checking the domain.
- Forgetting that every function has an inverse relation, but not every inverse relation is a function.
- Thinking injective automatically means surjective, or vice versa.
- Forgetting that a multi-argument function is formally a function on tuples.

## Why This Matters Beyond Pure Math

Relations and functions are central to computer science and later ML systems work.

They help formalize:

- mappings from inputs to outputs,
- database relationships,
- dependency structure,
- transformations on tensors or states,
- and domain restrictions that affect correctness.

This lecture is one of the first places where “mathematical object” starts looking a lot like “typed interface with constraints.”

## Short Takeaway

Relations are subsets of Cartesian products, and functions are the special relations that assign each input exactly one output; from there, injective, surjective, bijective, image, and inverse all describe different ways a mapping can behave.
