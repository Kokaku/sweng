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
	
	public String convertArrayToJSONString(String[] array) {
		String jsonString = "[";
		for (String element : array) {
			jsonString += " \"" + element + "\",";
		}
		jsonString = jsonString.substring(0, jsonString.length()-1);
		jsonString += " ]";
		
		return jsonString;
	}
}
