package epfl.sweng.offline;

import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_ANSWERS;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_ID;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_OWNER;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_QUESTION;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_QUESTION_ID;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_SOLUTION;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_SUBMIT;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_TAGS;
import static epfl.sweng.offline.CachedQuestionsTable.TABLE_NAME;

import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.QuestionIterator;
import epfl.sweng.servercomm.ServerCommunication;
import epfl.sweng.utils.JSONUtilities;

/**
 * Manages creation and access to the local database used for offline mode.
 * 
 * @author lseguy
 *
 */
public final class DatabaseHandler extends SQLiteOpenHelper {
    
    private static final String LOG_TAG = DatabaseHandler.class.getName();

    private static final String DATABASE_NAME = "quizquestions.db";
    private static final int DATABASE_VERSION = 4;
    private static final int MAX_RESPONSE_NUMBER = 10;
    
    private static DatabaseHandler instance;
    private SQLiteDatabase db;
    
    private DatabaseHandler() {
        super(SwEng2013QuizApp.getAppContext(), DATABASE_NAME, null,
            DATABASE_VERSION);
        db = getWritableDatabase();
    }
    
    public static synchronized DatabaseHandler getHandler() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        
        return instance;
    }
    
    @Override
    public void onCreate(SQLiteDatabase database) {
        CachedQuestionsTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        CachedQuestionsTable.onUpgrade(database, oldVersion, newVersion);
    }
    
    /**
     * Clears all the cached data.
     */
    public void clearCache() {
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    /**
     * Stores a question in the database. If the question is already in the
     * database, do nothing.
     * 
     * @param question the question to be stored
     */
    public void storeQuestion(QuizQuestion question, boolean toBeSubmitted)
        throws DBException {
        
        // Log.d(LOG_TAG, "Caching the question " + question);
        
        ContentValues values = new ContentValues();
        
        // Check if there is no id for this question
        if (question.getId() == 0) {
            values.putNull(COLUMN_QUESTION_ID);
        } else {
            values.put(COLUMN_QUESTION_ID, question.getId());
        }
        
        values.put(COLUMN_QUESTION, question.getQuestion());
        JSONArray answers = new JSONArray(question.getAnswers());
        values.put(COLUMN_ANSWERS, answers.toString());
        values.put(COLUMN_SOLUTION, question.getSolutionIndex());
        JSONArray tags = new JSONArray(question.getTags());
        values.put(COLUMN_TAGS, tags.toString());
        values.put(COLUMN_SUBMIT, toBeSubmitted ? 1 : 0);
        
        // Check if there is no owner for this question
        if (question.getOwner() == null) {
            values.putNull(COLUMN_OWNER);
        } else {
            values.put(COLUMN_OWNER, question.getOwner());
        }
                
        db.insertWithOnConflict(TABLE_NAME, null, values,
                 SQLiteDatabase.CONFLICT_IGNORE);
        
        // Log.d(LOG_TAG, "Request successful : " + requestSuccessfull);
        
//        if (!requestSuccessfull) {
//            throw new DBException("Could not store the question.");
//        }
    }
    
    /**
     * Gets a random question from the database.
     * 
     * @return a random question or null if there is no cached question
     * @throws DBException if the request is unsuccessful
     */
    public QuizQuestion getRandomQuestion()
        throws DBException {
        
        // Log.d(LOG_TAG, "Getting a question from cache.");
        
        // Get a random question
        Cursor cursor = db.rawQuery("SELECT * FROM " +
            TABLE_NAME + " ORDER BY RANDOM() LIMIT 1;", null);
        
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        
        try {
            QuizQuestion question = getQuestionFromCursor(cursor);
            // Log.d(LOG_TAG, "This question has been fetched from cache : " + question);
            return question;
        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSONException in getRandomQuestion()", e);
            throw new DBException("JSON badly formatted.");
        } finally {
            cursor.close();
        }
    }
    
    /**
     * Sends the question waiting for submission to the server.
     * 
     * @return the number of questions submitted
     * @throws DBException if a DB request is unsuccessful
     * @throws ServerCommunicationException if a question can't be submitted
     */
    public int synchronizeQuestions()
        throws DBException, ServerCommunicationException {
        
//         Log.d(LOG_TAG, "Synchronizing questions");
        
        int questionsSumbmitted = 0;
        
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_SUBMIT + "=1",
            null, null, null, null);
        
        if (cursor == null) {
            throw new DBException("Error while looking for questions waiting for submission");
        }
        
        try {
            while (cursor.moveToNext()) {
                QuizQuestion question = getQuestionFromCursor(cursor);
                QuizQuestion updatedQuestion = ServerCommunication.INSTANCE.send(question);

                if (updatedQuestion == null) {
                    // TODO
                    // Log.d(LOG_TAG, "Error during sync. The updated question is null");
                }
                
                // Log.d(LOG_TAG, "The updated question is : " + updatedQuestion);
                
                /*
                 * Update the question in cache : add the assigned id and owner
                 * and remove the submission flag.
                 */
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                
                ContentValues values = new ContentValues();
                values.put(COLUMN_QUESTION_ID, updatedQuestion.getId());
                values.put(COLUMN_OWNER, updatedQuestion.getOwner());
                values.put(COLUMN_SUBMIT, 0);
                db.updateWithOnConflict(TABLE_NAME, values , COLUMN_ID + "=?",
                		new String[] {String.valueOf(id)}, SQLiteDatabase.CONFLICT_IGNORE);
                ++questionsSumbmitted;
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSONException in synchronizeQuestions()", e);
            throw new DBException("JSON badly formatted.");
        } finally {
            cursor.close();
        }
        
        // Log.d(LOG_TAG, questionsSumbmitted + " questions have been submitted.");
        return questionsSumbmitted;
    }
    
    private QuizQuestion getQuestionFromCursor(Cursor cursor)
        throws JSONException {
        int idIndex = cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID);
        int questionIndex = cursor.getColumnIndexOrThrow(COLUMN_QUESTION);
        int answersIndex = cursor.getColumnIndexOrThrow(COLUMN_ANSWERS);
        int solutionIndex = cursor.getColumnIndexOrThrow(COLUMN_SOLUTION);
        int ownerIndex = cursor.getColumnIndexOrThrow(COLUMN_OWNER);
        int tagsIndex = cursor.getColumnIndexOrThrow(COLUMN_TAGS);
        
        JSONArray answers = new JSONArray(cursor.getString(answersIndex));
        JSONArray tags = new JSONArray(cursor.getString(tagsIndex));

        
        // Id is 0 if the question has no id.
        long id = cursor.isNull(idIndex) ? 0 : cursor.getLong(idIndex);
        // Owner is null if the question has no owner.
        String owner = cursor.isNull(ownerIndex) ? null : cursor.getString(ownerIndex);

        return new QuizQuestion(cursor.getString(questionIndex),
                                JSONUtilities.parseJSONArrayToList(answers),
                                cursor.getInt(solutionIndex),
                                JSONUtilities.parseJSONArrayToSet(tags),
                                id,
                                owner);
    }

    /**
     * This method searches a question in the Database if the device is off-line
     * and queries some set of question.
     * 
     * @param query: query in the proposed language (SwEngQL)
     * @param next: position to the next set of searched questions
     * @return a {@link QuestionIterator} with the questions retrieved from
     *         the database
     * @throws DBException if the database couldn't retrieve a question
     */
    public QuestionIterator searchQuestion(String query, String next)
        throws DBException, IllegalArgumentException {
            
        if (next == null || next.length() == 0) {
            next = "0";
        } else if (!next.matches("\\d+")) { //Check if next is a positive int
            throw new IllegalArgumentException("Pointer next is not valid");
        }
        
        String limit = "ORDER BY "+ COLUMN_ID +" ASC "+
                "LIMIT "+next+", "+ (MAX_RESPONSE_NUMBER+1);
        String querySQL = parseQuerytoSQL(query) + limit;
        
        Cursor cursor = db.rawQuery(querySQL, null);
        int arraySize = Math.min(cursor.getCount(), MAX_RESPONSE_NUMBER);
        QuizQuestion[] questions = new QuizQuestion[arraySize];
        String newNext = null;
        
        try {
            for (int i = 0; i<questions.length && cursor.moveToNext(); i++) {
                questions[i] = getQuestionFromCursor(cursor);
            }
            if (cursor.moveToNext()) {
                newNext = Integer.toString(Integer.parseInt(next)+MAX_RESPONSE_NUMBER);
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSONException in searchQuestion()", e);
            throw new DBException("Couldn't retrieve question from the Database");
        } finally {
            cursor.close();
        }
        
        return new QuestionIterator(questions, query, newNext);
    }

    /**
     * This method parses a SwEngQL query sanitized to a SQLite query:
     *   - * operator is not optional
     *   - spaces wrapping each operator, term and parenthesis
     *   
     * Example of valid query: ( banana + garlic ) * fruit
     * Examples of non valid query: 
     *   - (banana + garlic ) * fruit
     *   - ( banana + garlic ) fruit
     *   - ( banana +garlic )fruit
     * It selects always all attributes from columns COLUMN_QUESTION,
     * COLUMN_ANSWERS or COLUMN_TAGS ordered by COLUMN_ID
     * 
     * @param query: a SwEngQL query
     * @return querySQLite: query parsed from SwEngQL to SQLite
     */
    private String parseQuerytoSQL(String query) {
        StringTokenizer queryTokenizer = new StringTokenizer(query, " ");
        StringBuilder querySQLite = new StringBuilder("SELECT * FROM " + TABLE_NAME + " WHERE");
        String nextToken = null;
        
        while (queryTokenizer.hasMoreTokens()) {
            nextToken = queryTokenizer.nextToken();
            if (nextToken.isEmpty()) {
            } else if (nextToken.equals("(") || nextToken.equals(")")) {
                querySQLite.append(" " + nextToken + " ");
            } else if (nextToken.equals("*")) {
                querySQLite.append(" AND ");
            } else if (nextToken.equals("+")) {
                querySQLite.append(" OR ");
            } else {
                querySQLite.append(" ( "+ COLUMN_QUESTION +" LIKE '%"+ nextToken +"%' OR "+
                        COLUMN_ANSWERS +" LIKE '%"+ nextToken +"%' OR "+
                        COLUMN_TAGS +" LIKE '%"+ nextToken +"%' ) ");
            }
        }
        return querySQLite.toString();
    }
}
