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

## Repository Map

- `roadmap/`: long-horizon study plan and capability progression.
- `foundations/`: core CS background reinterpreted for ML systems engineering.
- `math/`: probability, statistics, information theory, and error analysis for systems ML.
- `architecture/`: computer architecture and memory hierarchy.
- `gpu/`: GPU execution model and performance constraints.
- `cuda/`: CUDA-specific concepts and optimization patterns.
- `python/`: Python language and runtime patterns that matter in ML systems code.
- `triton/`: Triton kernel concepts and design patterns.
- `transformers/`: transformer internals from a systems perspective.
- `inference/`: LLM inference systems, serving, batching, and KV cache behavior.
- `training/`: training-time systems topics such as memory, optimizer state, and precision.
- `quantization/`: low-precision representation, error, and systems tradeoffs.
- `kernels/`: kernel design principles for ML workloads.
- `profiling/`: benchmarking methodology and profiler literacy.
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
