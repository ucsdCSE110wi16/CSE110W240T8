package droidsquad.voyage.model;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import droidsquad.voyage.R;

public class FBFriendsAdapter extends RecyclerView.Adapter<FBFriendsAdapter.ViewHolder> {
    private Activity mActivity;
    private ArrayList<FacebookUser> results;

    private static final String TAG = FBFriendsAdapter.class.getSimpleName();

    public FBFriendsAdapter(Activity activity) {
        mActivity = activity;
        results = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fb_friend_drop_down_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FacebookUser friend = results.get(position);

        holder.mNameTextView.setText(friend.name);
        Picasso.with(mActivity)
                .load(friend.pictureURL)
                .placeholder(R.drawable.ic_account_circle)
                .into(holder.mProfilePicImageView);

        //FacebookAPI.getProfilePicAsync(holder.mProfilePicImageView, friend.id, "square");
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public ImageView mProfilePicImageView;

        public ViewHolder(View view) {
            super(view);

            mNameTextView = (TextView) view.findViewById(R.id.friend_name);
            mProfilePicImageView = (ImageView) view.findViewById(R.id.friend_profile_pic);
        }
    }

    public void updateResults(ArrayList<FacebookUser> friends) {
        results = friends;
        notifyDataSetChanged();
    }
}