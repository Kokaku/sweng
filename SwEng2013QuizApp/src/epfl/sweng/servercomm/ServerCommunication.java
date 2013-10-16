package epfl.sweng.servercomm;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.questions.QuizQuestion;
import epfl.sweng.utils.JSONUtilities;

/**
 * This class allow communication with the question server
 * It allows to fetch questions and send new questions
 * @author kokaku
 * 
 */
public final class ServerCommunication {

    /**
     * SERVER_URL is the server address
     */
    public static final String SERVER_URL = 
    		"https://sweng-quiz.appspot.com/quizquestions/";
    
    private ServerCommunication() {
    }
    
    /**
     * Send the question in parameter to the server
     * @param question to send to the server
     * @return true if the question has been correctly sent
     */
    public static boolean send(QuizQuestion question) {
        try {
            return new PostQuestionTask().execute(
                    JSONUtilities.getJSONString(question)).get();
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
        			JSONUtilities.parseAnswers(json),
        			json.getInt("solutionIndex"),
        			JSONUtilities.parseTags(json));
        	
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
		} catch (JSONException e) {
		} catch (IllegalArgumentException e) {   
		}
        
        return null;
    }
    
}
