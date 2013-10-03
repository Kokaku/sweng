/**
 * 
 */
package epfl.sweng;

/**
 * This Exception is thrown when there is an error with quiz's server communication
 * @author kokaku
 */
public class ServerQuestionException extends Exception {
	private static final long serialVersionUID = -2331205875729006555L;

	public ServerQuestionException(String message) {
	    super(message);
	}
}