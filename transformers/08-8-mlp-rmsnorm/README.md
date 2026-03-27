# 8.8 MLP RMSNorm

This folder is for the normalization that prepares the post-attention residual state for the MLP block.

Upstream input:
- post-attention residual state from [8.7](../08-7-residual-add-after-attention/README.md)

Downstream output:
- normalized MLP input

Deep-dive focus:
- repeated normalization pattern inside a layer
- reuse of row-wise reduction strategies
- fusion boundaries around normalization and MLP

Suggested local contents:
- mathematical notes
- normalization kernels
- implementation comparisons
- profiling notes
