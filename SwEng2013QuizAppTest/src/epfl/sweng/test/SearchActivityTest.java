/**
 * 
 */
package epfl.sweng.test;

import org.apache.http.HttpStatus;

import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.searchquestions.SearchActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.framework.QuizActivityTestCase;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * @author Zhivka Gucevska
 * 
 */
public class SearchActivityTest extends QuizActivityTestCase<SearchActivity> {
	public static final String SEARCH_TEXT = "Search";
	public static final String QUERY_HINT = "Type in the search query";
	private MockHttpClient mockHttpClient;

	public SearchActivityTest() {
		super(SearchActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockHttpClient = new MockHttpClient();
		mockHttpClient.pushCannedResponse("", 0, "", "");
		SwengHttpClientFactory.setInstance(mockHttpClient);
		UserCredentials.INSTANCE.setState(AuthenticationState.AUTHENTICATED);
		UserCredentials.INSTANCE.saveUserCredentials("test");
		getActivityAndWaitFor(TTChecks.SEARCH_ACTIVITY_SHOWN);

	}

	public void testSearchButtonIsDisplayed() {
		solo.sleep(500);
		assertTrue("Search button is displayed", solo.searchButton("Search"));
	}

	public void testSearchButtonIsDisabled() {
		solo.sleep(500);
		assertFalse("Search button is initially disabled",
				solo.getButton("Search").isEnabled());

	}

	public void testQueryEditTextIsDisplayed() {
		solo.sleep(500);
		assertTrue("Query edit text is displayed",
				solo.searchEditText(QUERY_HINT));
	}

	public void testSearchButtonIsDisabledWhenInvalidQuery1() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "(banana (tomato( ))");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());

	}

	public void testSearchButtonIsDisabledWhenInvalidQuery2() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "(banana **(tomato))");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonIsDisabledWhenInvalidQuery3() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "(banana *)(tomato)");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonIsDisabledWhenInvalidQuery4() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "(*banana (tomato))");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonIsDisabledWhenInvalidQuery5() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "(+banana *(tomato))");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonIsDisabledWhenInvalidQuery6() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "()");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonIsDisabledWhenInvalidQuery7() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "(banana))(");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonIsDisabledWhenInvalidQuery8() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "(banana)*");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonIsDisabledWhenInvalidQuery9() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "76247&&");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonIsDisabledWhenInvalidQuery10() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "(banana))(");
		assertFalse("Search is disabled when invalid nesting",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonIsEnabledWhenValidQuery() {
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "banana");
		assertTrue("Search is enabled when valid query",
				solo.getButton(SEARCH_TEXT).isEnabled());
	}

	public void testSearchButtonStartsShowQuestionActivity() {

		mockHttpClient
				.pushCannedResponse(
						"POST [^/]+",
						HttpStatus.SC_OK,
						"{\"questions\": ["
								+ " {"
								+ "\"id\": \"7654765\","
								+ " \"owner\": \"fruitninja\","
								+ " \"question\": \"How many calories are in a banana?\","
								+ " \"answers\": [ \"Just enough\", \"Too many\" ],"
								+ " \"solutionIndex\": 0,"
								+ " \"tags\": [ \"fruit\", \"banana\", \"trivia\" ]"
								+ "},{\"question\": \"What is the answer to life, the universe, and everything?\","
								+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }  ],"
								+ "\"next\": \"YG9HB8)H9*-BYb88fdsfsyb(08bfsdybfdsoi4\""
								+ "}", "application/json");
		solo.sleep(500);
		solo.enterText(solo.getEditText(QUERY_HINT), "banana");
		clickOnTextViewAndWaitFor(SEARCH_TEXT, TTChecks.QUESTION_SHOWN);
		solo.assertCurrentActivity("ShowQuestionActivity is launched",
				ShowQuestionsActivity.class);
	}

	public void testShowQuestionDisplaysAllQuestions() {
		mockHttpClient
				.pushCannedResponse(
						"POST [^/]+",
						HttpStatus.SC_OK,
						"{\"questions\": ["
								+ " {"
								+ "\"id\": \"7654765\","
								+ " \"owner\": \"fruitninja\","
								+ " \"question\": \"How many calories are in a banana?\","
								+ " \"answers\": [ \"Just enough\", \"Too many\" ],"
								+ " \"solutionIndex\": 0,"
								+ " \"tags\": [ \"fruit\", \"banana\", \"trivia\" ]"
								+ "},{\"question\": \"What is the answer to life, the universe, and everything?\","
								+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }  ],"
								+ "\"next\": \"YG9HB8)H9*-BYb88fdsfsyb(08bfsdybfdsoi4\""
								+ "}", "application/json");

		solo.sleep(100);
		solo.enterText(solo.getEditText(QUERY_HINT), "banana");
		clickOnTextViewAndWaitFor(SEARCH_TEXT, TTChecks.QUESTION_SHOWN);
		solo.sleep(100);
		assertTrue("Question is displayed",
				solo.searchText("banana"));
		clickOnTextViewAndWaitFor( "Just enough", TTChecks.ANSWER_SELECTED);
		clickOnTextViewAndWaitFor("Next question", TTChecks.QUESTION_SHOWN);
		solo.sleep(100);
		assertTrue("Question is displayed",
				 solo.searchText("universe"));
		clickOnTextViewAndWaitFor( "Forty-two", TTChecks.ANSWER_SELECTED);
		clickOnTextViewAndWaitFor("Next question", TTChecks.QUESTION_SHOWN);
		
	}
}
