# Java for C++ Programmers

## What

This note is a small bridge for students who already know some C++-style thinking and need the Java basics used in the course.

The lecture focuses on a narrow set of ideas:

- Java programs are organized around classes,
- compilation and execution happen through `javac` and `java`,
- output uses `System.out.println`,
- primitive variables and object variables behave differently,
- object variables act like references,
- `==` compares references for objects,
- and strings should be compared with `.equals(...)`.

## Why It Matters

This course will later ask you to read and write code in Java.
So before actual data structures appear, the lecture establishes a few rules that prevent basic confusion.

The point is not to learn all of Java.
It is to learn the specific language behavior that will matter once classes, objects, and templates for data structures appear.

## Core Idea

The lecture is really teaching one big distinction:

```text
primitive variables store values directly,
but object variables behave like references to objects
```

Once that becomes clear, many early Java surprises become much easier to reason about.

## Java Programs Live Inside Classes

The lecture starts with a `Hello` example and emphasizes:

- Java code is written inside classes,
- in principle, each class should have its own source file,
- `javac` compiles source code into bytecode,
- and `java` runs the specified class on the virtual machine.

So unlike C or C++-style habits where free functions may appear outside classes, Java introduces a more class-centered structure from the beginning.

## Output and String Concatenation

The lecture also shows simple output examples using:

```text
System.out.println(...)
```

and demonstrates that strings concatenate with `+`.

Important observations from the examples:

- `"abc" + 3` produces a string,
- `a + " " + b` builds a printable string representation,
- while `a + b` with integer variables performs numeric addition.

So the same symbol can participate in different behavior depending on the operand types.

## Primitives Store Values

The integer example shows:

- assign `a = 3`,
- assign `b = a`,
- later change `a = 4`,
- and `b` stays `3`.

That means primitive assignment copies the value.

So for primitives:

- variables are independent after assignment,
- changing one variable does not retroactively change the other.

## Objects Behave Like References

The lecture then introduces a small class like `MyInt` with getter and setter methods.

With object variables:

- `a` and `b` can refer to the same object,
- assigning `b = a` copies the reference, not the object itself,
- so mutating the object through `a` is visible through `b`.

That is why the lecture says object variables work like "pointers."

The key mental model is:

- primitive assignment copies a value,
- object assignment copies access to the same underlying object.

## Object Variables Must Refer to Real Objects

The lecture also shows invalid code in which object variables are declared but no object is created before method calls.

This matters because:

- declaring a variable is not the same as constructing an object,
- methods can only be called on an actual object reference.

So the course is trying to prevent a very common beginner confusion:

```text
variable declaration is not object creation
```

## Method Calls Can Mutate the Referred Object

The `reset(MyInt x)` example is important.

The method changes the object's internal state, and the caller later observes the updated value.

The lecture's point is not a full formal treatment of Java parameter passing.
The important practical lesson is:

- passing an object variable to a method gives the method access to the same underlying object,
- so mutating the object inside the method can affect what the caller later sees.

## `==` Does Not Mean Object Equality

The lecture then shows two separate `MyInt` objects that both store the value `3`.

Even though their contents match, `a == b` is false.

That is because for objects:

- `==` compares references,
- not the logical contents of the objects.

This is one of the most important early Java rules to internalize.

## Strings Use `.equals(...)`

The final string example reinforces this idea.

Strings are objects, so logical comparison should use:

```text
a.equals(b)
```

not `==`.

The lecture also shows reassignment behavior:

- `a = "3";`
- `b = a;`
- later `a = "4";`
- and `b` still refers to the earlier string value.

So string variables also follow reference-style behavior, even though strings often feel simpler than user-defined objects.

## What This Note Is Really Teaching

This note is not trying to teach the whole Java language.
It is preparing you to read later data-structure code without constant low-level confusion.

The key ideas are:

- code is class-based,
- object creation matters,
- references are different from primitive values,
- mutation through references is shared,
- and object equality is not the same as reference equality.

That is exactly the background needed before linked structures and object-based implementations appear.

## Common Mistakes

- Thinking Java functions can be freely defined outside classes the way they often are in C or C++ examples.
- Forgetting that `javac` compiles while `java` runs.
- Assuming object assignment copies the whole object rather than the reference.
- Declaring an object variable and then using it before creating the object.
- Using `==` when the intended comparison is logical equality of object contents.
- Forgetting that strings should usually be compared with `.equals(...)`.

## Why This Matters for CS / Systems

This note matters because later data structures in Java will be built out of objects and references.

Once nodes, linked structures, and container classes appear, you need to be comfortable with:

- what a variable actually stores,
- when two variables refer to the same object,
- and whether an operation mutates shared state or just changes one local variable.

Those are language details, but they directly affect whether your implementation is correct.

## Short Takeaway

This lecture gives the Java minimum needed for data structures: Java code lives inside classes, primitive variables copy values, object variables behave like references, methods can mutate shared objects, and object equality should not be confused with reference equality.
