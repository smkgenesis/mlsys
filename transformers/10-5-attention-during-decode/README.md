# 10.5 Attention During Decode

This folder is for the decode-time attention step, where one current query reads many cached keys and values.

Upstream input:
- current-token Q, K, V from the current layer path
- historical KV cache from [8.5](../08-5-kv-cache-write-during-prefill/README.md) and [10.6](../10-6-kv-cache-extension/README.md)

Downstream output:
- decode attention output for the current token

Deep-dive focus:
- query-one vs many-key/value asymmetry
- memory-bandwidth pressure during decode
- paged attention kernels
- online softmax with streamed K/V tiles

Suggested local contents:
- decode-attention math
- paged-attention notes
- Triton/CUDA kernel analysis
- memory-traffic experiments
