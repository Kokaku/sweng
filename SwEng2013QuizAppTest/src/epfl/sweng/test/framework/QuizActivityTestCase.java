package epfl.sweng.test.framework;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.testing.TestingTransaction;

/**
 * @author lseguy
 *
 */
public abstract class QuizActivityTestCase<T extends Activity>
    extends ActivityInstrumentationTestCase2<T> {
    
    public static final int WAIT_TIME_FOR_ACTIVITY = 2000;
    protected Solo solo;
    
    public QuizActivityTestCase(Class<T> activityClass) {
        super(activityClass);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation());
    }
    
    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }
    
    protected void runAndWaitFor(final Runnable runnable, final TestCoordinator.TTChecks expected) {
        TestCoordinator.run(getInstrumentation(), new CustomTransaction(runnable, expected));
    }
    
    protected void getActivityAndWaitFor(final TestCoordinator.TTChecks expected) {
        runAndWaitFor(new Runnable() {
            public void run() {
                getActivity();
            }
        }, expected);
    }
    
    protected void clickOnTextViewAndWaitFor(final String textViewText, 
        final TestCoordinator.TTChecks expected) {
        runAndWaitFor(new Runnable() {
            public void run() {
                solo.clickOnText(textViewText);
            }
        }, expected);
    }
    
    private class CustomTransaction implements TestingTransaction {
        /**
         * @param runnable
         * @param expected
         */
        private Runnable runnable;
        private TTChecks expected;
        
        public CustomTransaction(Runnable runnable, TTChecks expected) {
            this.runnable = runnable;
            this.expected = expected;
        }

        @Override
        public void initiate() {
            runnable.run();
        }
        
        @Override
        public void verify(TestCoordinator.TTChecks notification) {
            // No matter what the docs say, and no matter how testing transactions are implemented,
            // starting activities is not synchronous. Thus, we wait... sigh...
            // With a two second delay, it worked for Jonas for 10 repetitions. 
            solo.sleep(WAIT_TIME_FOR_ACTIVITY);
            assertEquals(String.format(
                "Expected notification %s, but received %s", expected,
                notification), expected, notification);
        }
        
        @Override
        public String toString() {
            return String.format("runAndWaitFor(%s)", expected);
        }
    }
    
}
