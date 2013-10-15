package epfl.sweng.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.entry.MainActivity;

/**
 * @author MathieuMonney
 *
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{
    
    private Solo solo;

    public MainActivityTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }
    
    public void testRandomQuestionButtonDisplayed () {
//        assertTrue("Random question button displayed", solo.searchButton("Show a random question"));
        
        Button showRandomQuestion = solo.getButton("Show a random question");
        assertTrue("Show random question is enabled", showRandomQuestion.isEnabled());
        
//        assertTrue("Edit question button displayed", solo.searchButton("Submit a quiz question"));
    }
}
