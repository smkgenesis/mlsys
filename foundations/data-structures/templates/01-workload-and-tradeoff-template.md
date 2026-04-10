# 01. Workload and Tradeoff Template

## What

This template is for DS02-style questions where one operation is not the whole story.

Use it when the problem gives:

- preprocessing code,
- query code,
- multiple possible strategies,
- or asks for time and space complexity as functions of more than one variable such as `n` and `q`.

It is based on [01. Preprocessing, Worst Case, and Tradeoffs](/Users/minkyu/Documents/mlsys/foundations/data-structures/01-preprocessing-worst-case-and-tradeoffs.md).

## Core Rule

```text
do not analyze one query in isolation;
analyze the whole workload
```

In DS02, that means:

- preprocessing cost,
- per-query cost,
- total cost for `q` queries,
- and extra space cost.

## Canonical Analysis Template

### Step 1. Define all parameters

Write down what each symbol means.

Typical DS02 variables:

- `n` = array size
- `q` = number of queries

Do not silently collapse everything into one variable if the lecture uses more than one.

### Step 2. Separate preprocessing from queries

Always split the solution into:

- preprocessing phase
- query phase

Write them separately before combining them.

### Step 3. Derive preprocessing time

Examples:

- no preprocessing: `Theta(1)`
- prefix-sum preprocessing: `Theta(n)`
- full range-sum table: `Theta(n^2)`

### Step 4. Derive query time

Examples:

- scan `a[x] ... a[y]`: up to `Theta(n)` in the worst case
- table lookup: `Theta(1)`
- prefix-sum subtraction: `Theta(1)`

### Step 5. Combine them for `q` queries

Use the structure:

```text
total time = preprocessing time + q * query time
```

Then simplify.

Typical DS02 outcomes:

- no preprocessing: `Theta(nq)`
- full table: `Theta(n^2 + q)`
- prefix sums: `Theta(n + q)`

### Step 6. Analyze extra space

Keep space as a separate answer.

Typical DS02 outcomes:

- no preprocessing: small extra space
- full table: `Theta(n^2)`
- prefix sums: `Theta(n)`

### Step 7. State the workload conclusion

Close with the real design statement:

- best for few queries,
- best for many queries,
- or best balance of time and space.

## DS02 Canonical Comparison Table

### Strategy A. No preprocessing

- preprocessing: `Theta(1)`
- query: `Theta(n)` worst case
- total for `q` queries: `Theta(nq)`
- extra space: minimal

### Strategy B. Full pairwise precomputation

- preprocessing: `Theta(n^2)`
- query: `Theta(1)`
- total for `q` queries: `Theta(n^2 + q)`
- extra space: `Theta(n^2)`

### Strategy C. Prefix sums

- preprocessing: `Theta(n)`
- query: `Theta(1)`
- total for `q` queries: `Theta(n + q)`
- extra space: `Theta(n)`

## Worst-Case Reminder Template

If the code has an early `break`, do not stop at the lucky case.

Write:

```text
running time may depend on input contents;
unless stated otherwise, use worst-case time complexity
```

That sentence is often part of the expected reasoning.

## Pressure Checklist

1. Did I define both `n` and `q`?
2. Did I separate preprocessing from query cost?
3. Did I use worst-case query time?
4. Did I compute total time for `q` queries?
5. Did I analyze extra space separately?
6. Did I finish with a tradeoff conclusion instead of only one formula?

## Common Mistakes

- Reporting only one query cost.
- Ignoring preprocessing time.
- Ignoring extra space.
- Forgetting that early `break` changes best case but not necessarily worst case.
- Choosing a strategy without mentioning the workload size.
- Treating prefix sums as if they store every possible answer.

## Short Takeaway

For DS02-style workload questions, the template is: define `n` and `q`, separate preprocessing from query cost, compute total time for `q` queries, analyze extra space separately, and end with the actual tradeoff rather than one isolated complexity class.
