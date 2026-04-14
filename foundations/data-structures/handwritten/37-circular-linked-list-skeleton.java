class CircularLinkedList {
    private final Node header;

    public CircularLinkedList() {
        Node newnode = new Node();
        newnode.next = newnode;
        header = newnode;
    }

    public boolean isEmpty() {
        return (header.next == header);
    }

    public CircularIterator getIterator() {
        return new CircularIterator(header);
    }
}
