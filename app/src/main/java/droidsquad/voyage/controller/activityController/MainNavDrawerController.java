package droidsquad.voyage.controller.activityController;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.VoyageUser;
import droidsquad.voyage.view.activity.LoginActivity;
import droidsquad.voyage.view.activity.MainNavDrawerActivity;
import droidsquad.voyage.view.fragment.RequestsFragment;
import droidsquad.voyage.view.fragment.TripListFragment;

/**
 * Controls the MainNavDrawerActivity. Basically just handles user interaction with the nav drawer,
 * most notably selecting options within the navigation drawer
 */
public class MainNavDrawerController {

    private MainNavDrawerActivity activity;
    private VoyageUser user;
    private android.support.v4.app.FragmentManager fragmentManager;

    public MainNavDrawerController(MainNavDrawerActivity activity) {
        this.activity = activity;
        fragmentManager = activity.getSupportFragmentManager();
        this.user = new VoyageUser();
    }

    public void pendingInvitationsPressed() {

    }

    /**
     * Called when the Trips option is selected from the navigation drawer
     */
    public void tripsPressed() {
        Fragment fragment = TripListFragment.newInstance();
        changeFragment(fragment, activity.getString(R.string.label_trip_list_fragment));
    }



    /**
     * Called when the Feed option is selected from the navigation drawer
     */
    public void feedPressed() {
        Toast.makeText(activity, "Feed Fragment", Toast.LENGTH_SHORT).show();
        // TODO: create feed fragment
        // Fragment fragment = FeedFragment.newInstance();
        // changeFragment(fragment, "Feed");
    }

    /**
     * Called when the Settings option is selected from the navigation drawer
     */
    public void settingsPressed() {
        Toast.makeText(activity, "Settings Fragment", Toast.LENGTH_SHORT).show();
        // TODO: create settings fragment
        // Fragment fragment = SettingsFragment.newInstance();
        // changeFragment(fragment, "Feed");
    }

    public void requestsPressed() {
        changeFragment(new RequestsFragment(), activity.getString(R.string.label_requests_fragment));
    }

    /**
     * Called when the Logout option is selected from the navigation drawer
     */
    public void logOutUser() {
        user.logOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    /**
     * Logic used to do the actual fragment changing
     * @param fragment  created from newInstance()
     * @param fragmentLabel     string label describing the fragment
     */
    private void changeFragment(Fragment fragment, String fragmentLabel) {
        Fragment currentFragment = activity.getSupportFragmentManager().
                findFragmentByTag(fragmentLabel);
        if(currentFragment == null || !currentFragment.isVisible()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, fragmentLabel)
                    .commit();
            activity.setTitle(fragmentLabel);
        }
    }
}
