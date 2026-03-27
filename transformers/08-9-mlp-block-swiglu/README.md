# 8.9 MLP Block (SwiGLU Style)

This folder is for the feed-forward block that expands, gates, and projects hidden states.

Upstream input:
- normalized MLP input from [8.8](../08-8-mlp-rmsnorm/README.md)
- trained MLP weights

Downstream output:
- MLP contribution in model dimension

Deep-dive focus:
- up/gate/down projections
- SiLU and gating mechanics
- fused elementwise kernels
- compute intensity and memory behavior

Suggested local contents:
- SwiGLU theory
- GEMM decomposition notes
- Triton/CUDA prototypes
- benchmark results
