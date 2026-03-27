# 10.2 Device Placement of Current Token

This folder is for making the current decode token available on the device.

Upstream input:
- current token ID from [10.1](../10-1-current-token-as-next-step-input/README.md)

Downstream output:
- GPU-resident current token ID

Deep-dive focus:
- tiny host-to-device copies
- device-side token buffers
- overlap with other runtime work

Suggested local contents:
- copy-path notes
- runtime code references
- microbenchmark notes
- stream-management observations
