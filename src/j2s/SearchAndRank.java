package j2s;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.code.stackexchange.schema.Answer;

public class SearchAndRank {
	
	private static ArrayList<Answer> queryResultStackOverflow;
	private static ArrayList<HashMap<String, String>> queryResultGoogleStack;
	private static ArrayList<HashMap<String, String>> queryResultGoogleAll;
	private static ArrayList<String> fullKeywordSet = new ArrayList<String>();
	private static ArrayList<String> tempKeyword = new ArrayList<String>();
	private static ArrayList<AnswerWrapper> finalStackOverflowResultsList = new ArrayList<AnswerWrapper>();
	
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
