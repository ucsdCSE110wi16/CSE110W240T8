package droidsquad.voyage.controller.fragmentController;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import droidsquad.voyage.view.fragment.TripListFragment;
import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.adapters.TripCardAdapter;

public class TripListController {
    private TripListFragment fragment;
    private Context context;
    private TripCardAdapter adapter;

    public TripListController(TripListFragment fragment) {
        this.context = fragment.getContext();
        this.fragment = fragment;
    }

    // called once from the activity, only needs to be called once
    public void setAdapter(RecyclerView recyclerView) {
        // set if size won't immediately change based on user interaction
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // specify an adapter (see also next example)
        adapter = new TripCardAdapter(context);
        recyclerView.setAdapter(adapter);
    }

    // to be called from the activity on startup and/or data refresh
    public void retrieveData() {
        // TODO: create a method in ParseModel, or another model class if necessary to retrieve data and call updateAdapter() below
        ParseTripModel.searchForAllTrips(new ParseTripModel.ParseTripCallback() {
            @Override
            public void onCompleted(ArrayList<Trip> trip) {
                updateAdapter(trip);
            }
        });
    }

    /**
     * Update the content of the adapter
     *
     * @param trips New trips to update the adapter with
     */
    public void updateAdapter(ArrayList<Trip> trips) {
        adapter.updateData(trips);
        refreshData();
    }

    /**
     * Refreshes the data within the adapter
     */
    private void refreshData() {
        adapter.notifyDataSetChanged();
    }

    public void createTripButtonPressed() {
        fragment.createTrip();
    }
}
