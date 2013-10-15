package epfl.sweng.test;

<<<<<<< HEAD
=======
import epfl.sweng.editquestions.EditQuestionActivity;
>>>>>>> Create tests for MainActivity
import epfl.sweng.entry.MainActivity;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
<<<<<<< HEAD
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
=======
 * @author lseguy
 *
 */
public class MainActivityTest extends QuizAppTest<MainActivity> {
    
    public String SHOW_QUESTION_TEXT = "Show a random question";
    public String SUBMIT_QUESTION_TEXT = "Submit a quiz question";
    
    public MainActivityTest() {
        super(MainActivity.class);
    }
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
    }
    
    public void testRightActivityIsDisplayed() {
        solo.assertCurrentActivity("Display activity is not displayed",
            MainActivity.class);
>>>>>>> Create tests for MainActivity
    }
    
    public void testRandomQuestionButtonIsDisplayed() {
        assertTrue("Show a random question button must be displayed", 
            solo.searchButton(SHOW_QUESTION_TEXT));
    }
    
    public void testSubmitQuestionButtonIsDisplayed() {
        assertTrue("Submit a quiz question button must be displayed",
            solo.searchButton(SUBMIT_QUESTION_TEXT));
    }
    
    public void testRandomQuestionButtonStartsActivity() {
        solo.clickOnButton(SHOW_QUESTION_TEXT);
        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
        solo.assertCurrentActivity("Show question button doesn't start activity",
                ShowQuestionsActivity.class);
    }
    
    public void testSubmitQuestionButtonStartsActivity() {
        solo.clickOnButton(SUBMIT_QUESTION_TEXT);
        getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
        solo.assertCurrentActivity("Edit question button doesn't start activity",
                EditQuestionActivity.class);
    }

}
