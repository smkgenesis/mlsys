# transformers

Transformer Inference (Part 1): Request → Tokenization → GPU Transfer

This document describes the first segment of end-to-end inference with minimal abstraction. Each step specifies:

Input (origin)

Process (exact transformation)

Math (if applicable)

PyTorch view

Triton view

CUDA view

Hardware / memory (where data lives, moves, and is computed)

Output (destination for next step)


Scope of Part 1:

User string arrival

Tokenization (CPU)

Scheduling + KV block reservation (CPU/runtime)

Host → Device transfer (CPU → GPU HBM)



---

0. Preconditions (Causal Origins Before This Request)

These objects already exist before the request:

Tokenizer (rules + vocab): trained offline, loaded into CPU RAM.

Model weights (embedding, projections, MLP, LM head): trained offline, loaded into GPU HBM.

Inference engine runtime (scheduler, block manager): initialized on CPU; optional device-side metadata buffers exist on GPU.


No step below creates these; they are read-only inputs for this request.


---

1. Request Entry (CPU)

1.1 Raw String Reception

Input (origin)

UTF-8 string from client (network): e.g.


"Explain the Transformer architecture in detail."

Process

Network stack delivers bytes to server process.

Server constructs a request object with:

prompt (string)

generation params (max_new_tokens, temperature, top_p, etc.)

metadata (request id, model id)



Math

None (byte transport + object construction)


PyTorch view

Not applicable (outside model graph)


Triton view

Not applicable


CUDA view

Not applicable


Hardware / memory

NIC → kernel buffers → user-space buffers

Request object in CPU RAM


Output

request (CPU object) containing prompt: str and params

Next consumer: tokenizer



---

2. Tokenization (CPU)

2.1 Tokenizer State (Origin)

Input (origin)

request.prompt (from 1.1)

Tokenizer rules and vocabulary in CPU RAM (preloaded)


Process

Subword segmentation (e.g., BPE/SentencePiece)

Vocabulary lookup per piece → integer IDs


Math

Let tokens be (t_1, …, t_N) and vocab mapping vocab(·):

i_k = \mathrm{vocab}(t_k)

PyTorch view

input_ids = tokenizer.encode(prompt)

Triton view

Not used (CPU-side)


CUDA view

Not used (CPU-side)


Hardware / memory

Execution: CPU cores

Tokenizer tables: CPU RAM

Output array: CPU RAM


Output

input_ids: int32[int] of length N

Next consumer: scheduler / GPU transfer



---

3. Scheduling & KV Block Reservation (CPU + runtime)

3.1 Enqueue and Batch Decision

Input (origin)

input_ids from 2.1

generation params from 1.1

scheduler state (CPU RAM)


Process

Insert request into scheduler queue

Decide:

batch membership

whether this step is prefill

admission based on KV capacity



Math

None (policy decisions)


PyTorch view

Not part of raw PyTorch


Triton view

Not applicable yet


CUDA view

No kernel yet


Hardware / memory

Scheduler data structures: CPU RAM


Output

Execution plan: (batch_id, is_prefill=True, positions, lengths)

Next consumer: KV block manager



---

3.2 KV Cache Block Allocation (Logical → Physical Mapping)

Input (origin)

planned sequence length N

model KV layout parameters (L, H, d_head)

KV block pool state (CPU RAM; optionally mirrored on GPU)


Process

Reserve logical KV slots for this request

Map logical blocks to physical GPU HBM blocks


Conceptually:

(req, layer ℓ, token t) -> physical_block_id + offset

Math

Address mapping function (engine-defined):


\mathrm{addr}(\ell, t) = \mathrm{base}(block\_id) + \mathrm{offset}(\ell, t)

PyTorch view

Not visible


Triton view

Kernels will receive pointers / block tables


CUDA view

Device buffers may hold block tables used by attention kernels


Hardware / memory

Mapping tables: CPU RAM (and possibly copied to GPU HBM)


Output

kv_block_table for this request

Next consumer: GPU kernels during prefill



---

4. Host → Device Transfer (CPU → GPU HBM)

4.1 Copy Token IDs to GPU

Input (origin)

input_ids in CPU RAM from 2.1


Process

Allocate device buffer (if not already)

Copy bytes from host to device


Math

None (memory copy)


PyTorch view

input_ids = torch.tensor(input_ids, device="cuda")
# or
input_ids = input_ids.to("cuda")

Triton view

Not a Triton kernel (host API copy)


CUDA view

cudaMemcpyAsync (HtoD)

Possibly enqueued on a CUDA stream


Hardware / memory

Source: CPU RAM

Interconnect: PCIe / NVLink

Destination: GPU HBM (global memory)


Output

input_ids in GPU HBM

Next consumer: embedding lookup kernel (prefill start in Part 2)



---

5. State at the End of Part 1 (All Inputs Ready on GPU)

At this point, before any Transformer math executes:

Data in GPU HBM

input_ids (this request)

model weights (preloaded):

W_embed

all layer weights (W_q, W_k, W_v, W_o, W_up, W_gate, W_down)

final norm scales

W_vocab


RoPE tables (sin/cos) if precomputed

optional device-side kv_block_table


Data in CPU RAM

tokenizer state

scheduler state

request metadata


Next Step (Part 2)

Embedding lookup kernel will read input_ids from HBM and W_embed from HBM

produce X^{(0)} ∈ ℝ^{B×N×d_model} in HBM

then enter the Transformer layer loop



---

One-Line Causal Summary

User string → (CPU tokenizer) → integer token IDs in CPU RAM → (scheduler + KV allocation) → token IDs copied over PCIe/NVLink → GPU HBM now holds all inputs required for the first embedding lookup kernel.
