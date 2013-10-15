package epfl.sweng.test;

import epfl.sweng.entry.MainActivity;

/**
 * @author MathieuMonney
 * 
 */
public class MainActivityTest extends QuizAppTest<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testRandomQuestionButtonDisplayed() {
        assertTrue("Random question button displayed",
                solo.searchButton("Show a random question"));
    }

    public void testSubmitQuestionButtonDisplayed() {
        assertTrue("Edit question button displayed",
                solo.searchButton("Submit a quiz question"));
    }
}
