package droidsquad.voyage.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.TripListController;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripListFragment extends Fragment {

    private TripListController controller;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static Fragment newInstance() {
        return new TripListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        controller.retrieveData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set the controller
        this.controller = new TripListController(this);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trip_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        recyclerView = (RecyclerView) v.findViewById(R.id.trip_recycler_view);
        controller.setAdapter(recyclerView);


        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controller.retrieveData();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        FloatingActionButton fb = (FloatingActionButton) v.findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.createTripButtonPressed();
            }
        });

        return v;
    }

    public void createTrip() {
        Intent intent = new Intent(getContext(), CreateTripActivity.class);
        getContext().startActivity(intent);
    }

}
