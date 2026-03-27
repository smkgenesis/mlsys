# 8.3 Rotary Position Encoding

This folder is for applying position-dependent rotations to Q and K.

Upstream input:
- Q and K tensors from [8.2](../08-2-qkv-projection/README.md)
- token position indices derived from prompt order
- RoPE frequency tables from model configuration

Downstream output:
- position-aware rotated Q and K tensors

Deep-dive focus:
- complex-plane interpretation of RoPE
- pairwise dimension rotation
- table generation and storage
- kernel-level implementation details

Suggested local contents:
- mathematical derivations
- Python reference code
- Triton/CUDA kernels
- long-context extension notes
