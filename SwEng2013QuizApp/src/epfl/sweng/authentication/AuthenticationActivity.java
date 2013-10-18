package epfl.sweng.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.servercomm.ServerCommunication;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class AuthenticationActivity extends Activity {

    private EditText mUsername;
    private EditText mPassword;

    /**
     * Initialization of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mUsername = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.user_password);

        TestCoordinator.check(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.authentication, menu);
        return true;
    }

    /**
     * Called when button "Log in using Tequila" is clicked. If login is
     * impossible, it only resets the EditText where the user enters its
     * credentials and finishes if the login is successful
     */
    public void onClickLogin(View view) {
        if (ServerCommunication.login(mUsername.getText().toString(),
                mPassword.getText().toString()) == true) {
            finish();
        } else {
            mUsername.setText("");
            mPassword.setText("");
        }
    }

}
