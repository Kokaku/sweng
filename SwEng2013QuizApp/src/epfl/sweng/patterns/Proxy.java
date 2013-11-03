package epfl.sweng.patterns;

import epfl.sweng.exceptions.CommunicationException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.offline.LocalCache;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.QuestionsCommunicator;
import epfl.sweng.servercomm.ServerCommunication;

/**
 * Proxy for getting and sending questions. The application can either be
 * online or offline. If it's online, communicate with the server, otherwise
 * access the cached data.
 * 
 * @author lseguy
 *
 */
// TODO : Change name, OfflineManager? ConnectionManager?
public enum Proxy implements QuestionsCommunicator {
    INSTANCE;
    
    private ConnectionState mCurrentState = ConnectionState.ONLINE;
    
    public enum ConnectionState {
        ONLINE, OFFLINE;
    }
    
    public ConnectionState getState() {
        return mCurrentState;
    }
    
    @Override
    public QuizQuestion getRandomQuestion()
        throws CommunicationException, NotLoggedInException {
        
        if (mCurrentState == ConnectionState.ONLINE) {
            return ServerCommunication.INSTANCE.getRandomQuestion();
        } else {
            return LocalCache.INSTANCE.getRandomQuestion();
        }
    }

    @Override
    public void send(QuizQuestion question)
        throws CommunicationException, NotLoggedInException {

        if (mCurrentState == ConnectionState.ONLINE) {
            ServerCommunication.INSTANCE.send(question);
        } else {
            LocalCache.INSTANCE.send(question);
        }
    }

}
