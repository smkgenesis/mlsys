# 8.7 Residual Add After Attention

This folder is for the residual merge that adds the attention contribution back into the main stream.

Upstream input:
- layer input hidden state
- projected attention contribution from [8.6](../08-6-attention-output-projection/README.md)

Downstream output:
- post-attention residual state

Deep-dive focus:
- why residual paths matter
- elementwise kernel behavior
- fusion opportunities with neighboring ops
- numerical effects of residual scaling variants

Suggested local contents:
- residual-path notes
- fused-kernel ideas
- code references
- precision experiments
