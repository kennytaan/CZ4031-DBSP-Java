package MemoryPool;
//import java.util.Arrays;


//record consists of 3 variables: tConst, avgRating, numOfVotes
public class Record{

	private String tConst;
	private float averageRating;
	private int numVotes;
	public Object getTConst;

	public Record(String tConst, float averageRating, int numVotes ){
		this.tConst = tConst;
		this.averageRating = averageRating;
		this.numVotes = numVotes;
	}

	public String getTConst(){
		return tConst;
	}

	public float getAverageRating(){
		return averageRating;
	}

	public int getNumVotes(){
		return numVotes;
	}

	//returning the entire record as a string
	public String getRecordString(){
		return "Record{ tConst = " + tConst +
				", averageRating = " + averageRating + 
				", numVotes = " + numVotes + " }";
	}

}