package droidsquad.voyage.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.AddFriendsController;
import droidsquad.voyage.controller.AutoWrappingLinearLayoutManager;
import droidsquad.voyage.model.FBFriendsAdapter;

public class AddFriendsActivity extends AppCompatActivity {
    private AddFriendsController controller;

    private android.support.v7.widget.SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private FBFriendsAdapter mAdapter;

    private static final String TAG = AddFriendsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        initUI();

        mAdapter = new FBFriendsAdapter(this);
        mRecyclerView.setLayoutManager(new AutoWrappingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        controller = new AddFriendsController(this, mAdapter);

        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                controller.onQueryTextChange(newText);
                return false;
            }
        });
    }

    /**
     * Initialize all the UI Elements of this activity
     */
    private void initUI() {
        mSearchView = (android.support.v7.widget.SearchView) findViewById(R.id.friends_search_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.friends_results_recycler_view);
    }
}
