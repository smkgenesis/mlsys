# 12. Trees and Binary Tree Basics

## What

This note opens the tree branch of the course.

The lecture introduces:

- the recursive definition of a rooted tree,
- basic tree vocabulary,
- subtree, degree, path, level, and height,
- internal representations of general trees,
- binary trees,
- complete binary trees,
- and pointer-based and array-based representations of binary trees.

## Why It Matters

Trees are the first major structures in the branch whose organization is fundamentally hierarchical rather than linear.

Arrays, linked lists, stacks, and queues all organize data along one main sequence.
Trees change that completely.

They matter because they let us represent:

- hierarchy,
- recursive decomposition,
- branching search spaces,
- and relationships that are not naturally linear.

## Core Idea

The lecture defines a rooted tree recursively:

- there is a specially designated root,
- and the remaining nodes are partitioned into disjoint subtrees of that root.

That recursive definition is the central point:

```text
a tree is built from smaller trees
```

So trees are not just pictures with branches.
They are recursive objects.

## Basic Tree Vocabulary

The lecture introduces the standard structural language:

- root
- parent
- child
- sibling
- ancestor
- descendant
- leaf
- internal node

This vocabulary matters because later algorithms and later data structures will all be described in these terms.

## Subtree, Degree, Path, Level, Height

The lecture then adds the first quantitative concepts:

- the subtree rooted at a node,
- the degree of a node as its number of children,
- a path between nodes,
- level as distance from the root,
- and height as the maximum level in the tree.

These are the tools for reasoning about tree shape.

## Why the Unique Path Property Matters

The lecture states that there is a unique simple path between any pair of nodes.

That fact is central.

It is one of the most important ways trees differ from more general graphs, and it is one reason tree algorithms are often simpler and cleaner.

## Internal Representation of General Trees

The lecture then turns from abstract definition to implementation choices.

One representation uses explicit nodes together with:

- child references,
- sibling-style connections,
- and optionally parent references.

The lecture also notes that if the maximum degree is known, children can be stored in an array.

That is the first important tree-representation tradeoff:

- linked flexibility for unknown branching,
- direct child indexing when the branching bound is known.

## Binary Trees

The lecture then specializes to binary trees.

A binary tree is presented as:

- an ordered tree,
- in which every node has at most two children.

The ordering matters.
Left and right are not interchangeable.

That is why the lecture can refer to:

- left child,
- right subtree,
- and a node with only a right child.

## Size Bounds in Binary Trees

The lecture gives two important counting facts:

- a binary tree can have at most `2^i` nodes at level `i`,
- and a binary tree of height `k` can have at most `2^(k + 1) - 1` nodes.

It then defines a full binary tree of height `k` as one that achieves that maximum.

These formulas are the first strong link between tree shape and asymptotic size reasoning in the tree branch.

## Numbering and Complete Binary Trees

The lecture next introduces a node numbering scheme for binary trees.

Using that scheme, it defines a complete binary tree as one whose nodes correspond to the numbers `1` through `n` without gaps.

This prepares the way for compact array representations.

## Internal Representation of Binary Trees

The lecture shows two main binary-tree representations.

### Pointer-based representation

Each node stores:

- data,
- left child,
- right child.

This mirrors the recursive definition directly.

### Array representation

For complete binary trees, the lecture gives the classic index formulas:

- root at index `1`,
- parent of node `i` at `floor(i / 2)`,
- left child at `2i`,
- right child at `2i + 1`.

This is one of the most important representation tricks in the whole branch.

## Leftmost-Child / Right-Sibling Representation

The lecture also presents the leftmost-child / right-sibling representation for general trees.

This is a powerful idea because it lets a node with arbitrarily many children be encoded using only:

- one leftmost-child link,
- and one right-sibling link.

That makes many-child trees manageable with a uniform node structure.

## What This Note Is Really Teaching

This lecture is not only about terminology.
It is teaching two deep ideas together:

```text
trees are recursive structures,
and tree representations depend strongly on what shape guarantees we have
```

That is why the lecture moves quickly from definition to implementation.

## Common Mistakes

- Treating trees as just visual diagrams instead of recursively defined structures.
- Forgetting that a binary tree is an ordered tree, not just a tree with degree at most two.
- Confusing height with number of nodes.
- Ignoring the unique-path property.
- Using array representation as if it were always efficient, even for sparse or non-complete trees.
- Forgetting the difference between general-tree and binary-tree representations.

## Why This Matters for CS / Systems

This note matters because trees are everywhere:

- file systems,
- expression structure,
- search structures,
- compiler representations,
- hierarchical indexes,
- and scheduling or dependency hierarchies.

It also marks the point where the branch leaves purely linear structures and starts treating recursive shape as a first-class organizing principle.

## Short Takeaway

This lecture opens the tree branch by defining rooted trees recursively and introducing the vocabulary needed to reason about them: parent, child, subtree, path, level, and height. It then shows that representation depends on structure: general trees can use child/sibling-style links, binary trees can use left/right pointers, and complete binary trees admit especially compact array representations through simple index formulas.
