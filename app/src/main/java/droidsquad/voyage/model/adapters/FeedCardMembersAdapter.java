package droidsquad.voyage.model.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.User;

public class FeedCardMembersAdapter extends RecyclerView.Adapter<FeedCardMembersAdapter.ViewHolder> {
    private static final String TAG = FeedCardMembersAdapter.class.getSimpleName();

    private Activity mActivity;
    private List<User> mFriends;

    public FeedCardMembersAdapter(Activity activity) {
        mActivity = activity;
        mFriends = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_dropdown_member, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User friend = mFriends.get(position);

        friend.loadProfilePicInto(mActivity, holder.mProfilePicImageView);
        holder.mNameTextView.setText(friend.getFullName());
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    /**
     * Update the data set of this adapter
     *
     * @param friends The friends to update the data set with
     */
    public void updateResults(List<User> friends) {
        mFriends = friends;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public ImageView mProfilePicImageView;

        public ViewHolder(View view) {
            super(view);

            mNameTextView = (TextView) view.findViewById(R.id.name);
            mProfilePicImageView = (ImageView) view.findViewById(R.id.profile_pic);
        }
    }
}