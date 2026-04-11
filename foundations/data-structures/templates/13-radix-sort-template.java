class RadixSortTemplate {
    static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    public static void lsdRadixSort(int[] a, int digits, int radix) {
        Node[] front = new Node[radix];
        Node[] rear = new Node[radix];

        for (int d = 0, div = 1; d < digits; d++, div *= radix) {
            for (int i = 0; i < radix; i++) {
                front[i] = null;
                rear[i] = null;
            }

            for (int x : a) {
                int bucket = (x / div) % radix;
                Node node = new Node(x);
                if (front[bucket] == null) {
                    front[bucket] = node;
                } else {
                    rear[bucket].next = node;
                }
                rear[bucket] = node;
            }

            int k = 0;
            for (int i = 0; i < radix; i++) {
                for (Node cur = front[i]; cur != null; cur = cur.next) {
                    a[k++] = cur.data;
                }
            }
        }
    }
}
