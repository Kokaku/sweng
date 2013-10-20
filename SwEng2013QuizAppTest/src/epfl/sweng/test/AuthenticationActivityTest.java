/**
 * 
 */
package epfl.sweng.test;

import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author Zhivka Gucevska
 * 
 */
public class AuthenticationActivityTest extends QuizActivityTestCase<AuthenticationActivity> {
	private MockHttpClient mockHttpClient;

	public AuthenticationActivityTest() {
		super(AuthenticationActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();

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
				solo.searchButton("Log in Tequila"));
	}

}
