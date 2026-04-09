# 06. Small Numeric Utilities and Stability

## What

Even small Python helper functions can sit on important mathematical assumptions.

Common examples include:

- vector scoring,
- similarity computations,
- normalization,
- and stable transformations from raw scores to weights.

This note focuses on two ideas:

- tiny numeric primitives still need clear contracts,
- and numerically stable implementations matter even in small functions.

## Why It Matters

In ML systems code, a short helper function can still influence:

- ranking,
- routing,
- weighting,
- selection,
- or threshold behavior.

When the function is numeric, correctness is not just about syntax.
It is also about:

- shape assumptions,
- floating-point behavior,
- and boundary-case handling.

## Core Idea

The main pattern is:

```text
small numeric helper -> explicit contract -> stable implementation
```

The smaller the function, the easier it is to forget that it still encodes mathematical meaning.

## Dot Product as a Primitive

A dot product is often used as a score:

```python
def score(a: tuple[float, ...], b: tuple[float, ...]) -> float:
    return sum(x * y for x, y in zip(a, b))
```

This is simple, but it still assumes:

- the vectors represent compatible coordinates,
- the dimensions should match,
- and the result is meaningful only under that shared interpretation.

One subtle risk is that `zip` silently truncates to the shorter input.
That can hide length mismatches unless the contract or code checks for them explicitly.

## Stable Normalization

Some helpers transform raw scores into normalized weights.

A classic example is a softmax-like computation:

```python
import math


def normalize_scores(scores: list[float]) -> list[float]:
    if not scores:
        return []

    offset = max(scores)
    exps = [math.exp(s - offset) for s in scores]
    total = sum(exps)
    return [x / total for x in exps]
```

The important trick is subtracting the maximum score first.

This does not change the normalized result, but it greatly reduces the chance of overflow in the exponentials.

## Why Max-Shifting Works

If every score is reduced by the same constant, the relative softmax weights are unchanged after normalization.

So:

```text
softmax(s) = softmax(s - c)
```

for a shared constant `c`.

Choosing `c = max(s)` keeps the largest shifted score at `0`, which makes the exponentials far safer numerically.

## Boundary Cases Matter

Small helper functions should still make edge-case behavior explicit.

Examples:

- what happens on empty input?
- what happens if all weights collapse numerically?
- what happens if vector lengths differ?

A function that is mathematically fine on the main path can still be operationally brittle if these cases are left implicit.

## Example

```python
import math


def weighted_choice(scores: list[float]) -> list[float]:
    if not scores:
        return []

    baseline = max(scores)
    scaled = [math.exp(s - baseline) for s in scores]
    total = sum(scaled)
    if total == 0.0:
        return [0.0 for _ in scores]
    return [x / total for x in scaled]
```

This example is not important because of its exact use case.
It is useful because it shows how a short numeric helper can still encode:

- normalization,
- stability,
- and explicit edge-case behavior.

## Common Mistakes

- Treating a short numeric helper as too small to need a contract.
- Forgetting that `zip` silently truncates mismatched iterables.
- Using exponentials directly on raw scores without any stability trick.
- Confusing a density-like value, score, or weight with a true probability.
- Ignoring empty-input or zero-total cases.

## Why This Matters for ML Systems

ML systems pipelines often contain small numeric helpers that decide:

- candidate ranking,
- score normalization,
- memory or cache weighting,
- or fallback selection logic.

Those helpers are small, but they sit on the path between raw model outputs or signals and operational decisions.

That makes stable, explicit implementations important even when the code itself looks tiny.

## Short Takeaway

Small numeric utilities still need strong contracts and stable implementations. In Python systems code, a helper can be only a few lines long and still deserve careful treatment of vector shape assumptions, exponentials, normalization, and boundary cases.
