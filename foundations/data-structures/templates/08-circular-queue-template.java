class CircularQueue {
    private final int[] data;
    private final int size;
    private int front, rear;

    public CircularQueue(int s) {
        data = new int[s + 1];
        size = s + 1;
        front = 0;
        rear = -1 + s;
    }

    public boolean isEmpty() {
        return ((rear + 1) % size) == front;
    }

    public boolean isFull() {
        return ((rear + 2) % size) == front;
    }

    public void enqueue(int x) {
        if (!isFull()) {
            rear = (rear + 1) % size;
            data[rear] = x;
        }
    }

    public int dequeue() {
        if (isEmpty()) {
            return -1;
        }
        int ret = data[front];
        front = (front + 1) % size;
        return ret;
    }
}
