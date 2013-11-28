package epfl.sweng.searchquestions;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import epfl.sweng.R;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.exceptions.AsyncTaskExceptions;
import epfl.sweng.exceptions.BadRequestException;
import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.patterns.Proxy;
import epfl.sweng.patterns.Proxy.ConnectionState;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class SearchActivity extends Activity {
	private final static int MAX_LENGTH_QUERY = 500;
	private Button mSearchButton;
	private EditText mSearchQuery;
	private ProgressBar mProgressBar;

	private QuestionIterator mQuestionIterator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		mSearchButton = (Button) findViewById(R.id.button_search);
		mSearchQuery = (EditText) findViewById(R.id.search_query);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar_search);

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
		new SearchTask().execute(interpreteWhiteSpaces(mSearchQuery.getText().toString()));
		
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
		query = query.trim().replaceAll("(\\w+)(\\s+)(\\w+)", "$1*$3")
				.replaceAll("\\s+", "").replaceAll("\\w+", "a");
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
				if (i >= query.length() - 1 || query.charAt(i + 1) != 'a') {
					return false;
				}
			} else if (current != 'a') {
				return false;
			}
		}

		return openParen == 0;

	}

	private void hideViews() {
		mSearchButton.setVisibility(View.GONE);
		mSearchQuery.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @param query
	 * @return query with white spaces interpreted: removed when useless,
	 *         replaced by '*' when used as such
	 */
	private String interpreteWhiteSpaces(String query) {
		return query.trim().replaceAll("(\\w+)(\\s+)(\\w+)", "$1 * $3")
				.replaceAll("\\s+", "");
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

	private class SearchTask extends AsyncTask<String, Void, QuestionIterator> {
		private AsyncTaskExceptions mException = null;

		@Override
		protected void onPreExecute() {
			hideViews();
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected QuestionIterator doInBackground(String... params) {
			Log.d("Potato SearchActivity",
					"Sending query to server, getting questions");
			try {
				return Proxy.INSTANCE.searchQuestion(params[0], null);
			} catch (NotLoggedInException e) {
				Log.d("POTATO SearchActivity",
						"NotLoggedInException : " + e.getMessage());
				mException = AsyncTaskExceptions.NOT_LOGGED_IN_EXCEPTION;
			} catch (BadRequestException e) {
				Log.d("POTATO SearchActivity",
						"BadRequestException : " + e.getMessage());
				mException = AsyncTaskExceptions.BAD_REQUEST_EXCEPTION;
			} catch (ServerCommunicationException e) {
				Log.d("POTATO SearchActivity",
						"ServerComException : " + e.getMessage());
				mException = AsyncTaskExceptions.SERVER_COMMUNICATION_EXCEPTION;
			} catch (DBException e) {
				Log.d("POTATO SearchActivity",
						"DBException : " + e.getMessage());
				mException = AsyncTaskExceptions.DB_EXCEPTION;
			}

			return null;
		}

		@Override
		protected void onPostExecute(QuestionIterator questionIterator) {
			if (mException == null) {
				if (questionIterator != null) {
					mQuestionIterator = questionIterator;
					Log.d("POTATO SHOWQUESTIONS",
							"Questions fetched successfully ");
				}
			} else {
				switch (mException) {
					case NOT_LOGGED_IN_EXCEPTION:
						SwEng2013QuizApp.displayToast(R.string.not_logged_in);
						break;
					case SERVER_COMMUNICATION_EXCEPTION:
						SwEng2013QuizApp
								.displayToast(R.string.failed_to_get_question);
						Proxy.INSTANCE.setState(ConnectionState.OFFLINE);
						SwEng2013QuizApp.displayToast(R.string.now_offline);
						// TestCoordinator.check(TTChecks.OFFLINE_CHECKBOX_ENABLED);
						break;
					case BAD_REQUEST_EXCEPTION:
						SwEng2013QuizApp
								.displayToast(R.string.failed_to_get_question);
						break;
					case DB_EXCEPTION:
						if (Proxy.INSTANCE.isOnline()) {
							SwEng2013QuizApp
									.displayToast(R.string.failed_to_cache_question);
							Log.d("POTATO SHOWQUESTIONS",
									"Toast failed to cache question displayed");
						} else {
							SwEng2013QuizApp.displayToast(R.string.broken_database);
							Log.d("POTATO SHOWQUESTIONS",
									"Broken DB toast displayed");
						}
						break;
					default:
						assert false;
				}
			}
		}

	}

}
