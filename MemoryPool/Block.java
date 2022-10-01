package MemoryPool;

//import java.util.Arrays;

import static MemoryPool.Utils.BLOCKSIZE;

public class Block {
    /*

     */
    private byte[] data;
    private int blockOffset;


    public Block(){
        this.data = new byte[BLOCKSIZE];
        this.blockOffset = 0;
    }

    public byte[] getData(){
        return data;
    }

    public int getOffset(){
        return blockOffset;
    }

    public void addData(byte[] record) {
        System.arraycopy(record, 0, this.data, this.blockOffset, record.length);
        this.blockOffset = this.blockOffset + record.length;
    }


    /*TO BE ADDED */
    public String getHexData() {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for(byte b: data)
            sb.append(String.format("%02x", b));
        return "0x" + sb.toString();
    }

    // public void addData(byte[] record){
    //     //to do
    // }

    // public void deleteData(byte[] record){
    //     //to do
    // }
}
