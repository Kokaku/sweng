package epfl.sweng.authentication;

import epfl.sweng.SwEng2013QuizApp;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * This singleton class keeps the authentication state of the user.
 * 
 * @author MathieuMonney
 * 
 */
public enum UserCredentials {
    INSTANCE;

    private SharedPreferences userSession = null;
    private AuthenticationState currentState = AuthenticationState.UNAUTHENTICATED;

    public enum AuthenticationState {
        UNAUTHENTICATED, TOKEN, TEQUILA, CONFIRMATION, AUTHENTICATED;
    }
    
    private UserCredentials() {
        initializeSharedPreferences(SwEng2013QuizApp.getAppContext());
    }

    /**
     * @return the current session ID if the user is logged in, an empty string
     * otherwise.
     */
    public String getSessionID() {
        return userSession.getString("SESSION_ID", "");
    }

    /**
     * Sets a new session ID.
     * 
     * @param sessionIdValue the session ID to be set
     * @return true if successful, false otherwise
     */
    public boolean saveUserCredentials(String sessionIdValue) {
        if (currentState == AuthenticationState.AUTHENTICATED
                    && !sessionIdValue.equals("")) {
            SharedPreferences.Editor preferencesEditor = userSession.edit();
            preferencesEditor.putString("SESSION_ID", sessionIdValue);
            preferencesEditor.commit();
            return true;
        }
        return false;
    }

    /**
     * Sets the authentication state. If not {@code AUTHENTICATED}, the
     * session ID is cleared.
     * 
     * @param newState the desired authentication state
     */
    public void setState(AuthenticationState newState) {
        if (newState != AuthenticationState.AUTHENTICATED
                && !getSessionID().equals("")) {
            clearUserCredentials();
        }
        currentState = newState;
    }

    /**
     * @return the current authentication state
     */
    public AuthenticationState getState() {
        return currentState;
    }
    
    /**
     * Gets the shared preferences and sets the authentication state if there is
     * a saved session ID.
     * 
     * @param context a context to get the preferences
     */
    private void initializeSharedPreferences(Context context) {
        userSession = context.getSharedPreferences("user_session", 
                Context.MODE_PRIVATE);
        
        if (!getSessionID().equals("")) {
            currentState = AuthenticationState.AUTHENTICATED;
        }
    }

    private void clearUserCredentials() {
        SharedPreferences.Editor preferencesEditor = userSession.edit();
        preferencesEditor.clear();
        preferencesEditor.commit();
    }
}
