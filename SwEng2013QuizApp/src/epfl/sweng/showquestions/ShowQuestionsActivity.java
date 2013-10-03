package epfl.sweng.showquestions;

import java.util.Arrays;
import java.util.TreeSet;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import epfl.sweng.QuizQuestion;
import epfl.sweng.R;
import epfl.sweng.ServerCommunication;

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

		String[] answers = null;
		
		QuizQuestion quizQuestion = ServerCommunication.getRandomQuestion();
		QuizQuestion sendQuestion = new QuizQuestion(
				"What is the blood alcohol concentration to be in an ultimate programming skills level also called \"Ballmer Peak\" ?",
				new String[] {"0.0% - 0.05%", "0.05% - 0.1%", "0.13% - 0.14%", "50% - 80%", "99.9% - 100%"},
				2, new TreeSet<String>(Arrays.asList(new String[]{"Microsoft", "SteveBallmer", "Alcohol", "WindowsMe"})));
		
		System.out.println("BANANANANANANANANANANNAANNANANA");
		System.out.println("Was my question posted ? : "+ServerCommunication.send(sendQuestion));

		System.out.println("PATATOS");
		
		TextView question = (TextView) findViewById(R.id.text_question);
		question.setMovementMethod(new ScrollingMovementMethod());

		if (quizQuestion == null) {	
			answers = new String[]{"Banana", "Potato", "It's so fluffy!", "Gnaaaah", "Foo", "Bar", "Blah", "Pouet"};
		}
		else {
			answers = quizQuestion.getAnswers();
			question.setText(quizQuestion.getQuestion());
		}
		
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
