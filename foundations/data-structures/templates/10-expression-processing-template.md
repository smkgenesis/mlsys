# 10. Expression Processing Template

## What

This template captures the two stack algorithms introduced in DS08:

- postfix evaluation
- infix-to-postfix conversion

It is based on [09. Expression Processing with Stacks](/Users/minkyu/Documents/mlsys/foundations/data-structures/09-expression-processing-with-stacks.md).

## Core Rules

```text
postfix evaluation uses a stack of operands;
infix-to-postfix conversion uses a stack of operators.
```

The exam trap is mixing those two roles together.

## Canonical Java Skeleton

Code:
- [10-expression-processing-template.java](/Users/minkyu/Documents/mlsys/foundations/data-structures/templates/10-expression-processing-template.java)

This file includes:

- operator precedence helper
- postfix evaluation
- infix-to-postfix conversion
- `peek()` on the operator stack

## Postfix Evaluation Invariant

The lecture's evaluation loop is:

1. scan left to right
2. if token is an operand, push it
3. otherwise pop the required operands
4. perform the operation
5. push the result back
6. final stack item is the answer

The invariant to remember is:

```text
the operand stack contains the unfinished intermediate values
for the prefix scanned so far
```

## Postfix Evaluation Repair Rule

For a binary operator:

1. pop right operand
2. pop left operand
3. compute `left op right`
4. push result

The left/right order matters for subtraction and division.

## Infix-to-Postfix Invariant

The lecture's deeper insight is:

```text
operands pass directly to output;
operators wait on the stack until precedence and parentheses allow them to leave
```

That is the invariant worth reconstructing, not just the code.

## Infix-to-Postfix Core Algorithm

The lecture's stack algorithm is:

1. if token is an operand, output it
2. if token is `(`, push it
3. if token is `)`, pop until matching `(`
4. otherwise, while the top operator has higher or equal precedence, pop and output
5. then push the incoming operator
6. after the scan, pop and output the remaining operators

## Why `peek()` Matters

This algorithm needs to inspect the top operator before deciding whether to pop it.

So `peek()` is not optional.
It is part of the expression-processing template itself.

## Parentheses Rule

The lecture emphasizes two rules:

- left parenthesis is always pushed
- right parenthesis causes popping until the matching left parenthesis is removed

And the conceptual rule:

- in the stack, `(` behaves as lowest precedence
- outside the stack, `(` behaves as highest precedence

That is what makes it a barrier.

## Pressure Checklist

1. Am I evaluating postfix with an operand stack, not an operator stack?
2. When I pop for a binary operator, did I preserve left/right operand order?
3. In infix-to-postfix conversion, do operands go straight to output?
4. Am I using `peek()` before deciding whether to pop operators?
5. Did I treat `(` as a barrier rather than an ordinary operator?
6. At the end, did I flush the remaining operators from the stack?

## Common Mistakes

- Using one stack idea for both algorithms without separating operand and operator roles.
- Reversing the left and right operands during postfix evaluation.
- Forgetting to flush remaining operators at the end of infix-to-postfix conversion.
- Popping across a left parenthesis.
- Writing conversion code without `peek()`.
- Treating postfix as just a weird notation instead of an evaluation-friendly representation.

## Short Takeaway

For DS08, the key hand-coding template is delayed processing with stacks. Postfix evaluation keeps unfinished values on an operand stack, while infix-to-postfix conversion sends operands directly to output and holds operators on a stack until precedence and parentheses say they can be emitted.
