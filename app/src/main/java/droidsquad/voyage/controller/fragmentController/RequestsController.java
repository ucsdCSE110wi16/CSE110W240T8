package droidsquad.voyage.controller.fragmentController;

import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.adapters.RequestsAdapter;
import droidsquad.voyage.model.objects.Request;
import droidsquad.voyage.model.parseModels.ParseRequestModel;
import droidsquad.voyage.view.fragment.RequestsFragment;

public class RequestsController {
    private static final String TAG = RequestsController.class.getSimpleName();
    private RequestsFragment mFragment;
    private RequestsAdapter mAdapter;

    public RequestsController(RequestsFragment fragment) {
        this.mFragment = fragment;
        this.mAdapter = new RequestsAdapter(mFragment.getContext());

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

        mAdapter.setOnDataEmptyListener(new RequestsAdapter.OnDataEmptyListener() {
            @Override
            public void onEmpty() {
                mFragment.showNoRequestsView(true);
            }
        });

        mFragment.showProgress(true);
        fetchData();
    }

    private void acceptRequest(final Request request) {
        Log.d(TAG, "Accepting request for " + request.trip.getName());
        ParseRequestModel.acceptRequest(request, new ParseRequestModel.ParseResponseCallback() {
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
        Log.d(TAG, "Declining request for " + request.trip.getName());
        ParseRequestModel.declineRequest(request, new ParseRequestModel.ParseResponseCallback() {
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
        ParseRequestModel.fetchInvitationsAndRequests(new ParseRequestModel.RequestListCallback() {
            @Override
            public void onSuccess(final List<Request> requests) {
                Log.d(TAG, "Requests received: " + requests.size());

                Collections.sort(requests, new Comparator<Request>() {
                    @Override
                    public int compare(Request lhs, Request rhs) {
                        if (lhs.elapsedTime < rhs.elapsedTime) return 1;
                        else if (lhs.elapsedTime == rhs.elapsedTime) return 0;
                        else return -1;
                    }
                });

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

    private void showSnackbar(String message) {
        Snackbar.make(mFragment.getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackbar(int messageId) {
        showSnackbar(mFragment.getString(messageId));
    }

    public RequestsAdapter getAdapter() {
        return mAdapter;
    }
}
