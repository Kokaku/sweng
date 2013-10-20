package epfl.sweng.test;

import android.widget.Button;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author MathieuMonney
 * 
 */
public class MainActivityAuthenticatedTest extends
        QuizActivityTestCase<MainActivity> {
    public static final String SHOW_QUESTION_TEXT = "Show a random question";
    public static final String SUBMIT_QUESTION_TEXT = "Submit a quiz question";
    public static final String TEQUILA_LOGOUT = "Log out";
    public static final String TEQUILA_LOGIN = "Log in using Tequila";

    public MainActivityAuthenticatedTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
    }

    public void testActivityInitiallyInTheRightState() {
        assertTrue(
                "The state is initially AUTHENTICATED",
                UserCredentials.INSTANCE.getState() == AuthenticationState.AUTHENTICATED);
    }

    public void testRandomQuestionButtonIsInitiallyEnabled() {
        Button randomQuestion = solo.getButton(SHOW_QUESTION_TEXT);
        assertTrue("Show a random question button is initially enabled",
                randomQuestion.isEnabled());
    }

    public void testSubmitQuestionButtonIsInitiallyEnabled() {
        Button submitQuestion = solo.getButton(SUBMIT_QUESTION_TEXT);
        assertTrue("Submit a quiz question button is initially enabled",
                submitQuestion.isEnabled());
    }

    public void testLogoutButtonIsDisplayed() {
        assertTrue("Logout button is displayed",
                solo.searchButton(TEQUILA_LOGOUT));
    }

    public void testLogoutButtonDeleteCredentials() {
        clickOnTextViewAndWaitFor(TEQUILA_LOGOUT, TTChecks.LOGGED_OUT);
        String sessionId = UserCredentials.INSTANCE.getSessionID();
        assertTrue("User credentials don't contain a SESSION_ID",
                sessionId.equals(""));
    }

    public void testButtonsUpdatedWhenLogoutClicked() {
        clickOnTextViewAndWaitFor(TEQUILA_LOGOUT, TTChecks.LOGGED_OUT);

        Button randomQuestion = solo.getButton(SHOW_QUESTION_TEXT);
        assertFalse("Show a random question button is disabled",
                randomQuestion.isEnabled());

        Button submitQuestion = solo.getButton(SUBMIT_QUESTION_TEXT);
        assertFalse("Submit a quiz question button is disabled",
                submitQuestion.isEnabled());

        assertTrue("Login button is displayed",
                solo.searchButton(TEQUILA_LOGIN));
    }

    public void testRandomQuestionButtonStartsActivity() {
        clickOnTextViewAndWaitFor(SHOW_QUESTION_TEXT, TTChecks.QUESTION_SHOWN);
        solo.assertCurrentActivity(
                "Show question button doesn't start activity",
                ShowQuestionsActivity.class);
    }

    public void testSubmitQuestionButtonStartsActivity() {
        clickOnTextViewAndWaitFor(SUBMIT_QUESTION_TEXT, TTChecks.EDIT_QUESTIONS_SHOWN);
        solo.assertCurrentActivity(
                "Edit question button doesn't start activity",
                EditQuestionActivity.class);
    }
    
    @Override
    protected void tearDown() throws Exception {
        UserCredentials.INSTANCE.setState(AuthenticationState.AUTHENTICATED);
        UserCredentials.INSTANCE.saveUserCredentials("test");
        super.tearDown();
    }
}