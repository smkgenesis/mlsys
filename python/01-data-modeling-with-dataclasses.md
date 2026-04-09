# 01. Data Modeling with Dataclasses

## What

Python systems code often needs small, structured objects to represent:

- metadata,
- lightweight state,
- immutable-ish records,
- and constrained status values.

The core tools for that job are:

- `@dataclass`,
- `Enum`,
- modern type hints,
- `slots=True`,
- and `field(default_factory=...)`.

## Why It Matters

When the schema of an object is known in advance, loose nested dictionaries quickly become harder to read and safer to misuse.

Dataclass-based records make the structure explicit:

- fields are visible in one place,
- defaults are obvious,
- types are documented at the declaration site,
- and tooling can reason about the object more effectively.

## Core Idea

The design goal is:

```text
known schema -> explicit fields -> safe defaults -> constrained values
```

This style works best when a class exists mainly to carry data rather than implement complicated behavior.

## Dataclasses

`@dataclass` is a strong fit for lightweight structured records.

It lets the class declaration focus on fields:

```python
from dataclasses import dataclass


@dataclass(slots=True)
class TaskMeta:
    index: int
    active: bool = False
    retries: int = 0
```

This is useful because the field list becomes the schema.

## Constrained Values with Enum

When a field should take one of a small number of meaningful values, `Enum` is better than raw strings scattered across the codebase.

```python
from enum import Enum


class TaskState(str, Enum):
    PENDING = "pending"
    RUNNING = "running"
    DONE = "done"
```

Using `str` together with `Enum` is practical because the values remain easy to print and serialize.

## Safe Defaults with `default_factory`

Mutable defaults should not be written directly in the field declaration.

Bad pattern:

```python
tags: list[str] = []
```

Better pattern:

```python
from dataclasses import dataclass, field


@dataclass(slots=True)
class Note:
    tags: list[str] = field(default_factory=list)
```

`default_factory` ensures each instance gets a fresh value.

This is also useful for nested structured defaults:

```python
meta: TaskMeta = field(default_factory=lambda: TaskMeta(index=0))
```

## `slots=True`

`slots=True` makes the object layout more fixed and discourages accidental dynamic attributes.

That is often a good fit when the object should behave like a disciplined record rather than a free-form container.

## Modern Type Hints

Some common modern forms are:

```python
list[str]
dict[str, int]
tuple[float, ...]
str | None
```

These make field intent much clearer than untyped containers.

`from __future__ import annotations` is often used so annotations stay lightweight and can refer to types more flexibly.

## Example

```python
from __future__ import annotations

from dataclasses import dataclass, field
from enum import Enum


class RunMode(str, Enum):
    TRAIN = "train"
    EVAL = "eval"


@dataclass(slots=True)
class RunRecord:
    run_id: str
    mode: RunMode = RunMode.TRAIN
    tags: list[str] = field(default_factory=list)
    note: str | None = None
```

This example shows:

- a constrained state field,
- safe mutable defaults,
- and a data-focused record with a clear schema.

## Common Mistakes

- Using `[]` or `{}` directly as dataclass defaults.
- Treating dataclasses as magic instead of ordinary Python classes with generated helpers.
- Using raw strings where a constrained enum would communicate intent better.
- Assuming type hints enforce correctness at runtime by themselves.
- Using `slots=True` and then expecting arbitrary attributes to be attachable later.

## Why This Matters for ML Systems

ML systems code is full of metadata objects:

- request state,
- run configuration snapshots,
- cache records,
- scheduling metadata,
- and experiment bookkeeping.

Those objects become much easier to inspect and maintain when their schema is explicit and their defaults are safe.

## Short Takeaway

Dataclasses, enums, and modern type hints give Python systems code a clean way to define structured records. The main win is not less typing, but clearer schemas, safer defaults, and more disciplined state.
