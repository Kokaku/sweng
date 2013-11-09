/**
 * 
 */
package epfl.sweng.exceptions;

/**
 * Used by AsyncTask subclasses to handle exceptions in their onPostExecute()
 * method.
 * 
 * @author lseguy
 *
 */
public enum AsyncTaskExceptions {
    DB_EXCEPTION, NOT_LOGGED_IN_EXCEPTION, SERVER_COMMUNICATION_EXCEPTION,
    INVALID_CREDENTIALS;
}
