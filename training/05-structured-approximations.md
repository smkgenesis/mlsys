# 05. Structured Approximations

## What

Structured approximation methods compress a model by replacing large dense parameter objects with lower-dimensional factored representations.

Instead of:

- deleting individual weights as pruning does, or
- training a separate smaller student as distillation does,

these methods rewrite the internal parameterization itself.

The two main techniques in this section are:

- low-rank matrix factorization for large matrices
- tensor decomposition for higher-order tensors

The shared idea is:

```text
many learned parameter structures contain redundancy,
so they can be approximated by lower-rank components
with much lower storage cost
```

## Why It Matters

Large neural networks often store parameters in objects such as:

- fully connected weight matrices
- embedding tables
- convolution kernels
- attention-related tensors

These structures can be expensive in three ways:

- they consume memory
- they create bandwidth pressure
- they require large dense linear operations at inference and training time

Structured approximations matter because they target the representation itself.

They are especially useful when the deployment question is:

- can we keep most of the model’s behavior,
- while storing and moving much less parameter data?

That makes them attractive for:

- edge deployment
- mobile and embedded ML
- cloud inference cost reduction
- hardware-aware model compression

## Structured Approximation vs Pruning vs Distillation

This distinction is the most important framing for the section.

### Pruning

- starts with an existing model
- removes weights or structures
- often produces sparse or shape-reduced models

### Distillation

- starts with a teacher model
- trains a smaller student
- transfers knowledge through training signals

### Structured approximation

- keeps the model’s overall function class but rewrites large parameter objects
- replaces a large matrix or tensor with smaller factors
- compresses by exploiting low-rank structure rather than deleting or imitating

So this family is best understood as:

```text
compress the parameterization by factorizing it
```

not:

```text
remove pieces
or
train a separate student
```

## The Core Intuition

Many learned parameter objects are high-dimensional but not fully information-dense.

In practice, a large matrix or tensor often has:

- correlated rows or columns
- repeated patterns
- a smaller effective dimension than its raw shape suggests

If that is true, then a lower-rank representation can preserve much of the useful structure while using fewer parameters.

This is why structured approximation is plausible:

- the stored object is large,
- but its intrinsic degrees of freedom may be much smaller.

## Low-Rank Matrix Factorization

### What it is

Low-rank matrix factorization approximates a large matrix with the product of smaller matrices.

Given:

```text
A ∈ R^(m×n)
```

we seek:

```text
A ≈ UV
```

where:

- `U ∈ R^(m×k)`
- `V ∈ R^(k×n)`
- `k << min(m, n)`

So instead of storing one `m × n` matrix, we store two smaller factors.

### Parameter savings

The original storage cost is:

```text
mn
```

The factored storage cost is:

```text
mk + kn
```

This is only beneficial when `k` is small enough that:

```text
mk + kn < mn
```

That is the basic compression condition.

### Why this works

If the matrix is close to rank `k`, then its most important structure can be captured by a smaller latent space.

The usual mathematical picture comes from singular value decomposition:

```text
A = UΣV^T
```

If the singular values decay, then keeping only the top `k` components gives a good approximation.

That is the cleanest mathematical reason low-rank compression is possible.

### What changes in a model

A dense layer originally computes something like:

```text
y = Ax
```

After factorization, that becomes:

```text
y ≈ U(Vx)
```

So one large transform becomes two smaller transforms with an intermediate latent dimension `k`.

This reduces parameter storage, but it also changes the execution pattern:

- there are now two multiplications instead of one
- the intermediate rank controls the compression-quality tradeoff

### Where it is useful

Low-rank factorization is especially natural for:

- fully connected layers
- projection matrices
- embeddings
- recommendation-system interaction matrices

These are places where the main object of interest is already a matrix.

## Low-Rank Trade-offs

Low-rank factorization gives clear storage savings, but it is not free.

### Benefits

- fewer stored parameters
- lower memory footprint
- less parameter movement
- often lower dense linear cost when the chosen rank is small enough

### Costs

- approximation error from dropping information
- rank selection becomes a new hyperparameter problem
- inference may now involve two smaller multiplies rather than one large one
- actual speedup depends on hardware and implementation details

The crucial systems point is:

```text
fewer parameters on paper
does not automatically mean lower end-to-end latency
```

The factorized form must map well to the target runtime.

## Rank Selection

Choosing rank `k` is the central control knob.

If `k` is too small:

- compression is strong
- but information loss may damage accuracy badly

If `k` is too large:

- approximation quality is good
- but compression benefit becomes weak

So the real problem is not just “factorize the matrix.”
It is:

```text
find the smallest rank that preserves acceptable task performance
```

That is usually a validation-driven or hardware-aware tuning problem.

## Tensor Decomposition

### Why we need it

Low-rank matrix factorization only applies directly to two-dimensional objects.

But many modern model parameters are higher-order tensors, such as:

- convolution kernels
- multi-way feature interactions
- attention-related tensors
- structured embedding representations

Tensor decomposition generalizes factorization to these higher-dimensional cases.

### What it is

Instead of factorizing a matrix into smaller matrices, tensor decomposition approximates a tensor with several lower-rank components.

The goal is the same:

- reduce storage
- reduce computation
- preserve important structure

But the object now has more than two axes, so the factorization is correspondingly richer.

## Main Tensor Decomposition Families

### CP decomposition

CP decomposition expresses a tensor as a sum of rank-one components.

Conceptually:

```text
A ≈ Σ_r u_r ⊗ v_r ⊗ w_r
```

This is one of the most direct tensor analogues of low-rank approximation.

### Tucker decomposition

Tucker decomposition introduces:

- a smaller core tensor
- plus factor matrices along each mode

Conceptually, it is like a tensor-valued generalization of SVD:

- compress each axis
- keep a compact core that links them

### Tensor-Train decomposition

Tensor-Train represents a high-order tensor as a sequence of smaller tensor cores.

This is especially useful for very high-dimensional tensors because it avoids working with one enormous monolithic object.

## Why tensor decomposition is attractive

Tensor decomposition can exploit structure that matrix factorization misses.

That matters when the model’s parameter object is genuinely multi-way rather than naturally matrix-shaped.

This makes tensor decomposition valuable for:

- convolutional kernels in CNNs
- compact embedding representations
- some attention-related parameter structures
- multi-modal or other high-order interaction settings

In these cases, flattening everything to a matrix can destroy useful structure or leave compression opportunities unused.

## Tensor Decomposition Trade-offs

Tensor decomposition can produce stronger compression than simple matrix factorization, but it is usually harder to work with.

### Benefits

- higher compression potential for multi-dimensional parameters
- better alignment with the original tensor structure
- strong memory savings in convolutional and other tensor-heavy models

### Costs

- more complex factorization procedures
- more complicated rank choices
- more challenging numerical behavior
- extra tensor contractions during inference
- harder implementation and deployment

So the trade-off is usually:

```text
more expressive compression
but
more implementation and optimization complexity
```

## Computational Reality

This section is careful about a systems point that is easy to miss:

compression and runtime are not identical goals.

Both low-rank factorization and tensor decomposition reduce parameter storage, but they often replace one large dense operator with several smaller structured operators.

That means practical performance depends on:

- whether the factored operators are well supported by the runtime
- whether the target hardware can execute the new structure efficiently
- whether extra reconstruction or contraction overhead dominates the savings

So these methods are not just linear algebra tricks.
They are deployment trade-offs.

## Numerical and Optimization Challenges

The section also emphasizes that factorization introduces stability and optimization issues.

Important concerns include:

- selecting ranks well
- avoiding excessive information loss
- handling noisy or incomplete data
- regularizing the factors
- avoiding poor local optima in decomposition procedures
- maintaining numerically stable factors

This is especially important for tensor decomposition, where the optimization landscape can be harder than in the matrix case.

## LRMF vs Tensor Decomposition

The cleanest comparison is:

### Low-rank matrix factorization

- operates on matrices
- easier to implement
- easier to analyze
- often lower overhead
- natural for dense linear layers and embeddings

### Tensor decomposition

- operates on higher-order tensors
- can exploit richer structure
- often offers stronger compression opportunities
- but is more complex to optimize and deploy

So the choice is usually determined by the structure of the parameter object:

- matrix-shaped object -> start with low-rank factorization
- genuinely multi-way object -> tensor decomposition may be the better fit

## Practical Decision Rule

If you compress a model with structured approximations, the right question is not only:

- how many parameters did I remove?

It is:

- did I preserve task quality?
- did I actually reduce memory movement?
- did the new factorized operator run efficiently on the target hardware?
- did the implementation complexity justify the gain?

That is the systems version of the problem.

## How This Fits the Chapter 10 Framework

This section belongs in the same structural optimization family as pruning and distillation, but it occupies a different branch.

- pruning removes parameters or structures
- distillation transfers behavior into a smaller model
- structured approximation rewrites parameter objects into lower-rank forms

So the shared theme is still:

```text
make the model representation cheaper
```

but the mechanism is different.

## Common Mistakes

- Treating factorization as a guaranteed latency win instead of checking whether the factored operators are actually efficient on the target runtime.
- Choosing ranks only by compression ratio without checking task quality.
- Flattening higher-order tensors too casually and losing structure that tensor decomposition could exploit.
- Treating low-rank factorization and tensor decomposition as purely mathematical rewrites rather than deployment trade-offs.
- Assuming fewer stored parameters automatically implies lower end-to-end cost.

## Why This Matters for ML Systems

Structured approximations matter for ML systems because they directly target:

- parameter storage,
- memory movement,
- operator structure,
- and hardware mapping.

They are especially relevant when the deployment problem is constrained less by raw model quality and more by:

- memory bandwidth,
- parameter footprint,
- accelerator friendliness,
- and the cost of dense linear operations.

These methods also reinforce a central systems lesson:

```text
compressed representation is useful only if the new operator structure
is actually efficient on the target hardware and software stack
```

## Short Takeaway

Structured approximations compress models by replacing large matrices and tensors with lower-rank factors. Low-rank matrix factorization is the simpler matrix case, while tensor decomposition extends the same idea to multi-dimensional parameter objects. Both can reduce storage and computation substantially, but their real value depends on rank choice, approximation error, and whether the factorized operators are actually efficient on the deployment hardware.
