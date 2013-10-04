package epfl.sweng;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class allow communication with the question server
 * It allows to fetch questions and send new questions
 * @author kokaku
 * 
 */
public abstract class ServerCommunication {

    /**
     * SERVER_URL is the server address
     */
    public static final String SERVER_URL = 
    		"https://sweng-quiz.appspot.com/quizquestions/";
    
    /**
     * Send the question in parameter to the server
     * @param question to send to the server
     * @return true if the question has been correctly sent
     */
    public static boolean send(QuizQuestion question) {
        try {
            return new PostQuestionTask().execute(
                    JSONParser.getJSONString(question)).get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }

		return false;
	}

    /**
     * Fetch a random question from the server
     * @return the random question fetched, null if an error occurred
     */
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



