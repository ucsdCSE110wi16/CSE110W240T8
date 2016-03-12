package droidsquad.voyage.ScenarioTests;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import droidsquad.voyage.R;
import droidsquad.voyage.view.activity.MainNavDrawerActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Zemei on 3/7/2016.
 */

// Preconditions: Before running tests: please log in to the app, and make sure there is a good wifi
// connection! Also wifi should be on before running the test, and the activity should begin on the
// default page (not the phone's wifi settings page, which Espresso is not compatible to deal with)

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WifiNetworkTest {
    @Rule
    public ActivityTestRule<MainNavDrawerActivity> mActivityRule = new ActivityTestRule<>(MainNavDrawerActivity.class);

    /**
     * Check that if the network is on, dialog does not appear
     */
    @Test
    public void networkOn() {
        WifiManager wifiManager = (WifiManager)mActivityRule.getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        sleep(10000);
        onView(withId(R.id.swiperefresh))
                .perform(swipeDown());
        sleep(10000);

        onView(withText("No Network Connection"))
                .check(doesNotExist());
    }

    /**
     * Basic checks for dialog that occurs when network wifi is turned off
     */
    @Test
    public void networkOff() {
        WifiManager wifiManager = (WifiManager)mActivityRule.getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        onView(withId(R.id.swiperefresh))
                .perform(swipeDown());
        sleep(10000);

        onView(withText("No Network Connection"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText("Dismiss"))
                .inRoot(isDialog())
                .perform(click());

        wifiManager.setWifiEnabled(true); // reset wifi to wrap up test
        sleep(10000);
    }

    /**
     * Pause the thread for a while
     * @param time
     */
    private void sleep(int time){
        try {
            Thread.sleep(time);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }


    /**
     * Action to simulate user swiping down to refresh the screen
     * @return ViewAction
     */
    public static ViewAction swipeDown() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER,
                GeneralLocation.BOTTOM_CENTER, Press.FINGER);
    }
}
