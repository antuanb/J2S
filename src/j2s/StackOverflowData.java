package j2s;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.code.stackexchange.client.query.StackExchangeApiQueryFactory;
import com.google.code.stackexchange.common.PagedList;
import com.google.code.stackexchange.schema.Question;
import com.google.code.stackexchange.schema.User.QuestionSortOrder;

public class StackOverflowData {
	
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
}