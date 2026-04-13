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
- [Chapter 1. The Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-01-pigeonhole-principle.md)
- [01. Propositional Logic](/Users/minkyu/Documents/mlsys/math/discrete-math/01-propositional-logic.md)
- [01.1 Normal Forms](/Users/minkyu/Documents/mlsys/math/discrete-math/01-1-normal-forms.md)
- [01.2 Quantificational Logic](/Users/minkyu/Documents/mlsys/math/discrete-math/01-2-quantificational-logic.md)
- [01.3 Logic and Computers](/Users/minkyu/Documents/mlsys/math/discrete-math/01-3-logic-and-computers.md)
- [Chapter 2. Basic Proof Techniques](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-02-basic-proof-techniques.md)
- [Chapter 5. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-05-sets.md)
- [03. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/03-sets.md)
- [04. Relations and Functions](/Users/minkyu/Documents/mlsys/math/discrete-math/04-relations-and-functions.md)
- [Chapter 3. Proof by Mathematical Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-03-proof-by-mathematical-induction.md)
- [Chapter 4. Strong Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-04-strong-induction.md)
- [07. Structural Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/07-structural-induction.md)
- [08. Countable and Uncountable Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/08-countable-and-uncountable-sets.md)

---

Discrete Mathematics:
Chapter-Driven Course Sequence -> Problem Patterns -> Formal Language -> Proof -> Structured Objects -> Induction -> Infinite Size

This branch is about exact reasoning over discrete objects.

The goal is not only to accumulate definitions.
The goal is to develop the formal habits that later technical work depends on:

- reading statements precisely,
- understanding what a proof must establish,
- reasoning about sets and mappings,
- handling recursively defined objects,
- and turning counting arguments into rigorous conclusions.

The documents in this folder are deep dives.
This README now also functions as the operating document for working through the course chapter by chapter.

At the moment, the active focus is:

```text
Chapter 5 -> Sets
```

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

## 1. Current Chapter-Driven Focus

The branch was originally organized as a topic-first deep-dive sequence:

- logic
- proof
- sets and functions
- induction
- countability
- combinatorial reasoning

That material is still useful and remains in the folder.

But for current exam prep, we are switching to a chapter-driven order based on the course material you are sending.
So for now, the active sequence is:

- [Chapter 1. The Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-01-pigeonhole-principle.md)
- [Chapter 2. Basic Proof Techniques](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-02-basic-proof-techniques.md)
- [Chapter 3. Proof by Mathematical Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-03-proof-by-mathematical-induction.md)
- [Chapter 4. Strong Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-04-strong-induction.md)
- [Chapter 5. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-05-sets.md)

Later chapters can be rethreaded into this README the same way as you send them.

The practical rule for now is:

```text
chapter order drives the active study flow;
older topic-first notes remain available as supporting references.
```

---

## 2. Chapter 1: The Pigeonhole Principle

Deep dive: [Chapter 1. The Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-01-pigeonhole-principle.md)

Chapter 1 is doing more than just introducing one theorem.
It sets the tone for the course:

- proof is different from testing or intuition,
- generalization turns examples into reusable statements,
- sets/functions/cardinality are the right abstraction layer,
- and many arguments reduce to choosing the right objects and buckets.

The chapter's main ideas are:

- proof and formal reasoning matter,
- generalization removes irrelevant detail,
- sets, cardinality, and functions support abstraction,
- the ordinary pigeonhole principle,
- the extended pigeonhole principle,
- and basic number-theoretic language used in the exercises.

So even though the named theorem is simple, the chapter is really building:

- problem-classification habits,
- abstraction habits,
- and early existence-proof intuition.

---

## 3. Chapter 2: Basic Proof Techniques

Deep dive: [Chapter 2. Basic Proof Techniques](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-02-basic-proof-techniques.md)

Chapter 2 turns the course from “what patterns force a result?” into “what counts as a proof of that result?”

The chapter's main ideas are:

- implication and its logically related forms,
- converse, inverse, and contrapositive,
- direct proof,
- proof by contrapositive,
- proof by contradiction,
- constructive versus nonconstructive existence,
- and careful critique of arguments that look plausible but contain an invalid step.

So the chapter is really building:

- proof-method selection,
- exact reading of logical form,
- and early proof-writing discipline.

---

## 4. Chapter Exercise Pattern Notes

The worked exercise patterns for each active chapter now live inside the chapter note itself:

- [Chapter 1. The Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-01-pigeonhole-principle.md)
- [Chapter 2. Basic Proof Techniques](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-02-basic-proof-techniques.md)
- [Chapter 3. Proof by Mathematical Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-03-proof-by-mathematical-induction.md)
- [Chapter 4. Strong Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-04-strong-induction.md)
- [Chapter 5. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-05-sets.md)

That is the better place for them because:

- the exercise patterns stay close to the theorem and notation they use,
- the README can stay focused on chapter flow,
- and later chapters can follow the same structure without turning the parent README into one giant exercise notebook.

---

## 5. Earlier Topic-First Notes Still Available

The remaining discrete-math notes were written earlier in a topic-first order and are still useful as references:

- [01. Propositional Logic](/Users/minkyu/Documents/mlsys/math/discrete-math/01-propositional-logic.md)
- [01.1 Normal Forms](/Users/minkyu/Documents/mlsys/math/discrete-math/01-1-normal-forms.md)
- [01.2 Quantificational Logic](/Users/minkyu/Documents/mlsys/math/discrete-math/01-2-quantificational-logic.md)
- [01.3 Logic and Computers](/Users/minkyu/Documents/mlsys/math/discrete-math/01-3-logic-and-computers.md)
- [Chapter 2. Basic Proof Techniques](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-02-basic-proof-techniques.md)
- [03. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/03-sets.md)
- [04. Relations and Functions](/Users/minkyu/Documents/mlsys/math/discrete-math/04-relations-and-functions.md)
- [Chapter 3. Proof by Mathematical Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-03-proof-by-mathematical-induction.md)
- [Chapter 4. Strong Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-04-strong-induction.md)
- [Chapter 5. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-05-sets.md)
- [07. Structural Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/07-structural-induction.md)
- [08. Countable and Uncountable Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/08-countable-and-uncountable-sets.md)

These should now be read as supporting material rather than the active chapter flow.

---

## 6. Chapter 3: Proof by Mathematical Induction

Deep dive: [Chapter 3. Proof by Mathematical Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-03-proof-by-mathematical-induction.md)

Chapter 3 is where proof technique becomes scalable.
Instead of proving one case and stopping, the chapter teaches how to prove an infinite family of statements by organizing them into:

- base case,
- induction hypothesis,
- induction step,
- and conclusion.

The chapter's main ideas are:

- the difference between everyday induction and mathematical induction,
- the exact logical role of the induction hypothesis,
- choosing the right induction variable,
- sum and product identities,
- non-algebraic induction proofs such as pigeonhole arguments,
- and recognizing when a proof by induction silently fails.

So the chapter is really building:

- proof skeleton discipline,
- “old case inside the new case” pattern recognition,
- and confidence with infinite families of discrete claims.

---

## 7. Chapter 4: Strong Induction

Deep dive: [Chapter 4. Strong Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-04-strong-induction.md)

Chapter 4 extends ordinary induction by widening the induction hypothesis.
Instead of assuming only one previous case, it allows the proof step to use all earlier cases in a valid range.

The chapter's main ideas are:

- why multiple base cases may be needed,
- how strong induction differs from ordinary induction in proof form,
- when a recurrence or decomposition forces a stronger hypothesis,
- and why strong induction is often the natural tool in number theory and recursive structures.

So the chapter is really building:

- a reusable strong-induction answer template,
- recognition of multi-step recurrence dependencies,
- and the habit of matching proof method to structural dependency.

---

## 8. Chapter 5: Sets

Deep dive: [Chapter 5. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-05-sets.md)

Chapter 5 shifts the course from proof form into the language of mathematical objects.
Instead of asking only whether a claim is true, it asks what kind of collection is being described, how those collections interact, and how their sizes behave.

The chapter's main ideas are:

- membership versus subset,
- equality by double inclusion,
- power sets and their cardinalities,
- set-builder notation,
- complements, unions, intersections, and differences,
- Cartesian products and ordered pairs,
- and counting with overlapping sets.

So the chapter is really building:

- comfort with formal object notation,
- the habit of reading braces and nesting carefully,
- and the set-theoretic language that later relations, functions, and counting arguments rely on.

---

## 9. Logic Comes First as Background

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

## 9. Formula Structure Comes Before Algorithmic Logic

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

## 10. Quantified Logic Extends Formula Structure

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

## 11. Logic Becomes Digital Computation

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

Deep dive: [Chapter 2. Basic Proof Techniques](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-02-basic-proof-techniques.md)

Once the language of claims is clear, the next question is:

```text
how do we establish that a claim is actually true?
```

This is where direct proof, contrapositive proof, contradiction, and constructive versus nonconstructive reasoning enter.

This note is the first place where the branch shifts from understanding statements to building valid arguments.

---

## 4. Sets, Relations, and Functions Build the Core Objects

Deep dives:
- [Chapter 5. Sets](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-05-sets.md)
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
- [Chapter 3. Proof by Mathematical Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-03-proof-by-mathematical-induction.md)
- [Chapter 4. Strong Induction](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-04-strong-induction.md)
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
- [Chapter 1. The Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/chapter-01-pigeonhole-principle.md)

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
