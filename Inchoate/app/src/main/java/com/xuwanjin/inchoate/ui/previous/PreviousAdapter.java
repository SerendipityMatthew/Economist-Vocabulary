package com.xuwanjin.inchoate.ui.previous;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;
import com.xuwanjin.inchoate.model.Issue;

import java.util.List;

import static com.xuwanjin.inchoate.Constants.CURRENT_DISPLAY_ISSUE_URL_ID;
import static com.xuwanjin.inchoate.Constants.INCHOATE_PREFERENCE_FILE_NAME;

public class PreviousAdapter extends RecyclerView.Adapter<PreviousAdapter.ViewHolder> {
    private Context mContext;
    private List<Issue> mIssueList;

    public PreviousAdapter(Context context) {
        this.mContext = context;
    }

    public void updateData(List<Issue> issueList) {
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
        Issue issue = mIssueList.get(position);
        Glide.with(mContext)
                .load(issue.coverImageUrl)
                .placeholder(R.mipmap.the_economist)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.issueCover);
        holder.issueDate.setText(issue.issueDate);
        holder.issueCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =
                        mContext.getSharedPreferences(INCHOATE_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
                String value = issue.issueFormatDate + ',' + issue.urlID;
                preferences.edit().putString(CURRENT_DISPLAY_ISSUE_URL_ID, value).apply();
                navigationToFragment(R.id.navigation_weekly);
            }
        });
    }


    private void navigationToFragment(int resId) {
        Utils.navigationController(InchoateApp.NAVIGATION_CONTROLLER, resId);
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
