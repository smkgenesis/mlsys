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
- expectations over joint distributions,
- covariance and correlation,
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
- marginal distributions,
- and summary quantities built from the pair.

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

## Expectations from a Joint Distribution

Once a joint distribution is known, we can compute expectations of either variable by averaging against the joint law.

For a joint discrete distribution:

```text
E[X] = ΣΣ x f_{X,Y}(x, y)
E[Y] = ΣΣ y f_{X,Y}(x, y)
```

For a joint continuous distribution:

```text
E[X] = ∫∫ x f_{X,Y}(x, y) dx dy
E[Y] = ∫∫ y f_{X,Y}(x, y) dx dy
```

So the joint distribution contains enough information to recover one-variable expectations too.

The same applies to variances:

```text
Var[X] = E[(X - μ_X)^2]
Var[Y] = E[(Y - μ_Y)^2]
```

computed against the joint distribution.

## Expectation of a Function of Two Variables

More generally, if `u(X, Y)` is any function of the pair, then:

For a joint discrete distribution:

```text
E[u(X, Y)] = ΣΣ u(x, y) f_{X,Y}(x, y)
```

For a joint continuous distribution:

```text
E[u(X, Y)] = ∫∫ u(x, y) f_{X,Y}(x, y) dx dy
```

This is one of the most useful rules in multivariable probability because many important quantities are functions of both coordinates.

Examples:

```text
u(X, Y) = X + Y
u(X, Y) = XY
u(X, Y) = max(X, Y)
```

The linearity rule still holds:

```text
E[a u(X, Y) + b v(X, Y)] = aE[u(X, Y)] + bE[v(X, Y)]
```

## Independence Revisited

Joint distributions are also where independence becomes concrete.

For two random variables, independence means the joint distribution factorizes into the product of the marginals.

In the discrete case:

```text
f_{X,Y}(x, y) = f_X(x) f_Y(y)
```

In the continuous case:

```text
f_{X,Y}(x, y) = f_X(x) f_Y(y)
```

throughout the support.

This is stronger than simply knowing the marginals separately.
It says the pair structure contains no interaction beyond what is already present in the one-variable distributions.

An important consequence is:

```text
if X and Y are independent, then E[XY] = E[X]E[Y]
```

But the reverse direction is generally not true.
Knowing only that `E[XY] = E[X]E[Y]` is weaker than full independence.

## Covariance

Covariance measures linear co-movement between two random variables.

It is defined by:

```text
Cov(X, Y) = E[(X - μ_X)(Y - μ_Y)]
```

An equivalent and often more convenient form is:

```text
Cov(X, Y) = E[XY] - E[X]E[Y]
```

Important facts:

```text
Cov(X, X) = Var[X]
Cov(X, Y) = Cov(Y, X)
```

If `X` and `Y` are independent, then:

```text
Cov(X, Y) = 0
```

But again, zero covariance does not in general imply independence.

So covariance is a useful dependence summary, but not a complete description of dependence.

## Correlation

Correlation rescales covariance so that the result is unit-free.

It is defined by:

```text
ρ(X, Y) = Cov(X, Y) / (σ_X σ_Y)
```

when the standard deviations are nonzero.

This normalization makes the scale easier to interpret:

- positive correlation means positive linear association
- negative correlation means negative linear association
- zero correlation means no linear association

The standard bound is:

```text
-1 <= ρ(X, Y) <= 1
```

If:

```text
Y = aX + b
```

for constants `a` and `b`, then the correlation is `+1` or `-1` depending on the sign of `a`.

## Variance of Sums and Differences

Joint reasoning matters immediately when we look at sums and differences.

The key formulas are:

```text
Var[X + Y] = Var[X] + Var[Y] + 2Cov(X, Y)
Var[X - Y] = Var[X] + Var[Y] - 2Cov(X, Y)
```

These formulas show exactly where dependence enters.

If `X` and `Y` are independent, then covariance is zero and the formulas simplify to:

```text
Var[X + Y] = Var[X] + Var[Y]
Var[X - Y] = Var[X] + Var[Y]
```

So independence is not only about conceptual simplicity.
It directly changes quantitative behavior of combined variables.

## Why This Extension Matters

The first half of joint probability is about representation:

- support,
- PMF or PDF,
- CDF,
- marginals.

This second half is about what we can *do* with that representation:

- compute expectations,
- measure interaction,
- and quantify how combined variables behave.

That is why covariance and correlation belong naturally inside the same joint-variables note rather than in a completely separate topic.

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
- Assuming `E[XY] = E[X]E[Y]` is equivalent to independence in all cases.
- Treating zero covariance as proof of independence.
- Forgetting that covariance affects `Var[X + Y]` and `Var[X - Y]`.
- Using correlation as if it captured every kind of dependence rather than specifically linear association.

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

Covariance and correlation make that even more explicit.
They give us compact ways to talk about whether two quantities rise together, move in opposite directions, or combine into larger or smaller variability than their separate one-variable summaries would suggest.

## Short Takeaway

Joint random variables extend probability from one numerical quantity to several at once. The key ideas are that the pair `(X, Y)` has its own support, joint distribution, and marginals, and that this joint view lets us compute expectations of functions of the pair, define covariance and correlation, and understand how dependence changes the behavior of sums, differences, and other combined quantities.
