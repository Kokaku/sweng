/**
 * 
 */
package epfl.sweng;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
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
            return new PostQuestionTask().execute(getJSONString(question)).get();
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
        			parseAnswers(json),
        			json.getInt("solutionIndex"),
        			parseTags(json));
        	
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
		} catch (JSONException e) {
		}
        
        return null;
    }
    
    private static String getJSONString(QuizQuestion question) {
    	return "{"
                + " \"question\": \""
                + protectTwiceSpecialChars(question.getQuestion())
                + "\","
                + " \"answers\": "
                + convertIterableToJSONString(Arrays
                        .asList(question.getAnswers()))
                + ","
                + " \"solutionIndex\": "
                + question.getSolutionIndex() + ","
                + " \"tags\": "
                + convertIterableToJSONString(question.getTags())
                + " }";
    }
	
	private static String convertIterableToJSONString(Iterable<String> iterable) {
		String jsonString = "[";
		for (String element : iterable) {
			jsonString += " \"" + protectTwiceSpecialChars(element) + "\",";
		}
		jsonString = jsonString.substring(0, jsonString.length()-1);
		jsonString += " ]";
		
		return jsonString;
	}

    private static String[] parseAnswers(JSONObject json) throws JSONException {
        JSONArray jsonAnswers = json.getJSONArray("answers");
        String[] answers = new String[jsonAnswers.length()];
        for (int i = 0; i < jsonAnswers.length(); i++) {
            answers[i] = jsonAnswers.getString(i);
        }
        return answers;
    }

    private static Set<String> parseTags(JSONObject json) throws JSONException {
    	JSONArray jsonTags = json.getJSONArray("tags");
        Set<String> tags = new HashSet<String>();
        for (int i = 0; i < jsonTags.length(); i++) {
            tags.add(jsonTags.getString(i));
        }
        return tags;
    }
    
    private static String protectTwiceSpecialChars(String str) {
    	return str.replace("\\", "\\\\").
    			replace("\"", "\\\"").
    			replace("\'", "\\\'");
    }
}
