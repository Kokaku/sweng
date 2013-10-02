/**
 * 
 */
package epfl.sweng;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author kokaku
 *
 */
public abstract class ServerQuestion extends QuizQuestion {
	
	protected static final String SERVER_URL = "https://sweng-quiz.appspot.com/quizquestions/";
	protected static final int TOTAL_CONNECTION_TRY_BEFORE_ABORD = 5;
	
	/**
	 * @link ServerQuestion
	 */
	public ServerQuestion(String question, String[] answers, int solutionIndex, Set<String> tags) {
		super(question, answers, solutionIndex, tags);
	}

	/**
	 * @param json must contain following fields :
	 * 	"id" : long
	 * 	"question" : String
	 * 	"answers" : JSONArray
	 * 	"solutionIndex" : int
	 * 	"tags" : JSONArray
	 * @throws JSONException thrown if JSON doen't contains field needed to construct object
	 */
	public ServerQuestion(JSONObject json) throws ServerCommunicationException {
		super(parseQuestion(json), parseAnswers(json), parseSolutionIndex(json), parseTags(json));
	}
	
	protected static long parseId(JSONObject json) throws ServerCommunicationException {
		try {
			return json.getLong("id");
		} catch (JSONException e) {
			throw new ServerCommunicationException("Couldn't parse JSON.");
		}
	}
	
	protected static String parseQuestion(JSONObject json) throws ServerCommunicationException {
		try {
			return json.getString("question");
		} catch (JSONException e) {
			throw new ServerCommunicationException("Couldn't parse JSON.");
		}
	}
	
	protected static String[] parseAnswers(JSONObject json) throws ServerCommunicationException {
		try {
			JSONArray jsonAnswers = json.getJSONArray("answers");
			String[] answers = new String[jsonAnswers.length()];
			for (int i = 0; i < jsonAnswers.length(); i++) {
				answers[i] = jsonAnswers.getString(i);
			}
			return answers;
		} catch (JSONException e) {
			throw new ServerCommunicationException("Couldn't parse JSON.");
		}
	}
	
	protected static int parseSolutionIndex(JSONObject json) throws ServerCommunicationException {
		try {
			return json.getInt("solutionIndex");
		} catch (JSONException e) {
			throw new ServerCommunicationException("Couldn't parse JSON.");
		}
	}
	
	protected static Set<String> parseTags(JSONObject json) throws ServerCommunicationException {
		try {
			JSONArray jsonTags = json.getJSONArray("tags");
			Set<String> tags = new HashSet<String>();
			for (int i = 0; i < jsonTags.length(); i++) {
				tags.add(jsonTags.getString(i));
			}
			return tags;
		} catch (JSONException e) {
			throw new ServerCommunicationException("Couldn't parse JSON.");
		}
	}
}
