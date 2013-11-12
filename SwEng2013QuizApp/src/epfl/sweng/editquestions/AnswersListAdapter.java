package epfl.sweng.editquestions;

import java.util.List;

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
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Adapter for the ListView that manages the EditText, and Buttons for removing
 * answers and checking as correct. It also enables the submit Button when the
 * required conditions are satisfied.
 * 
 * @author Zhivka Gucevska
 * 
 */
public class AnswersListAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final List<String> mAnswersArrayList;
    private int mCorrectAnswerPosition;
    private boolean mQuestionBodyValidity;
    private Button mSubmitButton;

    /**
     * Public constructor for the AnswersListAdapter
     * 
     * @param contextArg
     * @param answersArg
     * @param submit
     */
    public AnswersListAdapter(Context contextArg, List<String> answersArg,
            Button submit) {
        super(contextArg, epfl.sweng.R.layout.rowlayout_view_list_answers,
                answersArg);
        this.mContext = contextArg;
        this.mAnswersArrayList = answersArg;
        this.mCorrectAnswerPosition = -1;
        this.mQuestionBodyValidity = false;
        this.mSubmitButton = submit;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
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

        if (mAnswersArrayList.get(position).equals("")) {
            answerEditText.setHint(R.string.type_in_answer);
        } else {
            answerEditText.setText(mAnswersArrayList.get(position));
        }

        if (mCorrectAnswerPosition == position) {
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

    /**
     * Clears the all the answers
     */
    public void clearAnswers() {
        mCorrectAnswerPosition = -1;
        mAnswersArrayList.clear();
        mAnswersArrayList.add("");
        notifyDataSetChanged();
    }

    /**
     * @return position of the correct answer, -1 if there's no correct answer
     */
    public int getCorrectAnswerPosition() {
        return mCorrectAnswerPosition;
    }

    /**
     * Checks if the list of answers contains only valid answers.
     * 
     * @return true if all the answers in the array are valid
     */
    public boolean hasOnlyValidAnswers() {
        for (String answer : mAnswersArrayList) {
            if (answer.replaceAll("\\s+", "").equals("")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Setter for the state of the Question's body: questionValidity is true
     * only if the question text is non empty and tags contains some
     * alpha-numeric characters
     * 
     * @param newQuestionBodyValidity
     */
    public void setQuestionBodyValidity(boolean newQuestionBodyValidity) {
        mQuestionBodyValidity = newQuestionBodyValidity;
        updateSubmitButton();
    }

    /**
     * Updates the submit button
     */
    public void updateSubmitButton() {
        if (mCorrectAnswerPosition >= 0 && mQuestionBodyValidity
                && mAnswersArrayList.size() >= 2 && hasOnlyValidAnswers()) {
            mSubmitButton.setEnabled(true);
        } else {
            mSubmitButton.setEnabled(false);
        }
    }

    /**
     * Listener for answerEditText field
     */
    private class AnswerEditTextListener implements TextWatcher {
        private int editTextPosition;

        public AnswerEditTextListener(int position) {
            this.editTextPosition = position;
        }

        @Override
        public void afterTextChanged(Editable newText) {
            mAnswersArrayList.set(editTextPosition, newText.toString());
            updateSubmitButton();
            TestCoordinator.check(TTChecks.QUESTION_EDITED);
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
     * Listener for removeAnswerButton field
     * 
     */
    private class RemoveAnswerButtonListener implements View.OnClickListener {
        private int buttonPosition;
        private AnswersListAdapter answersAdapter;

        public RemoveAnswerButtonListener(int position,
                AnswersListAdapter adapter) {
            this.buttonPosition = position;
            this.answersAdapter = adapter;
        }

        @Override
        public void onClick(View v) {
            mAnswersArrayList.remove(buttonPosition);
            if (buttonPosition == mCorrectAnswerPosition) {
                mCorrectAnswerPosition = -1;
            } else if (buttonPosition < mCorrectAnswerPosition) {
                mCorrectAnswerPosition--;
            }
            updateSubmitButton();
            answersAdapter.notifyDataSetChanged();
            TestCoordinator.check(TTChecks.QUESTION_EDITED);
        }
    }

    /**
     * Listener for checkAnswerButton field
     * 
     */
    private class CheckAnswerButtonListener implements View.OnClickListener {
        private int buttonPosition;
        private AnswersListAdapter answersAdapter;

        public CheckAnswerButtonListener(int position,
                AnswersListAdapter adapter) {
            this.buttonPosition = position;
            this.answersAdapter = adapter;
        }

        @Override
        public void onClick(View v) {
            if (mCorrectAnswerPosition == buttonPosition) {
                mCorrectAnswerPosition = -1;
            } else {
                mCorrectAnswerPosition = buttonPosition;
            }
            updateSubmitButton();
            answersAdapter.notifyDataSetChanged();
            TestCoordinator.check(TTChecks.QUESTION_EDITED);
        }
    }
}