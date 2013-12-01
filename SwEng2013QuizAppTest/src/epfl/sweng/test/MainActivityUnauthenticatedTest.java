package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.widget.Button;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author lseguy
 * 
 */
public class MainActivityUnauthenticatedTest extends
        QuizActivityTestCase<MainActivity> {
	MockHttpClient mock = new MockHttpClient();

    public static final String SHOW_QUESTION_TEXT = "Show a random question";
    public static final String SUBMIT_QUESTION_TEXT = "Submit a quiz question";
    public static final String TEQUILA_LOGIN = "Log in using Tequila";
    public static final String SEARCH_TEXT = "Search";

    public MainActivityUnauthenticatedTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
        Thread.sleep(1000);

    }
    
    @Override
    protected void tearDown() throws Exception {
        UserCredentials.INSTANCE.setState(AuthenticationState.UNAUTHENTICATED);
        super.tearDown();
    }

    public void test0RightActivityIsDisplayed() {
        solo.assertCurrentActivity("Display activity is not displayed",
                MainActivity.class);
    }

    public void testActivityInitiallyInTheRightState() {
        assertTrue(
                "The state is initially UNAUTHENTICATED",
                UserCredentials.INSTANCE.getState() == AuthenticationState.UNAUTHENTICATED);
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
    
    public void testSearchButtonIsInitiallyDislayed() {
    	assertTrue("Search button must be displayed", 
    			solo.searchButton(SEARCH_TEXT));
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
    
    public void testSearchButtonIsInitiallyDisabled() {
    	Button searchButton = solo.getButton(SEARCH_TEXT);
    	assertFalse("Search question is initially disabled", searchButton.isEnabled());
    }

    public void testTequilaLoginButtonStartsActivity() {
        clickOnTextViewAndWaitFor(TEQUILA_LOGIN, 
                TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
        solo.assertCurrentActivity(
                "Tequila login button doesn't start activity",
                AuthenticationActivity.class);
    }

    public void testUserCredentialsStored() {
        UserCredentials.INSTANCE.setState(AuthenticationState.AUTHENTICATED);
        UserCredentials.INSTANCE.saveUserCredentials("test");
        String userCredentials = UserCredentials.INSTANCE.getSessionID();
        assertTrue("User creditentials correctly stored",
                userCredentials.equals("test"));
    }
    
    public void testLogingIn() {
    	setUpMock();
		clickOnTextViewAndWaitFor("Log in", TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);

    	
		solo.enterText(solo.getEditText("GASPAR Username"), "whatever");
		solo.enterText(solo.getEditText("GASPAR Password"), "whatever");

		clickOnTextViewAndWaitFor("Log in using Tequila", TTChecks.MAIN_ACTIVITY_SHOWN);
		UserCredentials.INSTANCE.setState(AuthenticationState.UNAUTHENTICATED);
    }
    
    private void  setUpMock(){
    	mock.pushCannedResponse("GET https://sweng-quiz.appspot.com/login", HttpStatus.SC_OK, "{ \"token\": \"rqtvk5d3za2x6ocak1a41dsmywogrdlv5\"," 
  			  + " \"message\": \"Here's your authentication token. Please validate it" 
  			  +  "with Tequila at https://tequila.epfl.ch/cgi-bin/tequila/login\"}", null);
  		mock.pushCannedResponse("POST https://tequila.epfl.ch/cgi-bin/tequila/login", HttpStatus.SC_MOVED_TEMPORARILY, "", null);
  		mock.pushCannedResponse("POST https://sweng-quiz.appspot.com/login", HttpStatus.SC_OK, "{\"session\": \"<random_string>\","
  			 + "\"message\": \"Here's your session id. Please include the following HTTP header in your subsequent requests:\n"
  			       + "Authorization: Tequila <random_string>\"}", null);
  		SwengHttpClientFactory.setInstance(mock);
    }
}
