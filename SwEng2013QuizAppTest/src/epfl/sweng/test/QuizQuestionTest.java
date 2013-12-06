/**
 * 
 */
package epfl.sweng.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.util.Log;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * @author lseguy
 * 
 */
public class QuizQuestionTest extends AndroidTestCase {

    private static final String LOG_TAG = QuizQuestionTest.class.getName();    
	private QuizQuestion mQuestion;
	private Set<String> mTags;

	private static final int CORRECT_ANSWER_ID = 1;
	private static final String QUESTION_TEXT = "This is the question";
	private static final String[] LIST_OF_ANSWERS = { "Answer 1", "Answer 2" };

	protected void setUp() {
	    try {
	        super.setUp();
	    } catch (Exception e) {
            Log.v(LOG_TAG, "Exception in setUp()", e);
	    }

		mTags = new TreeSet<String>();
		mTags.add("Tag1");
		mTags.add("Tag2");
		mQuestion = new QuizQuestion(QUESTION_TEXT,
				Arrays.asList(LIST_OF_ANSWERS), CORRECT_ANSWER_ID, mTags);
	}

	public void testCreateQuestionWithNullQuestion() {
		try {
			new QuizQuestion(null, Arrays.asList(LIST_OF_ANSWERS), 0, mTags);
			fail("Constructor can't accept null arguments");
		} catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCreateQuestionWithNullQuestion()", e);
		}
	}

	public void testCreateQuestionWithNullAnswers() {
		try {
		    new QuizQuestion(QUESTION_TEXT, null, 0, mTags);
			fail("Constructor can't accept null arguments");
		} catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCreateQuestionWithNullAnswers()", e);
        }
	}

	public void testCreateQuestionWithNullTags() {
		try {
			new QuizQuestion(QUESTION_TEXT, Arrays.asList(LIST_OF_ANSWERS), 0, null);
			fail("Constructor can't accept null arguments");
		} catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCreateQuestionWithNullTags()", e);
        }
	}

	public void testCreateQuestionWithOneAnswer() {
		try {
			new QuizQuestion(QUESTION_TEXT, Arrays.asList(new String[] { "answer1" }), 0, mTags);
			fail("Constructor must be called with more than one answer");
		} catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCreateQuestionWithOneAnswer()", e);
        }
	}

	public void testCreateQuestionWithNoTags() {
		try {
		    new QuizQuestion(QUESTION_TEXT, Arrays.asList(LIST_OF_ANSWERS), 1, new TreeSet<String>());
			fail("Set of tags can't be empty");
		} catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCreateQuestionWithNoTags()", e);
        }
	}

	public void testCreateQuestionWithNegativeSolutionIndex() {
		try {
			new QuizQuestion(QUESTION_TEXT, Arrays.asList(LIST_OF_ANSWERS), -1, mTags);
			fail("Constructor can't accept a negative solutionIndex");
		} catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCreateQuestionWithNegativeSolutionIndex()", e);
        }
	}

	public void testCreateQuestionWithOutOfBoundsSolutionIndex() {
		try {
			new QuizQuestion(QUESTION_TEXT, Arrays.asList(LIST_OF_ANSWERS),
			        LIST_OF_ANSWERS.length + 1, mTags);
			fail("solutionIndex must be between 0 and answers.length-1");
		} catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCreateQuestionWithOutOfBoundsSolutionIndex()", e);
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
		assertTrue(Arrays.asList(LIST_OF_ANSWERS)
				.equals(mQuestion.getAnswers()));
	}

	public void testGetTagsCopy() {
		mQuestion.getTags().add("Bug!");
		assertEquals("getTags getter must return a copy of the set", mTags,
				mQuestion.getTags());
	}

	public void testGetTags() {
		assertEquals(mTags, mQuestion.getTags());
	}

    public void testToString() {
        testCreateQuestionWithOneAnswer();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("question", mQuestion.getQuestion());
                jsonObject.put("answers", new JSONArray(mQuestion.getAnswers()));
            jsonObject.put("solutionIndex", mQuestion.getSolutionIndex());
            jsonObject.put("tags", new JSONArray(mQuestion.getTags()));
            jsonObject.put("owner", mQuestion.getOwner());
            jsonObject.put("id", mQuestion.getId());
            assertTrue(mQuestion.toString().equals(jsonObject.toString()));
        } catch (JSONException e) {
            Log.v(LOG_TAG, "JSONException in testToString()", e);
            fail("Unexpected JSONException");
        }
    }
    
    private void editQuestion(String question) {
        try {
            Field mQuestionField = QuizQuestion.class.getDeclaredField("mQuestion");
            mQuestionField.setAccessible(true);
            mQuestionField.set(mQuestion, question);
        } catch (Exception e) {
            Log.v(LOG_TAG, "Exception in editQuestion()", e);
            fail("Java reflexion error");
        }
    }
    
    private void editAnswer(List<String> answers) {
        try {
            Field mAnswersField = QuizQuestion.class.getDeclaredField("mAnswers");
            mAnswersField.setAccessible(true);
            mAnswersField.set(mQuestion, answers);
           
        } catch (Exception e) {
            Log.v(LOG_TAG, "Exception in editAnswer()", e);
            fail("Java reflexion error");
        }
    }
    
    private void editSolution(int solutionIndex) {
        try {
            Field mSolutionIndexField = QuizQuestion.class.getDeclaredField("mSolutionIndex");
            mSolutionIndexField.setAccessible(true);
            mSolutionIndexField.set(mQuestion, solutionIndex);
        } catch (Exception e) {
            Log.v(LOG_TAG, "Exception in editSolution()", e);
            fail("Java reflexion error");
        }
    }
    
    private void editTags(Set<String> tags) {
        try {
            Field mTagsField = QuizQuestion.class.getDeclaredField("mTags");
            mTagsField.setAccessible(true);
            mTagsField.set(mQuestion, tags);
        } catch (Exception e) {
            Log.v(LOG_TAG, "Exception in editTags()", e);
            fail("Java reflexion error");
        }
    }
    
//    private void editId(long id) {
//        try {
//            Field mIdField = QuizQuestion.class.getDeclaredField("mId");
//            mIdField.setAccessible(true);
//            mIdField.set(mQuestion, id);
//        } catch (Exception e) {
//            Log.v(LOG_TAG, "Exception in editId()", e);
//            fail("Java reflexion error");
//        }
//    }
//    
//    private void editOwner(String owner) {
//        try {
//            Field mOwnerField = QuizQuestion.class.getDeclaredField("mOwner");
//            mOwnerField.setAccessible(true);
//            mOwnerField.set(mQuestion, owner);
//        } catch (Exception e) {
//            fail("Java reflexion error");
//        }
//    }

    public void testAuditWhenNoErrors() {
        testCreateQuestionWithOneAnswer();
        assertTrue(mQuestion.auditErrors() == 0);
    }

    public void testAuditWhenQuestionNull() {
        testCreateQuestionWithOneAnswer();
        editQuestion(null);
        assertTrue(mQuestion.auditErrors() == 1);
    }

    public void testAuditWhenQuestionContainsOnlySpaces() {
        testCreateQuestionWithOneAnswer();
        editQuestion("   ");
        assertTrue(mQuestion.auditErrors() == 1);
    }

    public void testAuditWhenQuestionToLong() {
        testCreateQuestionWithOneAnswer();
        StringBuilder question = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            question.append("a");
        }
        editQuestion(question.toString());
        assertTrue(mQuestion.auditErrors() == 1);
    }

    public void testAuditWhenOneAnswer() {
        testCreateQuestionWithOneAnswer();
        ArrayList<String> answers = new ArrayList<String>();
        answers.add("question");
        editAnswer(answers);
        editSolution(0);
        assertTrue(mQuestion.auditErrors() == 1);
    }

    public void testAuditWhenToMuchAnswers() {
        testCreateQuestionWithOneAnswer();
        ArrayList<String> answers = new ArrayList<String>();
        for (int i = 0; i < 11; i++) {
            answers.add("question");
        }
        editAnswer(answers);
        assertTrue(mQuestion.auditErrors() == 1);
    }

    public void testAuditWhenTagsNull() {
        testCreateQuestionWithOneAnswer();
        editTags(null);
        if (mQuestion != null) {
            assertTrue(mQuestion.auditErrors() == 1);
        } else {
            fail("Question is null");
        }
    }

    public void testAuditWhenNoTag() {
        testCreateQuestionWithOneAnswer();
        TreeSet<String> tags = new TreeSet<String>();
        editTags(tags);
        editSolution(0);
        assertTrue(mQuestion.auditErrors() == 1);
    }

    public void testAuditWhenToMuchTags() {
        testCreateQuestionWithOneAnswer();
        TreeSet<String> tags = new TreeSet<String>();
        for (int i = 0; i < 21; i++) {
            tags.add("a"+i);
        }
        editTags(tags);
        assertTrue(mQuestion.auditErrors() == 1);
    }

    public void testAuditWhenSolutionIndexToBig() {
        testCreateQuestionWithOneAnswer();
        editSolution(42);
        assertTrue(mQuestion.auditErrors() == 1);
    }

    public void testAuditWhenAllWrong() {
        testCreateQuestionWithOneAnswer();
        ArrayList<String> answers = new ArrayList<String>();
        TreeSet<String> tags = new TreeSet<String>();
        for (int i = 0; i < 11; i++) {
            answers.add("   ");
        }
        for (int i = 0; i < 21; i++) {
            StringBuilder newTag = new StringBuilder();
            for (int j = 0; j < i; j++) {
                newTag.append(" ");
            }
            tags.add(newTag.toString());
        }
        editQuestion(" ");
        editAnswer(answers);
        editTags(tags);
        editSolution(42);
        assertTrue(mQuestion.auditErrors() == 36);
    }
}
