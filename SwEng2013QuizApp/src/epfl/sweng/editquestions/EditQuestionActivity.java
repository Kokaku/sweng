package epfl.sweng.editquestions;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
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

	private ArrayAdapter<String> adapterAnswers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_question);

		String[] tags = {};
		String[] answers = { "Bonjour", "Coucou","Saluuuuut","","","","Prout","" };

		//View view = adapterAnswers.getView(1, null, null);

		adapterAnswers = new AnswersListAdapter(this, answers);

		setListAdapter(adapterAnswers);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_question, menu);

		return true;
	}

	public boolean onClickSubmit() {
		String answerString = adapterAnswers.toString(); // To test
		System.out.println("answerString: " + answerString);
		return true;
	}

	public boolean onClickAdd() {
		// ListView listView = (ListView) findViewById(R.id.list_answers);
		// LinearLayout newLine = new LinearLayout(listView.getContext());
		// listView.addView(child, listView.getCount(), params);
		return true;
	}
}
