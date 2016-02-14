package droidsquad.voyage.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.fragmentController.RequestsController;

public class RequestsFragment extends Fragment {
    private RequestsController mController;
    private RecyclerView mRequestsRecyclerView;
    private ProgressBar mProgressBar;
    private LinearLayout mNoRequestsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        initUI(view);

        mController = new RequestsController(this);

        mRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRequestsRecyclerView.setAdapter(mController.getAdapter());

        return view;
    }

    private void initUI(View view) {
        mRequestsRecyclerView = (RecyclerView) view.findViewById(R.id.requests_recycler_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.requests_progress_bar);
        mNoRequestsView = (LinearLayout) view.findViewById(R.id.no_requests_view);
    }

    public void showProgress(boolean show) {
        if (show) {
            mRequestsRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mRequestsRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void showNoRequestsView(boolean show) {
        if (show) {
            mNoRequestsView.setVisibility(View.VISIBLE);
            mRequestsRecyclerView.setVisibility(View.GONE);
        } else {
            mNoRequestsView.setVisibility(View.GONE);
            mRequestsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
