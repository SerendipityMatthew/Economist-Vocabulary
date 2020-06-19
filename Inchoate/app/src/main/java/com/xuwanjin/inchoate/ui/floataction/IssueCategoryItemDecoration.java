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


/**
 * @author Matthew Xu
 */
public class IssueCategoryItemDecoration extends BaseItemDecoration<IssueCategoryAdapter> {
    private RecyclerView.LayoutManager mManager;
    // 每一项的分割线
    private Paint mLinePaint;

    public IssueCategoryItemDecoration(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.mManager = recyclerView.getLayoutManager();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.GRAY);
    }


    // 给item 设置间距的,
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    /**
     * 绘制的东西会在显示的 item 的下面, 也就说被 item 遮住了
     * 在这里给每一个 item 画一个分割线, 然后在没一个分析的小组的组头, 添加一个组头, 表示分类的组别
     *
     * @param canvas
     * @param parent
     * @param childView
     * @param position
     */
    @Override
    public void onDrawImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent, View childView, int position) {
        canvas.drawRect(50, childView.getTop() - 1, parent.getWidth(), childView.
                getTop(), mLinePaint);
    }

    @Override
    protected boolean isSkipDraw(int position, boolean isOver) {
        if (position != 0 && position != 1) {
            return false;
        }
        return true;
    }

    @Override
    public void onDrawOverImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent, int position) {

    }
}
