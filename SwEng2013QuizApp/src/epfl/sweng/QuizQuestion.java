package epfl.sweng;

import java.util.Set;
import java.util.TreeSet;

/**
 * Represent a quiz question
 * @author kokaku
 * 
 */
public class QuizQuestion {

	private String mQuestion;
	private String[] mAnswers;
	private int mSolutionIndex;
	private Set<String> mTags;
	
	/**
	 * 
	 * @param question a string corresponding to the question
	 * @param answers an array of possible answers
	 * @param solutionIndex index of the right answer
	 * @param tags set of keywords describing the question
	 */
	public QuizQuestion(String question, String[] answers, int solutionIndex, 
	                    Set<String> tags) {
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
	 * @return the string corresponding to the question
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
		return new TreeSet<String>(mTags);
	}
}
