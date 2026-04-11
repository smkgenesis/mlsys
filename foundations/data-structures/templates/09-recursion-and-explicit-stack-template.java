class RecursionTemplates {
    public static int binom(int n, int k) {
        int r1, r2;
        if ((k < 0) || (k > n)) {
            return 0;
        }
        if ((k == 0) || (k == n)) {
            return 1;
        }
        r1 = binom(n - 1, k);
        r2 = binom(n - 1, k - 1);
        return r1 + r2;
    }

    public static void hanoiRecursive(int n, int from, int to) {
        if (n <= 0) {
            return;
        }
        int aux = 6 - from - to;
        hanoiRecursive(n - 1, from, aux);
        System.out.println("Move a disk from rod " + from + " to rod " + to);
        hanoiRecursive(n - 1, aux, to);
    }
}

class Context {
    public int n, from, to;
    public int whereToReturn;

    public Context(int n, int f, int t, int w) {
        this.n = n;
        from = f;
        to = t;
        whereToReturn = w;
    }
}

class HanoiIterative {
    public static void hanoi(int n, int from, int to) {
        ArrayStack s = new ArrayStack(n * 4 + 4);
        Context ctx = new Context(n, from, to, 0);
        boolean callDone;

        while (true) {
            callDone = false;
            switch (ctx.whereToReturn) {
                case 0:
                    if (ctx.n <= 0) {
                        callDone = true;
                    } else {
                        s.pushContext(new Context(ctx.n, ctx.from, ctx.to, 1));
                        ctx = new Context(ctx.n - 1, ctx.from, 6 - ctx.from - ctx.to, 0);
                    }
                    break;
                case 1:
                    System.out.println("Move a disk from rod " + ctx.from + " to rod " + ctx.to);
                    s.pushContext(new Context(ctx.n, ctx.from, ctx.to, 2));
                    ctx = new Context(ctx.n - 1, 6 - ctx.from - ctx.to, ctx.to, 0);
                    break;
                case 2:
                    callDone = true;
                    break;
            }
            if (callDone) {
                if (s.isEmpty()) {
                    break;
                } else {
                    ctx = s.popContext();
                }
            }
        }
    }
}

class ArrayStack {
    private final Context[] data;
    private int top;

    public ArrayStack(int size) {
        data = new Context[size];
        top = -1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public void pushContext(Context ctx) {
        data[++top] = ctx;
    }

    public Context popContext() {
        return data[top--];
    }
}
