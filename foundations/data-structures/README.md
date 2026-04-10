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
- [05. Linked List Iterators](/Users/minkyu/Documents/mlsys/foundations/data-structures/05-linked-list-iterators.md)
- [06. Circular Linked Lists](/Users/minkyu/Documents/mlsys/foundations/data-structures/06-circular-linked-lists.md)
- [07. Stacks and Queues](/Users/minkyu/Documents/mlsys/foundations/data-structures/07-stacks-and-queues.md)
- [08. Recursion and Explicit Stack Simulation](/Users/minkyu/Documents/mlsys/foundations/data-structures/08-recursion-and-explicit-stack-simulation.md)
- [09. Expression Processing with Stacks](/Users/minkyu/Documents/mlsys/foundations/data-structures/09-expression-processing-with-stacks.md)
- [10. Sorting Basics and Divide-and-Conquer](/Users/minkyu/Documents/mlsys/foundations/data-structures/10-sorting-basics-and-divide-and-conquer.md)
- [11. Quick Sort and In-Place Partitioning](/Users/minkyu/Documents/mlsys/foundations/data-structures/11-quick-sort-and-in-place-partitioning.md)
- [12. Trees and Binary Tree Basics](/Users/minkyu/Documents/mlsys/foundations/data-structures/12-trees-and-binary-tree-basics.md)

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
- the linked list as the first noncontiguous alternative,
- iterator-based traversal as the linked-list alternative to array indexing,
- circular linked lists with header nodes as a way to simplify cyclic list handling,
- stack/queue abstract data types together with their linked-list and array implementations,
- recursion as stack-managed control flow that can be simulated explicitly,
- stack-based expression processing through postfix evaluation and infix-to-postfix conversion,
- the first sorting algorithms from bubble sort through merge sort,
- quick sort as an in-place recursive partitioning strategy,
- and the opening of the tree branch through recursive tree definitions and binary-tree representations.

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

The lecture then pushes one step further:

- once a structure has no natural index,
- traversal needs its own abstraction,
- which is why iterators appear immediately after the first linked-list operations.

---

## 6. Why Iterators Appear So Early

Deep dive: [05. Linked List Iterators](/Users/minkyu/Documents/mlsys/foundations/data-structures/05-linked-list-iterators.md)

This note explains how the course bridges from array-style use to linked-list use.

The main point is:

- a linked list should not be treated as if it had array indices,
- and a raw node reference is not yet the right user-facing abstraction,
- so traversal is packaged as an iterator with methods such as `atEnd`, `getData`, and `next`.

This is the branch's first explicit interface-level abstraction built on top of a structure.

---

## 7. Why Circular Linked Lists Matter

Deep dive: [06. Circular Linked Lists](/Users/minkyu/Documents/mlsys/foundations/data-structures/06-circular-linked-lists.md)

This note shows the next design move after ordinary singly linked lists and iterators:

- remove the terminal `null`,
- use a cyclic structure when there is no natural first/last item,
- and introduce a header node to simplify boundary handling.

This is the branch's clearest example so far of changing representation to simplify code.

---

## 8. Why Stacks and Queues Matter

Deep dive: [07. Stacks and Queues](/Users/minkyu/Documents/mlsys/foundations/data-structures/07-stacks-and-queues.md)

This note is where the branch moves from list variants to abstract data types.

It introduces:

- FIFO versus LIFO behavior,
- queues and stacks as algorithmic building blocks,
- linked-list implementations,
- array-based stacks,
- circular queues as the fix for wasted space in naive array queues,
- and stack use through parentheses matching.

This is also the first note where the difference between interface and representation becomes especially explicit.

---

## 9. Why Recursion Belongs in This Branch

Deep dive: [08. Recursion and Explicit Stack Simulation](/Users/minkyu/Documents/mlsys/foundations/data-structures/08-recursion-and-explicit-stack-simulation.md)

This note connects recursive problem solving to the stack discipline introduced just before it.

It covers:

- recursive binomial coefficients,
- Tower of Hanoi,
- the call stack,
- and explicit stack-based simulation of recursive control flow.

This is where the branch makes one of its most important hidden mechanisms visible.

---

## 10. Why Expression Processing Belongs Here

Deep dive: [09. Expression Processing with Stacks](/Users/minkyu/Documents/mlsys/foundations/data-structures/09-expression-processing-with-stacks.md)

This note shows the next major use of stacks after ADT introduction and call-stack motivation.

It covers:

- postfix notation,
- postfix evaluation,
- infix-to-postfix conversion,
- operator precedence,
- `peek`,
- and parenthesis handling through stack discipline.

This is where stacks become a concrete language-processing tool.

---

## 11. Why Sorting Starts Here

Deep dive: [10. Sorting Basics and Divide-and-Conquer](/Users/minkyu/Documents/mlsys/foundations/data-structures/10-sorting-basics-and-divide-and-conquer.md)

This note opens the sorting branch.

It covers:

- bubble sort,
- selection sort,
- insertion sort,
- stability,
- merging sorted lists,
- merge sort,
- and the setup for quick sort.

This is where the branch begins comparing full algorithm families rather than just isolated data-structure operations.

---

## 12. Why Quick Sort Gets Its Own Note

Deep dive: [11. Quick Sort and In-Place Partitioning](/Users/minkyu/Documents/mlsys/foundations/data-structures/11-quick-sort-and-in-place-partitioning.md)

This note continues the sorting branch with:

- pivot selection,
- recursive quick sort,
- and the in-place partition invariant.

It stands on its own because quick sort is not just another `O(n log n)` sorter.
It introduces a distinctly different structural idea from merge sort: recursive partitioning without an auxiliary merge buffer.

---

## 13. Why Trees Start Here

Deep dive: [12. Trees and Binary Tree Basics](/Users/minkyu/Documents/mlsys/foundations/data-structures/12-trees-and-binary-tree-basics.md)

This note opens the tree branch with:

- rooted-tree definitions,
- basic tree relations,
- subtree, path, level, and height,
- binary trees,
- complete binary trees,
- and multiple internal representations.

This is the branch's first major move from linear structures to hierarchical recursive ones.

---

## 14. Why This Subtrack Is Special

This subtrack may eventually be used differently from ordinary concept branches because data structures are often tested through hand coding and structural mutation.

But the branch should still be built from source material first.

That means:

- concepts come before templates,
- invariants come before memorization,
- and any future coding skeletons should be derived from the notes rather than invented independently.

---

## 15. Why This Branch Matters

Data structures matter because organization is not separate from efficiency.

A computer does not merely “have data.”
It stores information in a form that changes how expensive retrieval, modification, and processing will be.

That is why the subject sits so early in computer-science curricula.

---

## 16. After This Branch You Should Understand

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
- why linked lists replace contiguous storage with explicit links,
- how `first` and `last` change linked-list invariants,
- why iterators serve as the traversal abstraction once direct indexing disappears,
- how circular structure plus a header node can simplify list algorithms and edge cases,
- how stacks and queues can be implemented on top of linked lists or arrays depending on the operation pattern,
- how recursion can be understood as explicit context plus stack-managed control flow,
- how stacks support expression evaluation and notation conversion through delayed operator handling,
- how the first major sorting algorithms differ in invariants, runtime, stability, and space tradeoffs,
- and how quick sort uses in-place partitioning to define its recursive structure,
- and how rooted trees and binary trees are defined and represented internally.
