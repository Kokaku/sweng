package epfl.sweng.searchquestions;

import java.util.NoSuchElementException;

import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.patterns.Proxy;
import epfl.sweng.quizquestions.QuizQuestion;

public class QuestionIterator {

    private QuizQuestion[] questions;
    private int nextIndex = 0;
    private final String next;
    private final String query;
    
    /**
     * @param questions is the array of QuizQuestion over which you want to iterate
     * @param next is the String token received by the server in case the server
     * only return a fraction of all questions that match a query
     */
    public QuestionIterator(QuizQuestion[] questions, String query, String next) {
        if (questions == null) {
            throw new IllegalArgumentException("Argument questions cannot be null");
        }
            
        this.questions = questions.clone();
        this.next = next;
        this.query = query;
    }

    /**
     * @param questions is the array of QuizQuestion over which you want to iterate
     */
    public QuestionIterator(QuizQuestion[] questions) {
        this(questions, null, null);
    }
    
    /**
     * Returns true if the iteration has more elements. (In other words, returns
     *  true if next() would return an element rather than throwing an exception.)
     * @return true if the iteration has more elements
     */
    public boolean hasNext() {
        return questions.length > nextIndex || next != null;
    }

    /**
     * Returns the next element in the iteration.
     * Should only be called in an AsynTask
     * @return the next element in the iteration
     * @throws NotLoggedInException if the user is not logged in
     * @throws DBException if the database request is unsuccessful
     * @throws ServerCommunicationException if the network request is unsuccessful
     * @throws NoSuchElementException if the iteration has no more elements
     */
    public QuizQuestion next()
        throws NotLoggedInException, DBException, ServerCommunicationException,
               NoSuchElementException {
        
        if (!hasNext()) {
            throw new NoSuchElementException();  
        }
        
        if (questions.length > nextIndex) {
            fetchNextQuestions();
        }
        
        QuizQuestion nextQuestion = questions[nextIndex];
        nextIndex++;
        return nextQuestion;
    }
    
    /**
     * @return the questions already received as an array of QuizQuestion
     */
    public QuizQuestion[] getLocalQuestions() {
        return questions.clone();
    }
    
    /**
     * Replace the local cache with the next questions on whitch the query match
     * Should only be called in an AsynTask
     * @throws NotLoggedInException if the user is not logged in
     * @throws DBException if the database request is unsuccessful
     * @throws ServerCommunicationException if the network request is unsuccessful
     */
    private void fetchNextQuestions()
        throws NotLoggedInException, DBException, ServerCommunicationException {
        
        QuestionIterator responseIterator = Proxy.INSTANCE.searchQuestion(query);
        questions = responseIterator.getLocalQuestions();
        nextIndex = 0;
    }
}
