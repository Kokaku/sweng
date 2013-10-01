/**
 * 
 */
package epfl.sweng;

/**
 * This Exception is thrown when there is an error with quiz's server communication
 * @author kokaku
 */
public class ServerCommunicationException extends Exception {
	private static final long serialVersionUID = -8350491465512797992L;

	public ServerCommunicationException(String message) {
	    super(message);
	}
}