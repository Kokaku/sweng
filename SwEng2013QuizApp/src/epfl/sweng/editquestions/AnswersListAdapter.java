package epfl.sweng.editquestions;

import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
/**
 * 
 * @author Zhivka Gucevska
 *
 */
public class AnswersListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList<String> answers;

	public AnswersListAdapter(Context contextArg, ArrayList<String> answersArg) {
		super(contextArg, epfl.sweng.R.layout.rowlayout_view_list_answers, answersArg);
		this.context = contextArg;
		this.answers = answersArg;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(
				epfl.sweng.R.layout.rowlayout_view_list_answers, parent, false);
		Button buttonCheckAnswer = (Button) rowView
				.findViewById(R.id.button_check_answer);
		Button buttonRemoveAnswer = (Button) rowView
				.findViewById(R.id.button_remove_answer);
		EditText editAnswer = (EditText) rowView.findViewById(R.id.edit_answer);

		if (answers.get(position).equals("")) {
			editAnswer.setHint(R.string.type_in_answer);
		} else {
			editAnswer.setText(answers.get(position));
		}

		buttonCheckAnswer.setText(R.string.wrong_answer);
		buttonRemoveAnswer.setText(R.string.remove_answer);
		editAnswer.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable newText) {
				answers.set(position, newText.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

		});

		return rowView;
	}

}