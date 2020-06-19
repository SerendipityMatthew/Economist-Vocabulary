package com.xuwanjin.inchoate.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Matthew Xu
 */
public abstract class BaseItemDecoration<T extends BaseAdapter> extends RecyclerView.ItemDecoration {
    protected RecyclerView mRecyclerView;
    protected Context mContext;
    protected T mAdapter;

    protected BaseItemDecoration(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        this.mAdapter = (T) recyclerView.getAdapter();
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        T adapter = (T) getAdapter(parent);
        if (!isSkipDraw(position)) {
            onDrawOverImpl(canvas, parent, state, adapter, position);
        }
    }

    protected abstract boolean isSkipDraw(int position);

    /**
     * onDrawOver 的具体实现
     *
     * @param canvas
     * @param parent
     * @param state
     */
    public abstract void onDrawOverImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent,
                                        @NonNull RecyclerView.State state, T adapter, int position);


    public RecyclerView.Adapter getAdapter(RecyclerView parent) {
        return parent.getAdapter();
    }
}
