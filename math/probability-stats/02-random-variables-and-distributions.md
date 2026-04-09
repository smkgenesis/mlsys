# 02. Random Variables and Distributions

## What

A random variable is a function that assigns a real number to each outcome of a probability experiment.

This note covers the main ways such random variables are represented:

- range space,
- probability distribution table,
- probability mass function for discrete variables,
- probability density function for continuous variables,
- cumulative distribution function,
- and mixed variables that combine discrete and continuous behavior.

So this note is the transition from:

- event-level probability

to:

- numerical distributions over quantities of interest.

## Why It Matters

Probability starts with outcomes and events, but most useful questions are about numerical quantities:

- number of heads,
- waiting time,
- sum of two dice,
- request latency,
- queue length,
- retry count,
- token count.

Random variables let us turn raw outcomes into quantities that can be analyzed directly.

This is where probability starts to feel less like set manipulation alone and more like distributional reasoning.

## Core Idea

The central chain is:

```text
outcomes -> random variable -> possible numerical values -> distribution
```

The random variable is not the outcome itself.
It is the mapping from outcomes to numbers.

Once that mapping is defined, the next questions are:

- what values can it take?
- how much probability sits at each value or interval?
- how does probability accumulate up to a threshold?

The answers depend on whether the variable is:

- discrete,
- continuous,
- or mixed.

## Random Variable as a Function

A random variable is a function

```text
X : Ω -> R
```

which means:

- the input is an outcome `ω` in the sample space,
- the output is a real number `X(ω)`.

Example:

```text
Ω = {HH, HT, TH, TT}
```

If `X` is the number of heads, then:

```text
X(HH)=2, X(HT)=1, X(TH)=1, X(TT)=0
```

So `X` turns symbolic outcomes into numerical summaries.

## Range Space

The range of `X` is

```text
R_X = {X(ω) | ω in Ω}
```

which is the set of all values `X` can actually take.

In the example above:

```text
R_X = {0, 1, 2}
```

This range matters because it tells us what kind of variable we are dealing with.

- if `R_X` is finite or countably infinite, `X` is discrete
- if values range across an interval or union of intervals, `X` is continuous
- if both kinds appear, `X` is mixed

## Discrete Random Variables

A discrete random variable has a finite or countably infinite range.

For each `x` in `R_X`, define:

```text
p_X(x) = P(X = x)
```

This is the probability function.

The full collection

```text
{(x, p_X(x)) | x in R_X}
```

is the probability distribution of `X`.

Two basic rules must hold:

```text
p_X(x) >= 0
Σ p_X(x) = 1
```

### Distribution table

A discrete distribution is often displayed as a table:

```text
x        x1   x2   ...   xn
P(X=x)   p1   p2   ...   pn
```

This is useful because it makes the support and the assigned probabilities explicit at once.

### Probability mass function

It is often convenient to extend the probability function to all real numbers:

```text
f_X(x) =
    p_X(x), if x in R_X
    0,      if x not in R_X
```

This `f_X` is the probability mass function, or PMF.

It tells us where the mass is located on the real line.

### Histogram intuition

A discrete distribution can also be visualized with a histogram-like plot:

- each possible value `x`
- gets a bar centered at `x`
- whose height is `p_X(x)`

That picture makes the phrase “probability mass” more concrete.

## Continuous Random Variables

A continuous random variable is described by a density over intervals rather than by positive mass at isolated points.

A function `f_X : R -> R` is a probability density function, or PDF, if:

```text
f_X(x) >= 0
```

for all `x`, and

```text
∫(-∞ to ∞) f_X(x) dx = 1
```

The density is not itself a probability at a point.
Probability comes from area under the density.

### Interval probabilities

For a continuous random variable:

```text
P(a <= X <= b) = ∫(a to b) f_X(x) dx
```

The key consequence is:

```text
P(X = a) = 0
```

So for continuous variables, endpoint inclusion usually does not matter:

```text
P(a < X < b)
= P(a <= X < b)
= P(a < X <= b)
= P(a <= X <= b)
```

## Probability of a Set of Values

If `I` is a subset of the real numbers, define

```text
A_I = {ω in Ω | X(ω) in I}
```

Then:

```text
P(X in I) = P(A_I)
```

This is the formal bridge from:

- event language

to:

- value-set language for random variables.

It says that whenever we talk about `X` falling in some set, we are really talking about an event in the underlying sample space.

## Cumulative Distribution Function

The cumulative distribution function, or CDF, of `X` is

```text
F_X(x) = P(X <= x)
```

This definition works for discrete, continuous, and mixed variables alike.

It answers threshold questions directly.

### Discrete CDF

For a discrete random variable:

```text
F_X(x) = Σ f_X(x_i)
```

over all possible values `x_i` such that `x_i <= x`.

The CDF is therefore a step function:

- flat between support points
- and jumping exactly where probability mass is present

The jump interpretation is:

```text
P(X = a) = F_X(a) - F_X(a-)
```

where `F_X(a-)` is the left-hand limit.

So for discrete variables:

```text
jumps in the CDF are exactly where the mass lives
```

### Continuous CDF

For a continuous random variable:

```text
F_X(x) = ∫(-∞ to x) f_X(t) dt
```

When the density is well-behaved:

```text
d/dx F_X(x) = f_X(x)
```

So:

- the CDF accumulates probability,
- the PDF is the local rate of that accumulation.

Unlike the discrete case, the continuous CDF has no jumps from positive point masses.

## Boundary Formulas

For discrete random variables, endpoint choices matter because a point can have positive probability.

Remember:

```text
F_X(a)   = P(X <= a)
F_X(a-)  = P(X < a)
```

So:

```text
P(X = a)  = F_X(a) - F_X(a-)
P(X < a)  = F_X(a-)
P(X <= a) = F_X(a)
P(X > a)  = 1 - F_X(a)
P(X >= a) = 1 - F_X(a-)
```

For intervals:

```text
P(a < X < b)   = F_X(b-) - F_X(a)
P(a < X <= b)  = F_X(b)  - F_X(a)
P(a <= X < b)  = F_X(b-) - F_X(a-)
P(a <= X <= b) = F_X(b)  - F_X(a-)
```

For continuous variables, these all collapse to the same interval probability because point probabilities are zero.

## Mixed Random Variables

A mixed random variable has both:

- discrete atoms with positive probability at some points,
- and a continuous part spread over intervals.

So its CDF has both:

- jumps,
- and smoothly increasing parts.

One useful way to think about it is:

```text
F_X(x) = α F_Xd(x) + (1 - α) F_Xc(x)
```

for some `0 < α < 1`, where:

- `F_Xd` is a discrete CDF,
- `F_Xc` is a continuous CDF.

This model is useful whenever a system has special exact outcomes plus continuous variation elsewhere.

## Representational Tradeoffs

There are three especially useful ways to view a distribution:

- distribution table
- PMF or PDF
- CDF

The distribution table is best for listing support clearly.
The PMF or PDF is best for exact formulas and local structure.
The CDF is best for thresholds, intervals, and comparisons across endpoint conventions.

Learning random variables well means getting comfortable moving between those views.

## Common Mistakes

- Thinking a random variable is itself an outcome instead of a function on outcomes.
- Forgetting that `X = x` refers to an event in the underlying sample space.
- Confusing the range of a random variable with the original sample space.
- Forgetting that discrete versus continuous is about the structure of the range and the distribution, not about how the experiment informally feels.
- Treating the PMF or PDF value at a point as interchangeable across discrete and continuous cases.
- Forgetting that for continuous variables, `P(X = a) = 0`.
- Mixing up `F_X(a)` and `F_X(a-)` in the discrete case.
- Forgetting that jumps in the discrete CDF correspond exactly to point masses.
- Treating a mixed variable as if it were purely continuous when a discrete atom is present.

## Why This Matters for ML Systems

These ideas matter whenever system behavior is summarized by numerical quantities rather than raw outcomes.

Examples include:

- request latency,
- queue length,
- token count,
- batch size,
- retry count,
- cache-hit related timing distributions,
- and metrics with a mix of exact sentinel values plus continuous variability.

The broader lesson is that many engineering quantities are functions of underlying events.
To reason about them properly, we need to know what kind of random variable they are and how their distribution should be represented.

## Short Takeaway

Random variables turn outcomes into numerical quantities. Once that mapping is defined, the main task is to represent the resulting distribution correctly: with point masses for discrete values, densities for continuous intervals, and a CDF that accumulates probability across either case and cleanly exposes threshold behavior.
