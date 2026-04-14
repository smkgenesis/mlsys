class LinkedList {
    public Iterator getIterator() {
        return new Iterator(this, first);
    }

    public Node first;
}

class Iterator {
    private Node cur;
    private LinkedList llist;

    public Iterator(LinkedList caller, Node first) {
        llist = caller;
        cur = first;
    }
}
