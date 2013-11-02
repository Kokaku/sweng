package epfl.sweng.servercomm;

import epfl.sweng.exceptions.CommunicationException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Should be implemented by classes wanting to get or submit questions
 * to a remote server, a local file system, etc.
 * 
 * @author lseguy
 *
 */
public interface QuestionsCommunicator {
    
    /**
     * Get a question
     * 
     * @return a random quiz question
     * @throws NotLoggedInException if the user is not logged in
     */
    QuizQuestion getRandomQuestion()
        throws NotLoggedInException, CommunicationException;
    
    /**
     * Submit a new question
     * 
     * @param question the question to be submitted
     * @throws NotLoggedInException if the user is not logged in
     */
    void send(QuizQuestion question)
        throws NotLoggedInException, CommunicationException;
}
