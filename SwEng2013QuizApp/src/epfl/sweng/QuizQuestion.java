package epfl.sweng;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author kokaku
 *
 */
public abstract class QuizQuestion {
	/*
	 * DEFAULT_ID is the default value for an id when quiz question doen't have yet a associated id
	 */
	public final static long DEFAULT_ID = -1;
	private long mId;
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
	public QuizQuestion(long questionId, String question, String[] answers, int solutionIndex, Set<String> tags) {
		mId = questionId;
		mQuestion = question;
		mAnswers = answers.clone();
		mSolutionIndex = solutionIndex;
		mTags = new HashSet<String>(tags);
	}
	
	/**
	 * @return the id associated to the quiz question. return DEFAULT_ID if not yet associated.
	 */
	public long getQuestionId() {
		return mId;
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
		return new HashSet<String>(mTags);
	}
}
