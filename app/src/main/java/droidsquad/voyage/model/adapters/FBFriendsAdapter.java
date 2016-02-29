package droidsquad.voyage.model.adapters;

import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.FacebookUser;

public class FBFriendsAdapter extends RecyclerView.Adapter<FBFriendsAdapter.ViewHolder> {
    private Activity mActivity;
    private OnClickListener mListener;
    private ArrayList<FacebookUser> mResults;
    private final boolean mClickDeleteInstead;

    private static final String TAG = FBFriendsAdapter.class.getSimpleName();

    public FBFriendsAdapter(Activity activity, boolean clickDeleteInstead) {
        mActivity = activity;
        mResults = new ArrayList<>();
        mClickDeleteInstead = clickDeleteInstead;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fb_friend_drop_down_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FacebookUser friend = mResults.get(position);

        friend.loadProfilePicInto(mActivity, holder.mProfilePicImageView);
        holder.mNameTextView.setText(friend.name);

        View clickableView;
        if (mClickDeleteInstead) {
            // Redundant casting to get rid of a currently unresolved error in android sdk
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((LinearLayout) holder.itemView).setForeground(null);
            }

            holder.mDeleteImageView.setVisibility(View.VISIBLE);
            clickableView = holder.mDeleteImageView;
        } else {
            clickableView = holder.itemView;
        }

        clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(friend);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void updateResults(ArrayList<FacebookUser> friends) {
        mResults = friends;
        notifyDataSetChanged();
    }

    public void addFriend(FacebookUser friend) {
        mResults.add(friend);
        notifyItemInserted(mResults.size() - 1);
    }

    /**
     * Removes the given friend from this adapter
     *
     * @param friend Friend to be removed
     */
    public void removeFriend(FacebookUser friend) {
        int position = mResults.indexOf(friend);
        mResults.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mResults.size());
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    public interface OnClickListener {
        void onClick(FacebookUser user);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public ImageView mProfilePicImageView;
        public ImageView mDeleteImageView;

        public ViewHolder(View view) {
            super(view);

            mNameTextView = (TextView) view.findViewById(R.id.friend_name);
            mProfilePicImageView = (ImageView) view.findViewById(R.id.friend_profile_pic);
            mDeleteImageView = (ImageView) view.findViewById(R.id.friend_delete_image_view);
        }
    }
}
