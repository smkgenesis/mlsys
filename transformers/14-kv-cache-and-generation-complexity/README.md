# 14 Why KV Cache Makes Generation Practical

This folder is for the systems-level explanation of why autoregressive generation would be impractical without KV reuse.

Upstream input:
- full prompt-side KV cache established during prefill
- decode recurrence that reuses historical state

Downstream output:
- conceptual understanding of prefill/decode asymmetry

Deep-dive focus:
- recomputation avoided by caching
- complexity shift from prefill to decode
- bandwidth-dominated decode behavior
- practical consequences for serving systems

Suggested local contents:
- complexity notes
- roofline-style discussions
- decode cost models
- empirical measurements
