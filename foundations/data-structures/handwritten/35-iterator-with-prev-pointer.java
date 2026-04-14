class Iterator {
    private Node prev, cur;
    private LinkedList llist;

    public Iterator(LinkedList caller, Node first) {
        llist = caller;
        prev = null;
        cur = first;
    }

    public boolean atEnd() {
        return (cur == null);
    }

    public void next() {
        if (!atEnd()) {
            prev = cur;
            cur = cur.next;
        }
    }
}
