# Discrete Random Variables and the CDF

## What

A random variable is a function that assigns a real number to each outcome of a probability experiment.

If the set of possible values of that function is finite or countably infinite, the random variable is discrete.

For a discrete random variable, the main objects are:
- the range of possible values,
- the probability mass function,
- and the cumulative distribution function.

## Why It Matters

Probability starts with events in a sample space, but many practical questions are about numerical quantities:
- number of heads,
- number of failures,
- waiting time rounded to integer units,
- number of active users,
- number of errors in a batch.

Random variables are the bridge from raw outcomes to numerical analysis.

The PMF describes how probability is assigned to exact values.
The CDF describes how probability accumulates up to a threshold.

## Core Mechanism

### Random variable as a function

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

If `X` is defined as the number of heads, then:

```text
X(HH)=2, X(HT)=1, X(TH)=1, X(TT)=0
```

So `X` turns symbolic outcomes into numerical values.

### Range space

The range of `X` is

```text
R_X = {X(ω) | ω in Ω}
```

which is the set of all values that `X` can actually take.

In the example above:

```text
R_X = {0, 1, 2}
```

If `R_X` is finite or countably infinite, then `X` is a discrete random variable.

### Probability function and probability distribution

For each value `x` in `R_X`, define

```text
p_X(x) = P(X = x)
```

This is the probability that `X` takes the value `x`.

The full set of pairs

```text
{(x, p_X(x)) | x in R_X}
```

is the probability distribution of `X`.

For a discrete random variable:
- `p_X(x) >= 0` for every `x` in `R_X`,
- and the total probability satisfies

```text
Σ p_X(x) = 1
```

### Probability mass function

It is often convenient to extend the probability function to all real numbers:

```text
f_X(x) =
    p_X(x), if x in R_X
    0,      if x not in R_X
```

This `f_X` is the probability mass function, or PMF.

It tells where the probability mass is located on the real line.

For a discrete random variable, interval probabilities are found by summing the masses at all allowed points in the interval.

For example:

```text
P(a <= X <= b) = Σ f_X(x_i)
```

where the sum is over all values `x_i` in `R_X` such that `a <= x_i <= b`.

### Probability of a set of values

If `I` is a subset of the real numbers, define

```text
A_I = {ω in Ω | X(ω) in I}
```

Then:

```text
P(X in I) = P(A_I)
```

This is the formal way to move from:
- a set of numerical values,
to
- the event consisting of all outcomes whose assigned values lie in that set.

So when a book writes something like "the probability that `X` belongs to `I`", it means the probability of the event of outcomes whose image under `X` lies in `I`.

### Cumulative distribution function

The cumulative distribution function, or CDF, of `X` is

```text
F_X(x) = P(X <= x)
```

For a discrete random variable:

```text
F_X(x) = Σ f_X(x_i)
```

over all possible values `x_i` such that `x_i <= x`.

The CDF gives accumulated probability up to the threshold `x`.

### CDF properties

For a discrete random variable, the CDF has the following properties:

- it is nondecreasing,
- it is right-continuous,
- `F_X(x) -> 0` as `x -> -infinity`,
- `F_X(x) -> 1` as `x -> infinity`.

Because the probability mass sits at isolated points, the CDF is a step function.
It stays flat between mass points and jumps exactly where probability mass is present.

### Jump interpretation

The most important identity is:

```text
P(X = a) = F_X(a) - F_X(a-)
```

where `F_X(a-)` is the left-hand limit of the CDF at `a`.

This means:
- `F_X(a-)` is the cumulative probability just before `a`,
- `F_X(a)` is the cumulative probability including `a`,
- so the jump size at `a` is exactly the probability mass at `a`.

If there is no jump at `a`, then `P(X = a) = 0`.
If the CDF jumps at `a`, then `a` is an actual value in the range of `X`.

### Boundary formulas

The endpoint rules become much easier once the jump interpretation is clear.

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

The simplest rule is:
- if the left endpoint is included, use the left limit there,
- if the left endpoint is excluded, use the ordinary CDF there,
- if the right endpoint is included, use the ordinary CDF there,
- if the right endpoint is excluded, use the left limit there.

## Tradeoffs

The PMF is best for exact point probabilities such as `P(X = 2)`.
The CDF is best for threshold and interval questions such as `P(X <= 2)` or `P(a < X <= b)`.

The PMF makes the mass locations explicit.
The CDF makes cumulative comparisons and boundary reasoning easier.

## Common Mistakes

- Thinking a random variable is itself an outcome instead of a function on outcomes.
- Forgetting that `X = x` refers to the event of all outcomes mapped to `x`.
- Confusing the range of a random variable with the original sample space.
- Forgetting that the PMF of a discrete random variable is zero outside the possible values.
- Mixing up `F_X(a)` and `F_X(a-)`.
- Forgetting that the jump size of the CDF at `a` equals `P(X = a)`.
- Applying continuous-variable intuition to a discrete variable, especially at endpoints.

## ML Systems Connection

This topic is mathematical groundwork, but it supports several habits that matter in ML systems work:
- separating raw outcomes from measured summaries,
- reasoning about distributions over discrete counts,
- translating between point probabilities and cumulative thresholds,
- and handling boundary conditions carefully.

Those habits show up in:
- token count distributions,
- batch-size distributions,
- queue-length models,
- retry counts,
- and event-count monitoring.

The broader lesson is that many engineering quantities are not the original outcomes themselves.
They are functions of outcomes, and understanding their distribution often matters more than understanding the raw sample space directly.
