# 04. Measures of Location and Spread

## What

Once a random variable has a distribution, the next question is how to summarize that distribution with a few meaningful numbers.

The main measures of location are:
- expectation or mean,
- median,
- mode,
- quartiles,
- and percentiles.

The main measures of spread are:
- variance,
- and standard deviation.

## Why It Matters

A full distribution contains more information than a few summary numbers, but summary numbers are still useful because they answer different high-level questions:
- where is the distribution centered,
- what is a typical value,
- where is the peak,
- how far are values spread,
- and where do common quantile thresholds lie.

These ideas are the basis of almost all later statistical reasoning.

## Core Mechanism

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

Expectation is not necessarily a value the random variable can actually take.
It is the average level of the distribution, not the most likely value.

### Expectation of a function

If `Y = u(X)`, then its expectation can be computed directly from the distribution of `X`.

For a discrete random variable:

```text
E[u(X)] = Σ u(x) f_X(x)
```

For a continuous random variable:

```text
E[u(X)] = ∫(-∞ to ∞) u(x) f_X(x) dx
```

This avoids the need to first derive the full distribution of `Y`.

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

This is one of the most useful rules in probability.
It does not require independence.

### Median

The median is a value that splits the distribution into two halves in probability.

In the simplest form, it is defined by:

```text
F_X(m) = 0.5
```

So the median is a 50 percent point.

Depending on the shape of the CDF, the median may be:
- unique,
- non-unique,
- or require a broader definition when the CDF jumps over `0.5`.

### Mode

The mode is the value where the probability function or density function is largest.

If:

```text
f_X(x_0) = max f_X(x)
```

then `x_0` is a mode.

For a discrete random variable, the mode is the most probable value.
For a continuous random variable, it is the point where the density peaks.

Distributions can have:
- one mode,
- multiple modes,
- or no mode.

### Quartiles and percentiles

Quartiles are special quantile points defined by:

```text
F_X(Q_i) = i / 4,  for i = 1, 2, 3
```

So:
- `Q1` is the 25 percent point,
- `Q2` is the median,
- `Q3` is the 75 percent point.

More generally, the value `x_p` satisfying

```text
F_X(x_p) = p
```

for `0 < p < 1` is the `100p`-th percentile.

These quantities summarize where probability mass accumulates along the real line.

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

It measures how widely the distribution is spread, not where it is centered.

### Standard deviation

The standard deviation is the positive square root of the variance:

```text
σ_X = sqrt(Var[X])
```

Variance is measured in squared units.
Standard deviation returns the spread measure to the original units of the random variable.

### Computational variance formula

Variance can be computed more conveniently as:

```text
Var[X] = E[X^2] - (E[X])^2
```

This is often the fastest way to calculate variance.

It is important not to confuse:

```text
E[X^2]
```

with

```text
(E[X])^2
```

They are generally different, and their difference is exactly the variance.

### Effect of linear transformations on spread

For constants `a` and `b`:

```text
Var[aX + b] = a^2 Var[X]
σ[aX + b] = |a| σ[X]
```

This means:
- adding a constant shifts the center but does not change spread,
- multiplying by `a` rescales the spread.

So location and spread react differently to linear transformations.

### Relationship between mean, median, and mode

These are different notions of center.

For a symmetric unimodal distribution, they often coincide:

```text
mode = median = mean
```

For a positively skewed distribution:

```text
mode < median < mean
```

For a negatively skewed distribution:

```text
mean < median < mode
```

This gives a useful qualitative picture of how skewness moves the three centers apart.

## Tradeoffs

No single summary number captures everything.

The mean is sensitive to extreme values.
The median is more robust to skew and outliers.
The mode emphasizes where the distribution peaks.
Variance and standard deviation summarize spread but do not describe shape by themselves.

Using only one measure can hide important structure in the distribution.

## Common Mistakes

- Treating expectation as the most likely value.
- Assuming the mean must be one of the actual values the random variable can take.
- Confusing mean, median, and mode.
- Forgetting that the median is a quantile concept, not an averaging concept.
- Forgetting that a mode may be non-unique or fail to exist.
- Confusing `E[X^2]` with `(E[X])^2`.
- Forgetting that variance measures spread around the mean, not location.
- Forgetting that adding a constant changes the mean but not the variance.
- Forgetting that expectation is linear even without independence.

## ML Systems Connection

These summary measures matter whenever distributions must be monitored, compared, or optimized.

Examples include:
- latency mean versus latency percentile,
- token count averages versus tail behavior,
- batch-size distributions,
- queue-length variation,
- and resource-usage spread across workloads.

The broader lesson is that different summaries answer different engineering questions.

A mean captures average load.
A percentile captures threshold behavior.
A variance captures stability or instability.
A mode highlights the most concentrated regime.

Choosing the wrong summary can hide the operational property that actually matters.
