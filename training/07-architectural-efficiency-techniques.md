# 07. Architectural Efficiency Techniques

## What

Architectural efficiency optimization improves how a model executes on real hardware.

This is different from the other two optimization layers:

- model representation changes what computation exists,
- numerical representation changes how values are encoded,
- architectural efficiency changes how the remaining work is scheduled, mapped, and adapted at runtime.

The lecture organizes this branch around four themes:

- hardware-aware design,
- sparsity exploitation,
- dynamic computation,
- and operator fusion.

## Why It Matters

A model can be smaller on paper and still fail to become faster in deployment.

That happens when:

- sparse weights are still executed through dense kernels,
- operators are launched separately even though memory traffic dominates runtime,
- all inputs pay the same full computational cost regardless of difficulty,
- or the architecture scales in a way that mismatches the target accelerator.

So architectural efficiency is the layer that turns theoretical optimization gains into realized system gains.

## Core Idea

The central question of this note is:

```text
given the work that remains,
how should execution be organized so the hardware actually runs it efficiently?
```

That means architectural efficiency is mainly about:

- scheduling,
- data movement,
- parallelism,
- workload adaptation,
- and hardware alignment.

## Architectural Efficiency in the Optimization Stack

The lecture makes a useful separation:

- pruning and distillation reduce model structure,
- quantization reduces numerical cost,
- architectural efficiency changes execution behavior.

This separation matters because execution inefficiency often survives both pruning and quantization.

Examples:

- pruning may create zeros, but without sparse kernels the zeros still consume dense compute,
- quantization may reduce arithmetic cost, but repeated kernel launches and memory traffic can still dominate latency,
- and a fixed-depth architecture may still waste computation on easy inputs.

## 1. Hardware-Aware Design

Hardware-aware design means deployment constraints shape architecture decisions from the beginning rather than being handled only after training.

The lecture emphasizes that target constraints include:

- memory bandwidth,
- processing power,
- available parallelism,
- latency limits,
- and energy budget.

So the design question becomes:

```text
what architecture pattern matches the strengths and weaknesses of the target hardware?
```

### Efficient Design Principles

The lecture frames hardware-aware design through several principles:

- scaling optimization,
- computation reduction,
- memory optimization,
- and platform-specific architecture choices.

These principles work together rather than independently.

## 2. Scaling Optimization

Scaling means choosing how depth, width, and input resolution grow.

The lecture's systems point is that scaling is not only an accuracy question.
It is also a hardware question.

- more depth increases sequential work and latency,
- more width increases parallel work and memory use,
- higher resolution increases compute and activation cost sharply.

For convolutional models, the lecture summarizes the rough relationship as:

```text
FLOPs ∝ d · w^2 · r^2
```

where:

- `d` is depth,
- `w` is width,
- `r` is resolution.

### Compound Scaling

Instead of scaling those dimensions independently, compound scaling grows them together:

```text
d = α^φ d0
w = β^φ w0
r = γ^φ r0
```

The point is not the exact formula itself.
The point is that balanced scaling often uses hardware more effectively than aggressively scaling only one dimension.

This is why EfficientNet appears in the lecture as a hardware-aware example rather than just an architecture paper.

## 3. Computation Reduction

Computation reduction restructures expensive operations into cheaper ones while preserving most of the useful representation.

The lecture's main example is depthwise separable convolution.

Instead of one full convolution, it splits the work into:

- depthwise convolution,
- then pointwise `1 × 1` mixing.

The lecture contrasts:

```text
standard conv: O(hwCinCoutk^2)
depthwise separable: O(hwCink^2) + O(hwCinCout)
```

This matters because factorization removes redundant computation and often lowers memory bandwidth demand too.

The lecture also places grouped convolutions and bottleneck layers in the same family:

- grouped convolutions reduce repeated cross-channel mixing,
- bottlenecks shrink channel count before expensive operations.

## 4. Memory Optimization

Memory optimization matters when activations, feature maps, and parameters stress hardware capacity more than arithmetic itself.

The lecture highlights three examples:

- feature reuse,
- activation checkpointing,
- parameter reduction.

### Feature Reuse

DenseNet is the lecture's main example.
The point is that reuse can reduce redundant feature-map generation and lower memory pressure.

### Activation Checkpointing

Standard backprop stores all forward activations:

```text
O(Atotal)
```

Checkpointing stores only part of them and recomputes the rest later.
The lecture summarizes the reduced storage as:

```text
O(sqrt(Atotal))
```

The important systems lesson is the memory-time tradeoff:

- less activation storage,
- more recomputation.

### Parameter Reduction

SqueezeNet appears here as the example of shrinking parameter storage by reducing channels before expensive convolutions.

Again, the point is architectural:

- smaller parameter tensors reduce memory footprint,
- lower memory movement also helps power and bandwidth behavior.

## 5. Sparsity Exploitation

Sparsity exploitation asks whether zero or near-zero values can be turned into real execution savings rather than just theoretical parameter reduction.

The lecture defines sparsity as the fraction of tensor elements that are zero, or approximately zero under a thresholded notion of near-zero values.

The systems point is more important than the formal definition:

```text
sparsity is only potential efficiency
until hardware, storage format, and execution kernel can actually skip the zeros
```

That is why sparsity belongs in architectural efficiency rather than only in pruning.

Pruning may create sparse models.
Architectural efficiency determines whether the sparsity actually becomes:

- lower arithmetic cost,
- lower memory traffic,
- lower energy use,
- or lower latency.

### Unstructured and Structured Sparsity

The lecture emphasizes a critical distinction.

#### Unstructured sparsity

Unstructured sparsity removes individual weights without enforcing a regular pattern.

Its main advantage is flexibility:

- it can be applied almost anywhere,
- and it often appears naturally after pruning.

Its main weakness is hardware mismatch:

- irregular nonzero patterns are hard for GPUs and TPUs to exploit efficiently,
- sparse indexing overhead rises,
- and memory access becomes less regular.

So unstructured sparsity often gives a large paper-level sparsity number without guaranteed runtime speedup.

#### Structured sparsity

Structured sparsity removes larger regular units:

- filters,
- channels,
- neurons,
- layers,
- or fixed sparse patterns such as blocks or N:M masks.

This is much easier for accelerators to exploit because the resulting compute pattern is more predictable.

The practical rule is:

```text
unstructured sparsity is more flexible;
structured sparsity is more hardware-friendly
```

### Sparsity Utilization Methods

The lecture groups several ways to turn sparsity into useful execution behavior.

#### Sparse matrix operations

The most direct method is to skip zero-valued entries during matrix or tensor operations.

This is the idealized promise of sparsity:

- fewer multiplies,
- fewer loads,
- and potentially lower power.

But these savings are realized only if sparse storage, indexing, and execution overhead do not erase the benefit.

#### Low-rank approximation

The lecture places low-rank approximation alongside sparsity exploitation because it also reduces effective dense workload.

The core idea is to factor a large dense matrix into smaller matrices that preserve the main information while discarding redundancy.

So even though low-rank structure is not the same thing as literal zeros, it belongs in the same practical family of:

- reducing storage,
- reducing compute,
- and reshaping the workload for more efficient execution.

#### Sparsity-aware training

The lecture also notes that training itself can be made sparse-aware:

- sparse updates,
- sparse gradient handling,
- or training procedures that preserve useful sparse structure.

This matters because some efficient sparse behaviors are easier to maintain during training than to impose only after the model is finished.

### Hardware Support

The lecture is explicit that sparsity only matters operationally if hardware and software can exploit it.

#### GPUs

GPUs can exploit sparsity well when the pattern matches accelerator assumptions.

Examples:

- structured pruning,
- block sparsity,
- and accelerator-supported patterns such as 2:4 sparsity.

The lecture points out that modern GPUs provide sparse tensor-core style acceleration for these more regular formats, but irregular sparse patterns remain much harder to accelerate.

#### TPUs

TPUs benefit when sparse computation can still be expressed in large, regular matrix-style operations.

This makes structured sparsity and sparse-aware large-matrix execution especially relevant for transformer-like workloads.

#### FPGAs

FPGAs are useful because they can be customized around specific sparse dataflows.

That flexibility makes them appealing for irregular or application-specific sparse workloads, though the engineering cost is higher than for more general-purpose accelerators.

### Structured Patterns

The lecture highlights two especially important structured sparsity formats.

#### Block sparsity

Block sparsity keeps the matrix sparse at a coarse scale while preserving dense computation within each nonzero block.

That is attractive because:

- storage remains compact,
- indexing is simpler than fully irregular sparsity,
- and many operations can still be expressed as fewer dense block operations.

#### N:M sparsity

N:M sparsity enforces a fixed number of nonzeros within each group of `M` elements.

The lecture emphasizes this pattern because it gives hardware:

- predictable memory access,
- predictable skip behavior,
- and a practical balance between flexibility and acceleration.

This is exactly the kind of regularity modern accelerators prefer.

### Challenges and Limitations

The lecture is also very clear that sparsity is not automatically a win.

Main problems include:

- irregular unstructured sparsity that hardware struggles to accelerate,
- algorithmic and implementation overhead from sparse formats and kernels,
- accuracy degradation under aggressive pruning or approximation,
- possible energy overhead from sparse metadata and irregular execution,
- and limited benefit for models or tasks that genuinely rely on dense representations.

So the systems lesson is:

```text
sparsity reduces potential work,
but exploitation overhead can cancel that gain
```

### Combined Optimizations

The lecture closes this section by stressing that sparsity is strongest when combined thoughtfully with other methods.

#### Sparsity and pruning

Pruning often creates the sparse pattern in the first place.
Whether the result is useful depends heavily on whether the sparsity is structured enough for hardware to exploit.

#### Sparsity and quantization

This combination can reduce both:

- how many values matter,
- and how many bits represent those values.

But it also magnifies hardware-software alignment problems, especially for irregular sparse patterns.

#### Sparsity and model design

Sparsity can amplify already efficient architectural ideas such as:

- factorized operations,
- low-rank designs,
- and dynamic computation.

But again, the lecture's condition remains the same:

```text
the sparse structure must line up with the target execution stack
```

## 6. Adaptive Computation Methods

The lecture then moves from static design to runtime adaptation.

Dynamic computation means the model does not spend the same amount of work on every input.

This matters when:

- simple inputs can be handled early,
- difficult inputs need more depth,
- and fixed full-depth execution wastes time and energy on the easy cases.

The lecture presents several forms.

### Early Exit Architectures

Early exit models attach intermediate classifiers to the network.

If confidence is high enough at an intermediate stage:

- the model exits,
- and the remaining layers are skipped.

BranchyNet and multi-exit transformers are the lecture's examples.

The mechanism is:

```text
predict early when confidence is sufficient;
continue deeper only when needed.
```

### Conditional Computation

Conditional computation selects which parts of the network run at all.

Instead of only deciding when to stop, the model decides:

- which layers,
- which paths,
- or which subnetworks

should activate for this input.

SkipNet and dynamic routing are the lecture's examples here.

### Gate-Based Computation

Gate-based conditional computation uses a learned controller to route execution.

The lecture's major example is the mixture-of-experts pattern, especially Switch Transformer:

- a router scores experts,
- a subset of experts is chosen,
- only those experts process the token or input.

This lets total parameter count grow without activating the whole network on every example.

### Adaptive Inference

Adaptive inference is the broader idea:

- increase or decrease compute online,
- based on confidence, uncertainty, or input complexity.

The lecture uses this to unify dynamic layer usage, variable depth, and other runtime adaptation strategies.

## 7. Dynamic Computation Challenges

The lecture is careful not to oversell adaptive computation.
It lists several practical difficulties.

### Training and Optimization Difficulty

Dynamic models introduce:

- gating mechanisms,
- confidence estimators,
- routing policies,
- and discrete decisions that are awkward to optimize with ordinary backpropagation.

That can make training slower and less stable.

### Overhead and Latency Variability

Dynamic models save computation only after paying the cost of deciding what to skip.

They also introduce variable latency:

- some inputs finish early,
- some inputs run much deeper.

That variability can be a real systems problem in strict real-time applications.

### Hardware Execution Inefficiency

Modern accelerators like GPUs and TPUs prefer regular, uniform workloads.

Dynamic branching breaks that pattern:

- some threads or processing units go idle,
- divergent execution appears,
- throughput can fall even if the model does fewer total operations.

So the theoretical compute saving is not automatically a realized speedup.

### Generalization and Robustness

The lecture also points out that dynamic routing decisions can:

- under-allocate compute to hard but rare inputs,
- overfit to training-time path patterns,
- or create new attack surfaces through the routing mechanism itself.

### Evaluation Difficulty

Traditional benchmarking assumes a fixed computational budget.
Dynamic models break that assumption.

So evaluation has to look beyond static FLOP counts and account for:

- input-dependent cost,
- latency variability,
- and hardware-specific execution behavior.

## 8. What This Note Is Really Teaching

This lecture is not only listing techniques.
It is teaching a broader systems rule:

```text
efficiency depends not just on reducing work,
but on shaping the remaining work to fit hardware and input variability.
```

That is why architectural efficiency is its own optimization dimension.

Without it:

- pruning may not accelerate,
- quantization may not deliver end-to-end gains,
- sparsity may remain only theoretical sparsity rather than realized speedup,
- and model scaling may outrun hardware limits.

## Common Mistakes

- Treating FLOP reduction as if it guaranteed latency reduction.
- Discussing pruning or quantization without asking how the resulting workload is actually executed.
- Assuming dynamic computation is always faster even when routing overhead and divergence are high.
- Treating early exit, conditional computation, and mixture-of-experts as the same mechanism.
- Ignoring memory traffic while focusing only on arithmetic count.

## Why This Matters for ML Systems

Architectural efficiency is the bridge between model-level optimization and deployment reality.

It matters because real systems care about:

- latency,
- throughput,
- power,
- memory hierarchy behavior,
- and predictable execution under hardware constraints.

This note is where optimization becomes unmistakably a systems topic rather than just a modeling topic.

## Short Takeaway

Architectural efficiency techniques improve how models execute on hardware rather than only shrinking the model or lowering numerical precision. The lecture organizes this branch around hardware-aware design, computation and memory optimization, dynamic computation, and the practical challenges of making adaptive execution behave well on real accelerators.
