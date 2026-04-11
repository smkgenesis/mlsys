class QueueLL {
    private final LinkedList llist;

    public QueueLL() {
        llist = new LinkedList();
    }

    public void enqueue(int x) {
        llist.insertAtEnd(x);
    }

    public int dequeue() {
        return llist.deleteFirst();
    }

    public boolean isEmpty() {
        return llist.isEmpty();
    }
}

class StackLL {
    private final LinkedList llist;

    public StackLL() {
        llist = new LinkedList();
    }

    public void push(int x) {
        llist.insertAtFront(x);
    }

    public int pop() {
        return llist.deleteFirst();
    }

    public boolean isEmpty() {
        return llist.isEmpty();
    }
}

class ArrayStack {
    private final int[] data;
    private final int size;
    private int top;

    public ArrayStack(int s) {
        data = new int[s];
        size = s;
        top = -1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == (size - 1);
    }

    public void push(int x) {
        if (!isFull()) {
            data[++top] = x;
        }
    }

    public int pop() {
        if (isEmpty()) {
            return -1;
        }
        return data[top--];
    }
}
