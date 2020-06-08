package com.xuwanjin.inchoate.ui.floataction;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;


public class IssueCategoryStickHeaderDecoration extends RecyclerView.ItemDecoration {
    private RecyclerView mRecyclerView;
    private IssueCategoryAdapter mCategoryAdapter;
    private RecyclerView.LayoutManager mManager;

    private int mItemHeaderHeight;
    private Context mContext;
    private Rect mTextRect;
    // 每一项的分割线
    private Paint mLinePaint;

    public IssueCategoryStickHeaderDecoration(RecyclerView recyclerView, Context context) {
        this.mCategoryAdapter = (IssueCategoryAdapter) recyclerView.getAdapter();
        this.mRecyclerView = recyclerView;
        this.mManager = recyclerView.getLayoutManager();
        this.mContext = context;

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.GRAY);

        mTextRect = new Rect();
    }


    // 给item 设置间距的,
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof IssueCategoryAdapter) {
//            outRect.bottom = 50;
//            outRect.top = 10;
        }
    }

    // 绘制的东西会在显示的 item 的下面, 也就说被 item 遮住了
    // 在这里给每一个 item 画一个分割线, 然后在没一个分析的小组的组头, 添加一个组头, 表示分类的组别
    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            // view 是 RecyclerView 里的每一项, 包括填充进去的 HeaderView
            // 在这里绘制每一项的分割线
            int position = parent.getChildLayoutPosition(view);
            if (position != 0 && position != 1){
                canvas.drawRect(50, view.getTop() - 1, parent.getWidth(), view.getTop(), mLinePaint);
            }

        }
    }

    // 绘制的东西会在显示的 item 的上面, 也就说绘制的东西遮住 item 的显示
    // 在这里我们绘制在手机界面上可见的 item 上面画一个 header. 因为 header 需要在 item 的上面显示
    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
    }
}
