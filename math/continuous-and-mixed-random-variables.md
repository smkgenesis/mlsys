# Continuous and Mixed Random Variables

## What

A continuous random variable is a random variable whose probability is described by a density over intervals rather than by positive mass at isolated points.

A mixed random variable has both:
- a discrete part with positive probability at some points,
- and a continuous part with probability spread over intervals.

## Why It Matters

The distinction between discrete and continuous random variables changes how probability is computed.

For discrete variables:
- probability comes from sums,
- and point probabilities can be positive.

For continuous variables:
- probability comes from integrals,
- and single points have probability zero.

Many practical models are not purely one or the other.
Mixed variables appear whenever a system has both special atomic outcomes and a continuous range of other outcomes.

## Core Mechanism

### Continuous random variable and density

For a continuous random variable `X`, a function `f_X : R -> R` is a probability density function if:

```text
f_X(x) >= 0
```

for all `x`, and

```text
∫(-∞ to ∞) f_X(x) dx = 1
```

The density is not itself a probability at a point.
It is a function whose area over an interval gives probability.

### Interval probabilities

For a continuous random variable:

```text
P(a <= X <= b) = ∫(a to b) f_X(x) dx
```

This is the area under the density curve between `a` and `b`.

The key consequence is:

```text
P(X = a) = ∫(a to a) f_X(x) dx = 0
```

So a single point has zero probability.

### Why boundaries do not matter

Because point probabilities are zero for continuous random variables:

```text
P(a < X < b)
= P(a <= X < b)
= P(a < X <= b)
= P(a <= X <= b)
```

This is the clean opposite of the discrete case, where endpoint choices can matter because positive mass may sit at the endpoints.

### Continuous cumulative distribution function

The cumulative distribution function of `X` is

```text
F_X(x) = P(X <= x) = ∫(-∞ to x) f_X(t) dt
```

So the CDF is accumulated area under the density up to `x`.

Interval probabilities can be recovered by subtraction:

```text
P(a < X <= b) = F_X(b) - F_X(a)
```

and for a continuous random variable, all four endpoint variants are equal for the same reason as above.

### Derivative relation

When the density is well-behaved, the CDF and PDF are related by:

```text
d/dx F_X(x) = f_X(x)
```

So:
- the CDF accumulates probability,
- the PDF is the local rate of that accumulation.

### Properties of the continuous CDF

For a continuous random variable, the CDF:
- is nondecreasing,
- is continuous,
- satisfies `F_X(x) -> 0` as `x -> -infinity`,
- satisfies `F_X(x) -> 1` as `x -> infinity`,
- and always lies between `0` and `1`.

Unlike the discrete case, there are no jumps caused by point masses.

### Mixed random variable

A mixed random variable combines a discrete part and a continuous part.

If the range splits into disjoint pieces

```text
R_X = R_Xd ∪ R_Xc
```

with

```text
R_Xd ∩ R_Xc = ∅
```

and:
- `X_d` is the discrete part,
- `X_c` is the continuous part,

then a mixed random variable can be described by a CDF of the form

```text
F_X(x) = α F_Xd(x) + (1 - α) F_Xc(x)
```

for some `0 < α < 1`.

This means:
- a fraction of the probability sits in discrete atoms,
- and the rest is distributed continuously.

### Intuition for mixed variables

A mixed variable has both:
- jumps in the CDF at discrete mass points,
- and smooth growth in regions where probability is continuous.

So a mixed CDF looks partly like a step function and partly like a smooth continuous curve.

Example shape:
- `P(X = 0) = 0.2`
- with the remaining `0.8` spread continuously over an interval such as `[1,2]`

Then:
- there is a jump of size `0.2` at `0`,
- and continuous increase across `[1,2]`.

## Tradeoffs

Discrete models are easier when the quantity naturally takes countable values.
Continuous models are more natural when the quantity varies over intervals.
Mixed models are more realistic when a system has special atomic cases plus continuous variation elsewhere.

The tradeoff is conceptual simplicity versus fidelity to the actual process being modeled.

## Common Mistakes

- Thinking the density value `f_X(x)` is itself the probability that `X = x`.
- Forgetting that for a continuous random variable, `P(X = a) = 0`.
- Carrying discrete endpoint intuition into the continuous case.
- Forgetting that the CDF of a continuous random variable is accumulated area.
- Forgetting that mixed variables can have both jumps and smooth parts.
- Treating a mixed variable as purely continuous even when a positive point mass exists.

## ML Systems Connection

These distinctions matter whenever engineering quantities mix special cases and continuous behavior.

Examples include:
- latency with a point mass at a timeout or cached-hit value plus a continuous spread otherwise,
- resource usage with a positive probability of zero demand and a continuous positive-demand regime,
- and monitoring metrics that sometimes take exact sentinel values but otherwise vary continuously.

The broader lesson is not only about probability formulas.
It is about identifying what kind of random quantity is actually being modeled, because the right interpretation of boundaries, point probabilities, and distributions depends on that structural choice.
