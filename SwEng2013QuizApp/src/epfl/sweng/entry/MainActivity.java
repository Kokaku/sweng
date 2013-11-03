package epfl.sweng.entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import epfl.sweng.R;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.offline.DatabaseHandler;
import epfl.sweng.patterns.Proxy;
import epfl.sweng.patterns.Proxy.ConnectionState;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Main activity of the application.
 * Shows a menu to choose which activity should be launched.
 * The last button is used to log in / log out.
 * 
 * @author lseguy
 * 
 */

public class MainActivity extends Activity {
    
    private Button mShowQuestions;
    private Button mEditQuestion;
    private Button mTequilaLogin;
    private CheckBox mOfflineMode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShowQuestions = (Button) findViewById(R.id.show_random_question_button);
        mEditQuestion = (Button) findViewById(R.id.edit_question_button);
        mTequilaLogin = (Button) findViewById(R.id.tequila_login_button);
        mOfflineMode = (CheckBox) findViewById(R.id.checkbox_offline);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtons();
        TestCoordinator.check(TTChecks.MAIN_ACTIVITY_SHOWN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.clear_cache:
                // TODO : Add warning box
                DatabaseHandler db = new DatabaseHandler();
                db.clearCache();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Called when the first button is pressed, starts ShowQuestionsActivity.
     * 
     * @param view the button which is pressed
     */
    public void showQuestion(View view) {
        startActivity(new Intent(this, ShowQuestionsActivity.class));
    }

    /**
     * Called when the second button is pressed, starts EditQuestionActivity.
     * 
     * @param view the button which is pressed
     */
    public void submitQuestion(View view) {
        startActivity(new Intent(this, EditQuestionActivity.class));
    }

    /**
     * Called when the third button is pressed, either starts AuthenticationActivity
     * if the user is not currently logged in or log out the user.
     * 
     * @param view the button which is pressed
     */
    public void tequilaLogin(View view) {
        if (UserCredentials.INSTANCE.getState() != AuthenticationState.AUTHENTICATED) {
            startActivity(new Intent(this, AuthenticationActivity.class));
        } else {
            UserCredentials.INSTANCE.setState(AuthenticationState.UNAUTHENTICATED);
            updateButtons();
            TestCoordinator.check(TTChecks.LOGGED_OUT);
        }
    }
    
    /**
     * Called when the offline checkbox is ticked or unticked.
     * 
     * @param view the checkbox
     */
    public void offlineMode(View view) {
        // TODO : I need more code !
        CheckBox checkbox = (CheckBox) view;
        
        if (checkbox.isChecked()) {
            Proxy.INSTANCE.setState(ConnectionState.OFFLINE);
        } else {
            Proxy.INSTANCE.setState(ConnectionState.ONLINE);
        }
    }

    /**
     * Updates the buttons according to the current state of the application.
     */
    private void updateButtons() {
        if (UserCredentials.INSTANCE.isAuthenticated()) {
            mShowQuestions.setEnabled(true);
            mEditQuestion.setEnabled(true);
            mOfflineMode.setEnabled(true);
            mTequilaLogin.setText(R.string.tequila_logout);
        } else {
            mShowQuestions.setEnabled(false);
            mEditQuestion.setEnabled(false);
            mOfflineMode.setEnabled(false);
            mTequilaLogin.setText(R.string.tequila_login);
        }
        
        if (Proxy.INSTANCE.getState() == ConnectionState.OFFLINE) {
            mOfflineMode.setChecked(true);
        } else {
            mOfflineMode.setChecked(false);
        }
    }

}
