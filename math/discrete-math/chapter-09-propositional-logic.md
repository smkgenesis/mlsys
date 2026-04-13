# Chapter 9. Propositional Logic

## What

Propositional logic is the formal study of statements that are either true or false.

It gives precise meanings to logical connectives such as:

- not
- and
- or
- xor
- implication
- equivalence

The point of the subject is to replace the ambiguity of natural language with a system where truth can be analyzed exactly.

## Why It Matters

The lecture starts with a very practical problem:

- ordinary language is ambiguous

Words like:

- “or”
- “if”

can mean different things in English depending on context.

Computers cannot tolerate that ambiguity. Logic gives us a clean language where:

- statements have definite truth values,
- formulas have exact structure,
- and reasoning can be checked mechanically.

That is why propositional logic matters not only in mathematics, but also in:

- programming,
- digital circuits,
- verification,
- and formal reasoning in computer science.

## Propositions

A proposition is a declarative statement that is either:

- true
- or false

Examples from the lecture:

- “2000 was a leap year” is true
- “16 is a prime number” is false

The lecture also points out two important non-examples:

- ambiguous statements are not good propositions in this context
- meaningless sentences are not useful propositions either

So the key requirement is:

```text
a proposition must have a definite truth value
```

## Atomic and Compound Propositions

### Atomic proposition

An atomic proposition is a simple proposition that we treat as indivisible.

We usually represent these by symbols like:

- `p`
- `q`
- `r`

### Compound proposition

A compound proposition is built from atomic propositions using logical operators.

Examples:

- `¬p`
- `p ∨ q`
- `p ∧ q`

This is the beginning of the recursive structure of logic:

- simple formulas first,
- more complex formulas built from them.

## Logical Operators

The lecture introduces the main connectives.

### Negation

```text
¬p
```

means “not `p`”.

It flips truth value:

- if `p` is true, `¬p` is false
- if `p` is false, `¬p` is true

### Conjunction

```text
p ∧ q
```

means “`p` and `q`”.

It is true only when both parts are true.

### Inclusive disjunction

```text
p ∨ q
```

means “`p` or `q` or both”.

This is the inclusive sense of “or,” which often differs from conversational English.

### Exclusive or

```text
p ⊕ q
```

means exactly one of `p` and `q` is true.

So:

- both true -> false
- both false -> false
- exactly one true -> true

### Implication

```text
p -> q
```

means “if `p`, then `q`”.

This is one of the most misunderstood operators, because it does not exactly match how “if” is used informally in English.

The crucial truth-table fact is:

- an implication is false only when `p` is true and `q` is false

That is why:

```text
p -> q  ≡  ¬p ∨ q
```

### Equivalence

```text
p ↔ q
```

means “`p` if and only if `q`”.

It is true when `p` and `q` have the same truth value.

## Truth Tables

A truth table shows how the truth of a compound formula depends on the truth values of its components.

This is one of the core tools of propositional logic.

Truth tables let us:

- evaluate formulas,
- test equivalence,
- identify tautologies,
- and identify contradictions.

The lecture also calls propositional logic a kind of calculus because truth values can be computed systematically.

## Operator Precedence

When several operators occur together, order matters.

The lecture gives the precedence order:

1. `¬`
2. `∧`
3. `∨`, `⊕`
4. `->`
5. `↔`

This is like arithmetic precedence.

So:

```text
p ∨ q ∧ ¬r
```

should be read as:

```text
p ∨ (q ∧ (¬r))
```

Still, the best practical rule is the lecture’s rule:

```text
when in doubt, use parentheses
```

## Logical Equivalence

Two formulas `α` and `β` are logically equivalent if they have the same truth table.

This is written:

```text
α ≡ β
```

This is a statement about formulas at the meta-level.

The lecture contrasts this with:

```text
p ↔ q
```

which is itself a formula inside the logic.

That distinction matters:

- `≡` says two formulas always behave the same
- `↔` is just another connective inside a formula

## Important Equivalences

The lecture proves two especially useful equivalences.

### Implication as disjunction

```text
p -> q  ≡  ¬p ∨ q
```

This explains why implication is true whenever `p` is false.

### Exclusive or expansion

```text
p ⊕ q  ≡  (p ∧ ¬q) ∨ (¬p ∧ q)
```

This expresses xor in terms of simpler connectives.

These are important because they show that some operators are definable from others.

## Functional Completeness

The lecture says that the set:

```text
{∧, ∨, ¬}
```

is functionally complete.

That means:

- every truth-functional connective can be expressed using only these operators

So implication and xor are convenient, but not fundamentally necessary.

The lecture also notes that even smaller sets can be complete:

- NAND alone
- NOR alone

This is one of the bridges from logic into circuit design and computation.

## Tautology, Contradiction, and Satisfiability

These are three central classifications of formulas.

### Tautology

A tautology is always true, no matter how truth values are assigned.

Example:

```text
p ∨ ¬p
```

### Contradiction

A contradiction is always false.

Example:

```text
p ∧ ¬p
```

### Satisfiable

A formula is satisfiable if it is true under at least one assignment.

So:

- every tautology is satisfiable
- not every satisfiable formula is a tautology

The lecture also points out:

```text
α is a tautology  iff  ¬α is unsatisfiable
```

This is a very useful duality.

## Logical Laws

The lecture lists the standard algebraic laws of propositional logic.

These include:

- identity
- domination
- idempotent laws
- double negation
- commutative laws
- associative laws
- distributive laws
- De Morgan’s laws

These laws matter because they let us transform formulas symbolically, just as algebraic identities let us transform arithmetic expressions.

So propositional logic is not only about truth tables. It also has an algebraic side.

## Boolean Functions

Any propositional formula with `k` variables determines a function:

```text
{T, F}^k -> {T, F}
```

or equivalently:

```text
{0, 1}^k -> {0, 1}
```

This is a Boolean function, also called a truth function.

This perspective is very important because it connects logic to:

- computation,
- circuits,
- and formal specification.

Logical formulas are not just sentences. They are also finite descriptions of Boolean functions.

## Inductive Definition of Formulas

The lecture ends by pointing out that propositional formulas themselves can be defined inductively.

### Base case

Every atomic proposition is a formula.

### Constructor cases

If `α` and `β` are formulas, then so are:

- `¬α`
- `(α ∧ β)`
- `(α ∨ β)`

and similarly for the other connectives.

This is a very important structural point:

- formulas are recursively generated objects

That means structural induction can later be used to prove facts about all formulas.

So this lecture quietly links logic back to the previous structural-induction lecture.

## Common Mistakes

- Treating English “or” as if it always means inclusive disjunction.
- Forgetting that implication is false only in the true-to-false case.
- Confusing `≡` with `↔`.
- Ignoring precedence and reading formulas incorrectly.
- Thinking satisfiable means always true.
- Forgetting that logical formulas can be manipulated both by truth tables and by algebraic laws.

## Why This Matters Beyond Pure Math

Propositional logic is one of the clearest foundations for formal reasoning in computer science.

It supports:

- conditionals in programs,
- assertions and invariants,
- SAT and constraint reasoning,
- Boolean circuits,
- and formal verification.

For later systems and ML-adjacent work, the main transfer is discipline:

- separate syntax from meaning,
- make conditions explicit,
- and reason precisely about cases and implications.

## Short Takeaway

Propositional logic formalizes truth-functional reasoning: formulas are built from atomic statements using precise operators, evaluated by truth tables, transformed by logical equivalence laws, and interpreted as Boolean functions that underpin computation.

## Truth-Table Workflow

This chapter is easiest when every problem is treated as one of four recurring tasks:

1. translate natural language into connectives,
2. evaluate a formula with a truth table,
3. compare two formulas by checking whether their final columns match,
4. or rewrite one operator basis into another.

The practical decision rule is:

```text
if the question asks whether two formulas are equivalent,
or whether a formula is a tautology, contradiction, or satisfiable,
the safest default tool is a truth table.
```

The reusable classification pattern is:

- final column all `T` -> tautology
- final column all `F` -> unsatisfiable / contradiction
- mixture of `T` and `F` -> satisfiable but not a tautology

## Translation Cues

Many Chapter 9 exercises are really translation exercises disguised as logic problems.
The high-value phrases are:

- `if ... then ...` -> implication
- `only if` -> the condition goes on the right side of `=>`
- `necessary` -> result implies condition
- `sufficient` -> condition implies result
- `but` -> conjunction
- `unless` -> usually easiest to rewrite first, then translate

The main mistake pattern in this chapter is not the algebra.
It is assigning the wrong connective because the English cue word was read too quickly.

## Worked Exercise Patterns from Chapter 9

The main payoff of this chapter is learning to move cleanly between formulas, truth functions, and English statements.

### 9.1 and 9.2. Functional completeness by rewriting operator bases

Problems 9.1 and 9.2 train the same reflex:

- first rewrite one connective in terms of another basis,
- then substitute that rewrite everywhere else.

The two high-value identities are:

```text
p ∧ q  ≡  ¬(¬p ∨ ¬q)
p ∨ q  ≡  ¬(¬p ∧ ¬q)
```

So once `{¬, ∨}` is known to be sufficient, `{¬, ∧}` is sufficient too, and vice versa.

The reusable lesson is:

```text
completeness arguments often reduce to showing
one missing connective can be defined in the chosen basis.
```

### 9.3 and 9.4. Associative and distributive laws are best checked by column comparison

For associative and distributive laws, the cleanest exam method is:

1. build one truth table,
2. compute both target expressions,
3. compare the final two columns.

If the columns match in all rows, the formulas are logically equivalent.

This is the safest proof form when the problem explicitly says “using truth tables.”

The reusable lesson is:

```text
for algebraic laws of propositional logic,
equivalence means identical final truth-table columns.
```

### 9.5 and 9.12. Classifying formulas by the final truth-table column

These exercises are pure recognition drills.
The three outcomes are:

- all `T` -> tautology
- all `F` -> unsatisfiable
- mixed -> satisfiable but not a tautology

The reusable lesson is:

```text
tautology / satisfiable / unsatisfiable
is not three separate topics;
it is one table-reading skill.
```

### 9.6. Natural-language translation depends on cue words, not intuition

The real difficulty in these exercises is not symbolic manipulation.
It is reading words like:

- `only if`,
- `necessary`,
- `sufficient`,
- `unless`

without collapsing them into vague English intuition.

The reusable lesson is:

```text
translate by cue phrase first,
then simplify the symbolic formula second.
```

### 9.7 and 9.10. Formula equivalence versus truth-function equality

These exercises connect three levels:

- formulas,
- logical equivalence,
- and truth functions.

The key identification is:

```text
α ≡ β    iff    α ↔ β is a tautology
```

and in truth-function language:

```text
α ≡ β    iff    φ_α = φ_β
```

So logical equivalence means two formulas compute exactly the same Boolean function.

The reusable lesson is:

```text
logical equivalence is semantic equality of truth functions.
```

### 9.8 and 9.9. Building formulas from cases and reading parentheses carefully

“Exactly one of p, q, r is true” is a standard case split:

- `p` only,
- `q` only,
- `r` only.

This produces a disjunction of mutually exclusive conjunctions.

The “quite different meanings” problem with

```text
(p ∧ q) => r
```

versus

```text
p ∧ (q => r)
```

is a different but related lesson:

- the first is a single conditional rule,
- the second asserts `p` as an actual fact and also asserts a conditional.

The reusable lesson is:

```text
parentheses do not just change notation;
they change which claims are assumptions and which are asserted facts.
```

### 9.11. XOR-style bases are expressive in one direction and weak in another

This exercise is where operator-basis questions become more conceptual.

Using:

- `⊕`,
- `⇔`,
- `¬`,
- `T`,
- `F`

you can still express negation, parity, and equality-type behavior.
But you cannot express every truth function.
In particular, the family stays trapped in XOR / affine behavior and cannot build conjunction-style interactions such as `p ∧ q`.

The reusable lesson is:

```text
not every elegant operator family is complete;
to prove incompleteness, show one truth function that the family cannot express.
```

## Chapter 9 Pattern Summary

Chapter 9 is less about memorizing connectives and more about learning to switch viewpoints smoothly:

- English statement,
- symbolic formula,
- truth table,
- equivalence law,
- and Boolean function.

That is the core skill.
Once those views stay aligned, most of the chapter becomes routine rather than slippery.
