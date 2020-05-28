package com.xuwanjin.inchoate.ui.floataction;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.R;
import com.xuwanjin.inchoate.Utils;

import java.util.List;

public class IssueCategoryAdapter extends RecyclerView.Adapter<IssueCategoryAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mSectionList;
    private View mHeaderView;
    private View mFooterView;
    public static final String TAG = "IssueCategoryAdapter";
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_NORMAL = 2;

    public IssueCategoryAdapter(Context context, List<String> sectionList) {
        this.mSectionList = sectionList;
        this.mContext = context;
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case TYPE_HEADER:
                itemView = mHeaderView;
                break;
            case TYPE_FOOTER:
                itemView = mFooterView;
                break;
            case TYPE_NORMAL:
            default:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.issue_category_item, parent, false);
                break;
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_NORMAL) {
            int revisedPosition = position;
            if (mHeaderView != null){
                revisedPosition = position-1;
            }
            holder.categoryMenu.setText(mSectionList.get(revisedPosition));
            holder.categoryMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InchoateApp.setScrollToPosition(position);
                    Log.d(TAG, "onClick:  position = " + position);
                    Utils.navigationController(InchoateApp.NAVIGATION_CONTROLLER, R.id.navigation_weekly);
                }
            });
        }
    }


    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) {
            return TYPE_NORMAL;
        }
        // position 为零, 同时 mHeaderView 不为空, 那么第一个应该是 TYPE_HEADER
        if (position == 0) {
            if (mHeaderView != null) {
                return TYPE_HEADER;
            }
            return TYPE_NORMAL;
        }
        // 最后一个
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
        if (mHeaderView != null && mFooterView == null) {
            return mSectionList.size() + 1;
        }
        if (mHeaderView == null && mFooterView != null) {
            return mSectionList == null ? 0 : mSectionList.size() + 1;
        }
        if (mHeaderView == null && mFooterView == null) {
            return mSectionList == null ? 0 : mSectionList.size();
        }
        if (mHeaderView != null && mFooterView != null) {
            return mSectionList == null ? 0 : mSectionList.size() + 2;
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryMenu = itemView.findViewById(R.id.issue_category_menu);

        }
    }
}
