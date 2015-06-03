package j2s;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public static ArrayList<Answer> executeStackOverflowQuery(String keywords) {
		//StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory.newInstance();
		StackExchangeApiClientFactory clientFactory = StackExchangeApiClientFactory.newInstance(null, StackExchangeSite.STACK_OVERFLOW);
		StackExchangeApiClient client = clientFactory.createStackExchangeApiClient();
		ArrayList<Answer> searchResult = new ArrayList<Answer>();
		ArrayList<String> tags = new ArrayList<String>(Arrays.asList("ios", "swift"));
		
		PagedList<Question> questions = null;
		
		long qId = -1;
		if (Character.isDigit(keywords.charAt(0))) {
			//Question q = queryFactory.newAdvanceSearchApiQuery().withURL(keywords).withTags(tags).singleResult();
			qId = Long.parseLong(keywords);
			System.out.println("test");
		} else {
			questions = client.searchQuestions(keywords, tags, null, QuestionSortOrder.MOST_RELEVANT, null);
		}
		if (qId != -1) {
			PagedList<Answer> answers = client.getAnswersByQuestions(SortOrder.MOST_VOTED, "WITHBODY", qId);
			if (answers.size() == 0) {
				return searchResult;
			}
			searchResult.add(answers.get(0));
			System.out.println(answers.get(0).getBody());
			return searchResult;
		}
		for (Question q : questions) {
			PagedList<Answer> answers = client.getAnswersByQuestions(SortOrder.MOST_VOTED, "WITHBODY", q.getQuestionId());
			if (answers.size() == 0) {
				continue;
			}
			searchResult.add(answers.get(0));
		}

		return searchResult;
	}

	public static ArrayList<HashMap<String, String>> executeGoogleSearchQuery_Stack(
			String keywords) {
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory
				.newInstance("applicationKey");
		WebSearchQuery query = factory.newWebSearchQuery();
		query.withResultSetSize(ResultSetSize.LARGE);
		com.googleapis.ajax.common.PagedList<WebResult> response = query
				.withQuery(keywords + "swift website:www.stackoverflow.com")
				.list();
		ArrayList<HashMap<String, String>> googleSearchResults = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> googleSearchResult = new HashMap<String, String>();
		for (WebResult result : response) {
			if (result.getUrl().startsWith("http://stackoverflow.com/questions/")) {
				Pattern p = Pattern.compile("^[^\\d]*(\\d+)");
				Matcher m = p.matcher(result.getUrl());
				if (m.find()) {
					googleSearchResult.put("id", m.group(1));
				}
			}
			googleSearchResult.put("title", result.getTitle());
			googleSearchResult.put("content", result.getContent());
			googleSearchResult.put("url", result.getUrl());
			googleSearchResults.add(googleSearchResult);
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

	public static ArrayList<String> parseHTMLForCode(String input) {
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
		//System.out.println(executeStackOverflowQuery("26109669"));
		executeStackOverflowQuery("user location");

		//executeStackOverflowQuery("location");
		//String url = executeStackOverflowQuery("").get(
		//		2).get("url");
		/*
		for (String code : parseHTMLForCode(url)) {
			System.out.println(code);
		}*/
	}

}