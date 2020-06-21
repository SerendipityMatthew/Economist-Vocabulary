package com.xuwanjin.inchoate.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Matthew Xu
 */
public abstract class BaseItemDecoration<Adapter extends BaseAdapter> extends RecyclerView.ItemDecoration {
    protected RecyclerView mRecyclerView;
    protected Context mContext;
    protected Adapter mAdapter;

    protected BaseItemDecoration(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        this.mAdapter = (Adapter) recyclerView.getAdapter();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = parent.getChildAt(i);
            int position = parent.getChildLayoutPosition(childView);
            if (!isSkipDraw(position, false)) {
                onDrawImpl(canvas, parent, childView, position);
            }
        }
    }

    protected abstract void onDrawImpl(Canvas canvas, RecyclerView parent, View childView, int position);

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        if (!isSkipDraw(position, true)) {
            onDrawOverImpl(canvas, parent, position);
        }
    }

    /**
     * 跳过当前的绘制
     * @param position 绘制的位置
     * @param isOver 只是, 这个是onDraw 还是 onDrawOver 的绘制
     * @return 是否跳过当前的绘制
     */
    protected abstract boolean isSkipDraw(int position, boolean isOver);

    /**
     * onDrawOver 的具体实现
     *
     * @param canvas
     * @param parent
     * @param position
     */
    public abstract void onDrawOverImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent, int position);

}
