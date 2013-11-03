package epfl.sweng.offline;

import epfl.sweng.exceptions.CommunicationException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.QuestionsCommunicator;

/**
 * @author lseguy
 *
 */
public enum LocalCache implements QuestionsCommunicator {
    INSTANCE;
    
    private DatabaseHandler database;
    
    private LocalCache() {
        database = new DatabaseHandler();
    }
    
    @Override
    public QuizQuestion getRandomQuestion()
        throws NotLoggedInException, CommunicationException {
        
        return database.getRandomQuestion();
    }

    @Override
    public void send(QuizQuestion question)
        throws NotLoggedInException, CommunicationException {
        
        return;
    }
    
}
