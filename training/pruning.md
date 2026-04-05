# Pruning

## What

Pruning is a model optimization technique that removes parameters or larger computational structures from a neural network while trying to preserve task performance.

The core goal is simple:

- reduce model size,
- reduce memory movement,
- reduce computational cost,
- and improve deployment feasibility,

without causing unacceptable accuracy loss.

Pruning is one of the main techniques inside the **model representation** branch of model optimization.

## Why It Matters

Modern neural networks are often heavily overparameterized.

That overparameterization is useful during training because it:

- creates many optimization paths,
- improves flexibility,
- and often supports better convergence and generalization.

But at deployment time, the same overparameterization creates inefficiencies:

- larger memory footprint,
- more bandwidth pressure,
- more compute,
- higher latency,
- worse scalability on constrained devices.

Pruning matters because it exploits the gap between:

- what is useful for training,
- and what is truly necessary for deployment.

## Core Mechanism

Pruning asks:

> which parameters or structures contribute the least, and can be removed with limited damage?

At a high level, the workflow is:

1. score weights or structures by some importance criterion,
2. remove the least important ones,
3. fine-tune or retrain if needed,
4. measure whether the resulting model still meets deployment requirements.

This is a model-representation optimization because it changes **what the model contains**, not just the precision of numbers or the implementation of kernels.

## The Simplest Intuition: Magnitude-Based Pruning

The most intuitive pruning rule is:

- weights with very small absolute magnitude are often less important,
- so remove or zero out the smallest ones first.

This is a heuristic, not a universal truth, but it is simple and often effective enough to be widely used as a baseline.

Conceptually:

```python
mask = abs(weights) >= threshold
pruned_weights = weights * mask
```

This creates sparsity by keeping only weights whose magnitude exceeds the threshold.

## Mathematical View

Pruning can be framed as:

- keep the loss small,
- while limiting how many nonzero parameters remain.

The ideal optimization would minimize the loss of the pruned model subject to a parameter budget:

- low prediction error,
- few nonzero weights.

This is why the `L0` norm shows up in pruning discussions:

- `||W||0` counts how many parameters remain nonzero.

That directly matches the practical notion of sparsity.

The problem is that exact `L0` optimization is hard, so practical pruning methods rely on approximations and heuristics such as:

- magnitude-based selection,
- `L1`-style sparsity encouragement,
- iterative prune-and-fine-tune workflows,
- gradient-based importance estimates,
- and sensitivity-based approximations.

## What Can Be Pruned?

Pruning is not just about removing individual scalar weights.

Different pruning methods target different structures:

- individual weights,
- neurons,
- channels or filters,
- entire layers,
- or dynamically chosen active structures at runtime.

The structure being removed matters because it strongly affects:

- implementation complexity,
- hardware efficiency,
- and the likelihood of preserving accuracy.

## Unstructured Pruning

Unstructured pruning removes individual weights while leaving the high-level architecture mostly unchanged.

Conceptually:

- the layer shape stays the same,
- but many entries inside the weight matrices become zero.

### Benefits

- strong compression potential,
- high flexibility in which parameters are removed,
- often very good for reducing stored parameter count.

### Limitation

Unstructured pruning does **not** automatically produce fast inference on common accelerators.

Why?

- standard GPUs and TPUs are heavily optimized for dense matrix operations,
- sparse matrices often need specialized sparse kernels to realize speedups,
- otherwise the model may be smaller without being proportionally faster.

That means unstructured pruning is often best understood as:

- very good for storage and sparsity,
- but not guaranteed to improve runtime unless the deployment stack can exploit sparse execution well.

## Structured Pruning

Structured pruning removes larger dense units such as:

- neurons,
- channels,
- filters,
- or entire layers.

Instead of creating arbitrary sparsity inside a tensor, it changes the tensor shapes or topology of the model itself.

### Benefits

- more hardware-friendly than unstructured sparsity,
- direct reduction in FLOPs and dense workload size,
- more likely to improve inference speed on standard accelerators.

### Limitation

- higher risk of damaging model capacity,
- more architectural reconfiguration,
- and often stronger need for fine-tuning.

This is the main systems tradeoff:

> unstructured pruning can produce more parameter-level sparsity, but structured pruning is often much easier for real hardware to exploit.

## Dynamic Pruning

Dynamic pruning makes pruning decisions depend on:

- the input,
- runtime conditions,
- or training dynamics.

Examples include:

- activation-conditioned pruning at inference time,
- gradual pruning during training,
- pruning with regrowth or adaptive reactivation.

### Why it is attractive

Not every input requires the same amount of computation.

Dynamic pruning tries to use less compute for simpler cases while keeping the option to use more model capacity when needed.

### Cost

- more implementation complexity,
- extra decision overhead,
- and more difficult production integration.

Dynamic pruning is powerful, but it is not the easiest pruning style to operationalize.

## Pruning Criteria

Different pruning methods choose what to remove using different signals.

### Magnitude-based

Remove units whose weights have small norms or small absolute values.

Why it is popular:

- simple,
- cheap,
- easy to implement,
- and often good enough as a first baseline.

### Activation-based

Remove units that contribute little activation over a representative dataset.

Why it is useful:

- reflects actual model behavior on data,
- rather than only static weight values.

Cost:

- requires profiling over data,
- and adds analysis overhead.

### Gradient-based

Use gradient information to estimate which parameters or structures matter less for loss reduction.

Why it is useful:

- ties pruning decisions more directly to optimization dynamics.

Cost:

- more complex,
- and more naturally integrated into training-time workflows than simple post-training pruning.

## Iterative vs One-Shot Pruning

There are also different workflows for **when** pruning happens.

### One-shot pruning

- prune many parameters or structures at once,
- then fine-tune afterward.

Pros:

- simple,
- fast to apply,
- lower workflow complexity.

Cons:

- bigger immediate shock to the model,
- higher risk of sudden accuracy loss.

### Iterative pruning

- prune a little,
- fine-tune,
- prune again,
- repeat.

Pros:

- accuracy degradation is often easier to control,
- the model gets a chance to adapt gradually,
- often better final quality for the same sparsity target.

Cons:

- slower,
- more engineering time,
- more retraining effort.

This is the classic tradeoff between:

- simplicity and speed of optimization workflow,
- versus tighter quality preservation.

## The Real Systems Question

The most important systems question is not:

> did the number of parameters go down?

It is:

> did pruning improve the bottleneck that actually matters in deployment?

Examples:

- if model storage is the problem, even unstructured pruning may help a lot,
- if dense inference latency on GPU is the problem, structured pruning is often more relevant,
- if memory bandwidth dominates, reducing parameter movement may matter more than reducing nominal FLOPs,
- if the true bottleneck is outside the model, pruning may have limited practical effect.

This is why pruning has to be evaluated in the context of:

- target hardware,
- software stack,
- execution kernels,
- and the actual end-to-end system bottleneck.

## Tradeoffs

- More aggressive pruning usually risks more accuracy loss.
- Unstructured pruning improves sparsity but may not improve wall-clock speed on dense hardware.
- Structured pruning is more deployment-friendly but can damage model capacity more noticeably.
- Dynamic pruning can improve flexibility but increases implementation and runtime complexity.
- Fine-tuning often recovers quality, but adds optimization time and engineering cost.

## Common Mistakes

- Assuming fewer parameters automatically means faster inference.
- Treating pruning as universally beneficial without checking hardware support.
- Ignoring bandwidth and kernel implementation details when evaluating sparse models.
- Pruning too aggressively without measuring quality degradation.
- Judging pruning only by compression ratio instead of end-to-end system impact.

## Why This Matters for ML Systems

Pruning is one of the clearest examples of why model optimization is a systems discipline.

It combines:

- model-level redundancy,
- hardware compatibility,
- memory and bandwidth constraints,
- retraining costs,
- and deployment-specific objectives.

Understanding pruning well helps answer:

- when sparsity is truly useful,
- when structured reduction is better than sparse reduction,
- how model compression interacts with accelerator behavior,
- and why model optimization must always be evaluated as part of the full system rather than as an isolated algorithmic trick.
