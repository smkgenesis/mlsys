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
Efficiency Pressure -> Optimization Stack -> Structural Methods -> Architecture Search -> Precision Bridge

This document describes the training-side efficiency story in ML systems with deliberately system-level framing.

The goal is not merely to say that models should be:
- smaller,
- cheaper,
- or faster.

The goal is to make the major optimization choices explicit:
- why efficiency becomes a systems problem in the first place,
- what kinds of constraints force model optimization,
- which optimization dimensions exist,
- how the major structural methods differ,
- where their costs really appear,
- and why structural improvement alone is often not enough to meet deployment targets.

The documents in this folder are deep dives.
This README is the parent document that ties them together into one continuous narrative.

---

## 0. Scope and Preconditions

This folder assumes a basic understanding of:

- neural network structure,
- training and validation loss,
- dense linear layers and convolutions,
- deployment metrics such as latency, throughput, memory footprint, and FLOPs,
- and the difference between training-time and inference-time concerns.

The emphasis here is not on preserving textbook chapter boundaries.
The emphasis is on:

- mechanism,
- deployment relevance,
- trade-offs,
- and system bottlenecks.

Throughout this folder, model optimization is treated as a systems problem rather than a purely mathematical transformation.

That means the recurring questions are:

- what resource is limiting the system?
- what exactly does this method reduce?
- what new cost does it introduce?
- and when does the paper-level gain become a real deployment gain?

---

## 1. Why Training-Side Efficiency Becomes a Systems Problem

Deep dive: [01. Efficient AI](01-efficient-ai.md)

The first fact this folder tries to establish is that efficiency is not a niche optimization topic.
It is a first-class ML systems concern.

Modern ML systems are constrained not only by model quality, but by:

- training cost,
- inference memory footprint,
- energy use,
- hardware utilization,
- deployment latency,
- and operating cost at scale.

This is why the folder begins with efficient AI rather than immediately jumping into compression methods.

Efficiency has to be understood as a coupled problem across:

- algorithmic efficiency,
- compute efficiency,
- and data efficiency.

That broader framing matters because many optimization conversations are too narrow.

Examples:

- a model can be smaller but still map poorly to hardware,
- a theoretically cheaper method can still lose end-to-end if communication dominates,
- and a stronger model can still be impractical if it breaks memory or latency budgets.

The opening note in this folder therefore gives the big picture:
- scaling laws,
- compute-optimal allocation,
- context-specific efficiency priorities,
- sustainability and cost,
- and why “more capable” and “more deployable” are not the same.

Without that framing, later optimization methods are easy to misunderstand as isolated tricks.

---

## 2. The Optimization Stack

Deep dive: [02. Model Optimization Framework](02-model-optimization-framework.md)

Once efficiency is recognized as a systems problem, the next question is:

```text
which optimization dimension actually matches the bottleneck?
```

This is the job of the model optimization framework.

The central idea is that optimization happens through three connected layers:

1. model representation
2. numerical representation
3. hardware implementation

These layers answer different questions.

### 2.1 Model representation

This changes what the model is.

Typical examples:
- pruning,
- distillation,
- structured approximations,
- architecture search.

The question here is:

```text
can we change the structure or parameterization so there is less work to do?
```

### 2.2 Numerical representation

This changes how values are stored and computed.

Typical examples:
- quantization,
- reduced precision,
- mixed precision.

The question here is:

```text
can we represent the remaining work more cheaply?
```

### 2.3 Hardware implementation

This changes how the resulting workload maps onto real processors.

Typical examples:
- fusion,
- sparsity-aware execution,
- hardware-aware scheduling,
- operator implementation choices.

The question here is:

```text
does the supposedly improved model actually run efficiently on target hardware?
```

This framework matters because it prevents a common failure mode:

- applying an optimization technique before identifying which resource is actually limiting performance.

It also gives the structure for the rest of this folder.

Most of the current notes live in the **model representation** branch.

---

## 3. Structural Optimization as the Main Branch in This Folder

The current center of gravity in this folder is structural optimization.

That means the current notes mostly ask:

```text
how can we make the model representation itself cheaper?
```

There are several fundamentally different ways to answer that.

The most important distinction is that these methods do not all solve the same problem in the same way.

Some methods:
- remove parts of an existing model,
others:
- train a new smaller model,
others:
- rewrite the parameterization,
and others:
- search for a better architecture upstream.

That is why the current sequence goes:

```text
framework
-> pruning
-> distillation
-> structured approximations
-> neural architecture search
```

This is not a random list.
It is a progression through distinct structural optimization strategies.

---

## 4. Pruning: Make the Existing Model Smaller by Removal

Deep dive: [03. Pruning](03-pruning.md)

Pruning is the first major structural method because it is the most direct.

It starts with an existing model and asks:

```text
which parameters or larger structures contribute the least,
and can be removed with limited damage?
```

The key forms are:

- unstructured pruning,
- structured pruning,
- dynamic pruning.

The important systems distinction is:

- unstructured pruning can produce high sparsity,
- but structured pruning is much easier for ordinary hardware to exploit.

So pruning immediately teaches one of the major lessons of this folder:

```text
paper compression is not the same thing as deployment speedup
```

This is why the pruning note is not really about deleting weights in the abstract.
It is about:

- redundancy,
- sparsity,
- bandwidth,
- hardware support,
- and end-to-end bottlenecks.

Pruning belongs early in the sequence because it is the cleanest “remove what seems unnecessary” strategy.

---

## 5. Distillation: Make a Smaller Model Learn Better

Deep dive: [04. Knowledge Distillation](04-knowledge-distillation.md)

Distillation answers the same high-level efficiency question differently.

Instead of:
- removing pieces of an existing model,

it says:
- keep a strong teacher,
- train a smaller student,
- and transfer behavior through soft targets.

The core value of distillation is that it often produces:

- small dense models,
- with better performance than small models trained from scratch,
- and cleaner deployment characteristics than sparse pruned models.

This makes it one of the most practically important methods in the folder.

Distillation also sharpens another recurring contrast:

- pruning compresses by removal,
- distillation compresses by supervised transfer.

That difference matters because the deployment artifact is different:

- pruning often leaves the original architecture family intact, possibly with sparsity or reduced structure,
- distillation often yields a smaller dense student that maps neatly onto standard kernels.

So distillation belongs after pruning because it is the main alternative compression path.

---

## 6. Structured Approximations: Make the Parameterization Cheaper

Deep dive: [05. Structured Approximations](05-structured-approximations.md)

Structured approximations introduce a third branch.

This time the question is not:

- what can be removed?

and not:

- what can be taught to a smaller student?

Instead it is:

```text
can a large parameter object be rewritten into a lower-rank form
that preserves most of its useful structure?
```

This is the role of:

- low-rank matrix factorization,
- tensor decomposition.

These methods matter because they target:

- parameter storage,
- memory movement,
- and the structure of linear operators themselves.

They also teach another important systems lesson:

```text
fewer stored parameters does not guarantee lower latency
unless the new factorized operators are actually efficient on the target stack
```

So structured approximations are not just linear algebra.
They are deployment trade-offs about:

- rank choice,
- operator shape,
- hardware mapping,
- and the cost of extra factorized computation.

This branch belongs after pruning and distillation because it completes the core structural trio:

- remove,
- imitate,
- or reparameterize.

---

## 7. Neural Architecture Search: Move Upstream and Search the Design

Deep dive: [06. Neural Architecture Search](06-neural-architecture-search.md)

NAS is the most upstream method in the current sequence.

Instead of taking an architecture as given, it asks:

```text
what architecture should we have built under the deployment constraint
in the first place?
```

This changes the problem significantly.

Pruning, distillation, and factorization mostly begin from an architecture that already exists.
NAS instead turns architecture choice itself into an optimization problem involving:

- search space,
- search strategy,
- evaluation criteria,
- and often hardware-aware constraints.

This is why NAS belongs last in the current structural sequence.
It is not “one more compression trick.”
It is a reframing of the design problem itself.

NAS is especially important for ML systems because it makes deployment goals first-class:

- latency,
- memory,
- energy,
- hardware fit.

The key lesson is that the best architecture is not necessarily:
- the one with highest accuracy,

but:
- the one that best satisfies the actual deployment objective.

---

## 8. The Main Distinctions This Folder Tries to Keep Sharp

Several distinctions are easy to blur together if the notes are read too casually.

They should stay explicit.

### 8.1 Pruning vs distillation

- pruning removes parts of an existing model
- distillation trains a new smaller model with teacher guidance

### 8.2 Distillation vs structured approximations

- distillation changes the training target and student
- structured approximations rewrite parameter objects into lower-rank forms

### 8.3 Structured approximations vs NAS

- structured approximations compress an existing architecture
- NAS searches for better architectures upstream

### 8.4 Structural optimization vs numerical optimization

- structural optimization changes what the model contains or how it is parameterized
- numerical optimization changes how the remaining values are represented and executed

That last distinction is especially important, because structural improvement alone often does not solve the whole deployment problem.

---

## 9. Why Structural Optimization Alone Is Often Not Enough

One of the most important lessons in the overall Chapter 10 flow is that structural optimization is only one dimension of the full efficiency stack.

A model can become:

- smaller,
- less redundant,
- or architecturally better matched to a task,

and still miss deployment targets because:

- weights are still high precision,
- memory bandwidth is still dominant,
- hardware support is still poor,
- or software overhead still dominates.

This is why the structural methods in this folder should be read as:

- necessary,
- often powerful,
- but not sufficient by themselves.

In other words:

```text
structure changes what work remains
but precision and implementation decide how expensive that work still is
```

This is the bridge from the current folder contents to later optimization topics such as quantization and hardware-aware execution.

---

## 10. File Sequence and Future Expansion

The current numbered files are:

- [01. Efficient AI](01-efficient-ai.md)
- [02. Model Optimization Framework](02-model-optimization-framework.md)
- [03. Pruning](03-pruning.md)
- [04. Knowledge Distillation](04-knowledge-distillation.md)
- [05. Structured Approximations](05-structured-approximations.md)
- [06. Neural Architecture Search](06-neural-architecture-search.md)

The numbering is meant to be readable and expandable.

This folder is not considered closed.
New documents may be added later between or beneath these topics.

That means the numbering scheme should be interpreted as a growing study structure, not as a frozen final table of contents.

Future additions may include:

- new top-level notes,
- inserted intermediate notes,
- or subtopic deep dives under existing branches.

The structure should therefore preserve:

- conceptual order,
- local readability,
- and room for expansion.

---

## 11. Why This Folder Matters for ML Systems

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
- and whether the architecture is well matched to the actual deployment target.

More broadly, this folder is trying to build one durable systems habit:

```text
an optimization method is useful only when it improves the real bottleneck
under the actual deployment constraints
```

That is the lens through which all the structural methods here should be read.

---

## 12. After This Folder You Should Understand

After finishing this folder, you should be able to explain:

- why efficiency is a multi-dimensional systems problem rather than just “make the model smaller”
- how model representation, numerical precision, and hardware implementation form a connected optimization stack
- how pruning, distillation, structured approximations, and NAS differ in mechanism and deployment implications
- why parameter reduction does not automatically imply latency reduction
- why dense models and hardware-friendly operators often matter more than paper compression ratios
- and why architecture search should be judged by deployment objectives rather than novelty alone

You should also be able to look at a deployment problem and ask:

- is the bottleneck mainly structure?
- precision?
- hardware mapping?
- or something outside the model entirely?

That is the core systems habit this folder is supposed to build.
