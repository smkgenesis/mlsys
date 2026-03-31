# Heterogeneous Parallel Computing Foundations

## What

This document summarizes the foundational ideas introduced in Chapter 1 of *Programming Massively Parallel Processors*.

The central message is that modern high-performance computing is heterogeneous:

- CPUs are optimized for low-latency execution of a small number of threads,
- GPUs are optimized for high-throughput execution of massive numbers of threads,
- and real applications often need both.

This foundation is not specific to CUDA syntax. It explains why massively parallel programming exists, why GPU computing became central, and what limits real application speedups.

## Why It Matters

A programmer can learn CUDA or Triton syntax mechanically and still miss the actual reason GPU programming matters.

The deeper reason is that the old performance model broke. For many years, sequential programs became faster simply because each new CPU generation improved single-thread performance. That trend slowed when power and heat limits made continued clock-frequency scaling and increasingly aggressive single-core design too costly.

Once that happened, performance growth shifted toward parallel hardware:

- multicore CPUs for maintaining strong sequential performance while adding moderate thread-level parallelism,
- many-thread GPUs for maximizing total throughput across massive numbers of threads.

This shift matters for ML systems because modern workloads such as deep learning, large-scale simulation, and high-resolution media processing demand far more computation and data movement than low-thread-count execution can provide.

## Core Mechanism

### Latency-oriented CPUs and throughput-oriented GPUs

CPUs and GPUs follow different design philosophies.

A CPU is latency-oriented:

- it tries to reduce the execution time of an individual thread,
- it spends chip area and power on out-of-order execution, branch prediction, large caches, and complex control,
- and it performs best on sequential or lightly parallel workloads with irregular control flow.

A GPU is throughput-oriented:

- it allows individual threads to have higher latency,
- it uses chip area and power for many arithmetic units and many memory access channels,
- and it performs best when there is a very large amount of structured parallel work.

The practical interpretation is simple:

```text
CPU: make one worker fast
GPU: keep many workers busy
```

### Why many threads matter

GPU arithmetic operations and memory accesses can have long latency. Rather than trying to reduce that latency as aggressively as CPUs do, GPUs tolerate it by keeping many threads in flight.

When some threads are waiting on memory or arithmetic pipelines, other ready threads can execute. This is why GPU performance depends so strongly on whether an application can expose a large amount of parallel work.

### Why memory bandwidth matters

Many GPU applications are limited not only by arithmetic throughput but also by how quickly data can be delivered to the execution units.

GPUs devote significant hardware resources to memory throughput because graphics, simulation, and ML workloads all require moving very large amounts of data. This leads to an important practical point:

```text
Parallel speed requires both enough threads and efficient data delivery.
```

This is why data locality, coalescing, tiling, on-chip memory reuse, and bandwidth-aware design become central themes in GPU programming.

### Heterogeneous execution

Because CPUs and GPUs optimize different things, real applications often split work across them:

- CPU for sequential, control-heavy, irregular, or orchestration-heavy work
- GPU for numerically intensive, throughput-oriented, data-parallel work

CUDA and similar programming models exist because this CPU/GPU division of labor became a practical necessity rather than an edge case.

## Why More Speed Still Matters

Additional speed is not valuable only because old applications finish faster. It is valuable because it enables qualitatively different applications:

- larger and longer scientific simulations,
- higher-resolution media processing,
- more realistic interactive graphics and games,
- richer user interfaces,
- digital-twin style physical modeling,
- and modern deep learning.

These applications often process huge volumes of data, which creates natural opportunities for parallel execution. In many such applications, the world is represented or simulated at a finer level of detail as more throughput becomes available.

From this perspective, more speed means:

- larger scale,
- higher fidelity,
- lower latency,
- more interactivity,
- or entirely new product capabilities.

## Speeding Up Real Applications

### Application speedup versus kernel speedup

The speedup of system A over system B is:

```text
time on B / time on A
```

However, parallel hardware only helps the portion of the application that can actually be parallelized.

This is the key lesson of Amdahl's Law:

- if only a small part of the application is parallelizable, total application speedup will be limited no matter how fast that parallel part becomes,
- if nearly all of the runtime is spent in parallelizable code, very large application speedups become possible.

This means that the practical goal is not merely to make one kernel very fast. The goal is to ensure that most of the application's runtime sits in hardware-friendly, efficiently parallel code.

### Memory bandwidth as the next limit

Even when a program has abundant parallel work, speedup is often capped by DRAM bandwidth.

Straightforward parallelization may expose many threads but still fail to feed them with data efficiently. This is why real GPU optimization usually requires transformations such as:

- tiling,
- on-chip memory reuse,
- reduced DRAM traffic,
- more regular memory access patterns,
- and better locality.

## Challenges in Parallel Programming

Massively parallel execution is difficult for four recurring reasons.

### 1. Work efficiency

A poor parallel algorithm may perform much more total work than its sequential counterpart. If the extra work is too large, the parallel version may lose its advantage, especially for large inputs.

This is why parallel programming is not just about splitting work among threads. It is also about preserving algorithmic efficiency.

### 2. Memory-bound execution

Many applications are limited by memory latency or memory throughput rather than arithmetic throughput. In such cases, exposing many threads is not enough. The programmer must also improve data delivery behavior.

### 3. Sensitivity to input characteristics

Parallel programs are often more sensitive to irregular input sizes and uneven data distributions than sequential programs. If different threads receive very different amounts of work, load imbalance can dramatically reduce efficiency.

### 4. Synchronization overhead

Some applications are embarrassingly parallel and require little coordination. Others need barriers, atomic operations, or other synchronization mechanisms. These introduce waiting and reduce useful throughput.

Together these challenges mean that high-performance parallel programming requires more than correctness. It requires controlling work inflation, memory behavior, load balance, and coordination overhead.

## Related Programming Interfaces

Several major parallel programming interfaces target different hardware scopes and memory models.

- OpenMP:
  shared-memory parallelism with more compiler and runtime assistance
- MPI:
  distributed-memory programming across cluster nodes through explicit message passing
- CUDA:
  explicit programming of GPU parallelism within a node
- OpenCL:
  a standardized interface in a space similar to CUDA

CUDA is especially valuable as a learning vehicle because it exposes the underlying parallel execution and data-movement concepts directly. Those concepts transfer well to OpenCL and help programmers reason more effectively even when using higher-level interfaces such as OpenMP.

At large scale, CUDA and MPI are often combined:

- CUDA inside a node or device,
- MPI across nodes,
- and communication libraries such as NCCL across multiple GPUs.

## Overarching Goals of PMPP

The book frames massively parallel programming around three goals:

1. high performance,
2. correctness and reliability,
3. scalability across future hardware generations.

These goals reinforce one another.

Techniques such as regularizing memory accesses, reducing synchronization overhead, and localizing data often improve:

- current performance,
- predictability and debuggability,
- and scalability on more parallel future hardware.

This is why PMPP emphasizes hardware-aware computational thinking rather than treating GPU programming as mere API memorization.

## How the Book Is Structured

The book proceeds in four layers:

1. fundamentals of CUDA, execution, memory, and performance,
2. primitive parallel patterns,
3. advanced patterns and applications,
4. advanced practices such as cluster programming and dynamic parallelism.

This organization reflects a deeper teaching strategy:

- first understand the machine and execution model,
- then learn reusable computation patterns,
- then apply them to real applications,
- then extend to more advanced system-level practice.

The important implication is that the unit of learning is not just a command or API call. The unit of learning is a performance pattern and the hardware reasoning behind it.

## Tradeoffs

- CPU-friendly code is often easier to write and reason about, but it cannot deliver the throughput required by many modern workloads.
- GPU execution can deliver dramatic speedups, but only when the application exposes enough parallel work and feeds the hardware efficiently.
- Higher-level interfaces can improve portability and productivity, but explicit models such as CUDA are often better for learning and for performance-critical sections.
- Heterogeneous execution gives each processor the work it handles best, but it also introduces data movement and coordination costs between CPU and GPU.

## Common Mistakes

- Assuming that a fast GPU kernel automatically implies a fast application.
- Ignoring Amdahl's Law and overestimating achievable application-level speedup.
- Treating arithmetic throughput as the only performance limit while ignoring memory bandwidth.
- Assuming all parallel work is equally easy to scale, regardless of input irregularity or synchronization cost.
- Thinking GPUs simply behave like faster CPUs rather than processors designed around a different optimization target.
- Learning a programming model only as syntax and missing the hardware and workload assumptions that make the model useful.

## ML Systems Connection

These PMPP Chapter 1 ideas map directly onto modern ML systems.

- CPU responsibilities:
  request parsing, scheduling, batching, metadata management, networking, orchestration
- GPU responsibilities:
  embedding lookup, normalization, attention, MLP layers, tensor-heavy compute kernels

They also explain many recurring ML systems facts:

- training and inference need throughput, not just low single-thread latency,
- many tensor operations are highly data parallel,
- embedding lookup and decode steps are often memory-sensitive,
- bandwidth and data movement often dominate end-to-end performance,
- large speedups require that most runtime be moved into efficient GPU-executable regions,
- and future scalability depends on regular computation structure and disciplined memory behavior.

In this sense, PMPP Chapter 1 is not just historical context. It is a compact systems lens for understanding why GPU programming, CUDA, Triton, and hardware-aware ML engineering matter at all.
