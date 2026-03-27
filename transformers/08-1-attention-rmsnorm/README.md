# 8.1 Attention RMSNorm

This folder is for the normalization step that prepares hidden states for attention projections.

Upstream input:
- layer input hidden states from the previous residual stream

Downstream output:
- normalized hidden states consumed by QKV projection

Deep-dive focus:
- RMSNorm math
- reduction structure
- row-wise kernel design
- register/shared-memory use during normalization

Suggested local contents:
- derivations
- Triton kernels
- CUDA kernels
- numerical-stability notes
