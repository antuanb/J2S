package j2s;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.User.QuestionSortOrder;
import com.googleapis.ajax.schema.WebResult;
import com.googleapis.ajax.services.GoogleSearchQueryFactory;
import com.googleapis.ajax.services.WebSearchQuery;
import com.googleapis.ajax.services.enumeration.ResultSetSize;

public class ScrapeDataWithKeywords {
	
	public static void main(String args[]) {
		executeStackOverflowQuery(new ArrayList<String>());
	}

	//TODO: need to decide on searches per keyword or all together
	//need to figure out how to standardize returns from searches
	//if doing non stackoverflow (like blogs or other info) how 
	//to strip away code and also how do we know importance?
	public static ArrayList<String> executeStackOverflowQuery(
			ArrayList<String> keywords) {

		StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory
				.newInstance();

		PagedList<Question> questions = queryFactory.newAdvanceSearchApiQuery()
				.withTitle("user location")
				.withSort(QuestionSortOrder.MOST_RELEVANT)
				.withTags("ios", "swift").withFilter("WITHBODY").list();

		// System.out.println(questions.size());
		String answer = questions.get(0).getBody();
		// System.out.println(answer);

		Document doc = Jsoup.parse(answer);
		System.out.println(doc.getElementsByTag("code").get(0).text());

		ArrayList<String> searchResult = new ArrayList<String>();

		return searchResult;
	}

	public static HashMap<String, String> executeGoogleSearchQuery_Stack(ArrayList<String> keywords) {
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory
				.newInstance("applicationKey");
		WebSearchQuery query = factory.newWebSearchQuery();
		query.withResultSetSize(ResultSetSize.LARGE);
		com.googleapis.ajax.common.PagedList<WebResult> response = query
				.withQuery("location swift website:www.stackoverflow.com")
				.list();
		// System.out.println(response.getCurrentPageIndex());
		// System.out.println(response.getEstimatedResultCount());
		// System.out.println(response.getMoreResultsUrl());
		// System.out.println(response.getPages());
		HashMap<String, String> googleSearchResults = new HashMap<String, String>();
		for (WebResult result : response) {
			String[] tokens = result.getUrl().split(
					("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)"));
			if (tokens[0].equals("http://stackoverflow.com/questions/")) {
				googleSearchResults.put("title", result.getTitle());
				googleSearchResults.put("content", result.getContent());
				googleSearchResults.put("url", result.getUrl());
				googleSearchResults.put("id", tokens[1]);
			}
		}
		return googleSearchResults;
	}

	public static HashMap<String, String> executeGoogleSearchQuery_All(ArrayList<String> keywords) {
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory
				.newInstance("applicationKey");
		WebSearchQuery query = factory.newWebSearchQuery();
		query.withResultSetSize(ResultSetSize.LARGE);
		com.googleapis.ajax.common.PagedList<WebResult> response = query
				.withQuery("location swift").list();
		HashMap<String, String> googleSearchResults = new HashMap<String, String>();
		for (WebResult result : response) {
			googleSearchResults.put("title", result.getTitle());
			googleSearchResults.put("content", result.getContent());
			googleSearchResults.put("url", result.getUrl());
		}
		return googleSearchResults;
	}
	
	private String keywordCombiner(ArrayList<String> keywords) {
		String output = "";
		for (String s : keywords) {
			output+=s;
			output+=" ";
		}
		return output;
	}
}