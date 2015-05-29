package j2s;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.User.QuestionSortOrder;
import com.googleapis.ajax.schema.WebResult;
import com.googleapis.ajax.services.GoogleSearchQueryFactory;
import com.googleapis.ajax.services.WebSearchQuery;

public class StackOverflowData {
	
	public static void main(String[] args) {
		executeGoogleSearchQuery();
	}
	
	public static ArrayList<String> executeStackOverflowQuery(ArrayList<String> keywords) {
		
		StackExchangeApiQueryFactory queryFactory = StackExchangeApiQueryFactory.newInstance();
		
		PagedList<Question> questions =  queryFactory.newAdvanceSearchApiQuery()
				.withTitle("user location")
				.withSort(QuestionSortOrder.MOST_RELEVANT)
				.withTags("ios", "swift")
				.withFilter("WITHBODY")
				.list();

//		System.out.println(questions.size());
		String answer = questions.get(0).getBody();
//		System.out.println(answer);
		
		Document doc = Jsoup.parse(answer);
		System.out.println(doc.getElementsByTag("code").get(0).text());
		
		ArrayList<String> searchResult = new ArrayList<String>();
		
		return searchResult;
	}
	
	public static void executeGoogleSearchQuery() {
		GoogleSearchQueryFactory factory = GoogleSearchQueryFactory.newInstance("applicationKey");
		WebSearchQuery query = factory.newWebSearchQuery();
//		com.googleapis.ajax.common.PagedList<WebResult> response = query.withQuery("hadoop").list();
//		System.out.println(response.getCurrentPageIndex());
//		System.out.println(response.getEstimatedResultCount());
//		System.out.println(response.getMoreResultsUrl());
//		System.out.println(response.getPages());
//		for (WebResult result : response) {
//		        System.out.println(result.getTitle());                  
//		        System.out.println(result.getContent());                        
//		        System.out.println(result.getUrl());                    
//		        System.out.println("=======================================");                  
//		}
	}
}