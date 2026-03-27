# 8.6 Attention Output Projection

This folder is for projecting concatenated attention heads back into model dimension.

Upstream input:
- attention outputs from [8.4](../08-4-causal-self-attention/README.md)

Downstream output:
- projected attention contribution

Deep-dive focus:
- head concatenation
- output GEMM structure
- Tensor Core mapping
- memory layout before and after projection

Suggested local contents:
- shape walkthroughs
- GEMM references
- Triton kernels
- performance notes
