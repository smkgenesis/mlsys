# Sets

## What

A set is an unordered collection of distinct objects.

This definition has two important parts:

- unordered: the order of the elements does not matter
- distinct: duplicates do not matter

So:

```text
{2, 5, 7} = {7, 2, 5}
```

and:

```text
{83, 90, 90, 100, 100} = {83, 90, 100}
```

Sets are one of the basic languages of discrete mathematics. They let us talk clearly about collections, membership, structure, and relations between objects.

## Why It Matters

A large part of discrete mathematics is really about translating informal claims into:

- sets,
- membership,
- subset relations,
- and operations on collections.

Later topics in computer science use this constantly:

- domains and codomains of functions,
- graphs and relations,
- state spaces,
- event sets in probability,
- and data collections in algorithms.

If you read set notation comfortably, many later ideas become much less mysterious.

## Basic Notation

Some standard sets from the lecture:

- `N`: the nonnegative integers
- `Z`: the integers
- `Q`: the rational numbers
- `R`: the real numbers
- `∅`: the empty set

Membership notation:

- `a ∈ A` means `a` is an element of `A`
- `a ∉ A` means `a` is not an element of `A`

Examples:

- `2 ∈ {2, 5, 7}` is true
- `3 ∉ {2, 5, 7}` is true

## Sets vs Elements

One of the lecture’s most important warnings is:

- an object is not the same thing as the set containing that object

Examples:

- `1 ≠ {1}`
- `0 ≠ ∅`
- `∅ ≠ {∅}`

These look similar at first, but they are different kinds of objects.

### Why this matters

The symbols `∈` and `⊆` mean different things:

- `∈` asks whether something is an element of a set
- `⊆` asks whether one set is contained inside another set

So:

- `1 ∈ {1, 2}` is true
- `1 ⊆ {1, 2}` is false

because `1` is a number, not a set.

This is one of the most common early mistakes in discrete math.

## Subsets

`A ⊆ B` means every element of `A` is also an element of `B`.

Formally:

```text
A ⊆ B  iff  ∀x (x ∈ A -> x ∈ B)
```

So subset is really a universal implication statement.

Examples:

- `∅ ⊆ N`
- `N ⊆ Z`
- `{1} ⊆ {1, 2, 3}`

### Proper subset

`A ⊊ B` means:

- `A ⊆ B`
- and `A ≠ B`

So `A` is contained in `B`, but is strictly smaller.

## Equality of Sets

Two sets are equal when they contain exactly the same elements.

The standard proof method is:

```text
A = B  iff  A ⊆ B and B ⊆ A
```

This is called double inclusion.

It is one of the main proof patterns for set identities.

## Cardinality

`|S|` means the size of the set `S`, also called its cardinality.

Examples:

- `|∅| = 0`
- `|{3, 17}| = 2`
- `|{{1}, {2, 3, 4}}| = 2`

That last example is worth noticing:

- the set has two elements,
- even though one of those elements is itself a set with three elements.

Cardinality counts top-level elements, not everything nested inside.

## Finite and Infinite Sets

A set is finite if it has some finite cardinality.

A set is infinite if it is not finite.

The lecture also emphasizes an important point:

- not all infinite sets have the same size

Examples:

- integers and even integers have the same size
- integers and real numbers do not

This is one of the first hints that infinity in mathematics has internal structure.

## Power Set

The power set of `A`, written `P(A)`, is the set of all subsets of `A`.

Example:

If:

```text
A = {3, 17}
```

then:

```text
P(A) = {∅, {3}, {17}, {3, 17}}
```

The lecture asks the key question:

```text
if |A| = n, what is |P(A)|?
```

The answer is:

```text
|P(A)| = 2^n
```

because each element of `A` can either be:

- included in a subset,
- or not included.

That creates two independent choices per element.

## Set-Builder Notation

Instead of listing every element, we can define a set by a property.

Examples:

```text
{n ∈ Z : n is even}
{n ∈ Z : n = 2m for some m ∈ Z}
{2m : m ∈ Z}
```

All of these describe the even integers.

General pattern:

```text
{x ∈ A : P(x)}
```

Meaning:

- all elements `x` drawn from `A`
- such that predicate `P(x)` is true

This notation matters because it turns English descriptions into formal mathematical objects.

## Complement

The complement of `A`, written `Ā` or sometimes `A^c`, depends on a universe `U`.

Definition:

```text
Ā = {x ∈ U : x ∉ A}
```

So complement always means:

- everything in the universe,
- except the elements in `A`

This is why the universe must be understood. Without a universe, complement is ambiguous.

## Union

The union of `A` and `B`, written `A ∪ B`, contains everything that is in at least one of the two sets.

Definition:

```text
A ∪ B = {x : x ∈ A or x ∈ B}
```

Properties:

- commutative: `A ∪ B = B ∪ A`
- associative: `(A ∪ B) ∪ C = A ∪ (B ∪ C)`

Union behaves like “or.”

## Intersection

The intersection of `A` and `B`, written `A ∩ B`, contains everything that is in both sets.

Definition:

```text
A ∩ B = {x : x ∈ A and x ∈ B}
```

Properties:

- commutative: `A ∩ B = B ∩ A`
- associative: `(A ∩ B) ∩ C = A ∩ (B ∩ C)`

Intersection behaves like “and.”

## Difference

The difference `A - B` means:

- elements in `A`
- that are not in `B`

Definition:

```text
A - B = {x : x ∈ A and x ∉ B}
```

Equivalently:

```text
A - B = A ∩ B̄
```

Unlike union and intersection, difference is:

- not commutative
- not associative

So you need to treat it more carefully.

## Distributive Laws

Set operations satisfy analogues of algebraic distributive laws:

```text
A ∩ (B ∪ C) = (A ∩ B) ∪ (A ∩ C)
A ∪ (B ∩ C) = (A ∪ B) ∩ (A ∪ C)
```

These are important because they let you rewrite and simplify set expressions systematically.

## How to Prove Set Equalities

The lecture gives the standard method:

To prove `X = Y`, show:

1. `X ⊆ Y`
2. `Y ⊆ X`

This is the right proof template for many set identities.

For example, to prove:

```text
A ∩ (B ∪ C) = (A ∩ B) ∪ (A ∩ C)
```

you:

- start with an arbitrary element `x` in the left-hand side,
- show it must be in the right-hand side,
- then reverse the direction.

This is a very important habit:

- prove set equality through membership,
- not by saying “the Venn diagram looks right.”

The diagram helps intuition, but the proof comes from element-wise reasoning.

## The Universe

The lecture briefly emphasizes the universe `U`.

This is the ambient set of things that are allowed to be discussed.

It matters especially for:

- complements,
- set-builder notation,
- and statements that would otherwise be too vague.

The same subset description can mean different things if the universe changes.

## Ordered Pairs and Tuples

Sets are unordered, but some mathematical objects need order.

An ordered pair `(x, y)` stores:

- first component `x`
- second component `y`

with order mattering.

So in general:

```text
(x, y) ≠ (y, x)
```

And:

```text
(x, y) = (z, w)  iff  x = z and y = w
```

This extends to tuples:

- ordered triples,
- ordered `n`-tuples,
- and coordinate-like structures.

This is a crucial shift:

- sets forget order,
- tuples preserve order.

## Cartesian Product

The Cartesian product `A × B` is the set of all ordered pairs whose:

- first component comes from `A`
- second component comes from `B`

Definition:

```text
A × B = {(x, y) : x ∈ A, y ∈ B}
```

Example:

If:

```text
A = {1, 2, 3}
B = {-1, -2}
```

then `A × B` contains:

- `(1, -1)`
- `(1, -2)`
- `(2, -1)`
- `(2, -2)`
- `(3, -1)`
- `(3, -2)`

Important facts:

- `A × B` is generally different from `B × A`
- if `A` and `B` are finite, then:

```text
|A × B| = |A| · |B|
```

This is one of the simplest and most useful counting principles.

## Common Mistakes

- Confusing an element with the set containing that element.
- Confusing `∈` with `⊆`.
- Forgetting that sets ignore order and duplicates.
- Forgetting that complement depends on the universe.
- Treating ordered pairs like sets.
- Forgetting that `A × B` and `B × A` are usually different.
- Proving set identities only by picture instead of by membership reasoning.

## Why This Matters Beyond Pure Math

Sets are not just a discrete-math topic. They are one of the base languages of mathematical and computational reasoning.

You will see them underneath:

- functions and relations,
- events in probability,
- graphs,
- databases,
- type-like collections,
- and many algorithmic definitions.

For later ML systems work, sets support clean thinking about:

- state spaces,
- event definitions,
- structured collections,
- and precise input/output domains.

## Short Takeaway

Sets are unordered collections of distinct objects, and most of the important reasoning around them comes from four ideas: membership, subset, set operations, and the fact that equality of sets is proved by showing mutual inclusion.
