# 11.3 Stream Fragment to the Client

This folder is for the server-side step that ships decoded text fragments back to the client.

Upstream input:
- text fragments from [11.2](../11-2-token-id-to-text-fragment/README.md)

Downstream output:
- response bytes visible to the client

Deep-dive focus:
- streaming protocol details
- buffering and flush behavior
- partial-response delivery semantics
- user-visible latency

Suggested local contents:
- protocol notes
- server implementation notes
- latency experiments
- client-observable traces
