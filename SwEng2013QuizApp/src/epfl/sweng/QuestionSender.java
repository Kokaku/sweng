/**
 * 
 */
package epfl.sweng;

import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author kokaku
 *
 */
public class QuestionSender extends ServerQuestion {

	
	public QuestionSender(String question, String[] answers, int solutionIndex, Set<String> tags) {
		super(question, answers, solutionIndex, tags);
	}
	
	/**
	 * Not yet implemented
	 */
	public boolean postOnServer() {
		try {
            return new PostQuestionTask().execute().get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
		return false;
	}
}
