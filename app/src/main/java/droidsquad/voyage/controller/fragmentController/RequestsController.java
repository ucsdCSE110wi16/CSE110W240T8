package droidsquad.voyage.controller.fragmentController;

import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.parseModels.ParseRequestModel;
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

        mFragment.refreshing(true);
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
        ParseRequestModel.acceptRequest(request.memberId, new ParseRequestModel.ParseResponseCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully accepted Trip.");
                mAdapter.removeRequest(request);
                showSnackbar(R.string.snackbar_request_accepted);
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Couldn't accept trip. Error: " + error);
                showSnackbar(error);
            }
        });
    }

    private void declineRequest(final Request request) {
        Log.d(TAG, "Declining request for " + request.tripName);
        ParseRequestModel.declineRequest(request.tripId, new ParseRequestModel.ParseResponseCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully declined Trip.");
                mAdapter.removeRequest(request);
                showSnackbar(R.string.snackbar_request_declined);
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Couldn't decline trip. Error: " + error);
                showSnackbar(error);
            }
        });
    }

    public void fetchData() {
        mFragment.showProgress(true);

        ParseRequestModel.fetchRequests(new ParseRequestModel.OnRequestsReceivedCallback() {
            @Override
            public void onSuccess(List<Request> requests) {
                Log.d(TAG, "Requests received: " + requests.size());
                mFragment.showProgress(false);
                mFragment.refreshing(false);
                mFragment.showNoRequestsView(false);
                mAdapter.updateAdapter(requests);
            }

            @Override
            public void onFailure(String error) {
                // TODO
                Log.d(TAG, "Error while receiving requests: " + error);
                mFragment.showProgress(false);
                mFragment.refreshing(false);
            }
        });
    }

    private void showSnackbar(String messageId) {
        Snackbar.make(mFragment.getView(), messageId, Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackbar(int messageId) {
        showSnackbar(mFragment.getString(messageId));
    }

    public RequestsAdapter getAdapter() {
        return mAdapter;
    }
}
