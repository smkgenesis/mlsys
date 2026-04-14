class LinkedListDeleteFirstExample {
    public Node first;

    public int DeleteFirst() {
        int ret;
        if (first == null) {
            return -1;
        }
        ret = first.data;
        first = first.next;
        return ret;
    }
}
