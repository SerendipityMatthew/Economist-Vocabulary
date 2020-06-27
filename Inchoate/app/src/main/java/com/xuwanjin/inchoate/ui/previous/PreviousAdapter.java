package com.xuwanjin.inchoate.ui.previous;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Issue;
import com.xuwanjin.inchoate.ui.BaseAdapter;

import static com.xuwanjin.inchoate.Constants.CURRENT_DISPLAY_ISSUE_URL_ID;
import static com.xuwanjin.inchoate.Constants.INCHOATE_PREFERENCE_FILE_NAME;

public class PreviousAdapter extends BaseAdapter<PreviousIssueViewHolder, Issue> {

    public PreviousAdapter(Context context) {
        super(context, null);
    }

    @Override
    protected int getLayoutItemResId() {
        return R.layout.issue_list;
    }

    @Override
    protected PreviousIssueViewHolder getViewHolder(View view, boolean isHeaderOrFooter) {
        return new PreviousIssueViewHolder(view);
    }

    @Override
    public String getGroupName(int position) {
        return null;
    }

    @Override
    public void onBindViewHolderImpl(@NonNull PreviousIssueViewHolder holder, int position, Issue issue) {
        Glide.with(mContext)
                .load(issue.coverImageUrl)
                .placeholder(R.mipmap.the_economist_cover_placeholder)
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

    @Override
    public boolean isItemHeader(int position) {
        return false;
    }

}
