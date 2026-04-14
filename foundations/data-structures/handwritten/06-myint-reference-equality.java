class MyIntReferenceEquality {
    public static void main(String[] args) {
        MyInt a = new MyInt(), b = new MyInt();
        a.setVal(3);
        b.setVal(3);

        if (a == b) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }
}
