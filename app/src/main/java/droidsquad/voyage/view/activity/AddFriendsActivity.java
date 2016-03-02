package droidsquad.voyage.view.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.activityController.AddFriendsController;

public class AddFriendsActivity extends AppCompatActivity {
    private AddFriendsController controller;

    private android.support.v7.widget.SearchView mSearchView;
    private RecyclerView mResultsRecyclerView;
    private RecyclerView mSelectedFriendsRecyclerView;
    private ImageView mExitBackImage;
    private AppCompatImageButton mConfirmAddFriendsButton;
    private ProgressBar mProgressBar;

    private static final String TAG = AddFriendsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        controller = new AddFriendsController(this);
        initUI();

        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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

        mConfirmAddFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.addFriendsToTrip();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, false);
        }
    }

    /**
     * Initialize all the UI Elements of this activity
     */
    private void initUI() {
        mSearchView = (android.support.v7.widget.SearchView) findViewById(R.id.friends_search_view);
        mResultsRecyclerView = (RecyclerView) findViewById(R.id.friends_results_recycler_view);
        mSelectedFriendsRecyclerView = (RecyclerView) findViewById(R.id.selected_friends_recycler_view);
        mExitBackImage = (ImageView) findViewById(R.id.exit_back_image_view);
        mConfirmAddFriendsButton = (AppCompatImageButton) findViewById(R.id.confirm_add_friends);
        mProgressBar = (ProgressBar) findViewById(R.id.add_friends_progress);

        mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat
                .getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

        // Set the search manager for the search view
        SearchManager mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));

        // Set the layout managers for the recycler views
        mResultsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSelectedFriendsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Set the adapters for the recycler views
        mResultsRecyclerView.setAdapter(controller.getResultsAdapter());
        mSelectedFriendsRecyclerView.setAdapter(controller.getSelectedFriendsAdapter());
    }

    /**
     * Display or hide progress
     *
     * @param show True if progress should be displayed
     */
    public void showProgress(boolean show) {
        mConfirmAddFriendsButton.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /* GETTERS */

    public String getQuery() {
        return mSearchView.getQuery().toString();
    }
}
