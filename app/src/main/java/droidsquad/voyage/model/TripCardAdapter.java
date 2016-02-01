package droidsquad.voyage.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import droidsquad.voyage.R;

/**
 * Created by Andrew on 1/31/16.
 */
public class TripCardAdapter extends RecyclerView.Adapter<TripCardAdapter.ViewHolder> {
    private ArrayList<Trip> trips = new ArrayList<>();

    // TODO:
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // example:
        // public TextView title;
        // public

        // TODO: i'm not actually sure if you pass it a view or not, double check/research this
        public ViewHolder(View view) {
            super(view);
            // TODO: UI references here
            // example:
            // title = (TextView) findViewById(R.id.title...);
        }
    }

    public TripCardAdapter() {
        trips = new ArrayList<>();
    }

    public void updateData(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TripCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_card, parent, false);

        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // TODO: This is where you will bind all the data to the UI elements
        // example:
        // holder.mTextView.setText(trips.get(position).getOrigin());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return trips.size();
    }
}
