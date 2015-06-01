package j2s;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAndRank {
	
	private static ArrayList<String> queryResultStackOverflow;
	private static HashMap<String, String> queryResultGoogleStack;
	private static ArrayList<HashMap<String, String>> queryResultGoogleAll;
	private static ArrayList<String> fullKeywordSet = new ArrayList<String>();
	private static ArrayList<String> tempKeyword = new ArrayList<String>();
	
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
			queryResultStackOverflow = ScrapeDataWithKeywords.executeStackOverflowQuery(keyword);
			queryResultGoogleStack = ScrapeDataWithKeywords.executeGoogleSearchQuery_Stack(keyword);
			queryResultGoogleAll = ScrapeDataWithKeywords.executeGoogleSearchQuery_All(keyword);
		}
		
	}
	
	private void rank() {
		
	}
	
	//testing purposes
	public static void main(String args[]) {
		ArrayList<String> test = new ArrayList<String>();
		test.add("location");
		test.add("user");
		test.add("get");
		initKeywordSet(test);
		System.out.println(fullKeywordSet.toString());
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
}
