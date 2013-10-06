package epfl.sweng.editquestions;

import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
 * 
 * @author Zhivka Gucevska
 * 
 */
public class AnswersListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> answersArrayList;
    private int correctAnswerPosition;
    private boolean questionBodyValidity;
    private Button submitButton;

    public AnswersListAdapter(Context contextArg, ArrayList<String> answersArg,
            Button submit) {
        super(contextArg, epfl.sweng.R.layout.rowlayout_view_list_answers,
                answersArg);
        this.context = contextArg;
        this.answersArrayList = answersArg;
        this.correctAnswerPosition = -1;
        this.questionBodyValidity = false;
        this.submitButton = submit;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(
                epfl.sweng.R.layout.rowlayout_view_list_answers, parent, false);

        Button checkAnswerButton = (Button) rowView
                .findViewById(R.id.button_check_answer);

        Button removeAnswerButton = (Button) rowView
                .findViewById(R.id.button_remove_answer);

        EditText answerEditText = (EditText) rowView
                .findViewById(R.id.edit_answer);

        removeAnswerButton.setText(R.string.remove_answer);

        if (answersArrayList.get(position).equals("")) {
            answerEditText.setHint(R.string.type_in_answer);
        } else {
            answerEditText.setText(answersArrayList.get(position));
        }

        if (correctAnswerPosition == position) {
            checkAnswerButton.setText(R.string.correct_answer);
        } else {
            checkAnswerButton.setText(R.string.wrong_answer);
        }

        checkAnswerButton.setOnClickListener(new CheckAnswerButtonListener(
                position, this));

        answerEditText.addTextChangedListener(new AnswerEditTextListener(
                position));

        removeAnswerButton.setOnClickListener(new RemoveAnswerButtonListener(
                position, this));

        return rowView;
    }

    public void clearAnswers() {
        correctAnswerPosition = -1;
        answersArrayList.clear();
        answersArrayList.add("");
        notifyDataSetChanged();
    }

    public int getCorrectAnswerPosition() {
        return correctAnswerPosition;
    }

    public boolean hasOnlyValidAnswers() {
        for (String answer : answersArrayList) {
            if (answer.replaceAll("\\s+", "").equals("")) {
                return false;
            }
        }
        return true;
    }

    public void setQuestionBodyValidity(boolean newQuestionBodyValidity) {
        questionBodyValidity = newQuestionBodyValidity;
        updateSubmitButton();
    }

    public void updateSubmitButton() {
        if (correctAnswerPosition != -1 && questionBodyValidity == true
                && answersArrayList.size() >= 2 && hasOnlyValidAnswers()) {
            submitButton.setEnabled(true);
        } else {
            submitButton.setEnabled(false);
        }
    }

    /**
     * Listener for answerEditText EditText field
     * 
     */
    private class AnswerEditTextListener implements TextWatcher {
        private int editTextPosition;

        public AnswerEditTextListener(int position) {
            this.editTextPosition = position;
        }

        @Override
        public void afterTextChanged(Editable newText) {
            answersArrayList.set(editTextPosition, newText.toString());
            TestingTransactions.check(TTChecks.QUESTION_EDITED);
            updateSubmitButton();
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
     * Listener for removeAnswerButton Button
     * 
     */
    private class RemoveAnswerButtonListener implements View.OnClickListener {
        private int buttonPosition;
        private AnswersListAdapter answersAdapter;

        public RemoveAnswerButtonListener(int position, AnswersListAdapter adapter) {
            this.buttonPosition = position;
            this.answersAdapter = adapter;
        }

        @Override
        public void onClick(View v) {
            answersArrayList.remove(buttonPosition);
            if (buttonPosition == correctAnswerPosition) {
                correctAnswerPosition = -1;
            } else if (buttonPosition < correctAnswerPosition) {
                correctAnswerPosition--;
            }
            updateSubmitButton();
            answersAdapter.notifyDataSetChanged();
            TestingTransactions.check(TTChecks.QUESTION_EDITED);
        }
    }

    /**
     * Listener for checkAnswerButton Button
     * 
     */
    private class CheckAnswerButtonListener implements View.OnClickListener {
        private int buttonPosition;
        private AnswersListAdapter answersAdapter;

        public CheckAnswerButtonListener(int position, AnswersListAdapter adapter) {
            this.buttonPosition = position;
            this.answersAdapter = adapter;
        }

        @Override
        public void onClick(View v) {
            if (correctAnswerPosition != buttonPosition) {
                correctAnswerPosition = buttonPosition;
                updateSubmitButton();
                answersAdapter.notifyDataSetChanged();
                TestingTransactions.check(TTChecks.QUESTION_EDITED);
            }
        }
    }
}