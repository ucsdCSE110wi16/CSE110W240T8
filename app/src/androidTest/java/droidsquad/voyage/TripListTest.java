package droidsquad.voyage;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.android.gms.location.places.Place;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import droidsquad.voyage.controller.activityController.CreateTripController;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.view.activity.CreateTripActivity;
import droidsquad.voyage.view.activity.TripActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Created by Zemei on 3/5/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TripListTest {
    @Rule
    public ActivityTestRule<CreateTripActivity> mCreateTripActivityRule = new ActivityTestRule<>(CreateTripActivity.class);

    Trip trip;

    @Before
    public void setUp() {
        onView(withId(R.id.trip_name))
                .perform(typeText("TEST!"), closeSoftKeyboard());
        onView(withId(R.id.trip_name))
                .check(matches(withText("TEST!")));
        onView(withId(R.id.private_check)).check(matches(isChecked()));

        onView(withId(R.id.leaving_from))
                .perform(replaceText("San Jose Place, Carrington Street, Sydney, New South Wales, Australia"));
        onView(withId(R.id.leaving_from))
                .check(matches(withText("San Jose Place, Carrington Street, Sydney, New South Wales, Australia")));

        onView(withId(R.id.destination))
                .perform(replaceText("Alice's Wonderland Travel, Lavender Street, Milsons Point, New South Wales, Australia"));
        onView(withId(R.id.destination))
                .check(matches(withText("Alice's Wonderland Travel, Lavender Street, Milsons Point, New South Wales, Australia")));

        onView(withId(R.id.transportation))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Bus")))
                .perform(click());
        onView(withId(R.id.transportation))
                .check(matches(withSpinnerText("Bus")));
        onView(withId(R.id.create_trip_button))
                .perform(click());


        //TODO: Update Trip origin/dest somehow: i.e. methods to update origin/dest of Trip object so it can save & show on TripList


        /* For Reference: manually creating Trip object
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 3, 5);
        Date dateFrom = calendar.getTime();
        calendar.set(2015, 3, 20);
        Date dateTo = calendar.getTime();
        boolean isPrivate = false;

        JSONObject origin = new JSONObject();
        try {
            origin.put("address", "11200 Cruscades, France");
            origin.put("city", "Cruscades");
            origin.put("placeId", "ChIJkX_c0jS0sRIRAC1tFiGIBwQ");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        JSONObject destination = new JSONObject();
        try {
            destination.put("address", "Gum Spring, VA 23065, USA");
            destination.put("city", "Gum Spring");
            destination.put("placeId", "ChIJyZ5nKs1YsYkR1LBX1JvaNWU");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        trip = new Trip("Trip Name", "Plane", origin, destination, isPrivate, dateFrom, dateTo);
        trip.setAdmin(new User("yDSasmAjGv", "1245749292118566", "Zemei", "Zeng"));*/
    }

    @Test
    public void testInitTripList() {

    }
}