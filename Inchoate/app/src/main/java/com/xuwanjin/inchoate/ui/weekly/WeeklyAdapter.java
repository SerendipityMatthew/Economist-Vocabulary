package com.xuwanjin.inchoate.ui.weekly;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.model.Article;

import java.util.List;

public class WeeklyAdapter extends RecyclerView.Adapter<WeeklyAdapter.ViewHolder>
        implements StickHeaderDecoration.StickHeaderInterface {
    private Context mContext;
    private List<Article> mArticleList;
    private Fragment mFragment;
    private View mHeaderView;
    private View mFooterView;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_NORMAL = 2;

    public WeeklyAdapter(List<Article> articles, Context context, Fragment fragment) {
        mContext = context;
        mArticleList = articles;
        mFragment = fragment;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public boolean isFirstItem(int position) {
        if ((mHeaderView != null && position == 1) ||
                (mHeaderView == null && position == 0)) {
            return true;
        }
        return false;
    }

    public boolean isHasHeader() {
        return mHeaderView != null;
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setFooterView(View footerView) {
        this.mFooterView = footerView;
    }

    @NonNull
    @Override
    public WeeklyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.bookmark_list, parent, false);
                break;
        }
        return new WeeklyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            if (mHeaderView != null) {
                Glide.with(mContext).load(R.mipmap.article_image).into(holder.article_image);
                holder.article_title.setText(mArticleList.get(position).headline + "  , position = " + position);
                holder.dateAndReadTime.setText(mArticleList.get(position - 1).headline);
            } else {
                Glide.with(mContext).load(R.mipmap.article_image).into(holder.article_image);
                holder.article_title.setText(mArticleList.get(position).headline + "  , position = " + position);
                holder.dateAndReadTime.setText(mArticleList.get(position).headline);
            }
        }
    }

    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            if (mHeaderView != null) {
                return TYPE_HEADER;
            } else {
                return TYPE_NORMAL;
            }
        }
        if (position == getItemCount() - 1) {
            if (mFooterView != null) {
                //最后一个,应该加载Footer
                return TYPE_FOOTER;
            } else {
                return TYPE_NORMAL;
            }
        }
        return TYPE_NORMAL;
    }


    @Override
    public int getItemCount() {
        return mArticleList == null ? 0 : mArticleList.size();
    }

    public String getGroupName(int position) {
        return mArticleList.get(position).section.getName();
    }

    // 判断当前的 position 对应的 item1 是否是相应的组的第一项
    @Override
    public boolean isItemHeader(int position) {
        // position == 0 ,是inflater 进去的 HeaderView, HeaderView 上面不能画一个 ItemHeaderView, 所以, 返回的是 false
        // 因为第一项是 HeaderView
        if (position == 1) {
            return true;
        }
        // position == 1 ,以及之后的才是, 才是 item1, item2, item3, item3
        if (position == 0) {
            return false;
        }
        String lastGroupName = mArticleList.get(position - 1).section.getName();
        String currentGroupName = mArticleList.get(position).section.getName();
        if (lastGroupName.equals(currentGroupName)) {
            return false;
        }
        return true;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView article_title;
        ImageView article_image;
        TextView dateAndReadTime;
        ImageView bookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView == mHeaderView) {
                return;
            }
            article_title = itemView.findViewById(R.id.article_title);
            article_image = itemView.findViewById(R.id.article_image);
            dateAndReadTime = itemView.findViewById(R.id.date_and_read_time);
            bookmark = itemView.findViewById(R.id.bookmark);
        }

    }
}
