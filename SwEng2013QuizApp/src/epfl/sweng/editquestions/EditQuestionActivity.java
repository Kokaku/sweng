package epfl.sweng.editquestions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.questions.QuizQuestion;
import epfl.sweng.servercomm.ServerCommunication;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

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

    private ArrayList<String> mAnswersArrayList;
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

        TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);

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
        String[] finalAnswers = mAnswersArrayList
                .toArray(new String[mAnswersArrayList.size()]);
        int correctAnswer = mAnswersAdapter.getCorrectAnswerPosition();
        Set<String> tagsSet = extractTags();
        
        QuizQuestion question = new QuizQuestion(questionText, finalAnswers,
                correctAnswer, tagsSet);
        ServerCommunication.send(question);

        resetScreen();

        TestingTransactions.check(TTChecks.NEW_QUESTION_SUBMITTED);

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

        TestingTransactions.check(TTChecks.QUESTION_EDITED);
        return true;
    }

    /**
     * Fill the tagsArray field with the tags currently in the mTagsEditText
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
            if (mOnReset == false) {
                mValidQuestion = !newText.toString().replaceAll("\\s+", "")
                        .equals("");
                mAnswersAdapter.setQuestionBodyValidity(mValidTags
                        && mValidQuestion);

                TestingTransactions.check(TTChecks.QUESTION_EDITED);
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

            if (mOnReset == false) {
                if (!newText.toString().replaceAll("\\W+", "").equals("")) {
                    mValidTags = true;
                } else {
                    mValidTags = false;
                }
                mAnswersAdapter.setQuestionBodyValidity(mValidTags
                        && mValidQuestion);

                TestingTransactions.check(TTChecks.QUESTION_EDITED);
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
}
