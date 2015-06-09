package j2s;

import java.io.Serializable;

import com.google.code.stackexchange.schema.Answer;

public class AnswerWrapper implements Serializable{
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
