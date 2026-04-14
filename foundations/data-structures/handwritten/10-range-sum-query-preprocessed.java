class RangeSumQueryPreprocessed {
    static void preprocess(int[] a, int[][] sum, int n) {
        for (int i = 0; i < n; i++) {
            sum[i][i] = a[i];
            for (int j = i + 1; j < n; j++) {
                sum[i][j] = sum[i][j - 1] + a[j];
            }
        }
    }

    static int query(int[][] sum, int x, int y) {
        return sum[x][y];
    }
}
