# math

Mathematics as a long-term foundation layer for ML systems.

Belongs here:
- the mathematical branches that support later ML systems reasoning,
- formal language and proof habits,
- probability and statistics,
- and future branches such as linear algebra, calculus, ODEs, PDEs, and numerical analysis.

Does not belong here:
- CS-first reasoning topics whose center of gravity is computer science rather than mathematics; those belong in [foundations/README.md](/Users/minkyu/Documents/mlsys/foundations/README.md),
- cross-topic study strategy; that belongs in [roadmap/README.md](/Users/minkyu/Documents/mlsys/roadmap/README.md),
- or hardware/programming-model material that belongs elsewhere in the repository.

Current branches:
- [discrete-math/](/Users/minkyu/Documents/mlsys/math/discrete-math/README.md)
- [probability-stats/](/Users/minkyu/Documents/mlsys/math/probability-stats/README.md)

---

Math for ML Systems:
Formal Reasoning -> Structured Objects -> Uncertainty -> Statistics -> Future Continuous Mathematics

This directory is the top-level mathematics guide for the repository.

The goal is not to treat mathematics as one undifferentiated bucket.
The goal is to organize the mathematical foundation in a way that can keep growing without losing clarity.

Right now the mathematical material naturally falls into two strong branches:

- discrete mathematics,
- probability and statistics.

Later, this top-level `math/` directory should also be able to absorb additional branches such as:

- linear algebra,
- calculus,
- ODEs,
- PDEs,
- numerical methods,
- and information theory or optimization if they grow large enough to deserve their own tracks.

So `math/` should be read as a **branching parent guide**, not as one flat study sequence.

---

## 0. Scope and Philosophy

Mathematics in this repository is not here for exam nostalgia or for abstract completeness.

It is here because serious ML systems work eventually depends on:

- formal reasoning,
- approximation,
- uncertainty,
- distributional thinking,
- stability,
- and disciplined quantitative interpretation.

That means the mathematical standard in this repository is:

- conceptually clear,
- reusable,
- connected to later systems reasoning,
- and organized in a way that makes future growth easier rather than harder.

The right question is not:

```text
is this math?
```

The right question is:

```text
which mathematical branch does this belong to,
and what later systems understanding does it support?
```

---

## 1. Why the Math Layer Needs Branches

The old `math/` folder was beginning to mix together two very different learning arcs:

- formal discrete reasoning,
- and probabilistic/statistical reasoning.

They are connected, but they are not the same progression.

Discrete mathematics is mainly about:

- precise language,
- proof,
- set-based structure,
- countability,
- induction,
- and combinatorial reasoning.

Probability and statistics are mainly about:

- uncertainty,
- random variables,
- distributions,
- summary measures,
- moments,
- and later estimation and inference.

Keeping them as separate subtracks makes the repository more extensible.
It also avoids a flat `math/` folder that becomes increasingly hard to navigate as more mathematical areas are added.

---

## 2. Discrete Mathematics

Deep dive: [discrete-math/README.md](/Users/minkyu/Documents/mlsys/math/discrete-math/README.md)

The discrete-math branch is about the formal language of exact reasoning.

It begins with:

- propositional logic,
- proof techniques,
- sets,
- relations and functions,
- and the various forms of induction.

Then it expands into:

- countability,
- combinatorial reasoning,
- and later, if needed, more graph- or structure-oriented topics.

This branch matters because it builds the habits needed to:

- interpret definitions carefully,
- distinguish examples from proofs,
- reason about finite and infinite structured objects,
- and explain algorithms or system behavior without hand-waving.

It is the branch that sharpens mathematical discipline.

---

## 3. Probability and Statistics

Deep dive: [probability-stats/README.md](/Users/minkyu/Documents/mlsys/math/probability-stats/README.md)

The probability-stats branch is about uncertainty and quantitative summaries.

It begins with:

- the language of probability,
- random variables,
- distributions,
- measures of location and spread,
- and moments and inequalities.

Later it should be able to grow naturally into:

- covariance and correlation,
- common named distributions,
- LLN and CLT,
- estimation,
- hypothesis testing,
- Bayesian reasoning,
- and concentration tools.

This branch matters because ML systems work constantly runs into:

- uncertainty in data,
- noisy measurements,
- tail behavior,
- distribution shift,
- and the need to summarize and interpret system behavior statistically.

It is the branch that sharpens quantitative judgment under uncertainty.

---

## 4. Why Both Branches Matter for ML Systems

These two mathematical branches support different but complementary parts of ML systems thinking.

Discrete mathematics helps with:

- formal correctness,
- definitions and invariants,
- state and structure,
- algorithmic decomposition,
- and exact reasoning about symbolic objects.

Probability and statistics help with:

- noisy systems,
- measurement,
- uncertainty,
- calibration,
- empirical variability,
- and distributional interpretation.

A good ML systems engineer eventually needs both:

- the ability to reason exactly,
- and the ability to reason probabilistically.

That is why `math/` should guide both branches rather than collapsing them into one sequence.

---

## 5. Future Expansion

This top-level `math/` directory is intentionally designed to grow.

The current two branches are only the beginning.

Natural future branches include:

- linear algebra,
- calculus,
- ODEs,
- PDEs,
- numerical analysis,
- and information theory.

When those arrive, the correct move is not to pile them into one huge flat directory.
The correct move is to keep `math/` as the top-level guide and let each branch develop its own coherent sequence beneath it.

So this directory should be read as the mathematical map of the repository, not as a complete or final list.

---

## 6. After This Directory You Should Understand

After using this top-level `math/` guide, you should understand:

- why the mathematical layer is being split into branches,
- what discrete mathematics contributes to ML systems reasoning,
- what probability and statistics contribute,
- and how future areas such as linear algebra, calculus, ODEs, and PDEs should fit into the same structure later.

This README is not the place where all mathematics is taught directly.
It is the place where the mathematical foundation of the repository is kept coherent as it grows.
