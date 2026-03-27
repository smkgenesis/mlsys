# 9.3 First Next-Token Selection

This folder is for converting prompt-side logits into the first generated token.

Upstream input:
- logits from [9.2](../09-2-lm-head-and-vocabulary-projection/README.md)
- generation parameters from the request object

Downstream output:
- first generated token ID

Deep-dive focus:
- temperature scaling
- top-k and top-p filtering
- greedy vs stochastic sampling
- reduction and RNG kernels

Suggested local contents:
- sampling math
- implementation notes
- CUDA/Triton kernel references
- qualitative generation experiments
