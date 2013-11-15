package epfl.sweng.patterns;

import android.os.AsyncTask;
import android.util.Log;
import epfl.sweng.R;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.exceptions.AsyncTaskExceptions;
import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.offline.DatabaseHandler;
import epfl.sweng.offline.OnSyncListener;
import epfl.sweng.quizquestions.QuizQuestion;
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
// TODO : Change name ? ConnectionManager? OfflineManager?
public enum Proxy implements QuestionsCommunicator {
    INSTANCE;

    private ConnectionState mCurrentState = ConnectionState.ONLINE;
    private DatabaseHandler mDatabase;
    private QuestionsCommunicator instance = ServerCommunication.INSTANCE;
    
    public enum ConnectionState {
        ONLINE, OFFLINE;
    }

    private Proxy() {
        mDatabase = new DatabaseHandler();
        Log.d("POTATO Proxy", "Constructor has been called, DatabaseHandler created");
    }
  
    /**
     * @return true if the app is in {@code ConnectionState.ONLINE} state.
     */
    public boolean isOnline() {
    	Log.d("POTATO Proxy", "isOnline = " + mCurrentState);
        return mCurrentState == ConnectionState.ONLINE;
        
    }
    
    /**
     * Change the connection state of the application.
     * 
     * @param state the new connection state
     */
    public void setState(ConnectionState state) {
    	Log.d("POTATO Proxy", "Set state to " + state);
        setState(state, null);
    }
    
    /**
     * Change the connection state of the application. The activity implementing
     * {@link OnSyncListener} will be signaled when synchronization is over.
     * 
     * @param state the new connection state
     * @param listener the activity waiting for feedback
     */
    public void setState(ConnectionState state, OnSyncListener listener) {
        mCurrentState = state;
        Log.d("POTATO Proxy", "Set state to " + state);
        if (isOnline()) {
        	Log.d("POTATO Proxy", "App is online, execute SyncTask");
            new SynchronizationTask(listener).execute();
        }
    }

    /**
     * If in state {@link ConnectionState.ONLINE}, gets a question from the
     * server and stores in cache. Otherwise, if in state
     * {@link ConnectionState.OFFLINE} gets a question from the local cache.
     * 
     * @return a random question or null if there is no cached question
     * @throws NotLoggedInException if the user is not logged in
     * @throws ServerCommunicationException if the network request is unsuccessful
     * @throws DBException if the question can't be cached or
     *      can't be fetched from the cache
     */
    @Override
    public QuizQuestion getRandomQuestion() throws ServerCommunicationException,
        DBException, NotLoggedInException {

        if (!UserCredentials.INSTANCE.isAuthenticated()) {
        	Log.d("POTATO Proxy", "get: User is not authenticated");
            throw new NotLoggedInException();
        }

        if (isOnline()) {
        	Log.d("POTATO Proxy", "get: App is online, get question from server");
            QuizQuestion question = instance.getRandomQuestion();
            mDatabase.storeQuestion(question, false);
            return question;
        } else {
        	Log.d("POTATO Proxy", "get: App is offline, get question from DB");
            return mDatabase.getRandomQuestion();
        }
    }

    /**
     * If in state {@link ConnectionState.ONLINE}, sends a question to the
     * server. Otherwise, if in state {@link ConnectionState.OFFLINE} stores the
     * question in the cache so that it's submitted when back online.
     * 
     * @param question the question to be sent
     * @throws NotLoggedInException if the user is not logged in
     * @throws DBException if the database request is unsuccessful
     * @throws ServerCommunicationException if the network request is unsuccessful
     * @return the question sent
     */
    @Override
    public QuizQuestion send(QuizQuestion question) throws DBException,
        NotLoggedInException, ServerCommunicationException {

        if (!UserCredentials.INSTANCE.isAuthenticated()) {
        	Log.d("POTATO Proxy", "send: User is not authenticated");
            throw new NotLoggedInException();
        }

        if (isOnline()) {
        	Log.d("POTATO Proxy", "send: App is online, question sent to server");
            QuizQuestion submittedQuestion = instance.send(question);
            mDatabase.storeQuestion(submittedQuestion, false);
            return submittedQuestion;
        } else {
        	Log.d("POTATO Proxy", "send: App is offline, question stored in DB cash");
            mDatabase.storeQuestion(question, true);
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
        	Log.d("POTATO Proxy", "SyncTask: created");
            mListeningActivity = listener;
        }
        
        @Override
        protected Integer doInBackground(Void... unused) {
            try {
            	Log.d("POTATO Proxy", "SyncTask: doInBackground");
                return mDatabase.synchronizeQuestions();
            } catch (ServerCommunicationException e) {
            	Log.d("POTATO Proxy", "SyncTask: doInBackground exception 1");
                mException = AsyncTaskExceptions.SERVER_COMMUNICATION_EXCEPTION;
            } catch (DBException e) {
            	Log.d("POTATO Proxy", "SyncTask: doInBackground exception 2");
                mException = AsyncTaskExceptions.DB_EXCEPTION;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer questionsSubmitted) {
        	Log.d("POTATO Proxy", "SyncTask: onPostExecute");

        	if (mException == null) {
                if (questionsSubmitted > 0) {
                    SwEng2013QuizApp.displayToast(R.string.synchronization_success);
                	Log.d("POTATO Proxy", "SyncTask: onPostExecute questions submitted" + questionsSubmitted);

                }
            	Log.d("POTATO Proxy", "SyncTask: onPostExecute App is now online");
                SwEng2013QuizApp.displayToast(R.string.now_online);
            } else {
                switch (mException) {
                    case SERVER_COMMUNICATION_EXCEPTION:
                    	Log.d("POTATO Proxy", "SyncTask: onPostExecute: sync failure");
                        SwEng2013QuizApp.displayToast(R.string.synchronization_failure);
                        break;
                    case DB_EXCEPTION:
                    	Log.d("POTATO Proxy", "SyncTask: onPostExecute: broken DB");
                        SwEng2013QuizApp.displayToast(R.string.broken_database);
                        break;
                    default:
                        assert false;
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
