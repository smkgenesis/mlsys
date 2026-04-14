class LinkedListDeleteFirstWithLastExample {
    public Node first, last;

    public int DeleteFirst() {
        int ret;
        if (first == null) {
            return -1;
        }
        ret = first.data;
        first = first.next;
        if (first == null) {
            last = null;
        }
        return ret;
    }
}
