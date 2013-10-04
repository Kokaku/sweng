package epfl.sweng.editquestions;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;

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
	private EditText questionText;
	private EditText tagsText;
	
	

	// private String[] tags;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_question);

		answers = new ArrayList<String>();
		answers.add("");
		
		buttonSubmit = (Button) findViewById(R.id.button_submit);
		questionText = (EditText) findViewById(R.id.new_text_question);
		tagsText = (EditText) findViewById(R.id.new_tags);
		adapterAnswers = new AnswersListAdapter(this, answers);
		setListAdapter(adapterAnswers);
		// this.getListView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_question, menu);

		return true;
	}

	public boolean onClickSubmit(View view) {
		String answerString = adapterAnswers.toString(); // To test
		System.out.println("answerString: " + answerString);
		return true;
	}

	public boolean onClickAdd(View view) {
		answers.add("");
		adapterAnswers.notifyDataSetChanged();
		return true;
	}
	
	public boolean onClickClear(View view) {
		adapterAnswers.clearAnswers();
		buttonSubmit.setClickable(false);
		return true;
	}
	
	
	

}
