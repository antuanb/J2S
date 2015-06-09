package j2s;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.google.code.stackexchange.schema.Answer;

public class test {
	public static ArrayList<AnswerWrapper> testing = new ArrayList<AnswerWrapper>();
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		Answer a = new Answer();
		a.setAccepted(true);
		a.setBody("testing dis");
		AnswerWrapper aw = new AnswerWrapper(a, 0.0);
		
		Answer b = new Answer();
		AnswerWrapper bw = new AnswerWrapper(b, 0.0);
	
		testing.add(aw);
		testing.add(bw);
		
		//writeObject();
		readObject();
	}
	
	private static void writeObject() throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				"testing.txt"));
		oos.writeObject(testing);
		oos.flush();
		oos.close();
		System.out.println(testing.size() + " size before");
	}
	
	private static void readObject() throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("testing.txt"));
		@SuppressWarnings("unchecked")
		ArrayList<AnswerWrapper> list = (ArrayList<AnswerWrapper>) ois.readObject();
		ois.close();
		System.out.println(list.size() + " size after");
		System.out.println(list.get(0).getAnswer().getBody());
	}
}
