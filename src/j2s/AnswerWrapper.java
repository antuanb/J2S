package j2s;

import java.io.Serializable;

import com.google.code.stackexchange.schema.Answer;
import com.google.code.stackexchange.schema.Question;

public class AnswerWrapper implements Serializable {

	private static final long serialVersionUID = -2413137617961594719L;
	Answer answer;
	Question question;
	double rank;

	public AnswerWrapper(Question question, Answer answer, double rank) {
		this.question = question;
		this.answer = answer;
		this.rank = rank;
	}

	public Question getQuestion() {
		return question;
	}

	public Answer getAnswer() {
		return answer;
	}

	public double getRank() {
		return rank;
	}

	public String toString() {
		return "" + this.answer.getAnswerId() + ", "
				+ this.question.getQuestionId();
	}
}