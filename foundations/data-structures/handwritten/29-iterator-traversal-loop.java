class IteratorTraversalLoopExample {
    void displayAll(LinkedList list) {
        Iterator i;
        for (i = list.getIterator(); !i.atEnd(); i.next()) {
            System.out.println(i.getData());
        }
    }
}
