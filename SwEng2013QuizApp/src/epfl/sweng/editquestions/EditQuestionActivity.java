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
import android.widget.ListView;
import epfl.sweng.R;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.exceptions.AsyncTaskExceptions;
import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.patterns.Proxy;
import epfl.sweng.patterns.Proxy.ConnectionState;
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
    private Button mSubmitButton;
    private Button mAddButton;
    private AnswersListAdapter mAnswersAdapter;

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
        mAddButton = (Button) findViewById(R.id.button_add);
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

        QuizQuestion question = new QuizQuestion(questionText,
                mAnswersArrayList, correctAnswer, tagsSet);

        new SendQuestionTask().execute(question);

        resetScreen();
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
     * lunched.
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
     * @return the number of errors in the activity
     */
    public int auditErrors() {
        return auditEditTexts() + auditButtons() + auditAnswers();
    }

    /**
     * @return the number of errors in the EditText fields
     */
    private int auditEditTexts() {
        return auditEditTextQuestion() + auditEditTextAnswers()
                + auditEditTextTags();
    }

    /**
     * @return the number of errors in the buttons
     */
    private int auditButtons() {
        return auditPlusButton() + auditSubmitButton() + auditAnswersButtons();
    }

    /**
     * @return 1 if there is an error regarding the question EditText
     */
    private int auditEditTextQuestion() {
        return (mQuestionEditText == null
                || !mQuestionEditText.getHint().equals(
                        "Type in the question's text body") || mQuestionEditText
                        .getVisibility() != 0) ? 1 : 0;
    }

    /**
     * @return the number of errors in the answers listView
     */
    private int auditEditTextAnswers() {
        ListView listView = getListView();

        if (listView == null) {
            return 1;
        } else {
            int errors = 0;

            for (int i = 0; i < listView.getCount(); ++i) {
                EditText answerEditText = (EditText) listView.getChildAt(i)
                        .findViewById(R.id.edit_answer);
                errors += (!answerEditText.getHint().equals(
                        "Type in the answer") || answerEditText.getVisibility() != 0) ? 1
                        : 0;
            }
            return errors;
        }
    }

    /**
     * @return 1 if there is an error regarding the tags EditText
     */
    private int auditEditTextTags() {
        return (mTagsEditText == null
                || !mTagsEditText.getHint().equals(
                        "Type in the question's tags") || mTagsEditText
                        .getVisibility() != 0) ? 1 : 0;
    }

    /**
     * @return 1 if there is an error regarding the addAnswer Button
     */
    private int auditPlusButton() {
        return (mAddButton == null || !mAddButton.getText().equals("\u002B") || mAddButton
                .getVisibility() != 0) ? 1 : 0;
    }

    /**
     * @return 1 if there is an error regarding the submit Button
     */
    private int auditSubmitButton() {
        return (mSubmitButton == null
                || !mSubmitButton.getText().equals("Submit") || mSubmitButton
                    .getVisibility() != 0) ? 1 : 0;
    }

    /**
     * @return the number of errors regarding the buttons in the answer ListView
     */
    private int auditAnswersButtons() {
        int errors = 0;
        ListView listView = getListView();

        for (int i = 0; i < listView.getCount(); ++i) {
            View listElement = listView.getChildAt(i);
            Button checkAnswerButton = (Button) listElement
                    .findViewById(R.id.button_check_answer);
            Button removeButton = (Button) listElement
                    .findViewById(R.id.button_remove_answer);

            errors += (checkAnswerButton == null
                    || (!checkAnswerButton.getText().equals("\u2718") && !checkAnswerButton
                            .getText().equals("\u2714")) || checkAnswerButton
                            .getVisibility() != 0) ? 1 : 0;

            errors += (removeButton == null
                    || !removeButton.getText().equals("\u002D") || removeButton
                    .getVisibility() != 0) ? 1 : 0;
        }

        return errors;
    }
    
    /**
     * @return 1 if the number of correct answer is different than 1
     */
    private int auditAnswers() {
        int correctAnswersCount = 0;
        ListView listView = getListView();
        
        for (int i = 0; i < listView.getCount(); ++i) {
            Button checkAnswerButton = (Button) listView.getChildAt(i).findViewById(R.id.button_check_answer);
            correctAnswersCount += (checkAnswerButton.getText().equals("\u2714"))? 1 : 0;
        }
        
        return (correctAnswersCount != 1)? 1: 0;
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
                mValidTags = !newText.toString().replaceAll("\\W+", "")
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
     * Sends a new question in a separate thread.
     */    
    private class SendQuestionTask extends AsyncTask<QuizQuestion, Void, QuizQuestion> {
        
        private AsyncTaskExceptions mException = null;

        @Override
        protected QuizQuestion doInBackground(QuizQuestion... questions) {
            try {
                Proxy.INSTANCE.send(questions[0]);
            } catch (NotLoggedInException e) {
                mException = AsyncTaskExceptions.NOT_LOGGED_IN_EXCEPTION;
            } catch (DBException e) {
                mException = AsyncTaskExceptions.DB_EXCEPTION;
            } catch (ServerCommunicationException e) {
                mException = AsyncTaskExceptions.SERVER_COMMUNICATION_EXCEPTION;
            }

            return questions[0];
        }

        @Override
        protected void onPostExecute(QuizQuestion question) {
            if (mException == null) {
                if (!Proxy.INSTANCE.isOnline()) {
                    SwEng2013QuizApp.displayToast(R.string.question_cached);
                }
            } else {
                switch (mException) {
                    case NOT_LOGGED_IN_EXCEPTION:
                        SwEng2013QuizApp.displayToast(R.string.not_logged_in);
                        break;
                    case SERVER_COMMUNICATION_EXCEPTION:
                        SwEng2013QuizApp.displayToast(R.string.failed_to_send_question);
                        Proxy.INSTANCE.setState(ConnectionState.OFFLINE);
                        SwEng2013QuizApp.displayToast(R.string.now_offline);
                        // Send it again to cache the question
                        new SendQuestionTask().execute(question);
                        break;
                    case DB_EXCEPTION:
                        SwEng2013QuizApp.displayToast(R.string.failed_to_cache_question);
                        break;
                    default:
                        assert false;
                }
            }
            TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
        }
        
    }

}
