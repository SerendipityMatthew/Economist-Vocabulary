package com.xuwanjin.inchoate.ui.article;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Paragraph;

import java.util.List;

public class ArticleContentAdapter extends RecyclerView.Adapter<ArticleContentAdapter.ViewHolder> {
    private Context mContext;
    private List<Paragraph> mParagraphList;
    private View mHeaderView;
    private View mFooterView;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_NORMAL = 2;

    public ArticleContentAdapter(Context context, List<Paragraph> paragraphList) {
        this.mContext = context;
        this.mParagraphList = paragraphList;
    }
    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_HEADER:
                view = mHeaderView;
                break;
            case TYPE_FOOTER:
                view = mFooterView;
                break;
            case TYPE_NORMAL:
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.article_content_item, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.paragraph.setText(mParagraphList.get(position).paragraph);
    }

    @Override
    public int getItemCount() {
        return mParagraphList == null ? 0 : mParagraphList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView paragraph;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            paragraph = itemView.findViewById(R.id.paragraph);
        }
    }
}
