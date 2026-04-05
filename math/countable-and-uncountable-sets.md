# Countable and Uncountable Sets

## What

This topic extends the idea of size from finite sets to infinite sets.

For finite sets, size is just the number of elements.

For infinite sets, the lecture uses a different idea:

```text
two sets have the same size if there exists a bijection between them
```

That is the key move.

Once we define size through bijections, we can distinguish:

- countably infinite sets,
- countable sets,
- and uncountable sets.

## Why It Matters

This lecture is one of the first places where infinity stops being a vague word and becomes a structured mathematical object.

It matters because it teaches that:

- some infinite sets are the same size as `N`,
- some are strictly larger,
- and diagonalization can prove that no listing of all elements is possible.

That idea later connects directly to:

- computability,
- representability,
- and limits of algorithms.

## Bijection as the Definition of Same Size

The lecture starts by revisiting bijections.

A bijection is a function that is:

- injective,
- and surjective.

So it pairs every element of one set with exactly one element of another set, with nothing left over on either side.

For both finite and infinite sets, we say:

```text
|A| = |B|   iff   there exists a bijection A -> B
```

This is the foundation for the whole lecture.

## Hilbert Hotel

Hilbert Hotel is the intuition-building example.

Imagine a hotel with rooms:

```text
0, 1, 2, 3, ...
```

and all rooms are occupied.

At first that sounds full. But because the room numbers form an infinite set, the hotel can still accept more guests.

### One extra guest

Move each guest from room `n` to room `n + 1`.

Then room `0` becomes free.

### k extra guests

Move each guest from room `n` to room `n + k`.

Then rooms `0, 1, ..., k - 1` become free.

### Two full infinite hotels into one

Send one hotel to even-numbered rooms:

```text
n -> 2n
```

and the other to odd-numbered rooms:

```text
n -> 2n + 1
```

This works because `N` has a bijection with proper subsets of itself.

That is one of the first strange but important facts about infinite sets.

## Countably Infinite Sets

A set `S` is countably infinite if there is a bijection:

```text
f: N -> S
```

This means we can list its elements as:

```text
f(0), f(1), f(2), ...
```

without missing any element.

So “countably infinite” really means:

```text
its elements can be enumerated in an infinite sequence
```

All countably infinite sets have the same size as `N`.

That size is written:

```text
aleph_0
```

## Countable Sets

A set is countable if it is either:

- finite,
- or countably infinite.

A set is uncountable if it is not countable.

So uncountable means:

```text
there is no way to list all its elements in a sequence indexed by N
```

## Why Z Is Countably Infinite

The integers are not listed in the natural order if we want to reach every element.

If we start:

```text
0, 1, 2, 3, ...
```

we never get to the negative integers.

So the lecture uses an alternating enumeration:

```text
0, 1, -1, 2, -2, 3, -3, ...
```

This defines a bijection from `N` to `Z`.

That is the important lesson:

- countability is not about the familiar order,
- it is about whether some complete listing exists.

## Why N × N Is Countably Infinite

The lecture then counts ordered pairs of natural numbers.

A naive plan like:

- list all pairs starting with `0`,
- then all pairs starting with `1`,

fails, because you would never finish the first row.

Instead, list pairs by increasing value of:

```text
x + y
```

So the order begins:

```text
(0,0)
(0,1), (1,0)
(0,2), (1,1), (2,0)
...
```

This diagonal sweep shows that every pair eventually appears.

So `N × N` is also countably infinite.

This is a very important pattern:

- if you can arrange an infinite family into a systematic traversal that reaches every element in finite time, the set is countable.

## Countable Unions

The lecture notes an important theorem:

```text
the union of countably many countable sets is countable
```

This fits the same intuition as the `N × N` diagonal argument.

You can think of:

- one countable set per row,
- and then sweep through the rows diagonally.

This theorem is very useful because many later countability proofs reduce a set to a countable union of simpler countable pieces.

## Uncountable Sets

The lecture’s main uncountability example is:

```text
P(N)
```

the power set of the natural numbers.

To show this is uncountable, we must prove:

```text
there is no bijection N -> P(N)
```

This is where diagonalization enters.

## Characteristic Functions

Each subset `S ⊆ N` can be represented by an infinite bit string through its characteristic function:

```text
χ_S(n) = 1 if n ∈ S
χ_S(n) = 0 if n ∉ S
```

So every subset of `N` corresponds to an infinite binary sequence.

Example:

- `{1, 2, 3}` corresponds to `0111000...`
- even numbers correspond to `101010...`

This representation makes the diagonal argument much easier to visualize.

## Diagonalization

The proof structure is:

1. assume `P(N)` is countable
2. then there is a bijection `b: N -> P(N)`
3. list the subsets as `S0, S1, S2, ...`
4. construct a new set `D` that differs from each `Si` at least at position `i`
5. conclude `D` cannot be in the list
6. contradiction

### Construction of D

Define:

```text
D = { i ∈ N : i ∉ Si }
```

So at the `i`-th position, `D` does the opposite of `Si`.

That guarantees:

- `D` differs from `S0` at index `0`
- `D` differs from `S1` at index `1`
- `D` differs from `S2` at index `2`
- and so on

Therefore `D` is not equal to any listed set `Si`.

But if the list were a bijection onto `P(N)`, then every subset of `N` should appear somewhere in the list.

That contradiction proves:

```text
P(N) is uncountable
```

## Why the Contradiction Is So Sharp

The lecture writes the contradiction in the classic form:

If `D = Sd`, then:

```text
d ∈ D   iff   d ∉ Sd
```

but since `D = Sd`, this becomes:

```text
d ∈ D   iff   d ∉ D
```

which is impossible.

This is a very elegant contradiction because it comes directly from how `D` was constructed.

## Comparing Sizes of Sets

The lecture then gives the general comparison language.

We say:

```text
|A| = |B|
```

if there is a bijection between `A` and `B`.

We say:

```text
|A| < |B|
```

if:

- there is an injection `A -> B`,
- but no bijection between `A` and `B`.

Similarly:

```text
|A| > |B|
```

if:

- there is an injection `B -> A`,
- but no bijection between `A` and `B`.

For example:

```text
|N| < |P(N)|
```

because:

- there is an injection `n -> {n}`
- but diagonalization proves there is no bijection

## More General Theorem: Cantor’s Theorem

The lecture hints at the general statement:

```text
|P(A)| > |A|   for every set A
```

This is one of the most important theorems about infinite sets.

It says:

- the power set is always strictly larger than the original set

So there is no “largest” infinity in this sense, because from any set you can build a strictly larger one by taking its power set.

## Finite Subsets of N Are Countable

The lecture also mentions:

```text
the set of all finite subsets of N is countable
```

The intuition is that a finite subset corresponds to a bit string with only finitely many `1`s.

Such strings can be organized by:

- length of the nonzero prefix,
- then lexicographic order.

This makes them listable, hence countable.

This is a good example of turning a complicated-looking set into a structured encoding that can be enumerated.

## Countability and Computers

One of the best parts of the lecture is the connection to computability.

The key idea is:

- every algorithm must be written with finitely many symbols from a finite alphabet

So the set of all possible algorithms is countable.

But the set of all functions:

```text
N -> {0,1}
```

is uncountable.

Therefore:

```text
not all functions are computable
```

This is a huge conceptual result.

It means the space of possible input-output behaviors is strictly larger than the space of all algorithms.

So some well-defined functions cannot be computed by any program.

## Halting Problem Connection

The lecture closes by pointing to the halting problem.

This is not the same proof as the power-set diagonal argument, but it uses a similar self-reference / contradiction pattern.

The big message is:

- diagonalization is not just a set-theory trick
- it becomes a method for proving limits of computation

That makes this lecture especially important for theoretical computer science.

## Common Mistakes

- Thinking “infinite” means “all the same size.”
- Believing a set is uncountable just because it looks complicated.
- Confusing “hard to list” with “impossible to list.”
- Forgetting that countability means existence of some enumeration, not the obvious enumeration.
- Mixing up image, injection, and bijection in size comparisons.
- Missing the logic of diagonalization: the new object is built specifically to differ from every listed object.

## Why This Matters Beyond Pure Math

This lecture is foundational for:

- computability,
- complexity intuition,
- information representation,
- and limits of what algorithms can cover.

For later ML systems thinking, the main transfer is philosophical and structural:

- formal systems have representational limits,
- not every conceivable mapping is realizable by finite machinery,
- and encoding arguments matter.

That perspective becomes surprisingly valuable later.

## Short Takeaway

Countably infinite sets are the infinite sets that can be listed by natural numbers; uncountable sets cannot be fully enumerated, and diagonalization proves that power sets like `P(N)` are strictly larger than `N`.
