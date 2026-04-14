class ArrayReferenceSharing {
    public static void main(String[] args) {
        int[] a, b;
        a = new int[10];
        a[3] = 4;
        b = a;
        b[3] = 5;
        b[4] = b[3];
        b[4] = 6;

        System.out.println(a[3] + " " + a[4]);
    }
}
