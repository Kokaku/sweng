package epfl.sweng.showquestions;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import epfl.sweng.R;

/**
 * 
 * @author lseguy
 * 
 */
public class ShowQuestionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_questions);

        String[] tags = { "Despicable Me", "Unicorn", "Fluffiness", "Some tag",
                "Rainbow", "Blah" };

        ArrayAdapter<String> adapterTags = new ArrayAdapter<String>(this,
                R.layout.list_of_tags, tags);

        LinearLayout tagsLayout = (LinearLayout) findViewById(R.id.list_tags);
        for (int i = 0; i < adapterTags.getCount(); i++) {
            View item = adapterTags.getView(i, null, tagsLayout);
            tagsLayout.addView(item);
        }

        String[] answers = { "Banana", "Potato", "It's so fluffy!", "Gnaaaah",
                "Foo", "Bar", "Blah", "Pouet" };

        TextView question = (TextView) findViewById(R.id.text_question);
        question.setMovementMethod(new ScrollingMovementMethod());

        ArrayAdapter<String> adapterAnswers = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, answers);
        ListView answersList = (ListView) findViewById(R.id.list_answers);
        answersList.setAdapter(adapterAnswers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_questions, menu);
        return true;
    }

}
