# Performance Basics: Response Time, Throughput, CPI, and Clock Rate

This note covers the basic performance model used in introductory computer architecture.

The goal is to make the causal structure of performance explicit:
- what exactly is being measured,
- how time and performance are related,
- how CPU time decomposes,
- and why clock rate alone is not enough to understand speed.

This material is elementary, but it is one of the most reusable parts of architecture.
The same reasoning pattern reappears later in systems work and ML systems performance analysis.

## Response Time and Throughput

These are different performance measures.

### Response time

Response time is:

```text
how long one task takes
```

Examples:
- one program run finishes in 2 seconds
- one request returns in 150 ms
- one kernel launch completes in a certain latency

So response time is about **latency** for an individual unit of work.

### Throughput

Throughput is:

```text
how much total work is completed per unit time
```

Examples:
- transactions per second
- tasks per hour
- requests per second
- images processed per second

So throughput is about **rate** of completed work.

### Why they are different

A design change may affect these differently.

For example:
- replacing the processor with a faster one may reduce the response time of one job
- adding more processors may greatly improve throughput
- but one single task may not become proportionally faster

This distinction matters because architecture discussions often switch between:
- latency of one task,
- and total system work rate.

For this note, the main focus is response time.

## Relative Performance

Performance is defined inversely from execution time:

```text
Performance = 1 / Execution Time
```

This means:
- lower execution time implies higher performance
- higher execution time implies lower performance

### "X is n times faster than Y"

This means:

```text
Performance_X / Performance_Y = n
```

Since performance is inverse time, that is equivalent to:

```text
ExecutionTime_Y / ExecutionTime_X = n
```

### Example

Suppose a program takes:
- 10 s on machine A
- 15 s on machine B

Then:

```text
ExecutionTime_B / ExecutionTime_A = 15 / 10 = 1.5
```

So A is:

```text
1.5 times faster than B
```

The important point is that "times faster" is a ratio, not a subtraction.

## Measuring Execution Time

Execution time can mean more than one thing, depending on what you include.

### Elapsed time

Elapsed time is total wall-clock time.

It includes:
- actual processing time
- I/O time
- operating-system overhead
- time waiting for shared resources
- idle periods caused by the rest of the system

So elapsed time is what the user or external observer usually experiences.

### CPU time

CPU time is only the time the CPU spends working on the given job.

It excludes:
- I/O waiting
- time spent on other jobs
- unrelated external delays

CPU time is often split into:
- user CPU time
- system CPU time

This distinction matters because different programs may be limited by different parts of the system:
- one may be CPU-bound
- another may be I/O-bound
- another may spend large time in system services

## CPU Clocking

Digital hardware operates according to a clock.

The clock provides a regular timing structure that governs when state changes occur.

### Clock cycle

A clock cycle is one tick of this timing structure.

### Clock period

Clock period is:

```text
time per cycle
```

Example:

```text
250 ps = 0.25 ns = 250 x 10^-12 s
```

### Clock frequency or clock rate

Clock rate is:

```text
cycles per second
```

Example:

```text
4.0 GHz = 4.0 x 10^9 cycles/second
```

These are inverses:

```text
Clock Rate = 1 / Clock Cycle Time
```

So:
- shorter cycle time means higher frequency
- higher frequency means more cycles per second

## CPU Time and Clock Cycles

CPU time can be written as:

```text
CPU Time = CPU Clock Cycles x Clock Cycle Time
```

Equivalently:

```text
CPU Time = CPU Clock Cycles / Clock Rate
```

This already gives two obvious ways to improve CPU time:
- reduce the number of cycles
- increase the clock rate

But those are not independent in practice.

A design that increases clock rate may also require:
- deeper pipelines,
- more stages,
- more overhead,
- or more total cycles for some tasks.

So architecture often involves a tradeoff between:
- cycle count
- and clock speed

## Decomposing CPU Clock Cycles

The total number of CPU clock cycles for a program can be written as:

```text
CPU Clock Cycles = Instruction Count x CPI
```

where:
- `Instruction Count` is the number of instructions executed
- `CPI` means **cycles per instruction**

Substituting this into the earlier equation gives the central performance formula:

```text
CPU Time = Instruction Count x CPI x Clock Cycle Time
```

or equivalently:

```text
CPU Time = (Instruction Count x CPI) / Clock Rate
```

This is one of the most important equations in introductory architecture.

## What Determines Each Term?

### Instruction Count

Instruction count is influenced by:
- the algorithm
- the programming language
- the compiler
- the instruction set architecture

Different source programs or different compilations of the same source can produce different instruction counts.

### CPI

CPI is influenced mainly by:
- processor design
- datapath structure
- pipeline behavior
- cache behavior
- stalls
- and the mix of instruction types in the program

So CPI is partly a hardware property and partly a workload-dependent property.

### Clock cycle time or clock rate

This is determined by the implementation technology and microarchitectural design.

Examples of factors include:
- logic depth
- pipelining choices
- circuit timing
- frequency targets

So performance depends on both software-generated work and hardware cost per unit of work.

## CPU Time Example: Trading Off Clock Rate and Cycle Count

Suppose computer A has:
- clock rate = 2 GHz
- CPU time = 10 s

Then total cycles on A are:

```text
CPU Clock Cycles_A = 10 x 2 x 10^9 = 20 x 10^9 cycles
```

Now suppose computer B is designed to achieve:

```text
CPU Time_B = 6 s
```

but doing so requires:

```text
1.2 x as many clock cycles as A
```

Then:

```text
CPU Clock Cycles_B = 1.2 x 20 x 10^9 = 24 x 10^9 cycles
```

To finish in 6 seconds, B must have clock rate:

```text
Clock Rate_B = 24 x 10^9 / 6 = 4 x 10^9 Hz = 4 GHz
```

So B needs a 4 GHz clock.

This example shows why performance reasoning must combine:
- total cycles
- and clock rate

A faster clock may compensate for needing more cycles.

## Instruction Count and CPI

Another useful form of the CPU time equation is:

```text
CPU Time = Instruction Count x CPI x Clock Cycle Time
```

This makes a few architectural lessons very clear:

- fewer instructions usually helps, but not always enough by itself
- lower CPI usually helps, but not always enough by itself
- shorter cycle time usually helps, but not always enough by itself

Real performance depends on their combined product.

## CPI Example

Suppose computers A and B execute the same ISA and the same program, so the instruction count is `I`.

### Computer A

- cycle time = 250 ps
- CPI = 2.0

Then:

```text
CPU Time_A = I x 2.0 x 250 ps = I x 500 ps
```

### Computer B

- cycle time = 500 ps
- CPI = 1.2

Then:

```text
CPU Time_B = I x 1.2 x 500 ps = I x 600 ps
```

So A is faster, even though its CPI is worse.

Why?
Because its cycle time is much smaller.

Relative speed:

```text
CPUTime_B / CPUTime_A = 600 / 500 = 1.2
```

So A is:

```text
1.2 times faster than B
```

This is a very important architecture lesson:

```text
lower CPI does not automatically mean better performance
```

Clock rate and CPI must be considered together.

## CPI in More Detail

Not all instructions take the same number of cycles.

So average CPI is a weighted average over instruction classes.

If instruction class `i` has:
- `InstructionCount_i`
- `CPI_i`

then total clock cycles are:

```text
Clock Cycles = sum_i (InstructionCount_i x CPI_i)
```

And average CPI is:

```text
Average CPI = Clock Cycles / Total Instruction Count
```

So average CPI depends on both:
- hardware cost of each class
- the instruction mix of the program

## Weighted CPI Example

Suppose there are three instruction classes:

- class A with CPI = 1
- class B with CPI = 2
- class C with CPI = 3

### Sequence 1

Instruction counts:
- A: 2
- B: 1
- C: 2

Total instruction count:

```text
5
```

Total cycles:

```text
2x1 + 1x2 + 2x3 = 10
```

Average CPI:

```text
10 / 5 = 2.0
```

### Sequence 2

Instruction counts:
- A: 4
- B: 1
- C: 1

Total instruction count:

```text
6
```

Total cycles:

```text
4x1 + 1x2 + 1x3 = 9
```

Average CPI:

```text
9 / 6 = 1.5
```

This is a very important example:

Sequence 2 has **more instructions**, but still uses **fewer total cycles**.

So:

```text
more instructions != automatically slower execution
```

Instruction type matters.

## Instructions Per Second (IPS)

One simple throughput-style machine metric is:

```text
IPS = instructions per second
```

Using clock rate and CPI:

```text
IPS = Clock Rate x (1 / CPI) = Clock Rate / CPI
```

### Example

Suppose:

- P1: 3 GHz, CPI = 1.5
- P2: 2.5 GHz, CPI = 1.0
- P3: 4.0 GHz, CPI = 2.2

Then:

#### P1

```text
IPS = 3 x 10^9 / 1.5 = 2 x 10^9
```

#### P2

```text
IPS = 2.5 x 10^9 / 1.0 = 2.5 x 10^9
```

#### P3

```text
IPS = 4.0 x 10^9 / 2.2 ≈ 1.8 x 10^9
```

So P2 has the highest instructions-per-second rate among the three.

Again, this shows:

```text
highest clock rate != automatically highest performance
```

## Cycle Count Over Fixed Time

If a processor runs for 10 seconds, total cycles are:

```text
cycles = seconds x clock rate
```

Using the same three processors:

### P1

```text
10 x 3 x 10^9 = 30 x 10^9 cycles
```

### P2

```text
10 x 2.5 x 10^9 = 25 x 10^9 cycles
```

### P3

```text
10 x 4 x 10^9 = 40 x 10^9 cycles
```

This calculation is simple, but useful because it emphasizes that clock rate directly determines how many cycle opportunities the processor has per second.

## Performance Summary

The full architecture-level performance picture is:

```text
CPU Time = Instruction Count x CPI x Clock Cycle Time
```

This can also be read as:

```text
performance depends on work amount, work cost, and hardware speed
```

More specifically:

- algorithm affects instruction count, and sometimes CPI
- programming language affects instruction count and generated structure
- compiler affects instruction count and instruction mix
- ISA affects instruction count, CPI, and implementation implications
- processor design affects CPI and clock rate
- memory system affects stalls and effective CPI

So real performance is an interaction across layers, not a single isolated metric.

## Why This Matters for ML Systems

This architecture model transfers directly to ML systems thinking.

### Instruction count analogue

In ML workloads, the analogue of instruction count is often:
- total operation count,
- total memory accesses,
- total kernel launches,
- total sequence length or batch-driven work volume.

### CPI analogue

The analogue of CPI is the average execution cost per unit of work.

Examples:
- memory stalls
- poor occupancy
- bad locality
- synchronization overhead
- kernel inefficiency

### Clock-rate analogue

The analogue of raw hardware speed is:
- GPU frequency
- Tensor Core throughput
- HBM bandwidth
- or overall device generation capability

So even in ML systems, the same deep lesson holds:

```text
performance is determined by both how much work exists and how efficiently the machine executes each unit of that work
```

## Deepest Summary

This section is the foundation of quantitative performance reasoning in architecture.

It teaches:
- latency versus throughput,
- inverse relationship between performance and execution time,
- elapsed time versus CPU time,
- clock period versus clock rate,
- and the central decomposition:

```text
CPU Time = Instruction Count x CPI x Clock Cycle Time
```

If this equation is internalized, later topics such as:
- pipelining,
- caching,
- superscalar execution,
- accelerator performance,
- and ML systems bottleneck analysis

become much easier to reason about correctly.
