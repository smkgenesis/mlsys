# training

Training-time systems topics and model optimization methods.

Belongs here:
- activation memory,
- mixed precision,
- gradient flow,
- optimizer state,
- checkpointing,
- distributed training costs,
- and model optimization methods when the main question is training/deployment efficiency tradeoff.

Does not belong here:
- pure low-level quantized representation details when the main topic is bit-level design; those belong in `quantization/`,
- inference-serving runtime behavior; that belongs in `inference/`.

Current notes:
- [01. Efficient AI](01-efficient-ai.md)
- [02. Model Optimization Framework](02-model-optimization-framework.md)
- [03. Pruning](03-pruning.md)
- [04. Knowledge Distillation](04-knowledge-distillation.md)
- [05. Structured Approximations](05-structured-approximations.md)
- [06. Neural Architecture Search](06-neural-architecture-search.md)

---

Training Efficiency and Model Optimization:
Efficiency Framing -> Optimization Dimensions -> Structural Methods -> Architecture Search

This folder describes the training-side view of efficiency in ML systems.

The goal is not only to say that models should be "smaller" or "faster."
The goal is to explain:
- what efficiency means at the system level,
- which optimization dimensions exist,
- how structural optimization methods differ,
- what each method changes in the model or training process,
- and how those choices connect to deployment constraints such as memory, latency, hardware support, and operating cost.

The documents in this folder are meant to be read as one connected arc:
- first define efficiency as a systems problem,
- then define the optimization framework,
- then examine the major structural optimization families one by one.

This folder is therefore not just a bag of compression techniques.
It is a compact course on how to reason about model efficiency from a training-and-deployment perspective.

---

## 0. Scope and Preconditions

This folder assumes that the reader already has a basic understanding of:

- neural network structure,
- training and validation loss,
- standard dense linear layers and convolutions,
- deployment metrics such as latency, throughput, memory footprint, and FLOPs,
- and the difference between training-time and inference-time concerns.

The main emphasis here is not on deriving every optimization algorithm from scratch.
The emphasis is on:

- systems framing,
- mechanism-level understanding,
- tradeoff analysis,
- and deployment relevance.

Throughout the folder, model optimization is treated as a systems problem rather than a purely mathematical or research-paper problem.

That means the repeated questions are:

- what resource is being reduced?
- what new cost is being introduced?
- what assumptions does the method make about hardware or runtime?
- and when does the theoretical win actually show up in practice?

---

## 1. Global Conceptual Map

Before diving into the individual methods, it helps to state the overall flow in one line:

```text
efficiency pressure
-> identify which resource is limiting deployment or training
-> choose the optimization dimension that matches that bottleneck
-> apply a structural or numerical method
-> recover quality if needed
-> measure whether the full system actually improved
```

Within this folder, the main structural story is:

```text
efficiency as a multi-dimensional systems problem
-> model optimization framework
-> pruning
-> distillation
-> structured approximations
-> neural architecture search
```

These methods are related, but they do not solve the same problem in the same way.

The point of this folder is to make those differences explicit.

---

## 2. Read in This Order

### 2.1 Efficiency framing first

Deep dive: [01. Efficient AI](01-efficient-ai.md)

Start here because this document explains why efficiency is a first-class ML systems concern rather than a secondary optimization topic.

It covers:
- algorithmic, compute, and data efficiency,
- scaling-law intuition,
- compute-optimal resource allocation,
- and why efficiency has to be understood as a coupled resource-allocation problem.

This note gives the broad system lens that makes the later optimization methods easier to place.

### 2.2 The optimization stack

Deep dive: [02. Model Optimization Framework](02-model-optimization-framework.md)

This is the main organizing document for the rest of the folder.

It defines the three optimization layers:
- model representation,
- numerical representation,
- hardware implementation.

It also explains how to map deployment constraints such as memory, latency, throughput, and energy to the optimization dimension that is most likely to help.

This note should be treated as the folder’s conceptual hub.

### 2.3 Pruning

Deep dive: [03. Pruning](03-pruning.md)

Pruning is the first major structural optimization method because it starts from an existing trained model and removes weights or larger structures judged to be less important.

It covers:
- unstructured, structured, and dynamic pruning,
- pruning criteria,
- iterative vs one-shot workflows,
- and the crucial difference between parameter sparsity and real hardware speedup.

This note establishes the "remove parts of the model" branch of structural optimization.

### 2.4 Knowledge distillation

Deep dive: [04. Knowledge Distillation](04-knowledge-distillation.md)

Distillation is the second major structural method, but it solves the efficiency problem differently.

Instead of deleting parts of a trained model, it trains a smaller student model using supervision from a larger teacher.

It covers:
- teacher-student intuition,
- soft targets and temperature,
- distillation loss,
- regularization and generalization effects,
- and why dense distilled students are often easier to deploy than sparse pruned models.

This note is best read after pruning because it provides the main alternative compression path.

### 2.5 Structured approximations

Deep dive: [05. Structured Approximations](05-structured-approximations.md)

This note covers the "rewrite the parameterization" branch.

Instead of removing parameters or training a student, it compresses models by factorizing large parameter objects into lower-rank forms.

It covers:
- low-rank matrix factorization,
- tensor decomposition,
- rank selection,
- storage/computation trade-offs,
- and the systems question of whether the factorized operators are actually efficient on target hardware.

This note is best read after pruning and distillation because it completes the core trio of structural compression mechanisms:
- remove,
- imitate,
- or reparameterize.

### 2.6 Neural architecture search

Deep dive: [06. Neural Architecture Search](06-neural-architecture-search.md)

NAS moves one level upstream.

Rather than compressing an architecture that already exists, it searches for a better architecture under accuracy and deployment constraints.

It covers:
- search space design,
- search strategies,
- evaluation metrics,
- hardware-aware NAS,
- and when the search cost is actually worth paying.

This note belongs last in the current sequence because it reframes the whole structural optimization question:

```text
instead of asking how to fix an existing model,
ask what architecture should have been built under the constraint in the first place
```

---

## 3. Coverage

This folder currently covers five tightly connected topics.

### 3.1 Efficiency as a systems problem

Covered in:
- [01. Efficient AI](01-efficient-ai.md)

Key ideas:
- efficiency has algorithmic, compute, and data dimensions
- scaling laws explain why more scale helps, but also why resource allocation matters
- efficiency decisions must be made under real budgets, not abstract model-quality goals

### 3.2 Optimization dimensions

Covered in:
- [02. Model Optimization Framework](02-model-optimization-framework.md)

Key ideas:
- model representation optimization changes what the model is
- numerical representation optimization changes how values are stored and computed
- hardware implementation optimization changes how the resulting workload is executed

### 3.3 Structural compression by removal

Covered in:
- [03. Pruning](03-pruning.md)

Key ideas:
- remove weights or larger structures
- trade off sparsity against hardware usability
- distinguish storage reduction from actual latency reduction

### 3.4 Structural compression by transfer

Covered in:
- [04. Knowledge Distillation](04-knowledge-distillation.md)

Key ideas:
- teacher outputs contain richer supervision than hard labels alone
- smaller dense students can inherit useful behavior from larger models
- distillation often produces cleaner deployment artifacts than sparse pruning

### 3.5 Structural compression by factorization

Covered in:
- [05. Structured Approximations](05-structured-approximations.md)

Key ideas:
- large matrices and tensors may admit lower-rank approximations
- factorization reduces storage and memory movement
- but can introduce new operator overhead and rank-selection problems

### 3.6 Architecture optimization by search

Covered in:
- [06. Neural Architecture Search](06-neural-architecture-search.md)

Key ideas:
- architecture design can itself be optimized automatically
- good NAS depends on search-space design, search strategy, and evaluation criteria
- hardware-aware NAS matters because FLOPs alone do not determine deployment efficiency

---

## 4. Main Conceptual Distinctions

Several distinctions in this folder are easy to blur together.

They should stay sharp.

### 4.1 Pruning vs distillation

- pruning removes parts of an existing model
- distillation trains a new smaller model using a teacher

### 4.2 Distillation vs structured approximations

- distillation changes the training target and student architecture
- structured approximations rewrite the parameterization of large matrices or tensors

### 4.3 Structured approximations vs NAS

- structured approximations compress an existing architecture
- NAS searches for better architectures before or alongside later compression

### 4.4 Structural optimization vs numerical optimization

- structural optimization changes model structure or parameterization
- numerical optimization changes representation precision and arithmetic form

That last distinction is especially important, because the end of the Chapter 10 flow makes clear that structural optimization alone is often not enough.

---

## 5. Why This Folder Matters for ML Systems

This folder matters because model quality alone does not determine whether a model is usable.

Real systems care about:
- memory footprint,
- parameter movement,
- latency,
- throughput,
- energy use,
- hardware compatibility,
- and cost at deployment scale.

The methods in this folder matter for ML systems because they alter:
- how much computation remains,
- how much memory must be moved,
- how dense or sparse the resulting operators are,
- how cleanly the model maps onto target hardware,
- and whether an architecture is well matched to the deployment target at all.

The folder also establishes a broader systems habit:

```text
an optimization method is useful only when it improves the real bottleneck
under the actual deployment constraints
```

That is the lens through which the rest of the optimization stack should be read.

---

## 6. After This Folder You Should Understand

After finishing this folder, you should be able to explain:

- why efficiency is a multi-dimensional systems problem rather than just "make the model smaller"
- how model representation, numerical precision, and hardware implementation form a connected optimization stack
- how pruning, distillation, structured approximations, and NAS differ in mechanism and deployment implications
- why parameter reduction does not automatically imply latency reduction
- why dense models and hardware-friendly operators often matter more than paper compression ratios
- and when architecture search is justified versus when existing efficient architectures are the better choice

You should also be able to look at a deployment problem and ask:

- is the bottleneck mainly structure, precision, hardware mapping, or something outside the model entirely?

That is the core systems habit this folder is supposed to build.
