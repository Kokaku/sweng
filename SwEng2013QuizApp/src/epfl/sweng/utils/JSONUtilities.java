/**
 * 
 */
package epfl.sweng.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Class of utility methods to parse JSONObjects to String, String[], Set<String>
 * 
 * @author ValentinRutz
 *
 */
public class JSONUtilities {

    /**
     * Manipulates a {@link QuizQuestion} into a {@link JSONObject} 
     * formatted String as follows:
     * "{ "
     * + "\"question\": \"<question_text>\", "
     * + "\"answers\":  <answers_array>, "
     * + "\"solutionIndex\": <solution_index>, "
     * + "\"tags\": <tags_array>
     * + " }"
     * 
     * @param question to format
     * @return String representing the formatted question
     */
    public static String getJSONString(QuizQuestion question)
        throws JSONException {
        if (question == null) {
            throw new IllegalArgumentException("");
        }
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question", question.getQuestion());
        jsonObject.put("answers", new JSONArray(question.getAnswers()));
        jsonObject.put("solutionIndex", question.getSolutionIndex());
        jsonObject.put("tags", new JSONArray(question.getTags()));
        
        return jsonObject.toString();
    }
    
    /**
     * Parse answers from {@link JSONObject} into a {@link String} array
     * 
     * @param json the object from which we parse the answers
     * @return String[] of the answers
     * @throws JSONException if there is a problem getting the {@link JSONArray}
     */
    public static List<String> parseAnswers(JSONObject json) throws JSONException {
        JSONArray jsonAnswers = json.getJSONArray("answers");
        List<String> answers = new ArrayList<String>();
        for (int i = 0; i < jsonAnswers.length(); i++) {
            answers.add(jsonAnswers.getString(i));
        }
        return answers;
    }

    /**
     * Parse tags from {@link JSONObject} into a {@link Set}
     * 
     * @param json the object from which we parse the tags
     * @return a {@link Set} of the tags
     * @throws JSONException if there is a problem getting the {@link JSONArray}
     */
    public static Set<String> parseTags(JSONObject json) throws JSONException {
        JSONArray jsonTags = json.getJSONArray("tags");
        Set<String> tags = new HashSet<String>();
        for (int i = 0; i < jsonTags.length(); i++) {
            tags.add(jsonTags.getString(i));
        }
        return tags;
    }

}
