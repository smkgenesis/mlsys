# 3.1 Raw Byte Arrival

## What

This step is the ingress boundary where bytes from outside the server first become a structured, server-visible inference request.

At this point:

- no tokenization has happened
- no model computation has happened
- no GPU work has started
- no KV cache state exists for this request

The output of this step is not tokens or tensors. It is a parsed request object in CPU RAM that the inference runtime can validate, enqueue, and pass downstream.

## Why It Matters

Many explanations of transformer inference start at:

```text
prompt string -> tokenizer
```

That skips a real systems boundary.

Before there is a prompt string inside the server, there are only:

- bytes arriving from the network
- protocol framing
- kernel-managed buffers
- user-space parsing
- validation and request-object construction

This step matters because it creates the first application-level object that the rest of the inference pipeline can operate on.

## Input (Origin)

The upstream input is:

- UTF-8 encoded bytes sent by a client over the network

The client may be:

- a browser
- a CLI tool
- an SDK caller
- another internal service

At the moment of arrival, the server does not yet have:

- a prompt string object
- token IDs
- generation metadata in application form

It only has network-delivered bytes and connection state.

## Consumed Object

The object consumed by this step is:

- a protocol-framed byte stream or message payload

Depending on the serving stack, that may mean:

- an HTTP request body
- a WebSocket message payload
- a gRPC/protobuf message payload

So the consumed object is not yet a model-input object. It is a transport/protocol object represented as bytes.

## Produced Object

The produced object is:

- a parsed and validated request object in CPU RAM

Typical fields include:

- `prompt: str`
- `max_new_tokens`
- `temperature`
- `top_p`
- `top_k`
- stop conditions
- request ID
- model ID
- user or session metadata
- streaming flags

This request object is the first structured inference object visible to the server runtime.

## Process

The process in this step is:

1. the NIC receives packets
2. the OS kernel networking stack reconstructs the byte stream or message
3. bytes are stored in kernel-managed buffers
4. the user-space server reads those bytes
5. the serving framework parses the application protocol
6. the payload is decoded into structured fields
7. validation checks run
8. a request object is allocated and populated in CPU RAM

This is the causal path from raw network ingress to application-visible request state.

## Math

There is essentially no transformer math here.

There is no:

- embedding lookup
- attention
- normalization
- matrix multiplication
- logits computation

The only “computation” is systems-side parsing and validation:

- byte decoding
- message parsing
- field extraction
- range checks
- schema checks

So for this step, the mathematical view is intentionally thin.

## Runtime Layer

This step spans several runtime layers:

### 1. Hardware / device ingress

- NIC receives packets from the network

### 2. OS kernel networking layer

- TCP/TLS handling
- packet reassembly
- socket buffering
- kernel-visible request bytes

### 3. User-space serving framework

- reads from socket or framework-managed connection object
- parses HTTP / WebSocket / gRPC framing
- extracts the payload

### 4. Application request layer

- decodes payload fields
- validates request arguments
- constructs the application-level request object

The inference runtime becomes meaningfully involved only at the end of this chain, once the parsed request object exists.

## Data Location Before

Before this step completes, the relevant data lives in:

- client memory before transmission
- network packets in transit
- NIC receive buffers
- OS kernel networking buffers
- user-space byte buffers inside the serving process

At these points, the data is still not yet an inference request in application form.

## Data Location After

After this step completes, the relevant data lives in:

- CPU RAM inside the serving process
- as a parsed request object and associated metadata

This object is now available to:

- tokenizer logic
- request scheduler
- admission control
- batching logic
- and later GPU-preparation stages

## How This Becomes the Next Step's Input

The output of this step becomes the input to the next transformer-serving stages as follows:

- the parsed `prompt: str` becomes input to tokenizer-state logic and prompt-to-token-ID conversion
- generation parameters become part of downstream scheduling and decode policy
- request metadata becomes part of admission, batching, tracing, and lifecycle management

So the next step does not consume raw bytes anymore. It consumes a structured CPU-side request object whose most important payload field is the prompt string.

## Python / PyTorch View

At the Python/application level, this step usually looks like:

- request handler receives a request object from a web framework
- handler extracts fields such as `prompt`, `temperature`, and `max_new_tokens`
- validation runs
- an internal request structure is created

Conceptually:

```python
request = Request(
    prompt=decoded_prompt,
    max_new_tokens=max_new_tokens,
    temperature=temperature,
    top_p=top_p,
    stream=stream,
    model=model_name,
    request_id=req_id,
)
```

No PyTorch tensor work is necessary yet.

## Triton-Kernel View

There is no Triton kernel involved at this step.

Reason:

- the data has not reached GPU tensor form
- no tile ownership exists
- no pointer arithmetic over model tensors exists
- no GPU-side numerical operator has begun

So the Triton view here is:

```text
not applicable yet
```

That absence is itself important, because it marks this step as fully upstream of device-side kernel execution.

## CUDA / Hardware-Memory View

There is no CUDA kernel execution yet.

From a hardware-memory perspective, the interesting path is:

- NIC receive path
- kernel buffers
- user-space CPU buffers
- application object allocation in host memory

GPU HBM is not touched yet.

So this step belongs to request ingress and orchestration, not to model execution.

## Common Hidden Assumptions

This step is often mentally collapsed away because frameworks hide it well.

Common hidden assumptions include:

- assuming the server “starts” with a prompt string
- forgetting protocol parsing and validation costs
- forgetting that malformed requests can fail before tokenization
- forgetting that request metadata is created here, not later
- forgetting that this step is entirely CPU-side

Those assumptions make the later inference path look cleaner than it really is.

## Failure Modes

Important failures that can occur here include:

- malformed HTTP / WebSocket / gRPC framing
- invalid UTF-8
- invalid JSON or protobuf payloads
- missing required fields
- illegal generation parameters
- model-routing failures
- request-size or quota violations

All of these are upstream of tokenization and model execution.

## ML Systems Connection

This step is operational rather than mathematical, but it is still part of transformer inference as a system.

It matters because:

- it defines the first structured request object
- it determines when the scheduler can first see the request
- it gates all downstream tokenization and GPU execution
- it contributes to end-to-end latency even though no model math happens yet

For real serving systems, mastering this boundary is part of understanding inference as a full pipeline rather than only as layer math.

## Short Takeaway

Raw byte arrival is the ingress step where protocol-framed client bytes move through the NIC, kernel networking stack, and user-space parser, then become a validated request object in CPU RAM whose prompt string and generation metadata feed the next stages of tokenization and scheduling.
