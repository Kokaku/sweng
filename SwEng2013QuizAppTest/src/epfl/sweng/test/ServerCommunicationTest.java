package epfl.sweng.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.ServerCommunication;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author Zhivka Gucevska
 * 
 */
public class ServerCommunicationTest extends QuizActivityTestCase<MainActivity> {

    private final MockHttpClient mockHttpClient = new MockHttpClient();
    private QuizQuestion mQuestion;

    private String mQuestionText = "How many rings the Olympic flag Five has?";
    private String[] mAnswers = {"One", "Six", "Five"};
    private Set<String> mTags = new TreeSet<String>();
    private int mSolutionIndex = 2;

    public ServerCommunicationTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
        UserCredentials.INSTANCE.setState(AuthenticationState.AUTHENTICATED);
        UserCredentials.INSTANCE.saveUserCredentials("test");

        SwengHttpClientFactory.setInstance(mockHttpClient);
        mockHttpClient.pushCannedResponse(
                "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
                HttpStatus.SC_OK, "", "application/json");

    }

    @Override
    protected void tearDown() throws Exception {
        mockHttpClient.clearCannedResponses();
        super.tearDown();
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
        mockHttpClient
                .pushCannedResponse(
                        "POST [^/]+",
                        HttpStatus.SC_CREATED,
                        "{\"question\": \"How many rings the Olympic flag Five has?\","
                                + " \"answers\": [\"One\", \"Six\", \"Five\"], \"owner\": \"sweng\","
                                + " \"solutionIndex\": 2, \"tags\": [\"Tag1\", \"Tag2\"],  \"id\": \"1\" }",
                        null);

    }

    private void pushCannedAnswerForBADPostRequest() {
        mockHttpClient.clearCannedResponses();
        mockHttpClient.pushCannedResponse("POST [^/]+",
                HttpStatus.SC_BAD_REQUEST, null, null);

    }

    public void testGetRandomQuestion() throws ServerCommunicationException,
            NotLoggedInException {
        pushCannedAnswerForCorrectQuestion();
        mQuestion = ServerCommunication.INSTANCE.getRandomQuestion();
        assertTrue("Question is fetched", mQuestion != null);
    }

    public void testQuestionTextCorrectlyFetched()
        throws ServerCommunicationException, NotLoggedInException {

        pushCannedAnswerForCorrectQuestion();
        mQuestion = ServerCommunication.INSTANCE.getRandomQuestion();

        assertTrue("Question text is correctly fetched",
                mQuestionText.equals(mQuestion.getQuestion()));
    }

    public void testQuestionTagsCorrectlyFetched()
        throws ServerCommunicationException, NotLoggedInException {

        pushCannedAnswerForCorrectQuestion();
        mQuestion = ServerCommunication.INSTANCE.getRandomQuestion();

        mTags.add("Tag1");
        mTags.add("Tag2");

        assertTrue("Question tags are correctly fetced",
                mTags.equals(mQuestion.getTags()));
    }

    public void testQuestionAnswersCorrectlyFetched()
        throws ServerCommunicationException, NotLoggedInException {

        pushCannedAnswerForCorrectQuestion();
        mQuestion = ServerCommunication.INSTANCE.getRandomQuestion();

        assertTrue("Question answers are correctly fetched",
                Arrays.equals(mAnswers, mQuestion.getAnswers().toArray()));
    }

    public void testSolutionIndexCorrectlyFetched()
        throws ServerCommunicationException, NotLoggedInException {

        pushCannedAnswerForCorrectQuestion();
        mQuestion = ServerCommunication.INSTANCE.getRandomQuestion();

        assertTrue("Solution index is correctly fetched",
                mSolutionIndex == mQuestion.getSolutionIndex());
    }

    public void testGetRandomIncorrectQuestion()
        throws ServerCommunicationException, NotLoggedInException {

        pushCannedAnswerForIncorrectQuestion();
        try {
            mQuestion = ServerCommunication.INSTANCE.getRandomQuestion();
            fail("Incorrect question is not fetched");
        } catch (IllegalArgumentException e) {
        }

    }

    public void testGetRandomQuestionWithBadJSONObject()
        throws ServerCommunicationException, NotLoggedInException {

        pushCannedAnswerWithInvalidJSONObject();
        try {
            mQuestion = ServerCommunication.INSTANCE.getRandomQuestion();
            fail("Question should not be created when invalid JSONObject "
                    + "returned by server");
        } catch (ServerCommunicationException e) {
        }
    }

    public void testGetRandomQuestionWithMissingJSONFielAnswers()
        throws ServerCommunicationException, NotLoggedInException {
        pushCannedAnswerWithMissingJSONFieldAnswers();
        try {
            mQuestion = ServerCommunication.INSTANCE.getRandomQuestion();
            fail("Question should not be parsed when missing fields in"
                    + "the JSONObject retrurned by server");
        } catch (ServerCommunicationException e) {
        }
    }

    public void testSendQuestion() throws ServerCommunicationException {
        mTags.add("tag1");
        pushCannedAnswerForOKPostRequest();
        mQuestion = new QuizQuestion(mQuestionText, new ArrayList<String>(
                Arrays.asList(mAnswers)), mSolutionIndex, mTags);
        try {
            ServerCommunication.INSTANCE.send(mQuestion);
        } catch (ServerCommunicationException e) {
            fail("Valid question is sent");
        }
        mockHttpClient.clearCannedResponses();
    }

    public void testQuestionWellRecieved() throws ServerCommunicationException {
        mTags.add("tag1");
        mQuestion = new QuizQuestion(mQuestionText, new ArrayList<String>(
                Arrays.asList(mAnswers)), mSolutionIndex, mTags);

        pushCannedAnswerForOKPostRequest();
        ServerCommunication.INSTANCE.send(mQuestion);
        QuizQuestion questionOnServer = null;
        try {
            questionOnServer = new QuizQuestion(new JSONObject(
                    mockHttpClient.getLastPostRequestContent()).toString());
        } catch (JSONException e) {
            System.out.println("prout1");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("prout2");
            e.printStackTrace();
        }

        assertTrue("Question text is well recieved", mQuestion.getQuestion()
                .equals(questionOnServer.getQuestion()));
        assertTrue("Question answers are well recieved", Arrays.equals(
                mQuestion.getAnswers().toArray(), questionOnServer.getAnswers()
                        .toArray()));
        assertTrue("Question tags are well recieved", mQuestion.getTags()
                .equals(questionOnServer.getTags()));
        assertTrue("Question solution index is well recieved",
                mQuestion.getSolutionIndex() == questionOnServer
                        .getSolutionIndex());

        mockHttpClient.clearCannedResponses();
    }

    public void testQuestionNotRecieved() throws ServerCommunicationException {
        pushCannedAnswerForBADPostRequest();
        mTags.add("tag1");
        mQuestion = new QuizQuestion(mQuestionText, new ArrayList<String>(
                Arrays.asList(mAnswers)), mSolutionIndex, mTags);
        try {
            ServerCommunication.INSTANCE.send(mQuestion);
            fail("The server did not accept the request");
        } catch (ServerCommunicationException e) {

        }
    }
}
