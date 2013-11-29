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
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quizquestions.db";
    private static final int DATABASE_VERSION = 4;
    private static final int MAX_QUESTIONS = 50;
    
    public DatabaseHandler() {
        super(SwEng2013QuizApp.getAppContext(), DATABASE_NAME, null,
            DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        CachedQuestionsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CachedQuestionsTable.onUpgrade(db, oldVersion, newVersion);
    }
    
    /**
     * Clears all the cached data.
     */
    public void clearCache() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }

    /**
     * Stores a question in the database. If the question is already in the
     * database, do nothing.
     * 
     * @param question the question to be stored
     */
    public void storeQuestion(QuizQuestion question, boolean toBeSubmitted)
        throws DBException {
        
        Log.d("POTATO DB", "Caching the question " + question);
        
        SQLiteDatabase db = getWritableDatabase();
        
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
                
        boolean requestSuccessfull = db.insertWithOnConflict(TABLE_NAME, null, values,
            SQLiteDatabase.CONFLICT_IGNORE) != -1;
        
        Log.d("POTATO DB", "Request successful : " + requestSuccessfull);
        
        db.close();
        
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
        
        Log.d("POTATO DB", "Getting a question from cache.");
        
        SQLiteDatabase db = getReadableDatabase();
        
        // Get a random question
        Cursor cursor = db.rawQuery("SELECT * FROM " +
            TABLE_NAME + " ORDER BY RANDOM() LIMIT 1;", null);
        
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        
        try {
            QuizQuestion question = getQuestionFromCursor(cursor);
            Log.d("POTATO DB", "This question has been fetched from cache : " + question);
            return question;
        } catch (JSONException e) {
            throw new DBException("JSON badly formatted.");
        } finally {
            cursor.close();
            db.close();
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
        
        Log.d("POTATO DB", "Synchronizing questions");
        
        int questionsSumbmitted = 0;
        SQLiteDatabase db = getWritableDatabase();
        
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
                    Log.d("POTATO DB", "Error during sync. The updated question is null");
                }
                
                Log.d("POTATO DB", "The updated question is : " + updatedQuestion);
                
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
            throw new DBException("JSON badly formatted.");
        } finally {
            cursor.close();
            db.close();
        }
        
        Log.d("POTATO DB", questionsSumbmitted + " questions have been submitted.");
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
        throws DBException {
        String querySQL = "";
        
        if (query != null) {
            querySQL = parseQuerytoSQL(query +" LIMIT "+ MAX_QUESTIONS);
        }
        
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(querySQL, null);
        
        QuizQuestion[] questions = new QuizQuestion[MAX_QUESTIONS];
        
        if (cursor == null || cursor.getCount() == 0) {
            return new QuestionIterator(questions, query, null);
        }
        
        int nextPosition = 0;
        if (next != null && next.equals("")) {
            nextPosition = Integer.parseInt(next);
            cursor.moveToPosition(nextPosition);
        }
        
        try {
            for (int i = 0; i < MAX_QUESTIONS && cursor.moveToNext(); i++) {
                questions[i] = getQuestionFromCursor(cursor);
            }
            nextPosition = cursor.getPosition();
            
        } catch (JSONException e) {
            throw new DBException("Couldn't retrieve question from the Database");
        } finally {
            cursor.close();
            db.close(); 
        }
        
        return new QuestionIterator(questions, query, nextPosition + "");
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
     *   - etc...
     * It selects always all attributes from columns COLUMN_QUESTION,
     * COLUMN_ANSWERS or COLUMN_TAGS ordered by COLUMN_ID
     * 
     * @param query: a SwEngQL query
     * @return querySQLite: query parsed from SwEngQL to SQLite
     */
    private String parseQuerytoSQL(String query) {
        StringTokenizer queryTokenizer = new StringTokenizer(query, " ");
        String querySQLite = "SELECT * FROM "+ TABLE_NAME +" WHERE ";
        String nextToken = null;
        
        while (queryTokenizer.hasMoreTokens()) {
            nextToken = queryTokenizer.nextToken();
            if (nextToken.equals("(") || nextToken.equals(")")) {
                querySQLite += " " + nextToken + " ";
            } else if (nextToken.equals("*")) {
                querySQLite += " AND ";
            } else if (nextToken.equals("+")) {
                querySQLite += " OR ";
            } else {
                querySQLite += " ( "+ COLUMN_QUESTION +" LIKE "+ nextToken +" OR "+
                        COLUMN_ANSWERS +" LIKE "+ nextToken +" OR "+
                        COLUMN_TAGS +" LIKE "+ nextToken +" ) ";
            }
        }
        querySQLite += "ORDER BY "+ COLUMN_ID +" ASC ";
        return querySQLite;
    }
}
