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
- [02. Discrete Random Variables](/Users/minkyu/Documents/mlsys/math/probability-stats/02-discrete-random-variables.md)
- [03. Continuous and Mixed Random Variables](/Users/minkyu/Documents/mlsys/math/probability-stats/03-continuous-and-mixed-random-variables.md)
- [04. Measures of Location and Spread](/Users/minkyu/Documents/mlsys/math/probability-stats/04-measures-of-location-and-spread.md)
- [05. Moments, MGFs, and Basic Inequalities](/Users/minkyu/Documents/mlsys/math/probability-stats/05-moments-mgfs-and-inequalities.md)

---

Probability and Statistics:
Probability Language -> Random Variables -> Distributions -> Summary Measures -> Moments and Bounds

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

## 2. Random Variables Turn Outcomes into Numbers

Deep dives:
- [02. Discrete Random Variables](/Users/minkyu/Documents/mlsys/math/probability-stats/02-discrete-random-variables.md)
- [03. Continuous and Mixed Random Variables](/Users/minkyu/Documents/mlsys/math/probability-stats/03-continuous-and-mixed-random-variables.md)

After event language is in place, the branch shifts to random variables.

That is the key move from:

- uncertainty over outcomes

to:

- uncertainty over numerical quantities.

The discrete and continuous cases are separated because they change how probability is represented:

- sums versus integrals,
- point masses versus densities,
- and finite/countable ranges versus intervals.

Mixed random variables are included because real systems are often not purely one or the other.

---

## 3. Summary Measures Compress a Distribution

Deep dive: [04. Measures of Location and Spread](/Users/minkyu/Documents/mlsys/math/probability-stats/04-measures-of-location-and-spread.md)

Once a distribution exists, the next question is:

```text
how do we summarize it with a few useful numbers?
```

This is where mean, median, mode, quartiles, variance, and standard deviation enter.

These are not replacements for the full distribution.
They are compressed views that answer high-level questions about:

- center,
- spread,
- and typical scale.

---

## 4. Moments and Bounds Extend the Summary Toolkit

Deep dive: [05. Moments, MGFs, and Basic Inequalities](/Users/minkyu/Documents/mlsys/math/probability-stats/05-moments-mgfs-and-inequalities.md)

The final current note in this branch asks what we can do when:

- we want more than a mean and variance,
- but still do not want or need the full exact distribution.

Moments and MGFs help summarize and characterize distributions.
Markov and Chebyshev inequalities give general-purpose bounds.

This is one of the first places where the branch starts to move from simple description toward reusable probabilistic reasoning tools.

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
- covariance and correlation,
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
- how random variables differ across discrete and continuous cases,
- how distributions are summarized by center and spread,
- and how moments and basic inequalities help quantify behavior even without full exact distributional detail.

That is the current uncertainty-and-measurement branch of the mathematical layer.
