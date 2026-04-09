# cuda

CUDA-specific execution and optimization knowledge.

Belongs here:
- heterogeneous CPU/GPU execution framing when it is used as the precondition for CUDA,
- thread/block/grid structure,
- CUDA memory spaces,
- kernel optimization patterns,
- launch configuration,
- occupancy and scheduling as exposed through CUDA,
- and CUDA-specific constraints or failure modes.

Does not belong here:
- general computer architecture material with no direct CUDA learning role; that belongs in `architecture/`,
- or transformer/operator material whose primary question is not CUDA execution behavior.

Current notes:
- [00. Heterogeneous Computing](00-heterogeneous-computing.md)
- [01. CUDA Program Structure](01-cuda-program-structure.md)
- [02. Multidimensional Grids and Data Mapping](02-multidimensional-grids-and-data-mapping.md)
- [03. Compute Architecture and Scheduling](03-compute-architecture-and-scheduling.md)
- [04. Memory Types, Tiling, and Occupancy](04-memory-types-tiling-and-occupancy.md)
- [05. Memory Coalescing and Latency Hiding](05-memory-coalescing-and-latency-hiding.md)
- [06. Convolution](06-convolution.md)
- [07. Stencil Sweeps](07-stencil-sweeps.md)
- [08. Parallel Histograms](08-parallel-histograms.md)

---

CUDA Execution and Optimization:
Heterogeneous Framing -> Host/Device Split -> Thread Mapping -> Scheduling -> Memory Hierarchy -> Memory Traffic -> Pattern-Level Kernels -> Shared-Output Contention

This document describes the CUDA side of ML systems with deliberately mechanism-level emphasis.

The goal is not only to say:
- “CUDA uses threads,”
- “shared memory is faster,”
- or “tiling helps.”

The goal is to make the CUDA execution story explicit:
- how host code and device code divide responsibility,
- how threads are organized and mapped to data,
- how blocks and warps are actually scheduled,
- how different memory spaces behave,
- why memory traffic usually dominates performance,
- how these ideas show up in concrete parallel patterns such as convolution and stencil sweeps,
- and what changes when output ownership breaks and multiple threads contend on shared results.

The documents in this folder are deep dives.
This README is the parent document that ties them together into one continuous CUDA learning path.

---

## 0. Scope and Preconditions

This folder assumes a basic understanding of:

- C/C++-style arrays and indexing,
- parallel programming basics,
- matrix and tensor shapes,
- and the idea that GPUs trade control simplicity for massive throughput.

The emphasis here is not on CUDA API memorization for its own sake.
The emphasis is on:

- execution model,
- memory behavior,
- performance bottlenecks,
- and hardware-aware reasoning.

Throughout this folder, CUDA is treated as a systems interface to GPU execution.

That means the recurring questions are:

- what work is assigned to each thread?
- how do blocks and warps really execute?
- where does the data live?
- what limits performance: compute, memory, divergence, occupancy, or bandwidth?
- and which optimization changes the real bottleneck rather than just the source code?

---

## 1. The CUDA Story at a High Level

Before getting into the individual topics, it helps to state the CUDA story in one line:

```text
CPU host code prepares data and launches kernels
-> GPU threads are organized into grids and blocks
-> the hardware schedules blocks and warps
-> threads access a hierarchy of memory spaces
-> performance depends on how well thread mapping and memory behavior fit the hardware
```

The notes in this folder unpack each piece of that story.

The current conceptual sequence is:

```text
heterogeneous framing
-> program structure
-> multidimensional mapping
-> scheduling and occupancy
-> memory hierarchy and tiling
-> coalescing and latency hiding
-> convolution as a 2D pattern
-> stencil sweeps as a 3D pattern
-> parallel histograms as a shared-output contention pattern
```

This is not just an ordered list.
It is the conceptual progression by which CUDA starts to feel like a real execution model rather than a pile of APIs.

---

## 2. CUDA Begins with the Heterogeneous Performance Problem

Deep dive: [00. Heterogeneous Computing](00-heterogeneous-computing.md)

Before CUDA even becomes a programming model, there is a hardware and systems reason it needs to exist.

The old performance story was:

- wait for CPUs to get faster,
- write mostly sequential programs,
- and let hardware improvements carry much of the speedup burden.

That stopped scaling cleanly once power, heat, and single-core complexity became dominant constraints.

Modern high-performance systems therefore became heterogeneous:

- CPUs remain strong for control-heavy and orchestration-heavy work,
- GPUs provide throughput for massive numbers of structured parallel operations,
- and performance depends on moving the right parts of the application onto the right processor while controlling memory movement costs.

This framing matters because CUDA is easier to learn when it is seen as the programmer-facing interface to that heterogeneous division of labor, not as an arbitrary syntax layer.

---

## 3. CUDA Begins with the Host/Device Split

Deep dive: [01. CUDA Program Structure](01-cuda-program-structure.md)

The first thing CUDA forces you to understand is that the program is split into two sides:

- host code on the CPU
- device kernels on the GPU

Host code is responsible for:

- allocating device memory,
- moving data between host and device,
- launching kernels,
- and orchestrating execution.

Device code is responsible for:

- doing the massively parallel work on that data.

This split matters because a lot of CUDA confusion comes from not being clear about:

- where code is running,
- where the data currently lives,
- and which side is responsible for which step.

The program-structure note establishes the minimal CUDA mental model:

- host stub
- memory transfer
- kernel launch
- per-thread execution
- output retrieval

Everything else in the folder builds on that.

---

## 4. Mapping Threads to Data Is the First Real Skill

Deep dive: [02. Multidimensional Grids and Data Mapping](02-multidimensional-grids-and-data-mapping.md)

Once the host/device split is clear, the next question is:

```text
which output element should each thread compute?
```

That is the central mapping problem.

CUDA’s grid/block/thread model is not useful by itself unless you can map it cleanly onto:

- vectors,
- images,
- matrices,
- and later higher-dimensional tensors or grids.

This note covers:

- multidimensional grids and blocks,
- flattening multidimensional arrays,
- row-major indexing,
- image kernels,
- and matrix multiplication mapping.

This note matters because almost every CUDA kernel starts with:

- map threads to logical output coordinates
- convert those coordinates into linear memory addresses

If that mapping is wrong or awkward, the entire kernel becomes fragile or slow.

---

## 5. The Hardware Does Not Execute Blocks Abstractly

Deep dive: [03. Compute Architecture and Scheduling](03-compute-architecture-and-scheduling.md)

After thread mapping, the next major step is to stop thinking of the CUDA grid as a pure abstraction.

The hardware actually executes:

- blocks on SMs,
- warps inside those blocks,
- SIMD-style instruction streams across warp lanes.

That means performance now depends on things such as:

- block scheduling,
- warp formation,
- control divergence,
- latency hiding,
- and occupancy.

This note is the bridge between:

- the programmer-visible CUDA model

and:

- the hardware-visible execution behavior.

It is also where one of the most important CUDA habits is built:

```text
occupancy is useful,
but occupancy is not the same as performance
```

That distinction matters for the rest of the folder because many optimizations trade:

- register use,
- shared memory,
- block size,
- and occupancy

against each other.

---

## 6. CUDA Performance Is Usually a Memory Story First

Deep dive: [04. Memory Types, Tiling, and Occupancy](04-memory-types-tiling-and-occupancy.md)

At this point the folder moves from execution structure to memory structure.

This is where CUDA starts to look like ML systems work rather than just parallel syntax, because most real kernels are bottlenecked not by arithmetic availability but by:

- global memory latency,
- memory bandwidth,
- and avoidable data movement.

This note introduces the key CUDA memory spaces:

- global memory
- constant memory
- local memory
- registers
- shared memory

and explains why tiling matters so much.

The important shift here is:

```text
performance is often determined not by how much math exists,
but by how many times data has to travel through the expensive parts of the hierarchy
```

That is the beginning of serious CUDA optimization reasoning.

---

## 7. Traffic Shape Matters as Much as Traffic Volume

Deep dive: [05. Memory Coalescing and Latency Hiding](05-memory-coalescing-and-latency-hiding.md)

Once memory hierarchy is on the table, the next question becomes:

```text
how do accesses interact with DRAM and the GPU memory system?
```

This note explains:

- why DRAM is slow,
- why bursts matter,
- why coalesced accesses matter,
- how channels and banks provide memory-level parallelism,
- and why thread coarsening can sometimes reduce the price of over-parallelization.

This is where CUDA performance becomes visibly hardware-shaped.

The note’s key lessons include:

- consecutive warp accesses are much cheaper than scattered accesses
- occupancy helps hide memory latency, not just core latency
- thread coarsening is helpful only when fine-grained parallelism creates real overhead
- and optimizations should target the actual bottleneck rather than being applied blindly

This note functions as the main “performance mindset” document in the folder.

---

## 8. Convolution Is the First Full Pattern Case

Deep dive: [06. Convolution](06-convolution.md)

Convolution is where the previous ideas become a complete pattern rather than isolated rules.

It combines:

- natural output-side parallelism,
- heavy local input reuse,
- boundary conditions,
- constant memory for the filter,
- shared-memory tiling for interior reuse,
- and cache-aware handling of halo cells.

This makes convolution the first strong example of:

```text
map each kind of data reuse to the right memory mechanism
```

In convolution:

- the small read-only filter belongs in constant memory,
- interior tile data belongs in shared memory,
- and halo handling must balance complexity against cache behavior.

This note matters because it turns CUDA performance ideas into a concrete operator-level design.

---

## 9. Stencil Sweeps Show Where the Convolution Analogy Breaks

Deep dive: [07. Stencil Sweeps](07-stencil-sweeps.md)

Stencil sweeps resemble convolution at first:

- local neighborhoods,
- overlapping input reuse,
- and halo handling.

But they differ in ways that matter deeply for CUDA optimization:

- they are often 3D,
- their neighborhoods are sparse rather than dense,
- precision demands are often stricter,
- and shared-memory tiling alone gives less benefit than in 2D convolution.

That is why stencil optimization motivates:

- thread coarsening in the `z` direction,
- and register tiling for thread-private stencil data.

This note is important because it teaches a more advanced CUDA lesson:

```text
the best mapping is not always one thread per output element in every dimension;
sometimes a 2D thread plane should slide through the third dimension
```

This is one of the first places where CUDA optimization starts to look like genuine hardware-aware redesign rather than direct parallelization.

---

## 10. Histograms Show What Happens When Owner-Computes Fails

Deep dive: [08. Parallel Histograms](08-parallel-histograms.md)

The earlier pattern notes in this folder all fit the owner-computes rule reasonably well:

- each thread owns a natural output element or tile,
- and writes can be structured to avoid interference.

Histograms are different.

They introduce a new kind of pattern:

- output location is data-dependent,
- many threads may update the same output bin,
- and correctness now depends on synchronization rather than just mapping.

This is why histograms are a useful next step after convolution and stencil sweeps.
They introduce:

- read-modify-write race conditions,
- atomic operations,
- contention as a first-class performance bottleneck,
- block-level privatization,
- shared-memory local copies,
- coarsening to reduce merge overhead,
- and aggregation to reduce repeated updates.

So this note is the first explicit shared-output pattern in the folder.

---

## 11. The Main Distinctions This Folder Tries to Keep Sharp

Several distinctions matter across these notes.

### 11.1 CUDA abstraction vs hardware execution

- the programmer launches grids and blocks
- the hardware actually schedules blocks, warps, and memory transactions

### 11.2 Shared memory vs cache

- shared memory is programmer-managed and explicit
- cache is hardware-managed and implicit

### 11.3 Occupancy vs performance

- occupancy helps hide latency
- but maximizing occupancy is not the same as maximizing performance

### 11.4 Arithmetic intensity vs real speed

- more arithmetic per byte usually helps
- but only if memory traffic, coalescing, and on-chip resource usage are also favorable

### 11.5 Pattern analogy vs pattern identity

- stencil sweeps resemble convolution
- but their reuse structure and dimensionality lead to different optimization decisions

### 11.6 Owner-computes vs shared-output contention

- convolution and stencil sweeps mainly teach how to optimize independently owned outputs
- histograms teach what happens when many threads must coordinate on shared outputs

These distinctions are what turn CUDA from memorized terminology into usable engineering judgment.

---

## 12. Why This Folder Matters for ML Systems

This folder matters because many ML systems bottlenecks eventually become CUDA questions when workloads hit GPUs.

These notes teach how to reason about:

- mapping tensor work onto thread hierarchies,
- reducing memory traffic,
- using shared memory and registers effectively,
- handling contention on shared outputs,
- understanding when performance is bandwidth-bound,
- and interpreting operator-level optimization through the hardware execution model.

This is directly relevant for:

- training kernels,
- inference operators,
- custom CUDA code,
- performance debugging,
- and understanding what higher-level systems like PyTorch or Triton are really asking the hardware to do.

More broadly, the folder tries to build one durable CUDA habit:

```text
performance should be explained by execution structure and memory behavior,
not guessed from source code shape alone
```

That is the real CUDA mindset.

---

## 13. File Sequence and Future Expansion

The current deep-dive notes are:

- [01. CUDA Program Structure](01-cuda-program-structure.md)
- [02. Multidimensional Grids and Data Mapping](02-multidimensional-grids-and-data-mapping.md)
- [03. Compute Architecture and Scheduling](03-compute-architecture-and-scheduling.md)
- [04. Memory Types, Tiling, and Occupancy](04-memory-types-tiling-and-occupancy.md)
- [05. Memory Coalescing and Latency Hiding](05-memory-coalescing-and-latency-hiding.md)
- [06. Convolution](06-convolution.md)
- [07. Stencil Sweeps](07-stencil-sweeps.md)
- [08. Parallel Histograms](08-parallel-histograms.md)

This folder is not considered complete.
New documents may be added later between or beneath the current topics.

Likely future additions include:

- reduction and scan patterns
- sorting and merge patterns
- more CUDA-specific profiling guidance
- synchronization and communication patterns
- and more operator-level case studies

So the current order should be read as:

- a meaningful progression,
- but not a frozen final table of contents.

---

## 14. After This Folder You Should Understand

After finishing this folder, you should be able to explain:

- how CUDA divides work between host orchestration and device execution
- how to map threads to vectors, images, matrices, and grid-based outputs
- how warps, divergence, occupancy, and scheduling affect real execution
- how CUDA memory spaces differ and why shared memory and registers matter
- why coalescing, latency hiding, and tiling are central to performance
- why convolution and stencil sweeps are similar in shape but different in optimization payoff
- why histograms introduce a different pattern built around race conditions and contention management
- and how to interpret a CUDA kernel in terms of its actual bottleneck rather than just its source syntax

If this folder works well, CUDA should stop feeling like:

- API vocabulary

and start feeling like:

- a concrete model of GPU execution and memory behavior.
