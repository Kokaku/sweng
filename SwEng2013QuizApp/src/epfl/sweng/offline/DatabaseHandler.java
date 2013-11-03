package epfl.sweng.offline;

import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_ANSWERS;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_QUESTION_ID;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_OWNER;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_QUESTION;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_SOLUTION;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_SUBMIT;
import static epfl.sweng.offline.CachedQuestionsTable.COLUMN_TAGS;
import static epfl.sweng.offline.CachedQuestionsTable.TABLE_NAME;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.exceptions.DBCommunicationException;
import epfl.sweng.quizquestions.QuizQuestion;
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
    public void storeQuestion(QuizQuestion question, boolean toBeSubmitted) {
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
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
        if (question.getOwner() == null) {
            values.putNull(COLUMN_OWNER);
        } else {
            values.put(COLUMN_OWNER, question.getOwner());
        }
        values.put(COLUMN_SUBMIT, toBeSubmitted ? 1 : 0);
        
        // TODO : throw an exception if an error occurs
        
        db.insertWithOnConflict(TABLE_NAME, null, values,
            SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }
    
    /**
     * Gets a random question from the database.
     * 
     * @return a random question
     * @throws DBCommunicationException if the request is unsuccessful
     */
    public QuizQuestion getRandomQuestion()
        throws DBCommunicationException {
        
        SQLiteDatabase db = getReadableDatabase();
        
        // Get a random question
        Cursor cursor = db.rawQuery("SELECT * FROM " +
            TABLE_NAME + " ORDER BY RANDOM() LIMIT 1;", null);
        
        if (cursor == null || cursor.getCount() == 0) {
            throw new DBCommunicationException("Cache is empty.");
        }

        cursor.moveToFirst();
        
        int idIndex = cursor.getColumnIndex(COLUMN_QUESTION_ID);
        int questionIndex = cursor.getColumnIndex(COLUMN_QUESTION);
        int answersIndex = cursor.getColumnIndex(COLUMN_ANSWERS);
        int solutionIndex = cursor.getColumnIndex(COLUMN_SOLUTION);
        int ownerIndex = cursor.getColumnIndex(COLUMN_OWNER);
        int tagsIndex = cursor.getColumnIndex(COLUMN_TAGS);

        try {
            JSONArray answers = new JSONArray(cursor.getString(answersIndex));
            JSONArray tags = new JSONArray(cursor.getString(tagsIndex));
            return new QuizQuestion(cursor.getString(questionIndex),
                                    JSONUtilities.parseJSONArrayToList(answers),
                                    cursor.getInt(solutionIndex),
                                    JSONUtilities.parseJSONArrayToSet(tags),
                                    cursor.getInt(idIndex),
                                    cursor.getString(ownerIndex));
        } catch (JSONException e) {
            throw new DBCommunicationException("JSON badly formatted.");
        } finally {
            cursor.close();
            db.close();
        }
    }
    
}
