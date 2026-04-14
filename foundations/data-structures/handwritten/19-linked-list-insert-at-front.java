class LinkedListInsertAtFrontExample {
    public Node first;

    public void InsertAtFront(int x) {
        Node newnode;
        newnode = new Node();
        newnode.data = x;
        newnode.next = first;
        first = newnode;
    }
}
