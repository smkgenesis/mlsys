# kernels

Kernel design principles that cut across frameworks.

Belongs here:
- data layout,
- fusion,
- unpack/dequant patterns,
- reduction patterns,
- and mapping algorithm structure onto hardware execution.

Does not belong here:
- CUDA-specific programming-model details; those belong in `cuda/`,
- Triton syntax and Triton-specific idioms; those belong in `triton/`.
