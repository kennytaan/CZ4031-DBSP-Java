import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
//import java.util.List;
//import java.util.StringTokenizer;
//import java.util.ArrayList;
//import java.io.IOException;

import MemoryPool.BTreeNode;

//import java.io.FileWriter;
//import java.io.BufferedWriter;

//import java.util.Arrays;

import MemoryPool.DiskStorage;
import MemoryPool.Record;
import MemoryPool.Utils;

//import static MemoryPool.Utils.RECORDSPERBLOCK;
//import static MemoryPool.Utils.RECORDSIZE;

public class Main{
	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		DiskStorage diskfor200MB = readFile("data.tsv"); //reading tsv file

		System.out.println("EXPERIMENTS FOR 200MB");
		System.out.println("==================================================\n");

		//Experiment 1
		System.out.println("Experiment 1:");
		System.out.println("The number of blocks: "+ diskfor200MB.getNoOfBlocks());
		System.out.println("The size of database (in terms of MB): " + diskfor200MB.getDatabaseSizeInMB() +"MB");
		System.out.println("\n");
		//System.out.println("\n");


		//Experiment 2
		System.out.println("Experiment 2: ");
		System.out.println("The parameter n of the B+ tree: " + BTreeNode.MAX_KEYS);
		System.out.println("The number of nodes of the B+ tree: " + diskfor200MB.getBPT().getNoOfNodes());
		System.out.println("The height of the B+ tree, i.e the number of levels of the B+ tree: " + (diskfor200MB.getBPT().getHeight()+1));
		System.out.println("The content of the root node: \n" + diskfor200MB.getBPT().getContent() + "\n");
		//getting the first child node 
		BTreeNode firstChildNodeFor200MB = ((BTreeNode) diskfor200MB.getBPT().getPointers()[0]);
		System.out.println("The content of the 1st child node: \n" + firstChildNodeFor200MB.getContent() + "\n");

		//Experiment 3 /* TO BE ADDED */
		System.out.println("Experiment 3: ");
		System.out.println("Average of average rating: " + calcAvgRatingFromSearch(diskfor200MB.searchForRecord(500,500)) + "\n\n");

		//Experiment 4
		System.out.println("Experiment 4: ");
		System.out.println("Average of average rating: " + calcAvgRatingFromSearch(diskfor200MB.searchForRecord(30000, 40000)) + "\n\n");

		//Experiment 5
//		diskfor200MB.getBPT().search(1000,1000,true);
		System.out.println("Experiment 5: ");
		diskfor200MB.getBPT().removeNode(1000);
		System.out.println("Number of times that a node is deleted : " + diskfor200MB.getBPT().getNumOfDeleted());
		System.out.println("Number nodes of the updated B+ tree: " + diskfor200MB.getBPT().getNumOfNodes());
		System.out.println("Height of the updated B+ tree: " + diskfor200MB.getBPT().getHeight());
		System.out.println("content of the root node:");
		System.out.println(diskfor200MB.getBPT().getContent());
		System.out.println(((BTreeNode) diskfor200MB.getBPT().getPointers()[0]).getContent());
		//diskfor200MB.getBPT().search(1000, 1000);

//		diskfor200MB.getBPT().search(1000,1000,true);
		boolean option = false;
		while (!option){
			Scanner question = new Scanner(System.in);  // Create a Scanner object
    		System.out.println("Please choose an option to continue: \n");
			System.out.println("[1] Repeat Experiments 1-5 with new block size of 500MB");
			System.out.println("[2] Exit");

			String choice = question.nextLine();

			if (choice.equals("1")){
				
				/*add experiment 1-5 with new block size here */
				//Utils.BLOCKSIZE = 500;
				Utils.setBlockSize();
				DiskStorage diskfor500MB= readFile("data.tsv"); //reading tsv file
				System.out.println("\n");
				System.out.println("EXPERIMENTS FOR 500MB");
				System.out.println("==================================================\n");

				//Experiment 1
				System.out.println("Experiment 1:");
				System.out.println("The number of blocks: "+ diskfor500MB.getNoOfBlocks());
				System.out.println("The size of database (in terms of MB): " + diskfor500MB.getDatabaseSizeInMB() +"MB");
				System.out.println("\n");
				//System.out.println("\n");


				//Experiment 2
				System.out.println("Experiment 2: ");
				System.out.println("The parameter n of the B+ tree: " + BTreeNode.MAX_KEYS);
				System.out.println("The number of nodes of the B+ tree: " + diskfor500MB.getBPT().getNoOfNodes());
				System.out.println("The height of the B+ tree, i.e the number of levels of the B+ tree: " + (diskfor500MB.getBPT().getHeight()+1));
				System.out.println("The content of the root node: \n" + diskfor500MB.getBPT().getContent() + "\n");
				//getting the first child node 
				BTreeNode firstChildNodeFor500MB = ((BTreeNode) diskfor500MB.getBPT().getPointers()[0]);
				System.out.println("The content of the 1st child node: \n" + firstChildNodeFor500MB.getContent() + "\n");

				//Experiment 3 /* TO BE ADDED */
				System.out.println("Experiment 3: ");
				System.out.println("Average of average rating: " + calcAvgRatingFromSearch(diskfor500MB.searchForRecord(500,500)) + "\n\n");

				//Experiment 4
				System.out.println("Experiment 4: ");
				System.out.println("Average of average rating: " + calcAvgRatingFromSearch(diskfor500MB.searchForRecord(30000, 40000)) + "\n\n");

				//Experiment 5
				System.out.println("Experiment 5: ");
				diskfor500MB.getBPT().removeNode(1000);
				System.out.println("Number of times that a node is deleted : " + diskfor500MB.getBPT().getNumOfDeleted());
				System.out.println("Number nodes of the updated B+ tree: " + diskfor500MB.getBPT().getNumOfNodes());
				System.out.println("Height of the updated B+ tree: " + diskfor500MB.getBPT().getHeight());
				System.out.println("content of the root node:");
				System.out.println(diskfor500MB.getBPT().getContent());
				System.out.println(((BTreeNode) diskfor500MB.getBPT().getPointers()[0]).getContent());	
				option = true;

			} else{
					if (choice.equals("2")){
						System.out.println("Exiting!");
						break;
					} else {
						System.out.println("Please enter 1 or 2");
					}
				}
			
			
		}
		
	
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
				Record tempRecord = new Record(tConst, avgRating, numOfVotes);
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


	//for experiment 3
	private static double calcAvgRatingFromSearch(Record[] records){
		//Record[] records= diskfor200MB.searchForRecord(500,500);
		double totalRating = 0;
		for(Record record: records){
	 		totalRating += record.getAverageRating();
		}
		return totalRating/records.length;
	}

	

}