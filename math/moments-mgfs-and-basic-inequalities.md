# Moments, MGFs, and Basic Inequalities

## What

Moments, moment generating functions, standardization, and basic probability inequalities are tools for summarizing a distribution and bounding its behavior.

This note covers:
- raw moments,
- central moments,
- standardization,
- moment generating functions,
- and the basic bounds given by Markov's and Chebyshev's inequalities.

## Why It Matters

Not every useful statement about a random variable requires knowing its full distribution exactly.

Sometimes it is enough to know:
- a few moments,
- how far the variable tends to spread around its mean,
- or a guaranteed bound on how much probability can lie far in the tail.

These tools are the bridge between exact probability models and general-purpose quantitative reasoning.

## Core Mechanism

### Raw moments

For a nonnegative integer `n`, the `n`-th moment of `X` is:

For a discrete random variable:

```text
E[X^n] = Σ x^n f_X(x)
```

For a continuous random variable:

```text
E[X^n] = ∫(-∞ to ∞) x^n f_X(x) dx
```

So the `n`-th moment is just the expectation of `X^n`.

Important examples:
- first moment: `E[X]`
- second moment: `E[X^2]`

### Central moments

The `n`-th central moment is:

```text
E[(X - μ_X)^n]
```

where `μ_X = E[X]`.

These moments describe the distribution relative to its mean instead of relative to zero.

Important examples:
- first central moment: `E[X - μ_X] = 0`
- second central moment: `E[(X - μ_X)^2] = Var[X]`

So variance is the second central moment.

### Standardization

The standardized form of a random variable `X` is:

```text
Z = (X - μ_X) / σ_X
```

This transformation:
- subtracts the mean,
- and divides by the standard deviation.

As a result:

```text
E[Z] = 0
Var[Z] = 1
```

So standardization converts a random variable into a centered, unit-spread version measured in standard deviation units.

### Moment generating function

The moment generating function, or MGF, of `X` is:

```text
m_X(t) = E[e^{tX}]
```

For a discrete random variable:

```text
m_X(t) = Σ e^{tx} f_X(x)
```

For a continuous random variable:

```text
m_X(t) = ∫(-∞ to ∞) e^{tx} f_X(x) dx
```

It is called moment generating because the exponential expansion

```text
e^{tX} = 1 + tX + (t^2/2!)X^2 + (t^3/3!)X^3 + ...
```

implies

```text
m_X(t) = 1 + tE[X] + (t^2/2!)E[X^2] + (t^3/3!)E[X^3] + ...
```

So the moments are encoded in the coefficients of the power series.

### Moments from derivatives of the MGF

If the MGF exists appropriately, then:

```text
m_X^(n)(0) = E[X^n]
```

This makes the MGF a compact way to store all raw moments.

Important special cases:

```text
E[X] = m_X'(0)
E[X^2] = m_X''(0)
```

From this, variance can be written as:

```text
Var[X] = E[X^2] - (E[X])^2
       = m_X''(0) - (m_X'(0))^2
```

### Variance under linear transformations

For constants `a` and `b`:

```text
Var[aX + b] = a^2 Var[X]
σ[aX + b] = |a| σ[X]
```

So:
- shifting by `b` changes the center but not the spread,
- scaling by `a` rescales the spread.

### Markov's inequality

If `X` is nonnegative and `a > 0`, then:

```text
P(X >= a) <= E[X] / a
```

This is a universal upper bound on the probability that a nonnegative random variable exceeds a threshold.

Its intuition is an average-budget argument:
if too much probability mass lay above `a`, the mean would have to be at least that mass times `a`.

### Chebyshev's inequality

If `X` has mean `μ` and variance `σ^2`, then for `k > 1`:

```text
P(|X - μ| <= kσ) >= 1 - 1/k^2
```

Equivalently:

```text
P(|X - μ| >= kσ) <= 1/k^2
```

This says that a random variable with finite variance must keep most of its probability within a few standard deviations of the mean.

Chebyshev's inequality follows from Markov's inequality by applying Markov to the nonnegative random variable:

```text
(X - μ)^2
```

### Intuition for `k`

In Chebyshev's inequality, `k` means:
- how many standard deviations away from the mean you are looking.

Examples:
- `k = 2` means within `2σ`
- `k = 3` means within `3σ`

The larger `k` is, the wider the interval and the larger the guaranteed probability inside it.

## Tradeoffs

Moments summarize important aspects of a distribution, but they do not always reveal the full shape.
MGFs are powerful when they exist, but not every distribution has a convenient MGF on a useful neighborhood of zero.
Markov and Chebyshev are extremely general, but their bounds are often loose.

The tradeoff is between:
- generality,
- sharpness,
- and how much structural information about the distribution is being used.

## Common Mistakes

- Confusing raw moments with central moments.
- Forgetting that variance is the second central moment, not the second raw moment.
- Confusing `E[X^2]` with `(E[X])^2`.
- Thinking standardization changes the shape of the distribution rather than only recentering and rescaling it.
- Treating the MGF as a probability function rather than a transform of the distribution.
- Forgetting that derivatives of the MGF at zero recover raw moments.
- Using Markov's inequality when the random variable is not nonnegative.
- Thinking Chebyshev gives a sharp estimate rather than a universal lower bound.
- Forgetting that `k` counts standard deviations from the mean.

## ML Systems Connection

These tools support the kind of reasoning needed when exact distributions are unknown but quantitative guarantees are still needed.

Examples include:
- normalizing metrics before comparison,
- reasoning about spread and variability in latency or throughput,
- bounding tail behavior from coarse summary statistics,
- and comparing workloads using unit-free standardized scales.

The broader lesson is that engineering decisions often rely on partial information.
Moments and inequalities are useful because they turn limited information such as means and variances into concrete, defensible statements about behavior.
