package epfl.sweng.test;

import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.showquestions.ShowQuestionsActivity;

/**
 * @author lseguy
 * 
 */

public class ShowQuestionsActivityTest extends QuizAppTest<ShowQuestionsActivity> {
    private Solo solo;

    public ShowQuestionsActivityTest() {
        super(ShowQuestionsActivity.class);
    }

    @Override
    protected void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testShowQuestion() {
        assertTrue(
                "Question is displayed",
                solo.searchText("What is the answer to Life, the universe and everything?"));
        assertTrue("Correct answer is displayed", solo.searchText("Forty-two"));
        assertTrue("Incorrect answer is displayed",
                solo.searchText("Twenty-seven"));

        Button nextQuestionButton = solo.getButton("Next question");
        assertFalse("Next question button is disabled",
                nextQuestionButton.isEnabled());
    }
}
