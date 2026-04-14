class StringAssignmentAndEquals {
    public static void main(String[] args) {
        String a, b;
        a = "3";
        b = a;

        System.out.println(a + " " + b);

        a = "4";
        System.out.println(a + " " + b);

        if (a.equals(b)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }
}
