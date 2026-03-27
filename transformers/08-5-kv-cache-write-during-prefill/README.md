# 8.5 KV Cache Write During Prefill

This folder is for storing prompt-side keys and values into the request's KV cache.

Upstream input:
- K and V tensors produced earlier in the current layer
- KV block mapping from [5.3](../05-3-kv-cache-reservation/README.md)

Downstream output:
- populated prompt KV cache used by later decode steps

Deep-dive focus:
- write path from dense layer outputs into paged storage
- logical-to-physical addressing
- coalescing and scatter behavior
- prefill-to-decode causal handoff

Suggested local contents:
- cache-layout notes
- write-kernel sketches
- allocator integration notes
- memory-validation experiments
