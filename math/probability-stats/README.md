# probability-stats

Probability and statistics for uncertainty-aware ML systems reasoning.

Belongs here:
- probability language,
- random variables and distributions,
- descriptive statistics,
- moments,
- and later statistical inference and concentration tools.

Does not belong here:
- formal discrete reasoning whose primary subject is logic, proof, or set-based structure; that belongs in [discrete-math/README.md](/Users/minkyu/Documents/mlsys/math/discrete-math/README.md),
- model-specific optimization material that belongs elsewhere in the repository.

Current notes:
- [01. Probability Language](/Users/minkyu/Documents/mlsys/math/probability-stats/01-probability-language.md)
- [02. Random Variables and Distributions](/Users/minkyu/Documents/mlsys/math/probability-stats/02-random-variables-and-distributions.md)
- [03. Distribution Summaries and Moments](/Users/minkyu/Documents/mlsys/math/probability-stats/03-distribution-summaries-and-moments.md)
- [04. Joint Random Variables](/Users/minkyu/Documents/mlsys/math/probability-stats/04-joint-random-variables.md)

---

Probability and Statistics:
Probability Language -> Random Variables and Distributions -> Distribution Summaries and Moments -> Joint Random Variables

This branch is about reasoning under uncertainty and summarizing variable behavior quantitatively.

The goal is not merely to memorize formulas.
The goal is to build a durable probabilistic vocabulary for later ML systems work:

- what randomness is being modeled,
- what quantities vary,
- how distributions are represented,
- how they are summarized,
- and how tail behavior can be bounded when exact distributions are not convenient.

The documents in this folder are deep dives.
This README is the parent document that ties them together into one continuous probability-statistics learning path.

---

## 0. Scope and Preconditions

This branch assumes:

- basic algebra,
- comfort with functions and summation,
- and willingness to reason about uncertainty as a first-class object rather than as vague intuition.

The emphasis here is on:

- precise probabilistic language,
- distributions,
- summary measures,
- and reusable quantitative tools.

The recurring questions are:

- what is random here?
- what quantity are we modeling?
- how is probability assigned?
- how do we summarize the distribution?
- and what can we still say when we do not know every detail exactly?

---

## 1. Probability Begins with Language

Deep dive: [01. Probability Language](/Users/minkyu/Documents/mlsys/math/probability-stats/01-probability-language.md)

Probability starts before formulas.

It starts with:

- experiments,
- sample spaces,
- outcomes,
- events,
- event operations,
- conditional probability,
- independence,
- total probability,
- and Bayes' theorem.

This note is first because probability becomes much easier once the objects are clear.

---

## 2. Random Variables Turn Outcomes into Quantities

Deep dive: [02. Random Variables and Distributions](/Users/minkyu/Documents/mlsys/math/probability-stats/02-random-variables-and-distributions.md)

After event language is in place, the branch shifts to random variables.

That is the key move from:

- uncertainty over outcomes

to:

- uncertainty over numerical quantities.

This note now keeps the full representation story together:

- discrete versus continuous versus mixed variables,
- PMF versus PDF,
- range space,
- and the shared role of the CDF.

That makes the whole “what kind of random quantity is this?” question much easier to see as one coherent topic.

---

## 3. Summary Measures Compress and Extend a Distribution

Deep dive: [03. Distribution Summaries and Moments](/Users/minkyu/Documents/mlsys/math/probability-stats/03-distribution-summaries-and-moments.md)

Once a distribution exists, the next question is:

```text
how do we summarize it with a few useful numbers?
```

This note combines:

- center and spread summaries,
- quantiles,
- moments,
- MGFs,
- and basic inequalities.

That combination is intentional.
These are all ways of compressing or exploiting distributional information once the random variable has already been defined.

---

## 4. Several Quantities Must Often Be Modeled Together

Deep dive: [04. Joint Random Variables](/Users/minkyu/Documents/mlsys/math/probability-stats/04-joint-random-variables.md)

Single-variable probability is not always enough.

As soon as we care about relationships between quantities, we need:

- joint support,
- joint PMFs or PDFs,
- joint CDFs,
- and marginals.

This is the step where probability moves from:

- one random quantity at a time

to:

- structured multi-variable reasoning.

It is also the beginning of a more realistic systems viewpoint, because many operational questions are really about how quantities interact rather than how each behaves alone.

This note now includes not only:

- joint supports,
- joint PMFs or PDFs,
- and marginals,

but also:

- expectations over pairs,
- covariance,
- correlation,
- and the effect of dependence on sums and differences.

---

## 5. Why This Branch Matters for ML Systems

Probability and statistics matter to ML systems because real systems are noisy, variable, and imperfectly observed.

This branch supports later reasoning about:

- empirical performance variation,
- latency distributions,
- calibration,
- uncertainty in data,
- probabilistic modeling,
- and summary statistics for model and system behavior.

Even when a system is deterministic in code, what we observe at scale is often statistical:

- tail latency,
- workload variability,
- error rates,
- and distribution shift.

That is why this branch matters far beyond classroom probability exercises.

---

## 6. Future Expansion

This branch is intentionally still early.

Natural future additions include:

- common named distributions,
- conditional distributions and expectation,
- LLN and CLT,
- estimation,
- confidence intervals,
- hypothesis testing,
- Bayesian basics,
- and concentration inequalities.

So this README should be read as a starting probability-statistics spine, not as a finished curriculum.

---

## 7. After This Branch You Should Understand

After reading this branch, you should be able to explain:

- the basic language of probability,
- how random variables are represented across discrete, continuous, and mixed cases,
- how PMFs, PDFs, and CDFs relate,
- how distributions are summarized by center, spread, and quantiles,
- how joint distributions and marginals differ,
- and how moments and basic inequalities help quantify behavior even without full exact distributional detail.

That is the current uncertainty-and-measurement branch of the mathematical layer.
