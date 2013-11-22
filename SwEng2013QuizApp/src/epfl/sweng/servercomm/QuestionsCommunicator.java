package epfl.sweng.servercomm;

import org.json.JSONException;

import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.QuestionIterator;

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
     * Get questions that match the query.
     * 
     * @param query is a String representing the query
     * @param next is the String token received by the server in case the server
     * only return a fraction of all questions that match a query 
     * @return An iterator over the questions that match the query
     * @throws NotLoggedInException if the user is not logged in
     * @throws DBException if the database request is unsuccessful
     * @throws ServerCommunicationException if the network request is unsuccessful
     * @throws JSONException if there was a problem parsing the json
     */
    QuestionIterator searchQuestion(String query, String next)
        throws NotLoggedInException, DBException, ServerCommunicationException,
               JSONException;
    
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
