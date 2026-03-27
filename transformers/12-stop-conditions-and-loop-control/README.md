# 12 Stop Conditions and Loop Control

This folder is for the logic that decides whether decode continues or terminates.

Upstream input:
- newly generated token
- request generation limits
- server-side cancellation and budget state

Downstream output:
- continue-decode or stop-request decision

Deep-dive focus:
- EOS handling
- max token limits
- stop-sequence matching
- cancellation and timeout behavior

Suggested local contents:
- control-flow notes
- runtime code references
- edge-case lists
- tests for stopping behavior
