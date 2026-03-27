# 10.7 Output Projection, MLP, and Residual Path

This folder is for the non-attention decode substeps that still run on the current token after attention.

Upstream input:
- decode attention output from [10.5](../10-5-attention-during-decode/README.md)
- current residual stream

Downstream output:
- next decode-layer hidden state

Deep-dive focus:
- why these operations remain compute-heavy even in decode
- single-token GEMM behavior
- residual stream continuity through decode

Suggested local contents:
- decode-path notes
- kernel decomposition
- code references
- hardware cost discussions
