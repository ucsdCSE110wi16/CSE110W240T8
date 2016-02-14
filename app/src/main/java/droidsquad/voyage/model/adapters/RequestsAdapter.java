package droidsquad.voyage.model.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import droidsquad.voyage.model.objects.Request;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
    private ArrayList<Request> mRequests;

    public RequestsAdapter() {
        mRequests = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public void addRequest(Request request) {
        mRequests.add(request);
        notifyItemInserted(mRequests.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
