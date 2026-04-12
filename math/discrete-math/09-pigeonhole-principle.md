# 09. Pigeonhole Principle

## What

The pigeonhole principle is a counting argument:

- if more objects are assigned to fewer buckets,
- then at least one bucket must contain multiple objects.

In formal terms:

If `f: X -> Y` is a function between finite sets and `|X| > |Y|`, then there exist distinct elements `x1, x2 in X` such that:

```text
f(x1) = f(x2)
```

So two different inputs must land in the same output bucket.

## Why It Matters

The pigeonhole principle is one of the first important tools in discrete mathematics because it teaches several core habits at once:

- proof is different from experiment or intuition,
- generalization turns examples into reusable laws,
- and abstraction lets concrete situations be rewritten as sets, functions, and cardinalities.

Even though the principle is simple, it is powerful because many problems reduce to:

- identifying the objects,
- identifying the buckets,
- and comparing the counts.

## Proof Mindset Before the Principle

The principle is usually taught early not because it is technically difficult, but because it demonstrates what mathematical reasoning is supposed to look like.

Testing examples is not proof.

Statements such as:

- “it seems always true,”
- “it worked on all cases I checked,”
- or “that sounds obvious”

are not enough.

A mathematical statement becomes reliable when it follows from a precise argument.

The pigeonhole principle is one of the cleanest early examples of that standard.

## Generalization Comes First

Before the principle itself, the key intellectual move is generalization.

Example:

- “there is no triangle with side lengths 1, 2, and 6”

is much weaker than:

- “there is no triangle with side lengths `a, b, c` if `a + b <= c`”

The second statement is more useful because it:

- explains the first one,
- applies to infinitely many cases,
- and removes irrelevant detail.

This is exactly the same move used in the birthday example that motivates the pigeonhole principle:

- remove names,
- replace them with sets,
- and replace the story with a mapping.

## The Core Abstraction

To use the pigeonhole principle well, you need three ingredients:

### 1. Objects

These are the things being distributed.

Examples:

- people,
- integers,
- socks,
- students,
- inputs to a hash function.

### 2. Buckets

These are the categories or containers the objects fall into.

Examples:

- days of the week,
- possible remainders modulo 7,
- drawers,
- months,
- hash table slots.

### 3. Mapping

This is the rule that assigns each object to a bucket.

Examples:

- person -> birth weekday,
- integer -> remainder mod 7,
- sock -> color,
- key -> hash bucket.

Once these are identified, the problem usually becomes a cardinality comparison.

## The Basic Principle

The ordinary pigeonhole principle says:

> if there are more objects than buckets, some bucket contains at least two objects.

This is often expressed informally as:

- more pigeons than pigeonholes,
- every pigeon goes into a hole,
- therefore some hole contains at least two pigeons.

### Why it is true

Assume the opposite:

- each bucket contains at most one object.

Then the total number of objects can be at most the number of buckets.

But if the number of objects is strictly larger than the number of buckets, that is impossible.

So some bucket must contain at least two objects.

This is a counting contradiction.

## The Extended Pigeonhole Principle

The stronger form says:

If `|X| > k|Y|`, then some bucket must contain at least `k + 1` objects.

This is the natural extension of the same logic.

Why?

If every bucket had at most `k` objects, then the total number of objects would be at most:

```text
k|Y|
```

So if the total exceeds that amount, at least one bucket must contain `k + 1` or more objects.

The ordinary principle is the special case `k = 1`.

## Canonical Examples

### Birthdays by weekday

In any group of 8 people, at least two were born on the same day of the week.

- objects: 8 people
- buckets: 7 weekdays
- mapping: each person -> birth weekday

Since `8 > 7`, two people must land in the same weekday bucket.

### Same remainder modulo 7

Among any 8 integers, at least two have the same remainder when divided by 7.

- objects: 8 integers
- buckets: 7 possible remainders `{0,1,2,3,4,5,6}`
- mapping: each integer -> its remainder mod 7

Since `8 > 7`, two integers must share a remainder.

These examples look different, but they have the same structure.

## The Problem-Solving Template

For pigeonhole problems, the practical solving method is:

1. identify the objects,
2. identify the buckets,
3. define the mapping explicitly,
4. compare the counts,
5. apply the ordinary or extended pigeonhole principle,
6. translate the conclusion back into the original language.

This is the main skill.

Most of the difficulty is not in the theorem itself. It is in choosing the right objects and buckets.

## Common Mistakes

### Memorizing the story instead of the structure

Many people remember pigeons and holes but do not learn how to identify:

- objects,
- buckets,
- and the mapping

in a new problem.

That prevents transfer.

### Applying the principle without checking the numbers

Not every problem that “feels like” pigeonhole actually works.

Example:

- among 25 students, it is **not** guaranteed that 4 have last names starting with the same letter.

Why not?

There are 26 letters, so 25 students could all start with different letters.

The correct extended-pigeonhole threshold for forcing 4 students into one letter bucket would be:

```text
26 * 3 + 1 = 79
```

### Choosing bad buckets

If the buckets are too coarse or too fine, the principle may become useless or false.

A lot of pigeonhole reasoning is representation choice.

## Tradeoffs and Limits

The pigeonhole principle is powerful but blunt.

It tells you that a collision or concentration exists, but often not:

- where it is,
- how many total such collisions exist,
- or how to construct an explicit example efficiently.

So it is often best used as:

- an existence argument,
- a lower-bound argument,
- or a first structural observation.

## Worked Exercise Patterns from Chapter 1

The following exercise set has already been worked through from the Chapter 1 material.

### 1.1 Basic notation and number facts

Covered:

- cardinality `|X|`
- floor and ceiling
- divisors
- prime divisors

Main pattern:

```text
decode notation first;
then compute the concrete value carefully.
```

### 1.2 Largest prime divisor is not monotone

Main lesson:

- larger integer does not imply larger largest-prime-divisor

Main pattern:

- small counterexample construction

### 1.3 Floor and ceiling relation

Main lesson:

- `floor(x) = ceiling(x) - 1` exactly when `x` is not an integer

Main pattern:

- split into integer versus non-integer cases

### 1.4 Pigeons moving on a sphere/grid-style parity problem

Main lesson:

- coloring/parity can create bucket imbalance

Main pattern:

- put two points on the boundary
- use a two-color or two-region argument
- then apply pigeonhole

### 1.5 Same number of friends

Main lesson:

- possible degree values appear to be `0` through `n-1`,
- but the extremes `0` and `n-1` cannot coexist

Main pattern:

- eliminate impossible extreme combination
- then apply pigeonhole to degree counts

### 1.6 Four points on a sphere and a closed hemisphere

Main lesson:

- great-circle boundary points belong to both closed hemispheres

Main pattern:

- put two chosen points on the boundary
- distribute the remaining points between the two hemispheres
- force two into one side by pigeonhole

### 1.7 Three birthdays in the same month

Main pattern:

- if every month had at most two people, total would be at most `24`
- one more person forces a month with at least three

### 1.8 One denomination appears at least 100 times

Main pattern:

- extended pigeonhole threshold

\[
(k-1)m + 1
\]

with:

- `m = 6` denominations
- `k = 100`

### 1.9 Same class and same shirt color

Main lesson:

- sometimes the right buckets are combinations, not single features

Main pattern:

- use `(class, color)` as the bucket
- total buckets = `8 * 3 = 24`

### 1.10 Four integers between 1 and 60

Main lesson:

- interval partitioning can be the cleanest pigeonhole move

Main pattern:

- split `1..60` into three blocks of length 20
- four integers force two into one block
- same block means difference at most 19

### 1.11 Product of first k primes plus 1

Main lesson:

- Euclid-style construction is not always prime

Main pattern:

- if the number is composite, its prime factors are new
- but finding the first example is basically example search

Known example:

\[
2 \cdot 3 \cdot 5 \cdot 7 \cdot 11 + 1 = 2311 = 13 \cdot 179
\]

### 1.12 Same prime factors less than or equal to 5

Main lesson:

- divisibility by `2, 3, 5` defines an 8-type signature

Main pattern:

- classify each integer by a subset of `{2,3,5}`
- there are `2^3 = 8` possible signatures
- 9 integers force a repeated signature

### 1.13 Hash collisions

Main lesson:

- collision problems reduce to occupancy problems

Main pattern:

- strings are objects
- hash values are buckets
- largest bucket minimum size is `ceil(m/p)`
- collisions in a bucket = occupancy minus 1

So for this chapter, the recurring pattern library already includes:

- direct bucket counting
- extended thresholds
- interval partitioning
- composite buckets such as ordered pairs
- divisibility signatures
- extremal impossibility arguments
- and geometry/parity reductions into bucket language

## Why This Matters Beyond Pure Math

The pigeonhole principle is not just a classroom theorem.

It appears throughout computer science in forms such as:

- hashing collisions,
- finite-state repetition,
- memory or register allocation pressure,
- duplicate detection,
- counting arguments in algorithms,
- lower bounds and impossibility arguments.

Its deeper value is that it trains the habit of turning concrete cases into formal mappings between finite sets.

That habit is foundational for discrete reasoning more broadly.
