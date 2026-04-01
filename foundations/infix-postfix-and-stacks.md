# Infix, Postfix, and Stack Evaluation

## What

Infix and postfix are two different ways to write expressions.

In infix notation, the operator appears between its operands:

```text
A + B
(3 + 4) * 5
```

In postfix notation, also called Reverse Polish Notation (RPN), the operator appears after its operands:

```text
A B +
3 4 + 5 *
```

Both notations can represent the same computation.
The difference is how clearly the evaluation order is expressed.

## Why It Matters

This topic matters because expression notation is one of the simplest places where a data structure becomes essential to computation.

It connects:
- syntax,
- evaluation order,
- operator precedence,
- and stack behavior.

It is also a clean example of the difference between a human-friendly representation and a machine-friendly representation.

## Core Mechanism

### Infix

Infix notation is natural for humans, but it is not always self-explanatory.

For example:

```text
3 + 4 * 5
```

To interpret this correctly, a reader or program must know:
- precedence rules,
- associativity rules,
- and sometimes parentheses.

Without those rules, the expression is ambiguous.

### Postfix

Postfix notation removes that ambiguity by encoding the evaluation order directly.

For example:

```text
3 4 + 5 *
```

means:

```text
(3 + 4) * 5
```

No precedence table or parentheses are needed.

### Why stacks fit postfix evaluation

Postfix expressions are evaluated from left to right with a stack.

The general rule is:
- if the token is an operand, push it,
- if the token is an operator, pop the needed operands, apply the operator, and push the result back.

Example:

```text
3 4 + 5 *
```

Evaluation:
1. push `3`
2. push `4`
3. see `+`, pop `4` and `3`, compute `7`, push `7`
4. push `5`
5. see `*`, pop `5` and `7`, compute `35`, push `35`

Final result: `35`

The stack works because the most recently produced intermediate values are the next ones needed.

## Tradeoffs

Infix notation:
- easier for people to read,
- closer to normal mathematical writing,
- but requires precedence rules and parentheses handling.

Postfix notation:
- less natural at first,
- but unambiguous,
- simple to evaluate,
- and directly aligned with stack-based execution.

## Common Mistakes

- Thinking postfix is just a strange way to rewrite arithmetic instead of seeing that it encodes evaluation order explicitly.
- Forgetting that operator precedence is a separate rule required by infix notation, not by postfix notation.
- Popping operands from the stack in the wrong order for non-commutative operators such as subtraction or division.
- Treating the stack as a memorization trick rather than the mechanism that makes postfix evaluation work.

## ML Systems Connection

This topic is foundational rather than directly ML-specific, but it teaches an important systems habit:
separating a high-level representation from the execution strategy that makes it run efficiently.

That habit appears repeatedly in ML systems work:
- computation graphs are transformed before execution,
- compiler and runtime systems choose an execution order,
- and intermediate values must be materialized, consumed, or kept live according to a concrete evaluation strategy.

The immediate lesson is simple:

Infix notation emphasizes readability.
Postfix notation emphasizes execution order.

The deeper lesson is broader:

A good representation is not only about what is being computed.
It is also about how the structure of computation becomes executable.
