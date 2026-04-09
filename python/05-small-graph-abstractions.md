# 05. Small Graph Abstractions

## What

Python systems code often needs a lightweight way to represent relationships such as:

- dependency edges,
- parent-child structure,
- prerequisite links,
- or graph-shaped bookkeeping.

A practical pattern is to build a small graph abstraction on top of:

- dictionaries,
- sets,
- and a clear edge-direction convention.

## Why It Matters

Graph bugs are often not caused by syntax.
They are caused by semantics:

- what does an edge mean?
- does “outgoing” mean children or prerequisites?
- are incoming and outgoing neighbors both available?

Small graph abstractions are only reliable when those meanings are explicit.

## Core Idea

The pattern is:

```text
document edge meaning -> store sparse adjacency -> expose query methods
```

This keeps the internal representation simple while making the public behavior readable.

## Sparse Adjacency with `defaultdict(set)`

For many relationship graphs, most possible edges do not exist.

That makes sparse adjacency structures a good fit:

```python
from collections import defaultdict


children = defaultdict(set)
parents = defaultdict(set)
```

Sets are useful because they:

- avoid duplicate edges,
- support fast membership tests,
- and naturally represent neighbor collections.

## Incoming and Outgoing Views

Sometimes one direction is not enough.

If queries need both:

- “what does this node point to?”
- and “what points to this node?”

then keeping two adjacency views is often worth the extra bookkeeping cost.

That is a memory-for-query-speed tradeoff.

## Edge Convention Must Be Explicit

This is the most important design point.

A graph API should say clearly what an edge means.

For example:

```text
(task, dependency) means task depends on dependency
```

Without that rule, the meaning of “incoming” and “outgoing” becomes ambiguous.

## Representation Hiding

Query methods should usually hide the raw container layout.

```python
from collections import defaultdict


class TaskGraph:
    def __init__(self) -> None:
        self._parents = defaultdict(set)
        self._children = defaultdict(set)

    def add_dependency(self, task: str, dependency: str) -> None:
        self._parents[task].add(dependency)
        self._children[dependency].add(task)

    def parents_of(self, task: str) -> set[str]:
        return set(self._parents.get(task, set()))

    def children_of(self, task: str) -> set[str]:
        return set(self._children.get(task, set()))
```

Returning copies rather than internal sets helps protect invariants from accidental mutation by callers.

## Derived Metadata with Properties

If some useful quantity can be derived from the structure, a read-only property is often a nice API.

Examples:

- number of nodes,
- number of edges,
- whether the graph is empty.

This keeps the graph interface expressive without exposing internal containers directly.

## Common Mistakes

- Failing to document what an edge direction means.
- Storing only one adjacency direction and then adding slow ad hoc scans for reverse queries later.
- Returning internal mutable containers directly and letting callers accidentally mutate them.
- Treating all graphs as if they need a heavyweight framework when a small container-based abstraction is enough.
- Forgetting that sparse graph design is usually about semantics and query patterns more than about algorithmic cleverness.

## Why This Matters for ML Systems

Relationship structure appears constantly in ML systems:

- dependency tracking,
- caching relationships,
- pipeline prerequisites,
- graph-shaped state transitions,
- and retrieval or provenance links.

A small, explicit graph abstraction is often enough to make that structure readable and safe without introducing unnecessary complexity.

## Short Takeaway

Small graph abstractions work well in Python when edge meaning is explicitly documented, sparse adjacency is stored with dictionaries of sets, and callers interact through clear query methods rather than raw internal containers.
