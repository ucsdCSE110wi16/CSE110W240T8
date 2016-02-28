package droidsquad.voyage.model.adapters;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.TripActivity;
import droidsquad.voyage.view.fragment.TripListFragment;

public class TripCardAdapter extends RecyclerView.Adapter<TripCardAdapter.ViewHolder> {
    private static final String TAG = TripCardAdapter.class.getSimpleName();
    private ArrayList<Trip> trips = new ArrayList<>();
    private Fragment mFragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mCities;
        public TextView mDates;
        public TextView mOtherMembers;
        public ImageView mPrivateIcon;
        public ImageView mTransportationIcon;
        public ImageView mMember1;
        public ImageView mMember2;
        public ImageView mMember3;
        public ImageView mMember4;
        public CardView mTripCard;

        public ViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.trip_card_name);
            mCities = (TextView) view.findViewById(R.id.trip_card_cities);
            mDates = (TextView) view.findViewById(R.id.trip_card_date_range);
            mOtherMembers = (TextView) view.findViewById(R.id.trip_card_other_members);
            mPrivateIcon = (ImageView) view.findViewById(R.id.trip_card_private_icon);
            mTransportationIcon = (ImageView) view.findViewById(R.id.trip_card_transportation_icon);
            mMember1 = (ImageView) view.findViewById(R.id.trip_card_member_profile_pic1);
            mMember2 = (ImageView) view.findViewById(R.id.trip_card_member_profile_pic2);
            mMember3 = (ImageView) view.findViewById(R.id.trip_card_member_profile_pic3);
            mMember4 = (ImageView) view.findViewById(R.id.trip_card_member_profile_pic4);
            mTripCard = (CardView) view.findViewById(R.id.trip_card_view);
        }
    }

    public TripCardAdapter(Fragment fragment) {
        this.trips = new ArrayList<>();
        this.mFragment = fragment;
    }

    public void updateData(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    public void sortTrips(ArrayList<Trip> trips) {
        Collections.sort(trips, new Comparator<Trip>() {
            public int compare(Trip m1, Trip m2) {
                return m1.getDateTo().compareTo(m2.getDateTo());
            }
        });

        this.trips = trips;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public TripCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_card, parent, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Trip trip = trips.get(position);

        holder.mName.setText(trip.getName());

        try {
            holder.mCities.setText(
                    trip.getOrigin().get("city") + " –> " + trip.getDestination().get("city"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set the dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        String dates = mFragment.getString(R.string.trip_dates,
                dateFormat.format(trip.getDateFrom()), dateFormat.format(trip.getDateTo()));
        holder.mDates.setText(dates);

        holder.mPrivateIcon.setVisibility(
                (trip.isPrivate()) ? View.VISIBLE : View.GONE);

        setMemberPics(trip, holder);

        switch (trip.getTransportation()) {
            case "Car":
                holder.mTransportationIcon.setImageResource(R.drawable.ic_car);
                break;
            case "Bus":
                holder.mTransportationIcon.setImageResource(R.drawable.ic_bus);
                break;
            default:
                holder.mTransportationIcon.setImageResource(R.drawable.ic_flight);
                break;
        }

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

    private void setMemberPics(Trip trip, ViewHolder holder) {
        int numMembers = 0;
        if (trip.getAllMembers() != null)
            numMembers = trip.getAllMembers().size();

        switch (4 - numMembers) {
            case 4:
                holder.mMember1.setVisibility(View.GONE);
            case 3:
                holder.mMember2.setVisibility(View.GONE);
            case 2:
                holder.mMember3.setVisibility(View.GONE);
            case 1:
                holder.mMember4.setVisibility(View.GONE);
                break;
            case 0:
                break;
            default:
                holder.mOtherMembers.setVisibility(View.VISIBLE);
                String s = "... +" + (numMembers - 4);
                holder.mOtherMembers.setText(s);
                break;
        }

        if (trip.getAllMembers() == null) {
            Log.d(TAG, "SOMETHING IS WRONG HERE!!");
            return;
        }

        ArrayList<String> picURLs = setUpPicURLs(trip);
        if (picURLs == null) {
            Log.d(TAG, "ERROR with PicURLs");
            return;
        }

        for (int i = 0; i < 4; i++) {
            if (picURLs.size() == i)
                break;
            ImageView currentView;
            switch (i) {
                case 3:
                    currentView = holder.mMember4;
                    break;
                case 2:
                    currentView = holder.mMember3;
                    break;
                case 1:
                    currentView = holder.mMember2;
                    break;
                default:
                    currentView = holder.mMember1;
                    break;
            }
            Picasso.with(mFragment.getContext())
                    .load(picURLs.get(i))
                    .into(currentView);
        }
    }

    public ArrayList<String> setUpPicURLs(Trip trip) {
        ArrayList<Trip.TripMember> members = trip.getAllMembers();
        ArrayList<String> picURLs = new ArrayList<>();
        if(members == null) {
            Log.d(TAG, "ERROR with fbIds");
            return null;
        }
        for(int i = 0; i < members.size(); i++) {
            String picURL = String.format(Constants.FB_PICTURE_URL, members.get(i).fbId, "normal");
            if(picURL == null) {
                Log.d(TAG, "ERROR obtaining picURL");
                break;
            }
            picURLs.add(picURL);
        }
        return picURLs;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return trips.size();
    }

}
