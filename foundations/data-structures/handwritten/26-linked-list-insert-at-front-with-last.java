class LinkedListInsertAtFrontWithLastExample {
    public Node first, last;

    public void InsertAtFront(int x) {
        Node newnode;
        newnode = new Node();
        newnode.data = x;
        newnode.next = first;
        first = newnode;
        if (last == null) {
            last = newnode;
        }
    }
}
