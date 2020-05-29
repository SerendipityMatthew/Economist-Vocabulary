package com.xuwanjin.inchoate.ui.previous;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Issue;

import java.util.List;

public class PreviousAdapter extends RecyclerView.Adapter<PreviousAdapter.ViewHolder> {
    private Context mContext;
    private List<Issue> mIssueList;

    public PreviousAdapter(Context context) {
        this.mContext = context;
    }

    public void updateData(List<Issue> issueList){
        mIssueList = issueList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PreviousAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.issue_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mIssueList.get(position).coverImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.issueCover);
        holder.issueDate.setText(mIssueList.get(position).issueDate);
    }

    @Override
    public int getItemCount() {
        return mIssueList == null ? 0 : mIssueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView issueCover;
        public TextView issueDate;
        public ImageView issueDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            issueCover = itemView.findViewById(R.id.issue_cover);
            issueDate = itemView.findViewById(R.id.issue_date);
            issueDownload = itemView.findViewById(R.id.issue_download);
        }
    }
}
