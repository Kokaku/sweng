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

	// private String[] tags;

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
		questionEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable newText) {

				if (!questionEditText.getText().toString()
						.replaceAll("\\s+", "").equals("")) {
					adapterAnswers.setQuestionValidity(true);
				} else {
					adapterAnswers.setQuestionValidity(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_question, menu);

		return true;
	}

	public boolean onClickSubmit(View view) {
		questionText = removeExtraSpaces(questionEditText.getText().toString());
		extractFinalAnswers(answers, adapterAnswers.getCorrectAnswerPosition());
		extractTags();
		
		QuizQuestion question = new QuizQuestion(questionText, finalAnswers, correctAnswerPosition, tags);
		boolean sendSuccess = ServerCommunication.send(question);
		System.out.println(sendSuccess);
//		adapterAnswers.clear();
//		tags.clear();
//		questionText = "";
//		questionEditText.setHint(R.string.type_in_question);
//		buttonSubmit.setEnabled(false);
		//faire un truc si faux
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
				finalAnswers[validAnswersCount] = removeExtraSpaces(answers.get(i));
				System.out.println(answers.get(i));
				if (i == relativeCorrectAnswerPosition) { 	
					correctAnswerPosition = validAnswersCount;
				}
				validAnswersCount++;
			}
		}
		printFinalAnswers();
		System.out.println("correct postiton " + correctAnswerPosition);
	}

	/**
     * 
     */
    private void printFinalAnswers() {
        for (int i = 0; i < finalAnswers.length; ++i) {
            System.out.println("Array index " + i + ": " + finalAnswers[i]);
        }
    }

    private void extractTags() {
		tags = new TreeSet<String>();
		String[] tagsArray = removeExtraSpaces(tagsText.getText().toString()).split("\\W+");
		for (String tag : tagsArray) {
			System.out.println(tag);
			tags.add(tag);
		}
		
		System.out.println(tags.toString());
	}
	
	
	// removes all spaces at the begining and the end of the string
	private String removeExtraSpaces(String charSequence) {
	    return charSequence.trim();
	}
}
