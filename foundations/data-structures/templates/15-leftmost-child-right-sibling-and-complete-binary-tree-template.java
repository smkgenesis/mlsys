class TreeEncodingTemplates {
    static class LeftmostChildRightSiblingNode {
        int data;
        LeftmostChildRightSiblingNode leftmostChild;
        LeftmostChildRightSiblingNode rightSibling;
    }

    static class BinaryTreeNode {
        int data;
        BinaryTreeNode left;
        BinaryTreeNode right;
    }

    public static int parentIndex(int i) {
        return i / 2;
    }

    public static int leftChildIndex(int i) {
        return 2 * i;
    }

    public static int rightChildIndex(int i) {
        return 2 * i + 1;
    }
}
