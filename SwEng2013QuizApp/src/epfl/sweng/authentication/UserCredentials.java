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

    private SharedPreferences mUserSession = null;
    private AuthenticationState mCurrentState = AuthenticationState.UNAUTHENTICATED;
    
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
        return mUserSession.getString("SESSION_ID", "");
    }

    /**
     * Sets a new session ID.
     * 
     * @param sessionIdValue the session ID to be set
     * @return true if successful, false otherwise
     */
    public boolean saveUserCredentials(String sessionIdValue) {
        if (isAuthenticated() && !sessionIdValue.equals("")) {
            SharedPreferences.Editor preferencesEditor = mUserSession.edit();
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
        mCurrentState = newState;
    }

    /**
     * @return the current authentication state
     */
    public AuthenticationState getState() {
        return mCurrentState;
    }
    
    /**
     * @return true if the user is currently authenticated
     */
    public boolean isAuthenticated() {
        return mCurrentState == AuthenticationState.AUTHENTICATED;
    }
    
    /**
     * Gets the shared preferences and sets the authentication state if there is
     * a saved session ID.
     * 
     * @param context a context to get the preferences
     */
    private void initializeSharedPreferences(Context context) {
        mUserSession = context.getSharedPreferences("user_session", 
                Context.MODE_WORLD_READABLE);
        
        if (!getSessionID().equals("")) {
            mCurrentState = AuthenticationState.AUTHENTICATED;
        }
    }

    private void clearUserCredentials() {
        SharedPreferences.Editor preferencesEditor = mUserSession.edit();
        preferencesEditor.clear();
        preferencesEditor.commit();
    }
}
