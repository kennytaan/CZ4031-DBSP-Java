package MemoryPool;

public class testing {
    public static void main(String[] args) {
        BTreeNode tree = new BTreeNode();
        for (int i=0;i<17;i++){
            tree = tree.insertNode(i,i+100);
        }
        int[] arr = tree.search(1,12);
        for(int i=0;i<arr.length;i++){
            System.out.println(arr);
        }
    }
}
