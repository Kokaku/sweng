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

    public QuestionSender(String question, String[] answers, int solutionIndex,
            Set<String> tags) {
        super(question, answers, solutionIndex, tags);
    }

    /**
     * Not yet implemented
     */
    public boolean postOnServer() {
        try {
            return new PostQuestionTask().execute(this).get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }

		return false;
	}

	public int getSolutionIndex() {
		return super.getSolutionIndex();
	}
	
	public String convertIterableToJSONString(Iterable<String> iterable) {
		String jsonString = "[";
		for (String element : iterable) {
			jsonString += " \"" + element + "\",";
		}
		jsonString = jsonString.substring(0, jsonString.length()-1);
		jsonString += " ]";
		
		return jsonString;
	}
}
