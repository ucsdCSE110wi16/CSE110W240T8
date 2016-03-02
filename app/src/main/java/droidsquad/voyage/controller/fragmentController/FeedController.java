package droidsquad.voyage.controller.fragmentController;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import droidsquad.voyage.model.parseModels.ParseTripModel;
import droidsquad.voyage.model.adapters.TripCardAdapter;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.view.fragment.FeedFragment;

public class FeedController {
    private static final String TAG = FeedController.class.getSimpleName();

    private FeedFragment fragment;
    private Context context;
    private TripCardAdapter adapter;

    public FeedController(FeedFragment fragment) {
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
        adapter = new TripCardAdapter(fragment);
        recyclerView.setAdapter(adapter);
    }

    // to be called from the activity on startup and/or data refresh
    public void retrieveData() {
        ParseTripModel.getPublicTrips(new ParseTripModel.TripListCallback() {
            @Override
            public void onSuccess(List<Trip> trips) {
                updateAdapter(trips);
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Failed to retrieve data from trips: " + error);
            }
        });
    }

    /**
     * Update the content of the adapter
     *
     * @param trips New trips to update the adapter with
     */
    public void updateAdapter(List<Trip> trips) {
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
