# 03. Factories and Pluggable Backends

## What

Many Python systems need multiple implementations of the same role:

- a lightweight local backend,
- a heavier semantic backend,
- a stub or mock backend for testing,
- or a dependency-backed implementation used only in some environments.

A common way to structure that is:

- shared method contract,
- multiple backend classes,
- and a factory function that chooses one.

## Why It Matters

The calling code should usually depend on behavior, not on one specific implementation.

That is especially useful when:

- dependencies are optional,
- some backends are expensive to initialize,
- or different environments need different tradeoffs.

## Core Idea

The pattern is:

```text
common role -> multiple implementations -> one factory decides
```

This lets high-level code stay simple while backend choice stays centralized.

## Shared Method Shape

The important thing is usually not inheritance first.
It is that the backends expose the same operational shape.

Example:

```python
class LocalParser:
    def parse(self, text: str) -> list[str]:
        return text.lower().split()


class RemoteParser:
    def __init__(self, endpoint: str) -> None:
        self.endpoint = endpoint

    def parse(self, text: str) -> list[str]:
        return ["remote", "result"]
```

Both classes support:

```text
parse(text) -> list[str]
```

So callers can use them interchangeably at that interface level.

## Factory Functions

A factory centralizes backend selection:

```python
def make_parser(use_remote: bool) -> LocalParser | RemoteParser:
    if use_remote:
        return RemoteParser("https://example.com")
    return LocalParser()
```

This matters because it keeps implementation choice out of the rest of the system.

## Optional Dependencies and Lazy Imports

Some backends need packages that are not always installed.

One practical pattern is to import them only inside the implementation that needs them.

That keeps the rest of the code usable even when the optional dependency is absent.

It also makes it possible to raise a more specific error message exactly when that backend is selected.

## Reusing Expensive Objects with Caches

Some backends wrap expensive objects such as models or clients.

A small class-level cache can be useful when:

- initialization is slow,
- multiple instances would otherwise duplicate the same resource,
- and there is a natural key such as a model name.

This is a lightweight registry pattern.

## Example

```python
class FastEmbedder:
    def encode(self, text: str) -> tuple[float, ...]:
        return (float(len(text)),)


class HeavyEmbedder:
    _cache: dict[str, object] = {}

    def __init__(self, model_name: str) -> None:
        if model_name not in self._cache:
            self._cache[model_name] = object()
        self.model = self._cache[model_name]

    def encode(self, text: str) -> tuple[float, ...]:
        return (1.0, 2.0, 3.0)


def make_embedder(mode: str):
    if mode == "heavy":
        return HeavyEmbedder("demo-model")
    return FastEmbedder()
```

This shows:

- one role,
- two implementations,
- centralized selection,
- and reuse of an expensive backend resource.

## Common Mistakes

- Letting callers choose implementations all over the codebase instead of through one factory.
- Mixing backend-selection logic into unrelated business logic.
- Importing optional heavy dependencies at module import time when they are not always needed.
- Forgetting that backends should share a predictable public method shape.
- Using caches without thinking about lifecycle or memory growth.

## Why This Matters for ML Systems

ML systems often need:

- lightweight local fallbacks,
- production backends,
- testing doubles,
- and dependency-heavy model wrappers.

Factories and pluggable backends make those choices manageable without infecting every caller with backend-specific logic.

## Short Takeaway

Factories and pluggable backends let Python systems separate “what role this component plays” from “which implementation we are using today.” That keeps high-level code cleaner and makes optional dependencies, testing, and backend swaps much easier to manage.
