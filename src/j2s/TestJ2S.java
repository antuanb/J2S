package j2s;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class TestJ2S {
	public static void main(String args[]) {
		String filepath = "ReflectionTestData.txt";
		String path = "tempTestFile.txt";
		String name = "";
		BufferedReader br = null;
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filepath));
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.trim().startsWith("public")) {
					name = sCurrentLine;
				}
				if (sCurrentLine.trim().equals("")) {
					constructFile(lines, path);
					executeTestsOnFilename(path, name);
					lines = new ArrayList<String>();
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					lines.add(sCurrentLine);
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
	}

	public static void constructFile(ArrayList<String> lines, String file) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < lines.size(); i++) {
			writer.println(lines.get(i));
		}
		writer.close();
	}

	public static void executeTestsOnFilename(String filename, String name) {
		String[] args = {"tempTestFile.txt", "testing.."};
		J2SView.main(args);
	}
}
