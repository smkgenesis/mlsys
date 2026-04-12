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
- [09. Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/09-pigeonhole-principle.md)
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
Chapter 1 -> The Pigeonhole Principle
```

and the worked exercises below are organized with that chapter-first priority in mind.

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
So for now, the active sequence begins with:

- [09. Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/09-pigeonhole-principle.md)

Later chapters can be rethreaded into this README the same way as you send them.

The practical rule for now is:

```text
chapter order drives the active study flow;
older topic-first notes remain available as supporting references.
```

---

## 2. Chapter 1: The Pigeonhole Principle

Deep dive: [09. Pigeonhole Principle](/Users/minkyu/Documents/mlsys/math/discrete-math/09-pigeonhole-principle.md)

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

## 3. Chapter 1 Exercise Patterns Worked So Far

The following exercise set has already been worked through from the Chapter 1 material.

### 1.1 Basic notation and number facts

Covered:

- cardinality `|X|`
- floor and ceiling
- divisors
- prime divisors

Main pattern:

```text
decode notation first;
then compute the concrete value carefully.
```

### 1.2 Largest prime divisor is not monotone

Main lesson:

- larger integer does not imply larger largest-prime-divisor

Main pattern:

- small counterexample construction

### 1.3 Floor and ceiling relation

Main lesson:

- `floor(x) = ceiling(x) - 1` exactly when `x` is not an integer

Main pattern:

- split into integer versus non-integer cases

### 1.4 Pigeons moving on a sphere/grid-style parity problem

Main lesson:

- coloring/parity can create bucket imbalance

Main pattern:

- put two points on the boundary
- use a two-color or two-region argument
- then apply pigeonhole

### 1.5 Same number of friends

Main lesson:

- possible degree values appear to be `0` through `n-1`,
- but the extremes `0` and `n-1` cannot coexist

Main pattern:

- eliminate impossible extreme combination
- then apply pigeonhole to degree counts

### 1.6 Four points on a sphere and a closed hemisphere

Main lesson:

- great-circle boundary points belong to both closed hemispheres

Main pattern:

- put two chosen points on the boundary
- distribute the remaining points between the two hemispheres
- force two into one side by pigeonhole

### 1.7 Three birthdays in the same month

Main pattern:

- if every month had at most two people, total would be at most `24`
- one more person forces a month with at least three

### 1.8 One denomination appears at least 100 times

Main pattern:

- extended pigeonhole threshold

\[
(k-1)m + 1
\]

with:

- `m = 6` denominations
- `k = 100`

### 1.9 Same class and same shirt color

Main lesson:

- sometimes the right buckets are combinations, not single features

Main pattern:

- use `(class, color)` as the bucket
- total buckets = `8 * 3 = 24`

### 1.10 Four integers between 1 and 60

Main lesson:

- interval partitioning can be the cleanest pigeonhole move

Main pattern:

- split `1..60` into three blocks of length 20
- four integers force two into one block
- same block means difference at most 19

### 1.11 Product of first k primes plus 1

Main lesson:

- Euclid-style construction is not always prime

Main pattern:

- if the number is composite, its prime factors are new
- but finding the first example is basically example search

Known example:

\[
2 \cdot 3 \cdot 5 \cdot 7 \cdot 11 + 1 = 2311 = 13 \cdot 179
\]

### 1.12 Same prime factors less than or equal to 5

Main lesson:

- divisibility by `2, 3, 5` defines an 8-type signature

Main pattern:

- classify each integer by a subset of `{2,3,5}`
- there are `2^3 = 8` possible signatures
- 9 integers force a repeated signature

### 1.13 Hash collisions

Main lesson:

- collision problems reduce to occupancy problems

Main pattern:

- strings are objects
- hash values are buckets
- largest bucket minimum size is `ceil(m/p)`
- collisions in a bucket = occupancy minus 1

So for this chapter, the recurring pattern library already includes:

- direct bucket counting
- extended thresholds
- interval partitioning
- composite buckets such as ordered pairs
- divisibility signatures
- extremal impossibility arguments
- and geometry/parity reductions into bucket language

---

## 4. Earlier Topic-First Notes Still Available

The remaining discrete-math notes were written earlier in a topic-first order and are still useful as references:

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

These should now be read as supporting material rather than the active chapter flow.

---

## 5. Logic Comes First as Background

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
