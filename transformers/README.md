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