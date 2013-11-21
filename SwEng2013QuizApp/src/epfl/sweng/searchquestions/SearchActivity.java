package epfl.sweng.searchquestions;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class SearchActivity extends Activity {
	private Button mSearchButton;
	private EditText mSearchQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		mSearchButton = (Button) findViewById(R.id.search_button);
		mSearchQuery = (EditText) findViewById(R.id.search_query);
		
		mSearchQuery.addTextChangedListener(new QueryEditTextListener());

		TestCoordinator.check(TTChecks.SEARCH_ACTIVITY_SHOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	public void onClickSearch(View view) {
		//start AsynkTask that will fetch the questions
	}

	/**
	 * Listener for mSearchQuery. Checks if the mSearchQuery field has
	 * been modified.
	 */
	private class QueryEditTextListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable newText) {
			//check if the query is valid and enable the Search button if so
			TestCoordinator.check(TTChecks.QUESTION_EDITED);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
		 * int, int, int)
		 */
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) { }

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence,
		 * int, int, int)
		 */
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) { }
	}

}
