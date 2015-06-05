package j2s;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.google.code.stackexchange.client.StackExchangeApiClient;
import com.google.code.stackexchange.client.StackExchangeApiClientFactory;
import com.google.code.stackexchange.schema.Answer;
import com.google.code.stackexchange.schema.StackExchangeSite;

public class SearchAndRank {

	private static ArrayList<Answer> queryResultStackOverflow;
	private static ArrayList<HashMap<String, String>> queryResultGoogleStack;
	private static ArrayList<HashMap<String, String>> queryResultGoogleAll;
	private static ArrayList<String> fullKeywordSet = new ArrayList<String>();
	private static ArrayList<String> tempKeyword = new ArrayList<String>();
	private static ArrayList<AnswerWrapper> finalStackOverflowResultsList = new ArrayList<AnswerWrapper>();

	public static ArrayList<String> totalUniqueTokens = new ArrayList<String>();
	private static HashSet<String> filterKeys;

	static {
		filterKeys = new HashSet<String>();
		filterKeys.add(" ");
		filterKeys.add("public");
		filterKeys.add("string");
		filterKeys.add("\n");
		filterKeys.add(".");
		filterKeys.add(",");
		filterKeys.add(":");
		filterKeys.add(";");
		filterKeys.add("");
		filterKeys.add("{");
		filterKeys.add("}");
		filterKeys.add("[");
		filterKeys.add("]");
		filterKeys.add("?");
		filterKeys.add("-");
		filterKeys.add(">");
		filterKeys.add("<");
		filterKeys.add("=");
		filterKeys.add("!");
		filterKeys.add("\\");
		filterKeys.add("\"");
		filterKeys.add("%");
	}

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
			// store these results in something (meta data structure) and then
			// pass all info to ranking
			queryResultStackOverflow = ScrapeDataWithKeywords.executeStackOverflowQuery(keyword);
			for (int i = 0; i < queryResultStackOverflow.size(); i++) {
				AnswerWrapper aw = new AnswerWrapper(queryResultStackOverflow.get(i), 1.0 - (double) (i + 1) / queryResultStackOverflow.size());
				finalStackOverflowResultsList.add(aw);
			}

			queryResultGoogleStack = ScrapeDataWithKeywords.executeGoogleSearchQuery_Stack(keyword);
			Answer tempSingleQueryResultGoogleStack;
			for (int i = 0; i < queryResultGoogleStack.size(); i++) {
				tempSingleQueryResultGoogleStack = ScrapeDataWithKeywords.executeStackOverflowQuery(queryResultGoogleStack.get(i).get("id")).get(0);
				AnswerWrapper aw = new AnswerWrapper(tempSingleQueryResultGoogleStack, 1.0 - (double) (i + 1) / queryResultGoogleStack.size());
				finalStackOverflowResultsList.add(aw);
			}

			// make global static list of whatever item we use to store these
			// non stackoverflow posts
			// store not just the code but any feature information we can infer
			// here
			queryResultGoogleAll = ScrapeDataWithKeywords.executeGoogleSearchQuery_All(keyword);
			for (int i = 0; i < queryResultGoogleAll.size(); i++) {
				// do something with each all query result
			}
		}

	}

	private void rank() {
		// use list of answer wrappers and whatever formation is setup for the
		// non stackoverflow
		// along with the ranks given by order to formulate the n-dimensional
		// sphere
		// then need to do all pairwise distances between query and answers
		// report closest 3 to user
		HashMap<Long, MetaData> metaDataList = new HashMap<Long, MetaData>();
		HashSet<Long> uniques = new HashSet<Long>();

		for (AnswerWrapper aw : finalStackOverflowResultsList) {
			if (!uniques.contains(aw.getAnswer().getAnswerId())) {
				uniques.add(aw.getAnswer().getAnswerId());
				MetaData md = new MetaData();
				md.setNumDownVotes(aw.getAnswer().getDownVoteCount());
				md.setNumFav(aw.getAnswer().getFavoriteCount());
				md.setNumInQuery(aw.getRank());
				md.setNumViews(aw.getAnswer().getViewCount());
				md.setNumVotes(aw.getAnswer().getUpVoteCount());

				HashSet<String> titleTokens = createTitleTokens(aw.getAnswer().getTitle());
				md.setTitleTokens(titleTokens);

				HashMap<String, Integer> frequency = createTokenFrequency(aw.getAnswer());
				md.setFrequency(frequency);

				metaDataList.put(aw.getAnswer().getAnswerId(), md);
			} else {
				// not sure if this is how java works?
				// might not actually be setting this so need to test
				metaDataList.get(aw.getAnswer().getAnswerId()).setNumQueryAppear();
			}
		}

		// need to do same stuff for the non stack overflow results once we know
		// how those are stored
		// that is done here and make new metadata objects as well

		// final compare
		// need to get cos score for each to query vector only (need to isolate
		// query metadata object from rest with special answerid)
		ArrayList<MetaData> finalList = new ArrayList<MetaData>();
		Iterator it = metaDataList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			finalList.add((MetaData) pair.getValue());
			it.remove();
		}
		Collections.sort(finalList, new MetaDataComparator());
		Collections.reverse(finalList);

		// last thing here is get the cos value as said in above comment
		// then combine (at 50/50 weight for now) this finallist with sorted
		// list of cos values and return top 3 results
	}

	private static HashMap<String, Integer> createTokenFrequency(Answer a) {

		HashMap<String, Integer> frequency = new HashMap<String, Integer>();

		String answerBody = a.getBody();
		String questionTitle = getQuestionTitle(a.getQuestionId());
		String input = questionTitle + " " + answerBody;

		input = Jsoup.parse(input).text(); // Remove HTML tags

		// Remove Comments
		input = input.replaceAll("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "$1 ");

		List<String> tokens = process(input); // Custom preprocessor by IBM

		ArrayList<String> tempTokens = new ArrayList<String>(tokens);

		ArrayList<String> finalTokens = new ArrayList<String>();

		for (String token : tempTokens) {
			// camelCase, Class/method names etc
			String[] codeTokens = token.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|([0-9]+)|=|(\\()|(\\))|(\\.)|(\\_)|(\\n)|(\\,)|(\\@)");
			finalTokens.addAll(Arrays.asList(codeTokens));
		}

		filterTokens(finalTokens); // remove punctuation, symbols etc

		for (String t : finalTokens) {
			t = t.toLowerCase();
			t = applyStemming(t);
			if (frequency.containsKey(t)) {
				frequency.put(t, frequency.get(t) + 1);
			} else {
				frequency.put(t, 1);
			}
		}

		ArrayList<String> uniqueLocalFrequencyList = GenerateSwiftQueryString.sortByValue(frequency);
		removeCorpusDuplicates(uniqueLocalFrequencyList);
		totalUniqueTokens.addAll(uniqueLocalFrequencyList);

		return frequency;
	}

	private static void removeCorpusDuplicates(ArrayList<String> uniqueLocalFrequencyList) {
		ArrayList<String> tempList = new ArrayList<String>(uniqueLocalFrequencyList);
		for (String token : tempList) {
			if (totalUniqueTokens.contains(token)) {
				uniqueLocalFrequencyList.remove(token);
			}
		}
	}

	public static String applyStemming(String t) {
		String token = t;
		if (t.endsWith("ing")) {
			token = t.substring(0, t.length() - 3);
		}
		return token;
	}

	private static void filterTokens(ArrayList<String> tokens) {
		for (String key : filterKeys) {
			if (tokens.contains(key)) {
				while (tokens.remove(key)) {

				}
			}
		}
	}

	public static List<String> process(String input) {
		List<String> tokens = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

		char[] arr = input.toCharArray();
		for (int i = 0; i < arr.length; i++) {

			char prior = (i - 1 > 0) ? arr[i - 1] : ' ';
			char current = arr[i];
			char next = (i + 1 < arr.length) ? arr[i + 1] : ' ';

			// extract acronyms
			// this will actually extract acronyms of any length
			// once it detects this pattern a.b.c
			// it's a greedy lexer that breaks at ' '
			if (Character.isLetter(current) && '.' == next) {

				// Pattern-1 = U.S.A (5 chars)
				// Pattern-2 = U.S.A. (6 chars)
				if (i + 5 < input.length()) {

					// Pattern-1
					if (Character.isLetter(arr[i]) && '.' == arr[i + 1] && Character.isLetter(arr[i + 2]) && '.' == arr[i + 3]
							&& Character.isLetter(arr[i + 4])) {

						for (; i < arr.length && arr[i] != ' '; i++) {
							sb.append(arr[i]);
						}

						// check for Pattern-2 (trailing '.')
						if (i + 1 < input.length() && '.' == arr[i + 1]) {
							sb.append(arr[i++]);
						}

						addToken(tokens, sb);
						sb = new StringBuilder();
						continue;
					}
				}
			}

			if ('w' == current && '/' == next) {
				sb.append(current);
				sb.append(next);
				addToken(tokens, sb);
				sb = new StringBuilder();
				i += 1;
				continue;
			}

			// extract URLs
			if ('h' == current && 't' == next) {
				if (i + 7 < input.length() && "http://".equals(input.substring(i, i + 7))) {

					for (; i < arr.length && arr[i] != ' '; i++) {
						sb.append(arr[i]);
					}

					addToken(tokens, sb);
					sb = new StringBuilder();
					continue;
				}
			}

			// extract windows drive letter paths
			// c:/ or c:\
			if (Character.isLetter(current) && ':' == next) {
				if (i + 2 < input.length() && (arr[i + 2] == '\\' || arr[i + 2] == '/')) {
					sb.append(current);
					sb.append(next);
					sb.append(arr[i + 2]);
					i += 2;
					continue;
				}
			}

			// keep numbers together when separated by a period
			// "4.0" should not be tokenized as { "4", ".", "0" }
			if (Character.isDigit(current) && '.' == next) {
				if (i + 2 < input.length() && Character.isDigit(arr[i + 2])) {
					sb.append(current);
					sb.append(next);
					sb.append(arr[i + 2]);
					i += 2;
					continue;
				}
			}

			// keep alpha characters separated by hyphens together
			// "b-node" should not be tokenized as { "b", "-", "node" }
			if (Character.isLetter(current) && '-' == next) {
				if (i + 2 < input.length() && Character.isLetter(arr[i + 2])) {
					sb.append(current);
					sb.append(next);
					sb.append(arr[i + 2]);
					i += 2;
					continue;
				}
			}

			// need a greedy look-ahead to
			// avoid splitting this into multiple tokens
			// "redbook@vnet.ibm.com" currently is
			// tokenized as { "redbook@vnet", ".", "ibm", ".", "com" }
			// need to greedily lex all tokens up to the space
			// once the space is found, see if the last 4 chars are '.com'
			// if so, then take the entire segment as a single token
			// don't separate tokens concatenated with an underscore
			// eg. "ws_srv01" is a single token, not { "ws", "_", "srv01" }
			if (Character.isLetter(current) && '_' == next) {
				if (i + 2 < input.length() && Character.isLetter(arr[i + 2])) {
					sb.append(current);
					sb.append(next);
					i++;
					continue;
				}
			}

			// extract twitter channels
			if (('#' == current || '@' == current) && ' ' != next) {// &&
																	// !CodeUtilities.isSpecial(next))
																	// {
				sb.append(current);
				continue;
			}

			// keep tokens like tcp/ip and os/2 and system/z together
			if (' ' != current && '/' == next) {
				sb.append(current);
				sb.append(next);
				i++;
				continue;
			}

			if (' ' == current) {
				addToken(tokens, sb);
				sb = new StringBuilder();
				continue;
			}

			// don't tokenize on <word>'s or <words>'
			// but do tokenize on '<words>
			if ('\'' == current) {
				if (' ' == prior) {
					addToken(tokens, "'");
				} else {
					sb.append(current);
				}

				continue;
			}

			if (!Character.isLetterOrDigit(current)) {
				addToken(tokens, sb);
				addToken(tokens, String.valueOf(current));
				sb = new StringBuilder();
				continue;
			}

			sb.append(current);
		}

		if (0 != sb.length()) {
			addToken(tokens, sb);
		}

		return tokens;
	}

	protected static void addToken(List<String> tokens, String text) {
		if (!text.isEmpty()) {
			tokens.add(text);
		}
	}

	protected static void addToken(List<String> tokens, StringBuilder buffer) {
		if (null != buffer && 0 != buffer.length()) {
			addToken(tokens, buffer.toString().trim());
		}
	}

	public static void main(String[] args) {
		StackExchangeApiClientFactory clientFactory = StackExchangeApiClientFactory.newInstance(null, StackExchangeSite.STACK_OVERFLOW);
		StackExchangeApiClient client = clientFactory.createStackExchangeApiClient();

		HashMap<String, Integer> frequency = createTokenFrequency(client.getAnswers("WITHBODY", 24088081).get(0));
		HashMap<String, Integer> frequency2 = createTokenFrequency(client.getAnswers("WITHBODY", 24696739).get(0));
		
		System.out.println(SearchAndRank.totalUniqueTokens.size());
	}

	public static String getQuestionTitle(long questionID) {
		StackExchangeApiClientFactory clientFactory = StackExchangeApiClientFactory.newInstance(null, StackExchangeSite.STACK_OVERFLOW);
		StackExchangeApiClient client = clientFactory.createStackExchangeApiClient();

		return client.getQuestions(questionID).get(0).getTitle();
	}

	private HashSet<String> createTitleTokens(String title) {
		HashSet<String> titleTokens = new HashSet<String>();
		String[] tokens = title.split(" ");
		for (String s : tokens) {
			s = s.toLowerCase().trim();
			if (s.endsWith("?") || s.endsWith("!")) {
				s = s.substring(0, s.length() - 1);
			}
			titleTokens.add(s);
		}
		return titleTokens;
	}

	public static void permutation(ArrayList<String> str) {
		permutation("", str);
	}

	private static void permutation(String prefix, ArrayList<String> str) {
		int n = str.size();
		if (n == 0) {
			tempKeyword.add(prefix);
		} else {
			for (int i = 0; i < n; i++) {
				ArrayList<String> temp = new ArrayList<String>();
				for (int j = 0; j < i; j++) {
					temp.add(str.get(j));
				}
				for (int j = i + 1; j < n; j++) {
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
