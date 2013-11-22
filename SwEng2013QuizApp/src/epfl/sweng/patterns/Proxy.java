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
     * @param state the new connection state
     */
    public void setState(ConnectionState state) {
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
        Log.d("POTATO PROXY", "Setting connection state to " + state);
        if (listener != null) {
            Log.d("POTATO PROXY", "OnSyncListener activity is " + listener);
        }
        mCurrentState = state;
        if (isOnline()) {
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
        
        Log.d("POTATO PROXY", "getRandomQuestion() called");

        if (!UserCredentials.INSTANCE.isAuthenticated()) {
            throw new NotLoggedInException();
        }

        if (isOnline()) {
            Log.d("POTATO PROXY", "Online => getting question from server and caching it");
            QuizQuestion question = instance.getRandomQuestion();
            mDatabase.storeQuestion(question, false);
            return question;
        } else {
            Log.d("POTATO PROXY", "Offline => getting question from cache");
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

        Log.d("POTATO PROXY", "send() called");
        
        if (!UserCredentials.INSTANCE.isAuthenticated()) {
            throw new NotLoggedInException();
        }

        if (isOnline()) {
            Log.d("POTATO PROXY", "Online => sending question to server and caching it");
            QuizQuestion submittedQuestion = instance.send(question);
            mDatabase.storeQuestion(submittedQuestion, false);
            return submittedQuestion;
        } else {
            Log.d("POTATO PROXY", "Offline => storing question in cache");
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
            mListeningActivity = listener;
        }
        
        @Override
        protected Integer doInBackground(Void... unused) {
            Log.d("POTATO PROXY", "SyncTask doing its job");
            try {
                return mDatabase.synchronizeQuestions();
            } catch (ServerCommunicationException e) {
                Log.d("POTATO PROXY", "ServerComException in SyncTask : " + e.getMessage());
                mException = AsyncTaskExceptions.SERVER_COMMUNICATION_EXCEPTION;
            } catch (DBException e) {
                Log.d("POTATO PROXY", "DBException in SyncTask : " + e.getMessage());
                mException = AsyncTaskExceptions.DB_EXCEPTION;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer questionsSubmitted) {
            if (mException == null) {
                if (questionsSubmitted > 0) {
                    SwEng2013QuizApp.displayToast(R.string.synchronization_success);
                    Log.d("POTATO PROXY", "SyncTask has submitted " + questionsSubmitted + " questions");
                }
                Log.d("POTATO PROXY", "SyncTask executed successfully");
                SwEng2013QuizApp.displayToast(R.string.now_online);
            } else {
                switch (mException) {
                    case SERVER_COMMUNICATION_EXCEPTION:
                        SwEng2013QuizApp.displayToast(R.string.synchronization_failure);
                        break;
                    case DB_EXCEPTION:
                        SwEng2013QuizApp.displayToast(R.string.broken_database);
                        break;
                    default:
                        assert false;
                }
                
                Log.d("POTATO PROXY", "Exception in SyncTask, going offline");
                setState(ConnectionState.OFFLINE);
                SwEng2013QuizApp.displayToast(R.string.now_offline);
            }
            
            if (mListeningActivity != null) {
                Log.d("POTATO PROXY", "SyncTask signal to the listening activity : " + mListeningActivity);
                mListeningActivity.onSyncCompleted();
            }
        }

    }

    @Override
    public QuestionIterator searchQuestion(String query)
        throws NotLoggedInException, DBException, ServerCommunicationException {
        
        // TODO Auto-generated method stub
        return null;
    }
    
}
