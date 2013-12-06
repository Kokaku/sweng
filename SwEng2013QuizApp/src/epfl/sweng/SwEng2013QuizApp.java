package epfl.sweng;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * This class stores the application context to make it available to other
 * classes in a static fashion.
 * 
 * Also provides some utility methods.
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
        setContext(getApplicationContext());
    }
    
    /**
     * Displays a toast.
     * 
     * @param ressource the desired text for the {@link Toast}
     */
    public static void displayToast(int ressource) {
        String text = context.getResources().getString(ressource);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * @return the application context
     */
    public static Context getAppContext() {
        return SwEng2013QuizApp.context;
    }
    
    
    private static void setContext(Context context) {
        SwEng2013QuizApp.context = context;
    }
    
}