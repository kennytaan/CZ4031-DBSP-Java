package MemoryPool;

import java.io.IOException;

//for ByteArrayOutputStream
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static MemoryPool.Utils.NO_OF_BLOCKS;
import static MemoryPool.Utils.BLOCKSIZE;
import static MemoryPool.Utils.RECORDSPERBLOCK;
import static MemoryPool.Utils.RECORDSIZE;


public class DiskStorage {
    
    //store blocks in an array
    // to access certain block --> blocks[blockno]
    private Block[] blocks;
    private int currentBlock;

    //initialize
    public DiskStorage(){
        this.blocks = new Block[NO_OF_BLOCKS];
        this.currentBlock = 0; 
    }

    public Block getCurrentBlock(){
        return blocks[currentBlock];
    }

    public int getNoOfBlocks(){
        return currentBlock + 1;
    }

    public void insertRecord(Record record) throws IOException{
        byte[] tConst_b = stringToByteArray(record.getTConst());
        byte[] avgRating_b = floatToByteArray(record.getAverageRating());
        byte[] numVotes_b = intToByteArray(record.getNumVotes());
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

    }

    

    // Functions to help convert to byte[]
    private static byte[] stringToByteArray(String value){
        byte[] output = value.getBytes(StandardCharsets.UTF_8);
        return output;
    }

    private static byte[] floatToByteArray(float value) {
        int intBits = Float.floatToIntBits(value);
        return new byte[]{
                (byte) (intBits >> 24), (byte) (intBits >> 16), (byte) (intBits >> 8), (byte) (intBits)};
    }

    private static final byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }


    //for experiment 1: calculating the database size in MB
    //database size = no of blocks x records per block x record size
    public double getDatabaseSizeInMB(){


        //Record size in a block = records per block x record size
        int recordSizeInABlock = RECORDSIZE * RECORDSPERBLOCK;

        //Total size of the database = no of blocks x record size in a block
        int databaseSizeInBytes = (this.getNoOfBlocks() - 1) * recordSizeInABlock;

        //Convert database size (in bytes) to MB
        double databaseSizeInMB =(double)(databaseSizeInBytes / (1024.0 * 1024.0));

        return databaseSizeInMB;
    }


}

