/**
 * 
 */
package epfl.sweng.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import epfl.sweng.questions.QuizQuestion;
import epfl.sweng.servercomm.ServerCommunication;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.utils.JSONUtilities;

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

	private void pushCannedAnswerForIncorrectQuestion() {
		mockHttpClient.clearCannedResponses();
		mockHttpClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"How many rings the Olympic flag Five has?\","
								+ " \"answers\": [], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 2, \"tags\": [\"Tag1\", \"Tag2\"],  \"id\": \"1\" }",
						"application/json");
	}

	private void pushCannedAnswerForCorrectQuestion() {
		mockHttpClient.clearCannedResponses();
		mockHttpClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"How many rings the Olympic flag Five has?\","
								+ " \"answers\": [\"One\", \"Six\", \"Five\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 2, \"tags\": [\"Tag1\", \"Tag2\"],  \"id\": \"1\" }",
						"application/json");
	}

	private void pushCannedAnswerWithInvalidJSONObject() {
		mockHttpClient.clearCannedResponses();
		mockHttpClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\" \"How many rings the Olympic flag Five has?\","
								+ " ], \"owner\" \"sweng\","
								+ " \"solutionIndex\" 2, \"tags\" \"Tag2\"],  \"i \"1\" }",
						"application/json");
	}

	private void pushCannedAnswerWithMissingJSONFieldAnswers() {
		mockHttpClient.clearCannedResponses();
		mockHttpClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"How many rings the Olympic flag Five has?\","
								+ " \"an\": [\"One\", \"Six\", \"Five\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 2, \"tags\": [\"Tag1\", \"Tag2\"],  \"id\": \"1\" }",
						"application/json");
	}

	private void pushCannedAnswerForOKPostRequest() {
		mockHttpClient.clearCannedResponses();
		mockHttpClient.pushCannedResponse("POST [^/]+", HttpStatus.SC_OK, "correctly sent",
				null);

	}

	private void pushCannedAnswerForBADPostRequest() {
		mockHttpClient.clearCannedResponses();
		mockHttpClient.pushCannedResponse("POST [^/]+",
				HttpStatus.SC_BAD_REQUEST, null, null);

	}

	public void testGetRandomQuestion() {
		pushCannedAnswerForCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();
		assertTrue("Question is fetched", mQuestion != null);
	}

	public void testQuestionTextCorrectlyFetched() {
		pushCannedAnswerForCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Question text is correctly fetched",
				mQuestionText.equals(mQuestion.getQuestion()));
	}

	public void testQuestionTagsCorrectlyFetched() {
		pushCannedAnswerForCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		mTags.add("Tag1");
		mTags.add("Tag2");

		assertTrue("Question tags are correctly fetced",
				mTags.equals(mQuestion.getTags()));
	}

	public void testQuestionAnswersCorrectlyFetched() {
		pushCannedAnswerForCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Question answers are correctly fetched",
				Arrays.equals(mAnswers, mQuestion.getAnswers()));
	}

	public void testSolutionIndexCorrectlyFetched() {
		pushCannedAnswerForCorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Solution index is correctly fetched",
				mSolutionIndex == mQuestion.getSolutionIndex());
	}

	public void testGetRandomIncorrectQuestion() {
		pushCannedAnswerForIncorrectQuestion();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Incorrect question is not fetched", null == mQuestion);

	}

	public void testGetRandomQuestionWithBadJSONObject() {
		pushCannedAnswerWithInvalidJSONObject();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Question should not be created when invalid JSONObject "
				+ "returned by server", null == mQuestion);
	}

	public void testGetRandomQuestionWithMissingJSONFielAnswers() {
		pushCannedAnswerWithMissingJSONFieldAnswers();
		mQuestion = ServerCommunication.getRandomQuestion();

		assertTrue("Question should not be parsed when missing fields in"
				+ "the JSONObject retrurned by server", null == mQuestion);
	}

	public void testSendQuestion() {
		mTags.add("tag1");
		pushCannedAnswerForOKPostRequest();
		mQuestion = new QuizQuestion(mQuestionText, mAnswers, mSolutionIndex,
				mTags);
		boolean questionSent = ServerCommunication.send(mQuestion);
		assertTrue("Valid question is sent", questionSent);
		mockHttpClient.clearCannedResponses();
	}

	public void testQuestionWellRecieved() {
		mTags.add("tag1");
		mQuestion = new QuizQuestion(mQuestionText, mAnswers, mSolutionIndex,
				mTags);

		pushCannedAnswerForOKPostRequest();
		ServerCommunication.send(mQuestion);
		QuizQuestion questionOnServer = null;
		try {
			JSONObject json = new JSONObject(
					mockHttpClient.getLastPostRequestContent());

			questionOnServer = new QuizQuestion(json.getString("question"),
					JSONUtilities.parseAnswers(json),
					json.getInt("solutionIndex"), JSONUtilities.parseTags(json));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		assertTrue("Question text is well recieved", mQuestion.getQuestion()
				.equals(questionOnServer.getQuestion()));
		assertTrue(
				"Question answers are well recieved",
				Arrays.equals(mQuestion.getAnswers(),
						questionOnServer.getAnswers()));
		assertTrue("Question tags are well recieved", mQuestion.getTags()
				.equals(questionOnServer.getTags()));
		assertTrue("Question solution index is well recieved",
				mQuestion.getSolutionIndex() == questionOnServer
						.getSolutionIndex());

		mockHttpClient.clearCannedResponses();
	}

	public void testQuestionNotRecieved() {
		pushCannedAnswerForBADPostRequest();
		mTags.add("tag1");
		mQuestion = new QuizQuestion(mQuestionText, mAnswers, mSolutionIndex,
				mTags);
		boolean questionSent = ServerCommunication.send(mQuestion);
		assertFalse("The server did not accept the request", questionSent);
	}

}
