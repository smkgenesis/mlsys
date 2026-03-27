# 8.4 Causal Self-Attention Over the Prompt

This folder is for prompt-side causal attention, where each query attends only to allowed earlier positions.

Upstream input:
- rotated Q and K from [8.3](../08-3-rotary-position-encoding/README.md)
- V from [8.2](../08-2-qkv-projection/README.md)

Downstream output:
- attention output tensor

Deep-dive focus:
- score computation
- causal masking
- softmax stability
- FlashAttention-style streaming kernels

Suggested local contents:
- attention math
- online softmax notes
- Triton/CUDA kernel analyses
- memory-traffic breakdowns
