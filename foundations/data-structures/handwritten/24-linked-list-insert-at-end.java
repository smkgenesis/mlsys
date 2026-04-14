class LinkedListInsertAtEndExample {
    public Node last;

    public void InsertAtEnd(int x) {
        Node newnode;
        newnode = new Node();
        newnode.data = x;
        newnode.next = null;
        last.next = newnode;
        last = newnode;
    }
}
