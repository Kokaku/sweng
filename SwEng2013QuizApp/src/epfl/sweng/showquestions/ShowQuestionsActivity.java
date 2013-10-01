package epfl.sweng.showquestions;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import epfl.sweng.R;

/**
 * This activity displays questions and allows the user to answer them.
 * The user has to chose the right answer to be able to receive a new question.
 * 
 * @author lseguy
 * 
 */
public class ShowQuestionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_questions);

        
        /*
         * Initialize tags list with static data.
         */
        String[] tags = {"Despicable Me", "Unicorn", "Fluffiness", "Some tag",
            "Rainbow", "Blah"};

        // Using an adapter to fill the LinearLayout with data from the array
        ArrayAdapter<String> adapterTags = new ArrayAdapter<String>(this,
            R.layout.list_of_tags, tags);

        LinearLayout tagsLayout = (LinearLayout) findViewById(R.id.list_tags);
        
        for (int i = 0; i < adapterTags.getCount(); ++i) {
            View item = adapterTags.getView(i, null, tagsLayout);
            tagsLayout.addView(item);
        }

        
        /*
         * Initialize answers list with static data.
         */
        String[] answers = {"Banana", "Potato", "It's so fluffy!", "Gnaaaah",
            "Foo", "Bar", "Blah", "Pouet"};

        TextView question = (TextView) findViewById(R.id.text_question);
        question.setMovementMethod(new ScrollingMovementMethod());

        ArrayAdapter<String> adapterAnswers = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, answers);
        
        ListView answersList = (ListView) findViewById(R.id.list_answers);
        answersList.setAdapter(adapterAnswers);
    }

}
