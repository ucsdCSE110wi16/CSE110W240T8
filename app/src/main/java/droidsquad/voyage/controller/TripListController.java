package droidsquad.voyage.controller;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import droidsquad.voyage.activity.LoginActivity;
import droidsquad.voyage.activity.TripListActivity;
import droidsquad.voyage.model.Trip;
import droidsquad.voyage.model.TripCardAdapter;
import droidsquad.voyage.model.VoyageUser;

/**
 * Created by Andrew on 1/29/16.
 */
public class TripListController {

    private TripListActivity activity;
    private VoyageUser user = new VoyageUser();
    private TripCardAdapter adapter;

    public TripListController(TripListActivity activity) {
        this.activity = activity;
    }

    // called once from the activity, only needs to be called once
    public void setAdapter(RecyclerView recyclerView) {
        // set if size won't immediately change based on user interaction
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        // specify an adapter (see also next example)
        adapter = new TripCardAdapter();
        recyclerView.setAdapter(adapter);
    }

    // to be called from the activity on startup and/or data refresh
    public void retrieveData() {
        // TODO: create a method in ParseModel, or another model class if necessary to retrieve data and call updateAdapter() below

    }

    // TODO: BACKEND, CALL THIS ONCE YOU HAVE THE ARRAYLIST OF TRIPS
    public void updateAdapter(ArrayList<Trip> trips) {
        adapter.updateData(trips);
    }

    public void logOutUser() {
        // TODO: update this method
        user.logOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}
