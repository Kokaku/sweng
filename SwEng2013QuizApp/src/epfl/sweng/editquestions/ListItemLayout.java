/**
 * 
 */
package epfl.sweng.editquestions;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 *
 * @author MathieuMonney
 *
 */
public class ListItemLayout extends TableLayout{

    /**
     * @param context
     */
    
    private Button rightAnswer;
    private EditText answer;
    private Button deleteAnswer;
    
    public ListItemLayout(Context context) {
        super(context);
        initUI();
    }
    
    private void initUI() {
        // Set the size of the view (relativeWidth, relativeHeight, width, height)
        setPadding(10, 10, 10, 10);
        
        //creates the first row
        
        {
            rightAnswer = new Button(getContext());
            // set width, height and apply params
            rightAnswer.setPadding(10, 10, 10, 10);
        }
        
        // creates second row
        
        {
            answer = new EditText(getContext());
         // set width, height and apply params
            answer.setPadding(10, 10, 10, 10);
        }
        
        // creates third row
        
        {
            deleteAnswer = new Button(getContext());
            // set width, height and apply params
            deleteAnswer.setPadding(10, 10, 10, 10);
        }
        
        // add rows to the table
        
        {
            addView(rightAnswer);
            addView(answer);
            addView(deleteAnswer);
        }
    }

}
