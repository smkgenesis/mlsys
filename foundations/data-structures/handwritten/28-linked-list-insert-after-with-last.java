class LinkedListInsertAfterWithLastExample {
    public Node last;

    public void InsertAfter(Node n, int x) {
        Node newnode;
        if (n == null) {
            return;
        }
        newnode = new Node();
        newnode.data = x;
        newnode.next = n.next;
        n.next = newnode;
        if (newnode.next == null) {
            last = newnode;
        }
    }
}
