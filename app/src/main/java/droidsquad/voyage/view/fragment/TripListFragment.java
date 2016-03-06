package droidsquad.voyage.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.fragmentController.TripListController;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.CreateTripActivity;

/**
 * Fragment for displaying list of trips. Controller is instantiated in onCreateView
 */
public class TripListFragment extends Fragment {
    private TripListController controller;
    private RecyclerView recyclerView;
    private LinearLayout mNoTripsView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;


    public static Fragment newInstance() {
        return new TripListFragment();
    }

    /**
     * onResume overridden so that upon returning to this fragment when another activity closes
     * (like CreateTripActivity), data is re-polled to reflect newest changes on the Parse server.
     */
    @Override
    public void onResume() {
        super.onResume();
        controller.retrieveData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // the controller must be set here!
        this.controller = new TripListController(this);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trip_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        mProgressBar = (ProgressBar) v.findViewById(R.id.trip_list_progress_bar);
        recyclerView = (RecyclerView) v.findViewById(R.id.trip_recycler_view);
        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        mNoTripsView = (LinearLayout) v.findViewById(R.id.no_trips_view);

        controller.setAdapter(recyclerView);

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

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.createTripButtonPressed();
            }
        });

        return v;
    }

    /**
     * Navigates to the CreateTripActivity
     */
    public void createTrip() {
        Intent intent = new Intent(getContext(), CreateTripActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE_CREATE_TRIP_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Snackbar snackbar = Snackbar.make(mFab, "", Snackbar.LENGTH_SHORT);

        switch (requestCode) {
            case Constants.REQUEST_CODE_TRIP_ACTIVITY :
                switch (resultCode) {
                    case Constants.RESULT_CODE_TRIP_DELETED :
                        snackbar.setText(R.string.snackbar_trip_deleted).show();
                        break;

                    case Constants.RESULT_CODE_TRIP_LEFT :
                        snackbar.setText(R.string.snackbar_trip_left).show();
                        break;
                }
                break;

            case Constants.REQUEST_CODE_CREATE_TRIP_ACTIVITY :
                if (resultCode == Constants.RESULT_CODE_TRIP_CREATED) {
                    snackbar.setText(R.string.snackbar_trip_created).show();
                }
                break;
        }
    }

    public void showNoRequestsView(boolean show) {
        mNoTripsView.setVisibility((show) ? View.VISIBLE : View.GONE);
    }

    public void showProgress(boolean show) {
        if (show) {
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
