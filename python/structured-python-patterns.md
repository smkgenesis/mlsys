# Structured Python Patterns for ML Systems

## What

This note explains a family of Python patterns that appear repeatedly in ML systems code:

- structured records built with `@dataclass`,
- constrained values represented with `Enum`,
- modern type annotations such as `list[str]` and `str | None`,
- abstract interfaces defined with `ABC` and `@abstractmethod`,
- composition and delegation,
- and collection-processing idioms such as `sorted(..., key=...)`, generator expressions, and `default_factory`.

The examples are intentionally masked and rewritten as mock code. The goal is to teach the Python syntax and design habits, not preserve project-specific logic.

## Why It Matters

Python is often the language that holds the ML systems stack together even when the heavy compute runs somewhere else.

In practice, the Python layer usually defines:

- configuration objects,
- runtime state,
- metadata records,
- policy or backend interfaces,
- ranking and selection logic,
- and wrappers around lower-level engines or kernels.

That means Python quality directly affects how easy the system is to read, debug, tune, and extend.

## Core Pattern

Across many ML systems codebases, Python tends to organize itself into three layers:

1. **Structured data** that captures configuration, metadata, and lightweight state.
2. **Stable interfaces** that let multiple implementations share one API.
3. **Selection and orchestration logic** that ranks, filters, routes, or delegates work.

The syntax in this note fits naturally into those three layers.

## 1. Structured Data with Dataclasses and Types

### `from __future__ import annotations`

```python
from __future__ import annotations
```

This makes type annotations more flexible by deferring their evaluation. In modern Python code, it is commonly used to keep type hints cleaner, especially when types refer to each other or would otherwise require string-based forward references.

### `Enum` for constrained values

```python
from enum import Enum


class JobStatus(str, Enum):
    PENDING = "pending"
    RUNNING = "running"
    DONE = "done"
```

This pattern replaces loose string constants with a small controlled set of named values.

Why inherit from both `str` and `Enum`?

- `Enum` gives the constrained member set,
- `str` makes the values easier to print, compare, and serialize in ordinary Python workflows.

This is a good fit when a field should represent one of a small number of meaningful states.

### `@dataclass` for lightweight records

```python
from dataclasses import dataclass


@dataclass(slots=True)
class JobMeta:
    index: int
    active: bool = False
    retries: int = 0
```

`@dataclass` is a strong fit when a class exists mainly to store structured data rather than implement complex behavior.

It automatically generates common boilerplate such as:

- `__init__`,
- `__repr__`,
- and comparison helpers.

### `slots=True`

```python
@dataclass(slots=True)
class JobMeta:
    index: int
    active: bool = False
```

`slots=True` gives the object a more fixed layout and discourages dynamic accidental attributes.

In ML systems code, that usually signals:

- the object has a known schema,
- it may be instantiated frequently,
- and it should behave more like a disciplined record than a free-form container.

### Typed fields

```python
name: str
score: float = 0.0
max_steps: int = 8
```

This is the core dataclass field declaration style:

- field name,
- type annotation,
- optional default value.

For dataclasses, these declarations define both the schema and the generated constructor signature.

### Generic type syntax

Modern Python commonly uses generic syntax directly on built-in containers:

```python
list[str]
dict[str, Any]
tuple[float, ...]
```

These mean:

- a list of strings,
- a dictionary with string keys and unconstrained values,
- and a tuple containing floats, possibly of arbitrary length.

### Modern union syntax

```python
comment: str | None = None
```

This means the value can be either:

- a `str`,
- or `None`.

This is the modern version of what older code often wrote as `Optional[str]`.

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

`default_factory` creates a fresh value every time a new instance is constructed.

Common forms:

- `field(default_factory=list)`
- `field(default_factory=dict)`
- `field(default_factory=tuple)`

For nested structured defaults, a callable is often used:

```python
meta: JobMeta = field(default_factory=lambda: JobMeta(index=0))
```

That means:

- if no value is passed,
- call the factory,
- and use the newly created object as the default.

### What this layer is buying you

These patterns encourage a stable Python design habit:

- prefer structured records over loose nested dictionaries when the schema is known,
- make state explicit,
- attach types and defaults directly to the schema,
- and separate data definition from execution logic.

## 2. Stable Interfaces with Abstract Base Classes

ML systems code often needs multiple implementations behind one shared API:

- different backends,
- different policies,
- different retrieval strategies,
- different storage layers,
- or different runtime adapters.

Python commonly expresses that with abstract base classes.

### `ABC` and `@abstractmethod`

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
- and callers can rely on those method names regardless of which implementation they receive.

`ABC` means abstract base class. `@abstractmethod` marks methods that concrete subclasses must define.

The `raise NotImplementedError` line is a common documentary and defensive signal that the base class does not provide real behavior.

### `**kwargs` for flexible method signatures

```python
def load(self, text: str, item_id: str, **kwargs) -> None:
    ...
```

`**kwargs` collects additional keyword arguments into a dictionary.

This is useful when:

- the interface has a stable core shape,
- optional metadata may vary across callers,
- and implementations need flexibility without constantly changing the public signature.

Common idiom:

```python
label = kwargs.get("label", "default")
```

This reads:

- use the provided keyword argument if present,
- otherwise fall back to the default.

Another common defensive idiom is:

```python
for dep in kwargs.get("dependencies", []) or []:
    ...
```

This protects the loop both when the key is missing and when the caller explicitly passes `None`.

## 3. Composition and Delegation

A common mistake when reading class-based Python is to think every relationship is inheritance.

In practice, many ML systems classes use:

- inheritance for API shape,
- composition for implementation.

### Composition

```python
class Processor(BaseProcessor):
    def __init__(self, mode: str) -> None:
        self.engine = Engine(mode)
```

This is composition:

- the class inherits from `BaseProcessor` to satisfy the interface,
- but it contains an `Engine` instance to do real work.

This is different from inheriting behavior from the helper object.

### Delegation

```python
def load(self, text: str, item_id: str, **kwargs) -> None:
    self.engine.load(text, item_id, **kwargs)

def run(self, query: str) -> list[str]:
    return self.engine.run(query)
```

These are delegation methods.

They expose a stable public API while forwarding work to another object.

This is useful when:

- callers should see one clean interface,
- implementation details may change,
- and the top-level policy or service object should stay small and readable.

## 4. Ranking, Filtering, and Small Coordination Logic

ML systems Python often includes lightweight orchestration logic:

- ranking candidates,
- selecting a subset under a budget,
- computing simple aggregate statistics,
- and mutating lightweight runtime state.

Several syntax patterns appear repeatedly in that style of code.

### Local variable annotations

```python
ranked: list[tuple[float, Item]] = []
selected: list[Item] = []
```

These are local annotations.

They make the intended contents of a variable clearer without changing ordinary runtime behavior.

They are especially helpful when a method builds several intermediate collections.

### Generator expressions in aggregates

```python
max_score = max((item.score for item in items), default=0.0)
```

This combines:

- a generator expression, which produces values lazily,
- and `default=`, which prevents failure on an empty input.

This is a compact and very common Python pattern for computing summaries over collections.

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
- `reverse=True` means descending order.

### Explicit loops are still normal Python

Even in codebases that use comprehensions heavily, explicit loops are often the clearest choice for:

- accumulating scores,
- tracking budgets,
- mutating object state,
- and selecting the subset of records that fits a constraint.

Example:

```python
chosen: list[Item] = []
used = 0
for item in ordered:
    if used + item.size <= limit:
        chosen.append(item)
        used += item.size
```

This style is often clearer than forcing the whole operation into one expression.

## Integrated Mock Example

The following example ties the main patterns together in one neutral, masked piece of code:

```python
from __future__ import annotations

from abc import ABC, abstractmethod
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
class Item:
    item_id: str
    text: str
    score: float = 0.0
    order: int = 0
    size: int = 1
    status: JobStatus = JobStatus.PENDING
    meta: JobMeta = field(default_factory=lambda: JobMeta(index=0))
    comment: str | None = None


@dataclass(slots=True)
class PipelineConfig:
    limit: int = 256
    feature_dim: int = 128
    temperature: float = 0.5


class BaseSelector(ABC):
    @abstractmethod
    def add(self, text: str, item_id: str, **kwargs) -> None:
        raise NotImplementedError

    @abstractmethod
    def select(self, query: str) -> list[Item]:
        raise NotImplementedError


class Engine:
    def __init__(self, feature_dim: int) -> None:
        self.feature_dim = feature_dim

    def score(self, text: str, query: str) -> float:
        return float(len(text) + len(query)) / max(self.feature_dim, 1)


class ScoreSelector(BaseSelector):
    def __init__(self, config: PipelineConfig) -> None:
        self.config = config
        self.engine = Engine(config.feature_dim)
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
                meta=JobMeta(index=self.counter),
            )
        )
        self.counter += 1

    def select(self, query: str) -> list[Item]:
        ordered = sorted(
            self.items,
            key=lambda item: item.score + self.engine.score(item.text, query),
            reverse=True,
        )
        chosen: list[Item] = []
        used = 0
        for item in ordered:
            if used + item.size <= self.config.limit:
                chosen.append(item)
                used += item.size
        return chosen
```

This example is not important because of its domain behavior. It is useful because it shows the syntax patterns working together in the kind of class-oriented, systems-facing Python that appears often in ML codebases.

## Tradeoffs

- Dataclasses are excellent for structured state, but they are not a replacement for every class. When behavior and invariants dominate, a more explicit class may be clearer.
- `slots=True` improves discipline, but it also makes some dynamic patterns less convenient.
- `Any` increases flexibility, but too much `Any` weakens the value of typing.
- Abstract base classes make interfaces clearer, but they can add unnecessary ceremony if there is only one implementation and no real abstraction need.
- Delegation improves boundaries, but too many thin wrappers can make control flow harder to follow.

## Common Mistakes

- Using `[]` or `{}` directly as dataclass defaults instead of `default_factory`.
- Treating dataclasses as magic and forgetting they are still ordinary Python classes.
- Assuming type hints enforce runtime correctness automatically. Usually they are guidance, not runtime validation.
- Overusing `Any` until the type hints stop communicating useful structure.
- Confusing composition with inheritance when reading class relationships.
- Using abstract interfaces where a simple concrete class would be clearer.
- Turning small ranking or filtering logic into overly compressed one-liners that are harder to maintain than explicit loops.

## Why This Matters for ML Systems

These patterns appear constantly in ML systems work:

- config objects for training and inference,
- metadata records for pipelines and experiments,
- interface layers over different backends or policies,
- wrappers around runtimes, encoders, or kernels,
- and ranking, routing, or budget-selection logic in orchestration code.

Strong fluency with these patterns makes it easier to read framework code, build reliable tooling, and keep the Python layer around optimized compute from becoming the least understandable part of the system.
