package droidsquad.voyage.controller.fragmentController;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.adapters.TripCardAdapter;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.view.fragment.TripListFragment;

public class TripListController {
    private Context context;
    private TripListFragment fragment;
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
        adapter = new TripCardAdapter(fragment);
        recyclerView.setAdapter(adapter);
    }

    // to be called from the activity on startup and/or data refresh
    public void retrieveData() {
        ParseTripModel.getTrips(new ParseTripModel.ParseTripCallback() {
            @Override
            public void onCompleted(ArrayList<Trip> trips) {
                // Sort the trips
                Collections.sort(trips, new Comparator<Trip>() {
                    public int compare(Trip m1, Trip m2) {
                        return m1.getDateTo().compareTo(m2.getDateTo());
                    }
                });

                adapter.updateData(trips);
            }
        });
    }

    public void createTripButtonPressed() {
        fragment.createTrip();
    }
}
