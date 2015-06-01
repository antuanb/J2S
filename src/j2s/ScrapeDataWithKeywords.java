package j2s;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.code.stackexchange.client.StackExchangeApiClient;
import com.google.code.stackexchange.client.StackExchangeApiClientFactory;
import com.google.code.stackexchange.client.query.AnswerApiQuery;
import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Answer;
import com.google.code.stackexchange.schema.Answer.SortOrder;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.StackExchangeSite;
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
	// Can use questions vote count, view count, etc as features
	public static ArrayList<String> executeStackOverflowQuery(String keywords) {
		StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory.newInstance();
		StackExchangeApiClientFactory clientFactory = StackExchangeApiClientFactory.newInstance(null, StackExchangeSite.STACK_OVERFLOW);
		StackExchangeApiClient client = clientFactory.createStackExchangeApiClient();
		
		ArrayList<String> tags = new ArrayList<String>(Arrays.asList("ios", "swift"));
		
		PagedList<Question> questions = null;
		
		if (keywords.startsWith("http")) {
			questions = queryFactory.newAdvanceSearchApiQuery().withURL(keywords).list();
		} else {
			questions = client.searchQuestions(keywords, tags, null, QuestionSortOrder.MOST_RELEVANT, null);
		}
		
		Question question = questions.get(0);
		
		PagedList<Answer> answers = client.getAnswersByQuestions(SortOrder.MOST_VOTED, "WITHBODY", question.getQuestionId());

		String answer = answers.get(0).getBody();
		ArrayList<String> searchResult = new ArrayList<String>();
		searchResult.add(answer);
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
		System.out.println(parseHTMLForCode(executeStackOverflowQuery("user location").get(0)).get(0));
		
		//executeStackOverflowQuery("location");
		//String url = executeStackOverflowQuery("").get(
		//		2).get("url");
		/*
		for (String code : parseHTMLForCode(url)) {
			System.out.println(code);
		}*/
	}

}