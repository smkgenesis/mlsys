# 4.1 Tokenizer State Used at Inference Time

This folder is for the preloaded tokenizer state that exists before request execution begins.

Upstream input:
- tokenizer artifacts produced offline during tokenizer training and serialization

Downstream output:
- vocabulary tables and segmentation rules consumed by tokenization

Deep-dive focus:
- BPE or SentencePiece merge rules
- vocabulary layout in memory
- special-token handling
- CPU-side loading and caching

Suggested local contents:
- tokenizer theory
- vocabulary inspection scripts
- serialization format notes
- memory-layout sketches
