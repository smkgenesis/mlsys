# 10.6 KV Cache Extension

This folder is for appending the current decode token's K and V into the cache for future steps.

Upstream input:
- current token's K and V from the active decode layer path
- request-specific KV mapping from [5.3](../05-3-kv-cache-reservation/README.md)

Downstream output:
- longer KV cache that includes the current step

Deep-dive focus:
- decode-time append semantics
- cache growth by sequence step
- write ordering and correctness
- interaction with paged block allocators

Suggested local contents:
- append-path notes
- block-table updates
- write-kernel references
- validation tests
