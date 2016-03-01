package droidsquad.voyage.model.adapters;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.User;

public class SelectedFBFriendsAdapter extends RecyclerView.Adapter<SelectedFBFriendsAdapter.ViewHolder> {
    public ArrayList<User> mSelectedUsers;
    private Activity mActivity;
    private OnItemRemovedListener mListener;

    public SelectedFBFriendsAdapter(Activity activity) {
        mSelectedUsers = new ArrayList<>();
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
        User friend = mSelectedUsers.get(position);
        friend.loadProfilePicInto(mActivity, holder.mProfilePic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriend(position);
                if (mListener != null) {
                    mListener.onRemoved();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSelectedUsers.size();
    }

    /**
     * Add a friend to this adapter
     *
     * @param friend Friend to be added
     */
    public void addFriend(User friend) {
        mSelectedUsers.add(friend);
        notifyItemInserted(mSelectedUsers.size() - 1);
    }

    /**
     * Removes the friend on the given position from the adapter
     *
     * @param position Position of friend to be removed
     */
    private void removeFriend(int position) {
        mSelectedUsers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSelectedUsers.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mProfilePic;

        public ViewHolder(View view) {
            super(view);
            mProfilePic = (ImageView) view.findViewById(R.id.selected_friend_profile_pic);
        }
    }

    public interface OnItemRemovedListener {
        void onRemoved();
    }

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        mListener = listener;
    }
}
