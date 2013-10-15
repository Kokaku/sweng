package epfl.sweng.test;

import android.widget.Button;
import epfl.sweng.showquestions.ShowQuestionsActivity;

/**
 * @author lseguy
 * 
 */

public class ShowQuestionsActivityTest extends
        QuizAppTest<ShowQuestionsActivity> {

    public ShowQuestionsActivityTest() {
        super(ShowQuestionsActivity.class);
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
