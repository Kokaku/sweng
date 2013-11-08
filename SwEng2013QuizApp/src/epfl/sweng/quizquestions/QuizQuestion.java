package epfl.sweng.quizquestions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.utils.JSONUtilities;

/**
 * Represents a quiz question
 * 
 * @author kokaku
 * 
 */
public class QuizQuestion {
	private static final int MAX_QUESTION_CARACTERS = 500;
	private static final int MAX_ANSWERS = 10;
	private static final int MAX_ANSWER_CARACTERS = 500;
	private static final int MAX_TAGS = 20;
	private static final int MAX_TAG_CARACTERS = 20;

	private String mQuestion;
	private List<String> mAnswers;
	private int mSolutionIndex;
	private Set<String> mTags;
	private long mId;
	private String mOwner;

	/**
	 * 
	 * @param question
	 *            a string corresponding to the question
	 * @param answers
	 *            an array of possible answers
	 * @param solutionIndex
	 *            index of the right answer
	 * @param tags
	 *            set of keywords describing the question
	 * @param id
	 *            unique id of the question
	 * @param owner
	 *            owner of the question
	 * @throws IllegalArgumentException
	 *             if : - question, answers or tags are null - there is less
	 *             than two answers - there is less than one tag - solutionIndex
	 *             is negative
	 */
	public QuizQuestion(final String question, final List<String> answers,
			final int solutionIndex, final Set<String> tags, final long id,
			final String owner) {
		initializeQuestion(question, answers, solutionIndex, tags, id, owner);
	}

	/**
	 * Create a question from a JSON formatted string.
	 * 
	 * @param jsonInput
	 *            a JSON formatted question
	 * @throws JSONException
	 *             if a parsing error occurs
	 */
	public QuizQuestion(final String jsonInput) throws JSONException {
		JSONObject json = new JSONObject(jsonInput);

		initializeQuestion(
				json.getString("question"),
				JSONUtilities.parseJSONArrayToList(json.getJSONArray("answers")),
				json.getInt("solutionIndex"), JSONUtilities
						.parseJSONArrayToSet(json.getJSONArray("tags")), json
						.getLong("id"), json.getString("owner"));
	}

	private void initializeQuestion(final String question,
			final List<String> answers, final int solutionIndex,
			final Set<String> tags, final long id, final String owner) {
		// Owner is allowed to be null, it is generated by the server when
		// submitting
		if (question == null || answers == null || tags == null) {
			throw new IllegalArgumentException("Question can't be instanciated"
					+ "with null parameters");
		} else if (question.replaceAll("\\+s", "").length() < 1
				|| question.length() > MAX_QUESTION_CARACTERS) {
			throw new IllegalArgumentException(
					"The question must not be empty or have more than"
							+ MAX_QUESTION_CARACTERS + " caracters");
		} else if (answers.size() < 2 || answers.size() > MAX_ANSWERS) {
			throw new IllegalArgumentException(
					"A question needs at least two answers and at most"
							+ MAX_ANSWERS + " answers");
		} else if (checkAnswers(answers) != 0) {
			throw new IllegalArgumentException(
					"Each answer must be non empty and not longer than "
							+ MAX_ANSWER_CARACTERS + " caracters");
		}
		if (tags.size() < 1 || tags.size() > MAX_TAGS) {
			throw new IllegalArgumentException(
					"A question needs at least one tag and at most " + MAX_TAGS
							+ " tags.");
		} else if (checkTags(tags) != 0) {
			throw new IllegalArgumentException(
					"Each tag must be non empty and not longer than "
							+ MAX_TAG_CARACTERS + " caracters");
		} else if (solutionIndex < 0 || solutionIndex >= answers.size()) {
			throw new IllegalArgumentException(
					"The solution index must be in the range of the answers");
		} else if (solutionIndex >= answers.size()) {
			throw new IllegalArgumentException(
					"solutionIndex can't be greater than answers.length");
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
	public long getId() {
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
	 * @param answerIndex
	 *            the index of the answer you want to check
	 * @return true if answerIndex is the index of the correct answer, false
	 *         otherwise
	 */
	public boolean isSolutionCorrect(int answerIndex) {
		return mSolutionIndex == answerIndex;
	}

	/**
	 * @return the set of keywords describing the question
	 */
	public Set<String> getTags() {
		return new TreeSet<String>(mTags);
	}

	/**
	 * @return number of errors in the question
	 */
	public int auditErrors() {
		return checkQuestion() + checkAnswers() + checkSolutionIndex()
				+ checkTags();
	}

	/**
	 * @return 1 if there is an error regarding the question field, 0 otherwise
	 */
	private int checkQuestion() {
		return (mQuestion == null
				|| mQuestion.replaceAll("\\+s", "").equals("") || mQuestion
				.length() > MAX_QUESTION_CARACTERS) ? 1 : 0;
	}

	/**
	 * @return the number of errors in the answers list
	 */
	private int checkAnswers() {
		int errors = (mAnswers == null || mAnswers.size() < 2 || mAnswers
				.size() > MAX_ANSWERS) ? 1 : 0;

		return errors + checkAnswers(mAnswers);
	}

	/**
	 * @return 1 if there is an error regarding the solution index, 0 otherwise
	 */
	private int checkSolutionIndex() {
		return (mSolutionIndex < 0 || mSolutionIndex >= mAnswers.size()) ? 1
				: 0;
	}

	/**
	 * @return the number of errors in the tags set
	 */
	private int checkTags() {
		int errors = (mTags == null || mTags.size() < 1 || mTags.size() > MAX_TAGS) ? 1
				: 0;

		return errors + checkTags(mTags);
	}

	private int checkAnswers(List<String> answers) {
		int errors = 0;
		for (int i = 0; i < mAnswers.size(); ++i) {
			errors += (mAnswers.get(i) == null
					|| mAnswers.get(i).replaceAll("\\+s", "").equals("") || mAnswers
					.get(i).length() > MAX_ANSWER_CARACTERS) ? 1 : 0;
		}
		return errors;

	}

	private int checkTags(Set<String> tags) {
		int errors = 0;
		Iterator<String> tagsIter = mTags.iterator();
		while (tagsIter.hasNext()) {
			String currentTag = tagsIter.next();
			errors += (currentTag == null
					|| currentTag.replaceAll("\\+s", "").equals("") || currentTag
					.length() > MAX_TAG_CARACTERS) ? 1 : 0;
		}

		return errors;
	}
}
