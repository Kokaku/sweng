/**
 * 
 */
package epfl.sweng.test;

import org.apache.http.HttpStatus;


import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author Zhivka Gucevska
 * 
 */
public class AuthenticationActivityTest extends QuizActivityTestCase<AuthenticationActivity> {
	MockHttpClient mock = new MockHttpClient();
	
	public AuthenticationActivityTest() {
		super(AuthenticationActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		SwengHttpClientFactory.setInstance(mock);
		mock.pushCannedResponse("GET https://sweng-quiz.appspot.com/login", HttpStatus.SC_OK, "{ \"token\": \"rqtvk5d3za2x6ocak1a41dsmywogrdlv5\"," 
			  + " \"message\": \"Here's your authentication token. Please validate it" 
			  +  "with Tequila at https://tequila.epfl.ch/cgi-bin/tequila/login\"}", null);
		mock.pushCannedResponse("POST https://tequila.epfl.ch/cgi-bin/tequila/login", HttpStatus.SC_MOVED_TEMPORARILY, "", null);
		mock.pushCannedResponse("POST https://sweng-quiz.appspot.com/login", HttpStatus.SC_OK, "{\"session\": \"<random_string>\","
			 + "\"message\": \"Here's your session id. Please include the following HTTP header in your subsequent requests:\n"
			       + "Authorization: Tequila <random_string>\"}", null);
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);

	}

	public void testPasswordFieldDisplayed() {
		assertTrue("Password field is displayed",
				solo.searchText("GASPAR Password"));
	}

	public void testUsernameFieldDisplayed() {
		assertTrue("Username field is displayed",
				solo.searchText("GASPAR Username"));
	}

	public void testLogInButtonDisplayed() {
		assertTrue("Log in button is displayed",
				solo.searchButton("Log in using Tequila"));
	}

//	public void testLogingIn() {
//		solo.enterText(solo.getEditText("GASPAR Username"), "whatever");
//		solo.enterText(solo.getEditText("GASPAR Password"), "whatever");
//
//		clickOnTextViewAndWaitFor("Log in using Tequila", TTChecks.MAIN_ACTIVITY_SHOWN);
//	}
	
}
