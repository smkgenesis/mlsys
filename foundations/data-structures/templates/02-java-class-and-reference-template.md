# 02. Java Class and Reference Template

## What

This template captures the minimum Java patterns from DS02 that later hand-coded data structures will rely on.

It is based on [02. Java for C++ Programmers](/Users/minkyu/Documents/mlsys/foundations/data-structures/02-java-for-cpp-programmers.md).

This is not a full Java note.
It is the smallest reconstruction kit for later linked-structure code.

## Core Rules

```text
Java code lives inside classes.
Primitive variables copy values.
Object variables copy references.
```

If those three rules stay clear, most early Java data-structure code becomes much easier to rebuild.

## Minimal Class Skeleton

```java
class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, world!");
    }
}
```

Use this only as the base shape:

- class declaration
- `main`
- `System.out.println(...)`

## Minimal Object Skeleton

```java
class MyInt {
    private int val;

    public int getVal() {
        return val;
    }

    public void setVal(int x) {
        val = x;
    }
}
```

This is the lecture's smallest useful object example:

- one private field
- one getter
- one setter

Later node classes will look structurally similar.

## Primitive Assignment Template

```java
int a, b;
a = 3;
b = a;
a = 4;
```

Reasoning template:

- `b` got a copy of the value
- so changing `a` later does not change `b`

## Object Reference Template

```java
MyInt a = new MyInt();
MyInt b = new MyInt();
a.setVal(3);
b = a;
a.setVal(4);
```

Reasoning template:

- `b = a` copies the reference
- both names now refer to the same object
- mutating through one name is visible through the other

## Method-Mutation Template

```java
class Hello {
    private static void reset(MyInt x) {
        x.setVal(0);
    }
}
```

Reasoning template:

- `x` refers to the same underlying object as the caller's variable
- so mutating the object inside the method changes what the caller later sees

## Equality Reminder

For user-defined objects:

```java
a == b
```

checks whether the references are the same.

For strings:

```java
a.equals(b)
```

checks logical equality of contents.

## Pressure Checklist

1. Is my code inside a class?
2. Did I use `new` before calling methods on an object variable?
3. Am I treating object variables as references rather than copied objects?
4. If I pass an object to a method, can that method mutate shared state?
5. Am I accidentally using `==` where `.equals(...)` is intended?

## Common Mistakes

- Writing free functions outside a class.
- Forgetting to construct an object with `new`.
- Thinking `b = a` copies the object rather than the reference.
- Forgetting that method calls can mutate the shared object.
- Using `==` for string content comparison.

## Short Takeaway

For DS02, the Java template to remember is simple: code lives inside classes, `main` is the execution entry point, primitive assignment copies values, object assignment copies references, and later data-structure code will rely heavily on that reference model.
