# 04. Knowledge Distillation

## What

Knowledge distillation is a model-compression technique in which a smaller student model is trained using guidance from a larger pre-trained teacher model.

Instead of learning only from hard labels such as:

```text
cat = 1, everything else = 0
```

the student also learns from the teacher’s full output distribution over classes.

That richer supervision is the core idea.

## Why It Matters

Pruning makes an existing model smaller by removing parameters or structures.

Distillation takes a different path:

- keep the large model as a teacher,
- train a separate smaller model,
- and transfer useful behavior into the smaller architecture.

This is often attractive when you want:

- smaller memory footprint,
- lower inference latency,
- dense models that remain hardware-friendly,
- and better accuracy than a small model would achieve from scratch.

That makes distillation especially useful for:

- edge deployment,
- mobile inference,
- cloud serving cost reduction,
- and student models that must remain efficient on standard accelerators.

## Teacher and Student Intuition

The lecture’s professor-student analogy is exactly the right one.

Hard labels only say:

- which answer is correct

Teacher outputs say more:

- which wrong answers are close,
- which classes are similar,
- and how uncertain the model is.

For example, if the teacher sees an image of a dog, it might output something like:

```text
[dog: 0.85, wolf: 0.08, fox: 0.05, ...]
```

That tells the student something important:

- wolf is a more reasonable confusion than fox,
- and the class geometry is not flat.

That extra information is what makes distillation powerful.

## Distillation vs Pruning

This is one of the most important distinctions in the section.

### Pruning

- starts from an existing trained model
- removes parameters or structures
- often requires fine-tuning afterward

### Distillation

- keeps the teacher fixed
- trains a new student model
- transfers knowledge through teacher outputs

So distillation is not “cut the model down.” It is “train a smaller model better.”

## The Core Training Setup

The student is trained using two sources of supervision:

1. hard labels from the dataset
2. soft targets from the teacher

The two corresponding losses are:

- standard student loss, usually cross-entropy on ground-truth labels
- distillation loss, often based on KL divergence between teacher and student distributions

The overall training objective combines both.

This is the central systems idea:

```text
the student is optimized both to be correct
and to imitate the teacher’s softer class relationships
```

## Softmax with Temperature

The lecture introduces temperature-scaled softmax.

If `z_i` are logits, ordinary softmax uses:

```text
p_i = exp(z_i) / Σ_j exp(z_j)
```

Distillation replaces this with:

```text
p_i(T) = exp(z_i / T) / Σ_j exp(z_j / T)
```

where `T` is the temperature.

### What temperature does

- `T = 1` gives ordinary softmax
- larger `T` softens the distribution
- softened outputs reveal inter-class structure more clearly

So temperature is not just a numeric trick. It controls how much dark knowledge the teacher exposes to the student.

## Distillation Loss

The lecture gives the standard combined objective:

- cross-entropy loss on hard labels
- plus a KL-divergence term between teacher and student soft outputs

Conceptually:

```text
L = (1 - α) * hard-label loss + α * soft-target loss
```

with temperature scaling applied inside the soft-target term.

### Why the `T^2` factor appears

The lecture notes the common `T^2` scaling factor in the distillation term.

Its purpose is practical:

- when temperature is high, logits are divided by a larger number,
- which changes gradient scale,
- so the `T^2` factor helps keep the gradients in a reasonable regime.

This is one of those details that looks arbitrary until you remember the optimization mechanics.

## Why Soft Targets Help

The lecture’s main intuition here is very important.

Hard labels tell the model:

- what is correct

Soft labels also tell the model:

- which alternatives are more plausible,
- where class boundaries are softer,
- and how the teacher organizes uncertainty.

That provides two major benefits.

### 1. Better decision boundaries

The student learns which mistakes are “closer” than others.

That often improves generalization, especially between similar classes.

### 2. Regularization

Soft targets spread probability mass instead of forcing one class to probability 1 and all others to 0.

That makes training less brittle and can reduce overfitting.

This is one reason distilled models can outperform equally small models trained only on hard labels.

## Efficiency Gains

The lecture groups distillation’s practical benefits into three areas.

### Memory efficiency

The student model can be much smaller than the teacher while preserving much of its task performance.

This reduces:

- parameter storage,
- model load time,
- and memory footprint at deployment.

### Computational efficiency

Unlike unstructured pruning, a distilled student is usually still a dense model.

That means:

- it remains compatible with standard GPU, TPU, CPU, and edge inference stacks
- it reduces FLOPs by being architecturally smaller
- and it often improves latency directly without needing sparse-kernel support

### Deployment flexibility

Once distilled, the student can still be further optimized using:

- pruning,
- quantization,
- graph optimization,
- and hardware-specific inference engines

That makes distillation a strong intermediate compression stage rather than an endpoint.

## Why Distillation Is Often More Hardware-Friendly Than Pruning

This is a practical systems point the lecture gets right.

Pruning, especially unstructured pruning, may shrink a model on paper without producing proportional speedups on ordinary hardware.

Distillation usually produces:

- a smaller dense model

and dense models map cleanly onto existing inference kernels.

So in deployment terms, distillation often gives a simpler path from:

- smaller model

to:

- faster real inference

than sparse pruning does.

## Limitations

The lecture is careful not to oversell distillation.

Its main costs and risks are:

- you still need a good teacher
- student training becomes an extra training phase
- a very small student may not have enough capacity to absorb the teacher’s knowledge
- poor teacher calibration or bias can be inherited by the student

So distillation is not free. It trades more training complexity for a better deployment target.

## Distillation vs Pruning Trade-Off

The lecture’s trade-off table can be summarized cleanly like this.

### Distillation

Strengths:

- better accuracy retention
- dense hardware-friendly student models
- strong deployment compatibility

Costs:

- more training complexity
- teacher-student pipeline design

### Pruning

Strengths:

- simpler post-training application
- can directly reduce model size and, with structured pruning, compute

Costs:

- accuracy can drop sharply if pruning is too aggressive
- sparse variants may need specialized kernels for runtime gains

So the practical difference is:

```text
distillation preserves capability well but costs more to train;
pruning is easier to apply but more fragile and more hardware-dependent
```

## Why Combining Them Often Works Best

The lecture notes that real systems often combine:

- pruning,
- distillation,
- and later quantization.

This makes sense.

A reasonable compression pipeline can be:

1. reduce unnecessary structure
2. recover or transfer capability through teacher guidance
3. lower numerical precision for deployment

This layered approach aligns well with the chapter’s broader optimization framework.

## Distillation and Real Models

The lecture points to examples like:

- DistilBERT
- TinyBERT
- distilled MobileNet variants

The important takeaway is not the brand names. It is the recurring pattern:

- large accurate teacher,
- smaller dense student,
- close task performance,
- much better deployment feasibility.

That pattern shows up across NLP, vision, and edge inference.

## Practical Deployment View

A good deployment-oriented summary is:

- distillation is attractive when you want a smaller model that remains dense and accelerator-friendly
- pruning is attractive when you want to modify an existing model directly
- structured pruning is more deployable than unstructured pruning
- combining compression methods often yields the best final trade-off

This is why distillation sits naturally beside pruning in the model-representation branch of optimization.

## Common Mistakes

- Treating distillation as just “train on teacher predictions only” and ignoring the hard-label loss.
- Forgetting that codomain-like full probability structure is the point of soft labels.
- Assuming a stronger teacher automatically guarantees a strong student regardless of student capacity.
- Comparing pruning and distillation as if they solve the same optimization problem in the same way.
- Forgetting that dense distilled students are often easier to deploy than sparse pruned models.

## Why This Matters for ML Systems

Knowledge distillation is one of the best examples of a systems-aware optimization method:

- it changes the training process,
- but its goal is deployment efficiency,
- and its success depends on both statistical learning and hardware reality.

That is exactly the kind of cross-layer thinking ML systems needs.

## Short Takeaway

Knowledge distillation compresses capability rather than just parameters: a smaller student model is trained to match both ground-truth labels and the teacher’s softened output distribution, often yielding dense, hardware-friendly models that preserve much more performance than small models trained from scratch.
