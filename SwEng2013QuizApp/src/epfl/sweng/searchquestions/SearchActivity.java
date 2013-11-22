package epfl.sweng.searchquestions;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class SearchActivity extends Activity {
	private final static int MAX_LENGTH_QUERY = 500;
	private Button mSearchButton;
	private EditText mSearchQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		mSearchButton = (Button) findViewById(R.id.button_search);
		mSearchQuery = (EditText) findViewById(R.id.search_query);

		mSearchQuery.addTextChangedListener(new QueryEditTextListener());
		System.out.println(mSearchButton);
		TestCoordinator.check(TTChecks.SEARCH_ACTIVITY_SHOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	public void onClickSearch(View view) {
		// new SearchTask().execute(query);
	}

	private void updateSearchButton() {
		if (validQuery(mSearchQuery.getText().toString())) {
			mSearchButton.setEnabled(true);
		} else {
			mSearchButton.setEnabled(false);
		}
	}

	private boolean validQuery(String query) {
		if (query.length() <= MAX_LENGTH_QUERY
				&& !query.replaceAll("\\W+", "").equals("")
				&& validNesting(query)) {
			return true;
		}

		return false;
	}

	private boolean validNesting(String query) {
		int openParen = 0;
		query = query.trim().replaceAll("(\\w+)(\\s+)(\\w+)", "$1*$3").replaceAll("\\s+", "").replaceAll("\\w+", "a");
		for (int i = 0; i < query.length(); i++) {
			char current = query.charAt(i);
			if (current == '(') {
				openParen++;
			} else if (current == ')') {
				openParen--;
				if (openParen < 0) {
					return false;
				}
			} else if (current == '+' || current == '*') {
				if (i >= query.length()-1 || query.charAt(i + 1) != 'a') {
					return false;
				}
			} else if (current != 'a') {
				return false;
			}
		}

		return openParen == 0;

	}

	/**
	 * Listener for mSearchQuery. Checks if the mSearchQuery field has been
	 * modified.
	 */
	private class QueryEditTextListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable newText) {
			// check if the query is valid and enable the Search button if so
			updateSearchButton();
			TestCoordinator.check(TTChecks.QUERY_EDITED);
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
				int after) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence,
		 * int, int, int)
		 */
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	}

	private class SearchTask extends
			AsyncTask<QuizQuestion, Void, QuizQuestion> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected QuizQuestion doInBackground(QuizQuestion... questions) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
