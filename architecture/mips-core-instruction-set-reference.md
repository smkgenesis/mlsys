# MIPS Core Instruction Set Reference

This note organizes the introductory MIPS reference sheet into a causal, learnable structure.

The goal is not to memorize a table mechanically.
The goal is to understand:
- what kinds of work the ISA must support,
- how MIPS groups those operations,
- how the instruction formats relate to the work being done,
- and why this small core instruction set is enough to express ordinary programs.

This note stays within the introductory scope of the core MIPS instruction sheet:
- arithmetic,
- logical operations,
- comparisons,
- shifts,
- memory access,
- branches,
- jumps,
- upper-immediate construction,
- and the `ll/sc` atomic pair.

## What an Instruction Reference Sheet Is Showing

A MIPS reference sheet usually gives four kinds of information for each instruction:

- **mnemonic**:
  the assembly name such as `add`, `lw`, or `beq`
- **format**:
  the structural encoding class such as `R`, `I`, or `J`
- **operation**:
  the architectural meaning of the instruction
- **opcode/funct fields**:
  how the instruction is encoded in bits

So one row in the table is simultaneously:
- a programming-language surface form,
- a machine operation,
- and a bit-level encoding rule.

## The Three Main Instruction Formats

### R-format

R-format is used when the instruction primarily operates on register values.

Typical fields include:
- `rs`
- `rt`
- `rd`
- `shamt`
- `funct`

Core idea:

```text
read registers -> compute -> write register
```

Examples:
- `add`
- `sub`
- `and`
- `or`
- `nor`
- `slt`
- `sltu`
- `sll`
- `srl`
- `jr`

### I-format

I-format is used when the instruction contains a 16-bit immediate field.

That immediate may represent:
- a small constant,
- a memory offset,
- a branch displacement,
- or data used to build a larger constant.

Core idea:

```text
register state + immediate field
```

Examples:
- `addi`, `addiu`
- `andi`, `ori`
- `lw`, `sw`, `lb`, `lbu`, `lh`, `lhu`, `sb`, `sh`
- `beq`, `bne`
- `slti`, `sltiu`
- `lui`
- `ll`, `sc`

### J-format

J-format is used for long unconditional jumps.

Examples:
- `j`
- `jal`

Core idea:

```text
replace PC using a jump-target field
```

## Arithmetic Instructions

Arithmetic instructions operate on register values.

### `add`

```text
R[rd] = R[rs] + R[rt]
```

### `sub`

```text
R[rd] = R[rs] - R[rt]
```

### `addi`

```text
R[rt] = R[rs] + SignExtImm
```

This is the immediate form of addition.
It uses a sign-extended constant that is already stored inside the instruction.

### `addu`, `addiu`, `subu`

These are often described as the unsigned forms.
The important introductory point is not only "signed versus unsigned data."
The key architectural distinction is that these forms avoid signed overflow exception behavior.

So in an ISA discussion, "unsigned" often means:

```text
interpretation and overflow semantics differ
```

not simply "the bit pattern belongs to an unsigned variable in C."

## Logical Instructions

Logical instructions operate bit by bit.

### `and`

```text
R[rd] = R[rs] & R[rt]
```

### `or`

```text
R[rd] = R[rs] | R[rt]
```

### `nor`

```text
R[rd] = ~(R[rs] | R[rt])
```

### `andi`

```text
R[rt] = R[rs] & ZeroExtImm
```

### `ori`

```text
R[rt] = R[rs] | ZeroExtImm
```

These are especially useful when manipulating masks and bit fields.

One important detail is:
- `addi` uses **sign extension**
- `andi` and `ori` use **zero extension**

That difference affects the actual 32-bit value given to the ALU.

## Comparison Instructions

Comparison instructions turn a relation into a register value.

### `slt`

```text
R[rd] = (R[rs] < R[rt]) ? 1 : 0
```

Signed comparison.

### `sltu`

```text
R[rd] = (R[rs] < R[rt]) ? 1 : 0
```

but interpreted as an unsigned comparison.

### `slti`

```text
R[rt] = (R[rs] < SignExtImm) ? 1 : 0
```

### `sltiu`

Unsigned-style compare against an immediate.

The important systems lesson is that comparison does not always directly branch.
Sometimes the ISA first turns the comparison into a value `0` or `1`, and later instructions decide what to do with that result.

## Shift Instructions

### `sll`

```text
R[rd] = R[rt] << shamt
```

### `srl`

```text
R[rd] = R[rt] >> shamt
```

These move bits left or right by a constant amount.

They are useful for:
- multiplying or dividing by powers of two,
- field extraction,
- bit packing,
- address scaling,
- and low-level integer manipulation.

The presence of `shamt` in the R-format reminds you that not every register instruction is just "two sources, one destination."
Some instructions need an embedded shift amount instead.

## Memory Access Instructions

MIPS is a load/store architecture.

That means arithmetic normally does **not** read operands directly from memory.
Instead:
- loads move memory data into registers,
- stores move register data back to memory.

### Load instructions

#### `lw`

```text
R[rt] = M[R[rs] + SignExtImm]
```

Load a full word.

#### `lb`

Load a byte and sign-extend it.

#### `lbu`

Load a byte and zero-extend it.

#### `lh`

Load a halfword and sign-extend it.

#### `lhu`

Load a halfword and zero-extend it.

### Store instructions

#### `sw`

```text
M[R[rs] + SignExtImm] = R[rt]
```

Store a full word.

#### `sb`

Store only the low byte of `R[rt]`.

#### `sh`

Store only the low halfword of `R[rt]`.

### Effective address

All of these follow the same addressing idea:

```text
effective address = base register + offset
```

So in assembly like:

```text
lw $t0, 32($s3)
```

the CPU:
1. reads the base address from `$s3`
2. sign-extends the immediate `32`
3. adds them
4. uses the result as the memory address

## Branch Instructions

Branches change control flow conditionally.

### `beq`

```text
if (R[rs] == R[rt]) PC = PC + 4 + BranchAddr
```

### `bne`

```text
if (R[rs] != R[rt]) PC = PC + 4 + BranchAddr
```

These are **PC-relative branches**.

That means the instruction does not store a full destination address directly.
Instead it stores a displacement that is interpreted relative to the current instruction stream.

This keeps the encoding compact and makes nearby control-flow changes efficient.

## Jump Instructions

Jumps change control flow unconditionally.

### `j`

```text
PC = JumpAddr
```

### `jal`

```text
R[31] = PC + 8
PC = JumpAddr
```

This is used for procedure call.
`R[31]` is the return address register, also written as `$ra`.

### `jr`

```text
PC = R[rs]
```

This is commonly used to return from a procedure:

```text
jr $ra
```

So the causal structure of a function call in MIPS is:
1. `jal` saves where control should return
2. execution moves to the callee
3. `jr $ra` restores control to the saved location

## `lui`: Load Upper Immediate

### `lui`

```text
R[rt] = {imm, 16'b0}
```

This places the 16-bit immediate into the upper 16 bits of the register and fills the lower 16 bits with zero.

Why does this exist?

Because normal immediates are only 16 bits wide, but registers are 32 bits wide.
To build a full 32-bit constant, MIPS often uses a two-step pattern:

```text
lui $t0, upper16
ori $t0, $t0, lower16
```

So `lui` is part of constant construction.

## `ll` and `sc`: Atomic Update Pair

### `ll`

Load linked.

### `sc`

Store conditional.

These two instructions are designed to work together for atomic synchronization.

The high-level idea is:
1. `ll` reads a location and marks it as part of an atomic attempt
2. `sc` stores back only if that attempt is still valid

Architecturally, `sc` also returns a success/failure indicator in a register.

This makes it possible to build:
- atomic updates,
- locks,
- synchronization primitives,
- and concurrent data structure operations.

At an introductory level, the main lesson is simply:

```text
ordinary load/store is not enough for safe concurrent update
```

so the ISA provides a special pair for that purpose.

## Why the Footnotes Matter

The small notes on a reference sheet often contain the semantics that students miss first.

Important examples include:

### Sign extension

Many I-format instructions interpret the immediate as:

```text
SignExtImm = {16{immediate[15]}, immediate}
```

So a 16-bit immediate becomes a full 32-bit signed quantity before use.

### Zero extension

Logical immediate instructions such as `andi` and `ori` use:

```text
ZeroExtImm
```

so new high bits are zeros, not copies of the sign bit.

### Branch address formation

Branches do not jump to an arbitrary raw immediate.
They form a PC-relative branch target from the current instruction address and the immediate displacement.

### Jump address formation

Jump instructions also construct the target address, rather than storing a full arbitrary 32-bit value directly in the instruction.

### Unsigned comparisons

Instructions like `sltu` and `sltiu` compare operands using unsigned interpretation.
That means the same bit pattern may lead to a different comparison result than in signed mode.

## The Best Way to Organize the Sheet Mentally

Instead of memorizing one long alphabetical list, organize the instructions by job.

### Register arithmetic

- `add`
- `addu`
- `sub`
- `subu`

### Immediate arithmetic

- `addi`
- `addiu`

### Bitwise logic

- `and`
- `andi`
- `or`
- `ori`
- `nor`

### Comparison

- `slt`
- `sltu`
- `slti`
- `sltiu`

### Shifts

- `sll`
- `srl`

### Memory access

- `lw`
- `sw`
- `lb`
- `lbu`
- `lh`
- `lhu`
- `sb`
- `sh`

### Control flow

- `beq`
- `bne`
- `j`
- `jal`
- `jr`

### Special utility and synchronization

- `lui`
- `ll`
- `sc`

This grouping reflects what the CPU is doing much more clearly than raw table order.

## What You Should Internalize First

If you are studying this reference sheet for the first time, the most important backbone is:

1. arithmetic mostly uses register operands
2. memory is accessed only through explicit loads and stores
3. immediate fields let common small constants live inside instructions
4. branches and jumps handle control flow explicitly
5. signedness matters in comparison, extension, and overflow behavior

Once those are clear, the individual rows stop looking like unrelated trivia.

## Why This Matters for ML Systems

This is an old ISA, but the systems lesson is still alive:
- active operands should live in the fastest storage possible
- data movement rules are explicit and costly
- the representation of bits changes the meaning of arithmetic and comparison
- small instruction-level regularity makes large systems easier to implement efficiently

At a much larger scale, the same logic appears later in GPU systems:
- register operands versus HBM-resident tensors
- explicit movement between storage levels
- signed versus unsigned interpretation in kernels
- control-flow costs
- and the importance of regular execution structure

So the MIPS core instruction sheet is not just historical assembly trivia.
It is a compact model of how hardware wants computation to look.
