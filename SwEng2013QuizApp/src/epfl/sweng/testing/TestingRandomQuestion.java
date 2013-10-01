/**
 * 
 */
package epfl.sweng.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import epfl.sweng.QuizQuestion;
import epfl.sweng.RandomQuestion;
import epfl.sweng.ServerCommunicationException;

/**
 * @author kokaku
 *
 */
public class TestingRandomQuestion {
	private QuizQuestion quizQuestion;
    private int correctAnswerIndex = -1;
	

	@Test
    public void test() {
		HttpGet request = new HttpGet("https://sweng-quiz.appspot.com/quizquestions/" + "random");
		assertNotNull(request);
	}
	
	@Test
    public void init() {
		try {
			quizQuestion = new RandomQuestion();
		} catch (ServerCommunicationException e) {
			e.printStackTrace();
		}
	}
	
	@Test
    public void testQuestionNull() {
		assertNotNull(quizQuestion);
	    assertNotNull(quizQuestion.getQuestion());
	    assertNotNull(quizQuestion.getAnswers());
	    assertNotNull(quizQuestion.getTags());
	}
	
	@Test
    public void testQuestionHasAtLeastTwoAnswers() {
        assertTrue(quizQuestion.getAnswers().length >= 2);
	}
	
	@Test
    public void testQuestionIdIsPositiveOrDefault() {
        assertTrue(quizQuestion.getQuestionId() > 0 || quizQuestion.getQuestionId() == QuizQuestion.DEFAULT_ID);
	}
	
	@Test
    public void testQuestionHasExactlyOneAnswer() {
		int totalCorrectAnswers = 0;
        for (int i = 0; i < quizQuestion.getAnswers().length; i++) {
			if (quizQuestion.isSolutionCorrect(i)) {
				totalCorrectAnswers++;
				correctAnswerIndex = i;
			}
		}
        assertEquals(totalCorrectAnswers, 1);
	}
	
	@Test
    public void testQuestionManualCheck() {
        System.out.println("id : "+quizQuestion.getQuestionId());
        System.out.println("question : "+quizQuestion.getQuestion());
        System.out.println("solution : "+correctAnswerIndex);
        
        System.out.println("answers :");
        for (String answer : quizQuestion.getAnswers()) {
			System.out.println("\t"+answer);
		} 
        System.out.println("tags :");
        for (String tag : quizQuestion.getTags()) {
			System.out.println("\t"+tag);
		}
	}
}
