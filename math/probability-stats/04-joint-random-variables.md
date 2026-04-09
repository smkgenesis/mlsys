# 04. Joint Random Variables

## What

Joint random variables describe two or more random quantities at the same time.

For two random variables `X` and `Y`, the paired object

```text
(X, Y)
```

is itself a random variable that maps outcomes to points in `R^2`.

This note covers:

- joint support,
- joint PMF and joint PDF,
- joint CDF,
- marginal distributions,
- and the idea that the joint range is often only a subset of the Cartesian product of the individual ranges.

## Why It Matters

A single random variable is enough only when one quantity is all we care about.

But many real questions involve several quantities together:

- input length and output length,
- latency and queue length,
- cache hit indicator and response time,
- request size and memory usage,
- number of white balls and number of black balls drawn.

To reason about such pairs, we need a joint distribution rather than two unrelated one-variable descriptions.

This is the first step from:

- one random quantity at a time

to:

- structured dependence between several quantities.

## Core Idea

The main construction is:

```text
ω -> (X(ω), Y(ω))
```

So instead of assigning one number to each outcome, we assign an ordered pair.

That means the distribution must answer questions like:

- how much probability sits at the pair `(x, y)`?
- how much probability lies in a rectangle of the plane?
- what distribution do we get if we look only at `X` and ignore `Y`?

That is the role of:

- joint PMFs or PDFs,
- joint CDFs,
- and marginal distributions.

## Joint Support

If `X` has range `R_X` and `Y` has range `R_Y`, then the pair `(X, Y)` has support

```text
R_{X,Y} = {(x, y) | X(ω)=x, Y(ω)=y for some ω in Ω}
```

This joint support is always contained in:

```text
R_X × R_Y
```

but it is often smaller.

That point matters.

Example:

- if `X` is the number of heads in three coin tosses
- and `Y` is the number of tails

then:

```text
X + Y = 3
```

So not every pair in `R_X × R_Y` is possible.
Only pairs lying on that constraint are in the true joint support.

This is one of the first important lessons of joint distributions:

```text
the pair structure can contain constraints that are invisible if X and Y are viewed separately
```

## Joint Discrete Random Variables

If the joint support is finite or countably infinite, then `(X, Y)` is a joint discrete random variable.

Its joint probability mass function is:

```text
f_{X,Y}(x, y) = P(X = x, Y = y)
```

Basic properties:

```text
f_{X,Y}(x, y) >= 0
ΣΣ f_{X,Y}(x, y) = 1
```

where the double sum is over the joint support.

The meaning is simple:

- a one-variable PMF puts mass on points of the real line
- a two-variable PMF puts mass on points of the plane

## Joint Continuous Random Variables

For a joint continuous random variable, probability is described by a joint density:

```text
f_{X,Y}(x, y)
```

with:

```text
f_{X,Y}(x, y) >= 0
∫∫ f_{X,Y}(x, y) dx dy = 1
```

Probability over a region `A` in the plane is:

```text
P((X, Y) in A) = ∫∫_A f_{X,Y}(x, y) dx dy
```

In particular, for a rectangle:

```text
P(a <= X <= b, c <= Y <= d)
= ∫(a to b) ∫(c to d) f_{X,Y}(x, y) dy dx
```

So the geometric intuition is the natural 2D extension of the 1D density story:

- line intervals become planar regions
- area under a curve becomes volume under a surface

## Joint Cumulative Distribution Function

The joint CDF is:

```text
F_{X,Y}(x, y) = P(X <= x, Y <= y)
```

This works for both discrete and continuous cases.

It accumulates probability over the lower-left region:

```text
{(u, v) | u <= x, v <= y}
```

For a joint discrete variable:

```text
F_{X,Y}(x, y) = ΣΣ f_{X,Y}(u, v)
```

over all `u <= x`, `v <= y`.

For a sufficiently smooth joint continuous variable:

```text
∂^2 / (∂x ∂y) F_{X,Y}(x, y) = f_{X,Y}(x, y)
```

So the joint CDF is the accumulated probability view, while the joint PMF or PDF gives the local distribution structure.

## Marginal Distributions

A marginal distribution is what remains when we focus on one variable and ignore the other.

### Discrete marginals

From a joint PMF:

```text
f_X(x) = Σ_y f_{X,Y}(x, y)
f_Y(y) = Σ_x f_{X,Y}(x, y)
```

So the marginal of `X` is obtained by summing the joint mass across all possible `y` values.

### Continuous marginals

From a joint PDF:

```text
f_X(x) = ∫ f_{X,Y}(x, y) dy
f_Y(y) = ∫ f_{X,Y}(x, y) dx
```

So the marginal of `X` is obtained by integrating out `Y`.

This is one of the most important operational ideas in multivariable probability:

```text
joint -> marginal
```

means:

```text
keep the variable you care about,
sum or integrate away the one you do not
```

## Why Joint and Marginal Are Different

Marginals tell us how `X` behaves alone and how `Y` behaves alone.
But they do not fully describe how the pair behaves together.

The joint distribution contains:

- the support geometry,
- the co-occurrence structure,
- and any constraints or dependence between the variables.

So two different joint distributions can have the same marginals.

That is why the joint object is fundamentally richer.

## Rectangles and Regions

In one-variable probability, we ask for intervals.
In two-variable probability, we ask for rectangles or more general regions.

Examples:

```text
P(X <= x, Y <= y)
P(a <= X <= b, c <= Y <= d)
P(X < Y)
```

This is the conceptual shift from one-dimensional to two-dimensional probability.

The question is no longer “where is `X` on the line?”
It becomes “where is `(X, Y)` in the plane?”

## Common Mistakes

- Assuming the joint support is automatically the full Cartesian product `R_X × R_Y`.
- Forgetting that structural constraints can rule out many pairs.
- Confusing a marginal distribution with the full joint distribution.
- Forgetting that for a joint discrete variable, marginals come from summation, while for a joint continuous variable they come from integration.
- Treating the joint CDF as if it were just two separate one-variable CDFs pasted together.
- Forgetting that probabilities for continuous pairs are assigned to regions, not to isolated points.
- Ignoring the difference between “what values can `X` and `Y` each take?” and “what pairs can `(X, Y)` take together?”

## Why This Matters for ML Systems

Many ML systems questions are naturally multivariate.

Examples include:

- request size together with latency,
- queue length together with service time,
- token counts together with memory footprint,
- cache-hit events together with response-time distributions,
- and workload features together with failure or retry behavior.

A one-variable distribution can miss the structure we actually care about.
Joint distributions matter because they preserve relationships between quantities, not just the separate behavior of each quantity in isolation.

The broader lesson is that real systems often fail or bottleneck through interactions between variables rather than through any one variable alone.

## Short Takeaway

Joint random variables extend probability from one numerical quantity to several at once. The key new ideas are that the pair `(X, Y)` has its own support, its own joint PMF or PDF, its own joint CDF, and marginals obtained by summing or integrating out one coordinate, with the joint distribution carrying structural information that separate one-variable views can miss.
