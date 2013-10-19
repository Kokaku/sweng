package epfl.sweng.authentication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author MathieuMonney
 *
 */
public enum UserCredentials {
    INSTANCE;
    
    // TODO check if this is really what they ask in the homework statement
    private static SharedPreferences user_session = null;
    
    private UserCredentials() {
        
    }
    
    public void initializeSharedPreferences(Context context) {
        if (user_session == null) {
            user_session = context.getSharedPreferences("epfl.sweng.authentication", Context.MODE_PRIVATE);
        }
    }
    
    public SharedPreferences getPreferences() {
        return user_session;
    }
    
    public void saveUserCredentials(String sessionIdValue) {
        SharedPreferences.Editor preferencesEditor = user_session.edit();
        preferencesEditor.putString("SESSION_ID", sessionIdValue);
        preferencesEditor.commit();
    }
    
    public void clearUserCredentials() {
        SharedPreferences.Editor preferencesEditor = user_session.edit();
        preferencesEditor.clear();
        preferencesEditor.commit();
    }
}
