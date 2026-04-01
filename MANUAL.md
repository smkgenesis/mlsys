# Knowledge Manual

This repository is a curated ML systems knowledge base.

It is not a dumping ground for:
- random notes,
- half-formed paper summaries,
- project-specific private reasoning,
- or low-signal daily logs.

Every document added here must satisfy a clear purpose, fit a category, and remain useful after time passes.

## Core Rule

Only store knowledge that is:
- durable,
- generalizable,
- technically meaningful,
- and reusable in future ML systems work.

If a note is too temporary, too project-specific, too shallow, or too fragmented, it does not belong here.

## What This Repo Stores

This repository stores:
- core concepts,
- mechanism-level explanations,
- system tradeoffs,
- performance reasoning,
- implementation-oriented understanding,
- and stable reference material for ML systems engineering.

This repository does **not** store:
- private project details,
- temporary brainstorming,
- unstructured reading logs,
- raw terminal transcripts,
- or “I might organize this later” fragments.

## Scope Exception: `physics/`

This repository is primarily for ML systems knowledge.

However, `physics/` is an intentional exception:
- it may include broader physics study even when the connection to ML systems is indirect,
- because long-term engineering depth can benefit from a stronger scientific foundation.

This exception changes the **scope**, but not the **quality standard**.

That means documents in `physics/` must still be:
- durable,
- structured,
- high-signal,
- and worth returning to later.

## Document Types

Every document should belong to exactly one of these types.

### 1. Concept Document
Purpose:
- explain a single important concept clearly and deeply.

Examples:
- memory coalescing
- arithmetic intensity
- KV cache growth
- mixed precision training

Expected contents:
- what it is,
- why it matters,
- core mechanism,
- tradeoffs,
- common mistakes,
- and system relevance.

### 2. System Document
Purpose:
- explain how multiple concepts interact inside a real ML system.

Examples:
- prefill vs decode
- bandwidth bottlenecks in LLM inference
- activation memory in training
- CUDA execution hierarchy

Expected contents:
- subsystem definition,
- cost model,
- constraints,
- failure modes,
- and implementation implications.

### 3. Method Document
Purpose:
- explain a repeatable engineering method or evaluation method.

Examples:
- how to benchmark tokens/sec correctly
- how to profile GPU memory behavior
- how to reason about quantization error in practice

Expected contents:
- objective,
- measurement rules,
- pitfalls,
- interpretation rules,
- and practical checklist.

### 4. Foundation Bridge Document
Purpose:
- connect formal coursework or theory to ML systems practice.

Examples:
- discrete structures for scheduling policies
- computer architecture for GPU bottlenecks
- operating systems concepts for inference runtimes

Expected contents:
- academic topic,
- ML systems connection,
- concrete applications,
- and why the topic matters beyond exams.

### 5. Roadmap Document
Purpose:
- define a long-term study path or capability progression.

Examples:
- CUDA learning path
- Triton mastery roadmap
- ML systems engineer growth plan

Expected contents:
- scope,
- sequence,
- prerequisites,
- and milestone capabilities.

## Document Boundary Rule

The default unit of writing in this repository is a concept-level document.

That does not mean every noun deserves its own file. A topic should become a standalone document only when it has all of the following:
- a clear question it answers,
- a coherent mechanism or idea that can be explained as one unit,
- reusable value outside the moment that produced it,
- and a scope narrow enough to stay precise.

A topic should usually remain part of another document when:
- it is only a subsection of a larger explanation,
- it depends too heavily on local project context,
- it is too broad to stay coherent in one file,
- or it does not yet support a meaningful explanation beyond a definition.

Useful heuristic:
- if a topic can be summarized as "What is X, why does it matter, how does it work, and what tradeoffs does it create?" it is often a good standalone concept document,
- if the explanation is really about how multiple concepts interact, it should be a system document instead,
- if the explanation is really about process or evaluation, it should be a method document instead.

Split a document when multiple sections are each becoming independently reusable concepts.
Merge documents when they answer nearly the same question and repeat the same mechanism or tradeoff analysis.

## Placement Rules

Each document must go into the directory that best matches its dominant purpose.

- `foundations/`: bridge from core CS coursework to systems engineering; math-heavy material should usually go in `math/`
- `math/`: probability, statistics, information theory, numerical reasoning
- `architecture/`: memory hierarchy, compute organization, hardware cost reasoning
- `gpu/`: GPU execution behavior and hardware constraints
- `cuda/`: CUDA-specific execution and optimization knowledge
- `triton/`: Triton-specific kernel thinking and layout strategy
- `transformers/`: transformer internals from a systems lens
- `inference/`: inference runtime behavior and serving concerns
- `training/`: training-time systems behavior
- `quantization/`: low-precision methods and error/runtime tradeoffs
- `kernels/`: kernel construction principles
- `profiling/`: benchmarking and measurement discipline
- `roadmap/`: long-horizon study structure
- `physics/`: broader physics foundations, allowed to be more general than the rest of the repository

If a document could fit in multiple places, choose the directory that best answers:

> “What is this document primarily trying to teach?”

## File Naming Rules

Use lowercase kebab-case names.

Filenames should be short, scan-friendly, and concept-centered.
The filename should usually be the shortest phrase that uniquely identifies the document's main idea.
Put fuller scope and nuance in the document title and opening paragraphs rather than packing the entire summary into the filename.

Good:
- `memory-coalescing.md`
- `prefill-vs-decode.md`
- `roofline-model.md`
- `cuda-program-structure.md`
- `efficient-ai-and-scaling-laws.md`

Bad:
- `MyNotes.md`
- `week3.md`
- `stuff-about-cuda.md`
- `important.md`
- `a-document-that-tries-to-put-the-entire-outline-in-the-filename.md`

The filename should make sense in search results without surrounding context, but it should still remain easy to scan in directory listings and sidebars.

## Writing Standard

Every serious document should try to include these sections when applicable:

1. `What`
2. `Why It Matters`
3. `Core Mechanism`
4. `Tradeoffs`
5. `Common Mistakes`
6. `ML Systems Connection`

Not every file needs every section, but shallow summary-only documents are not acceptable.

## ML Systems Relevance Rule

Outside `physics/`, documents should usually explain why the topic matters for ML systems work.

This is the default standard because the repository is not a general study notebook. It is a knowledge base for becoming stronger at ML systems engineering.

In practice this means most non-`physics/` documents should connect the topic to at least one of:
- inference behavior,
- training behavior,
- kernel design,
- memory and bandwidth limits,
- low-precision compute,
- profiling and measurement,
- or hardware-aware implementation reasoning.

This rule is strong but not absolute.

If a concept is clearly foundational and worth keeping, it may be added before the ML systems connection is fully developed, as long as:
- the document is still durable and technically meaningful,
- the direction toward ML systems relevance is plausible,
- and the file is likely to be improved later rather than abandoned as generic background trivia.

## Quality Standard

A document is worth keeping only if it does at least one of these well:
- explains a mechanism better than a textbook summary,
- connects theory to implementation,
- clarifies a common confusion,
- or improves future engineering decisions.

A document should be rejected or rewritten if it is:
- mostly definitional without insight,
- too tied to one hidden private project,
- too vague to help future work,
- or mostly a memory aid for the moment rather than durable knowledge.

## Confidentiality Rule

This repository is public.

Do not include:
- confidential project details,
- internal benchmark artifacts,
- unpublished implementation specifics,
- or proprietary reasoning that should remain private.

When a private project motivates a document, rewrite the lesson in general form.

Bad:
- “Here is what we discovered in project X.”

Good:
- “Here is a general systems lesson about decode-time memory traffic.”

## Depth Rule

Prefer fewer, higher-quality documents over many shallow ones.

A short document is acceptable if it is:
- precise,
- complete in scope,
- and useful on its own.

A long document is acceptable only if it stays structured and high-signal.

## Update Rule

Do not create a new file when an existing file should simply be improved.

Create a new document only when:
- the topic is genuinely distinct,
- the scope is too large for the current file,
- or the new file would become a meaningful standalone reference.

When new knowledge appears through study, conversation, project work, or experimentation:
- prefer extracting the durable lesson instead of preserving the original context,
- prefer updating an existing document when the concept already has a natural home,
- and create a new document only when the extracted knowledge is distinct enough to stand on its own.

Raw conversation is not repository content. Repository content is the cleaned, generalized result of reasoning over that conversation.

## Source Material Rule

Useful source material can come from:
- coursework,
- personal study,
- research reading,
- project work,
- debugging,
- experiments,
- and conversations that uncover real understanding.

However, source material must be transformed before entering the repository.

The repository should store:
- the generalized concept,
- the mechanism,
- the durable lesson,
- and the system interpretation.

The repository should not store:
- chat transcripts,
- class-specific phrasing,
- private project details,
- or temporary thought fragments preserved only because they happened to be written down.

## Acceptance Checklist

Before adding a document, check:

- Is the topic durable?
- Is the scope clear?
- Is the filename precise?
- Does it belong in exactly one main directory?
- Does it explain mechanism, not just terminology?
- Would this still be useful six months later?
- Does it avoid private project leakage?

If the answer to several of these is “no,” the document should not be added yet.

## Intended Standard

The target is not “organized notes.”

The target is:

> a public repository that reads like the long-term knowledge system of someone becoming deeply competent in ML systems engineering.
