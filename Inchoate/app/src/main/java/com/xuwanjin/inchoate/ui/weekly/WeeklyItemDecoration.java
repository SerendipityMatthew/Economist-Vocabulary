package com.xuwanjin.inchoate.ui.weekly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.ui.BaseItemDecoration;


/**
 * @author Matthew Xu
 */
public class WeeklyItemDecoration extends BaseItemDecoration<WeeklyAdapter> {
    public static final String TAG = "WeeklyItemDecoration";
    private RecyclerView.LayoutManager mManager;
    private Paint mItemHeaderPaint;
    private Paint mTextPaint;
    private int mItemHeaderHeight;
    private Rect mTextRect;
    // 每一项的分割线
    private Paint mLinePaint;


    public WeeklyItemDecoration(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.mManager = mRecyclerView.getLayoutManager();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.GRAY);

        mItemHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mItemHeaderPaint.setColor(Color.RED);
        mItemHeaderHeight = dip2px(mContext, 40);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(46);
        mTextPaint.setColor(Color.BLACK);

        mTextRect = new Rect();
    }

    // 给item 设置间距的,
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildLayoutPosition(view);
        boolean isHeader = mAdapter.isItemHeader(position);
        // 第一个 item 的上面需要绘制一个 GroupHeader, 也就是 itemHeader
        if (isHeader) {
            // 这里是分组的 item, 这里要绘制 itemHeader
            outRect.top = mItemHeaderHeight;
        } else {
            outRect.bottom = 1;
        }
    }

    // 绘制的东西会在显示的 item 的下面, 也就说被 item 遮住了
    // 在这里给每一个 item 画一个分割线, 然后在没一个分析的小组的组头, 添加一个组头, 表示分类的组别
    @Override
    public void onDrawImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent, View childView, int position) {
        boolean isHeader = mAdapter.isItemHeader(position);
        if (isHeader) {
            //draw left 矩形的左边位置, top 矩形的上边位置, right 矩形的右边位置, bottom 矩形的下边位置
            int y = childView.getTop() - mItemHeaderHeight;
            String groupName = mAdapter.getGroupName(position);
            canvas.drawRect(0, y, parent.getWidth(), childView.getTop(), mItemHeaderPaint);
            mTextPaint.getTextBounds(groupName, 0, groupName.length(), mTextRect);
            canvas.drawText(groupName, 50,
                    (y) + mItemHeaderHeight / 2 + 15, mTextPaint);
        } else {
            // 在这里绘制每一项的分割线
            canvas.drawRect(50, childView.getTop() - 1, parent.getWidth(), childView.getTop(), mLinePaint);
        }
    }

    @Override
    protected boolean isSkipDraw(int position, boolean isOver) {
        if (position == 0) {
            return true;
        }
        if (position == mAdapter.getItemCount() - 1) {
            return true;
        }
        return false;
    }

    /**
     * 绘制的东西会在显示的 item 的上面, 也就说绘制的东西遮住 item 的显示
     * 在这里我们绘制在手机界面上可见的 item 上面画一个 header. 因为 header 需要在 item 的上面显示
     */
    @Override
    public void onDrawOverImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent, int position) {

        // 当 RecyclerView 含有 HeaderView 的时候, 第一个可见的 View, 不是里面的填充item, 而是 eaderView
        // 因此绘制第一个 Group 的 headerView 时候, 需要在大的 HeaderView 的下方
        if (parent.findViewHolderForAdapterPosition(position) == null) {
            return;
        }
        boolean isHeader = mAdapter.isItemHeader(position);
        // position 为零表示, 这个是 HeaderView, 不需要再 HeaderView 上面画一个 itemHeader


        View view = parent.findViewHolderForAdapterPosition(position).itemView;
        /*
            如果不是 mHeaderView 的话(也就是头部 View) ,
            那么就在 RecycleView 里列表的第一个可以看见的 View 的顶部画一个固定栏
            怎样找到第一个可见的 View, 以及在第一个可见的 View 的顶部x, y 坐标值
         */

        String groupName = mAdapter.getGroupName(position);
        int y = mItemHeaderHeight / 2 + mTextRect.height() / 2;
        canvas.drawRect(0, 0, parent.getWidth(), mItemHeaderHeight, mItemHeaderPaint);
        canvas.drawText(groupName, 50, y, mTextPaint);

    }

}
