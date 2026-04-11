class QuickSortTemplate {
    public static void quickSort(int[] a, int left, int right) {
        int pivot, tmp;
        int i, j;

        if (left >= right) {
            return;
        }
        pivot = a[left];

        j = left;
        for (i = left + 1; i <= right; i++) {
            if (a[i] < pivot) {
                j++;
                tmp = a[j];
                a[j] = a[i];
                a[i] = tmp;
            }
        }

        tmp = a[left];
        a[left] = a[j];
        a[j] = tmp;

        quickSort(a, left, j - 1);
        quickSort(a, j + 1, right);
    }
}
