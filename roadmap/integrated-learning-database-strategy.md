# Integrated Learning Database Strategy

## What

This document defines how to organize this repository as one integrated learning database even when the source materials come from very different places:

- textbooks such as *Programming Massively Parallel Processors*
- official documentation such as the CUDA C++ Programming Guide
- ML systems textbooks such as *Introduction to Machine Learning Systems*
- research papers such as FlashAttention or LLM serving papers
- project work and implementation experience

The goal is to ensure that the repository is organized by durable understanding rather than by source origin.

## Why It Matters

If the repository grows by following the boundaries of its sources, it will eventually fragment into:

- PMPP-style notes,
- CUDA-guide notes,
- MLSys-book notes,
- paper notes,
- and project notes

that all describe overlapping ideas using different vocabulary.

That makes the repository harder to use because the same concept gets rediscovered in multiple places:

- once as a hardware concept,
- once as a kernel optimization,
- once as a transformer bottleneck,
- once as a serving tradeoff,
- and once as an efficiency discussion.

The right organizing principle is not:

> “Which book or paper did this come from?”

The right organizing principle is:

> “What stable question does this knowledge help answer?”

## Core Principle

Every source should be decomposed into reusable knowledge units and then reclassified into the repository by dominant teaching purpose.

The repository should be source-agnostic at rest.

That means:

- read sources in their original structure,
- understand them in context,
- but store the resulting knowledge in concept, system, method, and roadmap documents that are independent of the original source.

## The Integrated Knowledge Model

All incoming material should be translated into one or more of these five layers.

### 1. Foundations Layer

Purpose:
- core ideas that improve systems reasoning across many domains

Typical questions:
- what is a thread?
- what is sequential versus parallel execution?
- what is latency versus throughput?
- what is work efficiency?
- what is a memory-bound workload?

Primary destinations:
- `foundations/`
- `math/`
- `architecture/`

### 2. Hardware and Execution Layer

Purpose:
- how actual processors and memory systems behave

Typical questions:
- how do CPU and GPU design philosophies differ?
- what are warps, occupancy, scheduling, and divergence?
- what are bandwidth limits and locality effects?
- what memory hierarchy behaviors matter in practice?

Primary destinations:
- `architecture/`
- `gpu/`
- `cuda/`

### 3. Kernel and Program Construction Layer

Purpose:
- how parallel work is expressed in code and mapped to hardware

Typical questions:
- how does a Triton kernel express a tile?
- how do loads, stores, masks, and pointer arithmetic work?
- when is a kernel reduction-heavy versus bandwidth-heavy?
- how do CUDA and Triton encode execution and memory decisions?

Primary destinations:
- `cuda/`
- `triton/`
- `kernels/`

### 4. ML System Layer

Purpose:
- how model computation, serving, training, and deployment behave as systems

Typical questions:
- what is prefill versus decode?
- why is KV cache management central to LLM serving?
- why is attention expensive?
- what determines inference throughput and latency?
- what is algorithmic, compute, and data efficiency?

Primary destinations:
- `transformers/`
- `inference/`
- `training/`
- `quantization/`
- `profiling/`

### 5. Integration and Decision Layer

Purpose:
- how to choose what to optimize, what to study next, and how topics connect

Typical questions:
- what capability is currently missing?
- which bottleneck matters most?
- which optimization dimension is dominant in this context?
- what study sequence best builds the next level of fluency?

Primary destinations:
- `roadmap/`

## Source-to-Repository Translation Rules

Each source category tends to contribute more heavily to some layers than others.

### Textbooks such as PMPP

Primary contribution:
- hardware and execution layer
- parallel patterns
- performance reasoning

Typical repository destinations:
- `gpu/`
- `cuda/`
- `architecture/`
- `kernels/`
- `roadmap/`

PMPP should usually be mined for:
- durable execution-model concepts
- memory and scheduling principles
- parallel patterns and optimization logic

It should not be stored as chapter-by-chapter reading notes unless a chapter itself defines a stable reusable topic.

### Official documentation such as the CUDA C++ Programming Guide

Primary contribution:
- precise definitions
- memory model and execution model details
- API behavior and constraints

Typical repository destinations:
- `cuda/`
- `gpu/`
- `kernels/`

Documentation should usually be converted into:
- concept references
- behavioral explanations
- constraint-focused notes

It should not dominate the repository tone. The repository should explain the ideas, not mirror the documentation structure.

### ML systems textbooks such as *Introduction to Machine Learning Systems*

Primary contribution:
- system tradeoffs
- deployment constraints
- efficiency frameworks
- evaluation thinking

Typical repository destinations:
- `inference/`
- `training/`
- `profiling/`
- `roadmap/`
- occasionally `gpu/` or `architecture/` when the lesson is really hardware-facing

These materials are especially valuable for turning low-level understanding into system-level decision frameworks.

### Research papers

Primary contribution:
- specialized mechanisms
- concrete optimization strategies
- design tradeoffs validated in practice

Typical repository destinations:
- `kernels/`
- `inference/`
- `training/`
- `quantization/`
- `profiling/`
- `transformers/`

Papers should usually be rewritten into:
- what problem they solve,
- what bottleneck they attack,
- what mechanism they introduce,
- what tradeoff they accept,
- and how broadly the lesson generalizes.

### Personal implementation work

Primary contribution:
- validation of understanding
- practical edge cases
- debugging and performance lessons

Typical repository destinations:
- update existing concept and system documents first
- create new documents only when the lesson has clear independent reuse value

The repository should store the generalized lesson, not a private build log.

## Canonical Axes for Integrating Knowledge

To make notes from different sources reinforce each other, incoming material should be normalized along a consistent set of axes.

Every serious note should try to answer some subset of:

1. What problem is being solved?
2. What is the dominant bottleneck?
3. What resource is scarce?
4. What execution pattern is used?
5. What memory movement pattern matters?
6. What tradeoff is being accepted?
7. What workload regime does the idea apply to?
8. How does this connect to ML systems?

These axes are more stable than source vocabulary.

For example:

- PMPP may describe throughput-oriented design,
- the CUDA guide may describe execution resources and memory spaces,
- a FlashAttention paper may describe IO awareness,
- an MLSys textbook may describe efficiency and deployment constraints.

All of these can still be normalized into the same questions:

- what is scarce?
- where is the bottleneck?
- what memory movement is being reduced?
- what tradeoff is being made?

## Practical Filing Strategy

When a new concept appears in multiple sources, prefer one durable home and cross-link from neighboring topics instead of duplicating the full explanation.

### Examples

`latency vs throughput`
- primary home: `gpu/` or `architecture/`
- reused by: `inference/`, `training/`, `profiling/`

`memory bandwidth as a bottleneck`
- primary home: `gpu/` or `architecture/`
- reused by: `transformers/`, `inference/`, `kernels/`

`embedding lookup as gather and memory-bound work`
- primary home: `transformers/`
- linked from: `triton/`, `gpu/`, `inference/`

`Triton pointer arithmetic and masks`
- primary home: `triton/`
- linked from: `transformers/` kernel walk-throughs

`KV cache serving tradeoffs`
- primary home: `inference/`
- linked from: `transformers/`

`scaling laws and compute-optimal allocation`
- primary home: `training/` or `roadmap/` depending on emphasis
- linked from: `inference/` and `profiling/` if needed

## Writing Rules for Source Integration

When adding material from any source:

1. Extract the durable claim.
2. Identify the dominant layer in the integrated knowledge model.
3. Place the note in the directory that best matches that teaching purpose.
4. Rewrite the material in repository language rather than source language.
5. Add ML systems relevance if it is not already obvious.
6. Link outward to related concepts rather than duplicating them.

## What Not to Do

Avoid these failure modes:

- making one directory per source or author
- storing chapter-by-chapter notes without conceptual reframing
- keeping separate parallel explanations of the same concept because they came from different materials
- treating papers as valuable mainly because they are papers rather than because they teach a durable mechanism
- mixing implementation diary content with repository-grade knowledge

## Immediate Reorganization Guidance for This Repository

Given the current materials and study direction, the repository should increasingly behave like this:

- `architecture/`
  formal machine structure, cost models, and baseline computer architecture reasoning
- `gpu/`
  GPU design philosophy, throughput execution, occupancy, latency hiding, bandwidth limits
- `cuda/`
  CUDA execution model, memory spaces, synchronization semantics, programming constraints
- `triton/`
  Triton syntax, kernel reading, pointer arithmetic, tiling, reductions, kernel walk-throughs
- `transformers/`
  end-to-end causal model of autoregressive inference and operator-level transformer mechanics
- `kernels/`
  reusable kernel design lessons and optimization patterns that are broader than one operator
- `inference/`
  serving systems, batching, scheduling, KV cache, latency/throughput tradeoffs, deployment bottlenecks
- `training/`
  scaling laws, optimization cost, data/compute tradeoffs, training-time systems behavior
- `profiling/`
  how to measure, validate, and interpret performance claims
- `roadmap/`
  capability progression, integration strategy, and study sequencing

## Decision Rule

When unsure where something belongs, ask:

> What is this material primarily trying to teach after the source has been removed?

If the answer is:

- hardware behavior -> `architecture/` or `gpu/`
- programming model mechanics -> `cuda/` or `triton/`
- transformer operator meaning -> `transformers/`
- serving/runtime tradeoff -> `inference/`
- optimization pattern across many kernels -> `kernels/`
- training-scale resource tradeoff -> `training/`
- measurement discipline -> `profiling/`
- capability planning -> `roadmap/`

That rule should dominate over source identity.

## Outcome

If this strategy is followed consistently, the repository becomes:

- source-agnostic,
- easier to search,
- more reusable over time,
- better at integrating textbooks, docs, papers, and implementation work,
- and more aligned with the actual goal of becoming strong at ML systems engineering rather than merely collecting reading notes.
