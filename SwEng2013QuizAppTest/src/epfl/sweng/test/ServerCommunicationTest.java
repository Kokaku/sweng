/**
 * 
 */
package epfl.sweng.test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpStatus;

import android.test.AndroidTestCase;
import epfl.sweng.questions.QuizQuestion;
import epfl.sweng.servercomm.ServerCommunication;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;

/**
 * @author Zhivka Gucevska
 * 
 */
public class ServerCommunicationTest extends AndroidTestCase {

	private MockHttpClient mockHttpClient;
	private QuizQuestion mQuestion;
	

	protected void setUp() throws Exception {
		super.setUp();
		mockHttpClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mockHttpClient);
		mockHttpClient
		.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				HttpStatus.SC_OK,
				"",
				"application/json");
		
	}

	private void pushIncorrectQuestion() {
		mockHttpClient.popCannedResponse();
		mockHttpClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"How many rings the Olympic flag Five has?\","
								+ " \"answers\": [], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 2, \"tags\": [\"Tag1\", \"Tag2\"], \"Tag3\": \"Tag4\" }",
						"application/json");
	}
	
	private void pushCorrectQuestion(){
		mockHttpClient.popCannedResponse();
		mockHttpClient
		.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				HttpStatus.SC_OK,
				"{\"question\": \"How many rings the Olympic flag Five has?\","
						+ " \"answers\": [\"One\", \"Six\", \"Five\"], \"owner\": \"sweng\","
						+ " \"solutionIndex\": 2, \"tags\": [\"Tag1\", \"Tag2\"], \"Tag3\": \"Tag4\" }",
				"application/json");
	}

	public void testGetRandomQuestion() {
		pushCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();
		assertFalse("Question is fetched", mQuestion == null);
	}

	public void testQuestionTextCorrectlyFetched() {
		pushCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();
		
		assertTrue("Question text is correctly fetched",
				"How many rings the Olympic flag Five has?".equals(mQuestion
						.getQuestion()));
	}

	public void testQuestionTagsCorrectlyFetched() {
		pushCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		Set<String> tags = new TreeSet<String>();
		tags.add("Tag1");
		tags.add("Tag2");

		assertTrue("Question tags are correctly fetced",
				tags.equals(mQuestion.getTags()));
	}

	public void testQuestionAnswersCorrectlyFetched() {
		pushCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();


		String[] answers = { "One", "Six", "Five" };

		assertTrue("Question answers are correctly fetched",
				Arrays.equals(answers, mQuestion.getAnswers()));
	}

	public void testSolutionIndexCorrectlyFetched() {
		pushCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		int correctAnswer = 2;

		assertTrue("Solution index is correctly fetched",
				correctAnswer == mQuestion.getSolutionIndex());
	}

	public void testGetRandomIncorrectQuestion() {
		pushIncorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();
		
		assertTrue("Incorrect question is not fetched", null == mQuestion);

	}

}
