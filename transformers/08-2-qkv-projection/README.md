# 8.2 QKV Projection

This folder is for the linear projection that produces query, key, and value tensors.

Upstream input:
- normalized hidden states from [8.1](../08-1-attention-rmsnorm/README.md)
- trained projection weights

Downstream output:
- Q, K, V tensors with head structure

Deep-dive focus:
- GEMM formulation
- fused vs separate Q/K/V projections
- tiling strategy
- Tensor Core usage

Suggested local contents:
- matrix-shape notes
- Triton GEMM sketches
- CUDA kernel references
- weight-layout discussions
