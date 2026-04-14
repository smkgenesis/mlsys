class Iterator {
    private Node cur;

    public boolean atEnd() {
        return (cur == null);
    }

    public int getData() {
        if (atEnd()) {
            // error
            return 0;
        }
        return cur.data;
    }

    public void next() {
        if (!atEnd()) {
            cur = cur.next;
        }
    }
}
