package j2s;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
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

public class J2SView extends ViewPart {
	Label label;
	Button button;
	Composite parent;
	String selectedText = null;

	public J2SView() {
	}
	
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		
		label = new Label(parent, SWT.WRAP);

		button = new Button(parent, SWT.PUSH);
		button.setText("Generate Query");
		
		button.addSelectionListener(buttonListener);
		
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		
	}
    
    private SelectionListener buttonListener = new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent arg0) {
			label.setForeground(new Color (Display.getCurrent (), 255, 0, 0));
			
			String filename = "C:\\Users\\Antuan\\Downloads\\methodSelection.txt";
//			String filename = System.getProperty("user.home") + "/Downloads/methodSelection.txt";
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
				sar = new SearchAndRank(searchKeywords);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<MetaData> rankedResults = sar.sortedFinalRanking;
			System.out.println(rankedResults.get(0).getID());
			System.out.println(rankedResults.get(0).printFields());
			System.out.println(rankedResults.get(1).getID());
			System.out.println(rankedResults.get(1).printFields());
			//System.out.println(rankedResults.get(1).getTitleTokens());
			//rankedResults is final sorted list
			//take rankedResults.get(0) for top choice, and so on
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public static void main(String[] args) {
		String filename = System.getProperty("user.home") + "/Downloads/methodSelection.txt";
//		String filename = "C:\\Users\\Antuan\\Downloads\\methodSelection.txt";
		GenerateSwiftQueryString tester = new GenerateSwiftQueryString();
		ArrayList<String> searchKeywords = tester.executeFrequencyAnalysis(filename);
		System.out.println("PRINTING KEYWORDS: " + searchKeywords.toString());
		SearchAndRank sar = null;
		try {
			sar = new SearchAndRank(searchKeywords);
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
		System.out.println("Number 1 ranked answer body is: ");
		System.out.println(rankedResults.get(0).getAnswerBody().toString());
	}
	
	private ISelectionListener selectionListener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
        	if (sourcepart != J2SView.this) {
			    showSelection(sourcepart, selection);
			}
        }
    };
    
    public void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
		setContentDescription(sourcepart.getTitle() + " (" + selection.getClass().getName() + ")");

		if (selection instanceof TextSelection) {
			
			label.setForeground(new Color (Display.getCurrent (), 0, 0, 0));
			
			ITextSelection ts  = (ITextSelection) selection;
			selectedText = ts.getText();
		
			
			if (selectedText.equals("") || selectedText == null) {
				label.setText("Select entire method from Java file");
			} else {
				label.setText(ts.getText());
			}
			parent.layout(true, true);
		}
		
	}
	
	public String getCurrentSelectedText() {
	    final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	    if (!(editor instanceof ITextEditor)) return null;
	    ITextEditor ite = (ITextEditor)editor;
	    
	    ITextSelection its = (ITextSelection) ite.getSelectionProvider().getSelection();
	    
	    return its.getText();
	}

	public void setFocus() {
		// set focus to my widget. For a label, this doesn't
		// make much sense, but for more complex sets of widgets
		// you would decide which one gets the focus.
	}
	
	public void dispose() {
		// important: We need do unregister our listener when the view is disposed
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
		super.dispose();
	}
}