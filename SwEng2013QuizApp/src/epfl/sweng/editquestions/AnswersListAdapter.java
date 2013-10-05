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

/**
 * 
 * @author Zhivka Gucevska
 * 
 */
public class AnswersListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> answers;
    private AnswersListAdapter adapter = this;
    private int correctAnswerPosition;
    private boolean questionValidity;
    private Button submitButton;

    public AnswersListAdapter(Context contextArg, ArrayList<String> answersArg,
            Button submit) {
        super(contextArg, epfl.sweng.R.layout.rowlayout_view_list_answers,
                answersArg);
        this.context = contextArg;
        this.answers = answersArg;
        this.correctAnswerPosition = -1;
        this.questionValidity = false;
        this.submitButton = submit;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(
                epfl.sweng.R.layout.rowlayout_view_list_answers, parent, false);

        Button buttonCheckAnswer = (Button) rowView
                .findViewById(R.id.button_check_answer);

        Button buttonRemoveAnswer = (Button) rowView
                .findViewById(R.id.button_remove_answer);

        EditText editAnswer = (EditText) rowView.findViewById(R.id.edit_answer);

        if (answers.get(position).equals("")) {
            editAnswer.setHint(R.string.type_in_answer);
        } else {
            editAnswer.setText(answers.get(position));
        }

        if (correctAnswerPosition == position) {
            buttonCheckAnswer.setText(R.string.correct_answer);
        } else {
            buttonCheckAnswer.setText(R.string.wrong_answer);
        }

        buttonRemoveAnswer.setText(R.string.remove_answer);
        editAnswer.addTextChangedListener(new EditAnswerListener(position));

        buttonRemoveAnswer.setOnClickListener(new ButtonRemoveAnswerListener(
                position));

        buttonCheckAnswer.setOnClickListener(new ButtonCheckAnswerListener(
                position));

        return rowView;
    }

    public void clearAnswers() {
        correctAnswerPosition = -1;
        answers.clear();
        answers.add("");
        notifyDataSetChanged();
    }

    public int getCorrectAnswerPosition() {
        return correctAnswerPosition;
    }

    public int getValidAnswersCount() {
        int validAnswersNumber = 0;
        for (String answer : answers) {
            if (!answer.replaceAll("\\s+", "").equals("")) {
                validAnswersNumber++;
            }
        }
        return validAnswersNumber;
    }

    public void setQuestionValidity(boolean newQuestionValidity) {
        questionValidity = newQuestionValidity;
        updateSubmitButton();
    }

    public void updateSubmitButton() {
        if (getValidAnswersCount() >= 2 && correctAnswerPosition != -1
                && questionValidity == true
                && !answers.get(correctAnswerPosition).equals("")) {
            submitButton.setEnabled(true);
        } else {
            submitButton.setEnabled(false);
        }
    }

    /**
     * Listener for editAnswer EditText field
     * 
     */
    private class EditAnswerListener implements TextWatcher {
        private int editTextPosition;

        public EditAnswerListener(int position) {
            this.editTextPosition = position;
        }

        @Override
        public void afterTextChanged(Editable newText) {
            answers.set(editTextPosition, newText.toString());
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
     * Listener for buttonRemoveAnswer Button
     * 
     */
    private class ButtonRemoveAnswerListener implements View.OnClickListener {
        private int buttonPosition;

        public ButtonRemoveAnswerListener(int position) {
            this.buttonPosition = position;
        }

        @Override
        public void onClick(View v) {
            answers.remove(buttonPosition);
            if (buttonPosition == correctAnswerPosition) {
                correctAnswerPosition = -1;
            } else if (buttonPosition < correctAnswerPosition) {
                correctAnswerPosition--;
            }
            updateSubmitButton();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Listener for buttonCheckAnswer Button
     * 
     */
    private class ButtonCheckAnswerListener implements View.OnClickListener {
        private int buttonPosition;

        public ButtonCheckAnswerListener(int position) {
            this.buttonPosition = position;
        }

        @Override
        public void onClick(View v) {
            correctAnswerPosition = buttonPosition;
            updateSubmitButton();
            adapter.notifyDataSetChanged();

        }
    }
}