package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.widget.Button;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author lseguy
 * 
 */

public class ShowQuestionsActivityTest extends
        QuizActivityTestCase<ShowQuestionsActivity> {

    private MockHttpClient mockHttpClient;

    public ShowQuestionsActivityTest() {
        super(ShowQuestionsActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mockHttpClient = new MockHttpClient();
        SwengHttpClientFactory.setInstance(mockHttpClient);
    }
    
    public void testShowQuestion() {
        mockHttpClient
                .pushCannedResponse(
                        "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
                        HttpStatus.SC_OK,
                        "{\"question\": \"What is the answer to life, the universe, and everything?\","
                                + " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
                                + " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
                        "application/json");
        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
        assertTrue(
                "Question is displayed",
                solo.searchText("What is the answer to life, the universe, and everything?"));
        assertTrue("Correct answer is displayed", solo.searchText("Forty-two"));
        assertTrue("Incorrect answer is displayed",
                solo.searchText("Twenty-seven"));
        Button nextQuestionButton = solo.getButton("Next question");
        assertFalse("Next question button is disabled",
                nextQuestionButton.isEnabled());
    }
}