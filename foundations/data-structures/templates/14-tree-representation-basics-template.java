class TreeRepresentationTemplates {
    static class GeneralTreeNode {
        int data;
        GeneralTreeNode children;
        GeneralTreeNode next;
        GeneralTreeNode parent;
    }

    static class FixedDegreeTreeNode {
        int data;
        FixedDegreeTreeNode[] children;

        FixedDegreeTreeNode(int data, int maxDegree) {
            this.data = data;
            this.children = new FixedDegreeTreeNode[maxDegree];
        }
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
