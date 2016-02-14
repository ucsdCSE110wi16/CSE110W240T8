package droidsquad.voyage.controller.fragmentController;

import java.util.List;

import droidsquad.voyage.model.ParseRequestModel;
import droidsquad.voyage.model.adapters.RequestsAdapter;
import droidsquad.voyage.model.objects.Request;
import droidsquad.voyage.view.fragment.RequestsFragment;

public class RequestsController {
    private RequestsFragment mFragment;
    private RequestsAdapter mAdapter;

    public RequestsController(RequestsFragment fragment) {
        this.mFragment = fragment;
        this.mAdapter = new RequestsAdapter(mFragment.getContext());

        fetchData();
    }

    private void fetchData() {
        ParseRequestModel.fetchRequests(new ParseRequestModel.OnRequestsRetrievedCallback() {
            @Override
            public void onSuccess(List<Request> requests) {
                mAdapter.updateAdapter(requests);
            }

            @Override
            public void onFailure(String error) {
                // TODO
            }
        });
    }

    public RequestsAdapter getAdapter() {
        return mAdapter;
    }
}
