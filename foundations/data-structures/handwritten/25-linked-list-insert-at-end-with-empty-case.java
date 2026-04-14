class LinkedListInsertAtEndWithEmptyCaseExample {
    public Node first, last;

    public void InsertAtEnd(int x) {
        Node newnode;
        newnode = new Node();
        newnode.data = x;
        newnode.next = null;
        if (last == null) {
            first = last = newnode;
        } else {
            last.next = newnode;
            last = newnode;
        }
    }
}
