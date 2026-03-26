# Basic Probability Language

## What

Probability begins with a small set of basic ideas:
- random phenomenon,
- random experiment,
- sample space,
- sample point,
- event,
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

## Tradeoffs

The main difficulty in early probability is not usually arithmetic.
It is describing the experiment correctly.

A sample space that is too vague or incomplete leads to bad reasoning later.
A careful setup makes later probability calculations straightforward.

## Common Mistakes

- Mixing up a random phenomenon with the experiment used to observe it.
- Forgetting that a sample space must include all possible outcomes.
- Treating an event as a single outcome instead of a set of outcomes.
- Ignoring how replacement changes the sample space.
- Using probability formulas before the underlying experiment and event are clearly defined.

## ML Systems Connection

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
