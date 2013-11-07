package epfl.sweng.patterns;

import android.os.AsyncTask;
import epfl.sweng.R;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.exceptions.CommunicationException;
import epfl.sweng.exceptions.DBCommunicationException;
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
    private DatabaseHandler database;

    public enum ConnectionState {
        ONLINE, OFFLINE;
    }

    private Proxy() {
        database = new DatabaseHandler();
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
     * @throws DBCommunicationException if the question can't be cached or
     *      can't be fetched from the cache
     */
    @Override
    public QuizQuestion getRandomQuestion() throws CommunicationException,
        NotLoggedInException {

        if (!UserCredentials.INSTANCE.isAuthenticated()) {
            throw new NotLoggedInException();
        }

        if (isOnline()) {
            QuizQuestion question = ServerCommunication.INSTANCE.getRandomQuestion();
            database.storeQuestion(question, false);
            return question;
        } else {
            return database.getRandomQuestion();
        }
    }

    /**
     * If in state {@link ConnectionState.ONLINE}, sends a question to the
     * server. Otherwise, if in state {@link ConnectionState.OFFLINE} stores the
     * question in the cache so that it's submitted when back online.
     * 
     * @param question the question to be sent
     * @throws NotLoggedInException if the user is not logged in
     * @throws CommunicationException if the request is unsuccessful
     * @return the question sent
     */
    @Override
    public QuizQuestion send(QuizQuestion question) throws CommunicationException,
        NotLoggedInException {

        if (!UserCredentials.INSTANCE.isAuthenticated()) {
            throw new NotLoggedInException();
        }

        if (isOnline()) {
            QuizQuestion submittedQuestion = ServerCommunication.INSTANCE.send(question);
            database.storeQuestion(submittedQuestion, false);
            return submittedQuestion;
        } else {
            database.storeQuestion(question, true);
            return question;
        }
    }
    
    /**
     * During synchronization, submit the questions in a separate thread.
     */
    private class SynchronizationTask extends AsyncTask<Void, Void, Integer> {
        
        private Exception mException = null;
        private OnSyncListener mListeningActivity = null;
        
        public SynchronizationTask(OnSyncListener listener) {
            mListeningActivity = listener;
        }
        
        @Override
        protected Integer doInBackground(Void... unused) {
            try {
                return database.synchronizeQuestions();
            } catch (CommunicationException e) {
                mException = e;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer questionsSubmitted) {
            
            if (mException == null) {
                if (questionsSubmitted > 0) {
                    SwEng2013QuizApp.displayToast(R.string.synchronization_success);
                }
                SwEng2013QuizApp.displayToast(R.string.now_online);
            } else {
                if (mException instanceof ServerCommunicationException) {
                    SwEng2013QuizApp.displayToast(R.string.synchronization_failure);
                } else if (mException instanceof DBCommunicationException) {
                    SwEng2013QuizApp.displayToast(R.string.broken_database);
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
