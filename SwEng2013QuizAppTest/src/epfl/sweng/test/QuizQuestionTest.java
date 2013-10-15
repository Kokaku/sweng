/**
 * 
 */
package epfl.sweng.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.test.AndroidTestCase;
import epfl.sweng.questions.QuizQuestion;

/**
 * @author lseguy
 *
 */
public class QuizQuestionTest extends AndroidTestCase {
    
    private QuizQuestion question;
    private Set<String> tags;
    
    public static final int CORRECT_ANSWER_ID = 1;
    public static final String QUESTION_TEXT = "This is the question";
    public static final String[] LIST_OF_ANSWERS = {"Answer 1", "Answer 2"};

    protected void setUp() throws Exception {
        super.setUp();
        
        tags = new HashSet<String>();
        tags.add("Tag1");
        tags.add("Tag2");
        question = new QuizQuestion(QUESTION_TEXT,
            LIST_OF_ANSWERS, CORRECT_ANSWER_ID, tags);
    }

    public void testCreateQuestionWithNull() {
        try {
            @SuppressWarnings("unused")
            QuizQuestion illegalQuestion = new QuizQuestion(null, null, 0,
                null);
            fail("Constructor can't accept null arguments");
        }
        catch (IllegalArgumentException e) {
            
        }
    }
    
    public void testCheckCorrectAnswerIsTrue() {
        assertTrue(question.isSolutionCorrect(CORRECT_ANSWER_ID));
    }
    
    public void testCheckCorrectAnswerIsFalse() {
        for (int i = 0; i < LIST_OF_ANSWERS.length; ++i) {
            if (i != CORRECT_ANSWER_ID) {
                assertFalse(question.isSolutionCorrect(i));
            }
        }
    }
    
    public void testGetQuestionText() {
        assertEquals(QUESTION_TEXT, question.getQuestion());
    }
    
    public void testGetSolutionIndex() {
        assertEquals(CORRECT_ANSWER_ID, question.getSolutionIndex());
    }
    
    public void testGetAnswersCopy() {
        assertNotSame("getAnswers getter must return a copy of the array",
            LIST_OF_ANSWERS, question.getAnswers());
    }
    
    public void testGetAnswers() {
        assertTrue(Arrays.equals(LIST_OF_ANSWERS, question.getAnswers()));
    }
    
    public void testGetTagsCopy() {
        assertNotSame("getTags getter must return a copy of the set",
            tags, question.getTags());
    }
    
    public void testGetTags() {
        assertEquals(tags, question.getTags());
    }

}
