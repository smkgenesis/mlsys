# 10.1 Current Token Becomes Next-Step Input

This folder is for the causal handoff where the selected token becomes the known input to the next decode step.

Upstream input:
- first token from [9.3](../09-3-first-next-token-selection/README.md) or the prior decode step

Downstream output:
- current decode-step token ID

Deep-dive focus:
- autoregressive recurrence
- request-local token buffers
- server loop control between decode iterations

Suggested local contents:
- decode-loop notes
- runtime state diagrams
- buffer-management notes
- sampling-to-decode handoff traces
