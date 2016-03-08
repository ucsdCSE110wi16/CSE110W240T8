package droidsquad.voyage.view.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;
import droidsquad.voyage.R;
import droidsquad.voyage.controller.activityController.MainNavDrawerController;
import droidsquad.voyage.model.objects.VoyageUser;

/**
 * Main nav bar activity, displays fragments (tripList, feed, etc)
 */
public class MainNavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    private MainNavDrawerController controller;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new MainNavDrawerController(this);
        initUI();
    }

    /**
     * Initialize the UI elements
     */
    private void initUI() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        CircleImageView profilePicView = (CircleImageView) headerView.findViewById(R.id.nav_drawer_profile_pic);
        TextView userName = (TextView) headerView.findViewById(R.id.nav_drawer_user_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(1).setChecked(true); //set trips highlight at start

        toggle.syncState();
        userName.setText(VoyageUser.getFullName());

        // Load the profile picture in the nav drawer
        VoyageUser.currentUser().loadProfilePicInto(this, profilePicView);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_feed:
                controller.feedPressed();
                break;

            case R.id.nav_trips:
                controller.tripsPressed();
                break;

            case R.id.nav_logout:
                controller.logOutUser();
                break;

            case R.id.nav_requests:
                controller.requestsPressed();
                break;
        }

        item.setChecked(true);
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public MainNavDrawerController getController() {
        return controller;
    }
}
