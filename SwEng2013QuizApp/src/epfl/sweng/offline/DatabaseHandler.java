package epfl.sweng.offline;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import epfl.sweng.SwEng2013QuizApp;
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
    private static final int DATABASE_VERSION = 1;
    
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
        db.execSQL("DELETE FROM " + CachedQuestionsTable.TABLE_NAME);
        db.close();
    }

    /**
     * Stores a question in the database. If the question is already in the
     * database, do nothing.
     * 
     * @param question the question to be stored
     */
    public void storeQuestion(QuizQuestion question) {
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(CachedQuestionsTable.COLUMN_ID, question.getId());
        values.put(CachedQuestionsTable.COLUMN_QUESTION, question.getQuestion());
        JSONArray answers = new JSONArray(question.getAnswers());
        values.put(CachedQuestionsTable.COLUMN_ANSWERS, answers.toString());
        values.put(CachedQuestionsTable.COLUMN_SOLUTION, question.getSolutionIndex());
        JSONArray tags = new JSONArray(question.getTags());
        values.put(CachedQuestionsTable.COLUMN_TAGS, tags.toString());
        values.put(CachedQuestionsTable.COLUMN_OWNER, question.getOwner());
        
        db.insertWithOnConflict(CachedQuestionsTable.TABLE_NAME, null, values,
            SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }
    
    /**
     * Gets a random question from the database.
     * 
     * @return a random question
     */
    public QuizQuestion getRandomQuestion() {
        SQLiteDatabase db = getReadableDatabase();
        
        // Get a random question
        Cursor cursor = db.rawQuery("SELECT * FROM " +
            CachedQuestionsTable.TABLE_NAME + " ORDER BY RANDOM() LIMIT 1;", null);
        
        if (cursor != null) {
            cursor.moveToFirst();
        }
        
        int idIndex = cursor.getColumnIndex(CachedQuestionsTable.COLUMN_ID);
        int questionIndex = cursor.getColumnIndex(CachedQuestionsTable.COLUMN_QUESTION);
        int answersIndex = cursor.getColumnIndex(CachedQuestionsTable.COLUMN_ANSWERS);
        int solutionIndex = cursor.getColumnIndex(CachedQuestionsTable.COLUMN_SOLUTION);
        int ownerIndex = cursor.getColumnIndex(CachedQuestionsTable.COLUMN_OWNER);
        int tagsIndex = cursor.getColumnIndex(CachedQuestionsTable.COLUMN_TAGS);

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
            
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }
    
}
