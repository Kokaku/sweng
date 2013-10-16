package epfl.sweng.test;

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
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
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

	public void testAddAnswerButton() {
		assertTrue("Button for adding answers is displayed ",
				solo.searchButton("\u002B"));
	}
}
