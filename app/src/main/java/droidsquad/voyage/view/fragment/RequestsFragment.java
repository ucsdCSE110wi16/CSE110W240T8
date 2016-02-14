package droidsquad.voyage.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import droidsquad.voyage.R;

public class RequestsFragment extends Fragment {

    private RecyclerView mRequestsRecyclerView;

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

        mRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void initUI(View view) {
        mRequestsRecyclerView = (RecyclerView) view.findViewById(R.id.requests_recycler_view);
    }
}
