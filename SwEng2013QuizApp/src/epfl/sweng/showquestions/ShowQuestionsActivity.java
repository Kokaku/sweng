package epfl.sweng.showquestions;

import java.util.Set;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import epfl.sweng.R;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.exceptions.CommunicationException;
import epfl.sweng.exceptions.DBCommunicationException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.patterns.Proxy;
import epfl.sweng.patterns.Proxy.ConnectionState;
import epfl.sweng.quizquestions.QuizQuestion;
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
    private ProgressBar mProgressBar;
    private Vibrator mVibrator;
    private int mAnimationDuration;
    
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
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_questions);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mAnimationDuration = getResources().getInteger(
            android.R.integer.config_shortAnimTime);
        
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
        
        mSymbol.setAlpha(0f);
        mSymbol.setVisibility(View.VISIBLE);
        mSymbol.animate().alpha(1f)
                         .setDuration(mAnimationDuration)   
                         .setListener(null);
        
        mSymbol.postDelayed(new Runnable() {
            public void run() {
                mSymbol.animate().alpha(0f)
                                 .setDuration(mAnimationDuration)
                                 .setListener(new AnimatorListenerAdapter() {
                                     @Override
                                     public void onAnimationEnd(Animator animation) {
                                         mSymbol.setVisibility(View.GONE);
                                         
                                         if (correctAnswer) {
                                             mNextButton.setEnabled(true);
                                         } else {
                                             getListView().setEnabled(true);
                                         }
                                         TestCoordinator.check(TTChecks.ANSWER_SELECTED);
                                     }
                                 });
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
          
    private void showNewQuestion() {
        new GetQuestionTask().execute();
    }
    
    private void showViews() {
        mTagsList.setVisibility(View.VISIBLE);
        mQuestionText.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.VISIBLE);
        getListView().setVisibility(View.VISIBLE);
    }
    
    private void hideViews() {
        mTagsList.setVisibility(View.GONE);
        mQuestionText.setVisibility(View.GONE);
        mNextButton.setVisibility(View.GONE);
        getListView().setVisibility(View.GONE);
    }
    
    private void updateViews() {
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
    
    /**
     * Retrieves a new question in a separate thread then sets the views so
     * that the question is displayed.
     */
    private class GetQuestionTask extends AsyncTask<Void, Void, QuizQuestion> {
        
        private Exception mException = null;
        
        @Override
        protected void onPreExecute() {
            hideViews();
            mProgressBar.setVisibility(View.VISIBLE);
        }
        
        @Override
        protected QuizQuestion doInBackground(Void... unused) {
            try {
                return Proxy.INSTANCE.getRandomQuestion();
            } catch (NotLoggedInException e) {
                mException = e;
            } catch (CommunicationException e) {
                mException = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(QuizQuestion question) {
            mProgressBar.setVisibility(View.GONE);
            
            if (mException == null) {
                if (question != null) {
                    showViews();
                    mCurrentQuestion = question;
                    updateViews();
                } else {
                    SwEng2013QuizApp.displayToast(R.string.no_cached_question);
                }
            } else {
                if (mException instanceof NotLoggedInException) {
                    SwEng2013QuizApp.displayToast(R.string.not_logged_in);
                } else if (mException instanceof ServerCommunicationException) {
                    SwEng2013QuizApp.displayToast(R.string.failed_to_get_question);
                    Proxy.INSTANCE.setState(ConnectionState.OFFLINE);
                    SwEng2013QuizApp.displayToast(R.string.now_offline);
                    showNewQuestion();
                } else if (mException instanceof DBCommunicationException) {
                    if (Proxy.INSTANCE.isOnline()) {
                        SwEng2013QuizApp.displayToast(R.string.failed_to_cache_question);
                    } else {
                        SwEng2013QuizApp.displayToast(R.string.broken_database);
                    }
                }
                TestCoordinator.check(TTChecks.QUESTION_SHOWN);
            }
        }

    }
}
