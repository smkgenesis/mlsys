# 3.1 Raw Byte Arrival

This folder is for the request-entry step where client bytes first become a server-visible request.

Upstream input:
- UTF-8 bytes sent by the client over the network

Downstream output:
- parsed request object in CPU RAM

Deep-dive focus:
- NIC -> kernel buffer -> user-space buffer path
- HTTP/WebSocket/gRPC parsing boundaries
- request object construction and validation
- where serving frameworks hide this step

Suggested local contents:
- protocol notes
- server code walkthroughs
- runtime traces
- diagrams of request ingress
