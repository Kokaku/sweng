package epfl.sweng.quizquestions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.utils.JSONUtilities;

/**
 * Represents a quiz question
 * @author kokaku
 * 
 */
public class QuizQuestion {

	private String mQuestion;
	private List<String> mAnswers;
	private int mSolutionIndex;
	private Set<String> mTags;
	private int mId;
	private String mOwner;
	
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
	 * - solutionIndex is negative
	 */
	public QuizQuestion(final String question, final List<String> answers,
	                    final int solutionIndex, final Set<String> tags,
	                    final int id, final String owner) {
	    initializeQuestion(question, answers, solutionIndex, tags, id, owner);
	}
	
	/**
	 * Create a question from a JSON formatted string.
	 * 
	 * @param jsonInput a JSON formatted question
	 * @throws JSONException if a parsing error occurs
	 */
	public QuizQuestion(final String jsonInput) throws JSONException {
	    JSONObject json = new JSONObject(jsonInput);
	    
	    initializeQuestion(json.getString("question"),
             JSONUtilities.parseAnswers(json),
             json.getInt("solutionIndex"),
             JSONUtilities.parseTags(json), 0, null);
	}
	
	private void initializeQuestion(final String question, final List<String> answers,
	                                final int solutionIndex, final Set<String> tags,
	                                final int id, final String owner) {
	    // For now owner is allowed to be null
        if (question == null || answers == null || tags == null) {
            throw new IllegalArgumentException("Question can't be instanciated"
                + "with null parameters");
        } else if (answers.size() < 2) {
            throw new IllegalArgumentException("A question needs at least two answers");
        } else if (tags.size() < 1) {
            throw new IllegalArgumentException("A question needs at least one tag");
        } else if (solutionIndex < 0) {
            throw new IllegalArgumentException("solutionIndex can't be negative");
        } else if (solutionIndex >= answers.size()) {
            throw new IllegalArgumentException("solutionIndex can't be greater than answers.length");
        }
        
        mQuestion = question;
        mAnswers = new ArrayList<String>(answers);
        mSolutionIndex = solutionIndex;
        mTags = new TreeSet<String>(tags);
        mId = id;
        mOwner = owner;
	}
	
	/**
	 * Create a question with no question ID and owner.
	 */
	public QuizQuestion(final String question, final List<String> answers,
	                    final int solutionIndex, final Set<String> tags) {
	    this(question, answers, solutionIndex, tags, 0, null);
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
     * @return the id of the question
     */
    public int getId() {
        return mId;
    }
    
    /**
     * @return the string corresponding to the owner of the question
     */
    public String getOwner() {
        return mOwner;
    }
	
	/**
	 * @return the array of possible answers
	 */
	public List<String> getAnswers() {
		return new ArrayList<String>(mAnswers);
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
