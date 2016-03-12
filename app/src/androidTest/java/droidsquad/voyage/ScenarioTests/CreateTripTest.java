package droidsquad.voyage.ScenarioTests;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.DatePicker;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.CreateTripActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Created by gumbe on 1/23/2016.
 */

// Precondition: Please log in to the app before running these scenario tests.

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateTripTest {

    @Rule
    public ActivityTestRule<CreateTripActivity> mCreateTripActivityRule = new ActivityTestRule(CreateTripActivity.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);

    String TRIP_NAME_TOO_SHORT;
    String TRIP_NAME;
    String LEAVING_FROM;
    String DESTINATION;
    String LOCATION_ERROR;
    String TRIP_NAME_ERROR;
    String PLANE;
    String CAR;
    String BUS;
    String OK_BUTTON;

    Calendar CALENDAR_FROM;
    Calendar CALENDAR_TO;

    @Before
    public void setUp() throws Exception {

        TRIP_NAME_TOO_SHORT = "AA";
        TRIP_NAME = "123";
        LEAVING_FROM = "San Diego, CA, USA";
        DESTINATION = "Cupertino, CA, USA";
        LOCATION_ERROR = "Please enter a valid location";
        TRIP_NAME_ERROR = "Name must be at least 3 character long";
        PLANE = "Plane";
        BUS = "Bus";
        CAR = "Car";
        OK_BUTTON = "OK";

        resetCalendars();
        sleep(500);
    }

    @Test
    public void testTripNameChangeText() {

        // check that trip name must be set
        onView(ViewMatchers.withId(R.id.create_trip_button))
                .perform(click());
        onView(withId(R.id.trip_name))
                .check(matches(hasErrorText(TRIP_NAME_ERROR)));

        // check entering a trip name that is too short
        onView(withId(R.id.trip_name))
                .perform(typeText(TRIP_NAME_TOO_SHORT), closeSoftKeyboard());
        onView(withId(R.id.trip_name))
                .check(matches(withText(TRIP_NAME_TOO_SHORT)));
        onView(withId(R.id.create_trip_button))
                .perform(click());
        onView(withId(R.id.trip_name))
                .check(matches(hasErrorText(TRIP_NAME_ERROR)));

        // Type trip name
        onView(withId(R.id.trip_name))
                .perform(clearText(), typeText(TRIP_NAME), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.trip_name)).
                check(matches(withText(TRIP_NAME)));
    }

    @Test
    public void testPrivateCheckboxChangeText() {

        // Check if checkbox is set by default
        onView(withId(R.id.private_check)).check(matches(isChecked()));

        // Check original text.
        onView(withId(R.id.trip_private_help)).check(matches(withText(R.string.help_trip_private)));

        // Un-check private checkbox
        onView(withId(R.id.private_check))
                .perform(click());

        // Check if checkbox is unset properly
        onView(withId(R.id.private_check)).check(matches(isNotChecked()));

        // Check that the text was changed.
        onView(withId(R.id.trip_private_help)).check(matches(withText(R.string.help_trip_public)));

    }

    @Test
    public void testErrorMessages(){

        // Click button before filling in fields
        onView(withId(R.id.create_trip_button))
                .perform(click());

        // Check if error was set correctly on trip name
        onView(withId(R.id.trip_name))
                .check(matches(hasErrorText(TRIP_NAME_ERROR)));

        // Check if error was set correctly on leaving from
        onView(withId(R.id.leaving_from))
                .check(matches(hasErrorText(LOCATION_ERROR)));

        // Check if error was set correctly on destination
        onView(withId(R.id.destination))
                .check(matches(hasErrorText(LOCATION_ERROR)));

    }

    @Test
    public void testLeavingFromChangeText() {

        onView(withId(R.id.leaving_from))
                .perform(replaceText(LEAVING_FROM));

        onView(withId(R.id.leaving_from))
                .check(matches(withText(LEAVING_FROM)));

        onView(withId(R.id.destination))
                .perform(replaceText(DESTINATION));

        onView(withId(R.id.destination))
                .check(matches(withText(DESTINATION)));

        // other possible ideas:

        //createTripActivityRule.getActivity().onActivityResult
        /*try {
            Thread.sleep(5000); // give the app extra time to update
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }*/

        //onView(withId(R.id.place_autocomplete_search_input))
        //        .inRoot(withDecorView(not(is(createTripActivityRule.getActivity().getWindow().getDecorView()))))
        //        .perform(typeText(tripOrigin)); <- doesn't work for some reason

        //onView(withId(android.R.layout.simple_list_item_1)).perform(click()); <- select a trip from menu
        //onView(withId(R.id.create_trip_button)).perform(click());
        //onView(withId(R.id.destination)).check(matches(hasErrorText(tripLocationError)));

    }

    @Test
    public void testTransportation() {

        // Select Plane transportation option and check that view updates
        onView(withId(R.id.transportation))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), is(PLANE)))
                .perform(click());
        onView(withId(R.id.transportation))
                .check(matches(withSpinnerText(PLANE)));

        // Select Car transportation option and check that view updates
        onView(withId(R.id.transportation))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), is(CAR)))
                .perform(click());
        onView(withId(R.id.transportation))
                .check(matches(withSpinnerText(CAR)));

        // Select Bus transportation option and check that view updates
        onView(withId(R.id.transportation))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), is(BUS)))
                .perform(click());
        onView(withId(R.id.transportation))
                .check(matches(withSpinnerText(BUS)));

    }

    @Test
    public void defaultTestDatePickers () {

        // Set Calendar to default
        CALENDAR_TO.add(Calendar.DAY_OF_WEEK, Constants.DEFAULT_TRIP_LENGTH);

        // Check if datePickers were initialized properly
        onView(withId(R.id.date_from))
                .check(matches(withText(dateFormat.format(CALENDAR_FROM.getTime()))));
        onView(withId(R.id.date_to))
                .check(matches(withText(dateFormat.format(CALENDAR_TO.getTime()))));

    }

    @Test
    public void basicTestDatePickers (){

        // Set Calendars to initial test date
        CALENDAR_FROM.add(Calendar.DAY_OF_WEEK, Constants.DEFAULT_TRIP_LENGTH);
        CALENDAR_TO.add(Calendar.MONTH, 1);

        // Set FROM Date using the DatePicker
        setDateFrom();

        // Check if date to was updated automatically
        CALENDAR_FROM.add(Calendar.DAY_OF_WEEK, Constants.DEFAULT_TRIP_LENGTH);
        onView(withId(R.id.date_to))
                .check(matches(withText(dateFormat.format(CALENDAR_FROM.getTime()))));

        // Set Calendars to initial test date
        resetCalendars();
        CALENDAR_FROM.add(Calendar.DAY_OF_WEEK, Constants.DEFAULT_TRIP_LENGTH);
        CALENDAR_TO.add(Calendar.MONTH, 1);

        // SET TO Date using the DatePicker
        setDateTo();

        // Check if dates are displayed properly
        onView(withId(R.id.date_from))
                .check(matches(withText(dateFormat.format(CALENDAR_FROM.getTime()))));
        onView(withId(R.id.date_to))
                .check(matches(withText(dateFormat.format(CALENDAR_TO.getTime()))));

    }

    @Test
    public void edgeTestDatePickers (){

        // Set date FROM and TO to the same date
        setDateFrom();
        setDateTo();

        // Check if displaying dates correctly
        onView(withId(R.id.date_from))
                .check(matches(withText(dateFormat.format(CALENDAR_FROM.getTime()))));
        onView(withId(R.id.date_to))
                .check(matches(withText(dateFormat.format(CALENDAR_TO.getTime()))));

        // Update FROM date
        CALENDAR_FROM.add(Calendar.DAY_OF_WEEK, 1);
        setDateFrom();

        // Both dates should still be the same
        onView(withId(R.id.date_from))
                .check(matches(withText(dateFormat.format(CALENDAR_FROM.getTime()))));
        onView(withId(R.id.date_to))
                .check(matches(withText(dateFormat.format(CALENDAR_FROM.getTime()))));

        // Reset dates
        resetCalendars();
        setDateFrom();
        CALENDAR_TO.add(Calendar.DAY_OF_WEEK, Constants.DEFAULT_TRIP_LENGTH);
        setDateTo();

        // Check if dates changed properly
        onView(withId(R.id.date_from))
                .check(matches(withText(dateFormat.format(CALENDAR_FROM.getTime()))));
        onView(withId(R.id.date_to))
                .check(matches(withText(dateFormat.format(CALENDAR_TO.getTime()))));

    }

    @Test
    public void fullRunThrough() {
        onView(withId(R.id.trip_name))
                .perform(typeText("TEST!"), closeSoftKeyboard());
        onView(withId(R.id.trip_name))
                .check(matches(withText("TEST!")));
        onView(withId(R.id.private_check)).check(matches(isChecked()));

        onView(withId(R.id.leaving_from))
                .perform(replaceText("San Jose, CA, USA"));
        onView(withId(R.id.leaving_from))
                .check(matches(withText("San Jose, CA, USA")));

        onView(withId(R.id.destination))
                .perform(replaceText("San Francisco, CA, USA"));
        onView(withId(R.id.destination))
                .check(matches(withText("San Francisco, CA, USA")));

        onView(withId(R.id.transportation))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), is(PLANE)))
                .perform(click());
        onView(withId(R.id.transportation))
                .check(matches(withSpinnerText(PLANE)));
        onView(withId(R.id.create_trip_button))
                .perform(click());
    }

    /* Give the app extra time to update */
    private void sleep(int time){
        try {
            Thread.sleep(time);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    /* Set Calendars to current date */
    private void resetCalendars(){
        CALENDAR_FROM = Calendar.getInstance();
        CALENDAR_TO = Calendar.getInstance();
    }

    /* Uses CALENDAR_FROM and DatePicker to set FROM Date */
    private void setDateFrom(){
        onView(withId(R.id.date_from))
                .perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions
                        .setDate(CALENDAR_FROM.get(Calendar.YEAR),
                                CALENDAR_FROM.get(Calendar.MONTH) + 1,
                                CALENDAR_FROM.get(Calendar.DAY_OF_MONTH)));
        onView(withText(OK_BUTTON))
                .perform(click());
    }

    /* Uses CALENDAR_TO DatePicker to set TO Date */
    private void setDateTo(){
        onView(withId(R.id.date_to))
                .perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions
                        .setDate(CALENDAR_TO.get(Calendar.YEAR),
                                CALENDAR_TO.get(Calendar.MONTH) + 1,
                                CALENDAR_TO.get(Calendar.DAY_OF_MONTH)));
        onView(withText(OK_BUTTON))
                .perform(click());
    }
}

