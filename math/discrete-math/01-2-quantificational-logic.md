# 01.2 Quantificational Logic

## What

Quantificational logic, also called predicate logic or first-order logic, extends propositional logic so that we can talk about:

- objects,
- properties of objects,
- relations between objects,
- and statements about all or some of those objects.

This is the logic of:

- `for all`
- `there exists`

rather than just the logic of whole propositions.

So the central move is:

```text
propositional logic talks about complete statements;
quantificational logic talks about structured statements built from objects and predicates
```

## Why It Matters

Propositional logic is precise, but it is limited.

It can express:

- `p ∧ q`
- `p -> q`

but it cannot naturally express statements like:

- every student is smart
- some number is prime
- everyone loves someone

To express those, we need:

- variables,
- predicates,
- a universe of discourse,
- and quantifiers.

This matters because quantificational logic is one of the main bridges from symbolic logic to:

- mathematics,
- database querying,
- formal specification,
- automated reasoning,
- and classical knowledge representation in AI.

## Core Idea

Quantificational logic adds three essential ingredients to propositional logic:

1. predicates
2. a universe of discourse
3. quantifiers

Predicates let us describe properties and relations.
The universe tells us what objects the variables can range over.
Quantifiers tell us whether a claim is about:

- all objects,
- or at least one object.

So the logical unit is no longer just a proposition symbol like `p`.
It becomes a structured expression such as:

```text
∀x H(x) -> B(x)
```

which says that every hen is black.

## Predicates

A predicate is like a proposition template.
It becomes a full proposition only after we supply its arguments.

Examples:

- unary predicate: `H(x)` means “`x` is a hen”
- unary predicate: `B(x)` means “`x` is black”
- binary predicate: `L(x, y)` means “`x` loves `y`”
- ternary predicate: `S(x, y, z)` means “`x + y = z`”

Once constants are plugged in, the result can be true or false:

- `H(Ginger)`
- `S(1, 2, 3)`

This is the first step beyond propositional logic:
we are no longer treating statements as indivisible atoms.

## The Universe of Discourse

The universe of discourse specifies the set of values that variables are allowed to range over.

This matters because the truth of a quantified statement depends on that universe.

Example:

```text
∃x (x + 5 = 3)
```

If the universe is:

- positive integers, the statement is false

If the universe is:

- all integers, the statement is true because `x = -2` exists

So logical truth here depends not just on syntax, but on what domain the formula is interpreted over.

## Quantifiers

There are two central quantifiers.

### Universal quantifier

```text
∀x F(x)
```

means:

```text
for every x in the universe, F(x) is true
```

### Existential quantifier

```text
∃x F(x)
```

means:

```text
there exists at least one x in the universe such that F(x) is true
```

These are simple symbols, but they change the expressive power of the language dramatically.

## Translating English Correctly

The lecture emphasizes that English translation is one of the easiest places to go wrong.

Two patterns matter especially.

### All A are B

If:

- `H(x)` means “`x` is a hen”
- `B(x)` means “`x` is black”

then:

```text
∀x (H(x) -> B(x))
```

means:

```text
all hens are black
```

The common mistake is:

```text
∀x (H(x) ∧ B(x))
```

which says everything in the universe is both a hen and black.
That is much stronger and usually wrong.

### Some A are B

The correct form is:

```text
∃x (H(x) ∧ B(x))
```

The common mistake is:

```text
∃x (H(x) -> B(x))
```

which is too weak, because any non-hen would make the implication true.

This is one of the main lessons of quantified logic:

```text
logical form matters much more than the surface grammar of English
```

## Quantifier Order

Quantifier order matters enormously.

Example:

```text
∀x ∃y L(x, y)
```

means:

```text
everyone loves someone
```

The loved person may vary with `x`.

But:

```text
∃y ∀x L(x, y)
```

means:

```text
there is one person whom everyone loves
```

That is a completely different claim.

So quantified formulas are not just about which quantifiers appear.
They are also about how those quantifiers are ordered and scoped.

## Unique Existence

Sometimes we want to say not just “there exists,” but “there exists exactly one.”

This is written informally as:

```text
∃!x P(x)
```

The usual strategy is to combine:

- existence
- and uniqueness

The standard pattern is:

```text
∃x (P(x) ∧ ∀y (P(y) -> y = x))
```

This says:

- at least one object satisfies `P`
- and anything else satisfying `P` must be the same object

So unique existence is not a new primitive.
It is a structured combination of familiar ones.

## Free and Bound Variables

This is one of the most important structural distinctions.

- a **bound** variable is captured by a quantifier
- a **free** variable is not

Example:

```text
∀x (P(x) ∧ Q(y))
```

Here:

- `x` is bound
- `y` is free

Why it matters:

- a closed formula, with no free variables, can be evaluated as true or false under an interpretation
- a formula with free variables is more like an open condition whose truth depends on the values plugged in

So quantificational logic introduces an important distinction between:

- complete statements
- and formulas with parameters still left open

## Syntax and Inductive Structure

Just like propositional formulas, quantificational formulas are built inductively.

Base layer:

- atomic formulas such as `P(x, y)`, `H(Ginger)`, or `x + 0 = x`

Constructor layer:

- propositional operators such as `∧`, `∨`, `¬`, `->`
- quantifiers such as `∀x` and `∃x`

So quantified logic does not replace propositional logic.
It extends it by adding richer atomic structure and new constructors.

## Semantics: Interpretations and Models

Truth in quantificational logic requires an interpretation.

An interpretation specifies:

1. a non-empty universe
2. the meaning of each predicate symbol
3. the meaning of each constant symbol
4. the meaning of each function symbol

A **model** of a formula is an interpretation in which that formula is true.

This is the quantified-logic analog of a satisfying assignment in propositional logic.

So semantics now means more than assigning truth values to proposition letters.
It means giving a whole mathematical world in which the formula is interpreted.

## Satisfiability and Validity

The familiar propositional ideas return in richer form.

- **satisfiable**: true in at least one interpretation
- **unsatisfiable**: true in no interpretation
- **valid**: true in every interpretation

So quantified logic keeps the same semantic questions as propositional logic, but now those questions range over interpretations rather than simple truth assignments.

## Key Equivalence Rules

Several structural equivalences matter a lot.

### Renaming bound variables

You may rename a bound variable as long as you do not accidentally capture another variable.

Example:

```text
∃x (H(x) ∧ B(x)) ≡ ∃y (H(y) ∧ B(y))
```

### Quantifier negation

These are the quantified analogs of De Morgan's laws:

```text
¬∀x F ≡ ∃x ¬F
```

```text
¬∃x F ≡ ∀x ¬F
```

These are among the most useful transformations in the subject.

### Scope movement

Quantifiers can sometimes be moved across other parts of a formula, but only when the variable does not occur free in the untouched part.

This is where careful variable discipline becomes essential.

## What This Note Is Really Teaching

This note is not only about introducing new symbols.
It is teaching a more structured view of statements.

In propositional logic:

- formulas are built from truth-valued atoms

In quantificational logic:

- formulas are built from objects, predicates, variables, and quantified scope

That shift is the real point.
The language becomes expressive enough to describe mathematical structure rather than just truth-functional combinations of whole statements.

## Common Mistakes

- Treating quantified formulas as if English word order alone determines the right formal translation.
- Writing `∀x (H(x) ∧ B(x))` when the intended meaning is “all hens are black.”
- Writing `∃x (H(x) -> B(x))` when the intended meaning is “some hens are black.”
- Forgetting that the universe of discourse affects truth.
- Ignoring quantifier order and assuming `∀x∃y` means the same thing as `∃y∀x`.
- Confusing free and bound variables.
- Renaming variables in ways that accidentally capture free variables.
- Thinking satisfiability and validity are checked only by syntax, rather than by interpretation.

## Why This Matters for ML Systems

ML systems are mostly numerical, but many important system components still rely on symbolic structure.

Quantificational logic matters because it strengthens intuition for:

- variables and scope,
- structured predicates and relations,
- the difference between syntax and interpretation,
- formal specifications,
- query languages,
- and constraint-like reasoning over structured objects.

It is also one of the conceptual ancestors of:

- database semantics,
- rule systems,
- verification languages,
- and symbolic knowledge representations that often sit around modern ML systems even when the models themselves are numeric.

This note is therefore less about training neural networks directly and more about developing the habit of reasoning precisely about structured claims over domains.

## Short Takeaway

Quantificational logic extends propositional logic by introducing predicates, universes of discourse, and quantifiers, allowing us to express statements about objects, properties, and relations. The deepest shift is that truth now depends not just on truth-functional structure, but on variables, scope, interpretation, and the domain over which a formula is understood.
