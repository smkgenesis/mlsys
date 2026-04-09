# 01. Preprocessing, Worst Case, and Tradeoffs

## What

This note extends the introductory efficiency discussion in three directions:

- running time can depend on the input contents, not only on input size,
- worst-case time complexity is usually the default lens,
- and preprocessing can trade space for faster query answering.

The lecture uses range-sum queries to show that efficiency is not about one operation in isolation.
It is about the whole workload.

## Why It Matters

After learning how to count exact running times and simplify them asymptotically, the next question is:

```text
efficient for what workload?
```

Sometimes a program is called many times on the same data.
Sometimes we can spend extra work up front so later queries become much cheaper.
That is one of the most important recurring ideas in data structures.

## Core Idea

This lecture adds three habits:

1. distinguish input size from input contents,
2. focus on worst-case complexity unless there is a strong reason not to,
3. evaluate preprocessing cost and query cost together rather than separately.

That is the beginning of real data-structure reasoning.

## Worst Case Depends on Contents as Well as Size

The lecture revisits the minimum-finding example and introduces an early `break`:

```text
for (i = 0; i < n; i++) {
    isMin = true;
    for (j = 0; j < n; j++) {
        if (a[j] < a[i]) {
            isMin = false;
            break;
        }
    }
    if (isMin) min = a[i];
}
```

This version shows that running time may depend on the contents of the input, not just on `n`.

If a smaller element is found quickly, the inner loop may terminate early.
If not, the loop may run much longer.

That is why the lecture explicitly says:

- running time may depend on the contents of the input,
- and we usually focus on the worst case.

Worst-case analysis gives a stable upper bound that does not depend on getting a lucky input arrangement.

## Another Example: Range-Sum Queries

The lecture then asks for a more realistic workload.

First, an array `a[0] ... a[n-1]` is given.
Later, many queries `(x, y)` arrive, and each query asks for:

```text
a[x] + a[x+1] + ... + a[y]
```

This example is important because it shifts the question away from one isolated algorithm run.
Now we must think about repeated queries on the same data.

## Strategy 1: No Preprocessing

The simplest strategy is:

```text
preprocessing: do nothing

query(x, y):
    answer = 0
    for i from x to y:
        answer += a[i]
```

This is attractive because:

- preprocessing cost is minimal,
- extra memory cost is minimal.

But each query may scan a long subarray.
So if many queries arrive, the repeated work can become expensive.

## Strategy 2: Full Pairwise Preprocessing

The lecture then precomputes every range sum:

```text
for (i = 0; i < n; i++) {
    sum[i][i] = a[i];
    for (j = i+1; j < n; j++) {
        sum[i][j] = sum[i][j-1] + a[j];
    }
}
```

After this preprocessing:

```text
answer = sum[x][y]
```

This makes each query very cheap.
But the preprocessing cost and storage cost are much larger.

The important lesson is not just that one method is "faster."
It is that:

```text
faster queries often require more preprocessing and more memory
```

## Strategy 3: Prefix Sums as a Better Balance

The lecture then introduces a more balanced idea:

```text
sum[0] = a[0];
for (i = 1; i < n; i++) {
    sum[i] = sum[i-1] + a[i];
}
```

Now:

- if `x == 0`, answer is `sum[y]`,
- otherwise, answer is `sum[y] - sum[x-1]`.

This keeps query time low while using much less space than the full `sum[x][y]` table.

This is a very important data-structure pattern:

- precompute the right auxiliary information,
- not necessarily all possible answers.

## Time Complexity as a Function of `n` and `q`

The lecture asks a better comparison question:

```text
suppose q queries come;
what is the time complexity as a function of n and q?
```

This is the right way to compare the strategies.

### No preprocessing

- preprocessing: `Θ(1)`
- each query: up to `Θ(n)`
- total for `q` queries: `Θ(nq)` in the worst case

### Full pairwise preprocessing

- preprocessing: `Θ(n^2)`
- each query: `Θ(1)`
- total for `q` queries: `Θ(n^2 + q)`

### Prefix sums

- preprocessing: `Θ(n)`
- each query: `Θ(1)`
- total for `q` queries: `Θ(n + q)`

The point is that no single strategy is "best" in all situations.
The right strategy depends on the workload.

## Time-Space Tradeoff

The lecture also asks for space complexity.
That makes the tradeoff explicit:

- no preprocessing uses very little extra space,
- the full pairwise table uses much more space,
- prefix sums use more than the first strategy but much less than the second.

So this note introduces one of the most important recurring ideas in data structures:

```text
time can often be reduced by spending more space
```

But the real skill is choosing how much extra structure is worth building.

## What This Note Is Really Teaching

This note is still not about linked lists, heaps, or trees.
It is about the reasoning style behind them.

The core habits are:

- analyze the whole workload, not one isolated operation,
- separate preprocessing cost from query cost,
- use worst-case complexity as the default reference,
- and recognize time-space tradeoffs as design choices.

These habits are what make later data structures feel motivated rather than arbitrary.

## Common Mistakes

- Treating query time alone as the whole efficiency story.
- Ignoring preprocessing cost when the structure is built from scratch.
- Ignoring space cost when precomputing many answers.
- Assuming one strategy is always better without considering how many queries arrive.
- Forgetting that worst-case analysis is about the hardest valid input arrangement, not the average-looking one.
- Missing that prefix sums are useful because they store exactly the right partial information, not because they store everything.

## Why This Matters for CS / Systems

This lecture introduces a very general systems pattern:

```text
pay once up front to make repeated future operations cheaper
```

That pattern appears everywhere:

- indexing,
- caching,
- lookup tables,
- prefix sums,
- and precomputed metadata.

The exact structures will change, but the tradeoff pattern stays the same.

## Short Takeaway

This lecture moves beyond raw counting and into workload-aware reasoning. Running time can depend on input contents, worst-case complexity is the usual baseline, and preprocessing lets us trade extra time and space up front for faster repeated queries later.
