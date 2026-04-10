# 09. Expression Processing with Stacks

## What

This note captures the lecture's next concrete stack application:

- postfix notation,
- postfix evaluation,
- infix-to-postfix conversion,
- operator precedence,
- and parenthesis handling.

The lecture's message is that stacks are not only for storing values.
They are the natural machinery for processing nested and precedence-sensitive expressions.

## Why It Matters

The previous stack note introduced the ADT and a simple matching-parentheses example.

This lecture goes one step further.
It shows how stacks support a full expression-processing workflow:

- represent expressions in a form that is easy to evaluate,
- evaluate them using operand stacks,
- and convert ordinary infix expressions into that form by managing operators on a stack.

That is a major conceptual upgrade.

## Core Idea

The lecture is built around one recurring observation:

```text
operands can often flow directly forward,
while operators must wait until the right moment
```

That "waiting area" for operators is exactly what the stack provides.

## Infix and Postfix Notation

The lecture begins by contrasting:

- infix notation, where the operator sits between operands,
- and postfix notation, where the operator comes after its operands.

Examples in the lecture show how different parenthesizations in infix become different postfix strings.

The important structural point is:

- postfix notation removes ambiguity by placing each operator after the data it needs.

## Why Postfix Is Attractive

The lecture highlights two practical advantages:

- no need for parentheses,
- easy evaluation using stacks.

That is the key tradeoff.

Infix notation is human-friendly.
Postfix notation is machine-friendly.

## Evaluating Postfix Expressions

The lecture gives the general evaluation pattern:

1. initialize an empty stack of operands,
2. scan tokens from left to right,
3. if the token is an operand, push it,
4. otherwise pop the required operands,
5. perform the operation,
6. push the result back,
7. and at the end pop the final result.

This is one of the cleanest algorithm/data-structure matches in the course so far.

## Why Postfix Evaluation Works

The reason is simple:

- when an operator appears in postfix form,
- all operands for that operator have already appeared.

So the operand stack always contains the most recent unfinished values.
That is exactly what the algorithm needs.

## Converting from Infix to Postfix

The lecture then turns to the harder problem:

- given an infix expression,
- produce the equivalent postfix expression.

The lecture first explains this conceptually through a transformation process:

1. fully parenthesize,
2. move each operator just left of its matching right parenthesis,
3. delete the parentheses.

This explanation is useful because it shows what postfix notation is encoding.

## The Operand-Order Insight

One of the nicest observations in the lecture is:

- the order of operands remains the same

That means the conversion problem is really about operators, not operands.

Operands can pass directly to the output.
Operators are the items that must be delayed and reordered.

## Stack-Based Infix-to-Postfix Conversion

The lecture then gives the stack algorithm:

1. initialize an empty stack of operators,
2. scan tokens left to right,
3. if the token is an operand, output it immediately,
4. otherwise pop and output operators of higher or equal precedence,
5. then push the new operator,
6. after the scan ends, pop and output the remaining operators.

This is the central algorithmic contribution of the lecture.

## Why We Need Peek

The lecture explicitly adds:

- `peek`

The reason is straightforward.

During infix-to-postfix conversion, we must compare the incoming operator with the operator currently at the top of the stack.
That requires reading the top without removing it.

So `peek` becomes a natural extension of the stack interface.

## Parentheses Handling

The lecture then refines the conversion algorithm to handle parentheses.

The rules are:

- a left parenthesis is always pushed,
- a right parenthesis causes the stack to be popped until the matching left parenthesis is removed.

This is exactly the kind of delayed-release behavior that stacks are good at.

## Asymmetric Precedence of Left Parentheses

The lecture makes a subtle but important point:

- in the stack, a left parenthesis has the lowest precedence,
- outside the stack, it has the highest precedence.

This asymmetry is what makes the algorithm work cleanly.

It ensures that:

- an incoming left parenthesis is pushed immediately,
- and no earlier operator is popped across it.

So parentheses become explicit stack barriers.

## What This Note Is Really Teaching

This lecture is not just about notation conversion.
It is teaching a reusable pattern:

```text
when part of the input can move directly forward
but another part must wait for context,
a stack often provides the right delayed-processing mechanism
```

That is why expression processing is such a classic stack application.

## Common Mistakes

- Treating postfix notation as just a strange syntax rather than as an evaluation-friendly representation.
- Forgetting that postfix evaluation uses an operand stack, not an operator stack.
- Forgetting that operands preserve their left-to-right order in infix-to-postfix conversion.
- Popping operators without checking precedence.
- Using only `pop` when the algorithm really needs `peek`.
- Mishandling parentheses instead of treating them as stack barriers.

## Why This Matters for CS / Systems

This note matters because stack-based expression processing shows up far beyond homework expressions:

- parsing,
- interpreters,
- compilers,
- calculators,
- and runtime evaluation systems.

It is one of the first places in the branch where a simple ADT becomes a real language-processing tool.

## Short Takeaway

This lecture shows why stacks are the natural tool for expression processing. Postfix notation is easy to evaluate because operators appear only after their operands are available, so an operand stack is enough. Converting infix to postfix requires a second idea: operands go straight to output, while operators wait on a stack until precedence and parentheses say they can be emitted.
