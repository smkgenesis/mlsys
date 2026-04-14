class BinarySearchWithInsertPosition {
    static boolean found;
    static int foundpos;
    static int insertpos;

    static void search(int[] a, int n, int x) {
        int left, right, mid;

        left = 0;
        right = n - 1;
        while (left < right) {
            mid = (left + right) / 2;
            if (a[mid] < x) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        if (left == right) {
            if (a[left] == x) {
                found = true;
                foundpos = left;
            } else {
                found = false;
                if (a[left] < x) {
                    insertpos = left + 1;
                } else {
                    insertpos = left;
                }
            }
        } else {
            found = false;
            insertpos = left;
        }
    }
}
