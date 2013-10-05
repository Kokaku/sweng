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

/**
 * This activity displays empty fields that let the user create a new question
 * by setting the question text, a set of tags, a set of possible answers and
 * the right answer.
 * 
 * @author MathieuMonney
 * 
 */

public class EditQuestionActivity extends ListActivity {

    private AnswersListAdapter adapterAnswers;
    private ArrayList<String> answers;
    private Button buttonSubmit;
    private EditText questionEditText;
    private EditText tagsText;

    private String questionText;
    private Set<String> tags;
    private int correctAnswerPosition;
    private String[] finalAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        answers = new ArrayList<String>();
        answers.add("");

        buttonSubmit = (Button) findViewById(R.id.button_submit);
        questionEditText = (EditText) findViewById(R.id.new_text_question);
        tagsText = (EditText) findViewById(R.id.new_tags);
        adapterAnswers = new AnswersListAdapter(this, answers, buttonSubmit);

        setListAdapter(adapterAnswers);

        System.out.println(questionEditText);
        questionEditText.addTextChangedListener(new QuestionEditTextListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_question, menu);

        return true;
    }

    public boolean onClickSubmit(View view) {
        questionText = cleanStartOfString(questionEditText.getText().toString());
        extractFinalAnswers(answers, adapterAnswers.getCorrectAnswerPosition());
        extractTags();

        QuizQuestion question = new QuizQuestion(questionText, finalAnswers,
                correctAnswerPosition, tags);
        ServerCommunication.send(question);

        adapterAnswers.clearAnswers();
        tags.clear();
        tagsText.setText("");
        questionText = "";
        questionEditText.setText("");
        buttonSubmit.setEnabled(false);
        adapterAnswers.notifyDataSetChanged();
        // faire un truc si faux
        return true;
    }

    public boolean onClickAdd(View view) {
        answers.add("");
        adapterAnswers.notifyDataSetChanged();
        return true;
    }

    public boolean onClickClear(View view) {
        adapterAnswers.clearAnswers();
        buttonSubmit.setEnabled(false);
        return true;
    }

    private void extractFinalAnswers(ArrayList<String> argAnswers,
            int relativeCorrectAnswerPosition) {
        int size = adapterAnswers.getValidAnswersCount();
        finalAnswers = new String[size];
        int validAnswersCount = 0;
        for (int i = 0; i < answers.size(); i++) {
            if (!answers.get(i).replaceAll("\\s+", "").equals("")) {
                finalAnswers[validAnswersCount] = cleanStartOfString(answers
                        .get(i));
                System.out.println(answers.get(i));
                if (i == relativeCorrectAnswerPosition) {
                    correctAnswerPosition = validAnswersCount;
                }
                validAnswersCount++;
            }
        }
        System.out.println("correct postiton " + correctAnswerPosition);
    }

    private void extractTags() {
        tags = new TreeSet<String>();
        String[] tagsArray = cleanStartOfString(tagsText.getText().toString())
                .split("\\W+");
        for (String tag : tagsArray) {
            System.out.println(tag);
            tags.add(tag);
        }

        System.out.println(tags.toString());
    }

    // removes all non alphanumeric characters in the beginning of the string
    private String cleanStartOfString(String charSequence) {
        return charSequence.replaceAll("^\\W+", "");
    }

    /**
     * Listener for questionEditText
     * 
     */
    private class QuestionEditTextListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable newText) {

            if (!questionEditText.getText().toString().replaceAll("\\s+", "")
                    .equals("")) {
                adapterAnswers.setQuestionValidity(true);
            } else {
                adapterAnswers.setQuestionValidity(false);
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
