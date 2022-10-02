package MemoryPool;

import java.io.IOException;

//for ByteArrayOutputStream
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static MemoryPool.Utils.NO_OF_BLOCKS;
import static MemoryPool.Utils.BLOCKSIZE;
import static MemoryPool.Utils.RECORDSPERBLOCK;
import static MemoryPool.Utils.RECORDSIZE;


public class DiskStorage {
    
    //store blocks in an array
    // to access certain block --> blocks[blockno]
    private Block[] blocks;
    BTreeNode bpt;
    private int currentBlock;

    //initialize
    public DiskStorage(){
        this.blocks = new Block[NO_OF_BLOCKS];
        this.currentBlock = 0; 
        this.bpt = new BTreeNode();
    }

    public Block getCurrentBlock(){
        return blocks[currentBlock];
    }

    public int getNoOfBlocks(){
        return currentBlock + 1;
    }
    
    public BTreeNode getBPT() {
        return bpt;
    }

    public void insertRecord(Record record) throws IOException{
        byte[] tConst_b = charArrToByteArray(record.getTConst().toCharArray());
        byte[] avgRating_b = floatToByteArray(record.getAverageRating());
        byte[] numVotes_b = intToByteArray(record.getNumVotes());
//        byte[] tConst_b = record.getTConst().getBytes("UTF-8");

        //concatenate byte array
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        byteOutputStream.write(tConst_b);
        byteOutputStream.write(avgRating_b);
        byteOutputStream.write(numVotes_b);
        byte[] record_b = byteOutputStream.toByteArray();
        // TODO: add data into blocks
        // TODO: determine the address of the record
        /* A brief description of address:
         * Assuming that all blocks are stored sequentially on the disk,
         * in order to locate the first byte of a record,
         * blockNumber = address / BLOCK_SIZE
         * blockOffset = address % BLOCK_SIZE
         * Then the following gives the value of the first byte of a record:
         * this.blocks[blockNumber].getData()[blockOffset]
         */

        int offset;

        if (blocks[currentBlock] == null) {
            blocks[currentBlock] = new Block();
        }

        if (blocks[currentBlock].getOffset() < (BLOCKSIZE - RECORDSIZE)) {
            offset = blocks[currentBlock].getOffset();
            blocks[currentBlock].addData(record_b);
        } else {
            currentBlock++;
            blocks[currentBlock] = new Block();
            offset = blocks[currentBlock].getOffset();
            blocks[currentBlock].addData(record_b);
        }
        //System.out.printf("adding to block no. " + currentBlock + "\n");

        //inserting into bplus tree
        int address = this.currentBlock * BLOCKSIZE + offset; //address = block tht is inserted x blocksize + offset 
        bpt = bpt.insertNode(record.getNumVotes(), address);
        
    }

    /* TO BE ADDED */
    public Record[] searchForRecord(int min, int max){

       

        //return the addresses 
        int[] searchResults = bpt.search(min, max);
        //System.out.println(searchResults);
        ArrayList<Record> recordsList = new ArrayList<>(); 

        //making hashmap to store the address 
        HashSet<Integer> testHash = new HashSet<Integer>();
        int noOfDataBlocksAccessed = 0;

        //looping through the searchresults
        System.out.println("The content of data blocks accessed: ");
        for(int i=0;i<searchResults.length; i++){
            
            //get the address of the key
            int address = searchResults[i];
            //System.out.println(address);

            //using the formula of the address to trace back the block where the key is stored in disk
            int targetBlock = address/ BLOCKSIZE;

            //using the formula to trace back the block offset
            int targetBlockOffset = address % BLOCKSIZE;

            //if the targetBlock is empty block
            if (blocks[targetBlock] == null){
                
                break;
            }

            //if successfully added to hashmap, get the hex data of the block byte[]
            boolean added = testHash.add(targetBlock);
            if (added){
                // if(noOfDataBlocksAccessed < 5){
                //     System.out.println((noOfDataBlocksAccessed+1) + "the data block: " + blocks[targetBlock].getHexData());
                // }
                noOfDataBlocksAccessed+=1;
                
            }

            
            //getting the record data and convert it back to its supposed variable (char[])
            byte[] recordData = Arrays.copyOfRange(blocks[targetBlock].getData(), targetBlockOffset, targetBlockOffset+BLOCKSIZE);
        

            //getting the tconst data in byte[] from the first 20bytes and convert to char[]
            byte[] tConstData = Arrays.copyOfRange(recordData, 0, 20);
            char[] tConstchar = convertFromByteArrToCharArr(tConstData);
            String tConstActual = String.valueOf(tConstchar);

            //getting the rating in bytep[] from the next 4 bytes and convert to float
            byte[] avgRatingData = Arrays.copyOfRange(recordData, 20, 24);
            float avgRatingActual = convertFromByteArrToFloat(avgRatingData);
//            float avgRatingActual = ByteBuffer.wrap(avgRatingData).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            //getting the num votes in byte[] from the next 4 bytes and convert to int
            byte[] numVotesData = Arrays.copyOfRange(recordData, 24,28);
            int numVotesActual = convertFromByteArrToInt(numVotesData);
//            int numVotesActual = ByteBuffer.wrap(numVotesData).getInt();

            //make a new record object w the datas retrieved
            Record recordToBeAdded = new Record(tConstActual,avgRatingActual,numVotesActual);

            //add this record into the arraylist of search results records
            recordsList.add(recordToBeAdded);
            
        }

        //saving the recordslist into a new record array
        Record[] recordsResults = new Record[recordsList.size()];
        //looping thru recordsList to get and add the records into the records array
        for(int i =0; i<recordsList.size();i++){
            recordsResults[i] = recordsList.get(i);
        }
        //System.out.println(recordsResults);

        final Object[][] table = new String[recordsResults.length][];
        for(int i=0;i<recordsResults.length;i++){
            table[i]= new String[] { "Record "+i, recordsResults[i].getTConst(), String.valueOf(recordsResults[i].getAverageRating()), String.valueOf(recordsResults[i].getNumVotes())  };
        }
        for(int i =0;i<table.length;i++){
            if((i/RECORDSPERBLOCK) == 5) break;
            if (i%RECORDSPERBLOCK ==0){
                System.out.println("data block "+(i/RECORDSPERBLOCK+1));
            }
            System.out.format("%15s%15s%15s%15s%n", table[i]);
        }

        System.out.println("The number of data blocks accessed is " + noOfDataBlocksAccessed + "\n");
        return recordsResults;

    }

    

    // Functions to help convert to byte[]
    private static byte[] charArrToByteArray(char[] value){
        byte[] b = new byte[value.length << 1];
        for (int i = 0; i < value.length; i++) {
            int bpos = i << 1;
            b[bpos] = (byte) ((value[i] & 0xFF00) >> 8);
            b[bpos + 1] = (byte) (value[i] & 0x00FF);
        }
        return b;
    }

    private static byte[] floatToByteArray(float value) {
        int intBits = Float.floatToIntBits(value);
        return new byte[]{
                (byte) (intBits >> 24), (byte) (intBits >> 16), (byte) (intBits >> 8), (byte) (intBits)};
//        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value).array();
    }

    private static final byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
//        return ByteBuffer.allocate(4).putInt(value).array();
    }



    /*TO BE ADDED */
    //Functions to help convert from byte[] back to char[], int, float
    private static char[] convertFromByteArrToCharArr(byte[] bytes){
        Charset charset = Charset.forName("UTF-8");
        CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(bytes));
        char[] arr = Arrays.copyOf(charBuffer.array(), charBuffer.limit());
        char[] result = new char[10];
        for (int i = 1; i < arr.length; i += 2) {
            result[i/2] = arr[i];
        }
        return result;
    }

    private static float convertFromByteArrToFloat(byte[] bytes){
        float result = ByteBuffer.wrap(bytes).getFloat();
        return result;
    }

    private static int convertFromByteArrToInt(byte[] bytes){
        int result = ByteBuffer.wrap(bytes).getInt();
        return result;
    }


    //for experiment 1: calculating the database size in MB
    //database size = no of blocks x records per block x record size
    public double getDatabaseSizeInMB(){

        //Record size in a block = records per block x record size
        int recordSizeInABlock = RECORDSIZE * RECORDSPERBLOCK;

        //Total size of the database = no of blocks x record size in a block
        int databaseSizeInBytes = (this.getNoOfBlocks() - 1) * recordSizeInABlock ;

        /*TO BE ADDED HERE */ 
        databaseSizeInBytes += this.getCurrentBlock().getOffset();

        //Convert database size (in bytes) to MB
        double databaseSizeInMB =(double)(databaseSizeInBytes / (1000000));

        return databaseSizeInMB;
    }


}

