# quantization

Quantization and numerical precision optimization for ML systems.

Belongs here:
- numerical precision reduction,
- floating-point and integer format tradeoffs,
- quantization error and calibration,
- precision-dependent memory, bandwidth, and energy behavior,
- and the systems consequences of representing weights and activations with fewer bits.

Does not belong here:
- general model optimization framing when the main question is structural efficiency; that belongs in [training/README.md](/Users/minkyu/Documents/mlsys/training/README.md),
- low-level CUDA or Triton kernel implementation detail when the main question is kernel construction rather than numerical representation.

Current notes:
- [01. Quantization and Precision Optimization](/Users/minkyu/Documents/mlsys/quantization/01-quantization-and-precision-optimization.md)

---

Quantization and Precision Optimization:
Structural Efficiency -> Numerical Efficiency -> Hardware Realization

This folder explains the second major branch of model optimization: changing not what the model computes, but how its numbers are represented while it computes.

The goal is not merely to say that lower precision makes models smaller.
The goal is to make the whole systems story explicit:

- why numerical precision is a first-class optimization variable,
- why model size, memory bandwidth, energy use, and throughput all depend on precision,
- how FP16, BF16, TF32, FP8, INT8, and more aggressive formats differ,
- why precision reduction can help inference dramatically but complicate training,
- and why hardware support and software orchestration determine whether chip-level gains become real end-to-end gains.

The documents in this folder are deep dives.
This README is the parent document that ties them together into one continuous quantization learning path.

---

## 0. Scope and Preconditions

This folder assumes a basic understanding of:

- neural network weights and activations,
- floating-point versus integer arithmetic,
- memory bandwidth and storage cost,
- and the difference between training-time and inference-time bottlenecks.

The emphasis here is not on treating quantization as an isolated compression trick.
The emphasis is on:

- numerical representation,
- hardware support,
- memory movement,
- calibration and error control,
- and the gap between theoretical speedups and real deployment gains.

Throughout this folder, quantization is treated as a systems optimization problem.

That means the recurring questions are:

- what resource does lower precision actually reduce?
- where does quantization error enter?
- which precision format matches the workload?
- when is the main problem compute throughput, bandwidth, energy, or storage?
- and when does a lower-bit representation really improve end-to-end behavior rather than just benchmark throughput?

---

## 1. Why Quantization Is Its Own Optimization Branch

Deep dive: [01. Quantization and Precision Optimization](/Users/minkyu/Documents/mlsys/quantization/01-quantization-and-precision-optimization.md)

The parent optimization framework in [training/README.md](/Users/minkyu/Documents/mlsys/training/README.md) separates three questions:

1. what the model computes,
2. how those computations are numerically represented,
3. and how the resulting workload maps onto hardware.

Quantization lives in the second branch.

That distinction matters because a model may already be structurally optimized and still remain too expensive in deployment. A smaller model can still:

- move too many bytes,
- consume too much DRAM bandwidth,
- draw too much power,
- or fail to match the efficient arithmetic modes of the target accelerator.

Quantization exists because the cost of a model depends not only on parameter count, but also on how many bits represent each parameter and activation throughout execution.

---

## 2. Precision Is a Memory, Compute, and Energy Story

Reducing precision usually helps in three coupled ways:

- smaller representations reduce storage,
- smaller values reduce memory traffic,
- and lower-precision arithmetic can execute at higher throughput on supported hardware.

That makes quantization especially attractive in:

- mobile and edge inference,
- bandwidth-limited server inference,
- large models whose memory movement dominates runtime,
- and accelerator platforms with dedicated low-precision units.

But the key systems point is that memory often matters as much as raw arithmetic throughput. Reducing FP32 to INT8 is not only about faster multiply-adds. It is also about moving one-quarter as many bytes for the same tensor payload.

---

## 3. Numeric Formats Are Not Interchangeable

Not all reduced-precision formats solve the same problem.

Some preserve dynamic range better:

- BF16 keeps the FP32 exponent width and is therefore much friendlier to training than FP16.

Some optimize hardware throughput for a specific accelerator generation:

- TF32 and FP8 are examples of accelerator-aware formats that try to preserve enough numerical behavior while improving effective throughput.

Some are especially strong for inference:

- INT8 is the workhorse practical format because it gives large storage and bandwidth reduction with manageable accuracy loss on many models.

And some are far more aggressive:

- INT4, binary, and ternary representations pursue extreme efficiency but often need stronger accuracy-recovery techniques or specialized architectures.

So “lower precision” is not one knob. It is a family of representational choices with different error behaviors and hardware implications.

---

## 4. Quantization Only Works When Range and Error Are Managed

Precision reduction is never free.

Lower-bit representations introduce:

- rounding error,
- clipping error,
- saturation,
- and distribution mismatch between original floating-point values and quantized values.

That is why quantization immediately becomes a problem of:

- scaling,
- clipping range selection,
- symmetric versus asymmetric mapping,
- and granularity such as layerwise versus channelwise quantization.

In other words, quantization is not just “cast the tensor to INT8.”
It is a controlled numerical approximation problem.

---

## 5. Training and Inference Need Different Precision Stories

Inference often tolerates aggressive quantization better than training.

Why:

- training needs stable gradient flow,
- training sees wider dynamic ranges,
- and training may fail long before end-task accuracy is measured if underflow, overflow, or poorly managed low-precision accumulation destabilizes optimization.

That is why practical training often settles on:

- BF16 mixed precision,
- FP16 with loss scaling,
- or selective low-precision execution.

By contrast, inference often benefits more directly from:

- INT8 post-training quantization,
- selective activation quantization,
- or mixed low-precision paths designed around deployment hardware.

So one of the most important distinctions in this folder is:

```text
training precision strategy != inference precision strategy
```

---

## 6. Hardware Support Determines Whether Precision Gains Materialize

Chip-level specification sheets can be misleading if read too literally.

A platform may advertise large TOPS gains for INT8 or FP8, but end-to-end benefit depends on many additional realities:

- precision conversion overhead,
- kernel implementation quality,
- whether the memory system was the real bottleneck,
- whether unsupported layers remain in higher precision,
- and whether software orchestration adds its own cost.

This is why quantization has to be read jointly with:

- hardware support,
- compiler/runtime support,
- and operator implementation quality.

The representation choice and the execution stack have to fit each other.

---

## 7. The Current Deep Dive

The first note in this folder is intentionally broad:

- [01. Quantization and Precision Optimization](/Users/minkyu/Documents/mlsys/quantization/01-quantization-and-precision-optimization.md)

It covers the core concepts needed before this folder splits into more specialized notes later:

- precision and energy,
- numeric encoding and storage,
- format comparison,
- precision-reduction tradeoffs,
- post-training quantization,
- quantization-aware training,
- weight versus activation quantization,
- static versus dynamic quantization,
- granularity choices such as channelwise quantization,
- and the role of quantization inside broader multi-technique optimization pipelines.

This is the right shape for now because the subject is broad and tightly connected. It is better to establish one strong canonical note first than to fragment the folder too early.

---

## 8. File Sequence and Future Expansion

The current quantization sequence is intentionally minimal:

1. one parent document,
2. one large foundational deep dive.

That is not a sign that the folder is finished.
It is an open starting point.

Natural future expansions include:

- PTQ in more detail,
- calibration methods,
- symmetric versus asymmetric quantization,
- granularity choices,
- QAT,
- weight versus activation quantization,
- static versus dynamic quantization,
- mixed precision for training,
- FP8 and emerging formats,
- extreme quantization,
- and quantized kernel/runtime implications.

So this folder should grow by splitting only when a subtopic becomes independently reusable enough to justify its own deep dive.

---

## 9. After This Folder You Should Understand

After reading the current quantization sequence, you should be able to explain:

- why precision optimization is distinct from structural optimization,
- why lower precision changes storage, bandwidth, throughput, and energy simultaneously,
- why FP16, BF16, TF32, FP8, and INT8 serve different purposes,
- why channelwise quantization is often the practical deployment default,
- why weight and activation quantization should be reasoned about separately,
- how static and dynamic quantization trade runtime efficiency for adaptability,
- when PTQ is enough and when QAT is worth the added training cost,
- why extreme quantization is qualitatively harder than ordinary INT8 deployment,
- and why quantization is often strongest when combined with pruning, distillation, or architecture-level optimization.
- why quantization is fundamentally a controlled approximation problem,
- why calibration and range selection matter,
- why training and inference usually need different precision strategies,
- and why hardware support is necessary but not sufficient for real quantization gains.

That is enough to move from “quantization makes models smaller” to “quantization is a full systems optimization layer with its own numerical, hardware, and deployment constraints.”
