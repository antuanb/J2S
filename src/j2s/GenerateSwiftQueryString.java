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

public class GenerateSwiftQueryString {

	private static HashMap<String, Integer> frequency = new HashMap<String, Integer>();
	private static HashSet<String> filterKeys;
	private static final int NUM_FREQ_RETURN = 1;

	static {
		filterKeys = new HashSet<String>();
		filterKeys.add("get");
		filterKeys.add(" ");
		filterKeys.add("public");
		filterKeys.add("string");
		filterKeys.add(" ");
		filterKeys.add(".");
		filterKeys.add(";");
		filterKeys.add("");
		filterKeys.add("{");
		filterKeys.add("}");
		filterKeys.add("[");
		filterKeys.add("]");
	}

	public ArrayList<String> executeFrequencyAnalysis(String filepath) {
		BufferedReader br = null;
		try {
			String sCurrentLine;

			br = new BufferedReader(new FileReader(filepath));
			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.trim().equals("")) {
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
		ArrayList<String> result = sortByValue();
		frequency = new HashMap<String, Integer>();
		ArrayList<String> keywords = new ArrayList<String>();
		for (int i = 0; i < NUM_FREQ_RETURN; i++) {
			keywords.add(result.get(result.size()-i+1));
		}
		return keywords;
	}

	public static ArrayList<String> sortByValue() {
		HashMap<String, Integer> newFrequency = new HashMap<String, Integer>();
		List<String> keySetFrequency = new LinkedList<String>();
		keySetFrequency.addAll(frequency.keySet());
		List<Integer> valueSetFrequency = new LinkedList<Integer>();
		valueSetFrequency.addAll(frequency.values());
		for (int i = 0; i < keySetFrequency.size(); i++) {
			for (int j = 0; j < keySetFrequency.size(); j++) {
				if (i != j) {
					if (keySetFrequency.get(i).contains(keySetFrequency.get(j))) {
						if (keySetFrequency.get(i).length() > keySetFrequency.get(j).length()) {
							valueSetFrequency.set(i, valueSetFrequency.get(i) + valueSetFrequency.get(j));
							keySetFrequency.remove(j);
							valueSetFrequency.remove(j);
						}
					}
				}
			}
		}
		for (int i = 0; i < keySetFrequency.size(); i++) {
			newFrequency.put(keySetFrequency.get(i), valueSetFrequency.get(i));
		}

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
		String[] tokens = token.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|([0-9]+)|=|(\\()|(\\))|(\\.)|(\\_)");
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
