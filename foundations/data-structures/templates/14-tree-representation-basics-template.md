# 14. Tree Representation Basics Template

## What

This template captures the hand-coding core from the tree part of DS11:

- general-tree node representation,
- optional parent links,
- fixed-degree array-of-children representation,
- binary-tree node representation,
- and the array index formulas for complete binary trees.

It is based on [12. Trees and Binary Tree Basics](/Users/minkyu/Documents/mlsys/foundations/data-structures/12-trees-and-binary-tree-basics.md).

## Core Rules

```text
general trees need child access and sibling movement;
binary trees need left and right;
complete binary trees turn structure into index arithmetic.
```

That is the lecture's representation story in one place.

## Canonical Java Skeleton

Code:
- [14-tree-representation-basics-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/14-tree-representation-basics-template.java)

The file includes three representation families from the lecture:

- `GeneralTreeNode` with `children`, `next`, and `parent`,
- `FixedDegreeTreeNode` with `children[]`,
- `BinaryTreeNode` with `left` and `right`,
- plus helper methods for complete-binary-tree indexing.

## General-Tree Representation Rule

The lecture's flexible linked representation is:

```text
one link to the first child;
one link to the next sibling;
optionally one link back to the parent.
```

This is the easiest way to encode many-child trees without fixing the degree in advance.

## If Max Degree Is Known

The lecture also shows the alternative:

```text
store the children in an array
```

That representation trades flexibility for direct indexing.
It is only natural when the maximum degree is known ahead of time.

## Binary-Tree Representation Rule

For binary trees, the core handwritten skeleton is simpler:

- `data`
- `left`
- `right`

The lecture's point is that left and right are ordered roles, not interchangeable positions.

## Complete-Binary-Tree Index Formulas

For the array representation:

- root is at index `1`
- parent of `i` is at `floor(i / 2)`
- left child of `i` is at `2i`
- right child of `i` is at `2i + 1`

These formulas are the main thing to reconstruct under pressure.

## Pressure Checklist

1. Am I representing a general tree or a binary tree?
2. If it is a general tree, do I need sibling traversal or fixed-degree direct indexing?
3. If it is a binary tree, did I keep the ordered `left/right` structure?
4. If it is complete, am I using the array index formulas correctly?
5. Did I remember that the lecture's array representation starts at index `1`?

## Common Mistakes

- Treating a binary tree as if left and right were interchangeable.
- Using array representation for sparse non-complete trees as if it were always natural.
- Forgetting the difference between `children` and `next` in the general-tree representation.
- Dropping the `parent` link when the question assumes upward navigation.
- Using zero-based heap-style formulas when the lecture's formulas are one-based.

## Short Takeaway

For DS11, the tree hand template is mostly a representation template. General trees use child and sibling links, binary trees use left and right child pointers, and complete binary trees compress that structure into simple one-based array index formulas.
