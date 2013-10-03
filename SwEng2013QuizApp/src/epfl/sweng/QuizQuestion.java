package epfl.sweng;

import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author kokaku
 * 
 */
public class QuizQuestion {

	private String mQuestion;
	private String[] mAnswers;
	private int mSolutionIndex;
	private Set<String> mTags;
	
	/**
	 * A QuizQuestion represent a quiz question
	 * 
	 * @param questionId is a unique id associated to each quiz question
	 * @param question is a string that invite the user to answer the quiz question
	 * @param answers is a list of possible answers
	 * @param solutionIndex is the index of the correct solution
	 * @param tags is the set of keyword about what is the question
	 */
	public QuizQuestion(String question, String[] answers, int solutionIndex, Set<String> tags) {
		mQuestion = question;
		mAnswers = answers.clone();
		mSolutionIndex = solutionIndex;
		mTags = new TreeSet<String>(tags);
	}
	
	/**
	 * @return the index of the correct answer
	 */
	public int getSolutionIndex() {
		return mSolutionIndex;
	}
	
	/**
	 * @return the string that invite the user to answer the quiz question
	 */
	public String getQuestion() {
		return mQuestion;
	}
	
	/**
	 * @return the list of possible answers
	 */
	public String[] getAnswers() {
		return mAnswers.clone();
	}
	
	/**
	 * @param answerId is the index of the answer you want to check
	 * @return true if answerId is the index of the correct answer else return false
	 */
	public boolean isSolutionCorrect(int answerId) {
		return mSolutionIndex == answerId;
	}
	
	/**
	 * @return the set of keyword about what is the question
	 */
	public Set<String> getTags() {
		return new TreeSet<String>(mTags);
	}
}
