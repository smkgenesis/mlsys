# 02. Typed Configuration Objects

## What

A typed configuration object is a structured Python object whose fields define the configurable surface of a component, policy, or experiment.

This is often implemented with a dataclass whose fields carry:

- names,
- types,
- and baseline default values.

## Why It Matters

Configuration is one of the most error-prone parts of systems code.

If configuration lives in loose dictionaries, it becomes harder to see:

- what parameters exist,
- which ones are required,
- what defaults are assumed,
- and what shape each value should have.

Typed configuration objects turn that implicit knowledge into explicit structure.

## Core Idea

The goal is:

```text
parameter surface -> explicit schema -> stable defaults
```

This makes configuration easier to inspect, document, and evolve.

## Configuration as Schema

A config object does more than hold values.
It defines the parameter schema of a system component.

```python
from dataclasses import dataclass


@dataclass(slots=True)
class CacheConfig:
    max_items: int = 1024
    ttl_seconds: float = 30.0
    warmup_ratio: float = 0.1
```

This makes three things obvious at once:

- what can be tuned,
- what the default behavior is,
- and what type each setting should have.

## Defaults as Baselines

Defaults are not merely filler values.
They often define the baseline experiment or baseline runtime behavior.

That means config design should be treated as part of the system design, not as an afterthought.

## `slots=True` for Config Objects

Configuration objects often have a fixed field set.
That makes `slots=True` a good fit:

- the schema stays fixed,
- accidental attributes are less likely,
- and the object behaves more like a disciplined record.

## Strings, Literals, and Enums

Sometimes a config field selects among a few modes:

```python
backend: str = "local"
```

This is convenient, but a little loose.

Stricter alternatives include:

- `Literal[...]`
- or `Enum`

The tradeoff is simple:

- strings are quick and flexible,
- enums or literals communicate the allowed set more clearly.

## Example

```python
from dataclasses import dataclass


@dataclass(slots=True)
class RetrievalConfig:
    score_weight: float = 0.5
    recall_limit: int = 16
    backend: str = "local"
    temperature: float = 0.25
```

This is easier to reason about than a free-form dictionary because the configurable surface is visible in one declaration.

## Common Mistakes

- Hiding configuration inside arbitrary dictionaries instead of a named schema.
- Treating defaults as meaningless when they actually define baseline behavior.
- Overusing `str` options without documenting the intended allowed values.
- Mixing unrelated configuration domains into one giant object.
- Forgetting that a good config object should be easy to inspect in logs, tests, and debugging sessions.

## Why This Matters for ML Systems

ML systems code constantly exposes tunable parameters:

- thresholds,
- budget limits,
- backend choices,
- weighting constants,
- and model-selection knobs.

Typed configuration objects make those knobs explicit and easier to change safely.

## Short Takeaway

A typed configuration object turns scattered parameters into a visible schema. The main benefit is clarity: the tunable surface, the defaults, and the intended value shapes all become obvious in one place.
