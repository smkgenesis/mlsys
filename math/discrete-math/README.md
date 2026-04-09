# discrete-math

Discrete mathematics for formal reasoning in ML systems.

Belongs here:
- logic,
- proof techniques,
- sets,
- relations and functions,
- induction,
- countability,
- and combinatorial reasoning.

Does not belong here:
- probabilistic or statistical material whose primary subject is uncertainty; that belongs in [probability-stats/README.md](/Users/minkyu/Documents/mlsys/math/probability-stats/README.md),
- CS-first topics whose main role is introductory computer science rather than mathematical structure; those belong in [foundations/README.md](/Users/minkyu/Documents/mlsys/foundations/README.md).

Current notes:
- [01. Propositional Logic](/Users/minkyu/Documents/mlsys/math/discrete-math/01-propositional-logic.md)
- [01.1 Normal Forms](/Users/minkyu/Documents/mlsys/math/discrete-math/01-1-normal-forms.md)
- [01.2 Quantificational Logic](/Users/minkyu/Documents/mlsys/math/discrete-math/01-2-quantificational-logic.md)
- [01.3 Logic and Computers](/Users/minkyu/Documents/mlsys/math/discrete-math/01-3-logic-and-computers.md)
- [02. Basic Proof Techniques](/Users/minkyu/Documents/mlsys/math/discrete-math/02-basic-proof-techniques.md)
- [03. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/03-sets.md)
- [04. Relations and Functions](/Users/minkyu/Documents/mlsys/math/discrete-math/04-relations-and-functions.md)
- [05. Mathematical Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/05-mathematical-induction.md)
- [06. Strong Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/06-strong-induction.md)
- [07. Structural Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/07-structural-induction.md)
- [08. Countable and Uncountable Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/08-countable-and-uncountable-sets.md)
- [09. Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/09-pigeonhole-principle.md)

---

Discrete Mathematics:
Language -> Formula Structure -> Quantified Structure -> Logic in Hardware -> Proof -> Structured Objects -> Induction -> Infinite Size -> Counting Arguments

This branch is about exact reasoning over discrete objects.

The goal is not only to accumulate definitions.
The goal is to develop the formal habits that later technical work depends on:

- reading statements precisely,
- understanding what a proof must establish,
- reasoning about sets and mappings,
- handling recursively defined objects,
- and turning counting arguments into rigorous conclusions.

The documents in this folder are deep dives.
This README is the parent document that ties them together into one continuous discrete-math learning path.

---

## 0. Scope and Preconditions

This branch assumes only a basic willingness to read formal definitions carefully.

The emphasis here is on:

- exact language,
- logical structure,
- proof form,
- and discrete mathematical objects.

The recurring questions are:

- what exactly is being claimed?
- what counts as a proof?
- what structure does the object have?
- and what later computational or systems reasoning does this support?

---

## 1. Logic Comes First

Deep dive: [01. Propositional Logic](/Users/minkyu/Documents/mlsys/math/discrete-math/01-propositional-logic.md)

The branch starts with propositional logic because mathematical reasoning needs a precise language before it can have rigorous structure.

Logic clarifies:

- truth values,
- connectives,
- implication,
- equivalence,
- and the difference between natural-language intuition and formal statement structure.

Without this layer, later proof techniques are easy to misuse.

---

## 2. Formula Structure Comes Before Algorithmic Logic

Deep dive: [01.1 Normal Forms](/Users/minkyu/Documents/mlsys/math/discrete-math/01-1-normal-forms.md)

Once propositional logic introduces formulas and connectives, the next useful question is:

```text
how should formulas be rewritten so their structure becomes regular?
```

This is where CNF, DNF, literals, and logical-law based normalization enter.

This note matters because it is the first place where logical expressions are treated as structured objects that can be transformed for algorithmic purposes, especially through the connection to satisfiability.

It is a natural bridge from:

- logical meaning

to:

- computational representation

---

## 2.5 Quantified Logic Extends Formula Structure

Deep dive: [01.2 Quantificational Logic](/Users/minkyu/Documents/mlsys/math/discrete-math/01-2-quantificational-logic.md)

Once formulas have a regular structure, the next limitation becomes visible:

```text
propositional logic can combine whole statements,
but it cannot directly talk about objects, properties, and relations
```

Quantificational logic addresses that limitation by introducing:

- predicates,
- variables,
- universes of discourse,
- and quantifiers such as `∀` and `∃`.

This is the step where logical language becomes expressive enough to talk about mathematical structure rather than only truth-functional combinations of atomic statements.

It is also where scope, free versus bound variables, quantifier order, and interpretation become central.

---

## 2.6 Logic Becomes Digital Computation

Deep dive: [01.3 Logic and Computers](/Users/minkyu/Documents/mlsys/math/discrete-math/01-3-logic-and-computers.md)

After formulas have become structured objects, the next natural question is:

```text
how does logic become a real machine?
```

This note answers that by connecting:

- truth values to bits,
- logical operators to gates,
- gates to circuits,
- and circuits to arithmetic components such as half adders, full adders, and ripple-carry adders.

This is an important step because it prevents logic from remaining purely symbolic.
It shows that abstraction and composition are not just proof ideas.
They are also the design principles of digital hardware.

---

## 3. Proof Is the Next Discipline

Deep dive: [02. Basic Proof Techniques](/Users/minkyu/Documents/mlsys/math/discrete-math/02-basic-proof-techniques.md)

Once the language of claims is clear, the next question is:

```text
how do we establish that a claim is actually true?
```

This is where direct proof, contrapositive proof, contradiction, and constructive versus nonconstructive reasoning enter.

This note is the first place where the branch shifts from understanding statements to building valid arguments.

---

## 4. Sets, Relations, and Functions Build the Core Objects

Deep dives:
- [03. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/03-sets.md)
- [04. Relations and Functions](/Users/minkyu/Documents/mlsys/math/discrete-math/04-relations-and-functions.md)

After logic and proof, the next layer is the language of structured objects.

Sets let us reason about:

- membership,
- subset structure,
- operations on collections,
- and Cartesian products.

Relations and functions then turn these collections into structure:

- mappings,
- connections,
- domains and codomains,
- and constraints on allowed behavior.

This is one of the main bridges from pure math toward later CS and systems abstractions.

---

## 5. Induction Handles Infinite Families of Cases

Deep dives:
- [05. Mathematical Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/05-mathematical-induction.md)
- [06. Strong Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/06-strong-induction.md)
- [07. Structural Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/07-structural-induction.md)

Induction is where discrete mathematics becomes scalable.

Instead of proving one case at a time forever, induction gives a way to prove:

- all integers in a range,
- all recursively built objects,
- or all values generated by a constructor system.

Ordinary induction, strong induction, and structural induction are separated here because they support different proof shapes and later apply to different kinds of computational objects.

---

## 6. Size and Counting Become Deeper Than They First Look

Deep dives:
- [08. Countable and Uncountable Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/08-countable-and-uncountable-sets.md)
- [09. Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/09-pigeonhole-principle.md)

The later part of this branch shifts from proof mechanics to deeper discrete structure.

Countability asks:

- what does “size” even mean for infinite sets?

Pigeonhole arguments ask:

- what must be true purely from how objects are distributed across buckets?

These are different topics, but they both develop an important discrete-math habit:

```text
global conclusions can often be forced by abstract structure alone
```

---

## 7. Why This Branch Matters for ML Systems

Discrete mathematics matters to ML systems because exact reasoning still matters even in probabilistic and numerical systems.

This branch supports later understanding of:

- state spaces,
- invariants,
- mappings and interfaces,
- recurrence and recursion,
- symbolic structure,
- and proof-style reasoning about correctness and constraints.

It does not directly teach GPU kernels or model serving.
It teaches the formal habits that prevent sloppy reasoning when systems become complex.

---

## 8. After This Branch You Should Understand

After reading this branch, you should be able to explain:

- how logic shapes formal statements,
- how normal forms turn formulas into algorithmically useful structures,
- how quantificational logic extends symbolic reasoning from whole propositions to objects, properties, and relations,
- how propositional logic becomes gates, circuits, and arithmetic hardware,
- how proof techniques differ,
- how sets, relations, and functions provide the language of structure,
- how the different forms of induction apply,
- and how countability and counting arguments extend discrete reasoning beyond finite toy cases.

That is the core formal reasoning track of the mathematical layer.
