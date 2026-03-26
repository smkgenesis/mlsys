# Recursion and Explicit Stack Simulation

## What

Recursion and an explicit stack are two ways to express the same control structure.

In a recursive program, the language runtime stores unfinished function calls on the call stack.
In an explicit-stack program, the programmer stores unfinished work in a stack data structure and resumes it manually.

The key idea is that a recursive call frame is not magic. It is just suspended work plus enough saved state to continue later.

## Why It Matters

This topic is important because many students learn recursive code as a pattern without understanding what the runtime is really doing.

Once the hidden structure becomes visible, recursion becomes easier to reason about:
- why recursive calls return to the correct place,
- why LIFO order is required,
- what a stack frame must remember,
- and how a recursive algorithm can be rewritten iteratively when needed.

This is one of the cleanest bridges between data structures and program execution.

## Core Mechanism

A recursive function call must preserve three kinds of information:
- the arguments for that call,
- any local state needed after a subcall returns,
- and the point in the function where execution should resume.

That preserved bundle is a stack frame.

When a function calls itself, the current call is suspended while the smaller subproblem runs.
After the subproblem finishes, execution resumes from the saved return point.

This is why the call stack behaves like a stack:
- the most recent unfinished call must resume first.

An explicit-stack version makes this visible.

Instead of relying on the runtime, the program creates a context object such as:

```java
class Context {
    int n;
    int from;
    int to;
    int whereToReturn;
}
```

The fields represent:
- the current problem instance,
- and the stage of execution that should continue after a subcall completes.

For example, a recursive function with three phases:
1. do the first recursive call,
2. perform local work,
3. do the second recursive call,

can be simulated by storing a context with states like:
- `0`: just entered the call,
- `1`: first recursive call finished, resume in the middle,
- `2`: second recursive call finished, the call is done.

The program then repeatedly:
- inspects the current context,
- pushes suspended contexts before descending into a smaller subproblem,
- and pops a saved context when a subproblem is complete.

This is exactly what the runtime call stack does for ordinary recursion.

## Tower of Hanoi as the Example

Tower of Hanoi is a good teaching example because its recursive structure is unusually clean.

To move a tower of `n` disks from one rod to another:
1. move the top `n - 1` disks away,
2. move the largest disk,
3. move the `n - 1` disks back on top.

The recursive version hides the stack management inside function calls.

The explicit-stack version exposes it:
- push the current problem with a saved resume point,
- switch to the first smaller subproblem,
- resume and perform the middle move,
- push again with a later resume point,
- switch to the second smaller subproblem,
- and finally resume the suspended call and finish it.

This makes the recursive structure concrete instead of mysterious.

## Tradeoffs

Recursive version:
- shorter and easier to read when the recursive structure is simple,
- naturally mirrors the mathematical decomposition of the problem,
- but hides control-flow details in the runtime stack.

Explicit-stack version:
- more verbose,
- requires manual management of context and resume states,
- but reveals the execution model directly and can avoid deep recursion limits.

The explicit-stack form is often better for understanding, debugging, or converting recursion into an iterative algorithm.

## Common Mistakes

- Treating recursion as a memorized coding pattern instead of identifying the suspended work.
- Forgetting that a recursive call frame must remember not only arguments, but also where execution should resume.
- Thinking that recursion and iteration are fundamentally different, rather than two representations of the same control flow.
- Rewriting recursion iteratively without preserving enough state to continue correctly after a subproblem returns.

## ML Systems Connection

This topic is not directly about ML workloads, but it supports ML systems reasoning in several ways.

It helps build accurate intuition for:
- execution stacks and control flow,
- depth-first traversal and backtracking patterns,
- program state machines,
- and the translation between a clean mathematical formulation and an implementation that makes state explicit.

That matters in ML systems because low-level work often requires turning implicit structure into explicit state:
- kernel launches make execution structure concrete,
- runtimes and schedulers make queued work concrete,
- and performance analysis often depends on understanding what state is live, suspended, or waiting.

The lesson is broader than Tower of Hanoi:

Recursion is implicit stack management.
An explicit-stack algorithm is manual stack management.
