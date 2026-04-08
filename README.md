# mlsys

A long-term public knowledge base for becoming a real ML systems engineer.

This repository is not a project log, a paper dump, or a random memo archive. It is a structured knowledge system for the concepts that matter in modern ML systems work: mathematical foundations, computer architecture, GPU execution, CUDA, Triton, transformer inference, training systems, quantization, kernel design, and performance profiling.

For repository standards, classification rules, and document acceptance criteria, see [`MANUAL.md`](MANUAL.md).
For the operating workflow that turns raw study/project conversation into curated repository content, see [`WORKFLOW.md`](WORKFLOW.md).

## Principles

- Store durable knowledge, not disposable notes.
- Prefer mechanism, tradeoff, and system interpretation over superficial summaries.
- Connect theory to implementation.
- Keep the structure stable enough that future study can build on it.
- Write so that returning months later is fast.

## Structural Rules

This repository should read more like a curriculum than a pile of notes.

That means three rules should stay stable across the repo:

### 1. Documents should use a consistent internal structure

Most serious documents should share a common skeleton so that reading feels predictable.

The default target sections are:

1. `What`
2. `Why It Matters`
3. `Core Idea` or `Core Mechanism`
4. `How It Works`, `Mechanism`, or another topic-specific middle section
5. `Common Mistakes`
6. `Why This Matters for ML Systems`
7. `Short Takeaway`

Not every file needs every section literally, but documents should not drift into arbitrary one-off heading structures without a reason.

### 2. Folder READMEs should act as canonical walkthroughs

Each folder should read in a natural order rather than as an unordered bucket.

The folder `README.md` should not be a shallow file index.
It should function as the top-level narrative for that folder, similar in spirit to `transformers/README.md`.

In practice this means:

- the folder `README.md` should itself be a real top-level document, not just a guide to other files,
- it should explain the whole conceptual or causal arc of the folder as one continuous narrative,
- it should cover the contents of the child documents in compressed form without leaving major gaps,
- it should point to deeper subdocuments where appropriate,
- the order should move from foundational material to more advanced material,
- and document titles and folder-local filenames should use visible sequence numbers when that improves readability and progression clarity.

That numbering should also remain extensible:

- new documents may be inserted later,
- so sequences should be designed to grow,
- and numbering should support future expansion rather than assuming the folder is already complete.

This is not a special rule for one folder.
It is the intended standard for the repository as a whole.

The point is to make each folder feel like a guided study track with one clear parent document and many deep dives beneath it.

### 3. Titles and filenames should stay short

Document titles should be concise and scan-friendly.
Filenames should be even shorter.

Use the document body to carry nuance.
Do not pack the entire outline into the title or filename.

The repository should optimize for:

- easy sidebar scanning,
- easy grep/search,
- and clear progression.

## Repository Map

- `roadmap/`: cross-topic study strategy, capability planning, and learning method.
- `foundations/`: basic computer science foundations only, such as core reasoning habits, recursion, stacks, and other CS-first concepts.
- `math/`: mathematics, probability, statistics, information theory, and mathematically primary discrete topics.
- `architecture/`: general computer architecture, memory hierarchy, ISA, and hardware cost/performance reasoning.
- `gpu/`: GPU execution ideas that are hardware-level but not CUDA-specific.
- `cuda/`: CUDA-specific execution model, launch model, and optimization patterns.
- `python/`: Python language and runtime patterns that matter in ML systems code.
- `triton/`: Triton programming model and kernel construction patterns.
- `transformers/`: transformer architecture and internal model computation.
- `inference/`: runtime behavior of serving and generation systems, including prefill/decode, batching, and KV cache management.
- `training/`: training-time system behavior and model optimization methods that matter for deployment or training efficiency.
- `quantization/`: low-precision representation, approximation error, and quantized runtime tradeoffs.
- `kernels/`: kernel design principles that cut across CUDA, Triton, and operator-specific implementation.
- `profiling/`: measurement, benchmarking, and profiler literacy.
- `physics/`: broader physics study as a long-term scientific foundation, with the same quality standards as the rest of the repository.

## Current Direction

This repository is being shaped to support long-term growth into ML systems engineering while staying general enough to remain public.

The focus is on building deep fluency in:

- memory and bandwidth bottlenecks,
- inference and training execution paths,
- low-precision compute,
- GPU kernel thinking,
- and rigorous performance measurement.

There is one intentional exception to the otherwise ML-systems-focused scope:

- `physics/` is allowed to be broader than direct ML systems relevance, because it serves as a long-term scientific foundation area.

## How To Use This Repo

Use it as a living reference, not as a chronological notebook.

Each directory should eventually contain:
- core concepts,
- why they matter in ML systems,
- tradeoffs and common mistakes,
- and links between theory and implementation.

Any new document added to this repository should follow the classification and quality rules in [`MANUAL.md`](MANUAL.md).

The intended outcome is simple:

> a repository that still feels organized, useful, and technically serious after years of study.
