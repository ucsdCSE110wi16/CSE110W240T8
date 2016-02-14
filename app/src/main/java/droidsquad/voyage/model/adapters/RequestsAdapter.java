package droidsquad.voyage.model.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import droidsquad.voyage.R;
import droidsquad.voyage.model.objects.Request;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
    private static final String TAG = RequestsAdapter.class.getSimpleName();
    private Context context;
    private List<Request> mRequests;
    private OnButtonClickedCallback mCallback;
    private OnDataEmptyListener mEmptyListener;

    public RequestsAdapter(Context context) {
        mRequests = new ArrayList<>();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Request request = mRequests.get(position);

        Picasso.with(context)
                .load(request.hostPicURL)
                .placeholder(R.drawable.ic_account_circle_gray)
                .into(holder.imageView);

        holder.hostNameView.setText(request.hostName);
        holder.invitationMsgView.setText(context.getString(R.string.request_message_template,
                request.tripName));
        holder.elapsedTimeView.setText("");
        holder.declineButton.setSupportBackgroundTintList(ContextCompat.getColorStateList(context, R.color.cardview_light_background));

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onAcceptClicked(request);
                }
            }
        });

        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onDeclineClicked(request);
                }
            }
        });
    }

    public void setOnButtonClickedCallback(OnButtonClickedCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public void updateAdapter(List<Request> requests) {
        mRequests = requests;
        notifyDataSetChanged();

        if (mEmptyListener != null && mRequests.size() == 0) {
            mEmptyListener.onEmpty();
        }
    }

    public void removeRequest(Request request) {
        int position = mRequests.indexOf(request);
        mRequests.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mRequests.size());

        if (mEmptyListener != null && mRequests.size() == 0) {
            mEmptyListener.onEmpty();
        }
    }

    public void setOnDataEmptyListener(OnDataEmptyListener listener) {
        this.mEmptyListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imageView;
        public TextView hostNameView;
        public TextView invitationMsgView;
        public TextView elapsedTimeView;
        public Button acceptButton;
        public AppCompatButton declineButton;

        public ViewHolder(View view) {
            super(view);

            imageView = (CircleImageView) view.findViewById(R.id.profile_pic);
            hostNameView = (TextView) view.findViewById(R.id.user_name);
            invitationMsgView = (TextView) view.findViewById(R.id.trip_name);
            elapsedTimeView = (TextView) view.findViewById(R.id.elapsed_time);
            acceptButton = (Button) view.findViewById(R.id.accept_button);
            declineButton = (AppCompatButton) view.findViewById(R.id.decline_button);
        }
    }

    public interface OnButtonClickedCallback {
        void onAcceptClicked(Request request);
        void onDeclineClicked(Request request);
    }

    public interface OnDataEmptyListener {
        void onEmpty();
    }
}
