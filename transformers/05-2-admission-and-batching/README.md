# 5.2 Admission and Batching

This folder is for the step that decides whether a request enters the next batch and in what execution mode.

Upstream input:
- tokenized request from [4.2](../04-2-prompt-string-to-token-ids/README.md)
- scheduler state from [5.1](../05-1-runtime-scheduling-context/README.md)

Downstream output:
- batch membership
- prefill or decode execution plan

Deep-dive focus:
- batching policy
- latency vs throughput tradeoffs
- continuous batching
- interaction with active decode traffic

Suggested local contents:
- scheduler policy comparisons
- batch-construction pseudocode
- request traces
- runtime benchmarks
