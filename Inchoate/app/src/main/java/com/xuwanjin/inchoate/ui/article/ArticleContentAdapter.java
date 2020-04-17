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

    public ArticleContentAdapter(Context context, List<Paragraph> paragraphList) {
        this.mContext = context;
        this.mParagraphList = paragraphList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.article_content_item, parent, false);
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
