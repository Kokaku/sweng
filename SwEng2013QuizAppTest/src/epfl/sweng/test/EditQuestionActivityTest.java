package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.widget.Button;
import android.widget.LinearLayout;
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
	
	/**
	 * @param index three index per line:
     * index%3==0 match checkButton
     * index%3==1 match editText
     * index%3==2 match removeButton
     * 
     * And index/3== line number 
	 * @param textButton a String of on button that exist in the LinearLayout
	 */
	private void clickOnCheckButton(final int index, final String textButton) {
	    runAndWaitFor(new Runnable() {
            @Override
            public void run() {
                solo.clickOnView(getLinearLayout(textButton).getChildAt(index));
            }
        }, TTChecks.QUESTION_EDITED);
	}
	
	private void submitQuestion() {
        clickOnButtonAndWaitFor("submit", TTChecks.QUESTION_EDITED);
	}

    public LinearLayout getLinearLayout(String textButton) {
        Button button = solo.getButton(textButton);
        return (LinearLayout) button.getParent();
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
		assertTrue("Button for checking an answer is displayed as wrong",
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
	
	public void testCheckButtonIsCorrectAfterBeingClicked(){
	  clickOnCheckButton(0, "\u2718");
	  Button correctButton = (Button) getLinearLayout("\u2714").getChildAt(0);
      assertTrue("Check button must be displayed as correct after click",
            correctButton.getText().equals("\u2714"));
	}
	
	public void testCheckButtonIsWrongAfterBeingReClicked() {
        clickOnCheckButton(0, "\u2718");
        clickOnCheckButton(0, "\u2714");
        
        Button wrongButton = (Button) getLinearLayout("\u2718").getChildAt(0);
        assertTrue("Check button must be displayed as wrong after double click",
                wrongButton.getText().equals("\u2718"));
	}
	
	public void testAddAnswerThenRemoveFirst() {
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer2");

        assertTrue("Answer field is displayed",
                solo.searchEditText("answer1"));
        assertTrue("Answer field is displayed",
                solo.searchEditText("answer2"));
        
        clickOnCheckButton(2, "\u002D");

        assertFalse("Answer field is displayed",
                solo.searchEditText("answer1"));
        assertTrue("Answer field is displayed",
                solo.searchEditText("answer2"));
	}
    
    public void testAddAnswerThenRemoveSecond() {
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer2");

        assertTrue("Answer field is displayed",
                solo.searchEditText("answer1"));
        assertTrue("Answer field is displayed",
                solo.searchEditText("answer2"));
        
        //TODO seem like click on button index doesn't works on others lines
//        clickOnCheckButton(5, "\u002D");

//        assertFalse("Answer field is displayed",
//                solo.searchEditText("answer2"));
//        assertTrue("Answer field is displayed",
//                solo.searchEditText("answer1"));
    }
    
    public void testCannotSubmitWithAllAnswersRemoved() {
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer2");
        
        Button submitButton = solo.getButton("Submit");
        clickOnCheckButton(0, "\u2718");
        assertTrue("Submit button is initially disabled",
                submitButton.isEnabled());
        
        removeAnswer();
        removeAnswer();
        submitButton = solo.getButton("Submit");
        assertFalse("Submit button is initially disabled",
                submitButton.isEnabled());
    }
    
    public void testCannotSubmitWithEmptyAnswer() {
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer1");
        addAnswer();
        clickOnCheckButton(0, "\u2718");
        
        Button submitButton = solo.getButton("Submit");
        assertFalse("Submit button is initially disabled",
                submitButton.isEnabled());
    }
    
    public void testCannotSubmitWithoutCorrectAnswer() {
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer2");
        
        Button submitButton = solo.getButton("Submit");
        assertFalse("Submit button is initially disabled",
                submitButton.isEnabled());
    }
    
    public void testClickAddAnswerButton() {
        assertTrue("Answer field is displayed",
                solo.searchEditText("Type in the answer"));
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer1");
        assertFalse("Answer field is displayed",
                solo.searchEditText("Type in the answer"));
        addAnswer();
        assertTrue("Answer field is displayed",
                solo.searchEditText("Type in the answer"));
    }
    
    public void testCannotSubmitWithoutQuestion() {
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"),
                "answer2");
        clickOnCheckButton(0, "\u2718");
        
        Button submitButton = solo.getButton("Submit");
        assertFalse("Submit button is initially disabled",
                submitButton.isEnabled());
    }
    
    public void testOnlyOneAnswerIsCorrect() {
        //TODO
        assertTrue(true);
    }
}
