package epfl.sweng.test;

import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author MathieuMonney
 * 
 */
public class UserCredentialsTest extends QuizActivityTestCase<MainActivity> {

    public UserCredentialsTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
    }

    @Override
    protected void tearDown() throws Exception {
        UserCredentials.INSTANCE.setState(AuthenticationState.UNAUTHENTICATED);
        super.tearDown();
    }

    public void testCantAddUserCredentialsWhenUnauthenticated() {
        UserCredentials.INSTANCE.setState(AuthenticationState.UNAUTHENTICATED);
        UserCredentials.INSTANCE.saveUserCredentials("test");
        assertTrue("User credentials should be empty", UserCredentials.INSTANCE
                .getSessionID().equals(""));
    }

    public void testCanAddUserCredentialsWhenAuthenticated() {
        UserCredentials.INSTANCE.setState(AuthenticationState.AUTHENTICATED);
        UserCredentials.INSTANCE.saveUserCredentials("test");
        assertTrue("User credentials should be empty", UserCredentials.INSTANCE
                .getSessionID().equals("test"));
    }

    public void testUserCredentialsDeletedWhenSwitchToUnauthenticated() {
        UserCredentials.INSTANCE.setState(AuthenticationState.AUTHENTICATED);
        UserCredentials.INSTANCE.saveUserCredentials("test");
        UserCredentials.INSTANCE.setState(AuthenticationState.UNAUTHENTICATED);
        assertTrue("User credentials should be empty", UserCredentials.INSTANCE
                .getSessionID().equals(""));
    }
}
