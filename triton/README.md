# triton

Triton notes for ML systems engineers who want a clean path from first syntax to first real kernels.

Belongs here:
- the Triton programming model,
- program-instance ownership,
- launch grids and compile-time kernel parameters,
- pointer arithmetic, masks, tiling, and block-level reasoning,
- and small Triton kernels that make memory movement and layout decisions concrete.

Does not belong here:
- CUDA-first execution-model material that belongs in [cuda/README.md](/Users/minkyu/Documents/mlsys/cuda/README.md),
- generic tensor-programming topics that are not Triton-specific,
- or transformer operator notes whose center of gravity is the model pipeline rather than Triton kernel structure.

Current notes:
- [01. Triton Syntax Primer](/Users/minkyu/Documents/mlsys/triton/01-triton-syntax-primer.md)
- [02. Launch Configuration and Kernel Reading](/Users/minkyu/Documents/mlsys/triton/02-launch-configuration-and-kernel-reading.md)
- [03. Embedding Lookup](/Users/minkyu/Documents/mlsys/triton/03-embedding-lookup.md)

## 0. Scope and Preconditions

This folder is not trying to be full Triton documentation.

Its job is narrower and more practical:

- make the first Triton kernels readable,
- make the execution model feel less magical,
- and keep the bridge between CUDA-style hardware reasoning and Triton kernel-writing explicit.

The intended reader already knows the basic ML systems context:

- tensors live on GPU memory,
- kernels run on device-side execution resources,
- addresses, strides, and layouts matter,
- and performance depends as much on memory traffic as on arithmetic.

If that layer still feels shaky, [cuda/README.md](/Users/minkyu/Documents/mlsys/cuda/README.md) should usually come first. Triton is easier to learn once the CUDA mental model is already there.

## 1. What Triton Changes Relative to CUDA

Triton sits in an interesting middle layer.

It is lower-level than ordinary PyTorch tensor code because the programmer still thinks in terms of:

- tiles,
- offsets,
- pointers,
- loads and stores,
- masks,
- and launch structure.

But it is higher-level than raw CUDA because the programmer does not usually start from:

- explicit thread and warp indexing,
- manually managed shared-memory declarations,
- or verbose kernel boilerplate.

So Triton changes the unit of thought.

In CUDA, the beginner usually starts with:

```text
what does one thread do?
```

In Triton, the better starting question is:

```text
what chunk of output does one program instance own?
```

That shift is the key to reading almost every beginner Triton kernel correctly.

## 2. Triton Starts with a Very Small Vocabulary

Most first-pass Triton confusion comes from the code looking more compressed than it really is.

A small number of constructs do most of the work:

- `tl.program_id` chooses which chunk the current program instance owns,
- `tl.arange` creates local offsets inside that chunk,
- pointer arithmetic turns logical indices into addresses,
- `tl.load` and `tl.store` move tile data,
- masks protect boundary cases,
- and a small set of elementwise, reduction, and dot-product primitives expresses the local computation.

That is why the first document in this folder is a syntax primer rather than a performance note.

Deep dive:
- [01. Triton Syntax Primer](/Users/minkyu/Documents/mlsys/triton/01-triton-syntax-primer.md)

The point of that note is not to catalog the whole language. The point is to reduce Triton to a small reusable grammar that can be recognized again and again in real kernels.

## 3. Launch Configuration Is Part of the Kernel Meaning

After the syntax layer, the next real hurdle is launch structure.

Many beginners can parse individual expressions like:

- `pid = tl.program_id(0)`
- `offs = pid * BLOCK + tl.arange(0, BLOCK)`
- `vals = tl.load(ptrs, mask=mask, other=0.0)`

but still cannot explain:

- how many program instances exist,
- which tile each instance owns,
- why `tl.constexpr` parameters shape the kernel,
- or what `num_warps` and `num_stages` are actually tuning.

That is why the second note exists. It reframes kernel reading around a stable workflow:

```text
ownership -> grid -> offsets -> pointers -> loads -> compute -> stores
```

Once that order becomes natural, simple Triton kernels stop feeling like compressed incantations and start feeling like ordinary block programs.

Deep dive:
- [02. Launch Configuration and Kernel Reading](/Users/minkyu/Documents/mlsys/triton/02-launch-configuration-and-kernel-reading.md)

## 4. The First Real Kernel Should Stay Simple

The first concrete Triton kernel should not be matmul.

That usually introduces too many ideas at once:

- multiple axes of ownership,
- tiled reductions,
- accumulator tiles,
- and more tuning parameters than a beginner can reason about comfortably.

An embedding lookup kernel is a better first real example because it keeps the math almost trivial and exposes the real skeleton:

- one program instance owns one token position,
- the token ID selects a row of the embedding table,
- `tl.arange` selects positions within the hidden dimension,
- pointer arithmetic builds source and destination addresses,
- masks protect the tail,
- and loads and stores do most of the visible work.

That makes embedding lookup an unusually clean example of Triton as a memory-movement DSL with just enough math to stay realistic.

Deep dive:
- [03. Embedding Lookup](/Users/minkyu/Documents/mlsys/triton/03-embedding-lookup.md)

## 5. The Main Distinctions This Folder Tries to Keep Sharp

This folder is trying to prevent a few very common confusions.

### Program instance is not CUDA thread

A Triton program instance owns a tile or chunk of work. It is closer to a block-level unit of local kernel work than to a single scalar CUDA thread.

### Offsets are not data

`tl.arange` and related expressions usually create logical positions, not loaded tensor values.

### Pointer arithmetic is not optional ceremony

In Triton, pointer arithmetic is often the real kernel logic. It decides what part of a tensor gets touched, in what layout, and with what memory behavior.

### Launch configuration is part of semantics, not just tuning

The grid and compile-time tile sizes do not merely affect speed. They define the ownership structure that makes the kernel meaningful.

### Boundary masks are part of correctness, not just cleanup

Many simple kernels are only correct because masked loads and stores protect the final partial tile.

## 6. Why This Folder Matters for ML Systems

Triton matters because many practical ML systems eventually run into a layer where high-level framework code is no longer the right abstraction for the problem at hand.

That happens when we need to reason more directly about:

- memory layout,
- tiling,
- bandwidth bottlenecks,
- fusion opportunities,
- reduction structure,
- and hardware-sensitive operator behavior.

Triton is often the first place where those concerns become programmable without dropping all the way into CUDA.

So this folder is not just about syntax.

It is about learning to see ML operators in a more implementation-shaped way:

- embedding lookup as address generation plus gather-like memory traffic,
- normalization as loads plus reductions plus scaling,
- softmax as max-reduction plus exponentials plus sum-reduction,
- and matrix multiplies as tiled load-accumulate-store programs.

That way of seeing operators is exactly what starts to matter when:

- profiling shows framework operators are the bottleneck,
- custom fused kernels become worth writing,
- or model-level understanding has to connect to actual GPU execution behavior.

## 7. File Sequence and Future Expansion

The current sequence is intentionally short:

1. syntax,
2. launch and reading workflow,
3. one real but simple kernel.

That sequence is meant to stay extensible.

This folder is not “done” once these three notes exist. It should be able to grow without breaking the main arc. Natural future additions include:

- vector add or copy-style warm-up kernels,
- reductions,
- softmax,
- RMSNorm or LayerNorm,
- matmul,
- fused attention-related kernels,
- and layout-sensitive transformer operators.

So the numbering here should be read as an open curriculum, not a closed table of contents.

## 8. After This Folder You Should Understand

After reading the current Triton sequence, you should be able to explain:

- why Triton thinks in program instances rather than individual CUDA threads,
- how launch grids and `tl.constexpr` parameters shape kernel ownership,
- how offsets become pointers through stride-aware arithmetic,
- why masked loads and stores are central to correctness,
- how to read a simple Triton kernel in a stable order,
- and why a kernel like embedding lookup is mostly about memory movement rather than heavy arithmetic.

That is enough to move from “Triton code looks mysterious” to “I can read small kernels and reason about what they are doing.” That transition is the real purpose of this folder.
