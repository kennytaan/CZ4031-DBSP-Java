package MemoryPool;

import java.util.ArrayList;
import java.util.Arrays;
import static MemoryPool.Utils.*;

public class BTreeNode {
    public static int MAX_KEYS = NUMOFKEYS;
    private static int MAX_POINTERS = NUMOFPOINTERS;
    private static int MIN_KEYS = MINKEYS;
    private static int MIN_NON_LEAF = NUMOFKEYS/2;
    private static int root = 0;
    private static int numOfNodes = 0;
    private int[] keys = new int[NUMOFKEYS];
    private Object[] pointers = new Object[NUMOFPOINTERS];
    private int size = 0;
    private int height = 0;
    private static int numOfDeleted = 0;

    // root of Btree has highest height of tree
    public BTreeNode(){
        for (int i=0;i<NUMOFKEYS;i++){
            this.keys[i] = -1;
        }
    }

    public String getContent() {
//        String node = "Node reference: " + super.() + "\n";
        String keysStr = "Keys: " + Arrays.toString(this.keys) + "\n";
        String ptrStr = "Pointers: " + Arrays.toString(this.pointers) + "\n";
        return keysStr + ptrStr;
    }

    public void brandNewTree(){
        MAX_KEYS = NUMOFKEYS;
        MAX_POINTERS = NUMOFPOINTERS;
        MIN_KEYS = MINKEYS;
        MIN_NON_LEAF = NUMOFKEYS/2;
        root = 0;
        numOfNodes = 0;
        numOfDeleted = 0;
    }
    public int[] getKeys() {
        return this.keys;
    }

    public int getNumOfNodes() {
        return numOfNodes;
    }

    public int getNumOfDeleted(){
        return numOfDeleted;
    }
    public Object[] getPointers() {
        return this.pointers;
    }

    public int getNoOfNodes(){
        int noOfNodes = 1;
        if (height == 0){
            return noOfNodes;
        }
        else{
            for(int i = 0; i <= this.size; i++){
                noOfNodes = noOfNodes + ((BTreeNode) pointers[i]).getNoOfNodes();
            }
            return noOfNodes;
        }
    }

    public int getHeight(){
        return height;
    }

    //insert the key here
    public BTreeNode insertNode(int key, int address){
        // if this is leaf
        if (this.height == 0){
            //new tree
            if(this.keys[0] == -1){
                brandNewTree();
                numOfNodes++;
            }
            return this.insertLeafNode(key, address);
        }
        // get child
        int childIndex = 0;
        for(int i=0;i<this.size && key >= this.keys[i]; i++){
            childIndex = i+1;
        }
        // this is a non leaf node
        //insert to child
        BTreeNode child = (BTreeNode) this.pointers[childIndex];
        this.pointers[childIndex] = child.insertNode(key, address);
        // if same parent key is returned
        if (child == this.pointers[childIndex]){
            return this;
        }
        BTreeNode newParent = (BTreeNode) this.pointers[childIndex];
        // if current non leaf is not full
        if( this.size< MAX_KEYS){
            this.insertKey(newParent.keys[0], newParent.pointers[0], newParent.pointers[1]);
            return this;
        }
        // current non leaf is full
        // number of keys allocated to new right node
        int newNodeKeys = MAX_KEYS/2;
        int newNodePointers = newNodeKeys+1;
        // number of keys allocated to left node
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
        // which we are going to reuse the current node
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
        // we are going to create a new node for this
        BTreeNode newNonLeaf = new BTreeNode();
        newNonLeaf.height = this.height;
        newNonLeaf.size = newNodeKeys;
        newNonLeaf.pointers[0] = childNodes[oldNodePointers];
        for(int i=0; i<newNodeKeys; i++){
            newNonLeaf.keys[i] = findMin(childNodes[oldNodePointers+i+1]);
            newNonLeaf.pointers[i+1] = childNodes[oldNodePointers+i+1];
        }

        //new sibling node created add nodes
        numOfNodes++;

        //return parent node
        BTreeNode parent = new BTreeNode();
        parent.insertKey(findMin(childNodes[oldNodePointers]), this, newNonLeaf);
        parent.height = this.height +1;
        // if new root is created
        if (this.root< parent.height){
            this.root = parent.height;
            numOfNodes++;
        }
        return parent;
    }


    private static int findMin(BTreeNode node) {
        return node.height == 0 ? node.keys[0] : findMin((BTreeNode) node.pointers[0]);
    }
    public BTreeNode insertLeafNode(int key, int address){
        // if node is not full insert key
        if (this.size < MAX_KEYS){
            this.insertKey(key, address);
            return this;
        }
        // node is full
        numOfNodes++;
        BTreeNode newLeaf = new BTreeNode();
        int newNodeSize=0;

        //split the node

        for(int i=MIN_KEYS;i<MAX_KEYS; i++){
            newLeaf.keys[newNodeSize] = this.keys[MIN_KEYS];
            newLeaf.pointers[newNodeSize] = this.pointers[MIN_KEYS];
            this.deleteLeafPointer(this.pointers[MIN_KEYS]);
            newNodeSize++;
        }
        newLeaf.size = newNodeSize; //new leaf is right node

        //insert key into corr node
        if (key < newLeaf.keys[0]){
            this.insertKey(key, address);
        }
        else{
            newLeaf.insertKey(key, address);
        }
        // check this.size == newLeaf.size  or this.size == newLeaf.size+1
        if (this.size < newLeaf.size){
            this.insertKey(newLeaf.keys[0], newLeaf.pointers[0]);
            newLeaf.deleteLeafPointer(newLeaf.pointers[0]);
        }
        else if(this.size> newLeaf.size+1){
            newLeaf.insertKey(this.keys[this.size-1], this.pointers[this.size-1]);
            this.deleteLeafPointer(this.pointers[this.size-1]);
        }
        // arrange last pointer
        newLeaf.pointers[MAX_KEYS] = this.pointers[MAX_KEYS];
        this.pointers[MAX_KEYS] = newLeaf;

        //return parent node
        BTreeNode parent = new BTreeNode();
        parent.insertKey(newLeaf.keys[0], this, newLeaf);
        parent.height = 1;
        if(this.root < parent.height) {
            this.root = parent.height;
            numOfNodes++;
        }
        return parent;
    }

    //only for leaf node
    //insert a key and sort the node
    public void insertKey(int key, Object pointer){
        for(int i=0;i<MAX_KEYS;i++){
            if(key < this.keys[i] || this.keys[i] == -1){
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
    //insert a key with both child pointers and sort the node
    public void insertKey(int key, Object pointer1, Object pointer2){
        int i;
        for(i=0;i<this.size+1;i++){
            if(key < this.keys[i] || this.keys[i] == -1){
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

    //delete key from a node and sort the node
    public void deleteLeafPointer(Object pointer){
        for (int i =0;i<this.size;i++){
            if(this.pointers[i] == pointer) {
                this.keys[i] = -1;
                this.pointers[i] = null;
                int j;
                for (j=i;j<this.size-1;j++){
                    this.keys[j] = this.keys[j+1];
                    this.pointers[j] = this.pointers[j+1];
                }
                this.keys[j] = -1;
                this.pointers[j] = null;
                this.size--;
                break;
            }
        }
    }

    public int[] search(int min, int max) {return this.search(min, max, true);}

    public int[] search(int min, int max, boolean printNodes){
        // count number of access
        int count=0;
        if (printNodes){
            System.out.println("Accessing nodes:");
        }
        ArrayList<Integer> result = new ArrayList<Integer>(Arrays.asList(-1));
        BTreeNode curNode;

        int ptr=-1, i;
        //get the min leaf node
        curNode = this;
        while(curNode.height>0){
            if(printNodes){
                //increment accesses
                count++;
                System.out.printf(curNode.getContent()+"\n");
            }
            ptr =0;
            for(i=0; i< curNode.size && min > curNode.keys[i];i++){ // scan the node for child with key
                ptr = i+1;
            }
            if (i== curNode.size) ptr = curNode.size;
            curNode = (BTreeNode) curNode.pointers[ptr];
        }

        while(curNode!=null){ // scan leaf nodes
            if(printNodes){
                count++;
                System.out.printf(curNode.getContent());
            }
            for(i=0;i< curNode.size;i++){
                if(curNode.keys[i] >= min && curNode.keys[i] <= max) { // if within range add to list
                    if (result.get(0) == -1) result.remove(0);
                    result.add((Integer) curNode.pointers[i]);
                }
                else if(curNode.keys[i] > max || curNode.keys[i] == -1){ //if max is reached
                    if(printNodes)
                        System.out.println("Number of Nodes accessed: "+ count);
                    return result.stream().mapToInt(num->num).toArray();
                }
            }
            curNode = (BTreeNode) curNode.pointers[MAX_KEYS]; // go to next node
            if(curNode == null){
                if (printNodes) System.out.println("Reached end of database");
                else if (result.get(0) == -1) System.out.println("Key not found!");
            }
        }
        System.out.println("Number of Nodes accessed: "+ count);
        return result.stream().mapToInt(num->num).toArray();
    }

    public BTreeNode removeNode(int key){
        // remove all duplicates
        BTreeNode rootNode = this;
        int[] address = this.search(key, key, false);
        for (int i=0; i< address.length;i++){
            rootNode = rootNode.remove(key, new ArrayList<BTreeNode>(), new ArrayList<Integer>());
        }
        return rootNode;
    }

    public BTreeNode remove(int key, ArrayList<BTreeNode> parents, ArrayList<Integer> parentPointer){
        BTreeNode curNode = this;
        int i=0;
        int ptr;
        // this gives me the leaf node
        while(curNode!=null) {
            while (curNode.height != 0) {

                //this gets the child position in parent
                // we need to check the node before to see if duplicates are there
                for (i = 0; i < curNode.size && key > curNode.keys[i]; i++) {
                    continue;
                }
                //remember the parent node and the position f the child node in parent
                //treat this list as a stack
                parents.add(0, curNode);
                parentPointer.add(0, i);

                curNode = (BTreeNode) curNode.pointers[i];//child node with key
            }
            // find key and delete in leaf node
            boolean found = false;
            for (i = 0; i < curNode.size; i++) {
                if (curNode.keys[i] == key) {
                    curNode.deleteLeafPointer(curNode.pointers[i]);
                    found = true;
                    break;
                }
            }

            if(found){
                break;
            }
            else {
                //get next node and update parents and pointer lists
                curNode = getNextNode(parents,parentPointer); //this the one , this line T-T
            }
        }
        if(curNode == null){
            System.out.println("not found");
            return this;
        }

        if(curNode == this){
            if(curNode.size == 0){
                System.out.println("Empty tree!");
            }
            return this;
        }
        // if the node from deletion has lesser than minimum keys
        if( curNode.size < MIN_KEYS){
            BTreeNode parentNode = parents.get(0);
            int childIndex = parentPointer.get(0);

//            parentPointer.remove(0);
            // check if left sibling exist
            if(childIndex-1 >= 0){
                BTreeNode leftSibling = (BTreeNode) parentNode.pointers[childIndex-1];
                // check if left sibling has enough keys
                if(leftSibling.size>MIN_KEYS){
                    //take keys from sibling
                    curNode.insertKey(leftSibling.keys[leftSibling.size-1], leftSibling.pointers[leftSibling.size-1]);
                    leftSibling.deleteLeafPointer(leftSibling.pointers[leftSibling.size-1]);
                    //update the parent keys
                    parentNode.keys[childIndex-1] =  curNode.keys[0];
                    return this;
                }
            }
            // check if right sibling exists
            if(childIndex+1 < parentNode.size+1){
                BTreeNode rightSibling = (BTreeNode) parentNode.pointers[childIndex+1];
                // check if right sibling has enough keys
                if(rightSibling.size>MIN_KEYS){
                    curNode.insertKey(rightSibling.keys[0], rightSibling.pointers[0]);
                    rightSibling.deleteLeafPointer(rightSibling.pointers[0]);
                    // update the parents
                    parentNode.keys[childIndex] = rightSibling.keys[0];
                    return this;
                }
            }
            // no siblings to get keys
            // attempt to merge with siblings
            //check if left sibling exists
            if(childIndex-1 >= 0){
                BTreeNode leftSibling = (BTreeNode) parentNode.pointers[childIndex-1];
                int j;
                for(i=leftSibling.size,j=0;j< curNode.size;j++,i++){
                    leftSibling.keys[i] = curNode.keys[j];
                    leftSibling.pointers[i] = curNode.pointers[j];
                    leftSibling.size++;
                }
                // copy over last pointer
                leftSibling.pointers[MAX_POINTERS-1] = curNode.pointers[MAX_POINTERS-1];
//                parentPointer.add(childIndex);
                BTreeNode result = removeInternal(parents, parentPointer);

                curNode.emptyALl();
                this.numOfNodes--;
                this.numOfDeleted++;
                if(result == this) return this;
                else{
                    return result;
                }
            }

            //merge right sibling if right sibling exists
            if(childIndex+1 < parentNode.size+1){

                BTreeNode rightSibling = (BTreeNode) parentNode.pointers[childIndex+1];

                int j;
                for(i=curNode.size,j=0;j< rightSibling.size;j++,i++){
                    curNode.keys[i] = rightSibling.keys[j];
                    curNode.pointers[i] = rightSibling.pointers[j];
                    curNode.size++;
                }
                // copy over last pointer
                curNode.pointers[MAX_POINTERS-1] = rightSibling.pointers[MAX_POINTERS-1];
                parentPointer.remove(0);
                parentPointer.add(childIndex+1);
                BTreeNode result = removeInternal(parents, parentPointer);
                rightSibling.emptyALl();
                this.numOfNodes--;
                this.numOfDeleted++;
                if(result == this) return this;
                else{
                    return result;
                }
            }

        }
        return this;
    }
    public BTreeNode getNextNode(ArrayList<BTreeNode> parents, ArrayList<Integer> parentPointer){
        while(parentPointer.size()!=0) {
//            BTreeNode gparent =
            // if more than Max num of pointer then go to parent to look for parent of next node
            if (parentPointer.get(0) + 1 > MAX_POINTERS-1) {
                parentPointer.remove(0);
                parents.remove(0);
            }
            else if (parents.get(0).pointers[parentPointer.get(0)+1] != null){
                // found a parent for the next node
                parentPointer.add(0,parentPointer.get(0)+1);
                parentPointer.remove(1);
                // return parent of next node
                return (BTreeNode) parents.get(0).pointers[parentPointer.get(0)];
            }
            else{
                parentPointer.add(0,parentPointer.get(0)+1);
                parentPointer.remove(1);
            }
        }
        //this is already the last node
        return null;
    }

    public void emptyALl(){
        int i;
        for(i=0;i<MAX_KEYS;i++){
            this.keys[i] = -1;
            this.pointers[i] = null;
        }
        this.pointers[i] = null;
    }

    //after merged, call removeInternal to delete the pointer from parents
    public BTreeNode removeInternal( ArrayList<BTreeNode> parentList, ArrayList<Integer> pointerList){

        // get the parent node from the makeshift stack
        BTreeNode parent = parentList.get(0);
        parentList.remove(0);

        // get the pointer index in parent node
        int parentPtr = pointerList.get(0);
        pointerList.remove(0);
        //if the parent is the root and only 1 key
        //lets kill the root and take the child
        if(parent.height == this.root && parent.size == 1){
            this.root--;
            return (BTreeNode) parent.pointers[0];
        }
//        System.out.println("CFM delete wrong");
//        System.out.println(parentPtr);
        //remove the key from parent
        //shift all keys forward
        for(int j=parentPtr; j< parent.size-1;j++){
            parent.keys[j] = parent.keys[j+1];
            parent.pointers[j] = parent.pointers[j+1];
        }

        parent.pointers[parent.size-1] = parent.pointers[parent.size];
        parent.pointers[parent.size] = null;
        parent.keys[parentPtr-1] = findMin((BTreeNode) parent.pointers[parentPtr]);

        parent.keys[parent.size-1] = -1;
        parent.pointers[parent.size] = null;
        parent.size--;
        // parent has enough keys
        //just return self

        if(parent.size >= MIN_NON_LEAF){
            return this;
        }

        // if code reaches here, there is underflow in parent
        if(parent.height == this.root){ //if we are root, nothing much you can do here, return parent
            return parent;
        }

        BTreeNode greaterParent = parentList.get(0);
        int parentIndex = pointerList.get(0);
        // find the position of parent in the greater of parent
//        for(int i=0;i< greaterParent.size+1;i++){
//            if(greaterParent.pointers[i] == parent){
//                parentIndex = i;
//            }
//        }
        //check if left sibling exists
        if(parentIndex > 0){
            BTreeNode leftSibling = (BTreeNode) greaterParent.pointers[parentIndex-1];
            //check if we can take key from leftSibling
            if(leftSibling.size > MIN_NON_LEAF){
                parent.insertKey(leftSibling.keys[leftSibling.size-1], leftSibling.pointers[leftSibling.size]);
                leftSibling.deleteLeafPointer(leftSibling.pointers[leftSibling.size]);
                //nicely robbed, now we will copy this key over to the greater parent
                greaterParent.keys[parentIndex] = parent.keys[0];
                return this;
            }
        }

        //check if right sibling exists
        if (parentIndex +1 < greaterParent.size+1){
            BTreeNode rightSibling = (BTreeNode) greaterParent.pointers[parentIndex+1];
            //check if rightSibling has any keys to take
            if(rightSibling.size > MIN_NON_LEAF){
                parent.insertKey(rightSibling.keys[0], rightSibling.pointers[0]);
                rightSibling.deleteLeafPointer(rightSibling.pointers[0]);
                //taken nicely, copy this right sibling's key to the greater parent
                greaterParent.keys[parentIndex+1] = rightSibling.keys[0];
                return this;
            }
        }

        //no siblings to steal keys from
        //try to merge with left sibling instead
        if(parentIndex > 0){
            BTreeNode leftSibling = (BTreeNode) greaterParent.pointers[parentIndex-1];
            int i,j;
            for(i=leftSibling.size,j=0;j< parent.size+1;j++,i++){
                // find min key for each pointer node inserted to left sibling
                leftSibling.keys[i] = findMin((BTreeNode) parent.pointers[j]);
                leftSibling.pointers[i+1] = parent.pointers[j];
                leftSibling.size++;
            }
            parent.emptyALl();
            this.numOfNodes--;
            this.numOfDeleted++;
            pointerList.add(parentIndex);
            return removeInternal(parentList, pointerList);
        }

        //if left sibling does not exist, merge with the right sibling
        if (parentIndex +1 < greaterParent.size+1) {
            BTreeNode rightSibling = (BTreeNode) greaterParent.pointers[parentIndex+1];
            int i, j;
            for(i=parent.size,j=0;j< rightSibling.size+1;j++,i++){
                parent.keys[i] = findMin((BTreeNode) rightSibling.pointers[j]);
                parent.pointers[i+1] = rightSibling.pointers[j];
                parent.size++;
            }
            rightSibling.emptyALl();
            this.numOfNodes--;
            this.numOfDeleted++;
            pointerList.add(parentIndex+1);
            return removeInternal(parentList, pointerList);
        }
        return this;
    }

}

