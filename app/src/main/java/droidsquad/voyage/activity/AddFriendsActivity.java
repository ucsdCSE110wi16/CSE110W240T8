package droidsquad.voyage.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.AddFriendsController;
import droidsquad.voyage.model.FBFriendsAdapter;
import droidsquad.voyage.model.FacebookAPI;
import droidsquad.voyage.model.FacebookUser;

public class AddFriendsActivity extends AppCompatActivity {
    private static final String TAG = AddFriendsActivity.class.getSimpleName();
    private android.support.v7.widget.SearchView mFriendsSearchView;
    private RecyclerView mFriendsResultsRecyclerView;
    private FBFriendsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        initUI();

        mAdapter = new FBFriendsAdapter();
        mFriendsResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFriendsResultsRecyclerView.setAdapter(mAdapter);

        final AddFriendsController controller = new AddFriendsController(this, mAdapter);

        mFriendsSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                controller.setQueryChange(newText);
                return false;
            }
        });
    }

    private void initUI() {
        mFriendsSearchView = (android.support.v7.widget.SearchView) findViewById(R.id.friends_search_view);
        mFriendsResultsRecyclerView = (RecyclerView) findViewById(R.id.friends_results_recycler_view);
    }

}
