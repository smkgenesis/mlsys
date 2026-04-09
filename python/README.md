# python

Python language and runtime knowledge for ML systems work.

Belongs here:
- Python language features that appear constantly in ML codebases,
- runtime behavior that affects performance and correctness,
- concurrency and process-model patterns,
- packaging and environment discipline,
- and framework-facing Python design habits.

Does not belong here:
- project-specific research code,
- framework-specific model behavior when the main question is not Python itself.

Current notes:
- [01. Data Modeling with Dataclasses](/Users/minkyu/Documents/mlsys/python/01-data-modeling-with-dataclasses.md)
- [02. Typed Configuration Objects](/Users/minkyu/Documents/mlsys/python/02-typed-configuration-objects.md)
- [03. Factories and Pluggable Backends](/Users/minkyu/Documents/mlsys/python/03-factories-and-pluggable-backends.md)
- [04. Abstract Base Classes and Interfaces](/Users/minkyu/Documents/mlsys/python/04-abstract-base-classes-and-interfaces.md)
- [05. Small Graph Abstractions](/Users/minkyu/Documents/mlsys/python/05-small-graph-abstractions.md)
- [06. Small Numeric Utilities and Stability](/Users/minkyu/Documents/mlsys/python/06-small-numeric-utilities-and-stability.md)

---

Python for ML Systems:
Data Modeling -> Configuration -> Pluggable Backends -> Interface Contracts -> Relationship Structures -> Numeric Helpers

This branch is about the Python layer that holds ML systems together.

The point is not to restate the Python language reference.
The point is to explain the patterns that appear repeatedly in systems-facing Python:

- structured state objects,
- typed configuration,
- swappable backends,
- stable interfaces,
- small relationship abstractions,
- and lightweight numerical helpers.

The notes in this folder use fresh minimal examples rather than reproducing original project code.
The goal is to deliver concepts clearly while keeping the abstraction level appropriate for reusable documentation.

---

## 0. Scope and Preconditions

This branch assumes basic comfort with ordinary Python syntax.

The emphasis here is on:

- code structure,
- data modeling,
- extension boundaries,
- and small implementation patterns that matter in real ML systems codebases.

The recurring questions are:

- how should state be represented?
- how should configurable behavior be exposed?
- how do we swap implementations cleanly?
- where should contracts live?
- and how do small helper abstractions stay readable and safe?

---

## 1. Structured Data Comes First

Deep dive: [01. Data Modeling with Dataclasses](/Users/minkyu/Documents/mlsys/python/01-data-modeling-with-dataclasses.md)

Many systems-facing Python modules begin with small record-like objects.

This note covers:

- dataclasses,
- enums,
- safe mutable defaults,
- `slots=True`,
- and modern type-hint syntax.

That is the foundation because the rest of a codebase becomes easier to understand once its basic records are explicit.

---

## 2. Configuration Deserves Its Own Schema

Deep dive: [02. Typed Configuration Objects](/Users/minkyu/Documents/mlsys/python/02-typed-configuration-objects.md)

Once structured state exists, the next question is how to expose the tunable surface of a component.

This note frames configuration as schema:

- defaults define baseline behavior,
- typed fields define the allowed surface,
- and the config object becomes part of the design rather than a loose parameter bag.

---

## 3. Systems Need Swappable Implementations

Deep dive: [03. Factories and Pluggable Backends](/Users/minkyu/Documents/mlsys/python/03-factories-and-pluggable-backends.md)

Many systems want:

- a lightweight local implementation,
- a heavier dependency-backed implementation,
- and a central place to choose between them.

This note covers:

- pluggable backends,
- factory functions,
- optional dependencies,
- and lightweight caching of expensive resources.

---

## 4. Contracts Should Be Explicit

Deep dive: [04. Abstract Base Classes and Interfaces](/Users/minkyu/Documents/mlsys/python/04-abstract-base-classes-and-interfaces.md)

When several implementations must remain interchangeable, interface shape matters.

This note focuses on:

- abstract base classes,
- abstract methods,
- interface-level type hints,
- and when contract definition is more important than shared implementation.

---

## 5. Small Relationship Structures Matter

Deep dive: [05. Small Graph Abstractions](/Users/minkyu/Documents/mlsys/python/05-small-graph-abstractions.md)

Not all important Python design work is about classes and configuration.

Sometimes the key challenge is representing relationships:

- dependencies,
- prerequisites,
- parent-child structure,
- or provenance links.

This note shows how a small graph abstraction can be built from ordinary containers as long as edge semantics are explicit.

---

## 6. Tiny Helpers Still Need Discipline

Deep dive: [06. Small Numeric Utilities and Stability](/Users/minkyu/Documents/mlsys/python/06-small-numeric-utilities-and-stability.md)

Some of the most consequential bugs in systems Python live in very small functions.

This note focuses on:

- numeric helper contracts,
- vector scoring,
- stable normalization,
- and edge-case handling in short but important utility functions.

---

## 7. Why This Branch Matters for ML Systems

ML systems often rely on optimized kernels, runtimes, and compiled libraries for the heavy compute.
But the Python layer still determines a lot of the system's readability and safety.

This branch matters because Python is often where we define:

- configuration schemas,
- orchestration logic,
- backend boundaries,
- relationship tracking,
- and the helper functions that translate signals into operational decisions.

Strong Python structure keeps the control layer from becoming the most fragile part of the system.

---

## 8. Future Expansion

Natural future additions include:

- concurrency and process models,
- context managers and resource ownership,
- packaging and environment boundaries,
- testing patterns for systems code,
- protocol types and structural typing,
- and Python performance traps in data-heavy loops.

---

## 9. After This Branch You Should Understand

After reading this branch, you should be able to explain:

- how Python dataclasses and enums support structured state,
- why configuration objects should be treated as schemas,
- how factories and pluggable backends decouple callers from implementations,
- when abstract base classes help clarify interface contracts,
- how small graph abstractions can be built safely on standard containers,
- and why even short numeric helpers deserve careful treatment of contracts and stability.
