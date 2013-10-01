package epfl.sweng.editquestions;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import epfl.sweng.R;

public class EditQuestionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_question);
		
		String[] tags = {};
		String answers[] = {};
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_question, menu);
		return true;
	}
	
//	public boolean onClickSubmit() {
//		ListView listView = (ListView) findViewById(R.id.list_new_answers);
//		ListAdapter listAdapter = listView.getAdapter();
//		List<String> answers;
//		int answersNumber = listAdapter.getCount();
//		for(int i = 0; i<answersNumber; ++i){
//			answers.add(listAdapter.getView(i, null, listView));
//			
//		}
//		
//	}
	
//	public boolean onClickAdd() {
//		
//		return true;
//	}

}
