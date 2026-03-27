# 10.8 Final Norm, LM Head, and Token Selection in Decode

This folder is for the end of each decode iteration, where the current hidden state becomes logits and then the next token.

Upstream input:
- final decode-step hidden state from [10.4](../10-4-decode-layer-loop/README.md)
- generation parameters from the original request

Downstream output:
- next token ID
- updated autoregressive state

Deep-dive focus:
- decode-time final norm and vocabulary projection
- sampling path per generated token
- how this closes one decode iteration

Suggested local contents:
- decode-output notes
- sampling kernel references
- code traces
- per-token latency measurements
