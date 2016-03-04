package droidsquad.voyage.model.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.User;

public class FBFriendsAdapter extends RecyclerView.Adapter<FBFriendsAdapter.ViewHolder> {
    private static final String TAG = FBFriendsAdapter.class.getSimpleName();

    private Activity mActivity;
    private OnFriendSelected mListener;
    private ArrayList<User> mFriends;

    public FBFriendsAdapter(Activity activity) {
        mActivity = activity;
        mFriends = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fb_friend_drop_down_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User friend = mFriends.get(position);

        friend.loadProfilePicInto(mActivity, holder.mProfilePicImageView);
        holder.mNameTextView.setText(friend.getFullName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSelected(friend);
                }
            }
        });
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
    public void updateResults(ArrayList<User> friends) {
        mFriends = friends;
        notifyDataSetChanged();
    }

    public void addFriend(User friend) {
        mFriends.add(friend);
        notifyDataSetChanged();
    }

    /**
     * Removes the given friend from this adapter
     *
     * @param friend Friend to be removed
     */
    public boolean removeFriend(User friend) {
        if (mFriends.remove(friend)) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void setOnFriendSelectedListener(OnFriendSelected listener) {
        this.mListener = listener;
    }

    public interface OnFriendSelected {
        void onSelected(User friend);
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
}