# 11.2 Token ID to Text Fragment

This folder is for the transformation from sampled token ID back into a decoded text fragment.

Upstream input:
- CPU-resident token ID from [11.1](../11-1-device-to-host-token-transfer/README.md)
- tokenizer decode tables from [4.1](../04-1-tokenizer-state/README.md)

Downstream output:
- text fragment or byte fragment

Deep-dive focus:
- inverse vocabulary lookup
- incremental detokenization
- UTF-8 and boundary correctness
- tokenizer-specific decode quirks

Suggested local contents:
- detokenization notes
- examples
- code references
- edge-case observations
