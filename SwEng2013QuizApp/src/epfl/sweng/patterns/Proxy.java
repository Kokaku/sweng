package epfl.sweng.patterns;

import android.os.AsyncTask;
import android.util.Log;
import epfl.sweng.R;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.exceptions.AsyncTaskExceptions;
import epfl.sweng.exceptions.BadRequestException;
import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.offline.DatabaseHandler;
import epfl.sweng.offline.OnSyncListener;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.QuestionIterator;
import epfl.sweng.servercomm.QuestionsCommunicator;
import epfl.sweng.servercomm.ServerCommunication;

/**
 * Proxy for getting and sending questions. The application can either be online
 * or offline. If it's online, communicate with the server, otherwise access the
 * cached data.
 * 
 * When the app goes back online, questions waiting for submission are submitted
 * during synchronization.
 * 
 * @author lseguy
 * 
 */
public enum Proxy implements QuestionsCommunicator {
	INSTANCE;
	
    private static final String LOG_TAG = Proxy.class.getName();

	private ConnectionState mCurrentState = ConnectionState.ONLINE;
	private QuestionsCommunicator instance = ServerCommunication.INSTANCE;

	public enum ConnectionState {
		ONLINE, OFFLINE;
	}

	private Proxy() {
	}

	/**
	 * @return true if the app is in {@code ConnectionState.ONLINE} state.
	 */
	public boolean isOnline() {
		return mCurrentState == ConnectionState.ONLINE;
	}

	/**
	 * Change the connection state of the application.
	 * 
	 * @param state
	 *            the new connection state
	 */
	public void setState(ConnectionState state) {
		setState(state, null);
	}

	/**
	 * Change the connection state of the application. The activity implementing
	 * {@link OnSyncListener} will be signaled when synchronization is over.
	 * 
	 * @param state
	 *            the new connection state
	 * @param listener
	 *            the activity waiting for feedback
	 */
	public void setState(ConnectionState state, OnSyncListener listener) {
		mCurrentState = state;
		if (isOnline()) {
			new SynchronizationTask(listener).execute();
		}
	}

	/**
	 * This method will distribute the query to the server or the database
	 * depending on whether the device is on-line or not. If the Proxy queries
	 * the server by intermediate of {@link ServerCommunication}, it will store
	 * the questions in the database.
	 * 
	 * @throws ServerCommunicationException
	 * @throws DBException
	 * @throws NotLoggedInException
	 *             if the user is not logged in
	 * 
	 * @return questionIterator: a {@link QuestionIterator} with the questions
	 *         retrieved from the server or the database
	 * @throws JSONException
	 *             if there was a problem parsing the JSON
	 */
	@Override
	public QuestionIterator searchQuestion(String query, String next)
		throws NotLoggedInException, DBException,
			ServerCommunicationException {


		if (!UserCredentials.INSTANCE.isAuthenticated()) {
			throw new NotLoggedInException();
		}

		if (isOnline()) {

			QuestionIterator questionIterator = instance.searchQuestion(query,
					next);
			QuizQuestion[] questions = questionIterator.getLocalQuestions();

			for (QuizQuestion question : questions) {
				DatabaseHandler.getHandler().storeQuestion(question, false);
			}

			return questionIterator;
		} else {
			try {
			    return DatabaseHandler.getHandler().searchQuestion(query, next);
			} catch (IllegalArgumentException e) {
			    Log.d(LOG_TAG, "IllegalArgumentException in searchQuestion()", e);
			    throw new BadRequestException("Unrecognized next argument");
			}
			
			
		}
	}

	/**
	 * If in state {@link ConnectionState.ONLINE}, gets a question from the
	 * server and stores in cache. Otherwise, if in state
	 * {@link ConnectionState.OFFLINE} gets a question from the local cache.
	 * 
	 * @return a random question or null if there is no cached question
	 * @throws NotLoggedInException
	 *             if the user is not logged in
	 * @throws ServerCommunicationException
	 *             if the network request is unsuccessful
	 * @throws DBException
	 *             if the question can't be cached or can't be fetched from the
	 *             cache
	 */
	@Override
	public QuizQuestion getRandomQuestion()
		throws ServerCommunicationException, DBException,
			NotLoggedInException {


		if (!UserCredentials.INSTANCE.isAuthenticated()) {
			throw new NotLoggedInException();
		}

		if (isOnline()) {
			QuizQuestion question = instance.getRandomQuestion();
			DatabaseHandler.getHandler().storeQuestion(question, false);
			return question;
		} else {
			return DatabaseHandler.getHandler().getRandomQuestion();
		}
	}

	/**
	 * If in state {@link ConnectionState.ONLINE}, sends a question to the
	 * server. Otherwise, if in state {@link ConnectionState.OFFLINE} stores the
	 * question in the cache so that it's submitted when back online.
	 * 
	 * @param question
	 *            the question to be sent
	 * @throws NotLoggedInException
	 *             if the user is not logged in
	 * @throws DBException
	 *             if the database request is unsuccessful
	 * @throws ServerCommunicationException
	 *             if the network request is unsuccessful
	 * @return the question sent
	 */
	@Override
	public QuizQuestion send(QuizQuestion question) throws DBException,
			NotLoggedInException, ServerCommunicationException {


		if (!UserCredentials.INSTANCE.isAuthenticated()) {
			throw new NotLoggedInException();
		}

		if (isOnline()) {
			QuizQuestion submittedQuestion = instance.send(question);
			DatabaseHandler.getHandler().storeQuestion(submittedQuestion, false);
			return submittedQuestion;
		} else {
			DatabaseHandler.getHandler().storeQuestion(question, true);
			return question;
		}
	}

	/**
	 * During synchronization, submit the questions in a separate thread.
	 */
	private class SynchronizationTask extends AsyncTask<Void, Void, Integer> {

		private AsyncTaskExceptions mException = null;
		private OnSyncListener mListeningActivity = null;

		public SynchronizationTask(OnSyncListener listener) {
			mListeningActivity = listener;
		}

		@Override
		protected Integer doInBackground(Void... unused) {
			try {
				return DatabaseHandler.getHandler().synchronizeQuestions();
			} catch (ServerCommunicationException e) {
			    Log.d(LOG_TAG, "ServerCommunicationException in SynchronizationTask", e);
				mException = AsyncTaskExceptions.SERVER_COMMUNICATION_EXCEPTION;
			} catch (DBException e) {
			    Log.d(LOG_TAG, "DBException in SynchronizationTask", e);
				mException = AsyncTaskExceptions.DB_EXCEPTION;
			}

			return 0;
		}

		@Override
		protected void onPostExecute(Integer questionsSubmitted) {
			if (mException == null) {
				if (questionsSubmitted > 0) {
					SwEng2013QuizApp
							.displayToast(R.string.synchronization_success);
				}
				SwEng2013QuizApp.displayToast(R.string.now_online);
			} else {
				switch (mException) {
					case SERVER_COMMUNICATION_EXCEPTION:
						SwEng2013QuizApp
								.displayToast(R.string.synchronization_failure);
						break;
					case DB_EXCEPTION:
						SwEng2013QuizApp.displayToast(R.string.broken_database);
						break;
					default:
						assert false;
						break;
				}

				setState(ConnectionState.OFFLINE);
				SwEng2013QuizApp.displayToast(R.string.now_offline);
			}

			if (mListeningActivity != null) {
				mListeningActivity.onSyncCompleted();
			}
		}

	}
}
