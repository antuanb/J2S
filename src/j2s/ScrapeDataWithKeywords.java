package j2s;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.code.stackexchange.client.StackExchangeApiClient;
import com.google.code.stackexchange.client.StackExchangeApiClientFactory;
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
	
	public static final String AUTH_KEY = "9etMjUnkWrL4v4fdd8ztkA((";

	public static ArrayList<HashMap<Answer,Question>> executeStackOverflowQuery(String keywords) {
		StackExchangeApiClientFactory clientFactory = StackExchangeApiClientFactory.newInstance(AUTH_KEY, StackExchangeSite.STACK_OVERFLOW);
		StackExchangeApiClient client = clientFactory.createStackExchangeApiClient();
		
		HashMap<Answer, Question> searchResult = new HashMap<Answer,Question>();
		ArrayList<HashMap<Answer, Question>> searchResults = new ArrayList<HashMap<Answer, Question>>();
		
		ArrayList<String> tags = new ArrayList<String>(Arrays.asList("ios", "swift"));
		
		PagedList<Question> questions = null;
		
		long qId = -1;
		if (keywords == null) {
			return searchResults;
		}
		if (Character.isDigit(keywords.charAt(0))) {
			qId = Long.parseLong(keywords);
//			System.out.println("test");
		} else {
			questions = client.searchQuestions(keywords, tags, null, QuestionSortOrder.MOST_RELEVANT, null);
//			questions = client.searchQuestions(keywords);
			System.out.println("QUESTIONS SIZE: " + questions.size());
		}
		if (qId != -1) {
			PagedList<Question> question = client.getQuestions(qId);
			PagedList<Answer> answers = client.getAnswersByQuestions(SortOrder.MOST_VOTED, "WITHBODY", qId);
			
			if (answers.size() == 0) {
				return searchResults;
			}
			searchResult.put(answers.get(0), question.get(0));
			searchResults.add(searchResult);
			return searchResults;
		}
		for (Question q : questions) {
			PagedList<Answer> answers = client.getAnswersByQuestions(SortOrder.MOST_VOTED, "WITHBODY", q.getQuestionId());
			if (answers.size() == 0) {
				continue;
			}
			searchResult = new HashMap<Answer,Question>();
			searchResult.put(answers.get(0), q);
			searchResults.add(searchResult);
		}

		return searchResults;
	}

	public static ArrayList<HashMap<String, String>> executeGoogleSearchQuery_Stack(
			String keywords) {
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory
				.newInstance("applicationKey");
		WebSearchQuery query = factory.newWebSearchQuery();
		query.withResultSetSize(ResultSetSize.LARGE);
		
		com.googleapis.ajax.common.PagedList<WebResult> response = null;
		
		if (GenerateSwiftQueryString.inputLanguage == "java") {
			response = query.withQuery(keywords + "swift website:www.stackoverflow.com").list();
		} else if (GenerateSwiftQueryString.inputLanguage == "swift") {
			response = query.withQuery(keywords + "java website:www.stackoverflow.com").list();
		}
		
		ArrayList<HashMap<String, String>> googleSearchResults = new ArrayList<HashMap<String, String>>();
		for (WebResult result : response) {
			HashMap<String, String> googleSearchResult = new HashMap<String, String>();
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