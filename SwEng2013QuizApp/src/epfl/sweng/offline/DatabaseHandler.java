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

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.exceptions.DBCommunicationException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.quizquestions.QuizQuestion;
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
                
        // TODO : throw an exception if an error occurs ?
        
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
        
        try {
            return getQuestionFromCursor(cursor);
        } catch (JSONException e) {
            throw new DBCommunicationException("JSON badly formatted.");
        } finally {
            cursor.close();
            db.close();
        }
    }
    
    public void synchronizeQuestions()
        throws DBCommunicationException, ServerCommunicationException {
        
        SQLiteDatabase db = getWritableDatabase();
        
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_SUBMIT + "=1",
            null, null, null, null);
        
        if (cursor == null) {
            throw new DBCommunicationException("Error while looking for questions waiting for submission");
        }
        
        try {
            while (cursor.moveToNext()) {
                QuizQuestion question = getQuestionFromCursor(cursor);
                QuizQuestion updatedQuestion = ServerCommunication.INSTANCE.send(question);

                /*
                 * Update the question in cache : add the assigned id and owner
                 * and remove the submission flag.
                 */
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                
                ContentValues values = new ContentValues();
                values.put(COLUMN_QUESTION_ID, updatedQuestion.getId());
                values.put(COLUMN_OWNER, updatedQuestion.getOwner());
                values.put(COLUMN_SUBMIT, 0);
                db.update(TABLE_NAME, values , COLUMN_ID + "=?", new String[] {String.valueOf(id)});
            }
        } catch (JSONException e) {
            throw new DBCommunicationException("JSON badly formatted.");
        } finally {
            cursor.close();
            db.close();
        }
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
        // TODO : should probably be long instead of int
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
}
