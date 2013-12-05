package epfl.sweng.test;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpStatus;

import android.test.AndroidTestCase;
import android.util.Log;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.offline.DatabaseHandler;
import epfl.sweng.patterns.Proxy;
import epfl.sweng.patterns.Proxy.ConnectionState;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.QuestionIterator;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;

/**
 * @author ValentinRutz
 *
 */
public class DatabaseHandlerTest extends AndroidTestCase {
    
    private static final String LOG_TAG = DatabaseHandlerTest.class.getName();
    
    private QuizQuestion mQuestion;
    private String mQuestionText = "How many rings the Olympic flag Five has?";
    private String[] mAnswers = {"One", "Six", "Five"};
    private Set<String> mTags = new TreeSet<String>();
    private int mSolutionIndex = 2;
    private int mID = 0;
    private String mOwner = "Dr. Sheldon Cooper";
    private String mQuery;
    private String mNext;
    private QuestionIterator mIterator;
    
    private DatabaseHandler db;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        db = DatabaseHandler.getHandler();
        mTags.add("Olympics");
        mTags.add("FunWithFlags");
        mQuestion = new QuizQuestion(mQuestionText, Arrays.asList(mAnswers),
                mSolutionIndex, mTags, mID, mOwner);
        
        UserCredentials.INSTANCE.setState(AuthenticationState.AUTHENTICATED);
        UserCredentials.INSTANCE.saveUserCredentials("test");
        
        db.clearCache();
        try {
            db.storeQuestion(mQuestion, false);
        } catch (DBException e) {
            Log.v(LOG_TAG, "DBException in setUp()", e);
            fail("Problem storing the question");
        }
        Proxy.INSTANCE.setState(ConnectionState.OFFLINE);
    }
    
    @Override
    public void tearDown() throws Exception {
        db.clearCache();
        super.tearDown();   
    }
    
    public void testQueryWithMultipleConsecutiveSpaces() {
        mQuery = "( Olympics +     Five ) * flag";
        mNext = null;try {
            mIterator = db.searchQuestion(mQuery, mNext);
        } catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "Exception in testQueryWithMultipleConsecutiveSpaces()", e);
            fail("next should be valid: "+ e.getMessage());
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in testQueryWithMultipleConsecutiveSpaces()", e);
            fail("Should not be DBException: "+ e.getMessage());
        }
        try {
            assertTrue("Questions should be the same",
                    compareQuestions(mIterator.next(), mQuestion, true));
        } catch (NoSuchElementException e) {
            Log.v(LOG_TAG, "Exception in testQueryWithMultipleConsecutiveSpaces()", e);
            fail("NoSuchElementException: "+ e.getMessage());
        } catch (NotLoggedInException e) {
            Log.v(LOG_TAG, "Exception in testQueryWithMultipleConsecutiveSpaces()", e);
            fail("NotLoggedInException: "+ e.getMessage());
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in testQueryWithMultipleConsecutiveSpaces()", e);
            fail("DBException: "+ e.getMessage());
        } catch (ServerCommunicationException e) {
            Log.v(LOG_TAG, "Exception in testQueryWithMultipleConsecutiveSpaces()", e);
            fail("ServerCommunicationException: "+ e.getMessage());
        }
    }
    
    public void testGetRandomQuestion() {
        assertTrue("Questions should be the same",
                compareQuestions(getNewQuestionFromDB(), mQuestion, true));
    }
    
    public void testSynchronizeQuestions() {
        try {
            db.storeQuestion(mQuestion, true);
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in testSynchronizeQuestions()", e);
            fail("Problem storing the question");
        }
        MockHttpClient mockHttpClient = new MockHttpClient();
        mockHttpClient.clearCannedResponses();
        SwengHttpClientFactory.setInstance(mockHttpClient);
        mockHttpClient.pushCannedResponse("POST [^/]+", HttpStatus.SC_CREATED,
                                          mQuestion.toString(), null);

        try {
            int nbSentQuestions = db.synchronizeQuestions();
            assertTrue("DB should have send a question to server, instead sent "+ nbSentQuestions,
                    nbSentQuestions == 1);
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in testSynchronizeQuestions()", e);
            fail("testSynchronizeQuestions during synchronizing"+ e.getMessage());
        } catch (ServerCommunicationException e) {
            Log.v(LOG_TAG, "Exception in testSynchronizeQuestions()", e);
            fail("Question can't be submitted");
        }
    }
    
    public void testNextGiveNextQuestionSet() {
        db.clearCache();
        for(int i = 0; i < 11; i++) {
            try {
                db.storeQuestion(new QuizQuestion(mQuestionText,
                        Arrays.asList(mAnswers),
                        mSolutionIndex, mTags, i, mOwner), false);
            } catch (DBException e) {
                Log.v(LOG_TAG, "Exception in testNextGiveNextQuestionSet()", e);
                fail("DBException: "+ e.getMessage());
            } 
        }
        mQuery = "( Olympics + Five ) * flag";
        mNext = null;
        try {
            mIterator = db.searchQuestion(mQuery, mNext);
        } catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "Exception in testNextGiveNextQuestionSet()", e);
            fail("next should be valid: "+ e.getMessage());
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in testNextGiveNextQuestionSet()", e);
            fail("Should not be DBException: "+ e.getMessage());
        }
        int i = 0;
        for(i = 0; mIterator.hasNext(); i++) {
            try {
                assertTrue("Question "+ i +" should be the same as "+mQuestion,
                        compareQuestions(mQuestion, mIterator.next(), false));
            } catch (NoSuchElementException e) {
                Log.v(LOG_TAG, "Exception in testNextGiveNextQuestionSet()", e);
                fail("NoSuchElementException: "+ e.getMessage());
            } catch (NotLoggedInException e) {
                Log.v(LOG_TAG, "Exception in testNextGiveNextQuestionSet()", e);
                fail("NotLoggedInException: "+ e.getMessage());
            } catch (DBException e) {
                Log.v(LOG_TAG, "Exception in testNextGiveNextQuestionSet()", e);
                fail("DBException: "+ e.getMessage());
            } catch (ServerCommunicationException e) {
                Log.v(LOG_TAG, "Exception in testNextGiveNextQuestionSet()", e);
                fail("ServerCommunicationException: "+ e.getMessage());
            }
        }
        assertTrue("Should have fetched 11 questions form DB", i == 11);
    }
    
    public void testInvalidNext() {
        mQuery = "( Olympics + Five ) * flag";
        mNext = "9p234dxchgf2345678dfghj34567dfgh";
        try {
            mIterator = db.searchQuestion(mQuery, mNext);
            fail("Should have IllegalArgumentException with invalid next");
        } catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "Exception in testInvalidNext()", e);
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in testInvalidNext()", e);
            fail("testSearchQuestionValidQueryWithoutMatch: DBException");
        }
    }
    
    public void testSearchQuestionValidQueryWithMatch() {
        mQuery = "( Olympics + Five ) * flag";
        mNext = null;
        try {
            mIterator = db.searchQuestion(mQuery, mNext);
            assertTrue("Should be a question for query with matches",
                    mIterator.getLocalQuestions().length != 0);
        } catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "Exception in testSearchQuestionValidQueryWithMatch()", e);
            fail("next should be valid");
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in testSearchQuestionValidQueryWithMatch()", e);
            fail("testSearchQuestionValidQueryWithMatch: DBException");
        }
    }
    
    public void testSearchQuestionValidQueryWithoutMatch() {
        mQuery = "( banana + garlic ) * fruit";
        mNext = null;
        try {
            mIterator = db.searchQuestion(mQuery, mNext);
            assertTrue("Should be no questions for query without matches",
                    mIterator.getLocalQuestions().length == 0);
        } catch (IllegalArgumentException e) {
            Log.v(LOG_TAG, "Exception in testSearchQuestionValidQueryWithoutMatch()", e);
            fail("next should be valid");
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in testSearchQuestionValidQueryWithoutMatch()", e);
            fail("testSearchQuestionValidQueryWithoutMatch: DBException");
        }
    }
    
    public void testClearCache() {
        db.clearCache();
        assertTrue("Should be not questions left in db after clearing the cache",
                getNewQuestionFromDB() == null);
    }
    
    public void testStoreQuestion() {
        db.clearCache();
        try {
            db.storeQuestion(mQuestion, false);
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in testStoreQuestion()", e);
            fail("testStoreQuestion: DBException");
        }
        
        QuizQuestion newQuestion = getNewQuestionFromDB();
        assertFalse("Question should not be null", newQuestion == null);
        
        assertTrue("Questions should be the same",
                compareQuestions(newQuestion, mQuestion, true));
    }
    
    public void testConcurrentWriting() {
        db.clearCache();
        
        class Writer implements Runnable {
            private int id;
            
            public Writer(int id) {
                this.id = id;
            }
            
            public void run() {
                for (int i = 0; i < 500; ++i) {
                    QuizQuestion question = new QuizQuestion(mQuestionText, Arrays.asList(mAnswers),
                        mSolutionIndex, mTags, id + i, mOwner);
                    try {
                        db.storeQuestion(question, false);
                    } catch (DBException e) {
                        Log.v(LOG_TAG, "Exception in testConcurrentWriting()", e);
                        fail("testConcurrentWriting DBException : " + e);
                    }
                }
            }
            
        }
        
        Thread t1 = new Thread(new Writer(1));
        Thread t2 = new Thread(new Writer(1000));
        Thread t3 = new Thread(new Writer(2000));
        t1.run();
        t2.run();
        t3.run();
    }
    
    private QuizQuestion getNewQuestionFromDB() {
        try {
            return db.getRandomQuestion();
        } catch (DBException e) {
            Log.v(LOG_TAG, "Exception in getNewQuestionFromDB()", e);
            fail("getNewQuestionFromDB: DBException");
        }
        
        return null;
    }
    
    private boolean compareQuestions(QuizQuestion question1, QuizQuestion question2, boolean compareID) {
        return question1 == question2 ||
                (question1.getQuestion().equals(question2.getQuestion()) &&
                 question1.getOwner().equals(question2.getOwner()) &&
                 (compareID ? question1.getId() == question2.getId() : true) &&
                 question1.getTags().equals(question2.getTags()) &&
                 question1.getAnswers().equals(question2.getAnswers()) &&
                 question1.getSolutionIndex() == question2.getSolutionIndex());
    }
}
