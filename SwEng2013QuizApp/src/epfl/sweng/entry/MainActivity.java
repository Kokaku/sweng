package epfl.sweng.entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import epfl.sweng.R;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
 * Main activity of the application. Shows a menu to choose which activity
 * should be launched.
 * 
 * @author lseguy
 * 
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestingTransactions.check(TTChecks.MAIN_ACTIVITY_SHOWN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void showQuestion(View view) {
        startActivity(new Intent(this, ShowQuestionsActivity.class));
    }

    public void submitQuestion(View view) {
        startActivity(new Intent(this, EditQuestionActivity.class));
    }

}
