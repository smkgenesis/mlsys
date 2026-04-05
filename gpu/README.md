# gpu

GPU hardware execution concepts that matter in ML systems work.

Belongs here:
- SIMT execution,
- warps,
- occupancy,
- scheduling,
- synchronization,
- hardware limits,
- and GPU design ideas that are not tied to CUDA syntax.

Does not belong here:
- CUDA API and launch-model material; that belongs in `cuda/`,
- framework-specific kernel design guidance; that belongs in `kernels/` or `triton/`.

Current notes:
- [Heterogeneous Parallel Computing Foundations](heterogeneous-computing.md)
