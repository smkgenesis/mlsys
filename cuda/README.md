# cuda

CUDA-specific execution and optimization knowledge.

Belongs here:
- thread/block/grid structure,
- CUDA memory spaces,
- kernel optimization patterns,
- launch configuration,
- occupancy and scheduling as exposed through CUDA,
- and CUDA-specific constraints or failure modes.

Does not belong here:
- GPU concepts that are hardware-general and not CUDA-specific; those belong in `gpu/`,
- framework-agnostic kernel design principles; those belong in `kernels/`.

Current notes:
- [CUDA Program Structure: Data Parallelism, Kernels, Threading, Memory Transfer, and Compilation](cuda-program-structure.md)
- [Multidimensional Grids and Data Mapping](multidimensional-grids-and-data-mapping.md)
- [CUDA Compute Architecture and Scheduling](compute-architecture-and-scheduling.md)
