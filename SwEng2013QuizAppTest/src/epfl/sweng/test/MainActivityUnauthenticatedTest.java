package epfl.sweng.test;

import android.widget.Button;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.AuthenticationState;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author lseguy
 * 
 */
public class MainActivityUnauthenticatedTest extends
        QuizActivityTestCase<MainActivity> {

    public static final String SHOW_QUESTION_TEXT = "Show a random question";
    public static final String SUBMIT_QUESTION_TEXT = "Submit a quiz question";
    public static final String TEQUILA_LOGIN = "Log in using Tequila";

    public MainActivityUnauthenticatedTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
        UserCredentials.INSTANCE.clearUserCredentials();
    }

    public void testRightActivityIsDisplayed() {
        solo.assertCurrentActivity("Display activity is not displayed",
                MainActivity.class);
    }

    public void testActivityInitiallyInTheRightState() {
        assertTrue(
                "The state is initially UNAUTHENTICATED",
                AuthenticationState.getState() == AuthenticationState.UNAUTHENTICATED);
    }

    public void testRandomQuestionButtonIsDisplayed() {
        assertTrue("Show a random question button must be displayed",
                solo.searchButton(SHOW_QUESTION_TEXT));
    }

    public void testSubmitQuestionButtonIsDisplayed() {
        assertTrue("Submit a quiz question button must be displayed",
                solo.searchButton(SUBMIT_QUESTION_TEXT));
    }

    public void testTequilaLoginButtonIsDisplayed() {
        assertTrue("Tequila login button must be displayed",
                solo.searchButton(TEQUILA_LOGIN));
    }

    public void testRandomQuestionButtonIsInitiallyDisabled() {
        Button randomQuestion = solo.getButton(SHOW_QUESTION_TEXT);
        assertFalse("Show a random question button is initially disabled",
                randomQuestion.isEnabled());
    }

    public void testSubmitQuestionButtonIsInitiallyDisabled() {
        Button submitQuestion = solo.getButton(SUBMIT_QUESTION_TEXT);
        assertFalse("Submit a quiz question button is initially disabled",
                submitQuestion.isEnabled());
    }

    public void testTequilaLoginButtonStartsActivity() {
        clickOnTextViewAndWaitFor(TEQUILA_LOGIN, 
                TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
        solo.assertCurrentActivity(
                "Tequila login button doesn't start activity",
                AuthenticationActivity.class);
    }

    public void testUserCredentialsStored() {
        UserCredentials.INSTANCE.saveUserCredentials("test");
        String userCredentials = UserCredentials.INSTANCE.getPreferences()
                .getString("SESSION_ID", "");
        assertTrue("User creditentials correctly stored",
                userCredentials.equals("test"));
        UserCredentials.INSTANCE.clearUserCredentials();
    }

}
