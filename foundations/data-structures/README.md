# data-structures

Data structures as a foundations subtrack.

Belongs here:
- introductory data-structure concepts,
- representation and processing efficiency,
- asymptotic reasoning,
- and later the core structures taught in a standard data-structures course.

Does not belong here:
- mathematics-first asymptotic proofs that belong in [math/README.md](/Users/minkyu/Documents/mlsys/math/README.md),
- language-only Java syntax notes without data-structure context,
- or systems-heavy performance topics whose main lesson is hardware behavior rather than data organization.

Current notes:
- [00. Introduction and Efficiency](/Users/minkyu/Documents/mlsys/foundations/data-structures/00-introduction-and-efficiency.md)
- [01. Preprocessing, Worst Case, and Tradeoffs](/Users/minkyu/Documents/mlsys/foundations/data-structures/01-preprocessing-worst-case-and-tradeoffs.md)
- [02. Java for C++ Programmers](/Users/minkyu/Documents/mlsys/foundations/data-structures/02-java-for-cpp-programmers.md)
- [03. Arrays and Sorted Arrays](/Users/minkyu/Documents/mlsys/foundations/data-structures/03-arrays-and-sorted-arrays.md)
- [04. Linked Lists Introduction](/Users/minkyu/Documents/mlsys/foundations/data-structures/04-linked-lists-introduction.md)

---

Data Structures:
Why Data Structures -> Data Organization -> Efficiency Measurement -> Order of Growth -> Workload Tradeoffs -> Java Object Model -> Arrays -> Sorted Arrays and Binary Search -> Linked Lists -> Core Structures

This branch is for learning data structures as a subject, not just as a list of implementations.

The opening material in this branch starts before linked lists or trees.
It starts with the reason the subject exists:

- computers store and process information,
- the way data is organized affects how efficiently processing can happen,
- and we need principled ways to compare one approach with another.

That is why the first note in this branch is about introduction and efficiency rather than about a specific structure.

---

## 0. Scope of the Current Material

At the moment, this branch is intentionally narrow because it is being built from course materials.

The current scope is only:

- why data structures matter,
- how data organization affects processing,
- how to compare alternative implementations,
- how asymptotic notation abstracts away platform-specific constants,
- how worst-case reasoning differs from content-sensitive running time,
- how preprocessing changes time and space tradeoffs for repeated queries,
- the Java reference model needed for later object-based implementations,
- basic Java file I/O used in the course,
- the array as the first concrete structure,
- the tradeoff between unsorted and sorted arrays,
- and the linked list as the first noncontiguous alternative.

Later notes should be added only when they are supported by the course material you provide.

---

## 1. Why Data Structures Exist

Deep dive: [00. Introduction and Efficiency](/Users/minkyu/Documents/mlsys/foundations/data-structures/00-introduction-and-efficiency.md)

The branch begins with a very basic but important observation:

```text
data organization in storage affects processing efficiency
```

That is the core motivation of the subject.

This note also introduces:

- empirical versus analytical performance comparison,
- exact running-time formulas,
- and the transition from exact cost counting to asymptotic order of growth.

---

## 2. Why Workload Tradeoffs Matter

Deep dive: [01. Preprocessing, Worst Case, and Tradeoffs](/Users/minkyu/Documents/mlsys/foundations/data-structures/01-preprocessing-worst-case-and-tradeoffs.md)

The second note extends the first one in an important direction:

- running time can depend on input contents as well as input size,
- worst-case complexity becomes the default comparison lens,
- and preprocessing can reduce future query cost at the expense of time and space up front.

This is the first point in the branch where the material starts to look like real data-structure design rather than standalone runtime counting.

---

## 3. Why the Java Bridge Belongs Here

Deep dive: [02. Java for C++ Programmers](/Users/minkyu/Documents/mlsys/foundations/data-structures/02-java-for-cpp-programmers.md)

Although this note is language-facing, it still belongs inside the data-structures branch because later structures in the course will be implemented with Java classes, references, and object mutation.

The point of this note is not to teach Java in general.
It is to make later structure implementations readable and writable.

---

## 4. Why Arrays Are the First Real Structure

Deep dive: [03. Arrays and Sorted Arrays](/Users/minkyu/Documents/mlsys/foundations/data-structures/03-arrays-and-sorted-arrays.md)

This note is where the branch moves from general reasoning into the first concrete representation.

It introduces:

- homogeneous indexed storage,
- `O(1)` access with a known index,
- sorted arrays and binary search,
- and the tradeoff between faster lookup and slower updates.

---

## 5. Why Linked Lists Come Next

Deep dive: [04. Linked Lists Introduction](/Users/minkyu/Documents/mlsys/foundations/data-structures/04-linked-lists-introduction.md)

The linked list appears immediately after arrays because it directly answers the array's main weakness.

Instead of relying on contiguous storage and direct indexing, it uses:

- nodes,
- links,
- and a first-node reference.

This changes access and update costs in a different direction.

---

## 6. Why This Subtrack Is Special

This subtrack may eventually be used differently from ordinary concept branches because data structures are often tested through hand coding and structural mutation.

But the branch should still be built from source material first.

That means:

- concepts come before templates,
- invariants come before memorization,
- and any future coding skeletons should be derived from the notes rather than invented independently.

---

## 7. Why This Branch Matters

Data structures matter because organization is not separate from efficiency.

A computer does not merely “have data.”
It stores information in a form that changes how expensive retrieval, modification, and processing will be.

That is why the subject sits so early in computer-science curricula.

---

## 8. After This Branch You Should Understand

After the current material in this branch, you should be able to explain:

- why data organization matters for computation,
- why two correct programs can still differ dramatically in efficiency,
- how analytical cost comparison differs from empirical timing,
- how `O`, `Ω`, and `Θ` notation capture dominant growth behavior while ignoring irrelevant constant factors,
- why worst-case complexity is the usual baseline,
- how preprocessing changes the time and space cost of repeated queries,
- how Java object references differ from primitive values,
- how basic Java file I/O appears in the course,
- why arrays support fast indexed access,
- how sorted arrays trade update cost for faster search,
- and why linked lists replace contiguous storage with explicit links.
