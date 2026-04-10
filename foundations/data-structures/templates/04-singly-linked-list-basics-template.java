class Node {
    public int data;
    public Node next;
}

class LinkedList {
    public Node first, last;

    public LinkedList() {
        first = last = null;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public void insertAtFront(int x) {
        Node newNode = new Node();
        newNode.data = x;
        newNode.next = first;
        first = newNode;
        if (last == null) {
            last = newNode;
        }
    }

    public int deleteFirst() {
        if (first == null) {
            return -1;
        }
        int ret = first.data;
        first = first.next;
        if (first == null) {
            last = null;
        }
        return ret;
    }

    public void insertAfter(Node n, int x) {
        if (n == null) {
            return;
        }
        Node newNode = new Node();
        newNode.data = x;
        newNode.next = n.next;
        n.next = newNode;
        if (newNode.next == null) {
            last = newNode;
        }
    }

    public void insertAtEnd(int x) {
        Node newNode = new Node();
        newNode.data = x;
        newNode.next = null;
        if (last == null) {
            first = last = newNode;
        } else {
            last.next = newNode;
            last = newNode;
        }
    }

    public void displayAll() {
        Node cur;
        for (cur = first; cur != null; cur = cur.next) {
            System.out.println(cur.data);
        }
    }
}
