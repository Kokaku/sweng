package epfl.sweng.entry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import epfl.sweng.R;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.AuthenticationState;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Main activity of the application. Shows a menu to choose which activity
 * should be launched.
 * 
 * @author lseguy
 * 
 */

public class MainActivity extends Activity {
    private Button mShowQuestions;
    private Button mEditQuestion;
    private Button mTequilaLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShowQuestions = (Button) findViewById(R.id.show_random_question_button);
        mEditQuestion = (Button) findViewById(R.id.edit_question_button);
        mTequilaLogin = (Button) findViewById(R.id.tequila_login_button);

        UserCredentials.INSTANCE.initializeSharedPreferences(this);
        updateStateAndButtons();
        TestCoordinator.check(TTChecks.MAIN_ACTIVITY_SHOWN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStateAndButtons();
    }

    /**
     * When the first button is pressed, starts ShowQuestionsActivity
     * 
     * @param view
     *            the button which is pressed
     */
    public void showQuestion(View view) {
        startActivity(new Intent(this, ShowQuestionsActivity.class));
    }

    /**
     * When the second button is pressed, starts EditQuestionActivity
     * 
     * @param view
     *            the button which is pressed
     */
    public void submitQuestion(View view) {
        startActivity(new Intent(this, EditQuestionActivity.class));
    }

    /**
     * When the second button is pressed, starts AuthenticationActivity
     * 
     * @param view
     *            the button which is pressed
     */
    public void tequilaLogin(View view) {
        if (AuthenticationState.getState() != AuthenticationState.AUTHENTICATED) {
            startActivity(new Intent(this, AuthenticationActivity.class));
        } else {
            UserCredentials.INSTANCE.clearUserCredentials();
            updateStateAndButtons();
            TestCoordinator.check(TTChecks.LOGGED_OUT);
        }
    }
    
    private void updateStateAndButtons() {
        updateState();
        updateButtons();
    }

    /**
     * Updates the state according to the user credentials stored in the
     * SharedPreferences
     */
    private void updateState() {
        SharedPreferences sharedPreferences = UserCredentials.INSTANCE.getPreferences();
        String sessionId = sharedPreferences.getString("SESSION_ID", "");
        if (sessionId.equals("")) {
            AuthenticationState.setState(AuthenticationState.UNAUTHENTICATED);
        } else {
            AuthenticationState.setState(AuthenticationState.AUTHENTICATED);
        }
    }

    /**
     * Updates the buttons according to the current state of the application.
     * 
     * @param crrentState
     */

    private void updateButtons() {
        if (AuthenticationState.getState() != AuthenticationState.AUTHENTICATED) {
            mShowQuestions.setEnabled(false);
            mEditQuestion.setEnabled(false);
            mTequilaLogin.setText(R.string.tequila_login);
        } else {
            mShowQuestions.setEnabled(true);
            mEditQuestion.setEnabled(true);
            mTequilaLogin.setText(R.string.tequila_logout);
        }
    }

}
