/**
 * 
 */
package epfl.sweng.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.test.AndroidTestCase;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * @author lseguy
 * 
 */
public class QuizQuestionTest extends AndroidTestCase {

	private QuizQuestion mQuestion;
	private Set<String> mTags;

	public static final int CORRECT_ANSWER_ID = 1;
	public static final String QUESTION_TEXT = "This is the question";
	public static final String[] LIST_OF_ANSWERS = { "Answer 1", "Answer 2" };

	protected void setUp() throws Exception {
		super.setUp();

		mTags = new TreeSet<String>();
		mTags.add("Tag1");
		mTags.add("Tag2");
		mQuestion = new QuizQuestion(QUESTION_TEXT,
				Arrays.asList(LIST_OF_ANSWERS), CORRECT_ANSWER_ID, mTags);
	}

	public void testCreateQuestionWithNullQuestion() {
		try {
			@SuppressWarnings("unused")
			QuizQuestion illegalQuestion = new QuizQuestion(null,
					Arrays.asList(LIST_OF_ANSWERS), 0, mTags);
			fail("Constructor can't accept null arguments");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testCreateQuestionWithNullAnswers() {
		try {
			@SuppressWarnings("unused")
			QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
					null, 0, mTags);
			fail("Constructor can't accept null arguments");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testCreateQuestionWithNullTags() {
		try {
			@SuppressWarnings("unused")
			QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
					Arrays.asList(LIST_OF_ANSWERS), 0, null);
			fail("Constructor can't accept null arguments");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testCreateQuestionWithOneAnswer() {
		try {
			@SuppressWarnings("unused")
			QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
					Arrays.asList(new String[] { "answer1" }), 0, mTags);
			fail("Constructor must be called with more than one answer");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testCreateQuestionWithNoTags() {
		try {
			@SuppressWarnings("unused")
			QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT, 
					Arrays.asList(LIST_OF_ANSWERS), 1, new TreeSet<String>());
			fail("Set of tags can't be empty");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testCreateQuestionWithNegativeSolutionIndex() {
		try {
			@SuppressWarnings("unused")
			QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
					Arrays.asList(LIST_OF_ANSWERS), -1, mTags);
			fail("Constructor can't accept a negative solutionIndex");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testCreateQuestionWithOutOfBoundsSolutionIndex() {
		try {
			@SuppressWarnings("unused")
			QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
					Arrays.asList(LIST_OF_ANSWERS), LIST_OF_ANSWERS.length + 1, mTags);
			fail("solutionIndex must be between 0 and answers.length-1");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testConstructorCopiesAnswers() {
		List<String> notSafeAnswers = new ArrayList<String>();
		notSafeAnswers.add("Ans 1");
		notSafeAnswers.add("Ans 2");
		mQuestion = new QuizQuestion(QUESTION_TEXT, notSafeAnswers, 0, mTags);
		notSafeAnswers.add("Bug");
		assertFalse("Constructor must copy array of answers",
				notSafeAnswers.equals(mQuestion.getAnswers()));
	}

	public void testConstructorCopiesTags() {
		mTags.add("Bug!");
		assertFalse("Constructor must copy set of tags",
				mTags.equals(mQuestion.getTags()));
	}

	public void testCheckCorrectAnswerIsTrue() {
		assertTrue(mQuestion.isSolutionCorrect(CORRECT_ANSWER_ID));
	}

	public void testCheckCorrectAnswerIsFalse() {
		for (int i = 0; i < LIST_OF_ANSWERS.length; ++i) {
			if (i != CORRECT_ANSWER_ID) {
				assertFalse(mQuestion.isSolutionCorrect(i));
			}
		}
	}

	public void testGetQuestionText() {
		assertEquals(QUESTION_TEXT, mQuestion.getQuestion());
	}

	public void testGetSolutionIndex() {
		assertEquals(CORRECT_ANSWER_ID, mQuestion.getSolutionIndex());
	}

	public void testGetAnswersCopy() {
		mQuestion.getAnswers().set(0, "Bug!");
		assertTrue("getAnswers getter must return a copy of the array",
				mQuestion.getAnswers().equals(Arrays.asList(LIST_OF_ANSWERS)));
	}

	public void testGetAnswers() {
		assertTrue(Arrays.asList(LIST_OF_ANSWERS).equals(mQuestion.getAnswers()));
	}

	public void testGetTagsCopy() {
		mQuestion.getTags().add("Bug!");
		assertEquals("getTags getter must return a copy of the set", mTags,
				mQuestion.getTags());
	}

	public void testGetTags() {
		assertEquals(mTags, mQuestion.getTags());
	}

}
