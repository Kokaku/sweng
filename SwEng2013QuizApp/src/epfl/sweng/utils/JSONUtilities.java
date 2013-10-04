/**
 * 
 */
package epfl.sweng.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.questions.QuizQuestion;

/**
 * @author ValentinRutz
 *
 */
public class JSONUtilities {

    public static String getJSONString(QuizQuestion question) {
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
    
    public static String convertIterableToJSONString(Iterable<String> iterable) {
        String jsonString = "[";
        for (String element : iterable) {
            jsonString += " \"" + protectTwiceSpecialChars(element) + "\",";
        }
        jsonString = jsonString.substring(0, jsonString.length()-1);
        jsonString += " ]";
        
        return jsonString;
    }
    
    public static String[] parseAnswers(JSONObject json) throws JSONException {
        JSONArray jsonAnswers = json.getJSONArray("answers");
        String[] answers = new String[jsonAnswers.length()];
        for (int i = 0; i < jsonAnswers.length(); i++) {
            answers[i] = jsonAnswers.getString(i);
        }
        return answers;
    }

    public static Set<String> parseTags(JSONObject json) throws JSONException {
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
