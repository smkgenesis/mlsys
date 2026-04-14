class IteratorDeleteCurrentExample {
    private Node prev, cur;
    private LinkedList llist;

    public int DeleteCurrent() {
        int ret;
        if (cur == null) {
            return -1;
        }
        ret = cur.data;
        cur = cur.next;
        if (prev == null) {
            llist.first = cur;
        } else {
            prev.next = cur;
        }
        if (cur == null) {
            llist.last = prev;
        }
        return ret;
    }
}
