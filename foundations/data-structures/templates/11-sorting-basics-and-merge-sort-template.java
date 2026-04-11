class SortingTemplates {
    public static void bubbleSort(int[] a, int n) {
        boolean sorted;
        int tmp;
        do {
            sorted = true;
            for (int i = 0; i < n - 1; i++) {
                if (a[i] > a[i + 1]) {
                    tmp = a[i];
                    a[i] = a[i + 1];
                    a[i + 1] = tmp;
                    sorted = false;
                }
            }
        } while (!sorted);
    }

    public static void selectionSort(int[] a, int n) {
        int i, j, k, tmp;
        for (i = 0; i < n - 1; i++) {
            j = i;
            for (k = i + 1; k < n; k++) {
                if (a[k] < a[j]) {
                    j = k;
                }
            }
            tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
    }

    public static void insertionSort(int[] a, int n) {
        int i, j, tmp;
        for (i = 0; i < n; i++) {
            tmp = a[i];
            for (j = i - 1; j >= 0; j--) {
                if (a[j] <= tmp) {
                    break;
                }
                a[j + 1] = a[j];
            }
            a[j + 1] = tmp;
        }
    }

    public static void mergeSort(int[] a, int n) {
        int[] aux = new int[a.length];
        doMergeSort(a, 0, n - 1, aux);
    }

    public static void doMergeSort(int[] a, int left, int right, int[] aux) {
        int mid;
        int i, j, k;

        if (left >= right) {
            return;
        }
        mid = (left + right) / 2;
        doMergeSort(a, left, mid, aux);
        doMergeSort(a, mid + 1, right, aux);

        for (i = left, j = mid + 1, k = left; (i <= mid) && (j <= right); ) {
            if (a[i] <= a[j]) {
                aux[k++] = a[i++];
            } else {
                aux[k++] = a[j++];
            }
        }
        while (i <= mid) {
            aux[k++] = a[i++];
        }
        while (j <= right) {
            aux[k++] = a[j++];
        }
        for (i = left; i <= right; i++) {
            a[i] = aux[i];
        }
    }
}
