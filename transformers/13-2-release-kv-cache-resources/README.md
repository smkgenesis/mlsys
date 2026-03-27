# 13.2 Release KV Cache Resources

This folder is for reclaiming request-local KV storage after request completion.

Upstream input:
- request completion state
- KV ownership metadata from [5.3](../05-3-kv-cache-reservation/README.md)

Downstream output:
- reusable KV blocks returned to the allocator

Deep-dive focus:
- block reclamation
- reference counting
- stale data vs logical reuse
- allocator invariants after completion

Suggested local contents:
- reclamation notes
- allocator code walkthroughs
- correctness tests
- fragmentation observations
