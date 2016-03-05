package droidsquad.voyage.model.adapters;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.model.objects.VoyageUser;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.TripActivity;

public class TripCardAdapter extends RecyclerView.Adapter<TripCardAdapter.ViewHolder> {
    private static final String TAG = TripCardAdapter.class.getSimpleName();

    private List<Trip> trips = new ArrayList<>();
    private Fragment mFragment;

    public TripCardAdapter(Fragment fragment) {
        this.trips = new ArrayList<>();
        this.mFragment = fragment;
    }

    @Override
    public TripCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Trip trip = trips.get(position);

        holder.mName.setText(trip.getName());
        holder.mCities.setText(trip.getSimpleCitiesStringRepresentation());
        holder.mDates.setText(trip.getSimpleDatesStringRepresentation());
        holder.mPrivateIcon.setVisibility((trip.isPrivate()) ? View.VISIBLE : View.GONE);
        holder.mCities.setCompoundDrawablesWithIntrinsicBounds(trip.getTransportationIconId(), 0, 0, 0);

        // Handle displaying the Admin information logic
        trip.getAdmin().loadProfilePicInto(mFragment.getContext(), holder.mAdminProfilePic);
        holder.mAdmin.setVisibility(
                (VoyageUser.currentUser().equals(trip.getAdmin())) ? View.VISIBLE : View.GONE);

        // Delegate displaying the members to TripCardMembersAdapter
        List<User> members = trip.getMembersAsUsersExclusive();
        holder.mMembersRecyclerView.setVisibility((members.size()) <= 0 ? View.GONE : View.VISIBLE);
        holder.mMembersAdapter.updateDataset(members);

        holder.mTripCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Card clicked for trip: " + trip.getName());

                Intent intent = new Intent(mFragment.getContext(), TripActivity.class);
                intent.putExtra(mFragment.getString(R.string.intent_key_trip), trip);
                mFragment.startActivityForResult(intent, Constants.REQUEST_CODE_TRIP_ACTIVITY);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void updateData(List<Trip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mAdmin;
        public TextView mCities;
        public TextView mDates;
        public ImageView mAdminProfilePic;
        public ImageView mPrivateIcon;
        public CardView mTripCard;
        public TripCardMembersAdapter mMembersAdapter;
        public RecyclerView mMembersRecyclerView;

        public ViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.trip_card_name);
            mAdmin = (TextView) view.findViewById(R.id.trip_card_admin);
            mCities = (TextView) view.findViewById(R.id.trip_card_cities);
            mDates = (TextView) view.findViewById(R.id.trip_card_date_range);
            mAdminProfilePic = (ImageView) view.findViewById(R.id.trip_card_admin_profile_pic);
            mPrivateIcon = (ImageView) view.findViewById(R.id.trip_card_private_icon);
            mTripCard = (CardView) view.findViewById(R.id.trip_card_view);

            // Build the recycler view for the Trip members
            TextView picturesExcess = (TextView) view.findViewById(R.id.trip_card_pictures_excess);
            mMembersAdapter = new TripCardMembersAdapter(view.getContext(), picturesExcess);

            mMembersRecyclerView = (RecyclerView) view.findViewById(R.id.trip_card_members);
            mMembersRecyclerView.setAdapter(mMembersAdapter);
            mMembersRecyclerView.setLayoutManager(
                    new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false) {
                        @Override
                        public boolean canScrollHorizontally() {
                            return false;
                        }
                    }
            );
        }
    }
}
