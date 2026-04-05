# Universal Problem-Solving Template

## What

This note presents a reusable problem-solving template that works across:

- mathematics,
- computer science,
- and physics.

The goal is not to force all problems into the same style, but to provide a stable sequence of questions that helps with:

- defining the problem clearly,
- choosing the right representation,
- applying first principles,
- executing a solution path,
- and verifying that the result is actually correct.

## Why It Matters

A large fraction of difficulty in technical work does not come from raw complexity alone. It comes from:

- solving the wrong problem,
- choosing the wrong representation,
- skipping governing principles,
- or failing to verify the result against constraints and edge cases.

A universal template is useful because it gives you a default reasoning structure even when the domain changes.

That matters especially when moving between:

- abstract proof-oriented work in mathematics,
- algorithmic or systems work in computer science,
- and model-based quantitative reasoning in physics.

## The Core Template

The full template is:

1. Define the problem precisely.
2. Classify the problem type.
3. Choose the right representation.
4. Identify governing principles.
5. Decompose into subproblems.
6. Generate candidate approaches.
7. Choose a working path.
8. Execute mechanically.
9. Verify the result.
10. Generalize and compress the lesson.

This can be remembered in a shorter form:

```text
Define -> Classify -> Represent -> Apply principles -> Decompose -> Solve -> Verify -> Generalize
```

## 1. Define the Problem Precisely

Start by removing ambiguity.

Ask:

- What exactly is being asked?
- What is known?
- What is unknown?
- What counts as a valid solution?
- What are the constraints?

Good output for this step:

- a one-sentence problem statement,
- a list of givens,
- a list of targets,
- a list of constraints.

This step is often skipped, and skipping it causes a surprising amount of downstream confusion.

## 2. Classify the Problem Type

Different problem types reward different methods.

Ask:

- Is this a proof problem, derivation, computation, design problem, optimization problem, or debugging problem?
- Is it static or dynamic?
- Is it deterministic or probabilistic?
- Is it discrete or continuous?
- Is it local or system-level?

This step helps narrow the likely tool family before you start manipulating details.

## 3. Choose the Right Representation

A large share of problem solving is representation choice.

Ask:

- What formal structure best captures the problem?
- Should this be written as equations, a graph, a state machine, a geometric picture, a data structure, a circuit, or a conservation model?

Examples:

- mathematics: definitions, symbolic structure, theorem conditions, geometric interpretation,
- computer science: input-output model, invariants, state transitions, data structures,
- physics: system boundary, coordinates, forces, fields, conserved quantities.

A bad representation can make an easy problem look impossible. A good representation often makes the solution path obvious.

## 4. Identify Governing Principles

Before attempting solution details, identify what must remain true.

Ask:

- What definitions, laws, invariants, axioms, or constraints govern this problem?
- What conservation laws or monotonicity properties apply?
- What cannot happen?

Examples:

- mathematics: definitions, theorem hypotheses, inequalities, algebraic identities,
- computer science: loop invariants, complexity bounds, correctness requirements, abstraction boundaries,
- physics: conservation of energy, momentum, charge, symmetry, constitutive laws, dimensional consistency.

This step anchors the work in first principles instead of local guesswork.

## 5. Decompose into Subproblems

Most complex problems become tractable only after decomposition.

Ask:

- Can the problem be split into stages?
- What is the bottleneck?
- What is essential and what is incidental?
- Which part is conceptual, and which part is mechanical?

Useful decomposition patterns include:

- input / transformation / output,
- local behavior / global behavior,
- existence / construction / efficiency,
- model / implementation / measurement.

This produces an ordered set of smaller problems instead of one large undifferentiated one.

## 6. Generate Candidate Approaches

Do not commit to the first idea too early.

Ask:

- What are two to four plausible approaches?
- Can I solve a special case first?
- Can I work backward from the target?
- Can I reduce this to a known problem?
- Can I bound it, simulate it, or check a limiting case?

This step is especially useful when the first attempted method starts becoming fragile or overly complicated.

## 7. Choose a Working Path

Pick the approach with the best balance of:

- simplicity,
- correctness,
- explanatory power,
- and likelihood of finishing cleanly.

This is not the stage to optimize for elegance. It is the stage to choose a path that is likely to produce a correct answer that can be checked.

## 8. Execute Mechanically

Now do the actual work:

- derive,
- prove,
- compute,
- implement,
- simulate,
- or test.

Two important habits matter here:

- keep assumptions explicit,
- and separate exploratory work from final presentation.

This helps prevent “symbol pushing” or implementation drift without understanding.

## 9. Verify the Result

Verification is not optional.

Ask:

- Does the result answer the original question?
- Are units or dimensions correct?
- Are edge cases handled?
- Does the simple-case behavior make sense?
- Does the asymptotic or limiting behavior make sense?
- Does anything violate a known law or invariant?

Domain-specific checks:

- mathematics: logical validity, coverage of assumptions, hidden exceptions,
- computer science: correctness, complexity, failure modes, boundary conditions,
- physics: dimensional analysis, conservation laws, sign sanity, limiting regimes.

Many wrong answers survive until this step because they are locally plausible but globally inconsistent.

## 10. Generalize and Compress

After solving the problem, extract the reusable lesson.

Ask:

- What pattern actually made this work?
- When would this method fail?
- What is the shortest correct reusable summary?
- Does this suggest a named technique, theorem pattern, invariant, or workflow?

This is where isolated problem solving turns into durable knowledge.

## Domain Emphasis

The same template applies across domains, but the emphasis changes.

### Mathematics

Emphasize:

- precise definitions,
- theorem conditions,
- proof structure,
- special cases,
- and counterexamples.

The biggest failure mode is often manipulating symbols before understanding the exact statement being proved.

### Computer Science

Emphasize:

- abstraction boundaries,
- invariants,
- algorithm and data structure choice,
- complexity,
- implementation constraints,
- and testing or failure modes.

The biggest failure mode is often solving the formal problem while missing the system bottleneck or implementation reality.

### Physics

Emphasize:

- system boundary,
- governing laws,
- units and dimensions,
- approximations,
- and limiting regimes.

The biggest failure mode is often writing equations before identifying the actual physical model and its assumptions.

## The Three Failure Modes to Watch

Across all three fields, a large fraction of failure comes from missing one of these:

1. the right representation,
2. the governing principle,
3. the verification step.

If stuck, ask:

- am I representing the problem badly?
- am I missing the central law, definition, or invariant?
- am I manipulating details without checking meaning?

## A Practical Working Version

For day-to-day use, the template can be compressed further into:

1. Define
2. Classify
3. Represent
4. Apply first principles
5. Decompose
6. Solve
7. Verify
8. Generalize

This shorter version is easier to keep in working memory while still preserving the structure that prevents sloppy reasoning.

## Why This Matters for ML Systems

Even though this template is universal, it is especially useful for ML systems work because ML systems constantly combine:

- mathematical reasoning,
- computer systems reasoning,
- and physical hardware constraints.

The same project may require:

- a mathematical argument about optimization or error,
- a computer science argument about correctness or complexity,
- and a physics-style argument about bandwidth, energy, or scaling limits.

A universal template helps keep those transitions disciplined instead of ad hoc.
