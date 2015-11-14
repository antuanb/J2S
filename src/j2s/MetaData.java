package j2s;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MetaData {
	
	private HashMap<String, Integer> frequency;
	private HashSet<String> titleTokens;
	private long numViews;
	private long isApprovedAnswer;
	private long numVotes;
	private long numDownVotes;
	private long numFav;
	private int numQueryAppear;
	private double numInQuery;
	private float normLinScore;
	private long id;
	private long questionFavoriteCount;
	private long questionScore;
	private String questionTitle;
	private long questionUpVoteCount;
	private long questionViewCount;
	private double cosValueFinal;
	private double finalRankingScore;
	private ArrayList<String> answerBody;
	private ArrayList<Float> resultVector = new ArrayList<Float>();

	//static feature weights for linear scoring
	private static final HashMap<String, Float> FEATURE_WEIGHTS;
	static {
		FEATURE_WEIGHTS = new HashMap<String, Float>();
		FEATURE_WEIGHTS.put("frequency", (float)1);
		FEATURE_WEIGHTS.put("title", (float)1);
		FEATURE_WEIGHTS.put("numViews", (float)2);
		FEATURE_WEIGHTS.put("isApprovedAnswer", (float)2);
		FEATURE_WEIGHTS.put("numVotes", (float)2);
		FEATURE_WEIGHTS.put("numDownVotes", (float)-1);
		FEATURE_WEIGHTS.put("numFav", (float)2);
		FEATURE_WEIGHTS.put("numQueryAppear", (float)2);
		FEATURE_WEIGHTS.put("numInQuery", (float)2);
		FEATURE_WEIGHTS.put("titleToken", (float)1);
		FEATURE_WEIGHTS.put("qFav", (float)1);
		FEATURE_WEIGHTS.put("qScore", (float)2);
		FEATURE_WEIGHTS.put("qUp", (float)1);
		FEATURE_WEIGHTS.put("qView", (float)2);
	}
	
	public MetaData() {
		//queries appear by default at least once
		numQueryAppear = 1;
	}
	
	public String toString() {
		return "http://stackoverflow.com/questions/" + this.getID();// + ", " + this.question.getQuestionId();
	}
	
	/*
	 * Calculates the linear score for linear combinations model
	 * 
	 * @ENTRY_POINT from outside class
	 * @return linear combination of the model score
	 */
	public float getLinearScore() {
		resultVector = new ArrayList<Float>();
		resultVector.add((float)this.getNumFav() * FEATURE_WEIGHTS.get("numFav"));
		resultVector.add((float)this.getNumDownVotes() * FEATURE_WEIGHTS.get("numDownVotes"));
		resultVector.add((float)this.getNumVotes() * FEATURE_WEIGHTS.get("numVotes"));
		resultVector.add((float)this.getNumViews() * FEATURE_WEIGHTS.get("numViews"));
		resultVector.add((float)this.getNumQueryAppear() * FEATURE_WEIGHTS.get("numQueryAppear"));
		resultVector.add((float)this.getNumInQuery() * FEATURE_WEIGHTS.get("numInQuery"));
		resultVector.add((float)this.getQuestionFavoriteCount() * FEATURE_WEIGHTS.get("qFav"));
		resultVector.add((float)this.getQuestionScore() * FEATURE_WEIGHTS.get("qScore"));
		resultVector.add((float)this.getQuestionUpVoteCount() * FEATURE_WEIGHTS.get("qUp"));
		resultVector.add((float)this.getQuestionViewCount() * FEATURE_WEIGHTS.get("qView"));
		if (this.isApprovedAnswer() == this.getID()) {
			resultVector.add((float) (1.0 * FEATURE_WEIGHTS.get("isApprovedAnswer")));
		}
		else {
			resultVector.add((float)(0.0));
		}
		
		//return linear combination score
		float totalScore = (float)0;
		for (float d : resultVector) {
			totalScore += d;		
		}
		return totalScore;
	}
	
	/*
	 * Calculates the sparse vector associated with a metadata object based
	 * on the tf-idf weights. Called by other metadata objects and itself
	 * 
	 * @ENTRY_POINT
	 * @return sparse metadata n-dimensional vector
	 */
	public float[] getMetaDataVector() {
		float[] sparseVector = new float[SearchAndRank.totalUniqueTokens.size()];
		//for all tokens if present, get weight and add, otherwise 0
		for (int i = 0; i < SearchAndRank.totalUniqueTokens.size(); i++) {
			String token = SearchAndRank.totalUniqueTokens.get(i);
			if (frequency.containsKey(token)) {
				//add extra weight to title tokens
				if (getTitleTokens().contains(token)) {
					sparseVector[i] = getTF_IDF(frequency.get(token), token, true);
				}
				else {
					sparseVector[i] = getTF_IDF(frequency.get(token), token, false);
				}
			}
			else {
				sparseVector[i] = 0;
			}
		}
		return sparseVector;
	}
	
	/*
	 * using global and local frequency across this document
	 * and all documents to calculate the tf-idf weights
	 */
	private float getTF_IDF(int freq, String token, boolean isTitle) {
		int DCountIn = getDCountIn(token);
		int newFreq = freq;
		if (isTitle) {
			newFreq++;
		}
		return (float) (newFreq * Math.log((float)SearchAndRank.DCount/DCountIn));
	}
	
	/*
	 * helper function for documents containing a term
	 */
	private int getDCountIn(String token) {
		int DCountIn = 0;
		for (int i = 0; i < SearchAndRank.DSet.size(); i++) {
			if (SearchAndRank.DSet.get(i).contains(token)) {
				DCountIn++;
			}
		}
		return DCountIn;
	}
	

	/*
	 * Get the norm of a vector, in L2 format for cosine calculation
	 * 
	 * @ENTRY_POINT
	 * @return scalar of the L2 norm
	 */
	public double getMetaDataVectorNorm(float[] vector) {
		double total = 0.0;
		for (int i = 0; i < vector.length; i++) {
			total += (vector[i] * vector[i]);
		}
		return Math.sqrt(total);
	}
	
	/*
	 * Gets the final cosine value between this document and a given other document
	 * 
	 * @ENTRY_POINT
	 * @return cosine between two vectors
	 */
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
	
	public ArrayList<String> getAnswerBody() {
		return answerBody;
	}

	public void setAnswerBody(ArrayList<String> answerBody) {
		this.answerBody = answerBody;
	}

	public double getFinalRankingScore() {
		return finalRankingScore;
	}

	public void setFinalRankingScore(double finalRankingScore) {
		this.finalRankingScore = finalRankingScore;
	}

	public ArrayList<Float> getResultVector() {
		return resultVector;
	}

	public void setResultVector(ArrayList<Float> resultVector) {
		this.resultVector = resultVector;
	}

	public double getCosValueFinal() {
		return cosValueFinal;
	}

	public void setCosValueFinal(double cosValueFinal) {
		this.cosValueFinal = cosValueFinal;
	}
	
	public long getQuestionFavoriteCount() {
		return questionFavoriteCount;
	}

	public void setQuestionFavoriteCount(long questionFavoriteCount) {
		this.questionFavoriteCount = questionFavoriteCount;
	}

	public long getQuestionScore() {
		return questionScore;
	}

	public void setQuestionScore(long questionScore) {
		this.questionScore = questionScore;
	}

	public String getQuestionTitle() {
		return questionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	public long getQuestionUpVoteCount() {
		return questionUpVoteCount;
	}

	public void setQuestionUpVoteCount(long questionUpVoteCount) {
		this.questionUpVoteCount = questionUpVoteCount;
	}

	public long getQuestionViewCount() {
		return questionViewCount;
	}

	public void setQuestionViewCount(long questionViewCount) {
		this.questionViewCount = questionViewCount;
	}

	public void setNumQueryAppear(int numQueryAppear) {
		this.numQueryAppear = numQueryAppear;
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

	public long isApprovedAnswer() {
		return isApprovedAnswer;
	}

	public void setApprovedAnswer(long isApprovedAnswer) {
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
