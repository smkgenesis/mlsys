class RangeSumQueryPrefixSums {
    static void preprocess(int[] a, int[] sum, int n) {
        sum[0] = a[0];
        for (int i = 1; i < n; i++) {
            sum[i] = sum[i - 1] + a[i];
        }
    }

    static int query(int[] sum, int x, int y) {
        if (x == 0) {
            return sum[y];
        } else {
            return sum[y] - sum[x - 1];
        }
    }
}
