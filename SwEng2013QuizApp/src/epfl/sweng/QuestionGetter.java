/**
 * 
 */
package epfl.sweng;

import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

/**
 * @author kokaku
 *
 */
public class QuestionGetter extends ServerQuestion {

    public QuestionGetter(String urlTermination) throws ServerQuestionException {
        super(exceptionHandler(SERVER_URL+urlTermination));
    }
    
    private static JSONObject exceptionHandler(String url) throws ServerQuestionException {
    	try {
    		Object questionOrException = new GetQuestionTask().execute(url).get();
    		if(questionOrException instanceof JSONObject)
    			return (JSONObject)questionOrException;
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
    	
    	throw new ServerQuestionException("Couldn't fetch question from server.");
    }
}
