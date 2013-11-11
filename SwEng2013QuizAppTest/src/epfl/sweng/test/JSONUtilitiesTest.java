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
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.utils.JSONUtilities;

/**
 * @author lseguy
 *
 */
public class JSONUtilitiesTest extends AndroidTestCase {

//    private QuizQuestion mQuestion;
//    private Set<String> mTags;
//    
//    public static final int CORRECT_ANSWER_ID = 2;
//    public static final String QUESTION_TEXT = "Dummy question";
//    public static final String[] LIST_OF_ANSWERS = {"Answer 1", "Answer 2", "Answer 3"};
//    
//    protected void setUp() throws Exception {
//        super.setUp();
//        
//        mTags = new TreeSet<String>();
//        mTags.add("Bim");
//        mTags.add("Bam");
//        mQuestion = new QuizQuestion(QUESTION_TEXT, LIST_OF_ANSWERS,
//            CORRECT_ANSWER_ID, mTags);
//    }
//    
//    public void testGetJSONStringWithNullParameter() {
//        try {
//            JSONUtilities.getJSONString(null);
//            fail("getJSONString can't accept a null argument");
//        } catch (JSONException e) {
//            fail("Wrong exception thrown");
//        } catch (IllegalArgumentException e) {
//        }
//    }
//    
//    public void testGetJSONString() {
//        JSONObject jsonObject;
//        try {
//            String jsonString = JSONUtilities.getJSONString(mQuestion);
//            
//            System.out.println(jsonString);
//            
//            jsonObject = new JSONObject(jsonString);
//            assertEquals(QUESTION_TEXT, jsonObject.getString("question"));
//            
//            String[] answers = parseAnswers(jsonObject.getJSONArray("answers"));
//            assertTrue(Arrays.equals(LIST_OF_ANSWERS, answers));
//            
//            assertEquals(CORRECT_ANSWER_ID, jsonObject.getInt("solutionIndex"));
//            
//            Set<String> tags = parseTags(jsonObject.getJSONArray("tags"));
//            assertEquals("Tags not equal", mTags, tags);
//        } catch (JSONException e) {
//            fail("JSONException while parsing the string or getting a value : " +
//                e.getMessage());
//        }
//    }
//    
//    public void testGetJSONStringWithQuotesAndBackslashes() {
//        String dangerousString = "I have some \"quotes\" and \\backslashes\\";
//        QuizQuestion dangerousQuestion = new QuizQuestion(dangerousString, 
//            LIST_OF_ANSWERS, CORRECT_ANSWER_ID, mTags);
//        
//        JSONObject jsonObject;
//        try {
//            String jsonString = JSONUtilities.getJSONString(dangerousQuestion);
//            jsonObject = new JSONObject(jsonString);
//            assertEquals(dangerousString, jsonObject.getString("question"));
//        } catch (JSONException e) {
//            fail("JSONException while parsing the string or getting a value : " +
//                e.getMessage());
//        }
//    }
//    
//    private String[] parseAnswers(JSONArray jsonAnswers)
//        throws JSONException {
//        if (jsonAnswers == null) {
//            throw new IllegalArgumentException();
//        }
//        
//        int length = jsonAnswers.length();
//        String[] answers = new String[length];
//        
//        for (int i = 0; i < length; ++i) {
//            answers[i] = jsonAnswers.getString(i);
//        }
//        
//        return answers;
//    }
//    
//    private Set<String> parseTags(JSONArray jsonTags)
//        throws JSONException {
//        if (jsonTags == null) {
//            throw new IllegalArgumentException();
//        }
//        
//        int length = jsonTags.length();
//        Set<String> tags = new TreeSet<String>();
//        
//        for (int i = 0; i < length; ++i) {
//            tags.add(jsonTags.getString(i));
//        }
//        
//        return tags;
//    }
    
}
