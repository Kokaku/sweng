package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

        mockHttpClient
                .pushCannedResponse(
                        "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
                        HttpStatus.SC_OK,
                        "{\"question\": \"What is the answer to life, the universe, and everything?\","
                                + " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
                                + " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
                        "application/json");

        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
    }
    
    private void selectRightAnswer() {
        clickOnTextViewAndWaitFor("Forty-two", TTChecks.ANSWER_SELECTED);
    }
    
    private void selectWrongAnswer() {
        clickOnTextViewAndWaitFor("Twenty-seven", TTChecks.ANSWER_SELECTED);
    }

    public void testQuestionCorrectlyDisplayed() {
        assertTrue(
                "Question is displayed",
                solo.searchText("What is the answer to life, the universe, and everything?"));
        assertTrue("Correct answer is displayed", solo.searchText("Forty-two"));
        assertTrue("Incorrect answer is displayed",
                solo.searchText("Twenty-seven"));
        assertTrue("Button Next question is displayed",
                solo.searchButton("Next question"));
    }

    public void testNextQuestionButtonInitiallyDisabled() {
        Button nextQuestionButton = solo.getButton("Next question");
        assertFalse("Next question button is disabled",
                nextQuestionButton.isEnabled());
    }

    public void testAnswersInitiallyClickable() {
        TextView correctAnswer = solo.getText("Forty-two");
        ListView listView = (ListView) correctAnswer.getParent();
        assertTrue("Answers are clickable", listView.isEnabled());
    }

    public void testWrongAnswerDisplayed() {
        selectWrongAnswer();
        assertTrue("Wrong answer dialog is displayed",
                solo.searchText("\u2718"));
    }

    public void testRightAnswerDisplayed() {
        selectRightAnswer();
        assertTrue("Right answer dialog is displayed",
                solo.searchText("\u2714"));
    }

    public void testNextQuestionButtonEnabledAfterRightAnswer() {
        selectRightAnswer();
        Button nextQuestionButton = solo.getButton("Next question");
        assertTrue("Next question button is disabled",
                nextQuestionButton.isEnabled());
    }

    public void testAnswersNotClickableAfterRightAnswer() {
        selectRightAnswer();
        TextView correctAnswer = solo.getText("Forty-two");
        ListView listView = (ListView) correctAnswer.getParent();
        assertFalse("Answers are not clickable", listView.isEnabled());
    }

    public void testAnswersClickableAfterWrongAnswer() {
        selectWrongAnswer();
        TextView correctAnswer = solo.getText("Forty-two");
        ListView listView = (ListView) correctAnswer.getParent();
        assertTrue("Answers are clickable", listView.isEnabled());
    }

}