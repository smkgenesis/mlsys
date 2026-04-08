# 03. Compute Architecture and Scheduling

## Why This Matters

CUDA exposes a simple programming model of kernels, blocks, and threads, but kernel performance depends on how that model maps to GPU hardware. The key compute-side concepts are:

- blocks are assigned to streaming multiprocessors (SMs),
- warps are the real thread scheduling unit inside an SM,
- long-latency operations are hidden by switching to other ready warps,
- and achievable parallelism is limited by SM resources such as thread slots, block slots, registers, and shared memory.

This is the core execution model behind CUDA performance reasoning.

## GPU Structure at a High Level

A CUDA-capable GPU is organized as an array of **streaming multiprocessors (SMs)**. Each SM contains:

- multiple execution units, often called CUDA cores,
- control logic that schedules thread execution,
- and on-chip memory resources used during execution.

The GPU also includes large off-chip device memory, usually referred to in CUDA as **global memory**.

For performance reasoning, the most important distinction is:

- **SM-local resources** are limited and fast,
- **global memory** is large but high-latency.

## Block Scheduling

When a kernel launches a grid, CUDA assigns work to the GPU **one block at a time**.

Important rules:

- all threads in a block are assigned to the same SM,
- multiple blocks may reside on one SM at the same time,
- but only a limited number can fit because each block consumes SM resources,
- and blocks beyond that limit wait until earlier blocks finish.

This means a grid usually executes in waves rather than all at once.

The fact that blocks are assigned independently is one of CUDA's most important design choices. It allows the same kernel launch to scale across GPUs with different numbers of SMs and different execution capacity.

## Synchronization and Transparent Scalability

Threads within the same block can coordinate with:

```c
__syncthreads();
```

This is a **block-wide barrier**:

- a thread that reaches it waits,
- execution continues only after every thread in the block reaches the same barrier.

Correct usage rule:

- if a barrier is executed, all threads in the block must reach that same barrier point.

Incorrect conditional placement of `__syncthreads()` can lead to undefined behavior or deadlock.

CUDA restricts ordinary barrier synchronization to blocks rather than the whole grid. That restriction preserves **transparent scalability**:

- a small GPU may execute only a few blocks at once,
- a large GPU may execute many blocks at once,
- and the same application code still works correctly.

This is one of the reasons CUDA programs scale cleanly across very different hardware.

## Warps

Once a block is assigned to an SM, it is partitioned into **warps**.

A warp is the hardware thread scheduling unit. On current NVIDIA GPUs, a warp typically contains **32 threads**.

For a one-dimensional block:

- warp 0 contains threads `0..31`,
- warp 1 contains threads `32..63`,
- and so on.

If the block size is not a multiple of 32, the final warp is padded with inactive threads.

For two-dimensional and three-dimensional blocks, CUDA first linearizes thread coordinates in row-major order and then partitions that linear order into warps.

This matters because performance-sensitive behavior is often determined at **warp granularity**, not at whole-block granularity.

## SIMD-Style Execution

Within an SM, warps execute in a SIMD-like manner:

- one instruction is issued for the warp,
- all active threads in that warp apply it to their own data.

This design shares control hardware across many execution lanes, which is one of the main reasons GPUs can devote so much chip area to arithmetic throughput rather than latency-optimized control logic.

At the programming-model level, CUDA threads still appear independent. At the hardware level, however, their execution efficiency depends strongly on warp behavior.

## Control Divergence

**Control divergence** happens when threads in the same warp take different execution paths.

Example:

```c
if (cond) {
    A();
} else {
    B();
}
```

If some threads in a warp go down the `if` path while others go down the `else` path, the warp cannot execute both paths simultaneously. Instead, the hardware takes multiple passes:

- one pass with one subset of threads active,
- another pass with the other subset active,
- then reconverges afterward.

This preserves correctness, but it reduces throughput because:

- more instruction passes are required,
- and inactive threads still occupy warp lanes during each pass.

Boundary checks such as `if (i < n)` often cause divergence only in edge warps, so their total cost tends to shrink as the input size grows.

Practical rule:

- if a branch or loop condition depends on `threadIdx`, divergence is possible.

## Warp Scheduling and Latency Hiding

An SM usually has many more resident threads than it can execute simultaneously.

This is intentional.

When one warp stalls on a long-latency operation such as a global memory access, the SM scheduler can immediately issue instructions from another **ready** warp. This is called:

- **latency tolerance**, or
- **latency hiding**.

The scheduler does not need to perform an expensive CPU-style context switch. GPU hardware keeps the execution state of resident warps on chip, which makes warp switching effectively **zero-overhead scheduling**.

This is one of the main architectural reasons GPUs can sustain high throughput even when individual instructions have long latency.

## Resource Partitioning and Occupancy

Each SM has limited execution resources, including:

- thread slots,
- block slots,
- registers,
- and shared memory.

These resources are partitioned dynamically across resident blocks.

### Occupancy

**Occupancy** is the fraction of an SM's maximum warp or thread capacity that is currently filled by resident work.

You can think of it as:

```text
resident threads or warps / maximum supported threads or warps
```

Higher occupancy generally gives the scheduler more warps to choose from and therefore improves the GPU's ability to hide latency.

### Why Occupancy Falls Below 100%

Occupancy may be limited by several interacting constraints:

- block size may not pack cleanly into the SM's thread capacity,
- the block-slot limit may be reached before the thread-slot limit,
- register usage per thread may reduce how many total threads fit,
- shared memory usage per block may reduce how many blocks fit.

This means small code changes can cause large occupancy changes.

### Performance Cliffs

A kernel may sit just below a resource threshold and run at full occupancy, then fall sharply when a small increase in resource use crosses a limit.

Example pattern:

- adding a couple of automatic variables increases registers per thread,
- that reduces how many blocks fit on an SM,
- occupancy drops from 100% to 75% or 50%,
- and performance may fall suddenly.

This is a common CUDA tuning hazard.

### Occupancy Is Useful but Not the Same as Performance

Occupancy is an important diagnostic, but it is not the same as throughput.

A kernel with lower occupancy can still be faster if it:

- reduces memory traffic,
- avoids register spilling,
- improves locality,
- or performs more useful work per thread.

So occupancy is best treated as a resource and latency-hiding indicator rather than a universal optimization target.

## Querying Device Properties

CUDA exposes runtime APIs that let host code inspect available GPUs and their hardware limits.

### Count Available Devices

```c
int devCount;
cudaGetDeviceCount(&devCount);
```

### Query One Device

```c
cudaDeviceProp devProp;
cudaGetDeviceProperties(&devProp, deviceIndex);
```

Important queried properties include:

- `devProp.maxThreadsPerBlock`
- `devProp.multiProcessorCount`
- `devProp.clockRate`
- `devProp.maxThreadsDim[]`
- `devProp.maxGridSize[]`
- `devProp.regsPerBlock`
- `devProp.warpSize`

These properties help applications:

- choose among available devices,
- validate launch dimensions,
- estimate occupancy limits,
- and adapt to weaker or stronger hardware.

## The Full Chapter 4 Execution Model

The compute-side CUDA execution model can be summarized as:

1. A kernel launch creates a grid of blocks.
2. Blocks are assigned to SMs independently.
3. Each block is partitioned into warps.
4. Warps are the true scheduling unit inside the SM.
5. SIMD-style execution makes warp-level control flow important.
6. Divergent warps lose efficiency because different paths are executed in separate passes.
7. Many resident warps allow the SM to hide long-latency operations.
8. The number of resident warps depends on occupancy.
9. Occupancy is limited by interacting SM resource constraints.
10. The exact limits are device-specific and can be queried at runtime.

## Common Mistakes

- Treating occupancy as identical to performance.
- Assuming threads in a warp always progress in lockstep for correctness.
- Ignoring register usage when tuning block size.
- Overfitting a launch configuration to one GPU without checking device limits.
- Assuming block execution order or cross-block synchronization.

## Why This Matters for ML Systems

These ideas show up constantly in modern ML systems work:

- kernel launch tuning depends on block size, warp behavior, and occupancy,
- throughput bottlenecks often come from memory latency and limited latency hiding,
- branch-heavy or irregular kernels can suffer from divergence,
- and hardware-aware optimization depends on understanding how kernel resource usage maps to SM residency.

This chapter is the bridge from CUDA syntax to actual GPU performance engineering.
