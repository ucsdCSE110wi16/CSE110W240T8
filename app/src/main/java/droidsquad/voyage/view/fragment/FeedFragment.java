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

import droidsquad.voyage.R;
import droidsquad.voyage.controller.fragmentController.FeedController;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.CreateTripActivity;

public class FeedFragment extends Fragment {
    private FeedController controller;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFab;

    public static Fragment newInstance() {
        return new TripListFragment();
    }

    /**
     * onResume overriden so that upon returning to this fragment when another activity closes
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
        this.controller = new FeedController(this);

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

        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
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
                        snackbar.setText(R.string.snackbar_trip_deleted);
                        snackbar.show();
                        break;

                    case Constants.RESULT_CODE_TRIP_LEFT :
                        snackbar.setText(R.string.snackbar_trip_left);
                        snackbar.show();
                        break;
                }
                break;

            case Constants.REQUEST_CODE_CREATE_TRIP_ACTIVITY :
                if (resultCode == Constants.RESULT_CODE_TRIP_CREATED) {
                    snackbar.setText(R.string.snackbar_trip_created);
                    snackbar.show();
                }
                break;
        }
    }

}
