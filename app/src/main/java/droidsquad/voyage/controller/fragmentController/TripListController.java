package droidsquad.voyage.controller.fragmentController;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import droidsquad.voyage.model.parseModels.ParseTripModel;
import droidsquad.voyage.model.adapters.TripCardAdapter;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.view.fragment.TripListFragment;

public class TripListController {
    private static final String TAG = TripListController.class.getSimpleName();

    private Context context;
    private TripListFragment fragment;
    private TripCardAdapter adapter;

    public TripListController(final TripListFragment fragment) {
        this.context = fragment.getContext();
        this.fragment = fragment;
        this.adapter = new TripCardAdapter(fragment);

        adapter.setOnDataEmptyListener(new TripCardAdapter.OnDataEmptyListener() {
            @Override
            public void onEmpty() {
                fragment.showNoRequestsView(true);
            }
        });
    }

    // called once from the activity, only needs to be called once
    public void setAdapter(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    // to be called from the activity on startup and/or data refresh
    public void retrieveData() {
        if (adapter.getItemCount() == 0) {
            fragment.showProgress(true);
        }

        ParseTripModel.getTrips(new ParseTripModel.TripListCallback() {
            @Override
            public void onSuccess(List<Trip> trips) {
                // Sort the trips
                Collections.sort(trips, new Comparator<Trip>() {
                    public int compare(Trip m1, Trip m2) {
                        return m1.getDateTo().compareTo(m2.getDateTo());
                    }
                });

                fragment.showProgress(false);
                adapter.updateData(trips);
            }

            @Override
            public void onFailure(String error) {
                fragment.showProgress(false);
                Log.d(TAG, "Failed to retrieved the data for the trips: " + error);
            }
        });
    }

    public void createTripButtonPressed() {
        fragment.createTrip();
    }
}
