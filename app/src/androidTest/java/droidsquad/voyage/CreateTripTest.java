package droidsquad.voyage;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.parse.Parse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import droidsquad.voyage.view.activity.CreateTripActivity;
import droidsquad.voyage.model.parseModels.ParseTripModel;
import droidsquad.voyage.model.objects.Trip;

/**
 * Created by gumbe on 1/23/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateTripTest {

    @Rule
    public ActivityTestRule<CreateTripActivity> mCreateTripActivityRule = new ActivityTestRule(CreateTripActivity.class);

    String TRIP_NAME_TOO_SHORT;
    String TRIP_NAME;
    String LEAVING_FROM;
    String LOCATION_ERROR;
    String TRIP_NAME_ERROR;
    String PLANE;
    String CAR;
    String BUS;

    @Before
    public void setUp() throws Exception {

        TRIP_NAME_TOO_SHORT = "AA";
        TRIP_NAME = "123";
        LEAVING_FROM = "San Diego, CA, USA";
        LOCATION_ERROR = "Please enter a valid location";
        TRIP_NAME_ERROR = "Name must be at least 3 character long";
        PLANE = "Plane";
        BUS = "Bus";
        CAR = "Car";
    }

    @Test
    public void testTripNameChangeText() {

        // check that trip name must be set
        onView(withId(R.id.create_trip_button))
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

        // Un-check private checkbox
        //onView(withId(R.id.leaving_from))
        //        .perform(click());

        /** TODO: Continue here */

        // Check that the text was changed.
        //onView(withId(R.id.trip_private_help)).check(matches(withText(R.string.help_trip_public)));

        //onView(withId(R.id.leaving_from)).perform(click());

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
}

