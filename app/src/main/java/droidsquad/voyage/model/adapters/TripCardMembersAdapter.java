package droidsquad.voyage.model.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.User;

public class TripCardMembersAdapter extends RecyclerView.Adapter<TripCardMembersAdapter.ViewHolder> {
    private static final String TAG = TripCardMembersAdapter.class.getSimpleName();
    private static final int MAX_MEMBER_TO_DISPLAY = 5;
    private static final int NEGATIVE_LEFT_MARGIN = -16;
    private static float marginLeft;

    private Context mContext;
    private TextView mExcessView;
    private List<User> mMembers;

    public TripCardMembersAdapter(Context context, TextView excessView) {
        mContext = context;
        mExcessView = excessView;
        mMembers = new ArrayList<>();

        marginLeft = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, NEGATIVE_LEFT_MARGIN, mContext.getResources().getDisplayMetrics());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_card_member, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User member = mMembers.get(position);
        member.loadProfilePicInto(mContext, holder.mProfilePic);

        // Set a negative margin for overlapping effect on the pictures
        if (position > 0) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mProfilePic.getLayoutParams();
            params.setMargins((int) marginLeft, 0, 0, 0);
            holder.mProfilePic.setLayoutParams(params);
        }

        if (mMembers.size() > MAX_MEMBER_TO_DISPLAY) {
            mExcessView.setText(String.format("... (+%s)", mMembers.size() - MAX_MEMBER_TO_DISPLAY));
            mExcessView.setVisibility(View.VISIBLE);
        } else {
            mExcessView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMembers.size() < MAX_MEMBER_TO_DISPLAY ? mMembers.size() : MAX_MEMBER_TO_DISPLAY;
    }

    public void updateDataset(List<User> members) {
        mMembers = members;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mProfilePic;

        public ViewHolder(View view) {
            super(view);
            mProfilePic = (ImageView) view.findViewById(R.id.trip_card_member_profile_pic);
        }
    }
}
