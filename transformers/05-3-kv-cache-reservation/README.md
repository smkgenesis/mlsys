# 5.3 KV Cache Reservation

This folder is for the logical and physical reservation of KV storage before attention kernels use it.

Upstream input:
- execution plan from [5.2](../05-2-admission-and-batching/README.md)
- model structural parameters
- free-block metadata from the serving runtime

Downstream output:
- request-specific KV allocation metadata
- logical-to-physical block mapping

Deep-dive focus:
- paged KV cache design
- fragmentation and block reuse
- address mapping for `(request, layer, token)`
- metadata placement on CPU and GPU

Suggested local contents:
- allocator notes
- block-table examples
- runtime code walkthroughs
- memory-fragmentation experiments
