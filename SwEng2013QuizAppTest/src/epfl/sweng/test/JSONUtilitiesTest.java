
package epfl.sweng.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.util.Log;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.utils.JSONUtilities;

/**
 * @author lseguy
 *
 */

public class JSONUtilitiesTest extends AndroidTestCase {

    private QuizQuestion mQuestion;
    private Set<String> mTags;
    private static final String LOG_TAG = JSONUtilitiesTest.class.getName();
    
    public static final int CORRECT_ANSWER_ID = 2;
    public static final String QUESTION_TEXT = "Dummy question";
    public static final List<String> LIST_OF_ANSWERS = new ArrayList<String>(Arrays.asList(new String[]{"Answer 1", "Answer 2", "Answer 3"}));
    
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
        } catch (JSONException e) {
            Log.v(LOG_TAG, "JSONException in testGetJSONStringWithNullParameter()", e);
            fail("Wrong exception thrown");
        } catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testGetJSONStringWithNullParameter()", e);
        }
    }
    
    public void testGetJSONString() {
        JSONObject jsonObject;
        try {
            String jsonString = JSONUtilities.getJSONString(mQuestion);
            
            System.out.println(jsonString);
            
            jsonObject = new JSONObject(jsonString);
            assertEquals(QUESTION_TEXT, jsonObject.getString("question"));
            
           List<String> answers = JSONUtilities.parseJSONArrayToList(jsonObject.getJSONArray("answers"));
            assertTrue(answers.equals(LIST_OF_ANSWERS));
            
            assertEquals(CORRECT_ANSWER_ID, jsonObject.getInt("solutionIndex"));
            
            Set<String> tags = JSONUtilities.parseJSONArrayToSet(jsonObject.getJSONArray("tags"));
            assertEquals("Tags not equal", mTags, tags);
        } catch (JSONException e) {
            Log.v(LOG_TAG, "JSONException in testGetJSONString()", e);
            fail("JSONException while parsing the string or getting a value : " +
                e.getMessage());
        }
    }
}
