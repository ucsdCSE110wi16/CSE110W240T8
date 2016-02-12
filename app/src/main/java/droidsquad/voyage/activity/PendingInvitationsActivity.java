package droidsquad.voyage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.parse.ParseUser;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.PendingInvitationsController;
import droidsquad.voyage.controller.TripListController;

/**
 * Created by Vivian on 2/12/2016.
 */
public class PendingInvitationsActivity extends AppCompatActivity {
    private PendingInvitationsController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_invites);

        controller = new PendingInvitationsController(this);

    }
}
