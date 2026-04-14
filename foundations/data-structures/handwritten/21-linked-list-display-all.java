class LinkedListDisplayAllExample {
    public Node first;

    public void DisplayAll() {
        Node cur;
        for (cur = first; cur != null; cur = cur.next) {
            System.out.println(cur.data);
        }
    }
}
