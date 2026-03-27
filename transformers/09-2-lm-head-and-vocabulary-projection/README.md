# 9.2 LM Head and Vocabulary Projection

This folder is for the projection from hidden state to vocabulary logits.

Upstream input:
- final normalized hidden state from [9.1](../09-1-final-normalization/README.md)
- vocabulary projection weights

Downstream output:
- logits over the vocabulary

Deep-dive focus:
- matrix-vector or small-GEMM structure
- weight tying vs separate LM head
- large-vocabulary cost profile
- hardware behavior of the final projection

Suggested local contents:
- LM head notes
- weight-tying discussion
- GEMM references
- profiling data
