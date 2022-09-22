import java.io.*;
import java.util.ArrayList;
//import java.util.List;
//import java.util.StringTokenizer;
//import java.util.ArrayList;
//import java.io.IOException;

//import java.io.FileWriter;
//import java.io.BufferedWriter;

//import java.util.Arrays;

import MemoryPool.DiskStorage;
import MemoryPool.Record;

//import static MemoryPool.Utils.RECORDSPERBLOCK;
//import static MemoryPool.Utils.RECORDSIZE;

public class Main{
	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		DiskStorage disk = readFile("data.tsv"); //reading tsv file

		//Experiment 1
		System.out.println("Experiment 1:");
		System.out.println("The number of blocks: "+ disk.getNoOfBlocks());
		System.out.println("The size of database (in terms of MB): " + disk.getDatabaseSizeInMB() +"MB");
		System.out.println("\n");
		System.out.println("==================================================");
		System.out.println("\n");


		//Experiment 2




		//Experiment 3






		//Experiment 4




		
	}












	//function to read the tsv file
	private static DiskStorage readFile(String filename) throws FileNotFoundException, IOException {
		
		//initialize
		DiskStorage d = new DiskStorage();
		ArrayList<Record> tempData = new ArrayList<>(); 
		File recordFile = new File(filename);
		int count = 0;

		//read line by line of tsv file
		//Creates a buffering character-input stream that uses a default-sized input buffer.
		try (BufferedReader TSVReader = new BufferedReader(new FileReader(recordFile))) {
			String line = null;

			//reading line by line
			while ((line = TSVReader.readLine()) != null) {
				count++;
				if (count==1) continue;
				//split each line to string array: [0] for tConst, [1] for avg Rating, [2] for num of votes
				String[] dataRecord = line.split("\t"); 
				
				//getting the values 
				String tConst = dataRecord[0];
				float avgRating = Float.parseFloat(dataRecord[1]);
				int numOfVotes = (int)Integer.valueOf(dataRecord[2]);
				
				//if tConst only 9 char, add one space to fill to 10 spaces as stated in utils
				if (tConst.length() == 9) {
					tConst = tConst + " ";
				}

				//store the record line as a temp record
				Record tempRecord = new Record(tConst.toString(), avgRating, numOfVotes);
				tempData.add(tempRecord); //storing the temp record to temp arraylist tempData
				
				
				////////TODO: insert the records to the diskstorage////////
				d.insertRecord(tempRecord);

			}

			//System.out.println(count);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return d;
	}

}