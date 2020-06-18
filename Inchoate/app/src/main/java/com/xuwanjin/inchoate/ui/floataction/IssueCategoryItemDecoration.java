package com.xuwanjin.inchoate.ui.floataction;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.xuwanjin.inchoate.ui.BaseItemDecoration;


public class IssueCategoryItemDecoration extends BaseItemDecoration<IssueCategoryAdapter> {
    private RecyclerView.LayoutManager mManager;
    private int mItemHeaderHeight;
    private Rect mTextRect;
    // 每一项的分割线
    private Paint mLinePaint;

    public IssueCategoryItemDecoration(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.mManager = recyclerView.getLayoutManager();

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

        }
    }

    /**
     * 绘制的东西会在显示的 item 的下面, 也就说被 item 遮住了
     * 在这里给每一个 item 画一个分割线, 然后在没一个分析的小组的组头, 添加一个组头, 表示分类的组别
     *
     * @param canvas
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            // view 是 RecyclerView 里的每一项, 包括填充进去的 HeaderView
            // 在这里绘制每一项的分割线
            int position = parent.getChildLayoutPosition(view);
            if (position != 0 && position != 1) {
                canvas.drawRect(50, view.getTop() - 1, parent.getWidth(), view.getTop(), mLinePaint);
            }

        }
    }

    @Override
    protected boolean isSkipDraw(int position) {
        return false;
    }

    @Override
    public void onDrawOverImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state, IssueCategoryAdapter adapter, int position) {

    }
}
