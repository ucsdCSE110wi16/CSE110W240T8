package droidsquad.voyage.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public class FBFriendsArrayAdapter extends ArrayAdapter<FBFriendsArrayAdapter.FriendsAutoComplete> implements Filterable {
    private LayoutInflater inflater;
    private ArrayList<FriendsAutoComplete> friendsResult;

    public FBFriendsArrayAdapter(Context context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return friendsResult.size();
    }

    @Override
    public FriendsAutoComplete getItem(int position) {
        return friendsResult.get(position);
    }

    private ArrayList<FriendsAutoComplete> getPredictions(CharSequence constraint) {
        // TODO
        return null;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // TODO
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // TODO
            }
        };
        return filter;
    }

    public class FriendsAutoComplete {
        public CharSequence name;
        public Bitmap proPic;

        public FriendsAutoComplete(CharSequence name, Bitmap proPic) {
            this.name = name;
            this.proPic = proPic;
        }
    }
}
