/**
 * 
 */
package epfl.sweng.test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import android.test.AndroidTestCase;
import epfl.sweng.questions.QuizQuestion;

/**
 * @author lseguy
 *
 */
public class QuizQuestionTest extends AndroidTestCase {
    
    private QuizQuestion mQuestion;
    private Set<String> mTags;
    
    public static final int CORRECT_ANSWER_ID = 1;
    public static final String QUESTION_TEXT = "This is the question";
    public static final String[] LIST_OF_ANSWERS = {"Answer 1", "Answer 2"};

    protected void setUp() throws Exception {
        super.setUp();
        
        mTags = new TreeSet<String>();
        mTags.add("Tag1");
        mTags.add("Tag2");
        mQuestion = new QuizQuestion(QUESTION_TEXT,
            LIST_OF_ANSWERS, CORRECT_ANSWER_ID, mTags);
    }

    public void testCreateQuestionWithNullQuestion() {
        try {
            @SuppressWarnings("unused")
            QuizQuestion illegalQuestion = new QuizQuestion(null,
            LIST_OF_ANSWERS, 0, mTags);
            fail("Constructor can't accept null arguments");
        } catch (IllegalArgumentException e) { }
    }

    public void testCreateQuestionWithNullAnswers() {
        try {
            @SuppressWarnings("unused")
            QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
            null, 0, mTags);
            fail("Constructor can't accept null arguments");
        } catch (IllegalArgumentException e) { }
    }

    public void testCreateQuestionWithNullTags() {
        try {
            @SuppressWarnings("unused")
            QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
            LIST_OF_ANSWERS, 0, null);
            fail("Constructor can't accept null arguments");
        } catch (IllegalArgumentException e) { }
    }

    public void testCreateQuestionWithOneAnswer() {
        try {
            @SuppressWarnings("unused")
            QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
            new String[] {"answer1"}, 0, mTags);
            fail("Constructor must be called with more than one answer");
        } catch (IllegalArgumentException e) { }
    }

    public void testCreateQuestionWithNoTags() {
        try {
            @SuppressWarnings("unused")
            QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
                    LIST_OF_ANSWERS, 0, new TreeSet<String>());
            fail("Constructor must be called with at least one tag");
        } catch (IllegalArgumentException e) { }
    }

    public void testCreateQuestionWithNegatifSolutionIndex() {
        try {
            @SuppressWarnings("unused")
            QuizQuestion illegalQuestion = new QuizQuestion(QUESTION_TEXT,
                    LIST_OF_ANSWERS, -1, mTags);
            fail("Constructor can't accept negatif SolutionIndex");
        } catch (IllegalArgumentException e) { }
    }
    
    public void testConstructorCopiesAnswers() {
        String[] notSafeAnswers = {"Ping", "Pong"};
        mQuestion = new QuizQuestion(QUESTION_TEXT, notSafeAnswers, 0, mTags);
        notSafeAnswers[0] = "Bug!";
        assertFalse("Constructor must copy array of answers",
            Arrays.equals(notSafeAnswers, mQuestion.getAnswers()));
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
        mQuestion.getAnswers()[0] = "Bug!";
        assertTrue("getAnswers getter must return a copy of the array",
            Arrays.equals(LIST_OF_ANSWERS, mQuestion.getAnswers()));
    }
    
    public void testGetAnswers() {
        assertTrue(Arrays.equals(LIST_OF_ANSWERS, mQuestion.getAnswers()));
    }
    
    public void testGetTagsCopy() {
        mQuestion.getTags().add("Bug!");
        assertEquals("getTags getter must return a copy of the set",
            mTags, mQuestion.getTags());
    }
    
    public void testGetTags() {
        assertEquals(mTags, mQuestion.getTags());
    }

}
