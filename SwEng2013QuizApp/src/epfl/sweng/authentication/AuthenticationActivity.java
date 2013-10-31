package epfl.sweng.authentication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import epfl.sweng.R;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.exceptions.InvalidCredentialsException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.servercomm.ServerCommunication;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/*
 * @author lseguy
 */
public class AuthenticationActivity extends Activity {

    private EditText mUsername;
    private EditText mPassword;
    private Button mButton;
    private ProgressBar mProgressBar;

    /**
     * Initialization of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mUsername = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.user_password);
        mButton = (Button) findViewById(R.id.button_login);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_authentication);

        TestCoordinator.check(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
    }

    /**
     * Called when the button is clicked. If login is successful, finish the
     * activity. Otherwise resets the EditText views.
     * 
     * @param view the button being clicked
     */
    public void onClickLogin(View view) {
        hideKeyboard();
        
        if (isEmpty(mUsername) || isEmpty(mPassword)) {
            SwEng2013QuizApp.displayToast(R.string.login_empty_fields_error);
        } else {
            new AuthenticationTask().execute();
        }
    }
    
    private boolean isEmpty(EditText view) {
        return view.getText().toString().trim().length() == 0;
    }
    
    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
    
    /**
     * Authenticate with the servers in a separate thread then sets the UI
     * accordingly.
     */
    private class AuthenticationTask extends AsyncTask<Void, Void, Void> {
        
        private Exception mException = null;

        @Override
        protected void onPreExecute() {
            mButton.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        
        @Override
        protected Void doInBackground(Void... unused) {
            try {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                ServerCommunication.INSTANCE.login(username, password);
            } catch (ServerCommunicationException e) {
                mException = e;
            }

            return null;
        }
        
        @Override
        protected void onPostExecute(Void unused) {
            if (mException == null) {
                finish();
            } else {
                mButton.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                if (mException instanceof InvalidCredentialsException) {
                    SwEng2013QuizApp.displayToast(R.string.invalid_credentials);
                }
                if (mException instanceof ServerCommunicationException) {
                    SwEng2013QuizApp.displayToast(R.string.failed_to_log_in);
                }
                mUsername.setText("");
                mPassword.setText("");
                Log.e("Authentication", mException.getMessage());
                TestCoordinator.check(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
            }
        }

    }

}
