package droidsquad.voyage.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.AddFriendsController;
import droidsquad.voyage.controller.AutoWrappingLinearLayoutManager;
import droidsquad.voyage.model.FBFriendsAdapter;
import droidsquad.voyage.model.SelectedFBFriendsAdapter;

public class AddFriendsActivity extends AppCompatActivity {
    private AddFriendsController controller;

    private android.support.v7.widget.SearchView mSearchView;
    private RecyclerView mResultsRecyclerView;
    private RecyclerView mSeletedFriendsRecyclerView;
    private FBFriendsAdapter mResultsAdapter;
    private SelectedFBFriendsAdapter mSelectedFriendsAdapter;
    private SearchManager mSearchManager;
    private ImageView mExitBackImage;

    private static final String TAG = AddFriendsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        initUI();

        mResultsAdapter = new FBFriendsAdapter(this);
        mResultsRecyclerView.setLayoutManager(new AutoWrappingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mResultsRecyclerView.setAdapter(mResultsAdapter);

        mSelectedFriendsAdapter = new SelectedFBFriendsAdapter(this);
        mSeletedFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSeletedFriendsRecyclerView.setAdapter(mSelectedFriendsAdapter);

        controller = new AddFriendsController(this, mResultsAdapter, mSelectedFriendsAdapter);

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

        mExitBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Initialize all the UI Elements of this activity
     */
    private void initUI() {
        mSearchView = (android.support.v7.widget.SearchView) findViewById(R.id.friends_search_view);
        mResultsRecyclerView = (RecyclerView) findViewById(R.id.friends_results_recycler_view);
        mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
        mExitBackImage = (ImageView) findViewById(R.id.exit_back_image_view);
        mSeletedFriendsRecyclerView = (RecyclerView) findViewById(R.id.selected_friends_recycler_view);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, false);
        }
    }
}
