package epfl.sweng.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import epfl.sweng.R;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.exceptions.AsyncTaskExceptions;
import epfl.sweng.exceptions.InvalidCredentialsException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.servercomm.ServerCommunication;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/*
 * Activity for logging in with Tequila server.
 * 
 * @author lseguy
 */
public class AuthenticationActivity extends Activity {

    private static final String LOG_TAG = AuthenticationActivity.class.getSimpleName();
    
    private AuthenticationTask mAuthTask = null;
    
    private EditText mUsername;
    private EditText mPassword;
    private View mLoginStatus;
    private View mLoginForm;

    /**
     * Initialization of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mUsername = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.user_password);
        mLoginStatus = findViewById(R.id.login_status);
        mLoginForm = findViewById(R.id.login_form);
        
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        
        TestCoordinator.check(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
    }
    
    /**
     * Called when the button is clicked.
     * 
     * @param view the button being clicked
     */
    public void onClickLogin(View view) {
        attemptLogin();
    }
    
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors, the errors are presented and no actual login
     * attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            Log.d(LOG_TAG, "Already logging in");
            return;
        }
        
        mUsername.setError(null);
        mPassword.setError(null);
        
        if (TextUtils.isEmpty(mUsername.getText())) {
            Log.d(LOG_TAG, "Empty username");
            mUsername.setError(getString(R.string.error_field_required));
            mUsername.requestFocus();
        } else if (TextUtils.isEmpty(mPassword.getText())) {
            Log.d(LOG_TAG, "Empty password");
            mPassword.setError(getString(R.string.error_field_required));
            mPassword.requestFocus();
        } else {
            mAuthTask = new AuthenticationTask();
            mAuthTask.execute();
        }
    }
    
    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int animationTime = getResources().getInteger(
            android.R.integer.config_shortAnimTime);
        
        mLoginStatus.setVisibility(View.VISIBLE);
        mLoginStatus.animate().setDuration(animationTime)
            .alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginStatus.setVisibility(show ? View.VISIBLE
                        : View.GONE);
                }
            });

        mLoginForm.setVisibility(View.VISIBLE);
        mLoginForm.animate().setDuration(animationTime)
            .alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginForm.setVisibility(show ? View.GONE
                        : View.VISIBLE);
                }
            });
    }
    
    /**
     * Force the keyboard to hide
     */
    private void showKeyboard(boolean show) {
        InputMethodManager inputManager = (InputMethodManager) 
            getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            inputManager.showSoftInput(mUsername,
                InputMethodManager.SHOW_IMPLICIT);
        } else {
            inputManager.hideSoftInputFromWindow(mUsername.getWindowToken(), 
                InputMethodManager.HIDE_NOT_ALWAYS);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
            SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
        
    /**
     * Authenticates with the servers in a separate thread then sets the UI
     * accordingly.
     */
    private class AuthenticationTask extends AsyncTask<Void, Void, Void> {
        
        private AsyncTaskExceptions mException = null;
        
        @Override
        protected void onPreExecute() {
            showKeyboard(false);
            showProgress(true);
        }
        
        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(LOG_TAG, "AuthenticationTask starting");
            
            try {
                ServerCommunication.INSTANCE.login(mUsername.getText().toString(), 
                    mPassword.getText().toString());
            } catch (InvalidCredentialsException e) {
                Log.d(LOG_TAG, e.getClass().getSimpleName() + ": " + e.getMessage());
                mException = AsyncTaskExceptions.INVALID_CREDENTIALS;
            } catch (ServerCommunicationException e) {
                Log.d(LOG_TAG, e.getClass().getSimpleName() + ": " + e.getMessage());
                mException = AsyncTaskExceptions.SERVER_COMMUNICATION_EXCEPTION;
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(Void unused) {
            mAuthTask = null;
            showProgress(false);
            
            if (mException == null) {
                Log.d(LOG_TAG, "Yipee, login successful!");
                finish();
            } else {
                switch (mException) {
                    case INVALID_CREDENTIALS:
                        mUsername.setError(getString(R.string.invalid_credentials));
                        mUsername.requestFocus();
                        break;
                    case SERVER_COMMUNICATION_EXCEPTION:
                        SwEng2013QuizApp.displayToast(R.string.failed_to_log_in);
                        break;
                    default:
                        assert false;
                }
                
                Log.d(LOG_TAG, "Oh no! Login failed");
                mUsername.setText("");
                mPassword.setText("");
                showKeyboard(true);
                TestCoordinator.check(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
            }
        }

    }

}
