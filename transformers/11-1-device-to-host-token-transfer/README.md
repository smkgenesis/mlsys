# 11.1 Device-to-Host Token Transfer

This folder is for bringing selected token IDs back to CPU-side response logic when needed.

Upstream input:
- selected token IDs from prompt-side or decode-side token selection

Downstream output:
- CPU-resident token IDs ready for detokenization

Deep-dive focus:
- GPU-to-CPU transfer path
- synchronization points
- when runtimes avoid or require these copies

Suggested local contents:
- transfer notes
- code references
- latency measurements
- stream-synchronization notes
