# 04. Abstract Base Classes and Interfaces

## What

Python often uses abstract base classes to define a stable behavioral contract before concrete implementations are introduced.

The key tools are:

- `ABC`
- `@abstractmethod`

These are useful when several implementations should share one interface.

## Why It Matters

Without an explicit contract, multiple implementations can drift apart in subtle ways:

- different method names,
- different argument shapes,
- or different return conventions.

Abstract interfaces make the intended surface area visible and enforceable.

## Core Idea

The pattern is:

```text
define the required behavior first,
implement the details later
```

That means the interface expresses what a component must do, not how it does it.

## Abstract Base Classes

An abstract base class is a base class that exists mainly to define required methods.

```python
from abc import ABC, abstractmethod


class StorageBackend(ABC):
    @abstractmethod
    def put(self, key: str, value: str) -> None:
        raise NotImplementedError

    @abstractmethod
    def get(self, key: str) -> str | None:
        raise NotImplementedError
```

This says:

- concrete subclasses must provide `put`
- concrete subclasses must provide `get`

The base class is defining a contract.

## Type Hints as Part of the Contract

In interface code, type hints do more than help editors.
They also clarify API meaning.

For example:

```python
def get(self, key: str) -> str | None:
```

communicates not only argument and return shapes, but also that the operation may legitimately fail to find a value.

## `**kwargs` as an Extension Point

Some interfaces need a stable core signature plus flexible optional metadata.

```python
def load(self, text: str, item_id: str, **kwargs) -> None:
    ...
```

This is useful when implementations need some room to grow without forcing frequent interface churn.

But it is a tradeoff:

- good for flexibility,
- weaker for strictness and discoverability.

## Abstract Interface vs Concrete Base Class

Not every base class should be abstract.

A concrete base class is more appropriate when:

- there is meaningful shared implementation,
- and subclasses mostly specialize small pieces.

An abstract base class is more appropriate when:

- the contract matters more than shared behavior.

## Example

```python
from abc import ABC, abstractmethod


class QueuePolicy(ABC):
    @abstractmethod
    def add(self, item: str) -> None:
        raise NotImplementedError

    @abstractmethod
    def next(self) -> str | None:
        raise NotImplementedError
```

The key value here is not the method bodies.
It is that any implementation of `QueuePolicy` must present the same interface shape.

## Common Mistakes

- Using an abstract interface when only one simple concrete class exists and no real abstraction need is present.
- Defining an interface so loosely that implementations still diverge in behavior.
- Overusing `**kwargs` until the contract becomes hard to understand.
- Forgetting that interface classes should make expected return shapes explicit.
- Confusing inheritance for code reuse with inheritance for contract definition.

## Why This Matters for ML Systems

ML systems code often has multiple interchangeable policies or backends:

- storage backends,
- retrieval strategies,
- caching policies,
- schedulers,
- and execution adapters.

Abstract interfaces help those implementations remain swappable without forcing callers to know every concrete class.

## Short Takeaway

Abstract base classes let Python code define behavior contracts first and implementations second. They are most useful when several components should remain interchangeable behind one stable caller-facing API.
