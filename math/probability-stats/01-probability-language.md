# 01. Probability Language

## What

Probability begins with a small set of basic ideas:
- random phenomenon,
- random experiment,
- sample space,
- sample point,
- event,
- operations on events,
- mutually exclusive events and partitions,
- probability as a numerical measure,
- classical, empirical, axiomatic, and geometric probability,
- and basic probability properties,
- conditional probability and independence,
- multiplication law, total probability, and Bayes' theorem,
- and replacement versus without replacement.

These ideas form the language used to describe uncertain outcomes before any probability is calculated.

## Why It Matters

Probability becomes confusing when formulas appear before the underlying objects are clear.

Before asking "what is the probability of this event?", it must be clear:
- what experiment is being performed,
- what outcomes are possible,
- and which outcomes belong to the event of interest.

This is the basic setup step for all later probability reasoning.

## Core Mechanism

### Probability and statistics

Probability studies uncertainty before the outcome is observed.
It asks what may happen and how likely different events are.

Statistics studies data after outcomes are observed.
It asks what the observed data suggests about the process that produced it.

In short:
- probability goes from model to outcome,
- statistics goes from outcomes back to conclusions.

### Random phenomenon

A random phenomenon is a phenomenon whose result is not known in advance.

Examples:
- tossing a coin,
- rolling a die,
- drawing a card,
- or observing rainfall tomorrow.

The key point is not that the phenomenon has no cause.
The point is that the outcome is uncertain before observation.

### Random experiment

A random experiment is the concrete trial, observation, or procedure that produces one outcome from a random phenomenon.

Examples:
- tossing one coin once,
- rolling one die once,
- drawing two balls from a bag.

The phenomenon is the uncertain situation.
The experiment is the actual trial that reveals one result.

### Sample space

The sample space is the set of all possible outcomes of a random experiment.

Examples:

```text
One coin toss: {H, T}
One die roll: {1, 2, 3, 4, 5, 6}
Two coin tosses: {HH, HT, TH, TT}
```

The sample space answers the question:
"What can possibly happen?"

For repeated experiments, sample spaces are often built from ordered tuples.

Examples:

```text
Two die rolls: {(1,1), (1,2), ..., (6,6)}
Three coin tosses: {HHH, HHT, HTH, HTT, THH, THT, TTH, TTT}
```

The ordering matters whenever the experiment distinguishes one trial from another.

That is why:

```text
(1,2) and (2,1)
```

are different outcomes when the first and second draws are different stages of the experiment.

### Sample point

A sample point is one individual outcome in the sample space.

For example, in:

```text
{HH, HT, TH, TT}
```

each of `HH`, `HT`, `TH`, and `TT` is a sample point.

### Event

An event is any subset of the sample space.

An event groups together all outcomes that satisfy some condition.

Example:

```text
Sample space: {HH, HT, TH, TT}
Event A = "at least one head" = {HH, HT, TH}
```

An event can contain:
- one sample point,
- many sample points,
- the entire sample space,
- or no sample points.

This “subset” viewpoint is one of the most important early moves in probability:

```text
probability is assigned to sets of outcomes, not just to individual outcomes
```

That is what makes event algebra possible.

### Common types of events

Elementary event:
- an event containing exactly one sample point.

Compound event:
- an event containing two or more sample points.

Certain event:
- the whole sample space.

Impossible event:
- the empty set.

Complement of an event:
- all outcomes in the sample space that are not in the event.

Mutually exclusive events:
- events that cannot happen at the same time because they do not overlap.

Exhaustive events:
- events that together cover the entire sample space.

Pairwise mutually exclusive events:
- a collection of events where every different pair has empty intersection.

This distinction matters because pairwise mutual exclusivity is exactly the condition used in countable additivity and in partitions of the sample space.

### Operations on events

Union:

```text
A ∪ B
```

means that `A` happens, or `B` happens, or both happen.

Intersection:

```text
A ∩ B
```

means that `A` and `B` happen together.

Complement:

```text
A^c
```

means that `A` does not happen.

Difference:

```text
A - B = A ∩ B^c
```

means that `A` happens but `B` does not.

Example with one die roll:

```text
Ω = {1, 2, 3, 4, 5, 6}
A = {2, 4, 6}
B = {4, 5, 6}
```

Then:

```text
A ∪ B = {2, 4, 5, 6}
A ∩ B = {4, 6}
A^c = {1, 3, 5}
A - B = {2}
```

These operations are just set operations applied to events.

This is why set fluency matters so much in early probability.
Most “probability manipulations” begin as set manipulations before any numbers are attached.

### Mutually exclusive events

Two events `A` and `B` are mutually exclusive if:

```text
A ∩ B = ∅
```

This means they cannot happen at the same time.

For a collection of events `A1, A2, ..., An`, they are pairwise mutually exclusive if every different pair has empty intersection.

Mutually exclusive means:
- no overlap.

### Partition of a sample space

A collection of events `A1, A2, ..., An` is a partition of the sample space if:
- the events are pairwise mutually exclusive,
- and their union is the whole sample space.

In other words, a partition breaks the sample space into separate pieces with:
- no overlap,
- and no gaps.

Example for one die roll:

```text
A1 = {1, 3, 5}
A2 = {2, 4, 6}
```

Then:

```text
A1 ∩ A2 = ∅
A1 ∪ A2 = Ω
```

So `{A1, A2}` is a partition of `Ω`.

Partitions matter because they give a structured way to break the whole probability space into non-overlapping cases.

That is why they reappear later in:

- total probability,
- Bayes' theorem,
- and case-based probabilistic reasoning in general.

### Probability of an event

The probability of an event `A` is a real number between `0` and `1` that represents how likely `A` is to happen.

Notation:

```text
P(A)
```

Interpretation:
- `P(A) = 0` means `A` is impossible,
- `P(A) = 1` means `A` is certain,
- values between `0` and `1` represent different degrees of likelihood.

### Classical probability

Classical probability applies when:
- the sample space is finite,
- and all sample points are equally likely.

If the sample space has `N` sample points and event `A` contains `n` of them, then:

```text
P(A) = n / N
```

This is the familiar rule:
- favorable outcomes divided by total outcomes.

Example:

```text
Ω = {1, 2, 3, 4, 5, 6}
A = {2, 4, 6}
P(A) = 3 / 6 = 1 / 2
```

This is often the first probability rule students learn, but it is less general than it looks.
It depends completely on the equal-likelihood assumption.

### Empirical probability

Empirical probability estimates probability from repeated trials.

If an experiment is repeated `N` times and event `A` occurs `n(A)` times, then the relative frequency of `A` is:

```text
n(A) / N
```

This is used as an estimate of `P(A)`.

As the number of trials becomes large, the relative frequency tends to become more stable.
This is the basic intuition behind the law of large numbers.

So empirical probability is not primarily about exact formulas.
It is about observed frequencies stabilizing through repetition.

### Axiomatic probability

Axiomatic probability is the formal mathematical foundation of probability.

It starts with a probability space:

```text
(Ω, F, P)
```

where:
- `Ω` is the sample space,
- `F` is the collection of events,
- `P` is a function assigning a real number to each event.

The function `P` is a probability measure when it satisfies three axioms:

1. Nonnegativity:

```text
0 <= P(A) <= 1
```

for every event `A`.

2. Normalization:

```text
P(Ω) = 1
```

3. Countable additivity:
for pairwise mutually exclusive events `A1, A2, ...`,

```text
P(A1 ∪ A2 ∪ ...) = P(A1) + P(A2) + ...
```

This framework is more general than classical or empirical probability.
Those are special settings that fit inside the axiomatic view.

This is the most durable definition mathematically.
It is the one that scales best beyond toy finite examples.

### Geometric probability

Geometric probability is used when the sample space is continuous and probability is defined by geometric proportion.

Typical forms are:

```text
P(A) = length(A) / length(Ω)
P(A) = area(A) / area(Ω)
P(A) = volume(A) / volume(Ω)
```

This is the continuous analogue of favorable size divided by total size.

It is useful when outcomes range over continuous regions rather than countable lists of points.

### Basic properties of probability

From the axioms, several standard rules follow.

Impossible event and certain event:

```text
P(empty set) = 0
P(Ω) = 1
```

Complement rule:

```text
P(A^c) = 1 - P(A)
```

Union rule:

```text
P(A ∪ B) = P(A) + P(B) - P(A ∩ B)
```

Union upper bound:

```text
P(A ∪ B) <= P(A) + P(B)
```

If `A` and `B` are mutually exclusive:

```text
P(A ∪ B) = P(A) + P(B)
```

If `A` is a subset of `B`:

```text
P(B - A) = P(B) - P(A)
P(A) <= P(B)
```

For three events:

```text
P(A ∪ B ∪ C)
= P(A) + P(B) + P(C)
- P(A ∩ B) - P(B ∩ C) - P(C ∩ A)
+ P(A ∩ B ∩ C)
```

The main intuition is:
- whole space has probability `1`,
- complement subtracts from `1`,
- and union requires correcting for overlap.

### Conditional probability

Conditional probability asks for the probability of an event after restricting attention to a condition that is already known to hold.

If `P(A) > 0`, then:

```text
P(B | A) = P(A ∩ B) / P(A)
```

This means:
- `A` becomes the new effective universe,
- and `A ∩ B` is the part of that universe where `B` also happens.

Conditional probability is not just a formula.
For a fixed event `A`, the mapping `B -> P(B | A)` behaves like a probability measure inside the conditioned world.

That is the important intuition:

```text
conditioning changes the effective universe from Ω to A
```

### Independence and dependence

Two events `A` and `B` are independent if knowing that one happened does not change the probability of the other.

When `P(A) > 0`, this means:

```text
P(B | A) = P(B)
```

An equivalent and more common test is:

```text
P(A ∩ B) = P(A) P(B)
```

If this fails, the events are dependent.

Important facts:
- if `A` and `B` are independent, then `A` and `B^c` are also independent,
- if `P(A) > 0` and `P(B) > 0` and the events are mutually exclusive, then they are dependent,
- pairwise independence is weaker than mutual independence for three or more events.

For events `A1, A2, ..., An`:
- pairwise independence means every pair satisfies the product rule,
- mutual independence means every subset satisfies the product rule.

This is one of the most common places to be sloppy.
Pairwise independence is not the same thing as full mutual independence.

### Multiplication law

The multiplication law computes the probability that several events all happen together.

For two events:

```text
P(A1 ∩ A2) = P(A1) P(A2 | A1)
```

For three events:

```text
P(A1 ∩ A2 ∩ A3) = P(A1) P(A2 | A1) P(A3 | A1 ∩ A2)
```

In general:

```text
P(A1 ∩ A2 ∩ ... ∩ An)
= P(A1) P(A2 | A1) P(A3 | A1 ∩ A2) ... P(An | A1 ∩ ... ∩ A(n-1))
```

This is the chain rule for joint probability.

If the events are independent, the conditional probabilities reduce to ordinary probabilities, so the formula becomes the usual product rule.

### Formula of total probability

Suppose `A1, A2, ..., An` form a partition of the sample space and each has positive probability.

Then for any event `B`:

```text
P(B) = Σ P(Ai) P(B | Ai)
```

This works because `B` can be split into disjoint pieces:

```text
(A1 ∩ B), (A2 ∩ B), ..., (An ∩ B)
```

Each term is the contribution of one case `Ai` to the overall probability of `B`.

So total probability is a weighted sum of conditional probabilities across a partition.

### Bayes' theorem

Bayes' theorem reverses the direction of conditioning.

If `A1, A2, ..., An` form a partition, `P(Ai) > 0`, and `P(B) > 0`, then:

```text
P(Ai | B) = P(Ai ∩ B) / P(B)
          = P(Ai) P(B | Ai) / Σ P(Aj) P(B | Aj)
```

Bayes' theorem updates the probability of a cause after observing evidence.

The main terms are:
- `P(Ai)`: prior probability,
- `P(B | Ai)`: likelihood,
- `P(B)`: total probability or evidence,
- `P(Ai | B)`: posterior probability.

The core intuition is:
- start with the prior,
- weight it by how well it explains the observed evidence,
- and normalize by the total chance of the evidence.

### With replacement and without replacement

With replacement means that after drawing an item, it is returned before the next draw.
Without replacement means that once an item is drawn, it is not returned before the next draw.

This changes the sample space and therefore changes the probabilities.

Example with balls `{1, 2, 3}` and two draws:

With replacement:

```text
(1,1), (1,2), (2,1), (3,3) ...
```

are all possible.

Without replacement:

```text
(1,1), (2,2), (3,3)
```

are impossible.

The important difference is:
- with replacement, the set resets each time,
- without replacement, the set changes after each draw.

This is also why with replacement often leads to independent draws, while without replacement makes draws dependent.

### A practical setup habit

Early probability problems become much easier if they are set up in this order:

1. define the experiment clearly
2. write the sample space explicitly when possible
3. define the event as a subset of that sample space
4. decide whether outcomes are equally likely
5. only then choose the probability rule

This sounds basic, but most early mistakes come from skipping step 2 or 3.

## Tradeoffs

The main difficulty in early probability is not usually arithmetic.
It is describing the experiment correctly.

A sample space that is too vague or incomplete leads to bad reasoning later.
A careful setup makes later probability calculations straightforward.

Another important tradeoff is between intuitive simplicity and mathematical generality:

- classical probability is easy to use but assumes equally likely outcomes,
- empirical probability connects to observed data but is only approximate in finite samples,
- axiomatic probability is more abstract but handles the broadest range of settings cleanly.

Learning early probability well means keeping those levels distinct rather than treating them as interchangeable.

## Common Mistakes

- Mixing up a random phenomenon with the experiment used to observe it.
- Forgetting that a sample space must include all possible outcomes.
- Treating an event as a single outcome instead of a set of outcomes.
- Confusing union with intersection, especially reading "or" as if it excluded the overlap.
- Forgetting that `A - B` means "in A but not in B."
- Confusing mutually exclusive events with events that are merely different.
- Applying classical probability when outcomes are not equally likely.
- Treating empirical probability from a small number of trials as if it were exact.
- Memorizing probability formulas without seeing that they come from the axioms and the event structure.
- Confusing mutually exclusive events with independent events.
- Forgetting that conditional probability changes the effective sample space.
- Using Bayes' theorem mechanically without identifying the partition, prior, likelihood, and evidence clearly.
- Ignoring how replacement changes the sample space.
- Using probability formulas before the underlying experiment and event are clearly defined.

## Why This Matters for ML Systems

This topic is mathematical groundwork rather than a direct ML systems topic, but it supports later systems-relevant reasoning.

ML systems work often depends on understanding:
- randomness in sampling,
- uncertainty in measurements,
- repeated trials and observed outcomes,
- and the difference between a model of a process and the data produced by that process.

The immediate lesson is simple:

Probability starts by defining the experiment, the possible outcomes, and the event of interest.

The broader lesson is also important:

Clear reasoning about uncertainty depends on defining the underlying objects precisely before doing any calculation.

## Short Takeaway

Probability starts with clear setup: define the experiment, enumerate the possible outcomes, represent events as subsets of the sample space, and only then attach probability using the appropriate framework. The deepest early lesson is that most probability formulas are consequences of careful event structure, not shortcuts that can be used before the underlying model is defined.
