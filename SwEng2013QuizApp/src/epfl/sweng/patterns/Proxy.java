package epfl.sweng.patterns;

import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.exceptions.CommunicationException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.offline.DatabaseHandler;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.QuestionsCommunicator;
import epfl.sweng.servercomm.ServerCommunication;

/**
 * Proxy for getting and sending questions. The application can either be online
 * or offline. If it's online, communicate with the server, otherwise access the
 * cached data.
 * 
 * @author lseguy
 * 
 */
// TODO : Change name? OfflineManager? ConnectionManager?
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
     * @return the current {@link ConnectionState}
     */
    public ConnectionState getState() {
        return mCurrentState;
    }
    
    /**
     * Change the connection state of the application.
     * 
     * @param state the new connection state
     */
    public void setState(ConnectionState state) {
        mCurrentState = state;
    }

    /**
     * If in state {@link ConnectionState.ONLINE}, gets a question from the
     * server and stores in cache. Otherwise, if in state
     * {@link ConnectionState.OFFLINE} gets a question from the local cache.
     * 
     * @return a random question
     * @throws NotLoggedInException
     *             if the user is not logged in
     * @throws CommunicationException
     *             if the request is unsuccessful
     */
    @Override
    public QuizQuestion getRandomQuestion() throws CommunicationException,
        NotLoggedInException {

        if (!UserCredentials.INSTANCE.isAuthenticated()) {
            throw new NotLoggedInException();
        }

        if (mCurrentState == ConnectionState.ONLINE) {
            QuizQuestion question = ServerCommunication.INSTANCE
                .getRandomQuestion();
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
     * @param question
     *            the question to be sent
     * @throws NotLoggedInException
     *             if the user is not logged in
     * @throws CommunicationException
     *             if the request is unsuccessful
     */
    @Override
    public void send(QuizQuestion question) throws CommunicationException,
        NotLoggedInException {

        if (!UserCredentials.INSTANCE.isAuthenticated()) {
            throw new NotLoggedInException();
        }

        if (mCurrentState == ConnectionState.ONLINE) {
            ServerCommunication.INSTANCE.send(question);
            database.storeQuestion(question, false);
        } else {
            database.storeQuestion(question, true);
        }
    }

}
