class ExpressionProcessing {
    private static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    private static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    private static int apply(int a, int b, char op) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return a / b;
            default:
                return 0;
        }
    }

    public static int evalPostfix(String[] tokens) {
        IntStack values = new IntStack(tokens.length);
        for (String token : tokens) {
            char c = token.charAt(0);
            if (Character.isDigit(c)) {
                values.push(Integer.parseInt(token));
            } else {
                int right = values.pop();
                int left = values.pop();
                values.push(apply(left, right, c));
            }
        }
        return values.pop();
    }

    public static String infixToPostfix(String expr) {
        CharStack ops = new CharStack(expr.length());
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char x = expr.charAt(i);
            if (x == ' ') {
                continue;
            }
            if (isOperand(x)) {
                out.append(x);
            } else if (x == '(') {
                ops.push(x);
            } else if (x == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    out.append(ops.pop());
                }
                if (!ops.isEmpty()) {
                    ops.pop();
                }
            } else {
                while (!ops.isEmpty() && ops.peek() != '('
                        && precedence(ops.peek()) >= precedence(x)) {
                    out.append(ops.pop());
                }
                ops.push(x);
            }
        }

        while (!ops.isEmpty()) {
            out.append(ops.pop());
        }
        return out.toString();
    }
}

class IntStack {
    private final int[] data;
    private int top;

    public IntStack(int size) {
        data = new int[size];
        top = -1;
    }

    public void push(int x) {
        data[++top] = x;
    }

    public int pop() {
        return data[top--];
    }
}

class CharStack {
    private final char[] data;
    private int top;

    public CharStack(int size) {
        data = new char[size];
        top = -1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public void push(char x) {
        data[++top] = x;
    }

    public char pop() {
        return data[top--];
    }

    public char peek() {
        return data[top];
    }
}
