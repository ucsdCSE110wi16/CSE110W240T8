package droidsquad.voyage.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;
import droidsquad.voyage.R;
import droidsquad.voyage.controller.activityController.MainNavDrawerController;
import droidsquad.voyage.util.Constants;

/**
 * Main nav bar activity, displays fragments (triplist, feed, etc)
 */
public class MainNavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {

    private MainNavDrawerController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // check if the user is logged in
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new MainNavDrawerController(this);

        // default start with TripList fragment
        controller.tripsPressed();
        initUI();
    }


    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        CircleImageView profilePic = (CircleImageView) headerView.findViewById(R.id.nav_drawer_profile_pic);
        TextView userName = (TextView) headerView.findViewById(R.id.nav_drawer_user_name);

        ParseUser user = ParseUser.getCurrentUser();

        userName.setText(user.get("firstName") + " " + user.get("lastName"));
        // Load the profile picture in the nav drawer
        Picasso.with(this)
                .load(String.format(
                        Constants.FB_PICTURE_URL, user.get("fbId"), "square"))
                .placeholder(R.drawable.ic_account_circle_gray)
                .into(profilePic);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            controller.feedPressed();

        } else if (id == R.id.nav_trips) {
            controller.tripsPressed();
        } else if (id == R.id.nav_settings) {
            controller.settingsPressed();

        } else if (id == R.id.nav_logout) {
            controller.logOutUser();
        } else if (id == R.id.nav_requests) {
            controller.requestsPressed();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public MainNavDrawerController getController() {
        return controller;
    }
}
