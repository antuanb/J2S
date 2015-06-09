package j2s;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MetaData {
	
	private HashMap<String, Integer> frequency;
	private HashSet<String> titleTokens;
	private long numViews;
	private boolean isApprovedAnswer;
	private long numVotes;
	private long numDownVotes;
	private long numFav;
	private int numQueryAppear;
	private double numInQuery;
	private float normLinScore;
	private long id;
	
	public static ArrayList<Float> resultVector = new ArrayList<Float>();

	private static final HashMap<String, Float> FEATURE_WEIGHTS;
	
	//TODO: ADD ACTUALLY CORRECT WEIGHT VALUES
	static {
		FEATURE_WEIGHTS = new HashMap<String, Float>();
		FEATURE_WEIGHTS.put("frequency", (float)1);
		FEATURE_WEIGHTS.put("title", (float)1);
		FEATURE_WEIGHTS.put("numViews", (float)1);
		FEATURE_WEIGHTS.put("isApprovedAnswer", (float)1);
		FEATURE_WEIGHTS.put("numVotes", (float)1);
		FEATURE_WEIGHTS.put("numDownVotes", (float)1);
		FEATURE_WEIGHTS.put("numFav", (float)1);
		FEATURE_WEIGHTS.put("numQueryAppear", (float)1);
		FEATURE_WEIGHTS.put("numInQuery", (float)1);
		FEATURE_WEIGHTS.put("titleToken", (float)1);
	}
	
	public MetaData() {
		//whatever fields end up being in constructor
		numQueryAppear = 1;
	}
	
	//called after everything is done
	public float getLinearScore() {
		resultVector = new ArrayList<Float>();
		resultVector.add((float)this.getNumFav() * FEATURE_WEIGHTS.get("numFav"));
		resultVector.add((float)this.getNumDownVotes() * FEATURE_WEIGHTS.get("numDownVotes"));
		resultVector.add((float)this.getNumVotes() * FEATURE_WEIGHTS.get("numVotes"));
		resultVector.add((float)this.getNumViews() * FEATURE_WEIGHTS.get("numViews"));
		resultVector.add((float)this.getNumQueryAppear() * FEATURE_WEIGHTS.get("numQueryAppear"));
		resultVector.add((float)this.getNumInQuery() * FEATURE_WEIGHTS.get("numInQuery"));
		
		//if can get this from answer or another way add into linear model
		/*
		if (this.isApprovedAnswer()) {
			resultVector.add(1.0 * FEATURE_WEIGHTS.get("isApprovedAnswer"));
		}
		else {
			resultVector.add(0.0 * FEATURE_WEIGHTS.get("isApprovedAnswer"));
		}
		*/
		
		//leaving as arraylist in case need values passed before summation
		float totalScore = (float)0;
		for (double d : resultVector) {
			totalScore += d;		
		}
		return totalScore;
	}
	
	//called after everything is done
	public float[] getMetaDataVector() {
		
		//TODO: need to implement tf-idf weighting for these weights
		float[] sparseVector = new float[SearchAndRank.totalUniqueTokens.size()];
		for (int i = 0; i < SearchAndRank.totalUniqueTokens.size(); i++) {
			String token = SearchAndRank.totalUniqueTokens.get(i);
			if (frequency.containsKey(token)) {
				/*if (getTitleTokens().contains(token)) {
					sparseVector[i] = (float)FEATURE_WEIGHTS.get("titleToken") * frequency.get(i);
				}
				else {
					sparseVector[i] = (float)frequency.get(i);
				}*/
				sparseVector[i] = getTF_IDF(frequency.get(token), token);
			}
			else {
				sparseVector[i] = 0;
			}
		}
		return sparseVector;
	}
	
	public static float getTF_IDF(int freq, String token) {
		int DCountIn = getDCountIn(token);
		return (float) (freq * Math.log(SearchAndRank.DCount/DCountIn));
	}
	
	public static int getDCountIn(String token) {
		int DCountIn = 0;
		for (int i = 0; i < SearchAndRank.DSet.size(); i++) {
			if (SearchAndRank.DSet.get(i).contains(token)) {
				DCountIn++;
			}
		}
		return DCountIn;
	}
	
	//called after everything is done
	public double getMetaDataVectorNorm(float[] vector) {
		double total = 0.0;
		for (int i = 0; i < vector.length; i++) {
			total += (vector[i] * vector[i]);
		}
		return Math.sqrt(total);
	}
	
	//final result for comparison that is called outside of this class in searchandrank
	public double getCosValue(MetaData other) {
		double total = 0.0;
		float[] self = this.getMetaDataVector();
		float[] out = other.getMetaDataVector();
		for (int i = 0; i < SearchAndRank.totalUniqueTokens.size(); i++) {
			total += (self[i] * out[i]);
		}
		
		total = total / (this.getMetaDataVectorNorm(self) * other.getMetaDataVectorNorm(out));
		
		return total;
	}
	
	public String printFields() {
		return resultVector.toString();		
	}
	
	public void setID(long id) {
		this.id = id;
	}
	
	public long getID() {
		return id;
	}
	
	public void setNormLinScore(float score) {
		this.normLinScore = score;
	}
	
	public float getNormLinScore() {
		return normLinScore;
	}
	
	public HashSet<String> getTitleTokens() {
		return titleTokens;
	}

	public void setTitleTokens(HashSet<String> titleTokens) {
		this.titleTokens = titleTokens;
	}

	public int getNumQueryAppear() {
		return numQueryAppear;
	}

	public void setNumQueryAppear() {
		this.numQueryAppear = this.numQueryAppear + 1;
	}

	public double getNumInQuery() {
		return numInQuery;
	}

	public void setNumInQuery(double numInQuery) {
		this.numInQuery = numInQuery;
	}
	
	public HashMap<String, Integer> getFrequency() {
		return frequency;
	}

	public void setFrequency(HashMap<String, Integer> frequency) {
		this.frequency = frequency;
	}

	public long getNumViews() {
		return numViews;
	}

	public void setNumViews(long numViews) {
		this.numViews = numViews;
	}

	public boolean isApprovedAnswer() {
		return isApprovedAnswer;
	}

	public void setApprovedAnswer(boolean isApprovedAnswer) {
		this.isApprovedAnswer = isApprovedAnswer;
	}

	public long getNumVotes() {
		return numVotes;
	}

	public void setNumVotes(long numVotes) {
		this.numVotes = numVotes;
	}

	public long getNumDownVotes() {
		return numDownVotes;
	}

	public void setNumDownVotes(long numDownVotes) {
		this.numDownVotes = numDownVotes;
	}

	public long getNumFav() {
		return numFav;
	}

	public void setNumFav(long numFav) {
		this.numFav = numFav;
	}
	
}
