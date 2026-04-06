# triton

Triton concepts for ML systems engineers.

Belongs here:
- the Triton programming model,
- block-level kernel design,
- pointer arithmetic,
- masks,
- tiling,
- and Triton-friendly layout decisions.

Does not belong here:
- CUDA-specific launch-model and execution-model material; that belongs in `cuda/`,
- framework-agnostic kernel concepts when they are not Triton-specific; those belong in `kernels/`.

Current notes:
- [Triton Syntax Primer: Program IDs, Offsets, Loads, Stores, Masks, and Tiles](triton-syntax-primer.md)
- [Triton Launch Configuration and Kernel Reading](launch-configuration-and-kernel-reading.md)
- [Triton Embedding Lookup Kernel Walkthrough](embedding-lookup-kernel.md)
