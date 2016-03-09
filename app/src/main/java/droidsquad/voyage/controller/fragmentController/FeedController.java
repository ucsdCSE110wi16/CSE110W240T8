package droidsquad.voyage.controller.fragmentController;

import android.util.Log;

import java.util.List;

import droidsquad.voyage.model.adapters.FeedCardAdapter;
import droidsquad.voyage.model.parseModels.ParseTripModel;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.NetworkAlerts;
import droidsquad.voyage.view.fragment.FeedFragment;

public class FeedController {
    private static final String TAG = FeedController.class.getSimpleName();

    private FeedFragment fragment;
    private FeedCardAdapter adapter;

    public FeedController(FeedFragment fragment) {
        this.fragment = fragment;
        this.adapter = new FeedCardAdapter(fragment);
    }

    // to be called from the activity on startup and/or data refresh
    public void retrieveData() {
        if (NetworkAlerts.isNetworkAvailable(fragment.getContext())) {
            ParseTripModel.getTripsFromFriends(new ParseTripModel.TripListCallback() {
                @Override
                public void onSuccess(List<Trip> trips) {
                    fragment.showProgress(false);
                    adapter.updateDataset(trips);
                }

                @Override
                public void onFailure(String error) {
                    fragment.showProgress(false);
                    Log.d(TAG, "Failed to retrieve data from trips: " + error);
                }
            });
        } else {
            NetworkAlerts.showNetworkAlert(fragment.getContext());
        }
    }

    public void createTripButtonPressed() {
        fragment.startCreateTripActivity();
    }

    public FeedCardAdapter getAdapter() {
        return adapter;
    }
}
