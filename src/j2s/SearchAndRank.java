package j2s;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.google.code.stackexchange.schema.Answer;

public class SearchAndRank {
	
	private static ArrayList<Answer> queryResultStackOverflow;
	private static ArrayList<HashMap<String, String>> queryResultGoogleStack;
	private static ArrayList<HashMap<String, String>> queryResultGoogleAll;
	private static ArrayList<String> fullKeywordSet = new ArrayList<String>();
	private static ArrayList<String> tempKeyword = new ArrayList<String>();
	private static ArrayList<AnswerWrapper> finalStackOverflowResultsList = new ArrayList<AnswerWrapper>();
	
	public static ArrayList<String> totalUniqueTokens = new ArrayList<String>();
	
	public SearchAndRank(ArrayList<String> searchKeywords) {
		initKeywordSet(searchKeywords);
		search();
		rank();
	}
	
	private static void initKeywordSet(ArrayList<String> searchKeywords) {
		for (int i = 0; i < searchKeywords.size(); i++) {
			fullKeywordSet.add(searchKeywords.get(i));
		}
		permutation(searchKeywords);
		for (int i = 0; i < tempKeyword.size(); i++) {
			fullKeywordSet.add(tempKeyword.get(i).trim());
		}
		if (searchKeywords.size() > 1) {
			fullKeywordSet.add(searchKeywords.get(0) + " " + searchKeywords.get(1));
			fullKeywordSet.add(searchKeywords.get(1) + " " + searchKeywords.get(0));
		}
	}
	
	private void search() {
		for (String keyword : fullKeywordSet) {
			//store these results in something (meta data structure) and then pass all info to ranking
			queryResultStackOverflow = ScrapeDataWithKeywords.executeStackOverflowQuery(keyword);
			for (int i = 0; i < queryResultStackOverflow.size(); i++) {
				AnswerWrapper aw = new AnswerWrapper(queryResultStackOverflow.get(i), 1.0 - (double)(i+1)/queryResultStackOverflow.size());
				finalStackOverflowResultsList.add(aw);
			}
			
			queryResultGoogleStack = ScrapeDataWithKeywords.executeGoogleSearchQuery_Stack(keyword);
			Answer tempSingleQueryResultGoogleStack;
			for (int i = 0; i < queryResultGoogleStack.size(); i++) {
				tempSingleQueryResultGoogleStack = ScrapeDataWithKeywords.executeStackOverflowQuery(queryResultGoogleStack.get(i).get("id")).get(0);
				AnswerWrapper aw = new AnswerWrapper(tempSingleQueryResultGoogleStack, 1.0 - (double)(i+1)/queryResultGoogleStack.size());
				finalStackOverflowResultsList.add(aw);
			}
			
			//make global static list of whatever item we use to store these non stackoverflow posts
			//store not just the code but any feature information we can infer here
			queryResultGoogleAll = ScrapeDataWithKeywords.executeGoogleSearchQuery_All(keyword);
			for (int i = 0; i < queryResultGoogleAll.size(); i++) {
				//do something with each all query result
			}
		}
		
	}
	
	private void rank() {
		//use list of answer wrappers and whatever formation is setup for the non stackoverflow
		//along with the ranks given by order to formulate the n-dimensional sphere
		//then need to do all pairwise distances between query and answers
		//report closest 3 to user
		HashMap<Long, MetaData> metaDataList = new HashMap<Long, MetaData>();
		HashSet<Long> uniques = new HashSet<Long>();
		
		for (AnswerWrapper aw : finalStackOverflowResultsList) {
			if (!uniques.contains(aw.getAnswer().getAnswerId())) {
				uniques.add(aw.getAnswer().getAnswerId());
				MetaData md = new MetaData();
				md.setNumDownVotes(aw.getAnswer().getDownVoteCount());
				md.setNumFav(aw.getAnswer().getFavoriteCount());
				md.setNumInQuery(aw.getRank());
				md.setNumViews(aw.getAnswer().getViewCount());
				md.setNumVotes(aw.getAnswer().getUpVoteCount());
				
				HashSet<String> titleTokens = createTitleTokens(aw.getAnswer().getTitle());
				md.setTitleTokens(titleTokens);
				
				HashMap<String, Integer> frequency = createTokenFrequency(aw.getAnswer());
				md.setFrequency(frequency);
				
				metaDataList.put(aw.getAnswer().getAnswerId(), md);
			}
			else {
				//not sure if this is how java works?
				//might not actually be setting this so need to test
				metaDataList.get(aw.getAnswer().getAnswerId()).setNumQueryAppear();
			}
		}
		
		//need to do same stuff for the non stack overflow results once we know how those are stored
		//that is done here and make new metadata objects as well
		
		//final compare
		//need to get cos score for each to query vector only (need to isolate query metadata object from rest with special answerid)
	    ArrayList<MetaData> finalList = new ArrayList<MetaData>();
		Iterator it = metaDataList.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        finalList.add((MetaData)pair.getValue());
	        it.remove(); 
	    }
	    Collections.sort(finalList, new MetaDataComparator());
	    Collections.reverse(finalList);
	    
	    //last thing here is get the cos value as said in above comment
	    //then combine (at 50/50 weight for now) this finallist with sorted
	    //list of cos values and return top 3 results
	}
	
	private HashMap<String, Integer> createTokenFrequency(Answer a) {
		HashMap<String, Integer> frequency = new HashMap<String, Integer>();
		
		//TODO: Sanchit
		//a.getBody() and a.getTitle()
		//add parsing of the answer body...needs to tokenize code AND non-code with counts
		//simply maps unique token to the amount of times its seen
		
		//Also need to take total frequency and if token not already contained add it, otherwise update count
		//totalUniqueTokens -updates here
		
		return frequency;
	}
	
	private HashSet<String> createTitleTokens(String title) {
		HashSet<String> titleTokens = new HashSet<String>();
		String[] tokens = title.split(" ");
		for (String s : tokens) {
			s = s.toLowerCase().trim();
			if (s.endsWith("?") || s.endsWith("!")) {
				s = s.substring(0, s.length()-1);
			}
			titleTokens.add(s);
		}
		return titleTokens;
	}
	
	public static void permutation(ArrayList<String> str) { 
	    permutation("", str); 
	}

	private static void permutation(String prefix, ArrayList<String> str) {
	    int n = str.size();
	    if (n == 0) {
	    	tempKeyword.add(prefix);
	    }
	    else {
	        for (int i = 0; i < n; i++) {
	        	ArrayList<String> temp = new ArrayList<String>();
	        	for (int j = 0; j < i; j++) {
	        		temp.add(str.get(j));
	        	}
	        	for (int j = i+1; j < n; j++) {
	        		temp.add(str.get(j));
	        	}	        	
	            permutation(prefix + str.get(i) + " ", temp);
	        }
	    }
	}
	
	private class AnswerWrapper {
		Answer answer;
		double rank;
		public AnswerWrapper(Answer answer, double rank) {
			this.answer = answer;
			this.rank = rank;
		}
		
		public Answer getAnswer() {
			return answer;
		}
		
		public double getRank() {
			return rank;
		}
	}
}
