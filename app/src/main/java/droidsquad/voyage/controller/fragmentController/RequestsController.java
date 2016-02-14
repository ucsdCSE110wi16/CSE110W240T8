package droidsquad.voyage.controller.fragmentController;

import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.ParseRequestModel;
import droidsquad.voyage.model.adapters.RequestsAdapter;
import droidsquad.voyage.model.objects.Request;
import droidsquad.voyage.view.fragment.RequestsFragment;

public class RequestsController {
    private static final String TAG = RequestsController.class.getSimpleName();
    private RequestsFragment mFragment;
    private RequestsAdapter mAdapter;

    public RequestsController(RequestsFragment fragment) {
        this.mFragment = fragment;
        this.mAdapter = new RequestsAdapter(mFragment.getContext());

        setRequestButtonClicks();

        setOnAdapterEmptyListener();

        mFragment.showProgress(true);
        fetchData();
    }

    private void setOnAdapterEmptyListener() {
        mAdapter.setOnDataEmptyListener(new RequestsAdapter.OnDataEmptyListener() {
            @Override
            public void onEmpty() {
                mFragment.showNoRequestsView(true);
            }
        });
    }

    private void setRequestButtonClicks() {
        mAdapter.setOnButtonClickedCallback(new RequestsAdapter.OnButtonClickedCallback() {
            @Override
            public void onAcceptClicked(final Request request) {
                acceptRequest(request);
            }

            @Override
            public void onDeclineClicked(Request request) {
                declineRequest(request);
            }
        });
    }

    private void acceptRequest(final Request request) {
        Log.d(TAG, "Accepting request for " + request.tripName);
        ParseRequestModel.acceptRequest(request.tripId, new ParseRequestModel.OnResultCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully accepted Trip.");
                mAdapter.removeRequest(request);
                Snackbar snackbar = Snackbar.make(mFragment.getView(),
                        R.string.snackbar_request_accepted, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Couldn't accept trip. Error: " + error);
                Snackbar snackbar = Snackbar.make(mFragment.getView(),
                        error, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    private void declineRequest(final Request request) {
        Log.d(TAG, "Declining request for " + request.tripName);
        ParseRequestModel.declineRequest(request.tripId, new ParseRequestModel.OnResultCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully declined Trip.");
                mAdapter.removeRequest(request);
                Snackbar snackbar = Snackbar.make(mFragment.getView(),
                        R.string.snackbar_request_declined, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Couldn't decline trip. Error: " + error);
                Snackbar snackbar = Snackbar.make(mFragment.getView(),
                        error, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    private void fetchData() {
        ParseRequestModel.fetchRequests(new ParseRequestModel.OnRequestsReceivedCallback() {
            @Override
            public void onSuccess(List<Request> requests) {
                mFragment.showProgress(false);
                mFragment.showNoRequestsView(false);
                mAdapter.updateAdapter(requests);
            }

            @Override
            public void onFailure(String error) {
                // TODO
                mFragment.showProgress(false);
            }
        });
    }

    public RequestsAdapter getAdapter() {
        return mAdapter;
    }
}
