# 10.4 Decode Layer Loop

This folder is for the per-layer recurrence that runs on one new token while reusing historical state.

Upstream input:
- current-token hidden state from [10.3](../10-3-one-token-embedding-lookup/README.md)
- cached K/V state from prior prompt and decode work

Downstream output:
- final-layer hidden state for the current decode token

Deep-dive focus:
- how decode differs structurally from prefill
- shape reductions from `B x N x d_model` to `B x 1 x d_model`
- where compute shrinks and memory reads persist

Suggested local contents:
- loop structure notes
- diagrams comparing prefill vs decode
- runtime traces
- performance breakdowns
