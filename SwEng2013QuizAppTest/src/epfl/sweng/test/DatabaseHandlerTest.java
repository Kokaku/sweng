package epfl.sweng.test;

import epfl.sweng.entry.MainActivity;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author ValentinRutz
 *
 */
public class DatabaseHandlerTest extends QuizActivityTestCase<MainActivity> {

    private static String CLEAR_CACHE = "Clear the cache";
    
    public DatabaseHandlerTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
        solo.clickOnMenuItem(CLEAR_CACHE);
        solo.clickOnCheckBox(0);
        
    }
    
    @Override
    public void tearDown() throws Exception {
        
        super.tearDown();   
    }
}
