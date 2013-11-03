/**
 * 
 */
package epfl.sweng.offline;

import android.database.sqlite.SQLiteDatabase;

/**
 * Helper class to manage the SQL table representing the cached questions.
 * 
 * @author lseguy
 *
 */
public class CachedQuestionsTable {
    
    public static final String TABLE_NAME = "cached_questions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUESTION_ID = "question_id";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_ANSWERS = "answers";
    public static final String COLUMN_SOLUTION = "solutionIndex";
    public static final String COLUMN_TAGS = "tags";
    public static final String COLUMN_OWNER = "owner";
    // Boolean stored as an integer, if 1 the question should be submitted when back online
    public static final String COLUMN_SUBMIT = "submit";
    
    private static final String DATABASE_CREATE = "CREATE TABLE " +
        TABLE_NAME + "(" + 
        COLUMN_ID + " INTEGER PRIMARY KEY, " +
        COLUMN_QUESTION + " TEXT NOT NULL, " +
        COLUMN_ANSWERS + " TEXT NOT NULL, " +
        COLUMN_SOLUTION + " TEXT NOT NULL, " +
        COLUMN_TAGS + " TEXT NOT NULL, " + 
        COLUMN_OWNER + " TEXT, " +
        COLUMN_QUESTION_ID + " INTEGER UNIQUE, " +
        COLUMN_SUBMIT + " INTEGER NOT NULL" +
        ");";
        
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }
    
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    
}
