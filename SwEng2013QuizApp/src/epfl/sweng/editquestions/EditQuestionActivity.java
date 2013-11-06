package epfl.sweng.editquestions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.exceptions.CommunicationException;
import epfl.sweng.exceptions.DBCommunicationException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.patterns.Proxy;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * This activity displays empty fields that let the user create a new question
 * by setting the question text, a set of tags, a set of possible answers and
 * the right answer.
 * 
 * @author MathieuMonney
 * 
 */

public class EditQuestionActivity extends ListActivity {

    private EditText mQuestionEditText;
    private EditText mTagsEditText;
    private AnswersListAdapter mAnswersAdapter;
    private Button mSubmitButton;

    private List<String> mAnswersArrayList;
    private boolean mValidQuestion;
    private boolean mValidTags;

    private boolean mOnReset;

    /**
     * Initialization of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        mAnswersArrayList = new ArrayList<String>();
        mAnswersArrayList.add("");

        mQuestionEditText = (EditText) findViewById(R.id.new_text_question);
        mTagsEditText = (EditText) findViewById(R.id.new_tags);
        mSubmitButton = (Button) findViewById(R.id.button_submit);
        mAnswersAdapter = new AnswersListAdapter(this, mAnswersArrayList,
                mSubmitButton);

        mValidQuestion = false;
        mValidTags = false;
        mOnReset = false;

        mQuestionEditText
                .addTextChangedListener(new QuestionEditTextListener());
        mTagsEditText.addTextChangedListener(new TagsEditTextListener());

        setListAdapter(mAnswersAdapter);

        TestCoordinator.check(TTChecks.EDIT_QUESTIONS_SHOWN);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_question, menu);

        return true;
    }

    /**
     * Extracts the tags and answers from their respective EditText, creates a
     * QuizQuestion and send it to the server when mSubmitButton is clicked.
     */
    public boolean onClickSubmit(View view) {
        mOnReset = true;
        String questionText = mQuestionEditText.getText().toString();
               
        int correctAnswer = mAnswersAdapter.getCorrectAnswerPosition();
        Set<String> tagsSet = extractTags();

        QuizQuestion question = new QuizQuestion(questionText, mAnswersArrayList,
                correctAnswer, tagsSet);
                
        new SendQuestionTask().execute(question);
   
        resetScreen();
        TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
        mOnReset = false;

        return true;
    }

    /**
     * Add a new answer by adding an empty answer in the mAnswersArrayList when
     * addButton is clicked.
     */
    public boolean onClickAdd(View view) {
        mAnswersArrayList.add("");
        mAnswersAdapter.notifyDataSetChanged();
        mSubmitButton.setEnabled(false);

        TestCoordinator.check(TTChecks.QUESTION_EDITED);
        return true;
    }

    /**
     * Returns a new Set containing the tags currently in the mTagsEditText
     * EditText.
     */
    private Set<String> extractTags() {
        String[] tagsArray = cleanStartOfString(
                mTagsEditText.getText().toString()).split("\\W+");
        return new TreeSet<String>(Arrays.asList(tagsArray));
    }

    /**
     * Removes all none alphanumeric characters at the beginning of the string
     * as well as the spaces at the end of the string.
     */
    private String cleanStartOfString(String charSequence) {
        return charSequence.replaceAll("^\\W+", "").trim();
    }

    /**
     * Reset all the components of the UI as they were when the application was
     * lunch.
     */
    private void resetScreen() {
        mAnswersAdapter.clearAnswers();
        mTagsEditText.setText("");
        mQuestionEditText.setText("");
        mSubmitButton.setEnabled(false);

        mValidQuestion = false;
        mValidTags = false;

        mAnswersAdapter.notifyDataSetChanged();
    }

    /**
     * Listener for mQuestionEditText. Checks if the mQuestionEditText field has
     * at least one none space character.
     */
    private class QuestionEditTextListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable newText) {
            if (!mOnReset) {
                mValidQuestion = !newText.toString().replaceAll("\\s+", "")
                        .equals("");
                mAnswersAdapter.setQuestionBodyValidity(mValidTags
                        && mValidQuestion);

                TestCoordinator.check(TTChecks.QUESTION_EDITED);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {
        }
    }

    /**
     * Listener for mTagsEditText. Checks if the mTagsEditText field has at
     * least one alphanumeric character.
     */
    private class TagsEditTextListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable newText) {

            if (!mOnReset) {
                mValidTags = !newText.toString()
                        .replaceAll("\\W+", "").equals("");
                mAnswersAdapter.setQuestionBodyValidity(mValidTags
                        && mValidQuestion);

                TestCoordinator.check(TTChecks.QUESTION_EDITED);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {

        }
    }
    
    /**
     * Sends a new question in a separate thread.
     */    
    private class SendQuestionTask extends AsyncTask<QuizQuestion, Void, Void> {
        
        private Exception mException = null;
                
        @Override
        protected Void doInBackground(QuizQuestion... questions) {
            try {
                Proxy.INSTANCE.send(questions[0]);
            } catch (NotLoggedInException e) {
                mException = e;
            } catch (CommunicationException e) {
                mException = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (mException == null) {
                SwEng2013QuizApp.displayToast(R.string.question_sent);
            } else {
                if (mException instanceof NotLoggedInException) {
                    SwEng2013QuizApp.displayToast(R.string.not_logged_in);
                } else if (mException instanceof ServerCommunicationException) {
                    SwEng2013QuizApp.displayToast(R.string.failed_to_send_question);
                    TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
                } else if (mException instanceof DBCommunicationException) {
                    SwEng2013QuizApp.displayToast(R.string.failed_to_cache_question);
                }
            }
        }

    }
}
