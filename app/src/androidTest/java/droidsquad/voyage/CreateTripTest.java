package droidsquad.voyage;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

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

    String TRIP_NAME;
    String LEAVING_FROM;

    @Before
    public void setUp() throws Exception {

        TRIP_NAME = "TEST";
        LEAVING_FROM = "San Diego, CA, USA";

    }

    @Test
    public void testTripNameChangeText() {

        // Type trip name
        onView(withId(R.id.trip_name))
                .perform(typeText(TRIP_NAME), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.trip_name)).check(matches(withText(TRIP_NAME)));

    }

    @Test
    public void testPrivateCheckboxChangeText() {

        // Check original text.
        onView(withId(R.id.trip_private_help)).check(matches(withText(R.string.help_trip_private)));

        // Un-check private checkbox
        onView(withId(R.id.private_check))
                .perform(click());

        // Check that the text was changed.
        onView(withId(R.id.trip_private_help)).check(matches(withText(R.string.help_trip_public)));

    }

    @Test
    public void testLeavingFromChangeText() {

        // Un-check private checkbox
        onView(withId(R.id.leaving_from))
                .perform(click());

        /** TODO: Continue here */

        // Check that the text was changed.
        //onView(withId(R.id.trip_private_help)).check(matches(withText(R.string.help_trip_public)));

    }

}

