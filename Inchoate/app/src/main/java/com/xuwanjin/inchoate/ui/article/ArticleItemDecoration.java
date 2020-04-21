package com.xuwanjin.inchoate.ui.article;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleItemDecoration extends RecyclerView.ItemDecoration {

    public RecyclerView mRecyclerView;
    public Context mContext;
    public ArticleItemDecoration(RecyclerView recyclerView, Context context){
        this.mRecyclerView= recyclerView;
        this.mContext = context;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof ArticleContentAdapter) {
            outRect.top = 50;
        }
    }
}
