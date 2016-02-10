package droidsquad.voyage.controller;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import droidsquad.voyage.activity.LoginActivity;
import droidsquad.voyage.activity.TripListActivity;
import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.Trip;
import droidsquad.voyage.model.TripCardAdapter;
import droidsquad.voyage.model.VoyageUser;

public class TripListController {
    private TripListActivity activity;
    private TripCardAdapter adapter;
    private VoyageUser user;

    public TripListController(TripListActivity activity) {
        this.activity = activity;
        this.user = new VoyageUser();
    }

    // called once from the activity, only needs to be called once
    public void setAdapter(RecyclerView recyclerView) {
        // set if size won't immediately change based on user interaction
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        // specify an adapter (see also next example)
        adapter = new TripCardAdapter(activity);
        recyclerView.setAdapter(adapter);
    }

    // to be called from the activity on startup and/or data refresh
    public void retrieveData() {
        // TODO: create a method in ParseModel, or another model class if necessary to retrieve data and call updateAdapter() below
        ParseTripModel ptm = new ParseTripModel(this);
        ptm.searchForAllTrips();
    }

    public void updateAdapter(ArrayList<Trip> trips) {
        adapter.updateData(trips);
        refreshData();
    }

    public void refreshData() {
        adapter.notifyDataSetChanged();
    }

    public void refreshViewContents() {
        ParseTripModel.searchForAllTrips();
    }

    public void logOutUser() {
        // TODO: update this method
        user.logOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}
