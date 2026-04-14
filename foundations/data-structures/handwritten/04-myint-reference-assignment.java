class MyIntReferenceAssignment {
    public static void main(String[] args) {
        MyInt a = new MyInt(), b = new MyInt();
        a.setVal(3);
        b = a;

        System.out.println(a.getVal() + " " + b.getVal());

        a.setVal(4);
        System.out.println(a.getVal() + " " + b.getVal());
    }
}
