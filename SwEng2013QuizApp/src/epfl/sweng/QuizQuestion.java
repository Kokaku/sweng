package epfl.sweng;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for questions.
 * 
 * @author kokaku
 * 
 */

public abstract class QuizQuestion {

	private String mQuestion;
	private String[] mAnswers;
	private int mSolutionIndex;
	private Set<String> mTags;
	
	/**
	 * Default constructor
	 * 
	 * @param question a string inviting the user to answer the quiz question
	 * @param answers an array of possible answers
	 * @param solutionIndex index of the right answer
	 * @param tags set of keywords describing the question
	 */
	public QuizQuestion(String question, String[] answers, int solutionIndex, 
	                    Set<String> tags) {
		mQuestion = question;
		mAnswers = answers.clone();
		mSolutionIndex = solutionIndex;
		mTags = new HashSet<String>(tags);
	}
	
	/**
	 * @return the string inviting the user to answer the quiz question
	 */
	public String getQuestion() {
		return mQuestion;
	}
	
	/**
	 * @return the array of possible answers
	 */
	public String[] getAnswers() {
		return mAnswers.clone();
	}
	
	/**
	 * @param answerId the index of the answer you want to check
	 * @return true if answerId is the index of the correct answer
	 * otherwise return false
	 */
	public boolean isSolutionCorrect(int answerId) {
		return mSolutionIndex == answerId;
	}
	
	/**
	 * @return the set of keywords describing the question
	 */
	public Set<String> getTags() {
		return new HashSet<String>(mTags);
	}
	
	protected int getSolutionIndex() {
		return mSolutionIndex;
	}
}
