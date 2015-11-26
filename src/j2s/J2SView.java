package j2s;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.code.stackexchange.schema.Answer;
import com.google.code.stackexchange.schema.Question;

public class J2SView extends ViewPart {
	Label label;
	Button button;
	Composite parent;
	String selectedText = null;
	String selectedLink = "";
	ArrayList<Button> resultRadioButtons;
	int resultIndex;
	public static ArrayList<String> PreRank = new ArrayList<String>();
	
	public J2SView() {
	}

	public void createPartControl(Composite parent) {
		this.parent = parent;

		parent.setLayout(new RowLayout(SWT.VERTICAL));

		label = new Label(parent, SWT.WRAP);
		FontData[] fD = label.getFont().getFontData();
		fD[0].setHeight(16);
		label.setFont(new Font(null, fD));

		button = new Button(parent, SWT.PUSH);
		button.setText("Generate Query");

		button.addSelectionListener(buttonListener);

		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		
		resultRadioButtons = new ArrayList<Button>();
	}

	private SelectionListener buttonListener = new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			label.setForeground(new Color(Display.getCurrent(), 255, 0, 0));
			
			label.setText("Query generated, awaiting results...");
			parent.layout(true, true);

			String filename = System.getProperty("user.home") + "/Downloads/methodSelection.txt";
			File file = new File(filename);
			file.setReadable(true);
			file.setWritable(true);
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(file);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.print(selectedText);
			writer.close();
			GenerateSwiftQueryString tester = new GenerateSwiftQueryString();
			ArrayList<String> searchKeywords = tester.executeFrequencyAnalysis(filename);
			System.out.println("PRINTING KEYWORDS: " + searchKeywords.toString());
			SearchAndRank sar = null;
			try {
				sar = new SearchAndRank(searchKeywords, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<MetaData> rankedResults = sar.sortedFinalRanking;
			for (int i = 0; i < rankedResults.size(); i++) {
				System.out.println(rankedResults.get(i).getID());
				System.out.println(rankedResults.get(i).printFields());
				System.out.println(rankedResults.get(i).getCosValueFinal());
				System.out.println(rankedResults.get(i).getNormLinScore());
				System.out.println(rankedResults.get(i).getFinalRankingScore());
			}

			label.setText("Results: ");
			
			for (int i = 0; i < 2; i++) {
				MetaData result = rankedResults.get(i);
				if (result == null) {
					continue;
				} else {
					String url = "http://www.stackoverflow.com/questions/" + result.getID();
					Button buttonResult = newActionLink(parent, result.getQuestionTitle() + "\n" + url);
					resultRadioButtons.add(buttonResult);
				}
				
			}
			button.setVisible(false);
			parent.layout(true, true);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * Creates and returns a new Link, which when selected, will run the
	 * specified action.
	 * 
	 * @param parent
	 *            The parent composite in which to create the new link.
	 * @param text
	 *            The text to display in the link.
	 * @return The new link.
	 */
	public Button newActionLink(Composite parent, String text) {
		Button button = new Button(parent, SWT.RADIO);
		FontData[] fD = button.getFont().getFontData();
		fD[0].setHeight(16);
		button.setFont(new Font(null, fD));
		button.setSelection(false);
		button.setText(text);
		button.setBackground(parent.getBackground());
		button.addSelectionListener(linkListener);
		return button;
	}

	private SelectionListener linkListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			Button selectedButton = (Button) arg0.getSource();
			String url = selectedButton.getText().split("\n")[1];
			StringSelection selection = new StringSelection(url);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
			label.setText("URL copied and Swift file generated");
//			button.setVisible(true);
			parent.layout(true, true);
			
			//here (should not be in main but actual plugin code but this is for testing)
			//need to take the selection made by the programmer (rank 1 or 2)
			//and call synthesize function in searchandrank with that index
			resultIndex = resultRadioButtons.indexOf(selectedButton);
			SearchAndRank.synthesize(resultIndex);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// TODO Auto-generated method stub

		}
	};
	
	private static void createTestEvalData() {
		String filename =
				 "C:\\Users\\Sanchit\\Downloads\\AllEvalCases.txt";
		String output =
				 "C:\\Users\\Sanchit\\Downloads\\output.csv";
		String methodTested =
				 "C:\\Users\\Sanchit\\Downloads\\methodTested.txt";
		
//		String filename =
//				System.getProperty("user.home") + "/Downloads/AllEvalCases.txt";
//		String output =
//				System.getProperty("user.home") + "/Downloads/output.csv";
//		String methodTested =
//				System.getProperty("user.home") + "/Downloads/methodTested.txt";
		
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));
			PrintWriter writer = new PrintWriter(methodTested, "UTF-8");
			PrintWriter outputWriter = new PrintWriter(output, "UTF-8");
			while ((sCurrentLine = br.readLine()) != null) {
				
				sCurrentLine = sCurrentLine.trim();
				if (!sCurrentLine.equals("")) {
					if (sCurrentLine.equals("*/")) {
						sCurrentLine = br.readLine();
						String methodHeader = sCurrentLine.split("\\(")[0];
						String[] methodHeaderArray = methodHeader.split(" ");
						String methodName = methodHeaderArray[methodHeaderArray.length - 1];
						outputWriter.println(methodName);
						outputWriter.println();
					}
					writer.println(sCurrentLine);
//					outputWriter.println(sCurrentLine);
				}
				else {
					writer.close();
					GenerateSwiftQueryString tester = new GenerateSwiftQueryString();
					ArrayList<String> searchKeywords = tester.executeFrequencyAnalysis(methodTested);
					writer = new PrintWriter(methodTested, "UTF-8");
					System.out.println("Keywords: " + searchKeywords.toString());
					
					outputWriter.print("Keywords,");
					for (String str : searchKeywords) {
						outputWriter.print(str + " ");
					}
					outputWriter.println();
					SearchAndRank sar = null;
					try {
						sar = new SearchAndRank(searchKeywords, outputWriter);
					} catch (IOException e) {
						if (e.getMessage() == "TimeOut") {
						resetStaticVariables();
						outputWriter.println();
						outputWriter.println("---------------------------------------");
						outputWriter.println();
						break;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					ArrayList<MetaData> rankedResults = sar.sortedFinalRanking;
//					SearchAndRank.synthesize(0);
					
					resetStaticVariables();
					
					outputWriter.println();
					outputWriter.println("---------------------------------------");
					outputWriter.println();
				}
			}
			outputWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void resetStaticVariables() {
		PreRank.clear();
		SearchAndRank.resetStaticVariables();
	}

	public static void main(String[] args) {
//		String filename = System.getProperty("user.home") + "/Downloads/methodSelection.txt";
//		 String filename =
//		 "C:\\Users\\Antuan\\Downloads\\methodSelection.txt";
//		GenerateSwiftQueryString tester = new GenerateSwiftQueryString();
//		ArrayList<String> searchKeywords = tester.executeFrequencyAnalysis(filename);
//		System.out.println("PRINTING KEYWORDS: " + searchKeywords.toString());
//		SearchAndRank sar = null;
//		try {
//			sar = new SearchAndRank(searchKeywords);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		ArrayList<MetaData> rankedResults = sar.sortedFinalRanking;
////		for (int i = 0; i < rankedResults.size(); i++) {
////			System.out.println(rankedResults.get(i).getID());
////			System.out.println(rankedResults.get(i).printFields());
////			System.out.println(rankedResults.get(i).getCosValueFinal());
////			System.out.println(rankedResults.get(i).getNormLinScore());
////			System.out.println(rankedResults.get(i).getFinalRankingScore());
////		}
//		
//		SearchAndRank.synthesize(0);

//		System.out.println("Number 1 ranked answer body is: ");
//		System.out.println(rankedResults.get(0).getAnswerBody().toString());
//		System.out.println("Number 2 ranked answer body is: ");
//		System.out.println(rankedResults.get(1).getAnswerBody().toString());
		

		createTestEvalData();

	}

	private ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			if (sourcepart != J2SView.this) {
				showSelection(sourcepart, selection);
			}
		}
	};
	
	public void hideResults() {
		for (Button result : resultRadioButtons) {
			result.setVisible(false);
		}
	}

	public void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
		setContentDescription(sourcepart.getTitle() + " (" + selection.getClass().getName() + ")");
		
		hideResults();
		resultRadioButtons = new ArrayList<Button>();

		if (selection instanceof TextSelection) {
			button.setVisible(true);
			label.setForeground(new Color(Display.getCurrent(), 0, 0, 0));

			ITextSelection ts = (ITextSelection) selection;
			selectedText = ts.getText();
			String[] lines = selectedText.split("\r\n|\r|\n");

			if (selectedText.equals("") || selectedText == null) {
				label.setText("Select entire method from Java file");
			} else {
				int startIndex = -1;
				int endIndex = -1;
				if (selectedText.contains("public")) {
					startIndex = selectedText.indexOf("public");
					endIndex = selectedText.indexOf(")", startIndex);
				} else if (selectedText.contains("private")) {
					startIndex = selectedText.indexOf("private");
					endIndex = selectedText.indexOf(")", startIndex);
				}
				if (startIndex != -1 && endIndex != -1) {
					String methodDeclaration = selectedText.substring(startIndex, endIndex + 1);
					label.setText("Selected " + lines.length + " lines from method: \n\n" + methodDeclaration);
				} else {
					label.setText("Select a valid method from the Java file.");
				}
			}
			parent.layout(true, true);
		}

	}

	public String getCurrentSelectedText() {
		final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (!(editor instanceof ITextEditor))
			return null;
		ITextEditor ite = (ITextEditor) editor;

		ITextSelection its = (ITextSelection) ite.getSelectionProvider().getSelection();

		return its.getText();
	}

	public void setFocus() {
		// set focus to my widget. For a label, this doesn't
		// make much sense, but for more complex sets of widgets
		// you would decide which one gets the focus.
	}

	public void dispose() {
		// important: We need do unregister our listener when the view is
		// disposed
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
		super.dispose();
	}
}