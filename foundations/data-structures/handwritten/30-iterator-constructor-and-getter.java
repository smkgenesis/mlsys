class Iterator {
    private Node cur;

    public Iterator(Node first) {
        cur = first;
    }

    // ...
}

class LinkedList {
    public Node first;

    public Iterator getIterator() {
        return new Iterator(first);
    }

    // ...
}
