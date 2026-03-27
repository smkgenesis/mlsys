# 7.1 Token IDs to Initial Hidden Vectors

This folder is for embedding lookup, where token IDs become dense hidden vectors.

Upstream input:
- GPU-resident token IDs from [6.1](../06-1-prompt-token-ids-on-gpu/README.md)
- embedding table already loaded in HBM

Downstream output:
- initial hidden-state tensor `X^(0)`

Deep-dive focus:
- gather semantics
- row layout of the embedding table
- why embedding lookup is often memory-bound
- Python, Triton, and CUDA implementations

Suggested local contents:
- gather-kernel notes
- PyTorch `F.embedding` traces
- Triton prototypes
- bandwidth experiments
