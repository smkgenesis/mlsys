# 10.3 One-Token Embedding Lookup

This folder is for decode-time embedding lookup on a single current token.

Upstream input:
- device-resident current token ID from [10.2](../10-2-device-placement-of-current-token/README.md)
- embedding table already in HBM

Downstream output:
- current-token hidden state entering layer 0

Deep-dive focus:
- single-token gather behavior
- why decode differs from prompt embedding lookup
- memory access patterns at batch size and sequence length extremes

Suggested local contents:
- gather notes
- code traces
- kernel experiments
- decode-specific performance analysis
