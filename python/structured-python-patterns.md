# Structured Python Patterns for ML Systems

## What

This note covers a set of Python syntax and design patterns that appear frequently in ML systems code:

- `@dataclass` for structured state and configuration,
- `Enum` for constrained named values,
- modern type hints such as `list[str]` and `str | None`,
- `field(default_factory=...)` for safe defaults,
- and `slots=True` for fixed-shape objects.

The examples here are intentionally masked and simplified. The goal is to teach the Python constructs themselves rather than preserve project-specific code.

## Why It Matters

A large amount of ML systems code is written in Python even when the heavy compute runs in C++, CUDA, Triton, or optimized tensor libraries.

That Python layer often defines:

- configuration objects,
- runtime state,
- metadata records,
- task descriptors,
- and the interfaces around kernels, training loops, and evaluation pipelines.

If these Python structures are vague or ad hoc, the codebase becomes much harder to debug and evolve. Structured Python patterns make ML systems code easier to read, safer to change, and easier to reason about at runtime.

## Core Mechanism

### `from __future__ import annotations`

```python
from __future__ import annotations
```

This makes type annotations more flexible by deferring their evaluation. In practice, it helps modern Python code use type hints more smoothly, especially when types refer to each other or when forward references would otherwise require string literals.

### `Enum` for constrained values

```python
from enum import Enum


class JobStatus(str, Enum):
    PENDING = "pending"
    RUNNING = "running"
    DONE = "done"
```

This pattern gives a small fixed set of named values instead of using loose string constants everywhere.

Why inherit from both `str` and `Enum`?

- `Enum` gives the constrained set of members,
- `str` makes the values easier to serialize, compare, and print in normal Python workflows.

This is a good fit when a field should only take one of a small number of meaningful states.

### `@dataclass` for structured records

```python
from dataclasses import dataclass


@dataclass(slots=True)
class JobMeta:
    index: int
    active: bool = False
    retries: int = 0
```

`@dataclass` generates common boilerplate such as:

- `__init__`,
- `__repr__`,
- and comparison helpers.

This makes it a strong fit for classes whose main purpose is to store structured data rather than implement complex behavior.

### `slots=True` for fixed-shape objects

```python
@dataclass(slots=True)
class JobMeta:
    index: int
    active: bool = False
```

`slots=True` means instances use a more fixed layout and do not behave like arbitrary dynamic attribute bags.

This is useful when:

- the shape of the object is known,
- accidental new attributes should be discouraged,
- and the object may be instantiated many times.

In ML systems code, that often applies to configuration objects, metadata containers, and runtime descriptors.

### Typed fields

```python
name: str
score: float = 0.0
max_steps: int = 8
```

This syntax combines:

- a field name,
- a type annotation,
- and optionally a default value.

For dataclasses, this field declaration style is also used to generate the constructor signature.

### Safe defaults with `field(default_factory=...)`

```python
from dataclasses import dataclass, field


@dataclass(slots=True)
class Record:
    tags: list[str] = field(default_factory=list)
```

This is the correct pattern for mutable defaults.

Why not write:

```python
tags: list[str] = []
```

Because that would reuse the same list across instances.

`default_factory` creates a fresh object each time a new instance is constructed.

Common forms:

- `field(default_factory=list)`
- `field(default_factory=dict)`
- `field(default_factory=tuple)`

For nested structured defaults, a callable is often used:

```python
meta: JobMeta = field(default_factory=lambda: JobMeta(index=0))
```

That means:

- if no value is supplied,
- call the function,
- and use the newly created object as the default.

### Modern union syntax

```python
comment: str | None = None
```

This means the field can be:

- a `str`,
- or `None`.

This is the modern form of what older code often wrote as `Optional[str]`.

### Generic type syntax

Modern Python uses bracketed generic syntax directly on built-in containers:

```python
list[str]
dict[str, Any]
tuple[float, ...]
```

These mean:

- a list of strings,
- a dictionary with string keys and unconstrained values,
- and a tuple containing floats, potentially of arbitrary length.

This syntax is now common in serious Python codebases and is worth becoming fluent in.

## Mock Example

The following example preserves the important Python syntax patterns while staying neutral and reusable:

```python
from __future__ import annotations

from dataclasses import dataclass, field
from enum import Enum
from typing import Any


class JobStatus(str, Enum):
    PENDING = "pending"
    RUNNING = "running"
    DONE = "done"


@dataclass(slots=True)
class JobMeta:
    index: int
    active: bool = False
    retries: int = 0
    notes: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True)
class Record:
    name: str
    score: float = 0.0
    tags: list[str] = field(default_factory=list)
    status: JobStatus = JobStatus.PENDING
    meta: JobMeta = field(default_factory=lambda: JobMeta(index=0))
    comment: str | None = None


@dataclass(slots=True)
class PipelineConfig:
    learning_rate: float = 0.01
    dropout: float = 0.1
    temperature: float = 0.5
    max_items: int = 256
    feature_dim: int = 128
    max_steps: int = 8
```

This example demonstrates the same family of syntax and design choices without preserving domain-specific identifiers.

## Design Habit Behind These Patterns

These patterns represent a broader Python design style:

- prefer structured records over loose nested dictionaries when the schema is known,
- make state explicit,
- attach types and defaults directly to the schema,
- and separate data definition from execution logic.

That style tends to scale better in ML systems code than ad hoc dictionaries or partially implicit configuration.

## Tradeoffs

- Dataclasses are excellent for structured state, but they are not a replacement for every class. When behavior and invariants dominate, a more explicit class may be clearer.
- `slots=True` improves discipline, but it also makes some dynamic patterns less convenient.
- `Any` increases flexibility, but too much `Any` weakens the value of typing.
- Typed containers improve readability, but only if the project actually treats the annotations as meaningful rather than decorative.

## Common Mistakes

- Using `[]` or `{}` directly as dataclass defaults instead of `default_factory`.
- Treating dataclasses as magic and forgetting they are still normal Python classes with normal constructor and attribute behavior.
- Assuming type hints enforce runtime correctness automatically. They are usually guidance, not runtime validation.
- Overusing `Any` until the type annotations stop communicating useful structure.
- Using dataclasses for highly dynamic objects whose shape is not stable.

## Why This Matters for ML Systems

These patterns show up constantly in ML systems work:

- config objects for training and inference,
- metadata records for pipelines and experiments,
- structured runtime state around serving or batching,
- and Python wrappers around lower-level kernels or libraries.

Strong fluency with these syntax patterns makes it easier to read framework code, build reliable tooling, and keep the Python layer around optimized compute from becoming disorganized.

## Interface and Delegation Patterns

Another very common Python pattern in ML systems code is:

- define a stable abstract interface,
- implement multiple concrete strategies behind that interface,
- and delegate real work to helper objects or internal runtime components.

This pattern appears in:

- backends,
- policies,
- storage adapters,
- schedulers,
- retrieval strategies,
- and wrappers around lower-level engines.

### Abstract base classes

```python
from abc import ABC, abstractmethod


class BaseProcessor(ABC):
    @abstractmethod
    def load(self, text: str, item_id: str, **kwargs) -> None:
        raise NotImplementedError

    @abstractmethod
    def run(self, query: str) -> list[str]:
        raise NotImplementedError
```

This defines a contract:

- subclasses must implement `load`,
- subclasses must implement `run`,
- and callers can rely on those method names regardless of which concrete implementation they receive.

`ABC` means abstract base class. `@abstractmethod` marks methods that must be implemented by subclasses.

The `raise NotImplementedError` line is a common defensive and documentary pattern that makes it explicit that the base class does not provide real behavior.

### Flexible method signatures with `**kwargs`

```python
def load(self, text: str, item_id: str, **kwargs) -> None:
    ...
```

`**kwargs` collects extra keyword arguments into a dictionary.

This is useful when:

- the interface has a stable core shape,
- optional metadata may vary across callers,
- and implementations need some flexibility without constantly changing the public signature.

Common idiom:

```python
label = kwargs.get("label", "default")
```

This reads:

- use the provided keyword value if present,
- otherwise fall back to the default.

Another common defensive idiom is:

```python
for dep in kwargs.get("dependencies", []) or []:
    ...
```

This protects the loop both when the key is missing and when the caller explicitly passes `None`.

### Composition instead of inheritance for helper behavior

```python
class Processor(BaseProcessor):
    def __init__(self, mode: str) -> None:
        self.engine = Engine(mode)
```

This is composition:

- the class inherits from `BaseProcessor` to satisfy the interface,
- but it contains an `Engine` instance to do the actual work.

That is different from inheriting behavior from the helper object.

This distinction is important:

- inheritance is used for API shape,
- composition is used for implementation structure.

### Delegation methods

```python
def load(self, text: str, item_id: str, **kwargs) -> None:
    self.engine.load(text, item_id, **kwargs)

def run(self, query: str) -> list[str]:
    return self.engine.run(query)
```

These are delegation methods.

They expose a clean public API while forwarding the real work to another object.

This pattern is useful when:

- different implementations should look the same to callers,
- internals may change,
- and the public policy or service object should remain small and readable.

## Sorting, Ranking, and Local Annotations

ML systems Python often includes ranking, filtering, and budget-selection logic. A few syntax patterns appear repeatedly in that style of code.

### Local variable annotations

```python
ranked: list[tuple[float, Item]] = []
selected: list[Item] = []
```

These are local annotations.

They make the intended contents of a variable clearer without changing runtime behavior in ordinary Python execution.

This is especially helpful when a method builds several intermediate collections.

### Generator expressions in aggregates

```python
max_score = max((item.score for item in items), default=0.0)
```

This uses:

- a generator expression to produce values lazily,
- and `default=` to avoid failure when the input is empty.

This is a compact and useful Python pattern for computing summaries over collections.

### Sorting with key functions

```python
ordered = sorted(items, key=lambda item: item.score, reverse=True)
```

This is one of the most important Python collection patterns.

Read it as:

- sort `items`,
- by each item's `score`,
- highest first.

Important pieces:

- `sorted(...)` returns a new sorted list,
- `key=` tells Python what value to sort by,
- `lambda ...` defines a short anonymous function inline,
- `reverse=True` requests descending order.

This pattern is used constantly in selection, ranking, batching, and scheduling code.

### Explicit loops remain normal Python

Even in codebases that use comprehensions and functional helpers, explicit loops are often the clearest choice for:

- accumulating scores,
- tracking budgets,
- mutating object state,
- and selecting the subset of records that fit a constraint.

Example:

```python
chosen: list[Item] = []
used = 0
for item in ordered:
    if used + item.size <= limit:
        chosen.append(item)
        used += item.size
```

This style is often more readable than forcing everything into one expression.

## Mock Example: Interface plus Ranking Policy

```python
from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass


@dataclass(slots=True)
class Item:
    item_id: str
    text: str
    score: float = 0.0
    order: int = 0
    size: int = 1


class BaseSelector(ABC):
    @abstractmethod
    def add(self, text: str, item_id: str, **kwargs) -> None:
        raise NotImplementedError

    @abstractmethod
    def select(self, query: str) -> list[Item]:
        raise NotImplementedError


class ScoreSelector(BaseSelector):
    def __init__(self, limit: int) -> None:
        self.limit = limit
        self.items: list[Item] = []
        self.counter = 0

    def add(self, text: str, item_id: str, **kwargs) -> None:
        self.items.append(
            Item(
                item_id=item_id,
                text=text,
                score=float(kwargs.get("score", 0.0)),
                order=self.counter,
                size=int(kwargs.get("size", 1)),
            )
        )
        self.counter += 1

    def select(self, query: str) -> list[Item]:
        ordered = sorted(
            self.items,
            key=lambda item: item.score,
            reverse=True,
        )
        chosen: list[Item] = []
        used = 0
        for item in ordered:
            if used + item.size <= self.limit:
                chosen.append(item)
                used += item.size
        return chosen


class RecentSelector(BaseSelector):
    def __init__(self, limit: int) -> None:
        self.limit = limit
        self.items: list[Item] = []
        self.counter = 0

    def add(self, text: str, item_id: str, **kwargs) -> None:
        self.items.append(Item(item_id=item_id, text=text, order=self.counter))
        self.counter += 1

    def select(self, query: str) -> list[Item]:
        ordered = sorted(self.items, key=lambda item: item.order, reverse=True)
        return ordered[: self.limit]
```

This example keeps the important syntax and design patterns while avoiding any project-specific content.
