/**
 * 
 */
package epfl.sweng.test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import epfl.sweng.questions.QuizQuestion;
import epfl.sweng.utils.JSONUtilities;

/**
 * @author lseguy
 *
 */
public class JSONUtilitiesTest extends AndroidTestCase {

    private QuizQuestion mQuestion;
    private Set<String> mTags;
    
    public static final int CORRECT_ANSWER_ID = 2;
    public static final String QUESTION_TEXT = "Dummy question";
    public static final String[] LIST_OF_ANSWERS = {"Answer 1", "Answer 2", "Answer 3"};
    
    protected void setUp() throws Exception {
        super.setUp();
        
        mTags = new TreeSet<String>();
        mTags.add("Bim");
        mTags.add("Bam");
        mQuestion = new QuizQuestion(QUESTION_TEXT, LIST_OF_ANSWERS,
            CORRECT_ANSWER_ID, mTags);
    }
    
    public void testGetJSONStringWithNullParameter() {
        try {
            JSONUtilities.getJSONString(null);
            fail("getJSONString can't accept a null argument");
        } catch (IllegalArgumentException e) { }
    }
    
    // Refactor this !
    // Add strings to asserts
    public void testGetJSONString() {
        String jsonString = JSONUtilities.getJSONString(mQuestion);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            assertEquals(QUESTION_TEXT, jsonObject.getString("question"));
            JSONArray jsonAnswers = jsonObject.getJSONArray("answers");
            String[] answers = new String[jsonAnswers.length()];
            for (int i = 0; i < jsonAnswers.length(); ++i) {
                answers[i] = jsonAnswers.getString(i);
            }
            assertTrue(Arrays.equals(LIST_OF_ANSWERS, answers));
            assertEquals(CORRECT_ANSWER_ID, jsonObject.getInt("solutionIndex"));
            JSONArray jsonTags = jsonObject.getJSONArray("tags");
            Set<String> tags = new TreeSet<String>();
            for (int i = 0; i < jsonTags.length(); ++i) {
                tags.add(jsonTags.getString(i));
            }
            assertEquals("Tags not equal", mTags, tags);
        } catch (JSONException e) {
            fail("JSONException while parsing the string or getting a value : " +
                e.getMessage());
        }
    }

    // TODO : I'm working on it (Louis)
    
}
