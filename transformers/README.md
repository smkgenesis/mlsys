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

---

Transformer Inference (Part 2): Prefill — Full Transformer Execution on GPU

This section starts from the exact state produced by Part 1.

Starting state inherited from Part 1

Already present in GPU HBM:

input_ids ∈ ℤ^{B×N}

W_embed ∈ ℝ^{Vocab×d_model}

W_qkv ∈ ℝ^{d_model×(3·d_model)}

W_o ∈ ℝ^{d_model×d_model}

W_up ∈ ℝ^{d_model×d_ff}

W_gate ∈ ℝ^{d_model×d_ff}

W_down ∈ ℝ^{d_ff×d_model}

W_vocab ∈ ℝ^{d_model×Vocab}

RMSNorm scale vectors

RoPE sin/cos tables

kv_block_table for cache writes


Nothing in this section is created “from nowhere.” Every input here came from either:

Part 1 output, or

preloaded trained model weights, or

precomputed runtime tables.



---

Goal of Prefill

Given the full prompt of length N, prefill must:

1. turn token IDs into embeddings,


2. run all L Transformer layers over all N tokens,


3. compute and store K/V for every token at every layer,


4. compute final logits for the last position,


5. select the first generated token.



This is the expensive “full prompt” phase.


---

1. Embedding Lookup

Input (origin)

input_ids[b,t] from Part 1, already copied into GPU HBM

W_embed, which was trained offline and loaded into GPU HBM before serving


Process

For each token position (b, t), the token ID is used as a row index into the embedding table.

No new embedding vector is mathematically derived at runtime. The runtime simply reads the corresponding trained row.

Math

For token ID i = input_ids[b,t]:

X^{(0)}[b,t,:] = W_{embed}[i,:]

Result:

X^{(0)} \in \mathbb{R}^{B \times N \times d_{model}}

PyTorch view

X = embed_tokens(input_ids)              # [B, N, d_model]
# equivalently
X = F.embedding(input_ids, W_embed)

Triton view

A Triton implementation treats this as a gather kernel.

Conceptually:

one program instance handles one or several (b,t) rows,

reads token_id = input_ids[row],

computes pointer W_embed + token_id * d_model,

loads a vector tile,

stores into output X.


CUDA view

A CUDA kernel does:

grid over token positions,

each block/warp handles one or more rows,

each thread loads a chunk of the embedding row,

coalesced loads happen along embedding dimension if layout is contiguous.


No Tensor Core use here. This is memory-read dominated.

Hardware / memory

input_ids: HBM

W_embed: HBM

row fragments: registers

output X^(0): written to HBM


Data flow:

HBM(W_embed, input_ids) → registers → HBM(X^(0))

Output

X^(0) in HBM

this becomes the input to the first layer’s RMSNorm



---

2. Transformer Layer Loop

For each layer ℓ = 0, 1, ..., L-1, the layer input is:

X^{(\ell)} \in \mathbb{R}^{B \times N \times d_{model}}

For ℓ = 0, this is X^(0) from embedding.

Each layer computes:

X^{(\ell)} \rightarrow \text{Attention block} \rightarrow \text{MLP block} \rightarrow X^{(\ell+1)}


---

2.1 RMSNorm (Attention Input)

Input (origin)

X^(ℓ) from the previous step

RMSNorm scale γ_attn, a trained parameter loaded in HBM


Process

For each token vector independently, compute its RMS magnitude, divide by that RMS, then scale by γ_attn.

This normalization is applied before QKV projection.

Math

For a single token vector x ∈ ℝ^{d_model}:

\mathrm{RMS}(x) = \sqrt{\frac{1}{d_{model}} \sum_{j=1}^{d_{model}} x_j^2 + \epsilon}

\hat{x}_j = \gamma_j \cdot \frac{x_j}{\mathrm{RMS}(x)}

Applied over all tokens:

X_n \in \mathbb{R}^{B \times N \times d_{model}}

PyTorch view

Xn = rmsnorm_attn(X)

Triton view

A Triton kernel typically:

assigns one program to one row (b,t),

loads chunks of the row,

reduces sum(x^2),

computes inverse RMS,

multiplies by scale vector,

writes normalized row.


CUDA view

A CUDA kernel typically:

loads row fragments from HBM into registers,

each thread accumulates partial x^2,

warp/block reduction combines partial sums,

one shared value rms is broadcast,

each thread normalizes and scales its fragment.


Hardware / memory

input X^(ℓ): HBM

reduction intermediates: registers

optional reduction staging: shared memory

output Xn: HBM


Data flow:

HBM(X^(ℓ)) → registers/shared → registers → HBM(Xn)

Output

Xn

next consumer: QKV projection



---

2.2 QKV Projection

Input (origin)

Xn from RMSNorm

W_qkv, pretrained weight already in HBM


Process

A single linear layer computes concatenated Q, K, and V.

This means the runtime does not “invent” Q, K, V conceptually. It literally computes them by multiplying the current normalized hidden states by the trained QKV weight matrix.

Math

QKV_{flat} = X_n \cdot W_{qkv}

where

W_{qkv} \in \mathbb{R}^{d_{model} \times (3d_{model})}

Split:

Q_{flat}, K_{flat}, V_{flat} \in \mathbb{R}^{B \times N \times d_{model}}

Then reshape into heads:

Q, K, V \in \mathbb{R}^{B \times H \times N \times d_{head}}

with

d_{head} = d_{model} / H

PyTorch view

qkv = Xn @ W_qkv                      # [B, N, 3*d_model]
q, k, v = qkv.chunk(3, dim=-1)

q = q.view(B, N, H, d_head).transpose(1, 2)
k = k.view(B, N, H, d_head).transpose(1, 2)
v = v.view(B, N, H, d_head).transpose(1, 2)

Triton view

A Triton kernel sees this as a tiled GEMM:

rows: B*N

reduction dimension: d_model

columns: 3*d_model


It loads Xn tiles and weight tiles, computes partial products, accumulates, and writes the output tile.

CUDA view

A CUDA GEMM kernel typically:

loads tile of Xn into shared memory,

loads tile of W_qkv into shared memory,

invokes Tensor Core MMA instructions,

accumulates into registers,

writes output tile to HBM.


This is compute-heavy and Tensor-Core-dominated.

Hardware / memory

read Xn: HBM

read W_qkv: HBM

stage tiles: shared memory

accumulate: registers

write qkv: HBM


Data flow:

HBM(Xn, W_qkv) → shared → Tensor Core / registers → HBM(QKV_flat)

Output

Q, K, V in HBM

next consumer: RoPE



---

2.3 RoPE

Input (origin)

Q, K from QKV projection

position indices 0..N-1, derived directly from token order

RoPE sin/cos tables precomputed in HBM


Process

For every token position and every head, rotate pairs of dimensions (2i, 2i+1) using the precomputed angle for that position and frequency.

This position information does not appear magically. It comes from:

token order → position index

model RoPE configuration → frequency table

precomputed sin/cos tables → actual runtime values


Math

For each token position p, head-dimension pair i, and vector pair (x_{2i}, x_{2i+1}):

\theta_{p,i} = p \cdot \omega_i

\begin{pmatrix}
x'_{2i} \\
x'_{2i+1}
\end{pmatrix}
=
\begin{pmatrix}
\cos\theta_{p,i} & -\sin\theta_{p,i} \\
\sin\theta_{p,i} & \cos\theta_{p,i}
\end{pmatrix}
\begin{pmatrix}
x_{2i} \\
x_{2i+1}
\end{pmatrix}

Applied to Q and K only.

PyTorch view

q, k = apply_rotary_pos_emb(q, k, cos, sin, position_ids)

Triton view

A Triton kernel:

iterates over (b, h, t, i) pairs,

loads q_even, q_odd, cos, sin,

computes rotated pair,

writes back.


CUDA view

A CUDA elementwise kernel:

each thread processes one or multiple dimension pairs,

loads Q/K pair + sin/cos,

performs 2×2 rotation in registers,

writes rotated values.


No Tensor Cores. Pure register arithmetic.

Hardware / memory

Q, K: HBM

sin/cos tables: HBM

arithmetic: registers

output Q_rot, K_rot: HBM


Data flow:

HBM(Q,K,sin,cos) → registers → HBM(Q_rot,K_rot)

Output

Q_rot, K_rot

next consumer: self-attention



---

2.4 Self-Attention (Prefill, Full Prompt)

Input (origin)

Q_rot, K_rot, V

causal mask (implicit by kernel logic or explicit mask)


Process

For each head, compute attention scores between every query token and all valid key tokens, apply softmax, then form weighted sums of value vectors.

In optimized inference, this is not done by writing the full score matrix to HBM. Instead, a fused FlashAttention-like kernel computes the result tile-by-tile.

Math

Per head:

S = \frac{QK^T}{\sqrt{d_{head}}}

Apply causal mask so future positions do not contribute.

P = \mathrm{softmax}(S)

O = PV

Shapes:

Q: [N, d_head]

K: [N, d_head]

S: [N, N]

P: [N, N]

V: [N, d_head]

O: [N, d_head]


Across batch and heads:

O_{attn} \in \mathbb{R}^{B \times H \times N \times d_{head}}

PyTorch view

Conceptual version:

scores = (q @ k.transpose(-2, -1)) / math.sqrt(d_head)
scores = scores + causal_mask
probs = torch.softmax(scores, dim=-1)
O_attn = probs @ v

Practical optimized version:

O_attn = flash_attn(q, k, v, causal=True)

Triton view

A Triton FlashAttention kernel typically:

launches over Q tiles,

streams K/V tiles,

keeps per-row running max and running exp-sum,

accumulates partial softmax(scores) @ V_tile,

never materializes the full N×N matrix in HBM.


CUDA view

Real fused execution looks like this conceptually:

for each Q_tile:
    load Q_tile into shared/registers

    row_max = -inf
    row_sum = 0
    out_acc = 0

    for each KV_tile:
        load K_tile, V_tile into shared memory

        scores = Q_tile @ K_tile^T           # Tensor Core MMA
        apply causal mask

        row_max_new = max(row_max, max(scores))

        row_sum = exp(row_max - row_max_new) * row_sum
                + sum(exp(scores - row_max_new))

        out_acc = exp(row_max - row_max_new) * out_acc
                + exp(scores - row_max_new) @ V_tile

        row_max = row_max_new

    output = out_acc / row_sum
    write output tile

The crucial point is this:

the score matrix exists only transiently in registers/shared memory,

not as a full HBM tensor.


Hardware / memory

Q/K/V source: HBM

Q/K/V tiles: shared memory

score fragments and softmax accumulators: registers

output O_attn: HBM


Data flow:

HBM(Q,K,V) → shared → registers → HBM(O_attn)

Output

O_attn

next consumer: output projection



---

2.5 Output Projection

Input (origin)

O_attn

W_o


Process

Concatenate head outputs back into hidden dimension, then apply output projection.

Math

First reshape:

O_{concat} \in \mathbb{R}^{B \times N \times d_{model}}

Then:

O_{proj} = O_{concat} W_o

PyTorch view

out = O_attn.transpose(1, 2).contiguous().view(B, N, d_model)
O_proj = out @ W_o

Triton view

Standard tiled GEMM with output shaped [B*N, d_model].

CUDA view

Same Tensor-Core GEMM pattern as QKV projection.

Hardware / memory

read O_attn: HBM

read W_o: HBM

stage tiles: shared memory

accumulate: registers

write O_proj: HBM


Output

O_proj

next consumer: first residual add



---

2.6 Residual Add 1

Input (origin)

original layer input X^(ℓ)

attention contribution O_proj


Process

Elementwise addition.

The attention block does not replace the old state. It adds a correction to it.

Math

R^{(\ell)} = X^{(\ell)} + O_{proj}

PyTorch view

R = X + O_proj

Triton view

Simple elementwise binary operation over the full tensor.

CUDA view

threads load corresponding elements of both tensors,

add in registers,

write output.


Hardware / memory

read two tensors from HBM

compute in registers

write result to HBM


Output

R^(ℓ)

next consumer: MLP RMSNorm



---

2.7 RMSNorm (MLP Input)

Input (origin)

R^(ℓ)

learned scale γ_mlp


Process

Same RMSNorm as before, but now applied before the MLP.

Math

Rn = \mathrm{RMSNorm}(R^{(\ell)})

PyTorch view

Rn = rmsnorm_mlp(R)

Triton / CUDA / Hardware

Same pattern as the first RMSNorm.

Output

Rn

next consumer: MLP



---

2.8 MLP (SwiGLU)

Input (origin)

Rn

W_up, W_gate, W_down


Process

The MLP expands the representation, gates it, then projects it back down.

Math

U = Rn W_{up}

G = Rn W_{gate}

\mathrm{SiLU}(x) = x \cdot \sigma(x)

H = U \odot \mathrm{SiLU}(G)

M = H W_{down}

where:

U, G, H ∈ ℝ^{B×N×d_ff}

M ∈ ℝ^{B×N×d_model}


PyTorch view

u = up_proj(Rn)
g = gate_proj(Rn)
h = u * F.silu(g)
M = down_proj(h)

Triton view

Likely split into:

1. GEMM for U


2. GEMM for G


3. fused elementwise kernel for SiLU(G) and U * SiLU(G)


4. GEMM for M



A custom Triton implementation can fuse some of these steps.

CUDA view

Typical CUDA path:

GEMM kernel for U

GEMM kernel for G

elementwise kernel:

load G

compute SiLU(G)

multiply by U


GEMM kernel for M


Hardware / memory

reads Rn and MLP weights from HBM

GEMM tiles staged in shared memory

Tensor Core math in Tensor Cores

accumulators in registers

elementwise gate in registers

output M in HBM


Data flow:

HBM(Rn, W_up/W_gate) → shared → TensorCore/registers → HBM(U,G)
HBM(U,G) → registers → HBM(H)
HBM(H, W_down) → shared → TensorCore/registers → HBM(M)

Output

M

next consumer: second residual add



---

2.9 Residual Add 2

Input (origin)

R^(ℓ)

M


Process

Add the MLP contribution back to the residual stream.

Math

X^{(\ell+1)} = R^{(\ell)} + M

PyTorch view

X_next = R + M

Triton view

Elementwise add kernel.

CUDA view

Same as first residual add.

Hardware / memory

read R^(ℓ) and M from HBM

add in registers

write X^(ℓ+1) to HBM


Output

X^(ℓ+1)

this becomes input to the next layer



---

2.10 KV Cache Write (Prefill)

Input (origin)

K_rot from this layer

V from this layer

kv_block_table from Part 1


Process

Store keys and values for every prompt token at this layer into the request’s assigned KV cache locations in HBM.

This is the causal origin of future decode reuse.

Without this write, later decode steps would have no cached keys/values to read.

Math

For each (layer ℓ, batch b, head h, token t):

KV_K[\ell,b,h,t,:] \leftarrow K^{(\ell)}_{b,h,t,:}

KV_V[\ell,b,h,t,:] \leftarrow V^{(\ell)}_{b,h,t,:}

PyTorch view

Conceptually:

k_cache[layer, :, :, :N, :] = K_rot
v_cache[layer, :, :, :N, :] = V

In engines like vLLM this is paged rather than contiguous.

Triton view

Scatter/store kernel using block-table-driven address computation.

CUDA view

compute physical block base address via kv_block_table

compute local token offset

coalesced stores of K/V fragments into global memory


Hardware / memory

source K_rot, V: HBM

address arithmetic: registers

destination KV cache blocks: HBM


Data flow:

HBM(K_rot,V) → registers(address calc) → HBM(KV cache)

Output

prompt KV cache fully populated for this layer

after all layers, the request has full prompt history cached



---

3. After the Final Layer

After completing the loop for all L layers, we have:

X^{(L)} \in \mathbb{R}^{B \times N \times d_{model}}


---

3.1 Final RMSNorm

Input (origin)

X^(L)


Process

Final normalization before vocabulary projection.

Math

X_{final} = \mathrm{RMSNorm}(X^{(L)})

PyTorch view

X_final = final_norm(X)

Triton / CUDA / Hardware

Same RMSNorm pattern as before.

Output

X_final



---

3.2 LM Head

Input (origin)

X_final

W_vocab


Process

Project hidden state into vocabulary logits.

Only the last prompt position is used for first-token generation.

Math

Logits = X_{final} W_{vocab}

For the last prompt position:

z \in \mathbb{R}^{Vocab}

where z is the logits vector for the next token.

PyTorch view

logits = X_final @ W_vocab
z = logits[:, -1, :]

Triton view

Large GEMM from hidden dimension to vocabulary dimension.

CUDA view

Tensor Core GEMM:

hidden vector tile × vocab weight tile

accumulate logits in registers

write logits to HBM


Hardware / memory

X_final: HBM

W_vocab: HBM

shared memory staging for tiles

Tensor Core compute

output logits: HBM


Output

z = last-position logits



---

3.3 First Token Selection (TTFT)

Input (origin)

logits vector z


Process

Convert logits into a next-token decision by greedy selection or sampling.

Math

Softmax:

p_i = \frac{e^{z_i/T}}{\sum_j e^{z_j/T}}

Then either:

next\_token = \arg\max_i z_i

or sample from p.

PyTorch view

Greedy:

next_token = torch.argmax(z, dim=-1)

Sampling:

probs = torch.softmax(z / temperature, dim=-1)
next_token = torch.multinomial(probs, num_samples=1)

Triton view

Could be:

reduction kernel for argmax

or softmax + filtering + RNG-based selection


CUDA view

argmax: reduction in registers/shared memory

sampling: softmax kernel + random draw kernel, or host-assisted logic


Hardware / memory

logits from HBM

reduction/intermediate values in registers/shared

resulting token ID stored in HBM or copied to CPU RAM


Output

next_token_id

this is both:

streamed to CPU/client

and used as input to Part 3 decode




---

4. State at the End of Part 2

In GPU HBM

prompt KV cache for all L layers

final hidden states

logits for last prompt position


In CPU memory

the first generated token, once copied back / decoded for streaming



---

5. One-Line Causal Summary

Prompt token IDs in HBM are gathered into embedding vectors, transformed through L repeated blocks of RMSNorm → QKV GEMM → RoPE → FlashAttention → output projection → residual → RMSNorm → SwiGLU MLP → residual, while K/V for every token are written into KV cache, and the final hidden state is projected by the LM head into logits from which the first output token is selected.

---

Transformer Inference (Part 3): Decode Loop, KV Cache Reuse, Streaming, and Cleanup

This section starts from the exact state produced at the end of Part 2.

Starting state inherited from Part 2

Already true before decode step 1 starts:

In GPU HBM

full prompt KV cache for every layer

model weights

RoPE tables

runtime block mapping tables

final hidden state and logits for the prompt’s last position


In CPU memory

the first generated token ID may already have been copied back

tokenizer state exists

streaming response is open



---

Goal of Decode

For each newly generated token:

1. feed that token back into the model,


2. process only one new token through all layers,


3. reuse all previous K/V from KV cache,


4. compute next-token logits,


5. emit one token,


6. append new K/V to KV cache,


7. repeat until stop.



This is the autoregressive generation loop.

The key difference from prefill is:

Prefill: process all N prompt tokens

Decode: process exactly one newly generated token at a time



---

1. Decode Loop Entry

1.1 Current Token Becomes Next Input

Input (origin)

next_token_id produced at the end of the previous step

for the very first decode step, this is the first token produced by Part 2


Process

The previously generated token becomes the current model input token.

This is the causal reason generation is autoregressive:

token t+1 is generated from all previous tokens,

then token t+1 becomes part of the input for generating token t+2.


Math

If the already known sequence is

(x_1, x_2, \dots, x_t)

then the model computes

P(x_{t+1} \mid x_1, x_2, \dots, x_t)

After choosing x_{t+1}, the next step conditions on

(x_1, x_2, \dots, x_t, x_{t+1})

PyTorch view

current_token = next_token

Triton view

No Triton math kernel yet. This is runtime sequencing.

CUDA view

No CUDA compute yet. This is loop control.

Hardware / memory

current token ID may temporarily exist in CPU RAM

then it is copied or already staged into GPU HBM


Output

current_token_id

next consumer: decode embedding lookup



---

1.2 Device Placement of the Current Token

Input (origin)

current_token_id from 1.1


Process

Ensure the token ID exists in device memory so kernels can consume it.

PyTorch view

current_token = current_token.to("cuda")

Triton view

Not a Triton kernel; host/device data placement.

CUDA view

Usually a small host-to-device copy or device-side reuse from prior step.

Hardware / memory

source: CPU RAM or existing device buffer

destination: GPU HBM


Output

current token ID in GPU HBM



---

2. Embedding Lookup for One Token

Input (origin)

current_token_id in HBM

W_embed in HBM


Process

Read one row from the embedding matrix.

This is exactly the same operation as Part 2 embedding lookup, except now sequence length is 1 for the new token.

Math

For token ID i_t:

x_t = W_{embed}[i_t]

Result:

x_t \in \mathbb{R}^{d_{model}}

or batched:

X_t \in \mathbb{R}^{B \times 1 \times d_{model}}

PyTorch view

x = embed_tokens(current_token)   # [B, 1, d_model]

Triton view

Same gather pattern as prefill, but only for one token row per request.

CUDA view

A gather kernel:

read token ID,

compute row pointer,

load row chunks,

write one embedding vector.


Hardware / memory

current_token_id: HBM

W_embed: HBM

row fragment compute: registers

output x: HBM


Output

x_t

next consumer: layer 0 decode pass



---

3. Decode Layer Loop

Now we process the current token through all layers.

Important difference from prefill:

the current layer input has shape [B, 1, d_model]

but the layer also reads the historical KV cache for all previous tokens


For each layer ℓ, we have:

current token hidden state for this layer

cached keys and values from all earlier tokens at this layer



---

3.1 RMSNorm (Decode Attention Input)

Input (origin)

current token hidden state for layer ℓ

learned norm scale γ_attn


Process

Exactly the same RMSNorm as prefill, but now applied to only one token vector.

Math

For token vector x_t:

\mathrm{RMS}(x_t) = \sqrt{\frac{1}{d_{model}} \sum_{j=1}^{d_{model}} x_{t,j}^2 + \epsilon}

\hat{x}_{t,j} = \gamma_j \cdot \frac{x_{t,j}}{\mathrm{RMS}(x_t)}

PyTorch view

xn = rmsnorm_attn(x)

Triton view

One row normalization kernel, now operating on much smaller token count.

CUDA view

Same reduction kernel pattern:

load row fragment,

accumulate squared sum,

reduce,

normalize and scale.


Hardware / memory

read x_t: HBM

reduction: registers/shared

write xn: HBM


Output

xn

next consumer: QKV projection



---

3.2 QKV Projection for the New Token

Input (origin)

xn

W_qkv


Process

Compute Q, K, V only for the new token, not for the entire sequence.

This is the crucial runtime saving.

Math

[q_t \mid k_t \mid v_t] = x_n W_{qkv}

where the output is then reshaped into heads:

q_t, k_t, v_t \in \mathbb{R}^{B \times H \times 1 \times d_{head}}

PyTorch view

qkv = xn @ W_qkv
q, k, v = qkv.chunk(3, dim=-1)
q = q.view(B, 1, H, d_head).transpose(1, 2)
k = k.view(B, 1, H, d_head).transpose(1, 2)
v = v.view(B, 1, H, d_head).transpose(1, 2)

Triton view

Same GEMM as prefill, but with one sequence position for the active token.

CUDA view

Still a GEMM/Tensor Core operation, but much smaller on the sequence dimension:

input tile is tiny (1 × d_model per sequence position),

weight tile is unchanged,

output is one token’s Q/K/V.


Hardware / memory

read xn: HBM

read W_qkv: HBM

stage tiles: shared memory

compute: Tensor Cores + registers

write q_t, k_t, v_t: HBM


Output

q_t, k_t, v_t

next consumer: RoPE



---

3.3 RoPE for the New Token

Input (origin)

q_t, k_t

decode position index t

RoPE sin/cos tables


Process

Apply rotation only for the current position t.

All previous tokens’ keys are already rotated and already stored in KV cache.

So in decode, RoPE is applied only to the new token’s Q and K.

Math

For each pair (2i, 2i+1):

\begin{pmatrix}
x'_{2i} \\
x'_{2i+1}
\end{pmatrix}
=
\begin{pmatrix}
\cos\theta_{t,i} & -\sin\theta_{t,i} \\
\sin\theta_{t,i} & \cos\theta_{t,i}
\end{pmatrix}
\begin{pmatrix}
x_{2i} \\
x_{2i+1}
\end{pmatrix}

PyTorch view

q_t, k_t = apply_rotary_pos_emb(q_t, k_t, cos_t, sin_t)

Triton view

Elementwise kernel on one-token Q/K slices.

CUDA view

Same register-level pairwise rotation as prefill, but only for one position.

Hardware / memory

read q_t, k_t: HBM

read sin/cos for position t: HBM

compute in registers

write rotated values to HBM


Output

q_t_rot, k_t_rot

next consumer: attention + KV cache write



---

3.4 Read Historical KV Cache

Input (origin)

kv_block_table

KV cache written during:

Part 2 prefill for prompt tokens

previous decode steps for generated tokens



Process

Load all previous keys and values for this layer so the current query can attend to them.

This is the single most important difference between decode with cache and decode without cache.

With KV cache:

past keys/values are reused


Without KV cache:

they would need to be recomputed from scratch


Math

For current step t, the kernel needs:

K_{1:t} = [K_1, K_2, \dots, K_t]

V_{1:t} = [V_1, V_2, \dots, V_t]

where the new K_t, V_t come from the current token, and K_1...K_{t-1}, V_1...V_{t-1} come from cache.

PyTorch view

Conceptually:

k_hist = k_cache[layer, :, :, :t, :]
v_hist = v_cache[layer, :, :, :t, :]

In paged engines like vLLM, this is not a simple contiguous slice; the runtime follows a block table.

Triton view

A Triton paged-attention kernel receives:

current query pointer

block table

base pointers for paged KV memory


and streams blocks in logical order.

CUDA view

The kernel:

computes physical cache addresses using kv_block_table

loads K/V tiles from HBM

streams them through shared memory as needed


Hardware / memory

KV cache resides in HBM

selected tiles are loaded to shared memory

address arithmetic in registers


Data flow:

HBM(KV cache) → shared → registers

Output

K/V history available to the attention kernel



---

3.5 Decode Attention (Current Query Against Cached History)

Input (origin)

q_t_rot

cached historical K_{1:t-1}, V_{1:t-1}

current k_t_rot, v_t


Process

Compute attention only for the current token’s query against all available keys up to the current position.

This is fundamentally different from prefill:

prefill computes attention for all prompt tokens

decode computes attention for one new query against the cached history


Math

For each head and each previous position j:

s_j = \frac{q_t \cdot k_j}{\sqrt{d_{head}}}

Then:

p_j = \frac{e^{s_j}}{\sum_{m=1}^{t} e^{s_m}}

Then output:

o_t = \sum_{j=1}^{t} p_j v_j

Across all heads:

O_{attn,t} \in \mathbb{R}^{B \times H \times 1 \times d_{head}}

Why this is cheaper than recomputing everything

Without KV cache, to generate token t+1, the system would need to recompute hidden states, keys, and values for all earlier tokens again.

With KV cache, it only computes:

q_t, k_t, v_t for the current token


and reuses all historical K,V.

That is why decode is practical.

PyTorch view

Conceptually:

scores = (q_t @ k_hist.transpose(-2, -1)) / math.sqrt(d_head)
probs = torch.softmax(scores, dim=-1)
ctx = probs @ v_hist

Optimized engines call paged attention / flash decode kernels instead of doing this naively.

Triton view

A Triton decode attention kernel usually:

treats the current query as very small,

streams large K/V history from paged memory,

computes score fragments,

performs streaming softmax,

accumulates one output vector.


CUDA view

This kernel is often more memory-bound than compute-bound because:

the current query is tiny,

the historical K/V read is large,

the kernel spends much of its time pulling K/V tiles from HBM.


Conceptually:

load q_t
for each KV tile in history:
    load K_tile, V_tile
    scores = q_t @ K_tile^T
    update running softmax stats
    out_acc += partial_weights @ V_tile
write final output

Hardware / memory

q_t: HBM → registers

K/V history: HBM → shared memory

score / softmax accumulators: registers

final output: HBM


Data flow:

HBM(q_t, KV cache) → shared/registers → HBM(O_attn,t)

Output

attention output for the current token only

next consumer: output projection



---

3.6 KV Cache Write for the Current Token

Input (origin)

k_t_rot, v_t

kv_block_table


Process

Store the new token’s key and value in the request’s KV cache so that the next decode step can reuse them.

This is the causal bridge between decode step t and decode step t+1.

Math

KV_K[\ell,b,h,t,:] \leftarrow k^{(\ell)}_{t}

KV_V[\ell,b,h,t,:] \leftarrow v^{(\ell)}_{t}

PyTorch view

Conceptually:

k_cache[layer, :, :, t, :] = k_t_rot
v_cache[layer, :, :, t, :] = v_t

Again, in paged systems this is a mapped write, not necessarily contiguous.

Triton view

Store/scatter kernel with physical address lookup from block table.

CUDA view

compute destination physical address

store K/V fragments to HBM

often coalesced stores across head dimension


Hardware / memory

source K/V: HBM or device buffers

address arithmetic: registers

destination KV cache: HBM


Data flow:

HBM(k_t,v_t) → registers(address calc) → HBM(KV cache)

Output

KV cache extended by one token for this layer

next decode step will read it



---

3.7 Output Projection

Input (origin)

current token attention output O_attn,t

W_o


Process

Concatenate heads and apply output projection.

Math

O_{proj,t} = \mathrm{Concat}(O_{attn,t}) W_o

PyTorch view

out = O_attn_t.transpose(1, 2).contiguous().view(B, 1, d_model)
O_proj_t = out @ W_o

Triton view

Small GEMM on the current token.

CUDA view

Same Tensor Core GEMM pattern as before, but sequence dimension is 1.

Hardware / memory

read O_attn,t and W_o: HBM

stage in shared memory

compute in Tensor Cores / registers

write O_proj_t: HBM


Output

projected attention contribution for the current token



---

3.8 Residual Add 1

Input (origin)

layer input token state

projected attention output


Math

R_t^{(\ell)} = X_t^{(\ell)} + O_{proj,t}

PyTorch view

R_t = X_t + O_proj_t

Triton view

Elementwise add on one token row.

CUDA view

Read two vectors, add in registers, write result.

Hardware / memory

read from HBM

compute in registers

write to HBM


Output

residual token state after attention



---

3.9 RMSNorm (MLP Input)

Input (origin)

R_t^(ℓ)


Process

Normalize the current token vector before MLP.

Math

Same RMSNorm as before.

PyTorch view

Rn_t = rmsnorm_mlp(R_t)

Triton / CUDA / Hardware

Same row-wise reduction + scaling pattern as previous norms.

Output

Rn_t



---

3.10 MLP (Current Token Only)

Input (origin)

Rn_t

W_up, W_gate, W_down


Process

Run SwiGLU MLP for only the current token.

Math

U_t = Rn_t W_{up}

G_t = Rn_t W_{gate}

H_t = U_t \odot \mathrm{SiLU}(G_t)

M_t = H_t W_{down}

PyTorch view

u_t = up_proj(Rn_t)
g_t = gate_proj(Rn_t)
h_t = u_t * F.silu(g_t)
M_t = down_proj(h_t)

Triton view

Same three-step GEMM/gate/GEMM pipeline, but on one token row.

CUDA view

small GEMM for U_t

small GEMM for G_t

fused elementwise gate

small GEMM for M_t


Hardware / memory

HBM reads for token row and weights

shared memory for tiles

Tensor Cores / registers for GEMMs

registers for SiLU and multiply

write M_t to HBM


Output

current token MLP output



---

3.11 Residual Add 2

Input (origin)

R_t^(ℓ)

M_t


Math

X_t^{(\ell+1)} = R_t^{(\ell)} + M_t

PyTorch view

X_next_t = R_t + M_t

Triton view

Elementwise add on one token row.

CUDA view

Vector add in registers.

Hardware / memory

read from HBM

compute in registers

write to HBM


Output

current token state for next layer



---

4. After the Final Decode Layer

After all L layers, the current token has a final hidden representation.


---

4.1 Final RMSNorm

Input (origin)

final-layer current-token hidden state


Process

Apply final normalization.

Math

x_{final,t} = \mathrm{RMSNorm}(x_t^{(L)})

PyTorch view

x_final_t = final_norm(x_t)

Triton / CUDA / Hardware

Same norm kernel pattern.

Output

x_final_t



---

4.2 LM Head

Input (origin)

x_final_t

W_vocab


Process

Project the current hidden vector into vocabulary space.

Math

z_t = x_{final,t} W_{vocab}

where:

z_t \in \mathbb{R}^{Vocab}

PyTorch view

z_t = x_final_t @ W_vocab

Triton view

Small-by-large GEMM from hidden dimension to vocabulary dimension.

CUDA view

Tensor Core GEMM for logits generation.

Hardware / memory

read x_final_t: HBM

read W_vocab: HBM

shared memory tile staging

Tensor Core compute

write logits z_t: HBM


Output

logits for the next token



---

4.3 Token Selection

Input (origin)

logits z_t


Process

Choose the next token using greedy decoding or sampling.

Math

Softmax with temperature:

p_i = \frac{e^{z_i/T}}{\sum_j e^{z_j/T}}

Greedy:

token_{t+1} = \arg\max_i z_i

Sampling:

token_{t+1} \sim p

Possibly after top-k / top-p filtering.

PyTorch view

Greedy:

token_next = torch.argmax(z_t, dim=-1)

Sampling:

probs = torch.softmax(z_t / temperature, dim=-1)
token_next = torch.multinomial(probs, num_samples=1)

Triton view

Could be implemented as:

reduction kernel for argmax, or

softmax + filtered sampling kernel


CUDA view

argmax: reduction over vocabulary

sampling: softmax + optional top-k/top-p + RNG draw


Hardware / memory

logits in HBM

reduction / partial sums in registers/shared

final token ID in HBM or copied to CPU RAM


Output

token_{t+1}

next consumers:

streaming output path

next iteration of decode loop




---

5. Streaming the Token to the User

5.1 Copy Token ID Back if Needed

Input (origin)

selected token ID from device-side output


Process

Move token ID to CPU if decode/streaming logic is CPU-side.

PyTorch view

token_cpu = token_next.cpu()

Triton view

Not a Triton kernel.

CUDA view

Device-to-host copy if needed.

Hardware / memory

source: GPU HBM

transport: PCIe / NVLink

destination: CPU RAM


Output

token ID in CPU memory



---

5.2 Detokenization of the New Token Piece

Input (origin)

token ID

tokenizer decode tables in CPU RAM


Process

Map token ID back to text fragment.

Math

text\_piece = vocab^{-1}(token\_id)

PyTorch view

Usually not PyTorch; tokenizer library:

piece = tokenizer.decode([token_id])

Triton view

Not applicable.

CUDA view

Not applicable.

Hardware / memory

tokenizer state: CPU RAM

output text fragment: CPU RAM


Output

text piece such as " data" or "ing"



---

5.3 Stream Fragment Over Network

Input (origin)

decoded text piece


Process

Append the fragment to the active response stream so the client can render it immediately.

PyTorch / Triton / CUDA views

Not applicable; this is server/networking logic.

Hardware / memory

response buffers in CPU RAM

network send through NIC


Output

fragment appears on the user’s screen



---

6. Loop Condition and Continuation

Input (origin)

just-generated token

generation counters

stop rules


Process

Check stop conditions:

EOS produced

max_new_tokens reached

stop sequence matched

client canceled request


If none are true:

the generated token becomes the next loop input,

decode repeats.


Math

Autoregressive recurrence:

x_{t+1} \sim P(x_{t+1} \mid x_1, x_2, \dots, x_t)

and then x_{t+1} is appended to the known sequence.

Output

either continue decode loop

or enter cleanup



---

7. Why KV Cache Changes Complexity

Without KV cache

At decode step t, the model would need to recompute hidden states, keys, and values for all previous tokens again.

That means repeatedly doing work proportional to the whole prefix.

With KV cache

At decode step t, the model computes only:

current token embedding

current token Q/K/V

current token forward through all layers


and reads historical K/V from cache.

That is the reason long generation is feasible.

Operational meaning

At token 10,000:

with KV cache: read K/V for positions 1..9999, compute new token only

without KV cache: recompute positions 1..9999 again


This is the core inference optimization.


---

8. Cleanup After Generation Ends

When stop conditions are met, the request enters termination.


---

8.1 Final Detokenization / Assembly

Input (origin)

all generated token IDs


Process

Decode the final list into a final text string if needed for non-streaming completion or final bookkeeping.

Output

full response string in CPU RAM



---

8.2 Free KV Cache Blocks

Input (origin)

request’s allocated KV blocks

block manager reference counts


Process

For each block used by the request:

1. decrement reference count


2. if it reaches zero, return block to free pool



Math

For block b:

refcount(b) \leftarrow refcount(b) - 1

If:

refcount(b) = 0

then block b becomes reusable.

PyTorch view

Not part of PyTorch.

Triton view

Not a Triton math kernel.

CUDA view

May involve device metadata updates, but mostly runtime memory management.

Hardware / memory

block tables and counters in CPU RAM and/or GPU metadata buffers

actual KV payload in HBM is usually not zeroed immediately; it is just marked reusable


Output

GPU KV blocks returned to free pool



---

8.3 Close Network Response

Input (origin)

request marked complete


Process

Finalize and close the HTTP/streaming response.

Output

request lifecycle ends


Hardware / memory

CPU-side networking logic

final response buffers released



---

9. One-Line Causal Summary

A generated token is fed back into the model, embedded, normalized, projected into Q/K/V, position-rotated, matched against all cached historical K/V, passed through output projection, residual, MLP, final norm, and LM head to produce the next-token logits; the chosen next token is streamed to the user, appended to the autoregressive loop, and its new K/V are stored so the following decode step can reuse them.


---

10. Final End-to-End Meaning of Part 3

Part 2 built the initial KV cache from the prompt.

Part 3 repeatedly does this:

1. take one generated token,


2. run one-token forward pass through all layers,


3. read historical KV cache,


4. write new K/V,


5. produce the next token,


6. stream it,


7. repeat.



That is the real execution of autoregressive Transformer inference.