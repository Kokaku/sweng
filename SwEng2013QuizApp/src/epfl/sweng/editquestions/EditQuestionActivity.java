package epfl.sweng.editquestions;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import epfl.sweng.R;

public class EditQuestionActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_question);
		
		String[] tags = {};
		String[] answers = {};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_question, menu);
		return true;
	}
	
//	public boolean onClickSubmit() {
//		ListView listView = (ListView) findViewById(R.id.list_answers);
//		ListAdapter listAdapter = listView.getAdapter();
//		String answerString = listAdapter.toString(); // To test
//		ArrayList<String> answers = new ArrayList<String>();
//		int answersNumber = listAdapter.getCount();
//		for (int i = 0; i<answersNumber; ++i) {
//			answers.add(((TextView) listAdapter.getView(i, null, listView)).getText().toString());
//		}
//		return true;
//	}
//	
//	public boolean onClickAdd() {
//	    ListView listView = (ListView) findViewById(R.id.list_answers);
//	    LinearLayout newLine = new LinearLayout(listView.getContext());
//	    //listView.addView(child, listView.getCount(), params);
//		return true;
//	}
}
