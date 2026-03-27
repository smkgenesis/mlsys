# Binary Integers, Two's Complement, Sign Extension, and Hexadecimal

This note covers the introductory computer-architecture view of integer bit representations:
- unsigned binary integers,
- signed integers in two's complement,
- signed negation,
- sign extension,
- and hexadecimal notation.

The goal is not abstract number theory.
The goal is to understand how fixed-width machine words represent integer values inside a real ISA such as MIPS.

## Bits Need an Interpretation Rule

A bit string by itself is only a pattern of 0s and 1s.
It becomes a number only after the machine or programmer chooses an interpretation rule.

The same 32-bit pattern may be interpreted as:
- an unsigned integer,
- a signed integer,
- a memory address,
- an instruction encoding,
- or a collection of bit fields.

So the important question is not only:

```text
what bits are stored?
```

but also:

```text
how are those bits being interpreted?
```

## Unsigned Binary Integers

For an `n`-bit number

```text
x_(n-1) x_(n-2) ... x_1 x_0
```

the unsigned value is:

```text
x = x_(n-1) 2^(n-1) + x_(n-2) 2^(n-2) + ... + x_1 2^1 + x_0 2^0
```

Every bit contributes a nonnegative power of 2.

### Unsigned range

With `n` bits, the range is:

```text
0 to 2^n - 1
```

For 32 bits:

```text
0 to 4,294,967,295
```

### Example

```text
1011_2
```

means:

```text
1*2^3 + 0*2^2 + 1*2^1 + 1*2^0
= 8 + 0 + 2 + 1
= 11
```

The unsigned interpretation is the simplest one:
all bits are magnitude bits.

## Signed Integers in Two's Complement

To represent negative numbers efficiently, most modern machines use **two's complement**.

For an `n`-bit number

```text
x_(n-1) x_(n-2) ... x_1 x_0
```

the two's complement value is:

```text
x = -x_(n-1) 2^(n-1) + x_(n-2) 2^(n-2) + ... + x_1 2^1 + x_0 2^0
```

This means the highest bit has **negative weight**.
All lower bits keep their usual positive weights.

That highest bit is the **sign bit**.

## Signed Range in Two's Complement

With `n` bits, the range becomes:

```text
-2^(n-1) to 2^(n-1) - 1
```

For 4 bits:

```text
-8 to 7
```

For 32 bits:

```text
-2,147,483,648 to 2,147,483,647
```

Notice the asymmetry:
- there is one extra negative value,
- because zero uses one of the nonnegative encodings.

## Why Nonnegative Values Look the Same

If the sign bit is `0`, then the negative term disappears.

So any nonnegative number has the same bit pattern under:
- unsigned interpretation,
- and two's complement signed interpretation.

Example:

```text
0000...0010
```

means `2` in both systems.

The difference appears only when the highest bit is `1`.

## Important Special Two's Complement Values

For a 32-bit word:

- `0`

```text
0000 0000 ... 0000
```

- `-1`

```text
1111 1111 ... 1111
```

- most negative

```text
1000 0000 ... 0000
```

- most positive

```text
0111 1111 ... 1111
```

The most negative value is special because it does not have a positive counterpart within the same 32-bit signed range.

## Example of Two's Complement Interpretation

Consider the 4-bit pattern:

```text
1000_2
```

Under two's complement:

```text
-1*2^3 + 0*2^2 + 0*2^1 + 0*2^0
= -8
```

Now consider:

```text
0111_2
```

which becomes:

```text
0*2^3 + 1*2^2 + 1*2^1 + 1*2^0
= 4 + 2 + 1
= 7
```

So the full 4-bit signed range is:

```text
1000_2 = -8
...
1111_2 = -1
0000_2 = 0
...
0111_2 = 7
```

## Signed Negation

To negate a two's complement number:

1. complement all bits
2. add 1

That is:

```text
-x = (~x) + 1
```

where `~x` means bitwise complement.

### Example: negate `+2`

Start with:

```text
0000...0010
```

Complement all bits:

```text
1111...1101
```

Add 1:

```text
1111...1110
```

This is the representation of `-2`.

### Why this matters

This rule is not just a trick for hand calculation.
It explains why:
- subtraction can be implemented as addition of a negated operand,
- negative numbers integrate naturally into ordinary binary addition hardware,
- and two's complement is operationally convenient for real CPUs.

## Sign Bit

In a 32-bit signed integer, bit 31 is the sign bit:

- `0` means nonnegative
- `1` means negative

This does **not** mean the sign bit is just a separate label.
In two's complement it has actual numeric weight:

```text
-2^31
```

That is why the encoding behaves arithmetically in a useful way.

## Sign Extension

Often a machine must take a value represented in fewer bits and widen it into more bits.

The goal is:

```text
preserve the numeric value
```

For signed two's complement numbers, this is done by **replicating the sign bit to the left**.

### Example: +2 from 8 bits to 16 bits

```text
0000 0010
-> 0000 0000 0000 0010
```

### Example: -2 from 8 bits to 16 bits

```text
1111 1110
-> 1111 1111 1111 1110
```

This works because the widened number must preserve the same signed meaning, and the sign bit must keep dominating the newly added higher bits.

## Zero Extension for Unsigned Values

Unsigned values do **not** use sign extension.
They use **zero extension**.

That means:

```text
new higher bits = 0
```

Example:

```text
0000 0010
-> 0000 0000 0000 0010
```

This looks the same as the positive signed case, but the reason is different:
- unsigned extension preserves magnitude by adding zeros,
- signed extension preserves sign and value by copying the sign bit.

## Where Sign Extension Appears in MIPS

Sign extension appears in real instructions because the ISA often starts from smaller fields or smaller loads.

Common examples:
- `addi`: immediate field is widened before arithmetic
- `lb`, `lh`: loaded byte or halfword may be widened to a full word
- `beq`, `bne`: branch displacement is widened before address calculation

This is a hardware necessity:
the datapath usually wants full register-width operands, even if the source field in the instruction or memory object is smaller.

## Hexadecimal

Binary is exact, but it becomes too long to read comfortably.

Hexadecimal is a compact base-16 representation that maps naturally onto binary.

Each hex digit corresponds to exactly 4 bits:

```text
0 = 0000
1 = 0001
2 = 0010
3 = 0011
4 = 0100
5 = 0101
6 = 0110
7 = 0111
8 = 1000
9 = 1001
a = 1010
b = 1011
c = 1100
d = 1101
e = 1110
f = 1111
```

So an entire 32-bit word can be written using only 8 hex digits.

### Example

```text
eca86420
```

corresponds to:

```text
1110 1100 1010 1000 0110 0100 0010 0000
```

Hexadecimal matters in architecture because it preserves the exact bit pattern while remaining compact enough for:
- register dumps,
- memory dumps,
- instruction encodings,
- addresses,
- and debugging output.

## Why This Matters for Architecture

These representations are not just notation.
They determine what the hardware actually does when it:
- adds numbers,
- compares numbers,
- extends immediates,
- loads smaller objects into full registers,
- or interprets register contents as signed versus unsigned.

This is why ISA-level programming must distinguish carefully between:
- signed operations,
- unsigned operations,
- sign extension,
- zero extension,
- and interpretation of the same bit string under different rules.

## Why This Matters for ML Systems Later

Even though this is elementary architecture material, the same representational discipline returns later in ML systems:
- integer quantization depends on bit-level integer ranges,
- signed and unsigned storage matter in kernels,
- sign extension and zero extension matter when widening packed values,
- and hex is constantly used when debugging low-level memory contents or instruction traces.

So this topic is a small-scale version of a general systems lesson:

```text
machine-level performance and correctness depend on exact bit interpretation, not informal human intent.
```
