# 00. Exact Runtime Counting Template

## What

This template is for the first data-structures lectures, where the task is not yet to hand-code a structure but to read code and derive a running-time expression in terms of `n`.

It is based on the counting style used in [00. Introduction and Efficiency](/Users/minkyu/Documents/mlsys/foundations/data-structures/00-introduction-and-efficiency.md).

## When to Use It

Use this template when the exam gives:

- a short loop-based program,
- a simple nested-loop program,
- or two alternative implementations of the same task

and asks for:

- the exact running-time formula,
- the dominant term,
- or the asymptotic class.

## Core Rule

```text
code -> exact count T(n) -> asymptotic simplification
```

Do not jump straight to `O(n)` or `O(n^2)` unless the question explicitly skips exact counting.

## Counting Template

### Step 1. Define `n`

Write exactly what `n` means.

Examples:

- `n` = number of elements in the array
- `n` = input length
- `n` = number of records

### Step 2. Choose the primitive operations

Use the lecture's cost model if one is given.

For DS01, the lecture used:

- integer comparison costs `1`,
- integer addition costs `1`,
- assignment costs `0`.

If the exam gives a different cost model, follow that one instead.

### Step 3. Count loop tests carefully

For a loop like:

```text
for (i = 0; i < n; i++)
```

the condition `i < n` is checked:

```text
n + 1 times
```

This is one of the most common places to lose points.

### Step 4. Count loop increments carefully

For the same loop:

```text
i++
```

runs:

```text
n times
```

If the loop starts at `1`, adjust accordingly.

### Step 5. Count the core body operation

Identify the operation that dominates the body.

Examples from DS01:

- `a[j] < a[i]`
- `a[i] < min`

Then count exactly how many times that operation executes.

### Step 6. Add all counted terms into `T(n)`

Write a full formula before simplifying.

Example shape:

```text
T(n) = loop tests + loop increments + comparisons
```

### Step 7. Simplify only after the exact formula is written

Then move to:

- dominant term,
- `O(...)`,
- `Ω(...)`,
- `Θ(...)`

depending on what the question asks.

## DS01 Canonical Patterns

### Single Loop

Typical shape:

```text
init
for (...) {
    comparison/update
}
```

Checklist:

- loop test count
- loop increment count
- comparison count

### Nested Loops

Typical shape:

```text
for (...) {
    for (...) {
        comparison
    }
}
```

Checklist:

- outer test count
- outer increment count
- inner test count
- inner-body comparison count

This is how the DS01 first minimum-finding attempt becomes quadratic.

## Pressure Checklist

When solving by hand, check these in order:

1. What does `n` mean?
2. What operations am I counting?
3. Did I count loop conditions with the extra final check?
4. Did I count loop increments separately?
5. Did I count the inner comparison the right number of times?
6. Did I write `T(n)` before simplifying?
7. Did I separate exact counting from asymptotic notation?

## Common Mistakes

- Jumping directly from code to Big-O.
- Forgetting the final failed loop-condition check.
- Forgetting loop increments.
- Mixing exact counts and asymptotic classes in one expression.
- Treating every recursive or iterative call as if it had the same cost without justification.

## Short Takeaway

For DS01, the first real template is not a Java data-structure implementation but a counting workflow: define `n`, choose the cost model, count loop tests, increments, and comparisons exactly, write `T(n)`, and only then reduce it to asymptotic notation.
