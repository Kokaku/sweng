package epfl.sweng.searchquestions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import android.os.Parcel;
import android.os.Parcelable;
import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.patterns.Proxy;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Is an iterator over a collection of QuizQuestion. In addition
 * QuestionIterator is able to fetch the remaining questions that match the
 * query
 * 
 * @author kokaku
 * 
 */
public class QuestionIterator implements Parcelable {

	private QuizQuestion[] mQuestions;
	private int mNextIndex = 0;
	private String mNext;
	private String mQuery;

	
	/**
     * @param questions is the array of QuizQuestion over which you want to iterate
     * @param query is a String representing the query that the questions must match
     * @param next is the String token received by the server in case the server
     * only return a fraction of all questions that match a query
     */
    public QuestionIterator(QuizQuestion[] questions, String query, String next) {
        if (questions == null) {
            throw new IllegalArgumentException("Argument questions cannot be null");
        }
        if (next != null && query == null) {
            throw new IllegalArgumentException(
                    "Argument query cannot be null if next isn't null");
        }
            
        this.mQuestions = questions.clone();
        this.mNext = next;
        this.mQuery = query;
    }

	

	/**
	 * @param questions
	 *            is the array of QuizQuestion over which you want to iterate
	 */
	public QuestionIterator(QuizQuestion[] questions) {
		this(questions, null, null);
	}

	/**
	 * Returns true if the iteration has more elements. (In other words, returns
	 * true if next() would return an element rather than throwing an
	 * exception.)
	 * 
	 * @return true if the iteration has more elements
	 */
	public boolean hasNext() {
		return mQuestions.length > mNextIndex || mNext != null;
	}
	

	/**
	 * Returns the next element in the iteration. Should only be called in an
	 * AsynTask
	 * 
	 * @return the next element in the iteration
	 * @throws NotLoggedInException
	 *             if the user is not logged in
	 * @throws DBException
	 *             if the database request is unsuccessful
	 * @throws ServerCommunicationException
	 *             if the network request is unsuccessful
	 * @throws NoSuchElementException
	 *             if the iteration has no more elements
	 * @throws JSONException
	 */
	public QuizQuestion next() throws NotLoggedInException, DBException,
			ServerCommunicationException, NoSuchElementException {

		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		if (mQuestions.length <= mNextIndex) {
			fetchNextQuestions();
		}

		QuizQuestion nextQuestion = mQuestions[mNextIndex];
		mNextIndex++;
		return nextQuestion;
	}

	/**
	 * @return the questions already received as an array of QuizQuestion
	 */
	public QuizQuestion[] getLocalQuestions() {
		return mQuestions.clone();
	}

	/**
	 * Replace the local cache with the next questions on which the query match
	 * Should only be called in an AsynTask
	 * 
	 * @throws NotLoggedInException
	 *             if the user is not logged in
	 * @throws DBException
	 *             if the database request is unsuccessful
	 * @throws ServerCommunicationException
	 *             if the network request is unsuccessful
	 * @throws JSONException
	 *             if there was a problem parsing the json
	 */
	private void fetchNextQuestions() throws NotLoggedInException, DBException,
			ServerCommunicationException {

		QuestionIterator responseIterator = Proxy.INSTANCE.searchQuestion(
				mQuery, mNext);
		mQuestions = responseIterator.getLocalQuestions();
        mNext = responseIterator.mNext;
		mNextIndex = 0;
	}


   
	public QuestionIterator(Parcel in) {
		readFromParcel(in);
	}

	/**
	 * Static field used to regenerate the object, individually or as an array
	 */
	public static final Parcelable.Creator<QuestionIterator> CREATOR = new Parcelable.Creator<QuestionIterator>() {
		public QuestionIterator createFromParcel(Parcel in) {
			return new QuestionIterator(in);
		}

		public QuestionIterator[] newArray(int size) {
			return new QuestionIterator[size];
		}
	};

	
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeInt(mNextIndex);
		dest.writeString(mNext);
		dest.writeString(mQuery);
		dest.writeInt(mQuestions.length);
		dest.writeTypedList(Arrays.asList(mQuestions));
	}

	public void readFromParcel(Parcel in) {
		mNextIndex = in.readInt();
		mNext = in.readString();
		mQuery = in.readString();
		int size = in.readInt();
		mQuestions = new QuizQuestion[size];
		ArrayList<QuizQuestion> questions = new ArrayList<QuizQuestion>();
		in.readTypedList(questions, QuizQuestion.CREATOR);
		questions.toArray(mQuestions);
		System.out.println("QuestionIterator: ");
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

}
