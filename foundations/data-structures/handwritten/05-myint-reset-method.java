class Hello {
    private static void reset(MyInt x) {
        x.setVal(0);
    }

    public static void main(String[] args) {
        MyInt a = new MyInt();
        a.setVal(10);
        reset(a);
        System.out.println(a.getVal());
    }
}
