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
