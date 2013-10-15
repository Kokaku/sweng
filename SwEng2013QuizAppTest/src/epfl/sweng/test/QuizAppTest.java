package epfl.sweng.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestingTransaction;

/**
 * @author lseguy
 *
 */
public abstract class QuizAppTest<T extends Activity>
    extends ActivityInstrumentationTestCase2<T>
    {
    
    protected Solo solo;
    
    public QuizAppTest(Class<T> activityClass) {
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
    
    protected void getActivityAndWaitFor(final TestCoordinator.TTChecks expected) {
        TestCoordinator.run(getInstrumentation(), new TestingTransaction() {
          @Override
          public void initiate() {
            getActivity();
          }

          @Override
          public void verify(TestCoordinator.TTChecks notification) {
            assertEquals(String.format(
                "Expected notification %s, but received %s", expected,
                notification), expected, notification);
          }

          @Override
          public String toString() {
            return String.format("getActivityAndWaitFor(%s)", expected);
          }
        });
      }
    
}
