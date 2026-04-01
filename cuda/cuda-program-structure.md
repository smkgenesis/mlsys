# CUDA Program Structure: Data Parallelism, Kernels, Threading, Memory Transfer, and Compilation

## What

This document integrates foundational CUDA ideas centered on:

- data parallelism,
- host and device roles,
- vector addition as a minimal CUDA example,
- device global memory allocation and transfer,
- kernel functions and thread indexing,
- kernel launch configuration,
- and the CUDA compilation model.

The goal is not to preserve textbook sequence for its own sake. The goal is to build one coherent mental model of how a simple CUDA program works from beginning to end.

## Why It Matters

The first challenge in learning CUDA is not syntax. It is adopting the correct execution model.

A CUDA program is not simply a C program with a faster function call. It is a heterogeneous program in which:

- host code executes on the CPU,
- device code executes on the GPU,
- data may need to move between host and device memories,
- and a kernel launch creates many parallel threads that together replace a sequential loop.

This execution model is the conceptual base for all later CUDA and Triton understanding. Without it, later topics such as occupancy, memory hierarchy, kernel scheduling, and ML-kernel optimization become much harder to interpret.

## Data Parallelism

Data parallelism exists when the work on different parts of a dataset can be done independently and therefore in parallel.

The core pattern is:

```text
for each data element i:
    output[i] = f(input[i])
```

or more generally:

```text
output[i] = f(local data needed for i)
```

The important condition is independence. If one data element can be processed without waiting for another, the work can often be mapped well to massively parallel hardware.

### Color-to-grayscale example

For a color image with pixels:

```text
I[i] = (r, g, b)
```

the grayscale luminance output is:

```text
O[i] = 0.21r + 0.72g + 0.07b
```

Each output pixel depends only on its own input pixel, so each pixel conversion can be performed independently.

This is a canonical data-parallel structure.

### Why data parallelism matters

Task parallelism also exists in many applications, but data parallelism is usually the main source of scalability for massively parallel processors because datasets can be extremely large:

- millions of pixels,
- billions of tensor elements,
- large grids in scientific computing,
- many tokens, heads, and hidden units in ML systems.

## Host and Device Structure

CUDA C extends C so that one source file can contain both:

- host code,
- device code.

The host is the CPU side of the system.
The device is the GPU side.

The host code typically:

- allocates memory,
- prepares inputs,
- transfers data,
- launches kernels,
- and collects results.

The device code performs the parallel computation.

This gives CUDA programs a basic execution pattern:

```text
host code
-> launch kernel
-> device executes many threads in parallel
-> host code continues after kernel completion
```

## Vector Addition as the Minimal CUDA Example

Vector addition is the simplest useful CUDA example because it is directly data parallel.

Sequential host version:

```c
for (int i = 0; i < n; ++i) {
    C[i] = A[i] + B[i];
}
```

Parallel interpretation:

- one thread handles one index `i`
- thread `i` computes `C[i] = A[i] + B[i]`

### Concrete example

If:

```text
A = [1, 2, 3, 4]
B = [10, 20, 30, 40]
```

then:

```text
C = [11, 22, 33, 44]
```

In CUDA terms:

- thread 0 computes `C[0] = 1 + 10`
- thread 1 computes `C[1] = 2 + 20`
- thread 2 computes `C[2] = 3 + 30`
- thread 3 computes `C[3] = 4 + 40`

This direct mapping from loop iterations to threads is the first important CUDA mental model.

## Device Global Memory and Data Transfer

Current CUDA devices have their own DRAM, called device global memory.

This memory is separate from host memory.
Therefore data used by a kernel often must be copied from host memory to device memory before execution, and copied back afterward if the host needs the results.

### Core CUDA memory-management API calls

Device memory allocation:

```c
cudaMalloc((void**)&A_d, size);
```

Device memory free:

```c
cudaFree(A_d);
```

Host-device copy:

```c
cudaMemcpy(A_d, A_h, size, cudaMemcpyHostToDevice);
cudaMemcpy(C_h, C_d, size, cudaMemcpyDeviceToHost);
```

### Meaning of host and device pointer naming

It is useful to suffix host pointers with `_h` and device pointers with `_d`.

Example:

- `A_h` points to host memory
- `A_d` points to device global memory

This naming helps prevent one of the most important beginner mistakes: confusing host pointers and device pointers.

### Concrete vector-add transfer trace

Suppose:

```text
A_h = [1, 2, 3, 4]
B_h = [10, 20, 30, 40]
```

After allocation and host-to-device copies:

```text
A_d = [1, 2, 3, 4]
B_d = [10, 20, 30, 40]
C_d = uninitialized
```

After kernel execution:

```text
C_d = [11, 22, 33, 44]
```

After copying back:

```text
C_h = [11, 22, 33, 44]
```

### Important restriction

Device pointers should not be dereferenced in ordinary host code.

They are meaningful for:

- CUDA API calls,
- kernel arguments,
- and device-side execution.

Treating a device pointer like a normal host pointer can lead to runtime errors.

## Kernel Functions

A CUDA kernel is a function executed on the device by many threads in parallel.

Kernel declaration uses:

```c
__global__
```

Example:

```c
__global__
void vecAddKernel(float* A, float* B, float* C, int n) {
    int i = threadIdx.x + blockDim.x * blockIdx.x;
    if (i < n) {
        C[i] = A[i] + B[i];
    }
}
```

This code is executed by every thread in the grid.

Because all threads execute the same program but work on different data, CUDA follows the SPMD model:

```text
single program, multiple data
```

## Threading Model: Grid, Block, Thread

When the host launches a kernel, it launches a grid of threads.

CUDA organizes these threads hierarchically:

- a grid contains blocks,
- each block contains threads.

For a one-dimensional organization:

- `blockIdx.x` identifies the block within the grid,
- `threadIdx.x` identifies the thread within the block,
- `blockDim.x` gives the number of threads in each block.

### Global index calculation

Each thread computes its global work index with:

```c
i = threadIdx.x + blockDim.x * blockIdx.x;
```

This is the key mapping from CUDA’s hierarchy to the original loop index.

### Concrete example with block size 256

If:

```text
blockDim.x = 256
```

then:

- block 0 covers `i = 0..255`
- block 1 covers `i = 256..511`
- block 2 covers `i = 512..767`

So blocks together provide continuous coverage of the original loop iteration space.

### Why `if (i < n)` is needed

Vector lengths are not always exact multiples of block size.

Example:

- `n = 100`
- block size = `32`
- needed blocks = `ceil(100 / 32) = 4`
- launched threads = `128`

Threads with `i = 100..127` must do no useful work, so the kernel uses:

```c
if (i < n)
```

to disable extra threads safely.

## Kernel Launch Configuration

The host launches a kernel with execution configuration syntax:

```c
kernel<<<number_of_blocks, threads_per_block>>>(args);
```

For vector addition:

```c
vecAddKernel<<<ceil(n/256.0), 256>>>(A_d, B_d, C_d, n);
```

This means:

- each block has 256 threads,
- the number of blocks is chosen so that total threads cover all `n` elements.

### Concrete example: `n = 1000`

```text
ceil(1000 / 256.0) = 4
```

So CUDA launches:

- 4 blocks
- 256 threads per block
- 1024 total threads

Useful threads:
- `i = 0..999`

Inactive extra threads:
- `i = 1000..1023`

### Important scheduling rule

Blocks are independent and may be executed in any order.

The programmer must not assume:

- block 0 runs before block 1,
- or all blocks execute simultaneously,
- or a specific GPU executes the same number of blocks concurrently as another GPU.

This independence is what allows CUDA programs to scale across GPUs with different hardware sizes.

## The Full Host Stub Pattern

The host-side wrapper for vector addition follows this structure:

1. allocate device memory
2. copy inputs from host to device
3. launch the kernel
4. copy outputs from device to host
5. free device memory

Representative form:

```c
void vecAdd(float* A, float* B, float* C, int n) {
    float *A_d, *B_d, *C_d;
    int size = n * sizeof(float);

    cudaMalloc((void **)&A_d, size);
    cudaMalloc((void **)&B_d, size);
    cudaMalloc((void **)&C_d, size);

    cudaMemcpy(A_d, A, size, cudaMemcpyHostToDevice);
    cudaMemcpy(B_d, B, size, cudaMemcpyHostToDevice);

    vecAddKernel<<<ceil(n/256.0), 256>>>(A_d, B_d, C_d, n);

    cudaMemcpy(C, C_d, size, cudaMemcpyDeviceToHost);

    cudaFree(A_d);
    cudaFree(B_d);
    cudaFree(C_d);
}
```

This host function does not perform the arithmetic itself.
It serves as a stub that manages device execution.

## Compilation Model

CUDA programs contain language extensions not recognized by a normal C compiler.

Therefore CUDA programs are compiled by tools such as NVCC.

NVCC separates the source into:

- host code,
- device code.

### Host path

Host code is compiled by the normal host C/C++ compiler and runs as CPU code.

### Device path

Device code is compiled into PTX, an intermediate virtual representation for NVIDIA GPUs, and then further compiled for execution on a CUDA-capable device.

This matches the heterogeneous execution model:

- host code compiled for CPU execution,
- device code compiled for GPU execution.

## Performance Caution

The vector-add example is structurally useful but often performance-poor as a real GPU workload.

Why:

- the computation per element is tiny,
- host-device data transfers introduce overhead,
- memory allocation and deallocation also introduce overhead.

So the CUDA version of vector addition may be slower than the sequential CPU version.

This is not a failure of CUDA. It is a lesson:

```text
Parallel execution is worthwhile when the useful work is large enough
relative to transfer and launch overhead.
```

Real applications usually:

- do more computation per byte,
- keep data on the device across multiple kernel launches,
- and amortize transfer costs across larger computation pipelines.

## Tradeoffs

- CUDA gives explicit control over execution and memory movement, which is excellent for learning and optimization, but it exposes more systems details to the programmer.
- Launching many lightweight threads enables scalable data-parallel execution, but only when the workload actually contains enough independent work.
- The simple host-device transfer model is easy to understand, but it is often inefficient if used for every small operation in isolation.
- Fixed block sizes simplify examples, but real performance tuning requires choosing launch configurations based on hardware and workload characteristics.

## Common Mistakes

- Confusing host pointers and device pointers.
- Thinking the GPU automatically sees ordinary host arrays without explicit transfer.
- Forgetting that one kernel launch creates many threads, not one device execution instance.
- Assuming blocks execute in a fixed order.
- Forgetting the bounds check when data size is not an exact multiple of block size.
- Treating a CUDA kernel as just a normal function call with different syntax.
- Assuming a GPU version is always faster even when transfer and launch overhead dominate the actual computation.

## ML Systems Connection

These ideas map directly onto ML systems work.

- Data parallelism underlies tensor operations across tokens, activations, weights, heads, and batch items.
- Host/device separation appears in real inference systems where CPUs handle scheduling, tokenization, and orchestration while GPUs execute model kernels.
- Device memory management is central to practical systems such as LLM serving, where weights, KV cache, and intermediate activations must be placed and reused carefully.
- The idea that a grid of threads replaces a sequential loop is the direct precursor to understanding CUDA kernels, Triton programs, and tensor-operator execution.
- The warning about transfer overhead generalizes into a major ML systems lesson: end-to-end performance depends not only on parallel arithmetic, but also on memory movement and orchestration overhead.

Taken together, these ideas form the first complete CUDA mental model:

```text
identify data parallelism
-> move needed data to the device
-> launch a kernel as a grid of threads
-> let each thread handle one piece of data
-> collect results
-> reason about performance in terms of both computation and movement
```
