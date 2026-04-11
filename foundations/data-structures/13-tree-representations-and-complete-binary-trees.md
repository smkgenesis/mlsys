# 13. Tree Representations and Complete Binary Trees

## What

This note continues the tree branch by focusing on the representation side of the lecture.

The lecture revisits:

- binary trees as ordered trees,
- full and complete binary trees,
- pointer-based binary-tree representation,
- array representation for complete binary trees,
- and leftmost-child / right-sibling encoding for general trees.

## Why It Matters

The previous tree note introduced vocabulary and recursive structure.
This continuation makes the implementation question sharper:

```text
once we know the shape guarantees of a tree,
we can choose a much more natural internal representation.
```

That is the main design lesson here.

## Core Idea

The lecture pushes three representation instincts:

- for binary trees, use explicit `left` and `right` links,
- for complete binary trees, exploit index arithmetic in an array,
- for general trees, reduce many children to two links through leftmost child and right sibling.

So the question is no longer just "what is a tree?"
It becomes "what structure do we know well enough to encode compactly?"

## Binary Trees Revisited

The lecture begins by restating the main binary-tree point:

- a binary tree is an ordered tree,
- and each node has at most two children.

That "ordered" part matters.
Left and right are roles, not just two interchangeable outgoing edges.

## Size Facts That Guide Representation

The lecture again highlights:

- up to `2^i` nodes at level `i`,
- up to `2^(k + 1) - 1` nodes in a binary tree of height `k`,
- and the special case of a full binary tree when that bound is achieved.

These facts matter because dense, level-by-level structure is exactly what makes array representation attractive.

## Complete Binary Trees

The lecture then uses the node numbering scheme to define completeness.

A binary tree with `n` nodes is complete if its nodes correspond to the numbers `1` through `n` without gaps under that numbering.

This is the key structural promise:

```text
no gaps in the level-order numbering
```

That promise is what makes array representation compact.

## Pointer-Based Binary-Tree Representation

The pointer-based representation is the direct one:

- `data`
- `left child`
- `right child`

This mirrors the recursive binary-tree definition exactly and works whether the tree is sparse or dense.

## Array Representation for Complete Binary Trees

The lecture then gives the classic one-based formulas:

- root at index `1`,
- parent of `i` at `floor(i / 2)`,
- left child at `2i`,
- right child at `2i + 1`.

These formulas are the implementation payoff of completeness.

Without completeness, array positions become sparse and wasteful.
With completeness, the numbering itself stores much of the structure.

## Leftmost-Child / Right-Sibling Encoding

The lecture closes by returning to general trees and giving the leftmost-child / right-sibling representation.

In this encoding, each node stores:

- its data,
- a pointer to its leftmost child,
- and a pointer to its right sibling.

This is powerful because a tree with arbitrarily many children per node can now be stored using a uniform node shape.

## Why This Encoding Is Clever

The trick is that:

- one link moves downward into the first child,
- the other link moves sideways across siblings.

So a general tree is encoded using local navigation rules rather than an arbitrarily large array of child pointers.

This makes the representation flexible while still being pointer-based and regular.

## Choosing Between the Representations

The lecture now gives us a simple selection rule:

- use `left/right` when the structure is naturally binary,
- use array indexing when the binary tree is complete,
- use leftmost-child/right-sibling when the tree is general and branching is not fixed.

That is the real design takeaway of this continuation.

## Common Mistakes

- Treating complete binary trees as if completeness were only a visual property rather than a numbering property.
- Forgetting that the array representation in the lecture is one-based.
- Using array representation for sparse trees where the index space would be mostly empty.
- Confusing `right sibling` with `right child`.
- Forgetting that leftmost-child/right-sibling is a representation of a general tree, not a claim that the original tree was binary.

## Why This Matters for CS / Systems

This note matters because later tree structures inherit these representation choices.

Search trees, heaps, parse trees, file hierarchies, and many runtime structures all depend on choosing the right internal encoding for the tree shape we expect.

So this lecture is not only about memorizing pointer layouts.
It is about learning how structural guarantees turn into implementation shortcuts.

## Short Takeaway

This continuation of the tree branch is about representation discipline. Ordinary binary trees are naturally stored with `left` and `right` pointers, complete binary trees can collapse much of their structure into one-based array index formulas, and general trees can be encoded compactly with leftmost-child/right-sibling links.
