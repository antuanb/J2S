package j2s;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Element;
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

	// TODO: need to decide on searches per keyword or all together
	// need to figure out how to standardize returns from searches
	// if doing non stackoverflow (like blogs or other info) how
	// to strip away code and also how do we know importance?
	public static ArrayList<String> executeStackOverflowQuery(String keywords) {

		StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory
				.newInstance();

		PagedList<Question> questions = queryFactory.newAdvanceSearchApiQuery()
				//.withUrl(url)
				.withTitle(keywords).withSort(QuestionSortOrder.MOST_RELEVANT)
				.withTags("ios", "swift").withFilter("WITHBODY").list();

		// System.out.println(questions.size());
		String answer = questions.get(0).getBody();
		// System.out.println(answer);

		ArrayList<String> searchResult = new ArrayList<String>();

		return searchResult;
		
	}

	public static HashMap<String, String> executeGoogleSearchQuery_Stack(
			String keywords) {
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory
				.newInstance("applicationKey");
		WebSearchQuery query = factory.newWebSearchQuery();
		query.withResultSetSize(ResultSetSize.LARGE);
		com.googleapis.ajax.common.PagedList<WebResult> response = query
				.withQuery(keywords + "swift website:www.stackoverflow.com")
				.list();

		HashMap<String, String> googleSearchResults = new HashMap<String, String>();
		for (WebResult result : response) {
			googleSearchResults.put("title", result.getTitle());
			googleSearchResults.put("content", result.getContent());
			googleSearchResults.put("url", result.getUrl());
		}
		return googleSearchResults;
	}

	public static ArrayList<HashMap<String, String>> executeGoogleSearchQuery_All(
			String keywords) {
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory
				.newInstance("applicationKey");
		WebSearchQuery query = factory.newWebSearchQuery();
		query.withResultSetSize(ResultSetSize.LARGE);
		com.googleapis.ajax.common.PagedList<WebResult> response = query
				.withQuery("location swift").list();
		ArrayList<HashMap<String, String>> googleSearchResults = new ArrayList<HashMap<String, String>>();
		for (WebResult result : response) {
			String websiteURL = result.getUrl();
			if (!websiteURL.contains("stackoverflow.com")) {
				HashMap<String, String> googleSearchResult = new HashMap<String, String>();
				googleSearchResult.put("title", result.getTitle());
				googleSearchResult.put("content", result.getContent());
				googleSearchResult.put("url", websiteURL);
				googleSearchResults.add(googleSearchResult);
			}
		}
		return googleSearchResults;
	}

	private static ArrayList<String> parseHTMLForCode(String input) {
		Document doc = null;
		if (input.startsWith("http")) {
			try {
				doc = Jsoup.parse(new URL(input), 0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			doc = Jsoup.parse(input);
		}
		ArrayList<String> results = new ArrayList<String>();
		for (Element result : doc.getAllElements()) {
			if (result.tagName().equals("code")) {
				results.add(result.text());
			}
			for (String className : result.classNames()) {
				if (className.contains("code")) {
					results.add(result.text());
				}
			}
		}

		return results;
	}

	public static void main(String args[]) {
		String url = executeGoogleSearchQuery_All("").get(
				2).get("url");

		for (String code : parseHTMLForCode(url)) {
			System.out.println(code);
		}
	}

}