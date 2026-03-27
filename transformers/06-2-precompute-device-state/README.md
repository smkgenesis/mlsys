# 6.2 State Just Before Model Math Begins

This folder is for the exact device-resident state that exists before the first model kernel runs.

Upstream input:
- GPU-resident prompt token IDs from [6.1](../06-1-prompt-token-ids-on-gpu/README.md)
- model weights loaded at initialization
- runtime metadata copied or mirrored to device

Downstream output:
- a complete device-side execution state ready for embedding lookup

Deep-dive focus:
- what is already in HBM before request math starts
- persistent weights vs per-request state
- device metadata buffers
- CUDA stream and launch preconditions

Suggested local contents:
- prelaunch checklists
- memory maps
- runtime buffer inventories
- instrumentation notes
