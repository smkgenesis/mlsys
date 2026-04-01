# Computer Architecture Overview and the Seven Great Ideas

This note is an introductory map of computer architecture.

The goal is not to dive immediately into one narrow mechanism.
The goal is to understand the big structure:
- what kinds of ideas organize computer design,
- where a program sits in the full stack,
- what the main machine components are,
- and why abstraction is necessary before deeper low-level study becomes manageable.

It also covers the textbook-style "Seven Great Ideas" because those ideas reappear again and again in later topics such as:
- pipelining,
- caching,
- parallel execution,
- accelerators,
- and ML systems performance engineering.

## Progress in Computer Technology

Progress in computer technology does not merely make old programs run faster.
It often makes entirely new applications feasible.

Examples include:
- computers in automobiles,
- mobile phones,
- genome-scale scientific workloads,
- the World Wide Web,
- search engines,
- and modern cloud services.

In older narratives, performance growth was often associated mainly with general-purpose processors getting faster.
In modern systems, progress is increasingly reinforced by **domain-specific accelerators**.

That means new application domains become practical not only because generic computing improves, but also because specialized hardware is designed for recurring workload patterns.

This is directly relevant to modern ML systems, where:
- GPUs,
- AI accelerators,
- video engines,
- and network offload hardware

all play important roles.

## Classes of Computers

Computer architecture is not about one single machine type.
Different classes of computers exist because different workloads and constraints exist.

### Personal computers

Personal computers are general-purpose systems intended to run many kinds of software.

Their design is usually shaped by a cost/performance tradeoff:
- they must be powerful enough for common workloads,
- but affordable enough for broad use.

### Server computers

Servers are built to provide services over a network.

Key goals include:
- capacity,
- throughput,
- reliability,
- and ability to handle many clients or tasks.

Servers range from:
- small individual machines,
- to racks,
- to large installations.

### Supercomputers

Supercomputers are an extreme high-end form of server-class computing.

They are used for:
- scientific simulation,
- engineering computation,
- and other workloads needing very high capability.

They represent a small fraction of the total market, but they push the limits of large-scale performance.

### Embedded computers

Embedded computers are hidden inside larger systems rather than exposed as standalone machines.

Examples:
- vehicles,
- appliances,
- cameras,
- printers,
- industrial devices.

Their design is often constrained very tightly by:
- power,
- cost,
- physical size,
- and workload specialization.

## The Post-PC Era

The idea of computing being centered around one desktop machine is no longer sufficient.

Two concepts become central:

### Personal Mobile Devices (PMDs)

Examples:
- smartphones,
- tablets,
- wearable devices.

Their defining characteristics include:
- battery operation,
- internet connectivity,
- relatively low device cost,
- and tight power and thermal constraints.

A PMD is powerful, but it cannot behave like a warehouse-scale server because it must live inside a battery-limited physical envelope.

### Cloud computing and warehouse-scale computers (WSCs)

Cloud computing shifts large parts of computation and storage to remote data centers.

A warehouse-scale computer is not just one large machine.
It is a data-center-scale computing environment designed to provide services at massive scale.

This enables:
- software as a service,
- remote storage,
- large-scale serving,
- and online systems where part of the logic runs locally and part runs in the cloud.

This split is now normal:

```text
device-side work + cloud-side work
```

That same division appears constantly in ML systems:
- local UI or preprocessing on a device,
- heavy inference or storage in the cloud.

## The Seven Great Ideas in Computer Architecture

These are not isolated slogans.
They are recurring design principles that keep showing up in both traditional architecture and ML systems.

### 1. Use abstraction to simplify design

Computer systems are too complex to reason about all at once.

So we use layers that hide lower-level detail:
- applications,
- compilers,
- operating systems,
- ISA,
- microarchitecture,
- circuits.

Abstraction does not remove the lower layers.
It gives each layer a stable interface so humans can work on one layer without re-deriving everything below it every time.

### 2. Make the common case fast

Not every path through the system should be optimized equally.

Architectures usually focus on frequent operations such as:
- common instruction forms,
- common memory-access patterns,
- common branch behavior,
- common constant handling.

This idea appears in many places:
- immediate operands,
- cache design,
- branch prediction,
- optimized kernels for common tensor shapes.

### 3. Performance via parallelism

If work can be done simultaneously, performance can improve.

Parallelism appears at many scales:
- instruction-level,
- vector/SIMD,
- multicore,
- GPU thread-level,
- distributed cluster-level.

Parallelism is one of the most important modern performance mechanisms.

### 4. Performance via pipelining

Pipelining means breaking work into stages and overlapping multiple items in flight.

This is like an assembly line:
- one instruction is decoding,
- another is executing,
- another is writing back.

The key idea is not reducing the latency of one instruction to zero.
It is increasing overall throughput by overlapping stages.

### 5. Performance via prediction

Modern machines often guess what is likely to happen next so they can begin work early.

Examples:
- branch prediction,
- speculative execution,
- memory prefetching.

Prediction is valuable because waiting for certainty can stall the machine.

### 6. Hierarchy of memories

No single memory structure gives:
- infinite size,
- infinite speed,
- zero cost,
- and low power

all at once.

So systems use a hierarchy:
- registers,
- caches,
- main memory,
- secondary storage.

Small nearby storage is fast but limited.
Large distant storage is slower but cheaper per bit.

This is one of the central architecture ideas and later becomes crucial for ML systems.

### 7. Dependability via redundancy

Reliable systems are often built by adding extra checking or extra copies.

Examples:
- ECC memory,
- RAID storage,
- replicated servers,
- redundant hardware modules.

Redundancy can improve fault tolerance and reliability even though it increases cost.

## Below Your Program

A high-level program does not run directly on raw hardware.
There are layers below it.

### Application software

This is the user-visible program written in a high-level language.

### System software

This includes:

#### Compiler

Translates high-level language code into machine instructions.

This is one of the most important bridges in architecture:

```text
high-level intent -> machine-executable form
```

#### Operating system

Provides essential services such as:
- input/output handling,
- memory management,
- storage management,
- task scheduling,
- resource sharing.

So the OS is not "just another program."
It is the system-level manager that coordinates execution and access to hardware resources.

### Hardware

This includes:
- processor,
- memory,
- I/O controllers,
- and connected devices.

## Levels of Program Code

The same computation can be viewed at multiple representation levels.

### High-level language

This is closest to the problem domain.

Its advantages are:
- productivity,
- readability,
- portability,
- easier abstraction.

### Assembly language

Assembly is a textual representation of machine instructions.

It is much closer to the hardware than a high-level language, but still readable by humans.

### Hardware representation

At the lowest visible ISA level, programs are stored and executed as bits.

So one program may exist as:
- source code,
- assembly,
- machine code,
- and actual bit patterns in memory.

This layered representation is exactly why abstraction matters.

## Components of a Computer

Across desktop systems, servers, and embedded systems, the same broad components keep appearing.

### Processor

Executes instructions and coordinates computation.

### Memory

Stores currently active instructions and data.

### Input/output

Connects the system to external devices and environments.

Examples include:
- user-interface devices such as display, keyboard, mouse, touchscreen,
- storage devices such as disks and flash,
- network adapters for communication with other machines.

Different machines may package these differently, but the categories remain stable.

## Touchscreen and the Post-PC Device

In post-PC systems, the touchscreen often replaces older input combinations such as:
- keyboard,
- mouse,
- pointer devices.

The point is not the electrical details of resistive versus capacitive touch at this stage.
The key architecture lesson is that I/O devices evolve, but they still feed data into the same broader system structure.

Capacitive touchscreens are especially important in modern mobile devices because they support multiple simultaneous touches and fit the interaction style of phones and tablets.

## Through the Looking Glass: Displays and Frame Buffers

An LCD screen displays an image through many picture elements called pixels.

The display image is driven from memory commonly called a **frame buffer**.

So there is a direct architecture concept here:

```text
memory contents -> visible image on screen
```

This is another example of hardware state having a concrete external effect.

## Opening the Box: Inside the Processor

The processor is not one indivisible block.
At a high level it can be divided into:
- datapath,
- control,
- cache.

### Datapath

The datapath performs operations on data.

Examples:
- add,
- subtract,
- shift,
- compare,
- move values between internal structures.

### Control

The control logic determines:
- which datapath actions happen,
- in what sequence,
- and how instruction semantics are enforced.

So:
- datapath does the work,
- control coordinates the work.

### Cache

Cache is small, fast SRAM close to the processor.

It exists because immediate access to frequently used data and instructions is extremely valuable.

This is the first concrete manifestation of the memory hierarchy principle.

## Abstractions: ISA, ABI, and Implementation

This is one of the most important conceptual distinctions in the chapter.

### ISA: Instruction Set Architecture

The ISA is the hardware/software interface.

It defines what software can rely on architecturally:
- instructions,
- registers,
- memory addressing behavior,
- visible machine-level semantics.

### ABI: Application Binary Interface

The ABI is broader than the ISA.

It includes the ISA plus binary-level software conventions such as:
- calling conventions,
- register preservation rules,
- binary object interfaces,
- system-level assumptions about executable code interaction.

### Implementation

Implementation means the actual underlying mechanism that realizes the interface.

Two machines may have the same ISA but different implementations:
- different pipelines,
- different cache structures,
- different branch predictors,
- different clock rates,
- different internal organizations.

This distinction is essential:

```text
same interface != same internal design
```

## A Safe Place for Data: Volatile and Non-Volatile Storage

Not all storage behaves the same.

### Volatile main memory

Main memory loses its contents when power is removed.

It is active working storage, not permanent retention.

### Non-volatile secondary memory

Secondary storage keeps data even when power is removed.

Examples:
- magnetic disk,
- flash memory,
- optical storage.

So another hierarchy appears:
- faster temporary working memory,
- slower persistent storage.

## Networks

Modern computers are not isolated.

Networks enable:
- communication,
- sharing of resources,
- nonlocal access,
- distributed computation.

Examples:
- local area network (LAN),
- wide area network (WAN),
- wireless links such as WiFi and Bluetooth.

This matters because many important applications are now inherently networked.

For ML systems, networks become essential for:
- distributed training,
- serving,
- storage access,
- cluster coordination,
- and cloud inference.

## Understanding Performance

Performance is not determined by a single layer.
It is an end-to-end property that depends on multiple parts of the system stack.

### Algorithm

The algorithm determines how much work exists at all.

It controls:
- number of operations,
- asymptotic complexity,
- structure of the workload.

### Programming language, compiler, and architecture

These determine how high-level operations map to machine instructions.

They affect:
- how many instructions are executed,
- how efficiently code is optimized,
- how well the program matches the machine model.

### Processor and memory system

These determine how fast those instructions can actually run.

This includes:
- datapath speed,
- pipelining,
- cache behavior,
- latency,
- bandwidth.

### I/O system and operating system

These determine how efficiently external interactions occur.

This includes:
- disk access,
- networking,
- system-call overhead,
- scheduling behavior,
- resource contention.

So performance is a full-stack issue, not merely a CPU issue.

## Technology Trends

The physical technology used to build computers has changed dramatically over time:
- vacuum tubes,
- transistors,
- integrated circuits,
- VLSI,
- ultra-large-scale integration.

The important architectural lesson is not memorizing each historical milestone.
The key point is:

```text
advances in implementation technology change the practical limits of cost, speed, storage, and scale
```

That is why:
- new application domains become practical,
- system classes evolve,
- and architecture keeps adapting.

## Why This Matters for ML Systems

This chapter-level overview connects directly to ML systems thinking.

Examples:
- abstraction:
  model code -> framework -> kernel -> hardware
- make the common case fast:
  optimized kernels for frequent tensor shapes
- parallelism:
  GPUs, distributed training, batched serving
- pipelining:
  instruction pipelines, model pipelines, system pipelines
- prediction:
  prefetching, speculative behavior, scheduler heuristics
- hierarchy of memories:
  registers, shared memory, cache, HBM, host RAM, storage
- dependability via redundancy:
  replicated services, checkpointing, fault tolerance

So the introductory computer-architecture worldview is not separate from ML systems.
It is a simpler, more general version of the same reasoning framework.

## The Deepest Summary

Computer architecture is about how computation is represented, organized, and executed across layers.

It studies:
- how high-level programs become machine behavior,
- how machine components cooperate,
- how abstraction hides complexity,
- and how design principles shape performance, cost, and reliability.

The chapter overview is broad on purpose.
It gives the conceptual map needed before diving into:
- instruction sets,
- datapaths,
- pipelines,
- caches,
- memory systems,
- and accelerators.
