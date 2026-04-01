# MIPS ISA, Registers, Memory, and Immediate Operands

This note explains the specific scope covered in an introductory computer architecture treatment of MIPS:
- what an instruction set is,
- why MIPS is used as a teaching ISA,
- why arithmetic instructions use registers,
- how memory operands are accessed,
- why immediate operands exist,
- and why this all leads naturally to the idea of a load/store architecture.

The goal is not to cover the full MIPS ISA.
The goal is to make the causal structure of simple MIPS instructions explicit.

## Instruction Set

An instruction set is the repertoire of machine instructions a processor understands.

If a CPU implements a particular ISA, then software targeting that ISA can assume:
- a specific set of instructions exists,
- a specific register file exists,
- specific operand rules exist,
- and instructions are encoded and executed according to that ISA's rules.

Different processor families have different instruction sets, but many share the same broad categories of work:
- arithmetic,
- logical operations,
- memory access,
- conditional control flow,
- and jumps or procedure calls.

Early computers often had very simple instruction sets because simpler instructions were easier to implement in hardware.
Many modern ISAs also preserve a relatively simple structure because regularity helps decoding, implementation, and performance.

This is the spirit behind the textbook design principle:

```text
Simplicity favors regularity.
```

MIPS is a useful teaching ISA because its instruction forms are clean enough that the boundary between:
- software expression,
- machine instruction sequence,
- register usage,
- and memory access

is easy to see.

## Arithmetic Instructions Use Register Operands

MIPS arithmetic instructions operate on registers.

The typical form is:

```text
op destination, source1, source2
```

Examples:

```text
add $s1, $s2, $s3
sub $s1, $s2, $s3
```

Meaning:

```text
$s1 = $s2 + $s3
$s1 = $s2 - $s3
```

This regular three-operand form makes implementation simpler because many arithmetic instructions share the same structural pattern:
- read two source registers,
- send values through arithmetic logic,
- write one destination register.

## The Register File

MIPS has a register file with:

```text
32 registers x 32 bits
```

A 32-bit quantity is called a **word**.

Registers are named by number internally, but assembly uses readable aliases.

Examples:
- `$t0` through `$t9`: temporaries
- `$s0` through `$s7`: saved variables

Registers are used for values that are being actively computed on.
They are much smaller in number than main memory locations, and that is exactly why they are faster to access.

This motivates the second design principle:

```text
Smaller is faster.
```

Main memory is large, but that size makes it slower than the small register file near the processor datapath.

## Register Example

C code:

```c
f = (g + h) - (i + j);
```

Suppose:
- `f` is in `$s0`
- `g` is in `$s1`
- `h` is in `$s2`
- `i` is in `$s3`
- `j` is in `$s4`

Then MIPS code can be:

```text
add $t0, $s1, $s2
add $t1, $s3, $s4
sub $s0, $t0, $t1
```

The causal sequence is:
1. read `g` and `h` from `$s1` and `$s2`
2. place `g + h` in temporary register `$t0`
3. read `i` and `j` from `$s3` and `$s4`
4. place `i + j` in temporary register `$t1`
5. subtract the temporary results
6. store the final result in `$s0`

The important lesson is that a single high-level expression is decomposed into multiple simple register operations.

## Main Memory Is Used for Larger Data

Registers are not large enough to hold all program data.
Main memory is used for:
- arrays,
- structures,
- dynamically allocated objects,
- and generally any data set larger than the small register file.

But arithmetic instructions still want register operands.
So if a value lives in memory, the processor must:
1. load it into a register,
2. compute using registers,
3. and possibly store the result back to memory.

This is the beginning of the load/store architecture idea.

## Byte Addressing and Word Alignment

MIPS memory is byte addressed.

That means each memory address identifies one 8-bit byte.

A word is 4 bytes, so an array of words advances by 4 bytes per element.

If `A` is an array of 32-bit words, then:

```text
address(A[k]) = base(A) + 4k
```

Words are aligned in memory, meaning a word address must be a multiple of 4.

So word-sized accesses naturally occur at addresses like:

```text
0, 4, 8, 12, ...
```

not arbitrary byte boundaries.

## Load and Store Instructions

The core word-sized memory instructions are:

```text
lw destination, offset(base)
sw source, offset(base)
```

Meaning:

```text
lw $s1, 20($s2)   =>   $s1 = Memory[$s2 + 20]
sw $s1, 20($s2)   =>   Memory[$s2 + 20] = $s1
```

The effective memory address is formed by:

```text
effective_address = base_register + offset
```

So MIPS memory operands are explicit about two things:
- where the base address comes from,
- and how far away the desired word is.

## Memory Operand Example 1

C code:

```c
g = h + A[8];
```

Suppose:
- `g` is in `$s1`
- `h` is in `$s2`
- base address of array `A` is in `$s3`

Then:

```text
lw  $t0, 32($s3)
add $s1, $s2, $t0
```

Why `32`?

Because:

```text
A[8] is 8 words away from the base
each word is 4 bytes
offset = 8 x 4 = 32
```

The causal flow is:
1. compute the address of `A[8]` as `$s3 + 32`
2. load the word at that address into `$t0`
3. add `$s2` and `$t0`
4. write the result into `$s1`

## Memory Operand Example 2

C code:

```c
A[12] = h + A[8];
```

Suppose:
- `h` is in `$s2`
- base address of `A` is in `$s3`

Then:

```text
lw  $t0, 32($s3)
add $t0, $s2, $t0
sw  $t0, 48($s3)
```

Why `48`?

Because:

```text
12 x 4 = 48
```

The causal flow is:
1. load `A[8]` into `$t0`
2. add `h`
3. store the result into the memory location for `A[12]`

This is the classic load-compute-store pattern.

## Registers Versus Memory

Registers are faster than main memory.
That means a compiler tries to keep frequently used values in registers as much as possible.

If too many values are simultaneously live and there are not enough registers, some values must be placed in memory temporarily.
That process is often called **spilling**.

So performance depends strongly on register use:
- good register allocation reduces memory traffic,
- excessive spilling increases load/store instructions,
- and more memory traffic usually means slower execution.

This is why register optimization matters.

## Immediate Operands

Many instructions use small constants.
Instead of forcing the machine to load that constant from memory first, MIPS allows an immediate operand directly inside the instruction.

Example:

```text
addi $s3, $s3, 4
```

Meaning:

```text
$s3 = $s3 + 4
```

This is useful because small constants are common in real programs:
- incrementing counters,
- moving pointers,
- adjusting stack offsets,
- loop stepping.

The point is captured by another design principle:

```text
Make the common case fast.
```

If immediate operands did not exist, the machine would need extra instructions just to fetch or construct simple constants before arithmetic.

MIPS does not need a separate "subtract immediate" instruction because subtraction by a constant can be written as addition of a negative constant:

```text
addi $s2, $s1, -1
```

## The Constant Zero Register

MIPS register `$zero` always contains:

```text
0
```

and cannot be overwritten.

This is useful because zero appears constantly in machine code:
- clearing values,
- comparisons against zero,
- moves,
- address calculations.

Example:

```text
add $t2, $s1, $zero
```

Meaning:

```text
$t2 = $s1 + 0
```

So this instruction effectively copies `$s1` into `$t2`.

## Why This Becomes a Load/Store Architecture

All the pieces above combine into one central idea:

- arithmetic uses register operands,
- memory accesses are performed explicitly by load and store instructions,
- and composite data in memory must be moved into registers before computation.

That is the essence of a load/store architecture.

The CPU does not normally perform arithmetic directly on memory operands.
Instead, it follows this causal pattern:

```text
memory -> register
register arithmetic
register -> memory
```

This structure makes datapath design cleaner and keeps the arithmetic core focused on a small, fast register file.

## Why This Matters for ML Systems

This is not just an old ISA lesson.
The same systems intuition remains important in ML systems:

- small, close storage is fast,
- large storage is slower,
- movement of data often matters as much as arithmetic,
- and performance depends on keeping active operands near the compute units.

At a much larger scale, this reappears as:
- registers vs shared memory vs HBM on GPUs,
- cache and memory hierarchy behavior,
- bandwidth versus latency tradeoffs,
- and the cost of repeatedly loading data instead of reusing it locally.

So an introductory MIPS lesson is a clean small-scale version of a broader systems truth:

```text
computation is cheapest when the needed data is already in the fastest nearby storage.
```
