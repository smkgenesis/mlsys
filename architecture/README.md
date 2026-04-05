# architecture

General computer architecture for ML systems.

This directory is for hardware ideas that are broader than GPU-only execution and broader than CUDA.

Belongs here:
- ISA basics,
- CPU-style performance models,
- memory hierarchy,
- cache and locality reasoning,
- bandwidth vs latency,
- multicore and hardware cost/performance tradeoffs.

Does not belong here:
- GPU-specific execution details that belong in `gpu/`,
- CUDA programming-model details that belong in `cuda/`.

Current notes:
- [Computer Architecture Overview and the Seven Great Ideas](computer-architecture-overview.md)
- [Performance Basics: Response Time, Throughput, CPI, and Clock Rate](performance-basics.md)
- [The Power Wall, Multiprocessors, and Performance Pitfalls](power-wall-and-multiprocessors.md)
- [MIPS ISA, Registers, Memory, and Immediate Operands](mips-isa-and-memory.md)
- [Binary Integers, Two's Complement, Sign Extension, and Hexadecimal](binary-integers-and-twos-complement.md)
- [MIPS Core Instruction Set Reference](mips-instruction-set-reference.md)
- [MIPS Pseudoinstructions and Calling Convention](mips-pseudoinstructions-and-calling.md)
