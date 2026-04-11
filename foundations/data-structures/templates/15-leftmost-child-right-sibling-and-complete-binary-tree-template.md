# 15. Leftmost-Child Right-Sibling and Complete Binary Tree Template

## What

This template captures the hand-coding core from DS12:

- pointer-based binary-tree representation,
- complete-binary-tree array formulas,
- and leftmost-child / right-sibling encoding for general trees.

It extends [13. Tree Representations and Complete Binary Trees](/Users/minkyu/Documents/mlsys/foundations/data-structures/13-tree-representations-and-complete-binary-trees.md).

## Core Rules

```text
binary tree: left and right;
complete binary tree: index arithmetic;
general tree: leftmost child and right sibling.
```

That is the shortest lecture-faithful reconstruction rule.

## Canonical Java Skeleton

Code:
- [15-leftmost-child-right-sibling-and-complete-binary-tree-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/15-leftmost-child-right-sibling-and-complete-binary-tree-template.java)

The file includes:

- `LeftmostChildRightSiblingNode`
- `BinaryTreeNode`
- `parentIndex`
- `leftChildIndex`
- `rightChildIndex`

## Binary-Tree Pointer Template

For the binary-tree representation, the lecture's minimal node is:

- `data`
- `left`
- `right`

This is the basic pointer skeleton to recover by hand.

## Complete-Binary-Tree Index Template

The lecture's one-based formulas are:

- root at `1`
- parent of `i` at `i / 2`
- left child of `i` at `2 * i`
- right child of `i` at `2 * i + 1`

These formulas are the real exam payload for the array representation.

## Leftmost-Child / Right-Sibling Template

For a general tree, the lecture's compact node shape is:

- `data`
- `leftmostChild`
- `rightSibling`

This turns a many-child tree into a two-link node structure.

## Navigation Rule

The easiest way to remember the encoding is:

```text
go down with leftmostChild;
move across siblings with rightSibling.
```

That single line usually reconstructs the whole idea.

## Pressure Checklist

1. Am I encoding a binary tree or a general tree?
2. If it is complete, did I switch to one-based array formulas?
3. Did I keep `rightSibling` separate from `right child` in my head?
4. If the question asks for a general tree with arbitrary degree, am I using leftmost-child/right-sibling rather than forcing a binary-tree interpretation?
5. If I use an array, am I assuming completeness explicitly?

## Common Mistakes

- Confusing `rightSibling` with the `right` child of a binary tree.
- Forgetting the one-based indexing assumption of the lecture formulas.
- Applying complete-binary-tree formulas to a sparse non-complete tree.
- Thinking leftmost-child/right-sibling changes the abstract tree rather than only its encoding.

## Short Takeaway

For DS12, the main hand-coding template is about choosing the right encoding. Binary trees use `left/right`, complete binary trees use one-based index formulas, and general trees with arbitrary branching can be stored compactly through `leftmostChild/rightSibling`.
