package epfl.sweng.test;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.http.HttpStatus;
import org.json.JSONException;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.patterns.Proxy;
import epfl.sweng.patterns.Proxy.ConnectionState;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.utils.JSONUtilities;

/**
 * @author MathieuMonney
 * 
 */
public class EditQuestionActivityTest extends
        QuizActivityTestCase<EditQuestionActivity> {

    private final static int SLEEP_TIME = 200;
    private QuizQuestion question = null;
    private MockHttpClient mockHttpClient;

    public EditQuestionActivityTest() {
        super(EditQuestionActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        question = new QuizQuestion("This is my question", Arrays.asList(
                "answer1", "answer2", "answer3"), 0, new TreeSet<String>(
                Arrays.asList("tag1", "tag2")), 0, "Moi");
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

        mockHttpClient.pushCannedResponse("POST [^/]+",
                HttpStatus.SC_BAD_REQUEST, null, null);

        getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
    }

    private void addAnswer() {
        clickOnTextViewAndWaitFor("\u002B", TTChecks.QUESTION_EDITED);
    }

    private void removeAnswer() {
        clickOnTextViewAndWaitFor("\u002D", TTChecks.QUESTION_EDITED);
    }

    /**
     * @param index
     *            three index per line: 0 match checkButton 1 match editText 2
     *            match removeButton
     * @param textButton
     *            a String of on button that exist in the LinearLayout
     */
    private void clickOnViewInListView(final int index, final String textButton) {
        runAndWaitFor(new Runnable() {
            @Override
            public void run() {
                solo.clickOnView(getLinearLayout(textButton).getChildAt(index));
            }
        }, TTChecks.QUESTION_EDITED);
    }

    private void submitQuestion() {
        clickOnTextViewAndWaitFor("Submit", TTChecks.NEW_QUESTION_SUBMITTED);
    }

    private LinearLayout getLinearLayout(String textDisplayed) {
        TextView view = solo.getText(textDisplayed);
        return (LinearLayout) view.getParent();
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

    public void testCheckButtonIsCorrectAfterBeingClicked() {
        clickOnViewInListView(0, "\u2718");
        Button correctButton = (Button) getLinearLayout("\u2714").getChildAt(0);
        assertTrue("Check button must be displayed as correct after click",
                correctButton.getText().equals("\u2714"));
    }

    public void testCheckButtonIsWrongAfterBeingReClicked() {
        clickOnViewInListView(0, "\u2718");
        clickOnViewInListView(0, "\u2714");

        Button wrongButton = (Button) getLinearLayout("\u2718").getChildAt(0);
        assertTrue(
                "Check button must be displayed as wrong after double click",
                wrongButton.getText().equals("\u2718"));
    }

    public void testAddAnswerThenRemoveFirst() {
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");

        assertTrue("Answer field is displayed", solo.searchEditText("answer1"));
        assertTrue("Answer field is displayed", solo.searchEditText("answer2"));

        clickOnViewInListView(2, "answer1");

        assertFalse("Answer field is displayed", solo.searchEditText("answer1"));
        assertTrue("Answer field is displayed", solo.searchEditText("answer2"));
    }

    public void testAddAnswerThenRemoveSecond() {
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");

        assertTrue("Answer field is displayed", solo.searchEditText("answer1"));
        assertTrue("Answer field is displayed", solo.searchEditText("answer2"));

        clickOnViewInListView(2, "answer2");

        assertFalse("Answer field is displayed", solo.searchEditText("answer2"));
        assertTrue("Answer field is displayed", solo.searchEditText("answer1"));
    }

    public void testCannotSubmitWithAllAnswersRemoved() {
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");

        Button submitButton = solo.getButton("Submit");
        clickOnViewInListView(0, "\u2718");
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
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        clickOnViewInListView(0, "\u2718");

        Button submitButton = solo.getButton("Submit");
        assertFalse("Submit button is initially disabled",
                submitButton.isEnabled());
    }

    public void testCannotSubmitWithoutCorrectAnswer() {
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");

        Button submitButton = solo.getButton("Submit");
        assertFalse("Submit button is initially disabled",
                submitButton.isEnabled());
    }

    public void testClickAddAnswerButton() {
        assertTrue("Answer field is displayed",
                solo.searchEditText("Type in the answer"));
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        assertFalse("Answer field is displayed",
                solo.searchEditText("Type in the answer"));
        addAnswer();
        assertTrue("Answer field is displayed",
                solo.searchEditText("Type in the answer"));
    }

    public void testCannotSubmitWithoutQuestion() {
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");
        clickOnViewInListView(0, "\u2718");

        Button submitButton = solo.getButton("Submit");
        assertFalse("Submit button is initially disabled",
                submitButton.isEnabled());
    }

    public void testOnlyOneAnswerIsCorrect() {
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer3");
        addAnswer();

        clickOnViewInListView(0, "answer1");
        clickOnViewInListView(0, "answer3");
        clickOnViewInListView(0, "answer2");
        int nbCorrectAnswers = 0;
        List<View> listViews = solo
                .getViews((ListView) (getLinearLayout("answer1").getParent()));
        for (View view : listViews) {
            if (view instanceof Button
                    && ((Button) view).getText().equals("\u2714")) {
                nbCorrectAnswers++;
            }
        }

        assertTrue("There must be one and only one correct aswer",
                nbCorrectAnswers == 1);
    }

    public void testCannotSubmitWithEmptyTags() {

        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer3");

        clickOnViewInListView(0, "answer2");
        Button submitButton = solo.getButton("Submit");

        assertFalse("Submit button must be disabled without tags",
                submitButton.isEnabled());

        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        assertTrue("Submit button must be enabled with valid question",
                submitButton.isEnabled());

        solo.enterText(solo.getEditText("tag1 tag2"), "");
        assertFalse("Submit button must be disabled after removing tags",
                submitButton.isEnabled());
    }

    public void testScreenIsResetAfterSubmit() {
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer3");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        clickOnViewInListView(0, "answer2");
        Button submitButton = solo.getButton("Submit");
        assertTrue("Submit button must be enabled with valid question",
                submitButton.isEnabled());

        submitQuestion();

        LinearLayout topLevelLayout = (LinearLayout) submitButton.getParent()
                .getParent();
        List<View> listViews = solo.getViews(topLevelLayout);
        for (View view : listViews) {
            if (view instanceof EditText) {
                assertTrue("EditText not empty after submit", ((EditText) view)
                        .getText().toString().equals(""));
            }

        }
    }

    public void testToastAppearsWhenBADRequest() {
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer3");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        clickOnViewInListView(0, "answer2");
        Button submitButton = solo.getButton("Submit");
        assertTrue("Submit button must be enabled with valid question",
                submitButton.isEnabled());

        submitQuestion();

        assertTrue("EditQuestionActivity should show a Toast if bad request",
                solo.searchText("Could not upload the question to the server"));
    }

    public void test00SendQuestionTwiceOffline() throws JSONException {
        // Proxy.INSTANCE.setState(ConnectionState.OFFLINE);
        enterQuestion();
        mockHttpClient.pushCannedResponse("POST [^/]+",
                HttpStatus.SC_INTERNAL_SERVER_ERROR, "", "");
        submitQuestion();
        enterQuestion();
        submitQuestion();
        mockHttpClient.clearCannedResponses();
        mockHttpClient.pushCannedResponse("POST [^/]+", HttpStatus.SC_CREATED,
                JSONUtilities.getJSONString(question), "application/json");
        Proxy.INSTANCE.setState(ConnectionState.ONLINE);

    }

    public void testAuditNoErrorsWhenFieldsCorrectlySet() {
        EditQuestionActivity editQuestion = this.getActivity();
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer3");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");
        clickOnViewInListView(0, "answer2");

        assertTrue(
                "Submit button must be enabled when all the fields are correctly set",
                editQuestion.auditErrors() == 0);
    }

    public void testAuditNoErrorsOnFreshActivity() {
        EditQuestionActivity editQuestion = this.getActivity();

        assertTrue("There is no errors when starting a fresh activity",
                editQuestion.auditErrors() == 0);
    }

    public void testAuditErrorWithTwoCorrectAnswers() {
        EditQuestionActivity editQuestion = this.getActivity();

        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer3");
        solo.enterText(solo.getEditText("Type in the question's tags"),
                "tag1 tag2");

        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getButton("\u2718").setText("\u2714");
            }
        });
        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getButton("\u2718").setText("\u2714");
            }
        });

        solo.sleep(SLEEP_TIME);

        assertTrue(
                "Audit errors should report an error when there are two questions marked as correct ",
                editQuestion.auditErrors() == 1);

    }

//    public void testAuditWrongHintInQuestionField() {
//        EditQuestionActivity editQuestion = this.getActivity();
//
//        editQuestion.runOnUiThread(new Runnable() {
//            public void run() {
//                solo.getEditText("Type in the question's text body").setHint(
//                        "Prout!");
//            }
//        });
//
//        solo.sleep(SLEEP_TIME);
//
//        assertTrue(
//                "Audit errors should report an error when the question field has a bad hint",
//                editQuestion.auditErrors() == 1);
//    }

    public void testAuditWrongHintsInManyAnswers() {

        EditQuestionActivity editQuestion = this.getActivity();
        addAnswer();
        addAnswer();

        solo.sleep(SLEEP_TIME);

        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getEditText("Type in the answer").setHint("Prout!");
            }
        });

        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getEditText("Type in the answer").setHint("Prout!");
            }
        });

        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getEditText("Type in the answer").setHint("Prout!");
            }
        });

        solo.sleep(SLEEP_TIME);

        assertTrue("Audit errors should report an error per incorrect hint",
                editQuestion.auditErrors() == 3);
    }

    public void testAuditWrongHintInTags() {
        EditQuestionActivity editQuestion = this.getActivity();

        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getEditText("Type in the question's tags").setHint("Prout!");
            }
        });

        solo.sleep(SLEEP_TIME);

        assertTrue("Audit errors should report an error per incorrect hint",
                editQuestion.auditErrors() == 1);
    }
    
    public void testAuditWrongHintsEverywhere() {
        EditQuestionActivity editQuestion = this.getActivity();
        addAnswer();
        addAnswer();

        solo.sleep(SLEEP_TIME);

        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getEditText("Type in the answer").setHint("Prout!");
            }
        });

        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getEditText("Type in the answer").setHint("Prout!");
            }
        });

        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getEditText("Type in the answer").setHint("Prout!");
            }
        });
        
        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getEditText("Type in the question's tags").setHint("Prout!");
            }
        });
        
        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                solo.getEditText("Type in the question's text body").setHint(
                        "Prout!");
            }
        });

        solo.sleep(SLEEP_TIME);

        assertTrue("Audit errors should report an error per incorrect hint",
                editQuestion.auditErrors() == 5);
    }
    
    public void testAuditGoodQuestionSubmitButtonDisabled() {
        EditQuestionActivity editQuestion = this.getActivity();
        
        enterQuestion();
        
        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                Button submitButton = solo.getButton("Submit");
                submitButton.setEnabled(false);
            }
        });
        
        solo.sleep(SLEEP_TIME);
        
        assertTrue("Audit errors should report an error if the submit button is disabled when all the fields are correctly filled"
                , editQuestion.auditErrors() == 1);
    }
    
    public void testAuditBadQuestionSubmitButtonEnabled() {
        EditQuestionActivity editQuestion = this.getActivity();
        
        solo.enterText(solo.getEditText("Type in the question's text body"),
                "This is my question");
        solo.enterText(solo.getEditText("Type in the answer"), "answer1");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer2");
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), "answer3");
       
        
        editQuestion.runOnUiThread(new Runnable() {
            public void run() {
                Button submitButton = solo.getButton("Submit");
                submitButton.setEnabled(true);
            }
        });
        
        solo.sleep(SLEEP_TIME);
        
        assertTrue("Audit errors should report an error if the submit button is disabled when all the fields are correctly filled", editQuestion.auditErrors() == 1);
    }

    private void enterQuestion() {
        solo.enterText(solo.getEditText("Type in the question's text body"),
                question.getQuestion());
        solo.enterText(solo.getEditText("Type in the answer"), question
                .getAnswers().get(0));
        addAnswer();
        solo.enterText(solo.getEditText("Type in the answer"), question
                .getAnswers().get(1));
        solo.enterText(solo.getEditText("Type in the question's tags"),
                question.getTags().toString());
        clickOnViewInListView(question.getSolutionIndex(), "answer1");

    }

}
