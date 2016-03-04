package droidsquad.voyage.model.adapters;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.User;

public class SelectedFBFriendsAdapter extends RecyclerView.Adapter<SelectedFBFriendsAdapter.ViewHolder> {
    private Activity mActivity;
    private OnFriendRemovedListener mListener;
    private ArrayList<User> mSelectedFriends;

    public SelectedFBFriendsAdapter(Activity activity) {
        mSelectedFriends = new ArrayList<>();
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_fb_friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final User friend = mSelectedFriends.get(position);
        friend.loadProfilePicInto(mActivity, holder.mProfilePic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriend(position);
                if (mListener != null) {
                    mListener.onRemoved(friend);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSelectedFriends.size();
    }

    /**
     * Add a friend to this adapter
     *
     * @param friend Friend to be added
     */
    public void addFriend(User friend) {
        mSelectedFriends.add(friend);
        notifyItemInserted(mSelectedFriends.size() - 1);
    }

    /**
     * Removes the friend on the given position from the adapter
     *
     * @param position Position of friend to be removed
     */
    private void removeFriend(int position) {
        if (position < mSelectedFriends.size()) {
            mSelectedFriends.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mSelectedFriends.size());
        }
    }

    public void setOnFriendRemovedListener(OnFriendRemovedListener listener) {
        mListener = listener;
    }

    public List<User> getSelectedFriends() {
        return mSelectedFriends;
    }

    public interface OnFriendRemovedListener {
        void onRemoved(User friend);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mProfilePic;

        public ViewHolder(View view) {
            super(view);
            mProfilePic = (ImageView) view.findViewById(R.id.selected_friend_profile_pic);
        }
    }
}