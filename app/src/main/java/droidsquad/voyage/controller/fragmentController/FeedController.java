package droidsquad.voyage.controller.fragmentController;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import droidsquad.voyage.model.adapters.FeedCardAdapter;
import droidsquad.voyage.model.parseModels.ParseTripModel;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.NetworkAlerts;
import droidsquad.voyage.view.fragment.FeedFragment;

public class FeedController {
    private static final String TAG = FeedController.class.getSimpleName();

    private Context context;
    private FeedFragment fragment;
    private FeedCardAdapter adapter;

    public FeedController(FeedFragment fragment) {
        this.context = fragment.getContext();
        this.fragment = fragment;
        this.adapter = new FeedCardAdapter(fragment);
    }

    // to be called from the activity on startup and/or data refresh
    public void retrieveData() {
        if (NetworkAlerts.isNetworkAvailable(context)) {
            fragment.showProgress(true);
            ParseTripModel.getTripsFromFriends(new ParseTripModel.TripListCallback() {
                @Override
                public void onSuccess(List<Trip> trips) {
                    fragment.showProgress(false);
                    updateAdapter(trips);
                }

                @Override
                public void onFailure(String error) {
                    fragment.showProgress(false);
                    Log.d(TAG, "Failed to retrieve data from trips: " + error);
                }
            });
        }
        else {
            NetworkAlerts.showNetworkAlert(context);
        }
    }

    /**
     * Update the content of the adapter
     *
     * @param trips New trips to update the adapter with
     */
    public void updateAdapter(List<Trip> trips) {
        adapter.updateDataset(trips);
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

    public FeedCardAdapter getAdapter() {
        return adapter;
    }
}
