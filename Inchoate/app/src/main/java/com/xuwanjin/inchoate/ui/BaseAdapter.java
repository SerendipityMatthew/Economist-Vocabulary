package com.xuwanjin.inchoate.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * @author Matthew Xu
 */
public abstract class BaseAdapter<T extends BaseViewHolder, E> extends RecyclerView.Adapter<T> {
    protected Context mContext;
    protected List<E> mDataList;
    protected View mHeaderView;
    protected View mFooterView;
    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_FOOTER = 1;
    protected static final int TYPE_NORMAL = 2;

    protected BaseAdapter(Context context, List<E> dataList) {
        mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_FOOTER:
                view = mFooterView;
                break;
            case TYPE_HEADER:
                view = mHeaderView;
                break;
            default:
                view = LayoutInflater.from(mContext).inflate(getLayoutItemResId(), parent, false);
        }
        if (view == mFooterView || view == mHeaderView) {
            return getViewHolder(view, true);
        }
        return getViewHolder(view, false);
    }

    /**
     * 提供给 Adapter 的布局 id,
     * @return 返回 资源的 id
     */
    protected abstract int getLayoutItemResId();

    /**
     *  抽象每一个 Adapter 的 ViewHolder, 让子类提供 ViewHolder
     * @param view Adapter 的布局文件生成的 view
     * @param isHeaderOrFooter 是否是 HeaderView 还是 FooterView
     * @return 返回相应 Adapter 需要的 ViewHolder
     */
    protected abstract T getViewHolder(View view, boolean isHeaderOrFooter);

    /**
     *
     * @param position
     * @return
     */
    public abstract String getGroupName(int position);

    /**
     *
     * @param holder
     * @param position
     */
    protected abstract void onBindViewHolderImpl(T holder, int position);

    /**
     *
     * @param position
     * @return
     */
    public abstract boolean isItemHeader(int position);

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        if (isBindViewItem(position)){
            onBindViewHolderImpl(holder, position);
        }
    }

    /**
     * 绘制 item 的条件
     * @param position
     * @return
     */
    protected boolean isBindViewItem(int position){
        return true;
    }


    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
    }

    public void setFooterView(View footerView) {
        this.mFooterView = footerView;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView != null && mFooterView != null) {
            return mDataList.size() + 2;
        }
        if ((mHeaderView == null && mFooterView != null) ||
                (mHeaderView != null && mFooterView == null)) {
            return mDataList.size() + 1;
        }
        if ((mHeaderView == null && mFooterView == null)) {
            return mDataList.size();
        }
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) {
            return TYPE_NORMAL;
        }
        // position 为零, 同时 mHeaderView 不为空, 那么第一个应该是 TYPE_HEADER
        if (position == 0) {
            if (mHeaderView != null) {
                return TYPE_HEADER;
            }
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

    public void updateData(List<E> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }
    public List<E> getDataList() {
        return mDataList;
    }
}
