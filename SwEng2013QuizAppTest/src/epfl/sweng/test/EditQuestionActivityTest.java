package epfl.sweng.test;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.editquestions.EditQuestionActivity;

/**
 * @author MathieuMonney
 * 
 */
public class EditQuestionActivityTest extends QuizAppTest<EditQuestionActivity> {

    public EditQuestionActivityTest() {
        super(EditQuestionActivity.class);
    }
    
    @Override
    protected void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

}
