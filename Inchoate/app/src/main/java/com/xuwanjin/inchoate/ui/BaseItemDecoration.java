package com.xuwanjin.inchoate.ui;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Matthew Xu
 */
public abstract class BaseItemDecoration<T extends BaseAdapter> extends RecyclerView.ItemDecoration {

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        if (isAttachedAdapter(parent)) {
            int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
            onDrawOverImpl(canvas, parent, state, (T) getAdapter(parent), position);
        }
    }

    /**
     * onDrawOver 的具体实现
     * @param canvas
     * @param parent
     * @param state
     */
    public abstract void onDrawOverImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent,
                                            @NonNull RecyclerView.State state, T adapter, int position);

    public boolean isAttachedAdapter(RecyclerView parent){
        try {
            if (Class.forName(getAdapterClassName()).getClass().isInstance(getAdapter(parent))) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取 Adapter 的具体类型
     * @return
     */
    public abstract String getAdapterClassName();

    public RecyclerView.Adapter getAdapter(RecyclerView parent) {
        return parent.getAdapter();
    }
}
