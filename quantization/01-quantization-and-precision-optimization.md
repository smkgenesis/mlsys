# 01. Quantization and Precision Optimization

## What

Quantization is a model optimization technique that reduces the numerical precision of weights and activations, usually moving from higher-precision floating-point formats such as FP32 to lower-precision formats such as FP16, BF16, INT8, or even more aggressive representations.

More broadly, precision optimization asks:

```text
how should the model's numbers be represented during computation?
```

That question affects:

- model size,
- memory bandwidth usage,
- compute throughput,
- power consumption,
- and sometimes training stability or final accuracy.

So this document is not only about integer quantization. It covers the larger numerical-representation story around quantization and reduced precision.

## Why It Matters

Structural optimization reduces how much model structure exists.
Precision optimization reduces how expensive each remaining number is to store, move, and compute.

That distinction matters because many real systems remain slow or expensive even after structural optimization.

A model can be:

- smaller in parameter count,
- but still bottlenecked by memory movement,
- still forced to use expensive FP32 arithmetic,
- or still poorly matched to the efficient arithmetic modes of the target hardware.

Modern accelerators make this even more important because they often provide dramatically higher throughput for lower-precision arithmetic than for FP32.

But raw hardware throughput is not the whole story.
The systems reality is more complex:

- conversions between precisions can add overhead,
- some layers are more numerically sensitive than others,
- training may destabilize under aggressive precision reduction,
- and end-to-end gains depend on whether the rest of the execution stack can exploit the lower precision effectively.

So quantization matters because it sits directly at the intersection of:

- numerical analysis,
- hardware support,
- memory systems,
- and deployment efficiency.

## Core Idea

The central idea of quantization is simple:

```text
represent the same useful signal with fewer bits
```

But doing that well requires answering several harder questions:

1. which precision format should be used?
2. which values should be represented in that format?
3. how should floating-point values be mapped into lower-precision ranges?
4. how much error can the model tolerate?
5. does the target hardware actually run that format efficiently?

That is why quantization is best understood not as one trick, but as a controlled trade between:

- efficiency gains,
- numerical error,
- and hardware/software support.

## Precision and Energy

Reduced precision improves efficiency not just because arithmetic becomes cheaper, but because data movement often becomes cheaper too.

This matters because moving data through the memory hierarchy is frequently more expensive than the arithmetic performed on that data.

Lower-precision representations help by:

- reducing tensor storage,
- reducing memory bandwidth demand,
- reducing memory-energy cost,
- and enabling more data to fit into faster on-chip buffers and caches.

That is why precision reduction is especially valuable in:

- mobile and embedded systems,
- bandwidth-limited inference,
- and large-model deployment where memory traffic dominates runtime.

The systems lesson is:

```text
quantization is often a memory-and-energy optimization as much as a compute optimization
```

## Numeric Encoding and Storage

Numerical precision is not just a bit-width question.
It is also an encoding question.

Floating-point formats use:

- a sign bit,
- exponent bits,
- and mantissa bits.

Integer formats remove the floating-point exponent/mantissa structure and instead represent values through scaled discrete integer levels.

This encoding choice changes what the format is good at.

For example:

- FP32 provides a strong general-purpose baseline with wide range and good precision.
- FP16 reduces storage and compute cost, but its smaller exponent narrows dynamic range.
- BF16 keeps the FP32 exponent width, preserving dynamic range while sacrificing mantissa precision.
- INT8 removes floating-point structure entirely and relies on external scale information to map back to real values.

So precision formats are not just smaller versions of FP32. They are different numerical tools with different stability and hardware characteristics.

## Numerical Format Comparison

The most important practical formats to distinguish are these:

### FP32

- strong general-purpose baseline
- stable for training and inference
- highest storage and bandwidth cost among the commonly used practical formats in this discussion

### FP16

- 2x smaller than FP32
- often much faster on FP16-optimized hardware
- but its reduced exponent range makes it more vulnerable to underflow and overflow during training

### BF16

- also 16-bit
- preserves FP32's exponent size
- better dynamic range than FP16
- often a much better training format for deep networks

### TF32

- accelerator-oriented compromise format
- keeps FP32-like range behavior while using reduced mantissa precision internally for faster matrix operations on supported NVIDIA hardware

### FP8

- more aggressive floating-point reduction
- increasingly relevant on modern AI accelerators
- promising for both training and inference in carefully engineered stacks

### INT8

- the most practical and common quantized inference format
- 4x smaller than FP32
- often gives major throughput and bandwidth improvements on supported hardware

### INT4 and more aggressive formats

- even smaller and potentially much more efficient
- but harder to deploy without noticeable accuracy loss or stronger software/hardware specialization

### Binary and ternary

- extreme efficiency
- usually research-heavy or architecture-sensitive
- much harder to use without substantial accuracy or modeling tradeoffs

The broad practical summary is:

- BF16 and mixed precision are especially important for training
- INT8 is especially important for inference
- FP8 and lower-bit integer formats are where newer hardware and research push more aggressively

## Precision Reduction Trade-offs

Reduced precision brings clear benefits, but it also introduces numerical error.

The main error sources include:

- rounding,
- clipping,
- saturation,
- and mismatch between the original floating-point distribution and the lower-precision representable set.

Not all models and not all layers tolerate these errors equally well.

Common patterns include:

- large CNNs and transformer models often tolerate moderate precision reduction surprisingly well,
- some layers such as normalization-related computations or attention-sensitive paths may be more fragile,
- and smaller or more numerically delicate models can degrade more quickly.

This leads to a practical rule:

```text
precision tolerance is workload-dependent, not universal
```

That is why mixed-precision approaches often work well: they preserve higher precision where needed and use lower precision where the model is more tolerant.

## Precision Reduction Strategies

There are several broad ways to use reduced precision.

### Post-training quantization

Post-training quantization applies lower precision after the model has already been trained.

Its appeal is obvious:

- no retraining,
- relatively low cost,
- and quick deployment.

But because the model does not adapt to the quantized representation during training, PTQ can suffer more accuracy loss on numerically sensitive workloads.

### Quantization-aware training

Quantization-aware training brings quantization effects into training itself so the model can adapt.

This usually gives better accuracy retention than naive PTQ, but it costs more engineering effort and training compute.

### Mixed precision

Mixed precision uses different precisions for different parts of the computation.

This is especially important for training, where:

- some operations can safely run at reduced precision,
- while others need a wider range or more stable accumulation behavior.

This is one of the most practical strategies because it balances efficiency and stability without forcing the entire system into one aggressive low-precision format.

## Post-Training Quantization and Uniform Quantization

A standard PTQ idea is uniform quantization, where floating-point values are mapped into evenly spaced integer levels.

The common conceptual form is:

```text
q = round(x / s)
```

where:

- `x` is the original floating-point value,
- `s` is the scale,
- and `q` is the quantized integer value.

The scale determines how floating-point range is compressed into the available integer range.

This immediately shows why quantization is not just type casting.
You have to define the mapping between:

- the floating-point world
- and the integer world

That mapping is where much of the practical quality of quantization is decided.

## Calibration

Calibration is the process of selecting the effective clipping or quantization range used during PTQ.

This matters because if the chosen range is poor:

- useful resolution can be wasted,
- outliers can dominate scale choice,
- or large parts of the true distribution can be clipped.

Common calibration approaches include:

- max-based range selection,
- entropy/KL-based range selection,
- percentile-based range selection.

These methods make different tradeoffs between:

- robustness to outliers,
- preservation of the original distribution,
- and simplicity.

So calibration is one of the most important practical parts of PTQ.

## Symmetric, Asymmetric, and Granularity Choices

Quantization also depends on how the quantization parameters are shared.

### Symmetric vs asymmetric

Symmetric quantization centers the range around zero.

This is simpler and often natural for zero-centered weight distributions.

Asymmetric quantization allows the quantized range to shift away from zero.

This is useful when the value distribution is skewed and a symmetric range would waste representational capacity.

### Layerwise vs channelwise vs finer granularity

One scale per whole layer is simple but can lose accuracy when value ranges vary widely across channels or filters.

Channelwise quantization often improves accuracy because it gives each channel its own scale.

Groupwise and even finer-grained schemes push this idea further, but with greater implementation complexity.

So granularity is another major tradeoff:

- coarser quantization is simpler,
- finer quantization often preserves accuracy better.

Among these options, channelwise quantization is the current practical standard for convolutional filters.
It usually gives most of the accuracy benefit of finer granularity without introducing the full complexity of much more fragmented scaling schemes.

So in practice the broad pattern is:

- layerwise is simple but often leaves accuracy on the table,
- channelwise is the common deployment balance,
- and finer granularity is reserved for cases where the extra complexity is justified.

## Weights Versus Activations

Quantization is also different depending on which tensors are being quantized.

### Weight quantization

Weight quantization reduces the precision of stored model parameters.

This helps by:

- shrinking model size,
- reducing bandwidth needed to fetch parameters,
- and enabling lower-precision arithmetic during inference.

Weight quantization is usually the easiest place to start because weights are fixed after training and their distributions can be analyzed ahead of time.

### Activation quantization

Activation quantization reduces the precision of intermediate layer outputs during execution.

This can further reduce:

- memory traffic,
- temporary buffer size,
- and arithmetic cost on low-precision hardware.

But it is usually harder than weight quantization because activations depend on the current input and can vary much more at runtime.

That makes range selection and error control more difficult.

So a useful practical distinction is:

- weight quantization mainly compresses stored parameters,
- activation quantization compresses live computation.

Both matter, but activation quantization usually makes runtime behavior more delicate.

## Static Versus Dynamic Quantization

After choosing granularity and tensor type, another important question is:

```text
when are the quantization ranges determined?
```

### Static quantization

Static quantization fixes the quantization ranges ahead of inference, usually through calibration.

Its main advantage is efficiency:

- no additional range-computation overhead at runtime,
- predictable execution behavior,
- and strong compatibility with deployment-oriented inference stacks.

Its weakness is that the fixed range may not fit every input equally well.

### Dynamic quantization

Dynamic quantization computes activation ranges during runtime.

Its main advantage is adaptability:

- the range is chosen based on the current activation values,
- which can improve accuracy on varying inputs.

Its weakness is extra runtime cost:

- range computation happens during inference,
- which can reduce or even offset part of the expected speedup.

So the tradeoff is direct:

- static quantization favors runtime efficiency,
- dynamic quantization favors input-adaptive accuracy.

## PTQ Advantages and Limitations

Post-training quantization is attractive because it is the fastest path from a trained model to a smaller and cheaper inference artifact.

Its main advantages are:

- no retraining,
- low engineering overhead,
- immediate memory savings,
- and strong usefulness for deployment under time or compute constraints.

This is why PTQ is often the default first choice for:

- mobile deployment,
- edge inference,
- rapid productization,
- and any case where retraining is expensive or unavailable.

But PTQ also has clear limitations:

- quantization error is introduced after the model has already finished learning,
- some architectures are more sensitive than others,
- and hardware support is not uniformly strong across all platforms.

A common practical pattern is:

- CNNs often tolerate PTQ reasonably well,
- transformer-like models can be more sensitive,
- and careful calibration plus channelwise scaling often become necessary to preserve quality.

So PTQ is best understood as:

- the easiest deployment-oriented quantization method,
- but not the most accuracy-preserving one.

## Quantization-Aware Training

Quantization-aware training integrates quantization into the training loop rather than applying it only after training.

The core idea is:

- simulate low-precision behavior in the forward pass,
- keep gradient updates workable during backpropagation,
- and let the model adapt its parameters to the quantized regime.

Conceptually, a common simulated quantization form is:

```text
q = round(x / s) * s
```

where the forward pass mimics quantization while training still updates parameters in a differentiable optimization process.

Because rounding is not differentiable, QAT typically relies on gradient approximations such as the Straight-Through Estimator (STE).

### Why QAT helps

QAT usually preserves accuracy better than PTQ because the model learns under the same numerical constraints it will later face at inference time.

This is especially important for:

- quantization-sensitive models,
- NLP and speech systems,
- and deployment targets where low-precision inference is mandatory but accuracy loss must stay small.

### Why QAT costs more

QAT is not free.
It adds:

- more training complexity,
- more tuning decisions,
- and more training time.

So the practical comparison is:

- PTQ is faster and easier,
- QAT is slower but more accuracy-preserving.

In many real workflows, a hybrid path is sensible:

- start with PTQ,
- then use QAT if PTQ alone loses too much accuracy.

## PTQ Versus QAT

The decision between PTQ and QAT is usually not philosophical.
It is operational.

Use PTQ when:

- deployment speed matters most,
- retraining is difficult,
- and moderate accuracy loss is acceptable.

Use QAT when:

- low-precision inference is required,
- the model is numerically sensitive,
- and accuracy preservation matters enough to justify extra training cost.

This is one of the most practical quantization decisions in real systems:

```text
fast deployment versus accuracy recovery
```

## Extreme Quantization

Beyond INT8 and INT4, extreme quantization pushes precision down to:

- binary representations,
- or ternary representations.

These methods can drastically reduce:

- storage,
- arithmetic cost,
- and power usage.

But the tradeoff is severe.
Extreme quantization greatly restricts representational flexibility and often requires:

- specialized training methods,
- gradient approximations such as STE,
- and sometimes specialized hardware to realize the intended efficiency.

So these methods are important, but they are not just "smaller INT8."
They belong to a much more constrained part of the design space.

## Multi-Technique Optimization

Quantization is powerful by itself, but it becomes much more effective when combined with other optimization branches.

The most important combinations are:

- pruning plus quantization,
- distillation plus quantization,
- and architecture search or architecture-aware design plus quantization.

These combinations work because each technique attacks a different part of the efficiency problem:

- pruning reduces parameter count,
- quantization reduces bits per parameter and activation,
- distillation recovers or preserves behavior in a smaller student,
- and architecture design can make the model more compatible with low-precision execution in the first place.

This is why quantization should not be viewed as the final optimization step in isolation.
It is often one part of a larger pipeline.

The practical systems lesson is:

```text
structural efficiency, numerical efficiency, and execution efficiency compound
```

Quantization handles the numerical-efficiency piece.
It is strongest when the rest of the optimization pipeline is aligned with it.

## Hardware Support and Real System Performance

One of the most important practical lessons is that hardware support is decisive.

Lower precision helps only when:

- the target hardware has efficient support for that format,
- the kernels and runtime can exploit it,
- and the rest of the pipeline does not erase the gain through conversion overhead or unsupported operators.

This is why chip-level benchmark claims are not enough.

A hardware vendor may advertise very large low-precision throughput improvements, but end-to-end benefit depends on:

- the actual model,
- operator coverage,
- memory bottlenecks,
- software stack maturity,
- and any extra orchestration needed for mixed precision or quantization.

So quantization always has to be evaluated as a whole-system optimization, not just as a hardware feature checkbox.

## Common Mistakes

- Treating weight quantization and activation quantization as if they posed the same runtime and accuracy challenges.
- Assuming layerwise quantization is the natural default when channelwise quantization is often the practical standard.
- Assuming dynamic quantization is always better because it is more adaptive, without accounting for its runtime overhead.
- Assuming PTQ and QAT differ only in implementation effort rather than in how the model experiences quantization error.
- Treating extreme quantization as just a more aggressive version of INT8 rather than a qualitatively different regime.
- Evaluating quantization in isolation instead of asking how it interacts with pruning, distillation, or architecture design.

## Why This Matters for ML Systems

This stage of quantization analysis is where the folder becomes fully systems-oriented.

The real deployment question is not simply:

- can the model be quantized?

It is:

- which tensors should be quantized,
- at what granularity,
- when ranges should be chosen,
- whether PTQ is sufficient or QAT is required,
- and how quantization fits with the rest of the optimization pipeline.

That is the level where quantization decisions start affecting end-to-end model viability rather than just benchmark numbers.

## Short Takeaway

Quantization becomes much more practical once its internal choices are made explicit: channelwise quantization is often the deployment default, weights and activations pose different challenges, static and dynamic quantization trade runtime cost for adaptability, PTQ and QAT trade deployment speed for accuracy retention, and aggressive precision reduction works best when combined with broader structural and execution optimizations.

## Common Mistakes

- Treating quantization as only a model-size reduction technique.
- Assuming lower precision automatically yields proportional end-to-end speedup.
- Thinking FP16 and BF16 are interchangeable just because they are both 16-bit.
- Ignoring memory bandwidth and energy while focusing only on arithmetic throughput.
- Assuming all layers tolerate the same degree of quantization.
- Treating PTQ as a simple cast instead of a scaling, range-selection, and error-management problem.
- Ignoring calibration quality.
- Assuming hardware support on paper guarantees production gains in practice.

## Why This Matters for ML Systems

Quantization matters to ML systems because it connects directly to real bottlenecks:

- model storage,
- HBM and DRAM bandwidth,
- cache and on-chip buffer pressure,
- accelerator arithmetic modes,
- serving latency,
- power consumption,
- and deployment viability on constrained hardware.

It is one of the few optimization families that can simultaneously affect:

- memory footprint,
- throughput,
- energy use,
- and cost per inference.

It also forms the natural bridge between:

- model-level optimization decisions in [training/README.md](/Users/minkyu/Documents/mlsys/training/README.md),
- and hardware/runtime execution decisions in [cuda/README.md](/Users/minkyu/Documents/mlsys/cuda/README.md) and [triton/README.md](/Users/minkyu/Documents/mlsys/triton/README.md).

That makes quantization one of the most important points where numerical methods, systems engineering, and deployment economics all meet.

## Short Takeaway

Quantization is the branch of optimization that reduces the bit-cost of the model's numbers rather than changing the model's structure; its value comes from lowering storage, bandwidth, energy, and compute cost, but it only works well when format choice, calibration, granularity, and hardware support are all matched carefully to the workload.
