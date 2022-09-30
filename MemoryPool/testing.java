package MemoryPool;

public class testing {
    public static void main(String[] args) {
        BTreeNode tree = new BTreeNode();
        for (int i=0;i<100;i++){
            tree.insertKey(i,i+100);
        }
        tree.search(55,55);
    }
}
