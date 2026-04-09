# 03. Distribution Summaries and Moments

## What

Once a random variable has a distribution, the next question is how to summarize that distribution with a few useful quantities.

This note covers:

- expectation or mean,
- median, mode, quartiles, and percentiles,
- variance and standard deviation,
- raw moments and central moments,
- standardization,
- moment generating functions,
- and the basic bounds given by Markov's and Chebyshev's inequalities.

So this note is about moving from:

- representing a distribution

to:

- extracting reusable information from it.

## Why It Matters

A full distribution contains a lot of information, but in practice we often work with compressed summaries.

Sometimes we want:

- a typical value,
- a measure of spread,
- a threshold percentile,
- or a bound on tail behavior.

These quantities are useful because they answer different questions without requiring us to manipulate the entire distribution every time.

This is where probability starts to become a practical quantitative toolkit rather than only a language of events and densities.

## Core Idea

The main progression is:

```text
distribution -> summary statistics -> moments -> transforms and bounds
```

Some summaries describe:

- center
- spread
- or quantile thresholds

Moments go a level deeper by encoding systematic numerical features of the distribution.
MGFs package those moments into one transform.
Inequalities then convert partial information such as means and variances into guaranteed probability bounds.

## Measures of Location

### Expectation or mean

The expectation `E[X]`, also written `μ_X`, is the probability-weighted average value of the random variable.

For a discrete random variable:

```text
E[X] = Σ x f_X(x)
```

For a continuous random variable:

```text
E[X] = ∫(-∞ to ∞) x f_X(x) dx
```

Expectation is the average level of the distribution.
It need not be one of the actual values the variable can take.

### Expectation of a function

If `Y = u(X)`, then:

For a discrete random variable:

```text
E[u(X)] = Σ u(x) f_X(x)
```

For a continuous random variable:

```text
E[u(X)] = ∫(-∞ to ∞) u(x) f_X(x) dx
```

This is useful because it lets us compute derived expectations directly from the distribution of `X`.

### Linearity of expectation

Expectation is linear.

For constants `a` and `b`:

```text
E[aX + b] = aE[X] + b
```

More generally:

```text
E[a u(X) + b v(X)] = aE[u(X)] + bE[v(X)]
```

This does not require independence.

### Median

The median is a value that splits the distribution into two halves in probability.

In the simplest form:

```text
F_X(m) = 0.5
```

So the median is a quantile concept rather than an averaging concept.

### Mode

The mode is the value where the PMF or PDF is largest.

For a discrete random variable, it is the most probable value.
For a continuous random variable, it is the point where the density peaks.

### Quartiles and percentiles

Quartiles are special quantile points:

```text
F_X(Q_i) = i / 4, for i = 1, 2, 3
```

More generally, the value `x_p` satisfying

```text
F_X(x_p) = p
```

for `0 < p < 1` is the `100p`-th percentile.

These quantities summarize where probability mass accumulates along the real line.

## Measures of Spread

### Variance

Variance measures spread around the mean.

If `μ_X = E[X]`, then:

For a discrete random variable:

```text
Var[X] = Σ (x - μ_X)^2 f_X(x)
```

For a continuous random variable:

```text
Var[X] = ∫(-∞ to ∞) (x - μ_X)^2 f_X(x) dx
```

Variance is the expected squared deviation from the mean.

### Standard deviation

The standard deviation is the positive square root of the variance:

```text
σ_X = sqrt(Var[X])
```

Variance is in squared units.
Standard deviation returns the spread to the original scale of the variable.

### Computational variance formula

Variance can also be computed as:

```text
Var[X] = E[X^2] - (E[X])^2
```

This is often the most convenient calculation form.

## Moments

### Raw moments

For a nonnegative integer `n`, the `n`-th raw moment is:

For a discrete random variable:

```text
E[X^n] = Σ x^n f_X(x)
```

For a continuous random variable:

```text
E[X^n] = ∫(-∞ to ∞) x^n f_X(x) dx
```

Important examples:

- first raw moment: `E[X]`
- second raw moment: `E[X^2]`

### Central moments

The `n`-th central moment is:

```text
E[(X - μ_X)^n]
```

where `μ_X = E[X]`.

These measure behavior relative to the mean instead of relative to zero.

Important examples:

- first central moment: `0`
- second central moment: `Var[X]`

So variance is the second central moment.

## Standardization

The standardized form of `X` is:

```text
Z = (X - μ_X) / σ_X
```

This produces a variable with:

```text
E[Z] = 0
Var[Z] = 1
```

Standardization recenters and rescales a variable so that values are measured in standard deviation units.

## Moment Generating Function

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

It is called moment generating because the expansion

```text
e^{tX} = 1 + tX + (t^2/2!)X^2 + ...
```

implies that the moments appear in the coefficients.

### Moments from MGF derivatives

If the MGF exists suitably, then:

```text
m_X^(n)(0) = E[X^n]
```

Important special cases:

```text
E[X] = m_X'(0)
E[X^2] = m_X''(0)
```

So:

```text
Var[X] = m_X''(0) - (m_X'(0))^2
```

## Linear Transformations

For constants `a` and `b`:

```text
E[aX + b] = aE[X] + b
Var[aX + b] = a^2 Var[X]
σ[aX + b] = |a| σ[X]
```

This shows a very important distinction:

- adding `b` shifts location but does not change spread
- multiplying by `a` rescales both location and spread

## Basic Inequalities

### Markov's inequality

If `X` is nonnegative and `a > 0`, then:

```text
P(X >= a) <= E[X] / a
```

This is a general upper bound on upper-tail probability.

### Chebyshev's inequality

If `X` has mean `μ` and variance `σ^2`, then:

```text
P(|X - μ| >= kσ) <= 1/k^2
```

Equivalently:

```text
P(|X - μ| <= kσ) >= 1 - 1/k^2
```

This says that finite variance forces a large portion of the distribution to remain within a few standard deviations of the mean.

Chebyshev follows from Markov by applying Markov to the nonnegative random variable:

```text
(X - μ)^2
```

## Representational Tradeoffs

No single summary captures everything.

- the mean captures average level,
- the median captures a central quantile,
- the mode captures the peak,
- the variance captures spread,
- moments capture richer numerical structure,
- MGFs package moments compactly,
- inequalities give robust but often loose bounds.

The real skill is choosing the right summary for the question at hand.

## Common Mistakes

- Treating expectation as the most likely value.
- Assuming the mean must be one of the actual attainable values.
- Confusing mean, median, and mode.
- Forgetting that the median is defined through the CDF.
- Confusing `E[X^2]` with `(E[X])^2`.
- Forgetting that variance is the second central moment, not the second raw moment.
- Thinking standardization changes the shape rather than just recentering and rescaling.
- Treating the MGF as a probability function instead of a transform.
- Forgetting that Markov requires a nonnegative random variable.
- Treating Chebyshev as a sharp estimate rather than a very general bound.

## Why This Matters for ML Systems

These summaries matter whenever system behavior must be monitored, compared, normalized, or bounded.

Examples include:

- mean latency versus percentile latency,
- workload spread and stability,
- token-count variability,
- throughput normalization,
- queue-length fluctuation,
- and coarse tail guarantees when only limited summary statistics are known.

The broader lesson is that engineering decisions often depend on compressed information about a distribution rather than the full exact law.
These tools tell us what can still be said when only means, variances, or a few structural properties are available.

## Short Takeaway

Once a distribution is known, the next step is to summarize it. Means, quantiles, variance, moments, MGFs, and basic inequalities are all ways of compressing distributional information into reusable quantities, with different summaries answering different practical questions about center, spread, scaling, and tail behavior.
