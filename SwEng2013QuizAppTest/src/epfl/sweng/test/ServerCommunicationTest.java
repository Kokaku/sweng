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

	private String mQuestionText = "How many rings the Olympic flag Five has?";
	private String[] mAnswers = { "One", "Six", "Five" };
	private Set<String> mTags = new TreeSet<String>();
	private int mSolutionIndex = 2;

	protected void setUp() throws Exception {
		super.setUp();
		mockHttpClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mockHttpClient);
		mockHttpClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				HttpStatus.SC_OK, "", "application/json");

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

	private void pushCorrectQuestion() {
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
		assertTrue("Question is fetched", mQuestion != null);
	}

	public void testQuestionTextCorrectlyFetched() {
		pushCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Question text is correctly fetched",
				mQuestionText.equals(mQuestion.getQuestion()));
	}

	public void testQuestionTagsCorrectlyFetched() {
		pushCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		mTags.add("Tag1");
		mTags.add("Tag2");

		assertTrue("Question tags are correctly fetced",
				mTags.equals(mQuestion.getTags()));
	}

	public void testQuestionAnswersCorrectlyFetched() {
		pushCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Question answers are correctly fetched",
				Arrays.equals(mAnswers, mQuestion.getAnswers()));
	}

	public void testSolutionIndexCorrectlyFetched() {
		pushCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Solution index is correctly fetched",
				mSolutionIndex == mQuestion.getSolutionIndex());
	}

	public void testGetRandomIncorrectQuestion() {
		pushIncorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Incorrect question is not fetched", null == mQuestion);

	}

	public void testSendQuestion() {
		mQuestion = new QuizQuestion(mQuestionText, mAnswers, mSolutionIndex,
				mTags);
		boolean questionSent = ServerCommunication.send(mQuestion);
		assertTrue("Valid question is sent", questionSent);
		//mockHttpClient.clearCannedResponses();
	}
	
	public void testQuestionWellRecieved() {
		mQuestion = new QuizQuestion(mQuestionText, mAnswers, mSolutionIndex,
				mTags);
		boolean questionSent = ServerCommunication.send(mQuestion);
		//mQuestion = mockHttpClient.getLastRequest();
		assertTrue("Valid question is sent", questionSent);
		//mockHttpClient.clearCannedResponses();
	}
}
