# 5.1 Runtime Scheduling Context

This folder is for the server-side runtime state that already exists before a request enters execution.

Upstream input:
- active-request queues
- runtime policy state
- model-specific serving metadata

Downstream output:
- admission decisions and execution-mode classification

Deep-dive focus:
- scheduler invariants
- request lifecycle state machines
- prefill-vs-decode classification
- runtime metadata structures in CPU RAM

Suggested local contents:
- scheduler notes
- serving-runtime code references
- diagrams of state transitions
- queueing-policy experiments
