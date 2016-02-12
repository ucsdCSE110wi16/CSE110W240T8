package droidsquad.voyage.controller;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import droidsquad.voyage.R;
import droidsquad.voyage.activity.LoginActivity;
import droidsquad.voyage.activity.MainNavDrawerActivity;
import droidsquad.voyage.activity.TripListFragment;
import droidsquad.voyage.model.VoyageUser;

/**
 * Created by Andrew on 2/12/16.
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

    public void tripsPressed() {
        Fragment fragment = TripListFragment.newInstance();
        changeFragment(fragment, "TripList");
    }



    public void feedPressed() {
        Toast.makeText(activity, "Feed Fragment", Toast.LENGTH_SHORT).show();
        // TODO: create feed fragment
        // Fragment fragment = FeedFragment.newInstance();
        // changeFragment(fragment, "Feed");
    }

    public void settingsPressed() {
        Toast.makeText(activity, "Settings Fragment", Toast.LENGTH_SHORT).show();
        // TODO: create settings fragment
        // Fragment fragment = SettingsFragment.newInstance();
        // changeFragment(fragment, "Feed");
    }

    public void logOutUser() {
        user.logOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    private void changeFragment(Fragment fragment, String fragmentLabel) {
        Fragment currentFragment = activity.getSupportFragmentManager().
                findFragmentByTag(fragmentLabel);
        if(currentFragment == null || !currentFragment.isVisible()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, fragmentLabel)
                    .commit();
        }
    }
}
