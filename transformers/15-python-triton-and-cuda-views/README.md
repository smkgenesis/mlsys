# 15 Python, Triton, and CUDA Views of the Same Pipeline

This folder is for lining up the same Transformer inference path across high-level model code, kernel code, and hardware execution.

Upstream input:
- all prior pipeline stages, viewed through different abstraction levels

Downstream output:
- a cross-layer mental model connecting model code to kernels and hardware behavior

Deep-dive focus:
- PyTorch graph structure
- Triton tile programs and fusion choices
- CUDA execution and memory hierarchy
- where abstractions line up and where they hide detail

Suggested local contents:
- side-by-side code comparisons
- kernel mappings
- hardware notes
- debugging workflows
