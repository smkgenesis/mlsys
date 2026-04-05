# transformers

Transformer architecture and internal model computation.

Belongs here:
- attention mechanics,
- MLP blocks,
- positional encoding,
- residual structure,
- and end-to-end causal understanding of how a transformer computes.

Does not belong here:
- serving-runtime topics whose primary question is batching, scheduler behavior, or KV-cache management; those belong in `inference/`.

Current notes:
- [End-to-End Autoregressive Transformer Inference](README.md)

---

End-to-End Autoregressive Transformer Inference:
User String -> Tokenization -> GPU Execution -> Returned String

This document describes the full causal path of transformer inference with deliberately low abstraction.

The goal is not to say "attention happens" or "the model generates text" and stop there.
The goal is to make every major transformation explicit:
- where the input came from,
- what exact object is being consumed,
- what math is applied,
- which runtime layer performs it,
- where the data lives before and after the step,
- and how the output becomes the next step's input.

The primary execution model assumed here is modern autoregressive LLM inference:
- request arrives at a server,
- prompt is tokenized on CPU,
- tokens are moved to GPU,
- the prompt is processed in a prefill pass,
- a decode loop generates one token at a time using the KV cache,
- generated tokens are detokenized and streamed back to the user.

The document describes the same pipeline from four complementary views whenever useful:
- mathematical view,
- Python / PyTorch view,
- Triton-kernel view,
- CUDA-kernel and hardware-memory view.

This document is intentionally long because the point is not brevity.
The point is to remove hidden inputs, skipped steps, and vague causality.

---

## 0. Scope and Preconditions

The user-visible request begins with a text string, but the request cannot be served unless several objects already exist before the request arrives.

These are preconditions, not outputs of the request itself:

- tokenizer rules and vocabulary:
  trained offline, loaded into CPU RAM
- model weights:
  trained offline, loaded into GPU HBM before serving
- inference runtime:
  scheduler, batcher, KV block manager, request metadata structures in CPU RAM
- optional device-side metadata:
  block tables, sequence metadata buffers, and other runtime support tensors in GPU HBM
- CUDA context, streams, kernel binaries:
  initialized before request execution

None of the steps below creates the model weights or tokenizer vocabulary.
They are causal inputs that were produced during training or server initialization.

Throughout the document, the main trained tensors include:

- token embedding table `W_embed`
- per-layer normalization scales
- attention weights `W_q`, `W_k`, `W_v` or combined `W_qkv`
- attention output projection `W_o`
- MLP projections `W_up`, `W_gate`, `W_down`
- final normalization scales
- LM head / vocabulary projection `W_vocab`

For compactness, many sections use a combined QKV matrix:

```text
W_qkv in R^(d_model x 3 d_model)
```

but the same causal story applies if the implementation stores separate `W_q`, `W_k`, `W_v`.


---

## 1. Global Causal Map

Before diving into the exact steps, it helps to state the global pipeline in one line:

```text
client bytes
-> server request object
-> prompt string
-> token IDs
-> scheduled execution plan
-> GPU-resident input IDs and runtime metadata
-> embedding lookup
-> repeated Transformer layer math
-> final logits
-> sampled/selected next token
-> detokenized text fragment
-> network response bytes
```

During generation this becomes a loop:

```text
prompt tokens -> prefill -> first generated token
-> decode step for token t
-> next generated token
-> decode step for token t+1
-> ...
-> stop condition
```

The decode loop is feasible only because each step reuses cached keys and values from all prior tokens.


---

## 2. Notation and Shapes

The document uses the following symbols:

- `B`: batch size
- `N`: prompt length during prefill
- `L`: number of Transformer layers
- `H`: number of attention heads
- `d_model`: hidden dimension
- `d_head`: head dimension, usually `d_model / H`
- `d_ff`: MLP expansion dimension
- `V`: vocabulary size
- `t`: decode step or token position
- `omega`: sample-space style symbol for an outcome when needed

Main tensor shapes:

- token IDs:

```text
input_ids in Z^(B x N)
```

- hidden states:

```text
X^(ell) in R^(B x N x d_model)
```

- per-head Q/K/V:

```text
Q, K, V in R^(B x H x N x d_head)
```

- decode-time single-token hidden state:

```text
X_t^(ell) in R^(B x 1 x d_model)
```

- logits:

```text
z_t in R^(B x V)
```


---

## 3. Request Arrival and Request Object Construction

### 3.1 Raw byte arrival

Deep dive: [03-1-raw-byte-arrival](03-1-raw-byte-arrival/README.md)

Input (origin)

- UTF-8 encoded bytes sent by the client over the network
- these bytes originate outside the server process, typically from:
  - browser UI
  - CLI client
  - API caller
  - another service

Process

- network interface card receives packets
- OS kernel reassembles byte stream / request payload
- user-space server reads bytes from kernel buffers
- request bytes are parsed into a structured request

Typical fields created at this moment:

- `prompt: str`
- generation parameters:
  - `max_new_tokens`
  - `temperature`
  - `top_p`
  - `top_k`
  - stop sequences
- routing / metadata:
  - request ID
  - user/session ID
  - model ID

Math

- none beyond byte parsing and object construction

Python view

```python
request = {
    "prompt": "Explain the Transformer architecture in detail.",
    "max_new_tokens": 256,
    "temperature": 0.7,
    "top_p": 0.95,
}
```

Triton view

- no Triton kernel is involved

CUDA view

- no CUDA compute is involved

Hardware / memory

- NIC -> kernel buffers -> user-space buffers
- request object is created in CPU RAM

Output

- request object in CPU RAM
- next consumer: tokenizer and scheduler


---

## 4. Tokenization on CPU

### 4.1 Tokenizer state used at inference time

Deep dive: [04-1-tokenizer-state](04-1-tokenizer-state/README.md)

Input (origin)

- `request.prompt` from Section 3
- tokenizer rules and vocabulary loaded during server startup into CPU RAM

The tokenizer state is not created by the request.
It was produced offline when the tokenizer was trained and serialized.

### 4.2 Prompt string -> token IDs

Deep dive: [04-2-prompt-string-to-token-ids](04-2-prompt-string-to-token-ids/README.md)

Process

- apply tokenizer segmentation rules such as BPE or SentencePiece
- split string into token pieces or subword pieces
- map each piece to an integer vocabulary ID

Math

Let the segmented token pieces be:

```text
t_1, t_2, ..., t_N
```

and let the vocabulary mapping be:

```text
vocab(t_k) = i_k
```

Then the tokenized prompt becomes:

```text
input_ids = [i_1, i_2, ..., i_N]
```

Python view

```python
input_ids = tokenizer.encode(request["prompt"])
```

Triton view

- not applicable; tokenization is CPU-side text processing

CUDA view

- not applicable

Hardware / memory

- execution on CPU cores
- tokenizer tables in CPU RAM
- output integer array in CPU RAM

Output

- `input_ids` in CPU RAM
- prompt length `N`
- next consumers:
  - scheduler
  - KV allocator
  - host-to-device transfer logic


---

## 5. Scheduling, Admission, and KV Reservation

### 5.1 Runtime scheduling context

Deep dive: [05-1-runtime-scheduling-context](05-1-runtime-scheduling-context/README.md)

Input (origin)

- `input_ids` from tokenization
- generation parameters from request construction
- scheduler queues and runtime state already living in CPU RAM

The scheduler state already exists before the request arrives.
It tracks active requests, available GPU capacity, and runtime policy.

### 5.2 Admission and batching

Deep dive: [05-2-admission-and-batching](05-2-admission-and-batching/README.md)

Process

- insert request into scheduler queue
- determine whether the request can enter the next execution batch
- assign batch membership
- classify the next execution stage as:
  - prefill if processing the prompt for the first time
  - decode if generating one new token for an already-active request

Math

- no model math; this is policy logic

Python view

- not part of vanilla PyTorch model code
- implemented by the inference runtime

Triton view

- not applicable yet

CUDA view

- no compute kernel yet

Hardware / memory

- scheduler data structures in CPU RAM

Output

- execution plan:
  - batch ID
  - prompt length
  - prefill/decode mode
  - sequence positions
- next consumer: KV block manager

### 5.3 KV cache reservation

Deep dive: [05-3-kv-cache-reservation](05-3-kv-cache-reservation/README.md)

Input (origin)

- planned sequence length `N`
- model structural parameters `L`, `H`, `d_head`
- KV block pool state from runtime metadata

Process

- reserve logical KV storage for every token position that will need keys and values
- map logical `(request, layer, token)` positions to physical GPU HBM block addresses

Conceptually:

```text
(request, layer ell, token t)
-> (physical_block_id, offset_within_block)
```

Math

The exact block mapping is engine-specific, but the logical idea is:

```text
addr(ell, t) = base(block_id) + offset(ell, t)
```

Python view

- runtime-specific, not standard PyTorch

Triton view

- future attention kernels will receive block tables or derived pointers

CUDA view

- block tables may be copied to device metadata buffers
- later kernels use those tables for gather/scatter of KV data

Hardware / memory

- block metadata in CPU RAM
- optional mirrored metadata in GPU HBM

Output

- request-specific KV block table
- next consumers:
  - prefill attention kernels
  - future decode attention kernels


---

## 6. Host-to-Device Placement

### 6.1 Prompt token IDs copied to GPU

Deep dive: [06-1-prompt-token-ids-on-gpu](06-1-prompt-token-ids-on-gpu/README.md)

Input (origin)

- `input_ids` from Section 4 in CPU RAM

Process

- allocate GPU buffer if needed
- copy token IDs from host memory to device global memory

Math

- no math; memory movement only

Python view

```python
input_ids_gpu = torch.tensor(input_ids, device="cuda")
```

Triton view

- not a Triton kernel; this is host-side runtime placement

CUDA view

```text
cudaMemcpyAsync(..., cudaMemcpyHostToDevice)
```

or framework-managed equivalents

Hardware / memory

- source: CPU RAM
- transport: PCIe or NVLink
- destination: GPU HBM

Output

- GPU-resident `input_ids`
- next consumer: embedding lookup

### 6.2 State just before model math begins

Deep dive: [06-2-precompute-device-state](06-2-precompute-device-state/README.md)

At this point the following already exist in GPU HBM:

- `input_ids`
- all model weights loaded at server startup
- optional RoPE tables
- optional runtime metadata such as KV block tables

The first real Transformer math step now has everything it needs on the device.


---

## 7. Embedding Lookup

### 7.1 Token IDs -> initial hidden vectors

Deep dive: [07-1-embedding-lookup](07-1-embedding-lookup/README.md)

Input (origin)

- `input_ids` copied to GPU in Section 6
- token embedding matrix `W_embed` loaded into GPU HBM during model initialization

Process

- each token ID is used as a row index into `W_embed`
- the corresponding embedding row is read and written into the hidden-state tensor

Math

For token ID `i = input_ids[b, t]`:

```text
X^(0)[b, t, :] = W_embed[i, :]
```

Result:

```text
X^(0) in R^(B x N x d_model)
```

Python view

```python
X = embed_tokens(input_ids_gpu)
# or
X = F.embedding(input_ids_gpu, W_embed)
```

Triton view

- gather kernel:
  - read token ID
  - compute row pointer into `W_embed`
  - load embedding vector tiles
  - store to output hidden-state tensor

CUDA view

- gather-style kernel
- each block/warp processes one or more token positions
- threads load chunks of a row
- memory behavior is dominated by reads from HBM

Hardware / memory

- read `input_ids` and `W_embed` from HBM
- temporarily hold embedding fragments in registers
- write `X^(0)` to HBM

Output

- initial hidden states `X^(0)`
- next consumer: first Transformer layer


---

## 8. Full Prompt Execution: Prefill

During prefill, the full prompt of length `N` is processed across all layers.
This stage computes:

- hidden states for all prompt positions
- keys and values for all prompt positions
- final logits for the last prompt position

Prefill is expensive because all prompt tokens participate in layer computation.
Its output includes the first usable KV cache for later decode steps.

For each layer `ell = 0, 1, ..., L-1`, the input is:

```text
X^(ell) in R^(B x N x d_model)
```

and the layer produces:

```text
X^(ell+1)
```

through attention and MLP sub-blocks.

### 8.1 Attention RMSNorm

Deep dive: [08-1-attention-rmsnorm](08-1-attention-rmsnorm/README.md)

Input (origin)

- `X^(ell)` from the previous layer, or `X^(0)` from embeddings for `ell = 0`
- learned normalization scale `gamma_attn` loaded with the model weights

Process

- for each token vector independently:
  - compute RMS magnitude
  - divide by RMS
  - multiply by learned scale

Math

For a token vector `x in R^(d_model)`:

```text
RMS(x) = sqrt((1 / d_model) sum_j x_j^2 + epsilon)
```

```text
hat{x}_j = gamma_j * x_j / RMS(x)
```

Python view

```python
Xn = rmsnorm_attn(X)
```

Triton view

- row-wise normalization kernel
- one program instance often handles one row or one row tile
- reduction over `x^2`
- broadcast inverse RMS
- scale and store

CUDA view

- load row fragments from HBM
- accumulate partial sums in registers
- warp/block reduction via registers/shared memory
- normalize and scale per element

Hardware / memory

- read `X^(ell)` from HBM
- use registers and possibly shared memory for reductions
- write normalized tensor `Xn` to HBM

Output

- normalized hidden states `Xn`
- next consumer: QKV projection

### 8.2 QKV projection

Deep dive: [08-2-qkv-projection](08-2-qkv-projection/README.md)

Input (origin)

- `Xn` from attention RMSNorm
- trained weight `W_qkv`

Process

- apply one linear projection that produces concatenated Q, K, V
- split the output into three tensors
- reshape into head structure

Math

```text
QKV_flat = Xn W_qkv
```

with

```text
W_qkv in R^(d_model x 3 d_model)
```

Then split:

```text
Q_flat, K_flat, V_flat in R^(B x N x d_model)
```

and reshape:

```text
Q, K, V in R^(B x H x N x d_head)
```

Python view

```python
qkv = Xn @ W_qkv
q, k, v = qkv.chunk(3, dim=-1)
q = q.view(B, N, H, d_head).transpose(1, 2)
k = k.view(B, N, H, d_head).transpose(1, 2)
v = v.view(B, N, H, d_head).transpose(1, 2)
```

Triton view

- tiled GEMM:
  - rows correspond to `B * N`
  - reduction dimension is `d_model`
  - output columns correspond to `3 * d_model`

CUDA view

- GEMM or fused linear kernel
- Tensor Cores are typically used
- tiles staged in shared memory
- accumulation in registers

Hardware / memory

- read `Xn` and `W_qkv` from HBM
- stage tiles in shared memory
- perform matrix multiplications in Tensor Cores / registers
- write QKV output to HBM

Output

- `Q`, `K`, `V`
- next consumer: RoPE

### 8.3 Rotary position encoding (RoPE)

Deep dive: [08-3-rotary-position-encoding](08-3-rotary-position-encoding/README.md)

Input (origin)

- `Q` and `K` from QKV projection
- position indices `0, 1, ..., N-1` derived directly from token order in the prompt
- RoPE frequency tables or sin/cos tables prepared from model configuration

The position index is not a new hidden input.
It comes causally from the order of prompt tokens produced by tokenization.

Process

- rotate pairs of dimensions in `Q` and `K`
- use position-dependent angles
- leave `V` unchanged

Math

For each position `p` and pair `(2i, 2i+1)`:

```text
theta_(p,i) = p * omega_i
```

```text
[x'_(2i), x'_(2i+1)]^T
= [[cos theta, -sin theta],
   [sin theta,  cos theta]]
  [x_(2i), x_(2i+1)]^T
```

Python view

```python
q, k = apply_rotary_pos_emb(q, k, cos, sin, position_ids)
```

Triton view

- elementwise rotation kernel over `(b, h, t, i)` tiles

CUDA view

- each thread processes one or more dimension pairs
- pure register arithmetic
- no Tensor Core use

Hardware / memory

- read Q, K, sin, cos from HBM
- compute rotated pairs in registers
- write rotated Q and K to HBM

Output

- `Q_rot`, `K_rot`, `V`
- next consumer: self-attention

### 8.4 Causal self-attention over the prompt

Deep dive: [08-4-causal-self-attention](08-4-causal-self-attention/README.md)

Input (origin)

- `Q_rot`, `K_rot`, `V` from prior steps
- causal masking rule derived from token order

Process

- each query token attends only to current and earlier tokens
- compute score matrix
- apply causal mask
- softmax over keys
- weighted sum of values

Math

Per head:

```text
S = (Q K^T) / sqrt(d_head)
```

Apply causal masking:

```text
S_(i,j) = -infinity for j > i
```

Then:

```text
P = softmax(S)
```

and:

```text
O_attn = P V
```

Shapes:

```text
O_attn in R^(B x H x N x d_head)
```

Python view

```python
scores = (q @ k.transpose(-2, -1)) / math.sqrt(d_head)
scores = scores + causal_mask
probs = torch.softmax(scores, dim=-1)
O_attn = probs @ v
```

Optimized real-world view:

```python
O_attn = flash_attn(q, k, v, causal=True)
```

Triton view

- FlashAttention-style kernel:
  - processes Q tiles
  - streams K/V tiles
  - keeps row-wise softmax statistics in registers
  - avoids writing the full `N x N` score matrix to HBM

CUDA view

- fused attention kernel:
  - Q/K dot products computed on tile pairs
  - running max and running denominator maintained for stable softmax
  - partial weighted sums accumulated directly

Hardware / memory

- read Q/K/V from HBM
- stage K/V and sometimes Q tiles in shared memory
- accumulate scores and softmax statistics in registers
- write attention output to HBM

Output

- attention outputs `O_attn`
- next consumers:
  - output projection
  - KV cache write path for prompt tokens

### 8.5 KV cache write during prefill

Deep dive: [08-5-kv-cache-write-during-prefill](08-5-kv-cache-write-during-prefill/README.md)

Input (origin)

- rotated keys `K_rot`
- values `V`
- request-specific KV block table allocated earlier

Process

- store K and V for every prompt token and every layer
- these writes create the cache that decode will later reuse

Math

Conceptually:

```text
KV_K[ell, b, h, t, :] <- K_rot[ell, b, h, t, :]
KV_V[ell, b, h, t, :] <- V[ell, b, h, t, :]
```

Python view

```python
k_cache[layer, :, :, :N, :] = k_rot
v_cache[layer, :, :, :N, :] = v
```

In paged engines this becomes mapped writes via block tables.

Triton view

- scatter/store kernel using logical-to-physical mapping

CUDA view

- address computation in registers
- coalesced stores when possible

Hardware / memory

- source tensors in HBM
- destination KV cache in HBM

Output

- prompt KV cache populated
- next consumer: decode loop

### 8.6 Attention output projection

Deep dive: [08-6-attention-output-projection](08-6-attention-output-projection/README.md)

Input (origin)

- `O_attn`
- trained matrix `W_o`

Process

- concatenate heads back into hidden dimension
- project back into model dimension

Math

```text
O_concat in R^(B x N x d_model)
```

```text
O_proj = O_concat W_o
```

Python view

```python
out = O_attn.transpose(1, 2).contiguous().view(B, N, d_model)
O_proj = out @ W_o
```

Triton view

- tiled GEMM over flattened `B * N` rows

CUDA view

- GEMM on Tensor Cores

Hardware / memory

- read `O_attn` and `W_o` from HBM
- stage tiles in shared memory
- write `O_proj` to HBM

Output

- projected attention contribution `O_proj`
- next consumer: first residual add

### 8.7 Residual add after attention

Deep dive: [08-7-residual-add-after-attention](08-7-residual-add-after-attention/README.md)

Input (origin)

- original layer input `X^(ell)`
- attention contribution `O_proj`

Process

- elementwise add attention contribution into the residual stream

Math

```text
R^(ell) = X^(ell) + O_proj
```

Python view

```python
R = X + O_proj
```

Triton view

- elementwise add kernel

CUDA view

- vector add in registers

Hardware / memory

- read both tensors from HBM
- add in registers
- write `R^(ell)` to HBM

Output

- post-attention residual state `R^(ell)`
- next consumer: MLP RMSNorm

### 8.8 MLP RMSNorm

Deep dive: [08-8-mlp-rmsnorm](08-8-mlp-rmsnorm/README.md)

Input (origin)

- `R^(ell)` from residual path
- learned MLP normalization scale `gamma_mlp`

Process

- same RMSNorm procedure as before, now preparing the MLP input

Math

```text
Rn = RMSNorm(R^(ell))
```

Python view

```python
Rn = rmsnorm_mlp(R)
```

Triton / CUDA / Hardware

- same row-normalization structure as attention RMSNorm

Output

- normalized MLP input `Rn`

### 8.9 MLP block (SwiGLU style)

Deep dive: [08-9-mlp-block-swiglu](08-9-mlp-block-swiglu/README.md)

Input (origin)

- `Rn`
- trained matrices `W_up`, `W_gate`, `W_down`

Process

- project upward into expansion dimension
- project gate values
- apply SiLU to gate values
- multiply gate activation with expanded values
- project back down to model dimension

Math

```text
U = Rn W_up
G = Rn W_gate
SiLU(x) = x * sigmoid(x)
H = U ⊙ SiLU(G)
M = H W_down
```

Shapes:

```text
U, G, H in R^(B x N x d_ff)
M in R^(B x N x d_model)
```

Python view

```python
u = up_proj(Rn)
g = gate_proj(Rn)
h = u * F.silu(g)
M = down_proj(h)
```

Triton view

- usually a sequence of:
  - GEMM for `U`
  - GEMM for `G`
  - fused elementwise SiLU and multiply
  - GEMM for `M`

CUDA view

- GEMM kernels for linear projections
- elementwise gate kernel
- Tensor Cores for GEMMs

Hardware / memory

- read `Rn` and MLP weights from HBM
- stage GEMM tiles in shared memory
- use registers/Tensor Cores for computation
- intermediate tensors in HBM unless fused

Output

- MLP contribution `M`
- next consumer: second residual add

### 8.10 Residual add after MLP

Deep dive: [08-10-residual-add-after-mlp](08-10-residual-add-after-mlp/README.md)

Input (origin)

- `R^(ell)`
- MLP contribution `M`

Process

- add MLP contribution back into the residual stream

Math

```text
X^(ell+1) = R^(ell) + M
```

Python view

```python
X_next = R + M
```

Triton / CUDA / Hardware

- same elementwise add pattern as the prior residual add

Output

- next layer input `X^(ell+1)`


---

## 9. Final Prompt-Side Projection to Logits

After layer `L-1`, the prompt hidden states have passed through the full model.
To produce the first generated token, the server only needs the final hidden state at the last prompt position.

### 9.1 Final normalization

Deep dive: [09-1-final-normalization](09-1-final-normalization/README.md)

Input (origin)

- final layer output `X^(L)` from the prefill layer loop
- final norm scale from pretrained weights

Process

- normalize the final hidden state, typically at all positions even if only the last position will be used for first-token prediction

Math

```text
X_final = RMSNorm_final(X^(L))
```

Python view

```python
X_final = final_norm(X_last_layer)
```

Output

- normalized hidden states
- next consumer: LM head

### 9.2 LM head / vocabulary projection

Deep dive: [09-2-lm-head-and-vocabulary-projection](09-2-lm-head-and-vocabulary-projection/README.md)

Input (origin)

- final normalized hidden state at the last prompt position
- `W_vocab` loaded in GPU HBM

Process

- project from hidden dimension to vocabulary dimension

Math

For the last position hidden vector `h_last`:

```text
z = h_last W_vocab
```

where:

```text
z in R^V
```

Python view

```python
logits = h_last @ W_vocab
```

Triton / CUDA view

- matrix-vector or small GEMM style projection

Hardware / memory

- read hidden vector and vocabulary matrix from HBM
- compute logits on Tensor Cores / registers
- write logits to HBM

Output

- vocabulary logits for the next token
- next consumer: token selection

### 9.3 First next-token selection

Deep dive: [09-3-first-next-token-selection](09-3-first-next-token-selection/README.md)

Input (origin)

- logits produced from the prompt's final position
- generation parameters from the original request

Process

- optionally apply temperature scaling
- optionally apply top-k / top-p filtering
- select token via:
  - greedy argmax
  - multinomial sampling

Math

Temperature-scaled softmax:

```text
p_i = exp(z_i / T) / sum_j exp(z_j / T)
```

Greedy:

```text
token_next = argmax_i z_i
```

Sampling:

```text
token_next ~ p
```

Python view

```python
probs = torch.softmax(logits / temperature, dim=-1)
token_next = torch.multinomial(probs, num_samples=1)
```

or:

```python
token_next = torch.argmax(logits, dim=-1)
```

Triton / CUDA view

- reduction kernel for argmax
- or softmax + filtering + RNG-based sampling kernel chain

Hardware / memory

- logits in HBM
- reductions and sampling intermediates in registers/shared memory

Output

- first generated token ID
- this token becomes the causal input to the decode loop


---

## 10. Autoregressive Decode Loop

At this point the prompt has already been processed.
The critical state now available is:

- the current generated token ID
- the full prompt KV cache written during prefill
- runtime metadata describing sequence length and cache layout

The decode loop repeatedly performs:

```text
current token ID
-> embedding lookup
-> one-token forward pass through all layers
-> read historical KV
-> write new KV
-> produce next logits
-> select next token
-> stream text
-> continue or stop
```

### 10.1 Current token becomes next-step input

Deep dive: [10-1-current-token-as-next-step-input](10-1-current-token-as-next-step-input/README.md)

Input (origin)

- token selected in the previous step
- for the first decode iteration, this came from Section 9.3
- for later iterations, it came from the immediately previous decode step

Process

- treat the selected token as the known last token in the sequence
- make it available to device code

Output

- current token ID for decode step `t`

### 10.2 Device placement of current token

Deep dive: [10-2-device-placement-of-current-token](10-2-device-placement-of-current-token/README.md)

Input (origin)

- current token ID from prior decode selection

Process

- ensure token ID is in GPU HBM
- often this is a tiny host-to-device copy or reuse of a device-resident buffer

Output

- GPU-resident current token ID

### 10.3 One-token embedding lookup

Deep dive: [10-3-one-token-embedding-lookup](10-3-one-token-embedding-lookup/README.md)

Input (origin)

- current token ID in HBM
- `W_embed` in HBM

Process

- same row-gather as prompt embedding lookup, but only for one token position

Math

```text
x_t = W_embed[i_t, :]
```

with shape:

```text
x_t in R^(B x 1 x d_model)
```

Output

- current-token hidden state entering layer 0

### 10.4 Decode layer loop

Deep dive: [10-4-decode-layer-loop](10-4-decode-layer-loop/README.md)

For each layer `ell`, decode now differs from prefill in one crucial way:

- only the current token's hidden state is newly computed
- historical keys and values are reused from KV cache

The current token still executes:

- attention RMSNorm
- QKV projection
- RoPE
- attention
- output projection
- residual add
- MLP RMSNorm
- MLP
- second residual add

but attention now uses:

```text
current query q_t
against
historical K_(1:t), V_(1:t)
```

instead of recomputing earlier tokens.

### 10.5 Attention during decode

Deep dive: [10-5-attention-during-decode](10-5-attention-during-decode/README.md)

Input (origin)

- current query `q_t`
- current key/value `k_t`, `v_t`
- historical cached keys/values from prefill and prior decode steps
- KV block table describing where those historical tensors live in HBM

Process

- read historical K/V cache tiles
- compute attention scores between the current query and all past keys
- softmax over past positions
- weighted sum over past values

Math

For each head and past position `j <= t`:

```text
s_j = (q_t · k_j) / sqrt(d_head)
```

```text
p_j = exp(s_j) / sum_m exp(s_m)
```

```text
o_t = sum_j p_j v_j
```

This is cheaper than prefill because only one query is new.
Historical K/V are reused.

### 10.6 KV cache extension

Deep dive: [10-6-kv-cache-extension](10-6-kv-cache-extension/README.md)

Input (origin)

- current token's `k_t` and `v_t`
- KV block table from runtime state

Process

- write the new key and value into the cache location assigned to this request, layer, and token position

This is the causal bridge from decode step `t` to decode step `t+1`.
Without this write, the next step could not reuse current-token information.

### 10.7 Output projection, MLP, and residual path

Deep dive: [10-7-output-projection-mlp-and-residual-path](10-7-output-projection-mlp-and-residual-path/README.md)

All the same substeps from prefill still occur, but only on shape:

```text
B x 1 x d_model
```

instead of:

```text
B x N x d_model
```

The main cost shift in decode is:

- smaller compute on the current token path
- continued large memory reads for historical KV

As sequence length grows, decode often becomes increasingly memory-bandwidth limited.

### 10.8 Final norm, LM head, and token selection in decode

Deep dive: [10-8-final-norm-lm-head-and-token-selection-in-decode](10-8-final-norm-lm-head-and-token-selection-in-decode/README.md)

After all layers:

- normalize the current token hidden state
- project to vocabulary logits
- select the next token

This repeats the same final-token path as in Section 9, but now one step per generated token.


---

## 11. Detokenization and Streaming Back to the User

### 11.1 Device-to-host token transfer if needed

Deep dive: [11-1-device-to-host-token-transfer](11-1-device-to-host-token-transfer/README.md)

Input (origin)

- selected token ID from decode or initial next-token selection

Process

- move token ID to CPU memory if detokenization and response logic are CPU-side

Hardware / memory

- source: GPU HBM
- transport: PCIe or NVLink if needed
- destination: CPU RAM

Output

- token ID available to tokenizer decode logic

### 11.2 Token ID -> text fragment

Deep dive: [11-2-token-id-to-text-fragment](11-2-token-id-to-text-fragment/README.md)

Input (origin)

- token ID selected from logits
- tokenizer decode tables from server initialization

Process

- map token ID back to a text piece
- merge into byte/string output according to tokenizer decoding rules

Math

Conceptually:

```text
piece = vocab^(-1)(token_id)
```

though real tokenizers may involve merge logic and UTF-8 handling

Python view

```python
piece = tokenizer.decode([token_id])
```

Hardware / memory

- tokenizer decode tables in CPU RAM
- output string fragment in CPU RAM

Output

- generated text fragment
- next consumer: streaming/network logic

### 11.3 Stream fragment to the client

Deep dive: [11-3-stream-fragment-to-the-client](11-3-stream-fragment-to-the-client/README.md)

Input (origin)

- decoded text fragment from detokenization

Process

- append fragment to response buffer or stream
- send bytes to client over the active response channel

Hardware / memory

- CPU RAM response buffers
- NIC transmission path

Output

- fragment appears on the user's screen or client process


---

## 12. Stop Conditions and Loop Control

Deep dive: [12-stop-conditions-and-loop-control](12-stop-conditions-and-loop-control/README.md)

At the end of every generation step, the runtime checks:

- EOS token produced?
- `max_new_tokens` reached?
- stop sequence matched?
- client canceled?
- server-side budget or timeout hit?

If none of these stop conditions holds:

- the newly selected token becomes the next loop's current token
- decode repeats

Math

Autoregressive recurrence:

```text
x_(t+1) ~ P(x_(t+1) | x_1, x_2, ..., x_t)
```

The generated token is appended to the known sequence, and the next decode step conditions on the longer prefix.


---

## 13. Request Completion and Cleanup

### 13.1 Final assembled response

Deep dive: [13-1-final-assembled-response](13-1-final-assembled-response/README.md)

Input (origin)

- all generated token IDs and/or accumulated text fragments

Process

- optionally reconstruct the final text response from all generated pieces
- finalize logs and request metadata

Output

- completed response string in CPU RAM

### 13.2 Release KV cache resources

Deep dive: [13-2-release-kv-cache-resources](13-2-release-kv-cache-resources/README.md)

Input (origin)

- request-specific KV block ownership information
- reference counts or allocator metadata

Process

- decrement reference counts
- return fully released blocks to the free pool

Math

Conceptually:

```text
refcount(b) <- refcount(b) - 1
```

If:

```text
refcount(b) = 0
```

then block `b` becomes reusable.

Hardware / memory

- metadata in CPU RAM and possibly mirrored in GPU metadata buffers
- actual stale KV data in HBM may not be zeroed immediately; it is simply marked reusable

Output

- request memory reclaimed

### 13.3 Close response stream

Deep dive: [13-3-close-response-stream](13-3-close-response-stream/README.md)

Input (origin)

- request completion state

Process

- finalize stream or HTTP response
- release CPU-side response resources

Output

- request lifecycle ends


---

## 14. Why KV Cache Makes Generation Practical

Deep dive: [14-kv-cache-and-generation-complexity](14-kv-cache-and-generation-complexity/README.md)

This is the single most operationally important systems fact in autoregressive inference.

Without KV cache, generating token `t+1` would require recomputing:

- all hidden states for earlier tokens
- all keys for earlier tokens
- all values for earlier tokens

again and again.

With KV cache, step `t+1` computes only:

- current-token embedding
- current-token Q/K/V
- current-token per-layer forward pass

while reusing historical K/V already stored in HBM.

That is why long-context generation is feasible.

Operationally:

- prefill is compute-heavy because it processes the whole prompt
- decode is often memory-bandwidth-heavy because it repeatedly reads large historical KV while computing one token at a time


---

## 15. Python, Triton, and CUDA Views of the Same Pipeline

Deep dive: [15-python-triton-and-cuda-views](15-python-triton-and-cuda-views/README.md)

### Python / PyTorch view

This view is closest to model code:

- token IDs become tensors
- modules such as embeddings, RMSNorm, attention, MLP, and LM head are called in sequence
- generation loop applies logits processing and token sampling

This view is best for conceptual graph structure and tensor shapes.

### Triton view

This view expresses many substeps as custom kernels over tiles:

- row-wise RMSNorm kernels
- gather kernels for embeddings
- tiled GEMMs
- fused attention kernels
- elementwise fused gates and residual adds

This view is best for understanding kernel-level scheduling, fusion, and memory access structure.

### CUDA / hardware view

This view asks:

- where does each tensor live?
- how does it move across HBM, shared memory, registers?
- when are Tensor Cores used?
- when is the workload compute-bound versus memory-bound?

Typical mapping:

- embeddings: memory/gather dominated
- RMSNorm: reduction + elementwise arithmetic
- QKV / output / MLP projections: GEMM-heavy, Tensor Core dominant
- attention:
  - fused tile streaming
  - register/shared-memory softmax accumulation
  - often bandwidth-sensitive, especially in decode
- residual adds and gating: elementwise vector arithmetic


---

## 16. End-to-End Causal Summary

The full inference path, with explicit causal lineage, is:

1. the client sends UTF-8 bytes
2. the server parses them into a request object
3. the request string is tokenized on CPU using a preloaded tokenizer
4. token IDs are scheduled and assigned KV storage by the inference runtime
5. token IDs and runtime metadata are placed in GPU memory
6. token IDs index into the pretrained embedding table to create initial hidden states
7. each Transformer layer applies:
   - RMSNorm
   - QKV projection
   - RoPE using position indices derived from token order
   - causal attention
   - KV cache write
   - output projection
   - residual add
   - MLP normalization
   - MLP projections and gating
   - second residual add
8. final normalization and LM head produce vocabulary logits
9. logits are converted into the next token ID by argmax or sampling
10. that token is detokenized into a text fragment and streamed to the user
11. the token is also fed back into the decode loop
12. decode reuses all previously written KV cache and computes only the new token path
13. when a stop condition is reached, the response is finalized and KV resources are released

Nothing appears in the middle of this pipeline without an origin.
Every tensor used by a later step comes from:

- the user request,
- preloaded tokenizer/model state,
- runtime metadata,
- or the output of a previous explicit transformation.

That is the real end-to-end execution of Transformer inference on a serving system.
