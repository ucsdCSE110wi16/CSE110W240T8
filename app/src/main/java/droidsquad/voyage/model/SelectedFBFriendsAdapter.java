package droidsquad.voyage.model;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import droidsquad.voyage.R;

public class SelectedFBFriendsAdapter extends RecyclerView.Adapter<SelectedFBFriendsAdapter.ViewHolder> {
    public ArrayList<FacebookUser> mSelectedUsers;
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
        FacebookUser friend = mSelectedUsers.get(position);

        Picasso.with(mActivity)
                .load(friend.pictureURL)
                .placeholder(R.drawable.ic_account_circle_gray)
                .into(holder.mProfilePic);

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

    public void addFriend(FacebookUser friend) {
        mSelectedUsers.add(friend);
        notifyItemInserted(mSelectedUsers.size() - 1);
    }

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
