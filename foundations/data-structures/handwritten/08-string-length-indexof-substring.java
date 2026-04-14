class StringLengthIndexOfSubstring {
    public static void main(String[] args) {
        String a = "abcdeabcde";
        int x;

        x = a.length();
        System.out.println(x);

        x = a.indexOf("bc");
        System.out.println(x);

        System.out.println(a.substring(2, 4));
    }
}
