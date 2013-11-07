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
        jsonObject.put("owner", question.getOwner());
        jsonObject.put("id", question.getId());
        
        return jsonObject.toString();
    }
    
    /**
     * Parse strings from a {@link JSONArray} into an {@link ArrayList}
     * 
     * @param json the JSONArray from which we parse the strings
     * @return an ArrayList containing the strings
     * @throws JSONException if there is a problem during parsing
     */
    public static List<String> parseJSONArrayToList(JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    /**
     * Parse strings from a {@link JSONArray} into a {@link Set}
     * 
     * @param json the JSONArray from which we parse the strings
     * @return a Set containing the strings
     * @throws JSONException if there is a problem during parsing
     */
    public static Set<String> parseJSONArrayToSet(JSONArray jsonArray) throws JSONException {
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            set.add(jsonArray.getString(i));
        }
        return set;
    }

}
