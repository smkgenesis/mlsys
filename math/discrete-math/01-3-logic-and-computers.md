# 01.3 Logic and Computers

## What

This note explains how propositional logic becomes physical computation inside a computer.

The key bridge is:

- logical truth values become bits,
- logical operators become logic gates,
- gates are composed into circuits,
- and circuits are layered into arithmetic hardware such as adders.

So this is not a separate topic from logic.
It is logic realized in hardware.

## Why It Matters

Propositional logic can feel abstract if it stays at the level of formulas and truth tables.

This note matters because it shows that logic is not just a language for proofs.
It is also the foundation of digital computation.

That connection matters for at least three reasons:

- it explains why formal logic matters in computer science,
- it introduces abstraction as a systems-design principle,
- and it shows how simple operators compose into real computational structures.

This is one of the clearest examples of a recurring systems idea:

```text
complex machines are built by composing simpler components through clean interfaces
```

## Core Idea

The core chain in this note is:

```text
truth values -> bits -> gates -> circuits -> arithmetic units -> larger computer systems
```

Each step preserves a logical interpretation while raising the level of abstraction.

At the lowest level:

- `0` corresponds to false
- `1` corresponds to true

At the next level:

- logical operators such as `AND`, `OR`, and `NOT` are implemented by physical gates

Then:

- gates are connected into circuits,
- circuits implement useful Boolean functions,
- and those functions can be composed into arithmetic components such as adders.

So the big point is that computers are not “beyond” logic.
They are layered realizations of logic.

## Logic Gates

Logic gates are the basic hardware components that implement Boolean operations.

Examples:

- `NOT`
- `AND`
- `OR`
- `XOR`
- `NAND`

Each gate:

- takes one or more input bits,
- applies a logical rule,
- and produces an output bit.

This is the hardware version of a truth table.

## From Gates to Circuits

A single gate is rarely the end goal.
The important move is composition.

The output of one gate can become the input of another, allowing many simple logical steps to combine into a more complex computation.

For example, `XOR` can be expressed as:

```text
x ⊕ y ≡ (x ∧ ¬y) ∨ (¬x ∧ y)
```

This matters because it means a circuit for `XOR` can be built from simpler gates such as:

- `AND`
- `OR`
- `NOT`

So a logical equivalence is also a hardware construction recipe.

## Abstraction and Hierarchical Design

This note makes a broader point that computer science is built on abstraction.

Abstraction means:

- hiding low-level implementation details,
- exposing only the behavior needed by the next layer,
- and composing systems layer by layer.

The important hierarchy here is:

- gates build XOR and AND behavior
- XOR and AND build a half adder
- half adders and carry logic build a full adder
- full adders chain into an `n`-bit adder
- arithmetic blocks compose into larger machine components such as the ALU and CPU

This is the systems-design lesson embedded in the chapter.

## Functional Completeness and NAND

A set of logical operators is **functionally complete** if every Boolean function can be expressed using only operators from that set.

The familiar set:

- `AND`
- `OR`
- `NOT`

is functionally complete.

But the stronger and more surprising result is that `NAND` alone is functionally complete.

That means:

- `NOT` can be built from `NAND`
- `AND` can be built from `NAND`
- `OR` can also be built from `NAND`

So one gate type is enough to construct arbitrary digital logic.

This matters both conceptually and practically:

- conceptually, it shows how much expressive power can come from one primitive,
- practically, it explains why manufacturing and circuit design can standardize around a single gate family.

## Binary Representation

To perform arithmetic, computers represent numbers in binary.

Each bit position corresponds to a power of two:

- the rightmost bit is `2^0`
- the next is `2^1`
- and so on

So a binary numeral is interpreted as a weighted sum of powers of two.

This matters because arithmetic hardware is ultimately operating on bit patterns, not on abstract decimal numerals.

## Half Adder

A half adder is the simplest circuit for adding two bits.

Inputs:

- `x`
- `y`

Outputs:

- sum bit `s`
- carry bit `c`

The logic is:

```text
s = x ⊕ y
c = x ∧ y
```

So the half adder is the first clear example of arithmetic emerging directly from logical operations.

## Full Adder

A half adder is not enough for multi-bit addition because real addition also needs to handle an incoming carry from the previous bit position.

A full adder therefore adds:

- `x`
- `y`
- carry-in `c_in`

and produces:

- sum `s`
- carry-out `c_out`

This can be built hierarchically from simpler components, typically by combining half adders and carry logic.

That is important because it shows how arithmetic circuits are designed:

- not from scratch,
- but by composing previously defined modules.

## Ripple-Carry Adder

To add two `n`-bit numbers, we can chain `n` full adders together.

The carry-out from one position becomes the carry-in for the next higher position.

This is called a **ripple-carry adder** because the carry signal propagates through the chain from low-order bits to high-order bits.

This is another good example of layered composition:

- bit-level logic
- becomes 1-bit addition
- which becomes multi-bit addition

The main systems intuition is that hierarchical composition gives power, but it also introduces performance structure.
Here, correctness is easy to understand, but the ripple of carry signals also hints at later timing and latency concerns in hardware design.

## What This Note Is Really Teaching

The note is doing more than introducing gates and adders.
It is teaching three ideas at once:

1. logic has a direct physical interpretation in digital hardware
2. abstraction is the main tool for managing complexity
3. powerful systems can be built by composing a very small set of primitives

That combination is what makes this a natural continuation of propositional logic.

## Common Mistakes

- Thinking logic is only about symbolic proof and not about hardware.
- Treating bits as merely numeric storage rather than truth-valued signals manipulated by logical operators.
- Missing the role of abstraction and seeing half adders, full adders, and multi-bit adders as unrelated designs.
- Assuming `NAND` is just another gate instead of recognizing that it is functionally complete.
- Thinking arithmetic circuits are fundamentally different from logical circuits, when in fact they are built from them.

## Why This Matters for ML Systems

ML systems are usually discussed at much higher abstraction levels, but those abstractions still sit on digital hardware built from Boolean structure.

This note matters because it strengthens intuition for:

- hardware-software abstraction boundaries,
- the composition of simple primitives into larger execution units,
- why operator implementations ultimately reduce to structured bit-level computation,
- and why systems thinking depends so heavily on modular design.

It does not teach accelerator kernels directly.
What it does teach is the compositional mindset that later helps when reasoning about:

- arithmetic units,
- tensor cores,
- memory systems,
- and the stack of abstractions that modern ML software rests on.

## Short Takeaway

Computers are logic machines: truth values become bits, logical operators become gates, gates compose into circuits, and those circuits build arithmetic hardware such as adders. The deeper lesson is that digital systems are built by abstraction and composition, with even complex computation emerging from a small set of logical primitives.
