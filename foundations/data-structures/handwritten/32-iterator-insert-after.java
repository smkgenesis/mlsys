class IteratorInsertAfterExample {
    private Node cur;

    public boolean atEnd() {
        return (cur == null);
    }

    public void InsertAfter(int x) {
        Node newnode;
        if (atEnd()) {
            return;
        }
        newnode = new Node();
        newnode.data = x;
        newnode.next = cur.next;
        cur.next = newnode;
    }
}
