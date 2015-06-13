package j2s;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.google.code.stackexchange.schema.Answer;

public class GenerateSwiftQueryString {

	private static String title = "";
	private static String body = "";
	public static MetaData mdQuery = new MetaData();
	private static boolean initialQuery = true;
	private static ArrayList<String> globalKeywords = new ArrayList<String>();
	public static ArrayList<String> controlFlowCode = new ArrayList<String>();

	public ArrayList<String> executeFrequencyAnalysis(String filepath) {
		BufferedReader br = null;
		boolean flag = true;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filepath));
			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.trim().equals("")) {
					if (flag) {
						if (sCurrentLine.startsWith("public") || sCurrentLine.startsWith("private")) {
							flag = false;
							title = sCurrentLine;
							parseControlFlowLine(sCurrentLine, true);
						}
						//carry over method header comments to swift file
						else {
							controlFlowCode.add(sCurrentLine);
						}
					}
					else {
						parseControlFlowLine(sCurrentLine, false);
					}
					StringTokenizer st = new StringTokenizer(sCurrentLine);
					while (st.hasMoreTokens()) {
						body += st.nextToken() + " ";
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		generateQueryMetaDataObject();
		
		return globalKeywords;
	}
	
	private static void parseControlFlowLine(String line, boolean isHeader) {
		if (isHeader) {
			String methodHeader = "";
			//public static? void/type methodName(params?) {?
			//handle potential header specific tokens and create
			//string with the line for method declaration as one string (not an arraylist)
			
			controlFlowCode.add(methodHeader);
		}
		else {
			String curLine = "";
			//handle every other line here
			//likely need hashmap of the syntax mapping we talked about earlier java-swift
			//iterative parse with no memory of past/future lines
			//treat all tokens as literal copy overs, not concerned with where an if statement's 
			//bracket ends and so forth
			
			controlFlowCode.add(curLine);
		}
	}
	
	private static void generateQueryMetaDataObject() {
		String[] tokens = title.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|([0-9]+)|=|(\\()|(\\))|(\\.)|(\\_)|(\\n)|(\\,)");
		HashSet<String> titleTokens = new HashSet<String>();
		for (int i = 0; i < tokens.length; i++) {
			titleTokens.add(tokens[i].toLowerCase());
		}
		mdQuery.setTitleTokens(titleTokens);

		HashMap<String, Integer> tempFreq = new HashMap<String, Integer>();
		
		Answer a = new Answer();
		a.setBody(body);
		a.setTitle(title);
		AnswerWrapper aw = new AnswerWrapper(null, a, 0.0);
		
		tempFreq = SearchAndRank.createTokenFrequency(aw, false);
		SearchAndRank.DSet.add(tempFreq.keySet());
		mdQuery.setFrequency(tempFreq);
	}
	
	public static HashMap<String, Integer> removeSubstring(HashMap<String, Integer> frequency) {		
		List<Map.Entry<String, Integer>> list = new LinkedList<>(frequency.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				if (o1.getKey().length() < o2.getKey().length()) {
					return -1;
				}
				else if (o1.getKey().length() == o2.getKey().length()) {
					return 0;
				}
				else {
					return 1;
				}
			}
		});
		Collections.reverse(list);
		Map<String, Integer> fullResult = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : list) {
			fullResult.put(entry.getKey(), entry.getValue());
		}
						
		List<String> keySetFrequency = new LinkedList<String>();
		keySetFrequency.addAll(fullResult.keySet());
		List<Integer> valueSetFrequency = new LinkedList<Integer>();
		valueSetFrequency.addAll(fullResult.values());
		HashMap<String, Integer> newFrequency = new HashMap<String, Integer>();	
		ArrayList<String> removalSetKey = new ArrayList<String>();
		ArrayList<Integer> removalSetValue = new ArrayList<Integer>();
		for (int i = 0; i < keySetFrequency.size(); i++) {
			boolean flag = true;
			for (int j = 0; j < removalSetKey.size(); j++) {
				if (removalSetKey.get(j).contains(keySetFrequency.get(i)) && keySetFrequency.get(i).length() > 2) {
					removalSetValue.set(j, valueSetFrequency.get(i) + removalSetValue.get(j));
					flag = false;
					break;
				}
			}
			if (flag) {
				removalSetKey.add(keySetFrequency.get(i));
				removalSetValue.add(valueSetFrequency.get(i));
			}
		}
		for (int i = 0; i < removalSetKey.size(); i++) {
			newFrequency.put(removalSetKey.get(i), removalSetValue.get(i));
		}

		return newFrequency;
	}

	public static ArrayList<String> sortByValue(HashMap<String, Integer> frequency) {
		HashMap<String, Integer> newFrequency = removeSubstring(frequency);
		List<Map.Entry<String, Integer>> list = new LinkedList<>(newFrequency.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<String, Integer> fullResult = new LinkedHashMap<>();
		ArrayList<String> result = new ArrayList<String>();
		for (Map.Entry<String, Integer> entry : list) {
			fullResult.put(entry.getKey(), entry.getValue());
			result.add(entry.getKey());
		}
		Collections.reverse(result);
		System.out.println(fullResult.toString());
		if (initialQuery) {
			initialQuery = false;
			globalKeywords.add(result.get(0));
			globalKeywords.add(result.get(1));
			globalKeywords.add(result.get(2));
		}
		return result;
	}
}
