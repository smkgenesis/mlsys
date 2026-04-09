# 07. Structural Induction

## What

Structural induction is the proof technique for objects that are defined inductively.

Instead of proving a statement for all natural numbers:

- `0`
- `1`
- `2`
- ...

you prove a statement for all objects built from:

- base cases,
- and constructor rules.

This makes structural induction the natural proof method for recursively defined objects such as:

- strings,
- trees,
- expressions,
- programs,
- and many data structures used in computer science.

## Why It Matters

Ordinary and strong induction work on integers.

But computers manipulate many non-numeric objects. Those objects are often defined by rules like:

- start with simple objects,
- then build bigger ones from smaller ones.

That is exactly the setting where structural induction applies.

So this lecture is really about a broader idea:

```text
induction is not only about counting upward on numbers;
it is about reasoning over anything built recursively
```

## Inductive Definitions

The lecture begins with the idea of building objects inductively.

An inductive definition usually has three parts:

1. base cases
2. constructor rules
3. an exhaustion clause

### Base cases

These specify the simplest objects that are in the set.

### Constructor rules

These explain how to build new objects from objects already known to be in the set.

### Exhaustion clause

Nothing else is in the set except what follows from the base cases and constructor rules.

This matters because structural induction depends on the fact that every object in the set was built in one of those allowed ways.

## Natural Numbers as an Inductive Definition

The lecture reminds us that even natural numbers can be defined inductively:

- `0` is a natural number
- if `n` is a natural number, then `n + 1` is a natural number

This is useful because it shows ordinary induction is already a special case of structural induction.

## Structural Induction: General Idea

Suppose `X` is a set defined inductively.

To prove:

```text
∀x ∈ X, P(x)
```

you follow the structure of the definition itself.

### Base step

Show `P(x0)` for each base object `x0`.

### Inductive step

For each constructor, assume the property holds for the smaller objects used as inputs to that constructor, and prove it holds for the newly built object.

That is the key idea:

```text
prove the property is preserved by every way the objects can be built
```

## Structural Induction vs Numerical Induction

The shape is the same, but the indexing idea is different.

### Ordinary induction

You move from:

```text
P(k) to P(k + 1)
```

### Structural induction

You move from:

```text
property holds for smaller constituent objects
```

to:

```text
property holds for the object constructed from them
```

So structural induction follows construction rules, not numeric successor.

## Bit Strings as an Inductively Defined Set

The lecture uses bit strings over alphabet `Σ = {0,1}`.

The set `Σ*` of all finite bit strings is defined inductively:

- base case: the empty string `λ` is in `Σ*`
- constructor case: if `s ∈ Σ*` and `a ∈ Σ`, then `(a, s) ∈ Σ*`

The lecture represents strings recursively as nested pairs:

- `0 = (0, λ)`
- `10 = (1, (0, λ))`
- `0010 = (0, (0, (1, (0, λ))))`

This is a good example because it makes clear that a string is not just a flat object. It has recursive structure.

## Recursive Definitions of Operations

Once a data type is defined inductively, operations on it are often defined inductively too.

The lecture gives two examples:

- length
- concatenation

### Length

Define `|s|` recursively:

- base case: `|λ| = 0`
- constructor case: `|(a, s)| = 1 + |s|`

### Concatenation

Define `s · t` recursively:

- base case: `λ · t = t`
- constructor case: `(a, s) · t = (a, s · t)`

This is an important programming-level idea:

- recursively defined data types naturally lead to recursively defined operations

That is exactly how many functions on lists, trees, and syntax objects are defined in CS.

## Proving Properties of Recursive Operations

The lecture proves associativity of concatenation:

```text
(s · t) · u = s · (t · u)
```

The important lesson is not only that concatenation is associative.

The deeper lesson is:

- the proof is by structural induction on `s`
- because the recursive definition of concatenation recurses on `s`

That is a very useful heuristic:

```text
when an operation is defined recursively on one argument,
structural induction on that argument is often the right proof method
```

## General Schema

The lecture gives the general structural-induction schema.

Suppose a set `S` is inductively defined by:

- base elements `b`
- constructors `c(x1, ..., xk)`

To prove `P(x)` for all `x ∈ S`:

1. prove `P(b)` for each base element
2. for each constructor, assume `P(x1), ..., P(xk)` for the smaller inputs
3. prove `P(c(x1, ..., xk))`

This is the structural analogue of:

- base case,
- induction hypothesis,
- induction step.

## Balanced Strings of Parentheses

The lecture’s second major example is the set of balanced strings of parentheses, BSP.

It defines BSP inductively:

- base case: `λ` is a BSP
- constructor 1: if `x` is a BSP, then `(x)` is a BSP
- constructor 2: if `x` and `y` are BSPs, then `xy` is a BSP

This is a very important example because:

- the objects are not numbers,
- the constructors are not successor functions,
- and the proof method still works perfectly.

## First BSP Property: Equal Numbers of Left and Right Parentheses

The lecture proves that every BSP has the same number of `(` and `)`.

Why structural induction fits:

- the base string `λ` obviously has equal counts
- if `x` has equal counts, then `(x)` still has equal counts
- if `x` and `y` each have equal counts, then `xy` also has equal counts

This is the exact structural-induction pattern:

- verify each constructor preserves the property

The lecture also points out an important logical nuance:

- equal numbers of left and right parentheses is necessary,
- but not sufficient,

for a string to be balanced.

So a property can be true of all BSPs without characterizing BSPs completely.

## The Counting Rule

The lecture then introduces the stronger characterization:

- scan left to right
- add `1` for each `(`
- subtract `1` for each `)`
- the running total never goes negative
- and ends at `0`

This is the counting rule.

The theorem is:

```text
a string of parentheses is balanced iff it satisfies the counting rule
```

This is stronger than just “equal numbers of left and right parentheses.”

## Why the BSP Example Matters

This example is powerful because it shows structural induction doing two things:

1. proving properties of recursively defined objects
2. connecting a recursive definition with an external characterization

That second move is very important in CS.

We often define objects recursively, then prove they are equivalent to some semantic or operational condition.

## Structural Induction as a Programming Habit

This lecture is mathematically about proof, but it also maps directly to programming.

When a type is recursively defined, the natural way to:

- write functions on it,
- reason about those functions,
- and prove invariants,

is to follow the same recursive structure.

That is why structural induction matters so much in computer science.

It shows up naturally in:

- lists,
- trees,
- syntax trees,
- recursive evaluators,
- parsers,
- and functional programs.

## Common Pitfalls

- Forgetting the exhaustion clause and proving only for some objects, not all recursively generated ones.
- Proving the property only for one constructor but not all constructors.
- Using ordinary induction on length when the recursive structure itself is the real object.
- Confusing “necessary condition” with “necessary and sufficient condition.”
- Forgetting that recursive operations are often proved by induction on the argument they recurse on.

## When to Use Structural Induction

Structural induction is the right first tool when:

- the object is defined recursively
- the set of valid objects is generated from constructors
- the property you want to prove depends on how the object was built

If the object’s definition says “start from these simple things and build more using these rules,” that is usually the signal.

## Why This Matters Beyond Pure Math

Structural induction is one of the most practically transferable proof ideas from discrete mathematics into computer science.

It supports reasoning about:

- data types,
- recursive functions,
- program syntax,
- parsing,
- algebraic structures,
- and invariants of recursively built states.

For later systems work, the key transfer is not the parentheses example itself. It is the mindset:

- understand the constructor rules,
- define operations recursively,
- prove properties by following the same structure.

That pattern keeps showing up.

## Short Takeaway

Structural induction is induction on how an object is built: if a set of objects is defined by base cases and constructor rules, then to prove a property for all such objects, prove it for each base case and show every constructor preserves it.
