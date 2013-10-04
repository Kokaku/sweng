package epfl.sweng.editquestions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;

public class AnswersListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] answers;

	public AnswersListAdapter(Context context, String[] answers) {
		super(context, epfl.sweng.R.layout.rowlayout_view_list_answers, answers);
		this.context = context;
		this.answers = answers;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(
				epfl.sweng.R.layout.rowlayout_view_list_answers, parent, false);
		Button buttonCheckAnswer = (Button) rowView.findViewById(R.id.button_check_answer);
		Button buttonRemoveAnswer = (Button) rowView.findViewById(R.id.button_remove_answer);
		EditText editAnswer = (EditText) rowView.findViewById(R.id.edit_answer);
		
		if (answers[position].equals("")) {
	        editAnswer.setHint(R.string.type_in_answer);
		} else {
		    editAnswer.setText(answers[position]);
		}
		
		buttonCheckAnswer.setText(R.string.wrong_answer);
		buttonRemoveAnswer.setText(R.string.remove_answer);
		
		return rowView;
	}

}