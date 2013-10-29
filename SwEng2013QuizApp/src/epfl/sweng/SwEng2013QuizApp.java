package epfl.sweng;

import android.app.Application;
import android.content.Context;

/**
 * This class stores the application context to make it available to other
 * classes in a static fashion.
 * 
 * @author lseguy
 *
 */
public class SwEng2013QuizApp extends Application {

    private static Context context;

    /**
     * Initialization
     */
    public void onCreate() {
        super.onCreate();
        SwEng2013QuizApp.context = getApplicationContext();
    }

    /**
     * @return the application context
     */
    public static Context getAppContext() {
        return SwEng2013QuizApp.context;
    }
    
}