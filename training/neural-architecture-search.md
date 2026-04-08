# Neural Architecture Search

## What

Neural Architecture Search (NAS) is a model optimization method that automates architecture design instead of relying entirely on manual human choices.

Rather than asking:

- which weights should be pruned,
- how should knowledge be distilled,
- or how should an existing matrix be factorized,

NAS asks:

```text
which architecture should we build in the first place
if we care about both accuracy and efficiency?
```

So NAS is a structural optimization method, but it operates at the architecture-design level rather than only compressing an already fixed model.

## Why It Matters

Manual architecture design works well, but it has limits:

- architecture spaces are huge,
- efficiency trade-offs are hardware-dependent,
- and many good designs are unintuitive.

NAS matters because it turns architecture design into an optimization problem.

The goal is not just:

- high accuracy

but:

- high accuracy under memory, compute, latency, and deployment constraints

That makes NAS especially relevant when:

- hardware constraints are unusual,
- deployment scale is very large,
- or efficiency targets are tight enough that architecture details matter a lot.

## How NAS Differs from Earlier Methods

This section is easiest to understand in contrast with earlier Chapter 10 methods.

### Pruning

- starts with a trained model
- removes parameters or structures

### Distillation

- starts with a large teacher
- trains a smaller student

### Structured approximation

- starts with existing parameter objects
- rewrites them into lower-rank forms

### NAS

- searches for the architecture itself
- treats architectural design choices as optimization variables

So NAS is upstream of those other methods.
It answers:

```text
what structure should the model have?
```

before or alongside later compression and deployment optimization.

## The Three-Part NAS Framework

The chapter organizes NAS around three components.

### 1. Search space

What architectures are allowed?

This defines:

- available layer types
- block types
- network depth and width ranges
- activation functions
- kernel sizes
- skip-connection choices
- hardware-aware options

### 2. Search strategy

How does the system explore the search space?

Common approaches include:

- reinforcement learning
- evolutionary algorithms
- gradient-based search

### 3. Evaluation

How is a candidate architecture judged?

Typical metrics include:

- validation accuracy
- FLOPs
- memory footprint
- latency
- energy or power
- hardware suitability

These three pieces together are the real NAS template.

## Search Space Design

Search space design is one of the most important parts of NAS.

If the search space is too narrow:

- the search cannot discover anything meaningfully new

If it is too broad:

- evaluation becomes too expensive
- and the search becomes computationally impractical

So good NAS is not “search everything.”
It is:

```text
search a tractable family of architectures
that still contains promising designs
```

This is why search-space design is partly an optimization question and partly an encoding of domain knowledge.

## What Usually Lives in the Search Space

A practical NAS search space often includes choices over:

- convolution types
- depthwise separable convolutions
- attention blocks
- residual blocks
- layer counts
- channel widths
- activation functions such as ReLU, Swish, or GELU
- kernel sizes and receptive fields
- skip connections

Some NAS systems also include:

- hardware-aware cost models
- operator sets tuned for a target accelerator
- latency constraints as first-class search objectives

That is what turns NAS from pure architecture search into deployment-aware architecture search.

## Cell-Based Search Spaces

A major practical idea in NAS is to search for reusable cells rather than entire end-to-end networks.

Instead of exploring every layer of a full architecture independently, NAS can search for:

- a small computational block,
- then replicate or stack it in a larger network.

Why this helps:

- search becomes more tractable
- discovered patterns can scale across model sizes
- the architecture family becomes easier to reuse

This is one reason cell-based NAS became influential in efficient architecture design.

## Search Strategies

Once the search space is defined, NAS needs a way to navigate it.

The chapter focuses on three broad strategies.

### Reinforcement learning

Reinforcement-learning NAS treats architecture generation as a sequential decision problem.

A controller proposes architectural choices and receives a reward based on model quality.

#### Strength

- flexible
- can explore discrete architecture choices directly

#### Weakness

- very expensive
- often requires training many candidate models

This was historically important, but also one of the reasons early NAS seemed impractically costly.

### Evolutionary algorithms

Evolutionary NAS keeps a population of candidate architectures and improves them through:

- mutation
- crossover
- selection

High-performing architectures survive and generate new candidates.

#### Strength

- naturally parallel
- often balances exploration and exploitation well

#### Weakness

- still computationally expensive
- requires population management and many evaluations

This approach is often easier to parallelize than reinforcement-learning NAS.

### Gradient-based NAS

Gradient-based NAS relaxes architecture choices into differentiable parameters and optimizes them together with model weights.

This is the key idea behind methods like DARTS.

#### Strength

- dramatically cheaper than RL- or evolution-based search
- much more practical under limited compute budgets

#### Weakness

- the continuous relaxation may distort the true discrete architecture problem
- can converge to poor local minima
- may miss patterns that discrete search would discover

So gradient-based NAS trades search fidelity for tractability.

## The NAS Optimization Problem

The chapter presents NAS as a bi-level optimization problem.

At a high level:

- outer loop: choose architecture `α`
- inner loop: train weights `w` for that architecture

Conceptually:

```text
find the architecture whose trained version performs best
while satisfying deployment constraints
```

This is the core difficulty of NAS.

Evaluating one architecture is already expensive because it usually requires training it, at least approximately.

That is why exhaustive search is impossible in realistic spaces.

All practical NAS methods are really attempts to make this expensive evaluation loop more efficient.

## Evaluation Criteria

A strong NAS system does not optimize only for accuracy.

It evaluates candidate architectures using deployment-relevant metrics such as:

- validation performance
- FLOPs
- memory consumption
- inference latency
- energy usage

This is one of the most ML-systems-relevant points in the section.

Architecture quality is not just:

- how accurate is the model?

It is:

- how accurate is it under actual resource constraints?

## Hardware-Aware NAS

The chapter emphasizes that FLOPs are not the same as real-world efficiency.

That matters because deployment systems care about:

- actual latency on a target device
- memory bandwidth limits
- cache behavior
- energy cost
- operator support on the target runtime

Hardware-aware NAS therefore incorporates real or predicted device behavior directly into the objective.

This is often the difference between:

- a theoretically efficient architecture

and:

- an architecture that is actually fast on a phone, GPU, TPU, or edge accelerator

That is why hardware-aware NAS is especially important in ML systems work.

## NAS in Practice

A practical NAS system often does some version of the following:

1. define a constrained architecture family
2. choose a search method
3. evaluate candidate architectures using accuracy plus deployment metrics
4. retain architectures that fall on a good accuracy-efficiency frontier

This is usually a multi-objective optimization problem rather than a single-metric one.

In practice, many good NAS systems are really searching for Pareto-efficient designs:

- better accuracy for the same cost
- or lower cost for the same accuracy

## When NAS Is Worthwhile

The section is careful not to oversell NAS.

NAS is most justified when:

- the deployment target has unusual constraints
- even small efficiency gains have large deployment value
- one search can amortize across many variants or products
- existing architectures are poorly matched to the target hardware

NAS is often not worth it when:

- standard architectures are already well-optimized
- compute budget is limited
- requirements are changing too quickly
- the search cost exceeds the likely deployment benefit

For many practitioners, using existing NAS-discovered architectures is better than running NAS from scratch.

That is a very sensible systems conclusion.

## Architecture Examples

The chapter points to several important NAS-generated families.

### EfficientNet

- searches for effective building blocks
- then uses compound scaling across depth, width, and resolution
- produces strong accuracy-efficiency trade-offs

### MobileNetV3

- hardware-aware search for mobile deployment
- integrates efficient residual structures, squeeze-and-excitation, and tuned activations

### FBNet

- explicitly latency-aware
- optimized for mobile inference rather than just theoretical FLOPs

### NAS-BERT and related transformer search

- extends NAS ideas beyond CNNs
- searches for efficient transformer-style structures under compute and memory constraints

These examples show that NAS is not just a theoretical idea; it has produced practical model families that people really use.

## The Real Systems Lesson

The most important systems lesson in this section is that architecture search should be tied to deployment objectives.

The best architecture is not simply:

- the one with highest accuracy

It is:

- the one that best satisfies the real deployment objective

That objective may be:

- latency under a mobile budget
- throughput in a cloud serving environment
- memory fit on an edge device
- or power efficiency on specialized hardware

This is why NAS belongs in an ML-systems discussion rather than only an AutoML discussion.

## Relationship to Later Optimization

NAS is not a replacement for pruning, distillation, quantization, or hardware-specific execution tuning.

Instead, it often provides a stronger starting architecture on top of which those later optimizations can be applied.

So the optimization stack can look like:

- discover a better architecture with NAS
- then prune, distill, or quantize it
- then map it onto hardware-aware runtimes

That layered view is more realistic than treating NAS as the final step.

## The Transition to Numerical Precision

The end of the section makes an important point:

- structural optimization changes what computations exist
- numerical precision optimization changes how those computations are represented and executed

So even after a model has been structurally improved:

- latency can still miss target
- because numerical representation remains too expensive

This is a strong bridge from:

- architecture and representation optimization

to:

- quantization and lower-precision execution

That transition is one of the most useful takeaways from the whole Chapter 10 flow.

## Short Takeaway

Neural Architecture Search automates architecture design by searching over model structures under accuracy and deployment constraints. Its real value is not just finding accurate networks, but finding architectures that are better matched to latency, memory, energy, and hardware requirements than manual design often can be. In practice, the usefulness of NAS depends on search-space design, evaluation cost, and whether the deployment gains justify the substantial search expense.
