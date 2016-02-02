package droidsquad.voyage.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import droidsquad.voyage.R;

public class TripCardAdapter extends RecyclerView.Adapter<TripCardAdapter.ViewHolder> {
    private ArrayList<Trip> trips = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mCities;
        public TextView mDates;
        public ImageView mPrivateIcon;

        public ViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.trip_card_name);
            mCities = (TextView) view.findViewById(R.id.trip_card_cities);
            mDates = (TextView) view.findViewById(R.id.trip_card_date_range);
            mPrivateIcon = (ImageView) view.findViewById(R.id.trip_card_is_private);
        }
    }

    public TripCardAdapter() {
        this.trips = new ArrayList<>();
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
        Trip trip = this.trips.get(position);

        holder.mName.setText(trip.getName());
        holder.mCities.setText(trip.getOrigin() + " –> " + trip.getDestination());
        holder.mDates.setText(trip.getDateFrom() + " – " + trip.getDateTo());
        holder.mPrivateIcon.setVisibility(
                (trip.isPrivate()) ? View.VISIBLE : View.GONE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return trips.size();
    }
}
