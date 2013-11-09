package epfl.sweng.servercomm;

import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
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
     * @throws DBException if the database request is unsuccessful
     * @throws ServerCommunicationException if the network request is unsuccessful
     */
    QuizQuestion getRandomQuestion()
        throws NotLoggedInException, DBException, ServerCommunicationException;
    
    /**
     * Submit a new question
     * 
     * @param question the question to be submitted
     * @throws NotLoggedInException if the user is not logged in
     * @throws DBException if the database request is unsuccessful
     * @throws ServerCommunicationException if the network request is unsuccessful
     * @return the question sent
     */
    QuizQuestion send(QuizQuestion question)
        throws NotLoggedInException, DBException, ServerCommunicationException;
}
