# 01.1 Normal Forms

## What

Normal forms are standardized ways of rewriting propositional formulas so that their structure becomes regular and predictable.

The two most important normal forms are:

- conjunctive normal form (CNF): an AND of ORs
- disjunctive normal form (DNF): an OR of ANDs

Both are built from a simpler unit called a **literal**, which is either:

- a propositional variable such as `p`
- or its negation such as `¬p`

The point of normal forms is not just cosmetic rewriting.
The point is to turn arbitrary logical formulas into forms that are easier to analyze, manipulate, and connect to algorithmic problems such as satisfiability.

## Why It Matters

Propositional logic gives us the language of formulas.
Normal forms make that language computationally usable.

Once a formula is rewritten into a regular structure:

- its truth conditions become easier to reason about,
- equivalence transformations become easier to track,
- and the formula becomes much more suitable for algorithmic treatment.

This matters because many later topics in computer science and systems reasoning rely on formulas not just being meaningful, but being represented in a form that a machine or proof procedure can process effectively.

Normal forms are one of the first places where logical syntax starts to look like a computational object rather than just a mathematical sentence.

## Core Idea

The normalization idea has three parts:

1. use a restricted operator set
2. push negations down to atomic variables
3. enforce a regular top-level structure

The restricted operator set is usually:

- `∧`
- `∨`
- `¬`

The negation rule is:

- `¬` should apply only to atomic variables, not to larger subformulas

The structural rule is:

- CNF must be a conjunction of disjunctions of literals
- DNF must be a disjunction of conjunctions of literals

So normalization is really a structural discipline imposed on formulas.

## Literals

A literal is the basic building block of both CNF and DNF.

Examples of literals:

- `p`
- `¬p`
- `q`
- `¬q`

Examples of non-literals:

- `p ∧ q`
- `¬(p ∨ q)`

The distinction matters because normal forms are built out of clauses whose members must be literals, not arbitrary formulas.

## Conjunctive Normal Form

A formula is in conjunctive normal form if it is:

```text
(Clause 1) ∧ (Clause 2) ∧ ... ∧ (Clause k)
```

where each clause is a disjunction of literals.

So CNF is:

```text
AND of ORs
```

Example:

```text
(p ∨ ¬q ∨ r) ∧ (¬p ∨ s)
```

This is CNF because:

- the whole formula is a conjunction
- each conjunct is a disjunction
- and each item inside those disjunctions is a literal

The truth condition is:

```text
a CNF formula is true iff every clause has at least one true literal
```

## Disjunctive Normal Form

A formula is in disjunctive normal form if it is:

```text
(Clause 1) ∨ (Clause 2) ∨ ... ∨ (Clause k)
```

where each clause is a conjunction of literals.

So DNF is:

```text
OR of ANDs
```

Example:

```text
(p ∧ ¬q ∧ r) ∨ (¬p ∧ s)
```

This is DNF because:

- the whole formula is a disjunction
- each disjunct is a conjunction
- and each item inside those conjunctions is a literal

The truth condition is:

```text
a DNF formula is true iff at least one clause has all of its literals true
```

## Converting from a Truth Table

One way to produce a normal form is directly from the truth table.

### DNF from truth-table rows

To build a DNF:

1. write the truth table
2. find every row where the formula is true
3. for each such row, create a conjunction of literals that matches the row exactly
4. OR those conjunctions together

Example idea:

If a row has:

- `p = T`
- `q = F`

then the corresponding conjunction uses:

- `p`
- `¬q`

This works because that conjunction is true exactly on that row.

Then OR-ing all such row-conjunctions gives a formula that is true on exactly the same rows as the original formula.

### CNF from truth-table rows

The dual idea works for CNF:

1. find every row where the formula is false
2. build one disjunctive clause that rules out each false row
3. AND those clauses together

This method is completely general, but it scales badly because a truth table has `2^n` rows.

So it is conceptually powerful, but often not the best practical conversion method.

## Converting with Logical Laws

The more structural method is to transform the formula using equivalence laws.

### Step 1: Eliminate non-basic connectives

Rewrite operators such as:

- implication
- equivalence
- xor

using only:

- `∧`
- `∨`
- `¬`

Examples:

```text
α -> β ≡ ¬α ∨ β
```

```text
α ↔ β ≡ (α -> β) ∧ (β -> α)
```

```text
α ⊕ β ≡ (α ∧ ¬β) ∨ (¬α ∧ β)
```

### Step 2: Push negations inward

Use:

- De Morgan's laws
- double-negation elimination

so that negation reaches only atomic variables.

Examples:

```text
¬(α ∧ β) ≡ ¬α ∨ ¬β
```

```text
¬(α ∨ β) ≡ ¬α ∧ ¬β
```

```text
¬¬α ≡ α
```

After this step, the formula is in negation normal form style, where `¬` appears only on variables.

### Step 3: Distribute to get the target form

For CNF, distribute `∨` over `∧`.

Example:

```text
α ∨ (β ∧ γ) ≡ (α ∨ β) ∧ (α ∨ γ)
```

For DNF, distribute `∧` over `∨`.

Example:

```text
α ∧ (β ∨ γ) ≡ (α ∧ β) ∨ (α ∧ γ)
```

This is the step that creates the required top-level clause structure.

## Why CNF and DNF Feel Different

Although CNF and DNF are dual forms, they tend to support different reasoning styles.

CNF emphasizes:

- all constraints must be satisfied
- each clause forbids certain bad assignments

DNF emphasizes:

- any one satisfying pattern is enough
- each clause describes one good assignment pattern

So even when both forms are equivalent representations of the same formula, they often support different computational or explanatory uses.

## SAT and Why CNF Matters So Much

Normal forms are tightly connected to satisfiability.

The satisfiability question is:

```text
given a formula, is there some assignment of truth values that makes it true?
```

This becomes especially important for CNF formulas.

Why:

- many practical search and verification problems can be encoded as CNF-SAT
- SAT solvers are typically built around CNF input
- and CNF-SAT is one of the central problems in computational complexity

The lecture also highlights an important asymmetry:

- DNF satisfiability is easy to test
- CNF satisfiability is computationally much harder in general

So normal forms are not just stylistic rewrites.
They directly affect how hard the resulting computational problem is.

## Trade-offs

- Truth-table conversion is conceptually clean and fully general, but scales exponentially with the number of variables.
- Law-based conversion is more practical and more algebraic, but it requires careful control of equivalence steps.
- CNF is often the most useful form for SAT-style reasoning, but the conversion process can increase formula size.
- DNF can make satisfiability visually obvious, but it can also become very large and awkward.

## Common Mistakes

- Thinking CNF means “OR of ANDs” instead of “AND of ORs”.
- Thinking DNF means “AND of ORs” instead of “OR of ANDs”.
- Treating arbitrary subformulas as literals.
- Forgetting to eliminate `->`, `↔`, or `⊕` before trying to normalize.
- Leaving negations wrapped around compound formulas instead of pushing them inward.
- Applying distributive laws in the wrong direction for the target normal form.
- Assuming truth-table conversion is computationally cheap just because it is conceptually simple.
- Thinking normal forms are only notation and not computationally important.

## Why This Matters for ML Systems

Normal forms do not directly teach GPU kernels or model serving, but they matter because they train an important habit:

```text
the representation of a problem affects how easy it is to analyze and solve
```

That habit appears everywhere in ML systems:

- choosing the right tensor layout,
- rewriting computation for hardware efficiency,
- normalizing operator structure for compilers,
- and converting high-level constraints into forms that optimizers or solvers can handle.

At a broader computer-systems level, CNF and SAT are also part of the background of:

- verification,
- automated reasoning,
- compiler analyses,
- and constraint-solving formulations.

So this topic is one of the first clean examples where a purely symbolic rewrite changes algorithmic tractability.

## Short Takeaway

Normal forms rewrite logical formulas into standardized structures built from literals: CNF is an AND of OR-clauses, DNF is an OR of AND-clauses, and both can be obtained either from truth tables or from logical equivalence laws. Their importance is not just syntactic regularity but the fact that representation shape strongly affects algorithmic reasoning, especially through the SAT connection.
