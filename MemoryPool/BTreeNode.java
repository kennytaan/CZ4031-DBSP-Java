package MemoryPool;

import java.util.ArrayList;
import java.util.Arrays;
import static MemoryPool.Utils.*;

public class BTreeNode {
    public static final int MAX_KEYS = NUMOFKEYS;
    private static final int MAX_POINTERS = NUMOFPOINTERS;
    private static final int MIN_KEYS = MINKEYS;
    private static final int MIN_NON_LEAF = NUMOFKEYS/2;
    private int[] keys = new int[NUMOFKEYS];
    private Object[] pointers = new Object[NUMOFPOINTERS];
    private int size = 0;
    private int height = 0;
    private static int numOfDeletedMerged = 0;

    public BtreeNode(){
        for (int i=0;i<NUMOFKEYS;i++){
            this.keys[i] = -1;
        }
    }

    public String getContent() {
//        String node = "Node reference: " + super.() + "\n";
        String keysStr = "Keys: " + Arrays.toString(this.keys) + "\n";
        String ptrStr = "Pointers: " + Arrays.toString(this.pointers) + "\n";
        return keysStr + ptrStr + "\n";
    }

    public int[] getKeys() {
        return this.keys;
    }

    public Object[] getPointers() {
        return this.pointers;
    }

    public BTreeNode insertNode(int key, int address){
        if (this.height == 0){
            return this.insertLeafNode(key, address);
        }
        // get child
        int childIndex = 0;
        for(int i=0;i<this.size && key >= this.keys[i]; i++){
            childIndex = i+1;
        }
        BTreeNode child = (BTreeNode) this.pointers[childIndex];
        this.pointers[childIndex] = child.insertNode(key, address);
        // if new parent key is returned
        if (child == this.pointers[childIndex]){
            return this;
        }
        BTreeNode newParent = (BTreeNode) this.pointers[childIndex];
        // if current non leaf is not full
        if( this.size< MAX_KEYS){
            this.insertKey(newParent.keys[0], newParent.pointers[0], newParent.pointers[1]);
            return this;
        }

        int newNodeKeys = MAX_KEYS/2;
        int newNodePointers = newNodeKeys+1;
        int oldNodeKeys = MAX_KEYS-newNodeKeys;
        int oldNodePointers = oldNodeKeys+1;
        int j=0;
        // copy all keys and pointers to one array
        BTreeNode[] childNodes = new BTreeNode[MAX_KEYS+2];
        for(int i=0; i<MAX_POINTERS;i++){
            if(i==childIndex){
                childNodes[j++] = (BTreeNode) newParent.pointers[0];
                childNodes[j++] = (BTreeNode) newParent.pointers[1];
                continue;
            }
            childNodes[j++] = (BTreeNode) this.pointers[i];
        }

        // initialise left non-leaf node
        this.pointers[0] = childNodes[0];
        for(int i=0; i<MAX_KEYS; i++){
            if(i < oldNodeKeys){
                this.keys[i] = findMin(childNodes[i+1]);
                this.pointers[i+1] = childNodes[i+1];
            }
            else{
                this.keys[i] =-1;
                this.pointers[i+1] = null;
            }
        }
        this.size = oldNodeKeys;

        //initialise right non-leaf node
        BTreeNode newNonLeaf = new BTreeNode();
        newNonLeaf.height = this.height;
        newNonLeaf.size = newNodeKeys;
        newNonLeaf.pointers[0] = childNodes[oldNodePointers];
        for(int i=0; i<newNodeKeys; i++){
            newNonLeaf.keys[i] = findMin(childNodes[oldNodePointers+i+1]);
            newNonLeaf.pointers[i+1] = childNodes[oldNodePointers+i+1];
        }

        //return parent node
        BTreeNode parent = new BTreeNode();
        parent.insertKey(findMin(childNodes[oldNodePointers]), this, newNonLeaf);
        parent.height = this.height +1;
        return parent;
    }

    private static int findMin(BTreeNode node) {
        return node.height == 0 ? node.keys[0] : findMin((BTreeNode) node.pointers[0]);
    }
    public BTreeNode insertLeafNode(int key, int address){
        // if node is not full insert key
        if (this.size < MAX_KEYS){
            this.insertKey();
            return this;
        }

        BTreeNode newLeaf = new BTreeNode();
        int newNodeSize=0;
        //split the node
        for(int i=MIN_KEYS;i<MAX_KEYS; i++){
            newLeaf.keys[newNodeSize] = this.keys[MIN_KEYS];
            newLeaf.pointers[newNodeSize] = this.pointers[MIN_KEYS];
            this.deleteKey(this.keys[MIN_KEYS]);
            newNodeSize++;
        }
        newLeaf.size = newNodeSize;

        //insert key into corr node
        if (key < newLeaf.keys[0]){
            newLeaf.insertKey(key, address);
        }
        else{
            this.insertKey(key, address);
        }
        // check this.size == newLeaf.size  or this.size == newLeaf.size+1
        if (this.size < newLeaf.size){
            this.insertKey(newLeaf.keys[0], newLeaf.pointers[0]);
            newLeaf.deleteKey(newLeaf.keys[0]);
        }
        else if(this.size> newLeaf.size+1){
            newLeaf.insertKey(this.keys[this.size-1], this.pointers[this.size-1]);
            this.deleteKey(this.keys[this.size-1]);
        }
        // arrange last pointer
        newLeaf.pointers[MAX_KEYS] = this.pointers[MAX_KEYS];
        this.pointers[MAX_KEYS] = newLeaf;

        //return parent node
        BTreeNode parent = new BTreeNode();
        parent.insertKey(newLeaf.keys[0], this, newLeaf);
        parent.height = 1;
        return parent;
    }

    //only for leaf node
    public void insertKey(int key, Object pointer){
        for(int i=0;i<this.size;i++){
            if(key > this.keys[i]){
                for (int j=this.size;j>i;j--){
                    this.keys[j] = this.keys[j-1];
                    this.pointers[j] = this.pointers[j-1];
                }
                this.keys[i] = key;
                this.pointers[i] = pointer;
                this.size++;
                return;
            }
        }
    }

    //only for non leaf node
    public void insertKey(int key, Object pointer1, Object pointer2){
        int i;
        for(i=0;i<this.size;i++){
            if(key > this.keys[i]){
                for (int j=this.size;j>i;j--){
                    this.keys[j] = this.keys[j-1];
                    this.pointers[j + 1] = this.pointers[j];
                    this.pointers[j] = this.pointers[j-1];
                }
                this.keys[i] = key;
                this.pointers[i] = pointer1;
                this.pointers[i+1] = pointer2;
                this.size++;
                break;
            }
        }
    }

    public int deleteKey(int key){
        for (int i =0;i<this.size;i++){
            if(this.keys[i] == key) {
                this.keys[i] = -1;
                this.pointers[i] = null;
                for (int j=i;j<this.size-1;j++){
                    this.keys[j] = this.keys[j+1];
                    this.keys[j+1] = -1;
                    this.pointers[j] = this.pointers[j+1];
                    this.pointers[j+1] = null;
                }
                this.size--;
                break;
            }
        }
    }

    public int[] search (int min, int max) {return this.search(min, max, true);}

    private int[] search(int min, int max, boolean printNodes){
        int count=0;
        if (printNodes){
            System.out.println("Accessing nodes:");
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        BTreeNode curNode;

        int ptr, i;
        //get the min leaf node
        while(curNode.height>0){
            if(printNodes){
                count++;
                System.out.printf(curNode.getContent()+"\n");
            }
            for(i=0; i< curNode.size;i++){
                if(min < curNode.keys[i]){
                    ptr = i;
                    break;
                }
            }
            if (i== curNode.size) ptr = curNode.size;
            curNode = (BTreeNode) curNode.pointers[ptr];
        }

        while(curNode!=null){ // scan leaf nodes
            if(printNodes){
                count++;
                System.out.printf(curNode.getContent());
            }
            for(int i=0;i< curNode.size;i++){
                if(curNode.keys[i] >= min && curNode.keys[i] <= max) { // if within range add to list
                    result.add(curNode.pointers[i]);
                }
                else if(curNode.keys[i] > max){ //if max is reached
                    System.out.println("Number of Nodes accessed: "+ count);
                    return result.stream().mapToInt(i -> i).toArray();
                }
            }
            curNode = (BTreeNode) curNode[MAX_KEYS]; // go to next node
        }

    }
}
