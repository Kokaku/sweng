package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.widget.Button;
import android.widget.ListView;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author MathieuMonney
 * 
 */
public class EditQuestionActivityTest extends
		QuizActivityTestCase<EditQuestionActivity> {

	private MockHttpClient mockHttpClient;

	public EditQuestionActivityTest() {
		super(EditQuestionActivity.class);
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

		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
	}

	private void addAnswer() {
	    clickOnButtonAndWaitFor("\u002B", TTChecks.QUESTION_EDITED);
	}
	
	private void removeAnswer() {
        clickOnButtonAndWaitFor("\u002D", TTChecks.QUESTION_EDITED);
	}
	
	private void checkAsCorrect(final int index) {
	}
	
	private void submitQuestion() {
        clickOnButtonAndWaitFor("submit", TTChecks.QUESTION_EDITED);
	}
	
	
	public void testQuestionFieldDisplayed() {
		assertTrue("Question field is displayed",
				solo.searchEditText("Type in the question's text body"));
	}

	public void testTagsFieldDisplayed() {
		assertTrue("Tags field is displayed",
				solo.searchEditText("Type in the question's tags"));
	}

	public void testAnswerFieldIsDisplayed() {
		assertTrue("Answer field is displayed",
				solo.searchEditText("Type in the answer"));
	}

	public void testAddAnswerButtonDisplayed() {
		assertTrue("Button for adding answers is displayed ",
				solo.searchButton("\u002B"));
	}

	public void testRemoveAnswerButtonDisplayed() {
		assertTrue("Button for removing answers is displayed ",
				solo.searchButton("\u002D"));
	}

	public void testCheckAnswerButtonDisplayed() {
		assertTrue("Button for checking an answer is desplayed as wrong",
				solo.searchButton("\u2718"));
	}

	public void testSubmitButtonDisplayed() {
		assertTrue("Submit button is displayed", solo.searchButton("Submit"));
	}

	public void testSubmitButtonIsInitiallyDisabled() {
		Button submitButton = solo.getButton("Submit");
		assertFalse("Submit button is initially disabled",
				submitButton.isEnabled());
	}
	
}
