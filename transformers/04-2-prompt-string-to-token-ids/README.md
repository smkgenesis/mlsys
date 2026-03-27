# 4.2 Prompt String to Token IDs

This folder is for the transformation from a parsed prompt string to integer token IDs.

Upstream input:
- request prompt string from request construction
- tokenizer state from [4.1](../04-1-tokenizer-state/README.md)

Downstream output:
- `input_ids` in CPU RAM
- prompt length metadata

Deep-dive focus:
- segmentation mechanics
- piece-to-ID mapping
- Unicode and boundary behavior
- why tokenization remains CPU-heavy in many runtimes

Suggested local contents:
- worked tokenization examples
- Python tokenizer traces
- edge-case notes
- latency measurements
