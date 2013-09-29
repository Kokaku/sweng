package epfl.sweng.showquestions;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import epfl.sweng.R;

/**
 * 
 * @author lseguy
 *
 */
public class ShowQuestionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_questions);
		
		String[] answers = {"Banana", "Potato", "It's so fluffy!", "Gnaaaah", "Foo", "Bar", "Blah", "Pouet"};

		TextView question = (TextView) findViewById(R.id.text_question);
		question.setMovementMethod(new ScrollingMovementMethod());
		
		ArrayAdapter<String> adapterAnswers = new ArrayAdapter<String>(this,
		android.R.layout.simple_list_item_1, answers);
		ListView answersList = (ListView) findViewById(R.id.list_answers);
		answersList.setAdapter(adapterAnswers);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_questions, menu);
		return true;
	}

}
