package epfl.sweng;

import epfl.sweng.offline.DatabaseHandler;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
    private static DatabaseHandler dbHandler;

    /**
     * Initialization
     */
    @Override
    public void onCreate() {
        super.onCreate();
        SwEng2013QuizApp.context = getApplicationContext();
        if (dbHandler == null) {
            dbHandler = new DatabaseHandler();
        }
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
    
    /**
     * @return the instance of DatabaseHandler used by the app
     */
    public static DatabaseHandler getDbHandler() {
        return dbHandler;
    }
}