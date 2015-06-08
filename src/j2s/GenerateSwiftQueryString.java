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

	private static HashMap<String, Integer> frequency = new HashMap<String, Integer>();
	private static HashSet<String> filterKeys;
	private static final int NUM_FREQ_RETURN = 3;
	private static String title = "";
	public static MetaData mdQuery = new MetaData();

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
						}
					}
					StringTokenizer st = new StringTokenizer(sCurrentLine);
					while (st.hasMoreTokens()) {
						parseToken(st.nextToken());
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

		
		filterFrequency();
		ArrayList<String> result = sortByValue(frequency);
		ArrayList<String> keywords = new ArrayList<String>();
		for (int i = 0; i < NUM_FREQ_RETURN; i++) {
			keywords.add(result.get(result.size()-i-1));
		}
		
		generateQueryMetaDataObject();
		frequency = new HashMap<String, Integer>();
		
		return keywords;
	}
	
	private static void generateQueryMetaDataObject() {
		String[] tokens = title.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|([0-9]+)|=|(\\()|(\\))|(\\.)|(\\_)|(\\n)|(\\,)");
		HashSet<String> titleTokens = new HashSet<String>();
		for (int i = 0; i < tokens.length; i++) {
			titleTokens.add(tokens[i].toLowerCase());
		}
		
		mdQuery.setTitleTokens(titleTokens);

		HashMap<String, Integer> tempFreq = new HashMap<String, Integer>();
		String body = "";
		ArrayList<String> freqKeys = new ArrayList<String>();
		freqKeys.addAll(frequency.keySet());
		for (int i = 0; i < freqKeys.size(); i++) {
			body += freqKeys.get(i) + " ";
		}
		body.substring(0, body.length()-1);
		Answer a = new Answer();
		a.setBody(body);
		a.setTitle("ANTUAN_AND_SANCHIT");
		tempFreq = SearchAndRank.createTokenFrequency(a);
		mdQuery.setFrequency(tempFreq);
	}

	public static ArrayList<String> sortByValue(HashMap<String, Integer> frequency) {
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
				if (removalSetKey.get(j).contains(keySetFrequency.get(i))) {
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
		
		list = new LinkedList<>(newFrequency.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		fullResult = new LinkedHashMap<>();
		ArrayList<String> result = new ArrayList<String>();
		for (Map.Entry<String, Integer> entry : list) {
			fullResult.put(entry.getKey(), entry.getValue());
			result.add(entry.getKey());
		}
		Collections.reverse(result);
		System.out.println(fullResult.toString());

		return result;
	}

	private static void filterFrequency() {
		for (String key : filterKeys) {
			if (frequency.containsKey(key)) {
				frequency.remove(key);
			}
		}
	}

	public static String applyStemming(String t) {
		String token = t;
		if (t.endsWith("ing")) {
			token = t.substring(0, t.length() - 3);
		}
		if (t.endsWith("ed") || t.endsWith("ly")) {
			token = t.substring(0, t.length() - 2);
		}
		return token;
	}

	private static void parseToken(String token) {
		String[] tokens = token.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|([0-9]+)|=|(\\()|(\\))|(\\.)|(\\_)|(\\n)|(\\,)");
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].toLowerCase();
		}
		for (String t : tokens) {
			t = applyStemming(t);
			if (frequency.containsKey(t)) {
				frequency.put(t, frequency.get(t) + 1);
			} else {
				frequency.put(t, 1);
			}
		}
	}
}
