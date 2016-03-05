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
import droidsquad.voyage.model.objects.Member;
import droidsquad.voyage.model.objects.User;
import droidsquad.voyage.model.objects.VoyageUser;

public class TripMembersAdapter extends RecyclerView.Adapter<TripMembersAdapter.ViewHolder> {
    public static final String TAG = TripMembersAdapter.class.getSimpleName();

    private Activity mActivity;
    private List<Member> mMembers;
    private User admin;
    private onDeleteMemberListener mListener;

    public TripMembersAdapter(Activity activity) {
        this.mActivity = activity;
        this.mMembers = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_member, parent, false);

        if (admin != null && !admin.equals(VoyageUser.currentUser())) {
            view.findViewById(R.id.delete_button).setVisibility(View.GONE);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Member member = mMembers.get(position);

        holder.nameView.setText(member.user.getFullName());
        member.user.loadProfilePicInto(mActivity, holder.profilePictureView);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDelete(member);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public void updateMembers(List<Member> members) {
        this.mMembers = members;
        notifyDataSetChanged();
    }

    public void removeMember(Member member) {
        int pos = this.mMembers.indexOf(member);
        if (pos != -1) {
            this.mMembers.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public void setOnDeleteMemberListener(onDeleteMemberListener listener) {
        this.mListener = listener;
    }

    public interface onDeleteMemberListener {
        void onDelete(Member member);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public ImageView profilePictureView;
        public ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.name);
            profilePictureView = (ImageView) itemView.findViewById(R.id.profile_pic);
            deleteButton = (ImageView) itemView.findViewById(R.id.delete_button);
        }
    }
}
