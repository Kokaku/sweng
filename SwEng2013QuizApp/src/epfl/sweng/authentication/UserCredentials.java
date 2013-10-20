package epfl.sweng.authentication;

import android.content.Context;
import android.content.SharedPreferences;

/**
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

    public void initializeSharedPreferences(Context context) {
        if (userSession == null) {
            userSession = context.getSharedPreferences("user_session",
                    Context.MODE_PRIVATE);
        }
    }

    public String getSessionID() {
        return userSession.getString("SESSION_ID", "");
    }

    public boolean saveUserCredentials(String sessionIdValue) {
        if (currentState == AuthenticationState.AUTHENTICATED) {
            SharedPreferences.Editor preferencesEditor = userSession.edit();
            preferencesEditor.putString("SESSION_ID", sessionIdValue);
            preferencesEditor.commit();
            return true;
        }
        return false;
    }

    public void setState(AuthenticationState newState) {
        if (newState != AuthenticationState.AUTHENTICATED
                && !getSessionID().equals("")) {
            clearUserCredentials();
        }
        currentState = newState;
    }

    public AuthenticationState getState() {
        return currentState;
    }

    private void clearUserCredentials() {
        SharedPreferences.Editor preferencesEditor = userSession.edit();
        preferencesEditor.clear();
        preferencesEditor.commit();
    }
}
