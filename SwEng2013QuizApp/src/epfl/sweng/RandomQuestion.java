/**
 * 
 */
package epfl.sweng;



/**
 * @author kokaku
 *
 */
public class RandomQuestion extends QuestionGetter {
    
	/**
	 * Get a random question from server
	 * @throws ServerCommunicationException thrown if there is a problem with the server communication
	 */
	public RandomQuestion() throws ServerQuestionException {
		super("random");
	}
}
