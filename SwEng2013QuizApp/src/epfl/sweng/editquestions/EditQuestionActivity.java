package epfl.sweng.editquestions;

import java.util.ArrayList;
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

    private EditText questionEditText;
    private EditText tagsEditText;
    private AnswersListAdapter answersAdapter;
    private Button submitButton;

    private String questionText;
    private Set<String> tagsSet;
    private ArrayList<String> answersArrayList;
    private int correctAnswerPosition;
    private String[] finalAnswers;
    private boolean hasValidQuestion;
    private boolean hasValidTags;

    private boolean onReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        answersArrayList = new ArrayList<String>();
        answersArrayList.add("");

        questionEditText = (EditText) findViewById(R.id.new_text_question);
        tagsEditText = (EditText) findViewById(R.id.new_tags);
        submitButton = (Button) findViewById(R.id.button_submit);
        answersAdapter = new AnswersListAdapter(this, answersArrayList,
                submitButton);

        tagsSet = new TreeSet<String>();
        hasValidQuestion = false;
        hasValidTags = false;
        onReset = false;

        questionEditText.addTextChangedListener(new QuestionEditTextListener());
        tagsEditText.addTextChangedListener(new TagsEditTextListener());

        setListAdapter(answersAdapter);

        TestingTransactions.check(TTChecks.EDIT_QUESTIONS_SHOWN);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_question, menu);

        return true;
    }

    public boolean onClickSubmit(View view) {
        onReset = true;

        questionText = cleanStartOfString(questionEditText.getText().toString());
        extractFinalAnswers(answersArrayList,
                answersAdapter.getCorrectAnswerPosition());
        extractTags();

        QuizQuestion question = new QuizQuestion(questionText, finalAnswers,
                correctAnswerPosition, tagsSet);
        ServerCommunication.send(question);

        resetScreen();
        
        TestingTransactions.check(TTChecks.NEW_QUESTION_SUBMITTED);

        onReset = false;

        // faire un truc si faux
        return true;
    }

    public boolean onClickAdd(View view) {
        answersArrayList.add("");
        answersAdapter.notifyDataSetChanged();
        submitButton.setEnabled(false);

        TestingTransactions.check(TTChecks.QUESTION_EDITED);
        return true;
    }
    
    public boolean onClickClear(View view) {
        resetScreen();

        TestingTransactions.check(TTChecks.QUESTION_EDITED);
        return true;
    }

    private void extractFinalAnswers(ArrayList<String> argAnswers,
            int relativeCorrectAnswerPosition) {
        int size = answersAdapter.getCount();
        finalAnswers = new String[size];
        int validAnswersCount = 0;
        for (int i = 0; i < answersArrayList.size(); i++) {
            if (!answersArrayList.get(i).replaceAll("\\s+", "").equals("")) {
                finalAnswers[validAnswersCount] = cleanStartOfString(answersArrayList
                        .get(i));
                if (i == relativeCorrectAnswerPosition) {
                    correctAnswerPosition = validAnswersCount;
                }
                validAnswersCount++;
            }
        }
    }

    private void extractTags() {
        String[] tagsArray = cleanStartOfString(
                tagsEditText.getText().toString()).split("\\W+");
        for (String tag : tagsArray) {
            System.out.println(tag);
            tagsSet.add(tag);
        }
    }

    // removes all non alphanumeric characters in the beginning of the string
    private String cleanStartOfString(String charSequence) {
        return charSequence.replaceAll("^\\W+", "").trim();
    }

    private void resetScreen() {
        answersAdapter.clearAnswers();
        tagsSet.clear();
        tagsEditText.setText("");
        questionText = "";
        questionEditText.setText("");
        submitButton.setEnabled(false);

        hasValidQuestion = false;
        hasValidTags = false;

        answersAdapter.notifyDataSetChanged();
    }

    /**
     * Listener for questionEditText
     * 
     */
    private class QuestionEditTextListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable newText) {
            if (onReset == false) {
                System.out.println("Question field edited");

                if (!questionEditText.getText().toString()
                        .replaceAll("\\s+", "").equals("")) {
                    hasValidQuestion = true;
                } else {
                    hasValidQuestion = false;
                }
                answersAdapter.setQuestionBodyValidity(hasValidTags
                        && hasValidQuestion);

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
     * Listener for tagsEditText
     * 
     */
    private class TagsEditTextListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable newText) {

            if (onReset == false) {
                System.out.println("Tags field edited");

                if (!tagsEditText.getText().toString().replaceAll("\\s+", "")
                        .equals("")) {
                    hasValidTags = true;
                } else {
                    hasValidTags = false;
                }
                answersAdapter.setQuestionBodyValidity(hasValidTags
                        && hasValidQuestion);

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
