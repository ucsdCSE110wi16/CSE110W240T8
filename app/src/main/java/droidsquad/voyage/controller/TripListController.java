package droidsquad.voyage.controller;

import android.content.Intent;

import droidsquad.voyage.activity.LoginActivity;
import droidsquad.voyage.activity.TripListActivity;
import droidsquad.voyage.model.VoyageUser;

/**
 * Created by Andrew on 1/29/16.
 */
public class TripListController {

    private TripListActivity activity;
    private VoyageUser user = new VoyageUser();

    public TripListController(TripListActivity activity) {
        this.activity = activity;
    }

    public void logOutUser() {
        // TODO: update this method
        user.logOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}
