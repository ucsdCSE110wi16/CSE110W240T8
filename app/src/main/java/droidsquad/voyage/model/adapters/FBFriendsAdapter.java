package droidsquad.voyage.model.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.view.activity.AddFriendsActivity;

public class FBFriendsAdapter extends RecyclerView.Adapter<FBFriendsAdapter.ViewHolder> {
    private static final String TAG = FBFriendsAdapter.class.getSimpleName();

    private AddFriendsActivity mActivity;
    private OnFriendSelected mListener;
    private List<User> mFriends;
    private int currentHeight;

    public FBFriendsAdapter(AddFriendsActivity activity) {
        mActivity = activity;
        mFriends = new ArrayList<>();
        currentHeight = 1;
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
        animateRecyclerView();
    }

    public void addFriend(User friend) {
        mFriends.add(friend);
    }

    /**
     * Removes the given friend from this adapter
     *
     * @param friend Friend to be removed
     */
    public boolean removeFriend(User friend) {
        if (mFriends.remove(friend)) {
            notifyDataSetChanged();
            animateRecyclerView();
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

    private void animateRecyclerView() {

        final RecyclerView v = mActivity.getResultsRecyclerView();
        v.measure(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        final int initialHeight = currentHeight;
        final int targetHeight = (v.getMeasuredHeight() == 0) ? 1 : v.getMeasuredHeight();
        final int difference = targetHeight - currentHeight;

        v.getLayoutParams().height = currentHeight;

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? (initialHeight + difference)
                        : (int) (initialHeight + (difference * interpolatedTime));
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        currentHeight = targetHeight;
    }
}