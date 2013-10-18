package epfl.sweng.servercomm;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.questions.QuizQuestion;
import epfl.sweng.utils.JSONUtilities;

/**
 * This class allows communication with the question server.
 * Used to fetch questions and send new ones to the server.
 * 
 * @author kokaku
 * 
 */
public final class ServerCommunication {

    public static final String SERVER_URL = 
    		"https://sweng-quiz.appspot.com/quizquestions/";
    
    private ServerCommunication() { }
    
    /**
     * Sends a question to the server
     * @param question to send to the server
     * @return true if the question has been correctly sent
     */
    public static boolean send(QuizQuestion question) {
        if (question != null) {
            try {
                String httpAnswer = new HttpPostTask().execute(
                        SERVER_URL,
                        JSONUtilities.getJSONString(question),
                        "Content-type",
                        "application/json").get();
                return httpAnswer != null &&
                       !httpAnswer.equalsIgnoreCase("error");
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            } catch (JSONException e) {
            }
        }

		return false;
	}

    /**
     * Fetch a random question from the server
     * @return the random question fetched, null if an error occurred
     */
    public static QuizQuestion getRandomQuestion() {
        try {
            String httpAnswer = new HttpGetTask().execute(
                    SERVER_URL + "random").get();
            if (httpAnswer != null) {
            	JSONObject json = new JSONObject(httpAnswer);
            	return new QuizQuestion(
            			json.getString("question"),
            			JSONUtilities.parseAnswers(json),
            			json.getInt("solutionIndex"),
            			JSONUtilities.parseTags(json));
            }
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
		} catch (JSONException e) {
		} catch (IllegalArgumentException e) {   
		}
        
        return null;
    }
    
    public static boolean login() {
        return false;
    }
}
