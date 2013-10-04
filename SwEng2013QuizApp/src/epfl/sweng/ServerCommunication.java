/**
 * 
 */
package epfl.sweng;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author kokaku
 * 
 */
public abstract class ServerCommunication {

    public static final String SERVER_URL = 
    		"https://sweng-quiz.appspot.com/quizquestions/";
    
    public static boolean send(QuizQuestion question) {
        try {
            return new PostQuestionTask().execute(JSONParser.getJSONString(question)).get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }

		return false;
	}

    public static QuizQuestion getRandomQuestion() {
        try {
        	JSONObject json = new GetQuestionTask().execute(
        			SERVER_URL + "random").get();
        	if (json == null) {
        		return null;
        	}
        	return new QuizQuestion(
        			json.getString("question"),
        			JSONParser.parseAnswers(json),
        			json.getInt("solutionIndex"),
        			JSONParser.parseTags(json));
        	
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
		} catch (JSONException e) {
		}
        
        return null;
    }
    
}



