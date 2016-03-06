package droidsquad.voyage.model.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import droidsquad.voyage.model.DividerItemDecoration;
import droidsquad.voyage.model.objects.Request;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.objects.VoyageUser;
import droidsquad.voyage.model.parseModels.ParseModel;
import droidsquad.voyage.model.parseModels.ParseRequestModel;
import droidsquad.voyage.view.fragment.FeedFragment;

public class FeedCardAdapter extends RecyclerView.Adapter<FeedCardAdapter.ViewHolder> {
    private static final String TAG = FeedCardAdapter.class.getSimpleName();
    private static final String NO_FRIENDS_FORMAT = "%s is going on this trip";
    private static final String SINGLE_FRIEND_FORMAT = "%s and %s are going on this trip";
    private static final String FRIENDS_FORMAT = "%s and %d more friends are going on this trip";

    private List<Trip> mTrips;
    private FeedFragment mFragment;

    public FeedCardAdapter(FeedFragment fragment) {
        this.mFragment = fragment;
        this.mTrips = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_trip_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Trip trip = mTrips.get(position);

        trip.getAdmin().loadProfilePicInto(mFragment.getActivity(), holder.mAdminPhoto);
        holder.mTitle.setText(trip.getName());
        holder.mCities.setText(trip.getSimpleCitiesStringRepresentation());
        holder.mDates.setText(trip.getSimpleDatesStringRepresentation());
        holder.mCities.setCompoundDrawablesWithIntrinsicBounds(trip.getTransportationIconId(), 0, 0, 0);
        holder.mMembersAdapter.updateResults(trip.getMembersAsUsersExclusive());

        // Set the correct subhead text based on the number of friends going on the trip
        int membersSize = trip.getMembers().size() - 1;
        if (membersSize == 0) {
            holder.mSubhead.setText(String.format(NO_FRIENDS_FORMAT, trip.getAdmin().getFullName()));
            holder.mViewMembersButton.setVisibility(View.GONE);
            holder.mCaretIconView.setVisibility(View.GONE);
        } else if (membersSize == 1) {
            holder.mSubhead.setText(String.format(SINGLE_FRIEND_FORMAT,
                    trip.getAdmin().getFullName(), trip.getMembersAsUsers().get(0).getFullName()));
        } else {
            holder.mSubhead.setText(String.format(FRIENDS_FORMAT,
                    trip.getAdmin().getFullName(), trip.getMembers().size() - 1));
        }

        // Disable the ask to join the trip button if user has already requested
        for (Request request : trip.getRequests()) {
            if (request.user.equals(VoyageUser.currentUser())) {
                holder.mJoinButton.setText("Request sent");
                holder.mJoinButton.setEnabled(false);
            }
        }

        holder.mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseRequestModel.sendRequest(trip, new ParseModel.ParseResponseCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Request sent with success");
                        holder.mJoinButton.setText("Request sent");
                        holder.mJoinButton.setEnabled(false);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d(TAG, "Failed to send the request: " + error);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    public void updateDataset(List<Trip> trips) {
        this.mTrips = trips;
        notifyDataSetChanged();
    }

    public static void expand(final RecyclerView v) {
        v.measure(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RecyclerView.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
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
    }

    public static void collapse(final RecyclerView v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mAdminPhoto;
        public TextView mTitle;
        public TextView mSubhead;
        public TextView mCities;
        public TextView mDates;
        public TextView mJoinButton;
        public TextView mViewMembersButton;
        public RecyclerView mMembersRecyclerView;
        public FeedCardMembersAdapter mMembersAdapter;
        public ImageView mCaretIconView;

        public ViewHolder(View itemVie) {
            super(itemVie);

            mAdminPhoto = (ImageView) itemView.findViewById(R.id.admin_profile_pic);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mSubhead = (TextView) itemView.findViewById(R.id.subhead);
            mCities = (TextView) itemView.findViewById(R.id.cities);
            mDates = (TextView) itemView.findViewById(R.id.dates);
            mJoinButton = (TextView) itemView.findViewById(R.id.join_button);
            mViewMembersButton = (TextView) itemView.findViewById(R.id.view_friends_button);
            mCaretIconView = (ImageView) itemView.findViewById(R.id.caret);
            mMembersAdapter = new FeedCardMembersAdapter(mFragment.getActivity());

            mMembersRecyclerView = (RecyclerView) itemView.findViewById(R.id.members_recycler_view);
            mMembersRecyclerView.setLayoutManager(new LinearLayoutManager(mFragment.getContext()));
            mMembersRecyclerView.setAdapter(mMembersAdapter);
            mMembersRecyclerView.addItemDecoration(new DividerItemDecoration(mFragment.getContext()));

            mViewMembersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpansion();
                }
            });

            mCaretIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpansion();
                }
            });
        }

        public void toggleExpansion() {
            if (mMembersRecyclerView.getVisibility() == View.GONE) {
                mCaretIconView.animate().rotation(180).setDuration(200);
                expand(mMembersRecyclerView);
            } else {
                mCaretIconView.animate().rotation(0).setDuration(200);
                collapse(mMembersRecyclerView);
            }
        }
    }
}
