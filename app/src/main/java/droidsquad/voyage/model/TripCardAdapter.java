package droidsquad.voyage.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.activity.TripActivity;

public class TripCardAdapter extends RecyclerView.Adapter<TripCardAdapter.ViewHolder> {
    private static final String TAG = TripCardAdapter.class.getSimpleName();
    private ArrayList<Trip> trips = new ArrayList<>();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mCities;
        public TextView mDates;
        public ImageView mPrivateIcon;
        public ImageView mTransportationIcon;
        public CardView mTripCard;

        public ViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.trip_card_name);
            mCities = (TextView) view.findViewById(R.id.trip_card_cities);
            mDates = (TextView) view.findViewById(R.id.trip_card_date_range);
            mPrivateIcon = (ImageView) view.findViewById(R.id.trip_card_private_icon);
            mTransportationIcon = (ImageView) view.findViewById(R.id.trip_card_transportation_icon);
            mTripCard = (CardView) view.findViewById(R.id.trip_card_view);
        }
    }

    public TripCardAdapter(Context context) {
        this.trips = new ArrayList<>();
        this.context = context;
    }

    public void updateData(ArrayList<Trip> trips) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Trip trip = this.trips.get(position);

        holder.mName.setText(trip.getName());

        try {
            JSONObject origin = new JSONObject(trip.getOrigin());
            JSONObject dest = new JSONObject(trip.getDestination());

            holder.mCities.setText(origin.get("city") + " –> " + dest.get("city"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set the dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        String dates = context.getString(R.string.trip_dates,
                dateFormat.format(trip.getDateFrom()), dateFormat.format(trip.getDateTo()));
        holder.mDates.setText(dates);

        holder.mPrivateIcon.setVisibility(
                (trip.isPrivate()) ? View.VISIBLE : View.GONE);

        switch (trip.getTransportation()) {
            case "Car":
                holder.mTransportationIcon.setImageResource(R.drawable.ic_car);
                break;

            case "Bus":
                holder.mTransportationIcon.setImageResource(R.drawable.ic_bus);
                break;

            default :
                holder.mTransportationIcon.setImageResource(R.drawable.ic_flight);
                break;
        }

        holder.mTripCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Card clicked for trip: " + trip.getName());

                Intent intent = new Intent(context, TripActivity.class);
                intent.putExtra(context.getString(R.string.intent_key_trip), trip);
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return trips.size();
    }
}
