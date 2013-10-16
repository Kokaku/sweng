package epfl.sweng.questions;

import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a quiz question
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
	 * @throws IllegalArgumentException if :
	 * - question, answers or tags are null
	 * - there is less than two answers
	 * - there is less than one tag
	 * - solutionIndex is negatif
	 */
	public QuizQuestion(String question, String[] answers, int solutionIndex, 
	                    Set<String> tags) {
	    if (question == null || answers == null || tags == null) {
	        throw new IllegalArgumentException("Question can't be instanciated"
	            + "with null parameters");
	    } else if (answers.length < 2) {
            throw new IllegalArgumentException("A question needs at least two answers");
        } else if (tags.size() < 1) {
            throw new IllegalArgumentException("A question needs at least one tag");
        } else if (solutionIndex < 0) {
            throw new IllegalArgumentException("solutionIndex can't be negative");
        } else if (solutionIndex >= answers.length) {
            throw new IllegalArgumentException("solutionIndex can't be greater than answers.length");
        }
	    
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
