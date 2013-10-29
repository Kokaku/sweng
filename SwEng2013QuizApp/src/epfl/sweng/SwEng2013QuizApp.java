/**
 * 
 */
package epfl.sweng;

import android.app.Application;
import android.content.Context;

/**
 * @author lseguy
 *
 */
public class SwEng2013QuizApp extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        SwEng2013QuizApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return SwEng2013QuizApp.context;
    }
}