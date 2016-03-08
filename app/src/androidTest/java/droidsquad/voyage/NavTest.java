package droidsquad.voyage;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import droidsquad.voyage.view.activity.MainNavDrawerActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * PRECONDITION: Please log on to the app before performing these tests.
 * Created by Zemei on 3/8/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavTest {
    @Rule
    public ActivityTestRule<MainNavDrawerActivity> mActivityRule = new ActivityTestRule<>(MainNavDrawerActivity.class);

    @Before
    public void setUp() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        sleep(5000);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }

    @Test
    public void testFeed() {
        onView(withText("Feed")).perform(click());
        sleep(5000);
        onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText("Feed")));
    }

    @Test
    public void testTrips() {
        onView(withText("Trips")).perform(click());
        sleep(5000);
        onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText("My Trips")));
    }

    @Test
    public void testRequests() {
        onView(withText("Requests")).perform(click());
        sleep(5000);
        onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText("Requests")));
    }

/*    @Test
    public void testLogout() {
        onView(withText("Logout")).perform(click());
        sleep(5000);
        onView(withId(R.id.facebook_login_button)).check(matches(isDisplayed()));
    }*/

    private void sleep(int time){
        try {
            Thread.sleep(time);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
