package epfl.sweng.searchquestions;

import android.app.Activity;
import android.content.Intent;
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
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * This activity allows the user to insert search queries and send them to the 
 * server or search in the local cash. Once the query was successful, questions
 * are shown by starting the ShowQuestionsActivity
 * 
 * @author Zhivka Gucevska
 *
 */
public class SearchActivity extends Activity {

	private static final String LOG_TAG = SearchActivity.class.getName();
	private static final int MAX_LENGTH_QUERY = 500;

	private Button mSearchButton;
	private EditText mSearchQuery;
	private ProgressBar mProgressBar;

	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		mSearchButton = (Button) findViewById(R.id.button_search);
		mSearchQuery = (EditText) findViewById(R.id.search_query);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar_search);

		mSearchQuery.addTextChangedListener(new QueryEditTextListener());
		TestCoordinator.check(TTChecks.SEARCH_ACTIVITY_SHOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	/**
	 * Called when clicking on the button "Search", sends the query to the 
	 * SwEngServer/local DataBase
	 * 
	 * @param view the button being clicked
	 */
	public void onClickSearch(View view) {
		mIntent = new Intent(this, ShowQuestionsActivity.class);
		new SearchTask().execute(interpreteWhiteSpaces(mSearchQuery.getText()
				.toString()), null);

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
				&& !"".equals(query.replaceAll("\\W+", ""))
				&& validNesting(query)) {
			return true;
		}

		return false;
	}

	private boolean validNesting(String query) {
		int openParen = 0;
		query = sanitizeQuery(query);

		for (int i = 0; i < query.length(); i++) {
			char current = query.charAt(i);
			if (current == '(') {
				openParen++;
				if (i >= query.length() - 1
						|| (query.charAt(i) != 'a' && query.charAt(i) != '(')) {
					return false;
				}
			} else if (current == ')') {
				openParen--;
				if (openParen < 0) {
					return false;
				}
			} else if (current == '+' || current == '*') {
				if (i >= query.length() - 1
						|| (query.charAt(i + 1) != 'a' && query.charAt(i + 1) != '(')) {
					return false;
				}
				if (i == 0
						|| (query.charAt(i - 1) != 'a' && query.charAt(i - 1) != ')')) {
					return false;
				}
			} else if (current != 'a') {
				return false;
			}
		}

		return openParen == 0;

	}

	private String sanitizeQuery(String query) {
		query = query.trim().replaceAll("(\\w+)(\\s+)(\\w+)", "$1*$3")
				.replaceAll("\\s+", "").replaceAll("\\w+", "a");
		query = query.replaceAll("(\\))(\\()", "$1*$2")
				.replaceAll("(\\))(\\w+)", "$1 * $2")
				.replaceAll("(\\w+)(\\()", "$1 * $2").replaceAll("\\s+", "");

		return query;
	}

	private void hideViews() {
		mSearchButton.setVisibility(View.GONE);
		mSearchQuery.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @param query
	 * @return query with white spaces interpreted: removed when useless,
	 *         replaced by '*' when used as such, afterwords spaces are inserted 
	 *         before and after every operator
	 */
	private String interpreteWhiteSpaces(String query) {
		query = query.trim().replaceAll("(\\w+)(\\s+)(\\w+)", "$1*$3")
				.replaceAll("\\s+", "").replaceAll("", "");

		query = query.trim().replaceAll("(\\w+)(\\s+)(\\w+)", "$1*$3")
				.replaceAll("\\s+", "").replaceAll("(\\))(\\()", "$1*$2")
				.replaceAll("(\\))(\\w+)", "$1 * $2")
				.replaceAll("(\\w+)(\\()", "$1 * $2").replaceAll("\\s+", "")
				.replaceAll("\\*", " * ").replaceAll("\\+", " + ")
				.replaceAll("(\\()", " " + "$1" + " ")
				.replaceAll("(\\))", " " + "$1" + " ");
		return query;
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

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			//This method is not necessary, hence it's empty
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			//This method is not necessary, hence it's empty

		}
	}

	/**
	 * Retrieves the questions corresponding to the query in a separate thread, 
	 * starts ShowQuestionsActivity when fetch is successeful
	 * 
	 * @author Zhivka Gucevska
	 * 
	 */
	private class SearchTask extends AsyncTask<String, Void, QuestionIterator> {
		private AsyncTaskExceptions mException = null;

		@Override
		protected void onPreExecute() {
			hideViews();
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected QuestionIterator doInBackground(String... params) {
			try {
				return Proxy.INSTANCE.searchQuestion(params[0], params[1]);
			} catch (NotLoggedInException e) {
				Log.d(LOG_TAG, "NotLoggedInException in SearchTask", e);
				mException = AsyncTaskExceptions.NOT_LOGGED_IN_EXCEPTION;
			} catch (BadRequestException e) {
				Log.d(LOG_TAG, "BadRequestException in SearchTask", e);
				mException = AsyncTaskExceptions.BAD_REQUEST_EXCEPTION;
			} catch (ServerCommunicationException e) {
				Log.d(LOG_TAG, "ServerCommunicationException in SearchTask", e);
				mException = AsyncTaskExceptions.SERVER_COMMUNICATION_EXCEPTION;
			} catch (DBException e) {
				Log.d(LOG_TAG, "DBException in SearchTask", e);
				mException = AsyncTaskExceptions.DB_EXCEPTION;
			}

			return null;
		}

		@Override
		protected void onPostExecute(QuestionIterator questionIterator) {
			if (mException == null) {
				if (questionIterator != null) {
					// put extras in mIntent
					mIntent.putExtra("Source", SearchActivity.class.getName());
					mIntent.putExtra("iterator", questionIterator);

					startActivity(mIntent);
					finish();
				} else {
					Log.d(LOG_TAG, "Something went wrong in SearchTask, QuestionIterator was not created");
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
						break;
					case BAD_REQUEST_EXCEPTION:
						SwEng2013QuizApp
								.displayToast(R.string.failed_to_get_question);
						break;
					case DB_EXCEPTION:
						if (Proxy.INSTANCE.isOnline()) {
							SwEng2013QuizApp
									.displayToast(R.string.failed_to_cache_question);
						} else {
							SwEng2013QuizApp.displayToast(R.string.broken_database);
						}
						break;
					default:
						assert false;
						break;
				}
			}
		}

	}

}
