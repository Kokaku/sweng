package epfl.sweng.test;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.http.HttpStatus;
import org.json.JSONException;

import android.util.Log;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.QuestionIterator;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.utils.JSONUtilities;

/**
 * @author kokaku
 *
 */

public class QuestionIteratorTest extends QuizActivityTestCase<MainActivity> {
    
    private final MockHttpClient mockHttpClient = new MockHttpClient();
    private final QuizQuestion[] questions = new QuizQuestion[] {
            new QuizQuestion(
                    "question 1",
                    Arrays.asList(new String[]{"1", "2", "3", "4"}),
                    0,
                    new HashSet<String>(Arrays.asList(new String[] {"test"}))),
            new QuizQuestion(
                    "question 2",
                    Arrays.asList(new String[]{"a", "b", "c", "d"}),
                    1,
                    new HashSet<String>(Arrays.asList(new String[] {"test"}))),
            new QuizQuestion(
                    "question 3",
                    Arrays.asList(new String[]{"A", "B", "C", "D"}),
                    2,
                    new HashSet<String>(Arrays.asList(new String[] {"test"}))),
            new QuizQuestion(
                    "question 4",
                    Arrays.asList(new String[]{"4", "3", "2", "1"}),
                    3,
                    new HashSet<String>(Arrays.asList(new String[] {"test"})))};

    private final QuizQuestion[] newQuestions = new QuizQuestion[] {
            new QuizQuestion(
                    "question 5",
                    Arrays.asList(new String[]{"a1", "a2", "a3", "a4"}),
                    0,
                    new HashSet<String>(Arrays.asList(new String[] {"test2"})),
                    0, "me"),
            new QuizQuestion(
                    "question 6",
                    Arrays.asList(new String[]{"a1", "b2", "c3", "d4"}),
                    1,
                    new HashSet<String>(Arrays.asList(new String[] {"test2"})),
                    0, "me"),
            new QuizQuestion(
                    "question 7",
                    Arrays.asList(new String[]{"A.1", "B.2", "C.3", "D.4"}),
                    2,
                    new HashSet<String>(Arrays.asList(new String[] {"test2"})),
                    0, "me"),
            new QuizQuestion(
                    "question 8",
                    Arrays.asList(new String[]{"q", "w", "e", "r"}),
                    3,
                    new HashSet<String>(Arrays.asList(new String[] {"test2"})),
                    0, "me")};
    private static final String LOG_TAG = QuestionIteratorTest.class.getName();

    public QuestionIteratorTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
        UserCredentials.INSTANCE.setState(AuthenticationState.AUTHENTICATED);
        UserCredentials.INSTANCE.saveUserCredentials("test");

        SwengHttpClientFactory.setInstance(mockHttpClient);
        mockHttpClient.pushCannedResponse(
                "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
                HttpStatus.SC_OK, "", "application/json");
    }

    @Override
    protected void tearDown() throws Exception {
        mockHttpClient.clearCannedResponses();
        super.tearDown();
    }
    
    public void testCannotConstructWithoutQuestion() {
        try {
            QuizQuestion[] question = null;
            new QuestionIterator(question);
            fail("QuestionIterator constructed without any questions");
        } catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCannotConstructWithoutQuestion()", e);
        }
        
        try {
            new QuestionIterator(null, null, null);
            fail("QuestionIterator constructed without any questions");
        } catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCannotConstructWithoutQuestion()", e);
        }
    }
    
    public void testCannotConstructWithoutQueryIfNextSetted() {
        try {
            new QuestionIterator(questions, null, "next");
            fail("QuestionIterator constructed without any questions");
        } catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "IllegalArgumentException in testCannotConstructWithoutQueryIfNextSetted()", e);
        }
    }
    
    public void testHasTheCorrectNumberOfQuestion() {
        QuestionIterator iterator = new QuestionIterator(questions);
        
        int counter = 0;
        while (iterator.hasNext()) {
            try {
                iterator.next();
            } catch (Exception e) {
                Log.v(LOG_TAG, "Exception in testHasTheCorrectNumberOfQuestion()", e);
                fail("Unexpected exception");
            }
            counter++;
        }
        
        assertTrue(counter == questions.length);
    }
    
    public void testNextReturnQuestionInCorrectOrder() {
        QuestionIterator iterator = new QuestionIterator(questions);
        
        int counter = 0;
        while (counter < questions.length && iterator.hasNext()) {
            try {
                assertTrue(questions[counter] == iterator.next());
            } catch (Exception e) {
                Log.v(LOG_TAG, "Exception in testNextReturnQuestionInCorrectOrder()", e);
                fail("Unexpected exception");
            }
            counter++;
        }
    }
    
    public void testGetLocalQuestionsReturnCorrectArrayOfQuestions() {
        QuestionIterator iterator = new QuestionIterator(questions);
        
        QuizQuestion[] retArray = iterator.getLocalQuestions();
        assertTrue(retArray.length == questions.length);
        for (int i = 0; i < retArray.length; i++) {
            assertTrue(retArray[i] == questions[i]);
        }
    }
    
    private void pushNextQuestions() throws JSONException {
        String JSONQuestion = "{\"questions\": [ ";
        StringBuffer buf = new StringBuffer();
        for(QuizQuestion question: newQuestions) {
            buf.append(JSONUtilities.getJSONString(question)+", ");
        }
        JSONQuestion += buf.toString();
        JSONQuestion = JSONQuestion.substring(0, JSONQuestion.length()-2)+
                " ]}";
        
        mockHttpClient.clearCannedResponses();
        mockHttpClient.pushCannedResponse(
            "POST [^/]+", HttpStatus.SC_OK, JSONQuestion, null);
    }
    
    private boolean questionsEquals(QuizQuestion q1, QuizQuestion q2) {
        return q1.getId()==q2.getId() &&
               q1.getOwner().equals(q2.getOwner()) &&
               q1.getQuestion().equals(q2.getQuestion()) &&
               q1.getSolutionIndex() == q2.getSolutionIndex() &&
               q1.getAnswers().equals(q2.getAnswers()) &&
               q1.getTags().equals(q2.getTags());
    }
    
    public void testWhenNextQuestionsFetchCorrectNumberOfQuestion() {
        try {
            pushNextQuestions();
        } catch (JSONException e1) {
            Log.v(LOG_TAG, "JSONException in testWhenNextQuestionsFetchCorrectNumberOfQuestion()", e1);
            fail("Problem with mock");
        }
        
        QuestionIterator iterator = new QuestionIterator(questions, "banana + patatos", "42");
        for (int i = 0; i < questions.length; i++) {
            try {
                iterator.next();
            } catch (Exception e) {
                Log.v(LOG_TAG, "Exception in testWhenNextQuestionsFetchCorrectNumberOfQuestion()", e);
                fail("Unexpected exception");
            }
        }
        
        int counter = 0;
        while (iterator.hasNext()) {
            try {
                iterator.next();
            } catch (Exception e) {
                Log.v(LOG_TAG, "Exception in testWhenNextQuestionsFetchCorrectNumberOfQuestion()", e);
                fail("Problem with mock");
            }
            counter++;
        }
        
        assertTrue(counter == newQuestions.length);
    }
    
    public void testNextQuestionsFetchCorrectly() {
        try {
            pushNextQuestions();
        } catch (JSONException e1) {
            Log.v(LOG_TAG, "JSONException in testNextQuestionsFetchCorrectly()", e1);
            fail("Problem with mock");
        }
        QuestionIterator iterator = new QuestionIterator(questions, "banana + patatos", "42");
        for (int i = 0; i < questions.length; i++) {
            try {
                iterator.next();
            } catch (Exception e) {
                Log.v(LOG_TAG, "Exception in testNextQuestionsFetchCorrectly()", e);
                fail("Unexpected exception");
            }
        }
        
        int counter = 0;
        while (counter < newQuestions.length && iterator.hasNext()) {
            try {
                assertTrue(questionsEquals(newQuestions[counter], iterator.next()));
            } catch (Exception e) {
                Log.v(LOG_TAG, "Exception in testNextQuestionsFetchCorrectly()", e);
                fail("Problem with mock");
            }
            counter++;
        }
    }
    
    public void testWhenNextQuestionsFetchReturnCorrectArrayOfQuestions() {
        try {
            pushNextQuestions();
        } catch (JSONException e1) {
            Log.v(LOG_TAG, "JSONException in testWhenNextQuestionsFetchReturnCorrectArrayOfQuestions()", e1);
            fail("Problem with mock");
        }
        QuestionIterator iterator = new QuestionIterator(questions, "banana + patatos", "42");
        for (int i = 0; i <= questions.length; i++) {
            try {
                iterator.next();
            } catch (Exception e) {
                Log.v(LOG_TAG, "Exception in testWhenNextQuestionsFetchReturnCorrectArrayOfQuestions()", e);
                fail("Unexpected exception");
            }
        }
        
        QuizQuestion[] retArray = iterator.getLocalQuestions();
        assertTrue(retArray.length == newQuestions.length);
        for (int i = 0; i < retArray.length; i++) {
            assertTrue(questionsEquals(retArray[i], newQuestions[i]));
        }
    }
}
