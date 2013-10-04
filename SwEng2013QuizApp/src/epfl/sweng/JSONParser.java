/**
 * 
 */
package epfl.sweng;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class of utility methods to parse JSONObjects to String, String[], Set<String>
 * 
 * @author ValentinRutz
 *
 */
public class JSONParser {

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
     * The question and the tags are double-checked
     *  with the method {@link #protectTwiceSpecialChars(String)}
     *  
     * @param question to format
     * @return String representing the formatted question
     */
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
    
    /**
     * Converts an {@link Iterable} to a String in the following format:
     *  "[ \"<element_1>\", \"<element_2\" , ... , \"<element_n>\" ]"
     *  
     * @param iterable to be formatted
     * @return String representing the formatted {@link Iterable}
     */
    public static String convertIterableToJSONString(Iterable<String> iterable) {
        String jsonString = "[";
        for (String element : iterable) {
            jsonString += " \"" + protectTwiceSpecialChars(element) + "\",";
        }
        jsonString = jsonString.substring(0, jsonString.length()-1);
        jsonString += " ]";
        
        return jsonString;
    }
    
    /**
     * Parse answers from {@link JSONObject} into a {@link String[]}
     * 
     * @param json the object from which we parse the answers
     * @return String[] of the answers
     * @throws JSONException if there is a problem getting the {@link JSONArray}
     */
    public static String[] parseAnswers(JSONObject json) throws JSONException {
        JSONArray jsonAnswers = json.getJSONArray("answers");
        String[] answers = new String[jsonAnswers.length()];
        for (int i = 0; i < jsonAnswers.length(); i++) {
            answers[i] = jsonAnswers.getString(i);
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
    
    /**
     * Protects twice any special char that could interfere with the translation
     * of the question or tags on the server, i.e: \ " '
     * @param str the {@link String} to check 
     * @return a {@link String} where the chars presented above are protected twice
     */
    private static String protectTwiceSpecialChars(String str) {
        return str.replace("\\", "\\\\").
                replace("\"", "\\\"").
                replace("\'", "\\\'");
    }
}
