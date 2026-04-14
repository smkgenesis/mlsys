class ObjectArrayReferenceSharing {
    public static void main(String[] args) {
        MyInt[] c, d;
        c = new MyInt[10];
        c[3] = new MyInt();
        c[3].setVal(4);
        d = c;
        d[3].setVal(5);
        d[4] = d[3];
        d[4].setVal(6);

        System.out.println(c[3].getVal() + " " + c[4].getVal());
    }
}
