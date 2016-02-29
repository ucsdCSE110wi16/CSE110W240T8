package droidsquad.voyage.model.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;

public class TripCardMembersAdapter extends RecyclerView.Adapter<TripCardMembersAdapter.ViewHolder> {
    private static final String TAG = TripCardMembersAdapter.class.getSimpleName();
    private static final int MAX_MEMBER_TO_DISPLAY = 5;

    private Context mContext;
    private List<Trip.TripMember> mMembers;

    public TripCardMembersAdapter(Context context) {
        mContext = context;
        mMembers = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_card_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trip.TripMember member = mMembers.get(position);
        Glide.with(mContext)
                .load(String.format(Constants.FB_PICTURE_URL, member.fbId, "square"))
                .asBitmap()
                .into(holder.mProfilePic);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (position > 0) {
            params.setMargins(-16, 0, 0, 0);
        }

        if (ParseUser.getCurrentUser().getObjectId().equals(member.objectId)) {
            params.gravity = Gravity.END;
        }

        holder.mProfilePic.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return mMembers.size() < MAX_MEMBER_TO_DISPLAY ? mMembers.size() : MAX_MEMBER_TO_DISPLAY;
    }

    public void updateDataset(List<Trip.TripMember> members) {
        this.mMembers = members;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public ImageView mProfilePic;

        public ViewHolder(View view) {
            super(view);
            mWrapper = (LinearLayout) view.findViewById(R.id.trip_card_member_wrapper);
            mProfilePic = (ImageView) view.findViewById(R.id.trip_card_member_profile_pic);
        }
    }
}
