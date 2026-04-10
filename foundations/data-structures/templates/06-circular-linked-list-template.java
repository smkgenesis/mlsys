class Node {
    public int data;
    public Node next;
}

class CircularLinkedList {
    private final Node header;

    public CircularLinkedList() {
        header = new Node();
        header.next = header;
    }

    public boolean isEmpty() {
        return header.next == header;
    }

    public CircularIterator getIterator() {
        return new CircularIterator(header);
    }
}

class CircularIterator {
    private final Node header;
    private Node prev, cur;

    public CircularIterator(Node h) {
        header = h;
        prev = header;
        cur = header.next;
    }

    public boolean atEnd() {
        return cur == header;
    }

    public int getData() {
        if (atEnd()) {
            return -1;
        }
        return cur.data;
    }

    public void next() {
        if (!atEnd()) {
            prev = cur;
            cur = cur.next;
        }
    }

    public int deleteCurrent() {
        if (cur == header) {
            return -1;
        }
        int ret = cur.data;
        prev.next = cur.next;
        cur = cur.next;
        return ret;
    }

    public void insertAfter(int x) {
        Node newNode = new Node();
        newNode.data = x;
        newNode.next = cur.next;
        cur.next = newNode;
    }
}
