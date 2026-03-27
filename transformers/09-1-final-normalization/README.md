# 9.1 Final Normalization

This folder is for the last normalization applied after the final Transformer layer.

Upstream input:
- final-layer hidden states from the prefill or decode path

Downstream output:
- normalized hidden states ready for vocabulary projection

Deep-dive focus:
- role of the final norm in the output path
- prompt-side vs decode-side use
- implementation structure

Suggested local contents:
- formula notes
- code walkthroughs
- kernel references
- comparisons across model families
