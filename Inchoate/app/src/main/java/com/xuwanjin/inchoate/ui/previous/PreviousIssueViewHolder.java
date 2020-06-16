package com.xuwanjin.inchoate.ui.previous;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.ui.BaseViewHolder;

public class PreviousIssueViewHolder extends BaseViewHolder {
    public ImageView issueCover;
    public TextView issueDate;
    public ImageView issueDownload;
    public PreviousIssueViewHolder(@NonNull View itemView) {
        super(itemView, false);
    }

    @Override
    protected void initView() {
        issueCover = itemView.findViewById(R.id.issue_cover);
        issueDate = itemView.findViewById(R.id.issue_date);
        issueDownload = itemView.findViewById(R.id.issue_download);
    }
}
