package MemoryPool;


//import java.lang.Math;

public class Utils {

	//10char for tConst with 2bytes for each char, 4 bytes for float averageRating, 4 bytes for int
	public static final int RECORDSIZE = (10 * 2) + 4 + 4; 
	
	//BLOCK SIZE = 200B -> stated in project docs
	public static final int BLOCKSIZE = 200;
	
	//DISK CAPACITY = 500MB -> 100~500MB choose greatest first (can change)
	public static final int DISKCAPACITY = 500 * 1024 * 1024; //convert to bytes
	
	//no of records in a block
	public static final int RECORDSPERBLOCK = BLOCKSIZE/RECORDSIZE;
	
	//total no of blocks
	public static final int NO_OF_BLOCKS = DISKCAPACITY/BLOCKSIZE;
	
	//total no of records
	public static final int NO_OF_RECORDS = NO_OF_BLOCKS * RECORDSPERBLOCK;
	/*
    BLOCKSIZE
    n+1 pointers (8 Bytes)
    n integer keys (4 Bytes)
    BLOCKSIZE = 8n + 8 +4n
    1 BLOCK can fit in (BLOCKSIZE-8/12) keys
     */
	public static final int NUMOFKEYS = ((BLOCKSIZE-8)/12);

	public static final int NUMOFPOINTERS= NUMOFKEYS+1;

	public static final int MINKEYS =  (NUMOFKEYS+1)/2;
	
}

