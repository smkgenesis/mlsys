# Repository Workflow

This repository is intended to work with free-form idea capture and strict downstream curation.

The input may come from:
- coursework,
- project work,
- private study,
- or open-ended discussion.

The stored result should still look like a stable ML systems knowledge base.

## Operating Principles

- Conversation is raw material, not final content.
- The repository stores durable knowledge, not the history of how that knowledge was discovered.
- The default unit is a concept document, but concept boundaries are judged by coherence, not by vocabulary.
- Existing documents should absorb new knowledge when possible.
- New files should only be created when the extracted topic is distinct enough to justify independent reuse.
- Outside `physics/`, documents should usually grow toward explicit ML systems relevance.
- Strong principles are preferred over rigid bureaucracy; valuable knowledge can be added before it is perfectly complete.

## Conversion Method

When raw discussion is turned into repository content, use this sequence:

1. Extract the durable claims.
2. Separate general concepts from examples, analogies, and project-local details.
3. Identify whether the result is best represented as a concept, system, method, bridge, or roadmap document.
4. Decide whether the material belongs in an existing file or needs a new one.
5. Rewrite the content into reusable, repository-grade language.
6. Add or strengthen ML systems relevance when appropriate.
7. Reject or defer material that is still too shallow, too temporary, or too context-bound.

## Concept Boundary Heuristics

A topic is a good standalone document when:
- it answers one clear question,
- its explanation has one dominant mechanism or idea,
- it can be revisited later without needing the original conversation,
- and it reduces repetition elsewhere in the repository.

A topic is not yet a good standalone document when:
- it is only a loose category label,
- it is mostly a definition with no mechanism,
- it only makes sense inside one private project,
- or it should really be a section inside a broader file.

Examples of likely standalone documents:
- `memory-coalescing.md`
- `sram-vs-dram.md`
- `kv-cache-growth.md`
- `prefill-vs-decode.md`

Examples of topics that often need reframing before becoming files:
- `cuda-optimization.md`
- `things-about-memory.md`
- `week-3-notes.md`

## Classification Method

Use the directory that best matches the document's dominant teaching purpose.

- Put it in `architecture/` when the core lesson is hardware structure, hierarchy, ISA, or cost reasoning.
- Put it in `cuda/` when the core lesson is GPU execution behavior as exposed through CUDA kernels, launch structure, memory spaces, or CUDA-side optimization.
- Put it in `triton/` when the material is specifically about the Triton programming model, pointer arithmetic, masks, tiling, or Triton kernel reading/writing.
- Put it in `transformers/` when the core lesson is the causal computation path inside transformer inference or model-side sequence processing.
- Put it in `training/` when training-side efficiency, optimization methods, or training/deployment tradeoffs are the real subject.
- Put it in `quantization/` when numerical representation, low-precision formats, or quantization error/runtime tradeoffs are the real subject.
- Put it in `foundations/` when formal coursework is being translated into ML systems understanding.
- Put it in `physics/` only for the intentional scope exception.

When a topic fits multiple places, classify by the primary teaching goal, not by every keyword that appears in the text.

## Commit Method

When asked to commit repository updates:

1. Review the relevant conversation-derived material.
2. Extract the stable knowledge worth keeping.
3. Update existing files first when appropriate.
4. Create new files only when the topic has a clean independent boundary.
5. Keep filenames precise and in lowercase kebab-case.
6. Ensure the written result is generalized and safe for a public repository.
7. Commit only the curated repository state, not the raw discussion that produced it.

## Practical Standard

The target is not perfect completeness on first write.

The target is a repository that can steadily improve while remaining:
- organized,
- technically serious,
- reusable,
- and clearly oriented toward long-term ML systems competence.
