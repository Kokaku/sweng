package epfl.sweng.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

/**
 * @author MathieuMonney
 *
 */
public abstract class QuizAppTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
    
    protected Solo solo;
    
    
    public QuizAppTest(Class<T> activityClass) {
        super(activityClass);
    }

}
