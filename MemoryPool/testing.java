package MemoryPool;

public class testing {
    public static void main(String[] args) {
        BTreeNode tree = new BTreeNode();
        for (int i=0;i<300;i++){
            tree = tree.insertNode(10,i+100);
        }
        int[] arr = tree.search(10,10);
        System.out.println(arr.length);
    }
}
