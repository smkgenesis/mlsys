class RangeSumQueryNaive {
    static int query(int[] a, int x, int y) {
        int answer = 0;
        for (int i = x; i <= y; i++) {
            answer += a[i];
        }
        return answer;
    }
}
