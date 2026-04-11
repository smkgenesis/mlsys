# 00. Architectural Efficiency Design Template

## What

This template captures the smallest reusable decision pattern from the architectural-efficiency lecture.

It is not a code template.
It is a systems-design template for deciding:

- which architectural efficiency technique matches the bottleneck,
- what hardware assumption the technique relies on,
- and what hidden implementation costs might erase the theoretical gain.

It is based on [07. Architectural Efficiency Techniques](/Users/minkyu/Documents/mlsys/training/07-architectural-efficiency-techniques.md).

## Core Rule

```text
do not ask only "how can we reduce computation?"
ask "how will the reduced or adaptive computation actually execute on this hardware?"
```

That is the lecture's main filter.

## The Four-Way Decision Template

When reading a model-optimization problem, sort it into one of these first:

### 1. Scaling optimization

Use this when the main question is:

- how much depth,
- how much width,
- how much resolution

the target hardware can support without breaking latency, memory, or power limits.

Main lecture signal:

```text
depth, width, and resolution must be balanced together
```

### 2. Computation reduction

Use this when the expensive operator itself is the problem.

Main lecture signal:

- replace standard convolution with a factorized form,
- reduce redundant channel mixing,
- or compress expensive blocks into cheaper ones.

Typical examples:

- depthwise separable convolutions,
- grouped convolutions,
- bottleneck layers.

### 3. Memory optimization

Use this when activations or parameters are the real bottleneck.

Main lecture signal:

- memory capacity,
- bandwidth pressure,
- feature-map storage,
- checkpointing,
- or parameter footprint.

Typical examples:

- feature reuse,
- activation checkpointing,
- parameter reduction.

### 4. Dynamic computation

Use this when different inputs clearly deserve different amounts of work.

Main lecture signal:

- easy inputs should finish earlier,
- hard inputs should receive more compute,
- or only part of the model should activate.

Typical examples:

- early exit,
- conditional computation,
- gate-based routing,
- adaptive inference.

## Hardware Fit Checklist

Before claiming a win, check these five questions:

1. Does the target hardware prefer uniform dense execution?
2. Will the new method create branching, routing, or irregular memory access?
3. Is the bottleneck compute, memory traffic, or latency variance?
4. Does the technique reduce useful work, or only theoretical FLOPs?
5. Will the runtime system actually exploit the new structure?

If those questions are skipped, the optimization claim is probably too optimistic.

## Dynamic Computation Warning Template

If the method is adaptive, always check:

- routing overhead,
- latency variability,
- hardware divergence,
- training instability,
- and evaluation mismatch.

This is the lecture's main caution.

Dynamic computation can save work,
but it can also damage throughput predictability and accelerator utilization.

## Pressure Checklist

1. What is the real bottleneck: compute, memory, latency, power, or variance?
2. Which of the four branches does this problem belong to?
3. What hardware assumption makes the method attractive?
4. What overhead might erase the gain?
5. Is this a static architecture decision or a runtime adaptation decision?

## Common Mistakes

- Saying "fewer FLOPs" as if that alone proves lower latency.
- Treating all adaptive methods as if they had the same overhead pattern.
- Ignoring routing cost in mixture-of-experts style methods.
- Ignoring memory hierarchy when discussing architectural efficiency.
- Failing to separate model representation gains from execution-structure gains.

## Short Takeaway

The reusable template from this lecture is simple: identify whether the problem is scaling, computation reduction, memory optimization, or dynamic computation, then ask whether the target hardware can actually execute that choice efficiently after routing, synchronization, memory, and scheduling costs are included.
