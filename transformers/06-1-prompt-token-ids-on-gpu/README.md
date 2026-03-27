# 6.1 Prompt Token IDs Copied to GPU

This folder is for the host-to-device movement that makes prompt token IDs available to device-side kernels.

Upstream input:
- CPU-resident `input_ids` from [4.2](../04-2-prompt-string-to-token-ids/README.md)

Downstream output:
- GPU-resident `input_ids`

Deep-dive focus:
- PCIe/NVLink transfer path
- pinned memory and async copies
- framework-managed vs explicit transfer APIs
- overlap opportunities with scheduler work

Suggested local contents:
- copy-path notes
- CUDA transfer examples
- PyTorch device-placement examples
- bandwidth measurements
