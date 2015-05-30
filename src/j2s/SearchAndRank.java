package j2s;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAndRank {
	
	private static ArrayList<String> queryResultStackOverflow;
	private static HashMap<String, String> queryResultGoogleStack;
	private static HashMap<String, String> queryResultGoogleAll;
	
	public SearchAndRank(ArrayList<String> searchKeywords) {
		search(searchKeywords);
		rank();
	}
	
	private void search(ArrayList<String> keywords) {
		queryResultStackOverflow = ScrapeDataWithKeywords.executeStackOverflowQuery(keywords);
		queryResultGoogleStack = ScrapeDataWithKeywords.executeGoogleSearchQuery_Stack(keywords);
		queryResultGoogleAll = ScrapeDataWithKeywords.executeGoogleSearchQuery_All(keywords);
	}
	
	private void rank() {
		
	}
}
