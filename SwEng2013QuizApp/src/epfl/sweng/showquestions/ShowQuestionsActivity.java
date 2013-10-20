package epfl.sweng.showquestions;

import java.util.Set;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import epfl.sweng.R;
import epfl.sweng.questions.QuizQuestion;
import epfl.sweng.servercomm.ServerCommunication;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * This activity displays questions and allows the user to answer them.
 * The user has to chose the right answer to be able to get a new question.
 * Displays an error dialog if unable to retrieve a question from the server.
 * 
 * @author lseguy
 * 
 */

public class ShowQuestionsActivity extends ListActivity {
    
    //  How long the "correct" or "incorrect" symbol will be displayed (in milliseconds)
    private static final int SYMBOL_DISPLAY_TIME = 1000;
    
    // How long should the device vibrate (in milliseconds)
    private static final int VIBRATOR_DURATION = 250;
    
    private QuizQuestion mCurrentQuestion;
    private TextView mQuestionText;
    private LinearLayout mTagsList;
    private Button mNextButton;
    private TextView mSymbol;
    private Vibrator mVibrator;
    
    /**
     * Initialization of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_questions);
        
        mQuestionText = (TextView) findViewById(R.id.text_question);
        mNextButton = (Button) findViewById(R.id.button_next);
        mSymbol = (TextView) findViewById(R.id.text_check_answer);
        mTagsList = (LinearLayout) findViewById(R.id.list_tags);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        // Enable scrolling for the question
        mQuestionText.setMovementMethod(new ScrollingMovementMethod());
        
        showNewQuestion();
    }
    
    /**
     * Called when an item is clicked in the answer list.
     * Displays a check mark and enable the "next" button if the answer is
     * correct. Otherwise, displays an error symbol for SYMBOL_DISPLAY_TIME
     * milliseconds.
     */
    @Override
    public void onListItemClick(ListView list, View view,
        int position, long id) {
        
        final boolean correctAnswer = mCurrentQuestion.isSolutionCorrect(position);
        
        if (correctAnswer) {
            mSymbol.setText(R.string.correct_answer);
            mSymbol.setTextColor(getResources().getColor(R.color.right_answer));
            view.setBackgroundColor(getResources().getColor(R.color.right_answer_list_item));
        } else {
            mSymbol.setText(R.string.wrong_answer);
            mSymbol.setTextColor(getResources().getColor(R.color.wrong_answer));
            
            mVibrator.vibrate(VIBRATOR_DURATION);
        }
        
        getListView().setEnabled(false);
        mSymbol.setVisibility(View.VISIBLE);
        
        mSymbol.postDelayed(new Runnable() {
            public void run() {
                if (correctAnswer) {
                    mNextButton.setEnabled(true);
                } else {
                    getListView().setEnabled(true);
                }
                
                TestCoordinator.check(TTChecks.ANSWER_SELECTED);
                mSymbol.setVisibility(View.INVISIBLE);
            }
        }, SYMBOL_DISPLAY_TIME);
        
    }
    
    /**
     * Called when clicking on the button "Next question"
     * Sets views back to their normal state then gets a new question.
     * 
     * @param view the button being clicked
     */
    public void nextButtonClicked(View view) {
        getListView().setEnabled(true);
        mNextButton.setEnabled(false);
        showNewQuestion();
    }
    
    /**
     * Retrieve a new question and set views so that the question is displayed.
     */
    private void showNewQuestion() {
        mCurrentQuestion = ServerCommunication.getInstance().getRandomQuestion();
        
        if (mCurrentQuestion == null) {
            showErrorDialog();
        } else {
            Set<String> tags = mCurrentQuestion.getTags();
            String[] tagsArray = tags.toArray(new String[tags.size()]);
    
            // Using an adapter to fill the LinearLayout with data from the array
            ArrayAdapter<String> adapterTags = new ArrayAdapter<String>(this,
                R.layout.list_of_tags, tagsArray);
    
            mTagsList.removeAllViews();
            
            for (int i = 0; i < adapterTags.getCount(); ++i) {
                View item = adapterTags.getView(i, null, mTagsList);
                mTagsList.addView(item);
            }

            mQuestionText.setText(mCurrentQuestion.getQuestion());
            
            // Sets the answer list
            ArrayAdapter<String> adapterAnswers = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mCurrentQuestion.getAnswers());
            
            setListAdapter(adapterAnswers);
            
            TestCoordinator.check(TTChecks.QUESTION_SHOWN);
        }
        
    }
    
    /**
     * Displays an error dialog when the question can't be retrieved.
     */
    private void showErrorDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        
        // The user can either try to get a new question
        dialogBuilder.setPositiveButton(R.string.text_retry,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    showNewQuestion();
                }
            });
        
        // Or close the activity
        dialogBuilder.setNegativeButton(R.string.text_abort,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
        
        dialogBuilder.setCancelable(false);
        dialogBuilder.setMessage(R.string.dialog_showquestions_error);
        dialogBuilder.show();

        TestCoordinator.check(TTChecks.DIALOG_SHOWN);
    }
}
