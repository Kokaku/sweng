package epfl.sweng.entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import epfl.sweng.R;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.AuthenticationState;
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
        AuthenticationState.setState(AuthenticationState.UNAUTHENTICATED);
        TestCoordinator.check(TTChecks.MAIN_ACTIVITY_SHOWN);
        mShowQuestions = (Button) findViewById(R.id.show_random_question_button);
        mEditQuestion = (Button) findViewById(R.id.edit_question_button);
        mTequilaLogin = (Button) findViewById(R.id.tequila_login_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonStates(AuthenticationState.getState());
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
            AuthenticationState.setState(AuthenticationState.UNAUTHENTICATED);
            // TODO delete credentials from the persistent storage
        }
    }

    /**
     * Updates the buttons according to the current state of the application.
     * 
     * @param crrentState
     */

    private void updateButtonStates(AuthenticationState crrentState) {
        if (crrentState != AuthenticationState.AUTHENTICATED) {
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
