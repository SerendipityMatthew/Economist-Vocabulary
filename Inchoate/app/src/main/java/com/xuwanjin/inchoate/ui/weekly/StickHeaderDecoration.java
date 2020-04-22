package com.xuwanjin.inchoate.ui.weekly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class StickHeaderDecoration extends RecyclerView.ItemDecoration {
    public StickHeaderInterface headerInterface;
    private RecyclerView recyclerView;
    private WeeklyAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private Paint mItemHeaderPaint;
    private Paint mTextPaint;
    private int mItemHeaderHeight;
    private Context mContext;
    private Rect mTextRect;
    // 每一项的分割线
    private Paint mLinePaint;

    public interface StickHeaderInterface {
        boolean isItemHeader(int position);
    }

    public StickHeaderDecoration(RecyclerView recyclerView, Context context) {
        this.adapter = (WeeklyAdapter) recyclerView.getAdapter();
        this.headerInterface = (StickHeaderInterface) adapter;
        this.recyclerView = recyclerView;
        this.manager = recyclerView.getLayoutManager();
        this.mContext = context;

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.GRAY);

        mItemHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mItemHeaderPaint.setColor(Color.RED);
//        mItemHeaderPaint.setAlpha(100);
        mItemHeaderHeight = dip2px(mContext, 40);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(46);
        mTextPaint.setColor(Color.BLACK);

        mTextRect = new Rect();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // 给item 设置间距的,
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof WeeklyAdapter) {
            WeeklyAdapter adapter = (WeeklyAdapter) parent.getAdapter();
            int position = parent.getChildLayoutPosition(view);
            boolean isHeader = adapter.isItemHeader(position);
            boolean isFirst = adapter.isFirstItem(position);
            // 第一个 item 的上面需要绘制一个 GroupHeader, 也就是 itemHeader
            if (isHeader) {
                // 这里是分组的 item, 这里要绘制 itemHeader
                outRect.top = mItemHeaderHeight;
            } else {
                outRect.bottom = 1;
            }
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
            int position = parent.getChildLayoutPosition(view);
            boolean isHeader = adapter.isItemHeader(position);
            if (isHeader) {
                //draw left 矩形的左边位置, top 矩形的上边位置, right 矩形的右边位置, bottom 矩形的下边位置
                int y = view.getTop() - mItemHeaderHeight;
                String groupName = adapter.getGroupName(position - 1);
                canvas.drawRect(0, y, parent.getWidth(), view.getTop(), mItemHeaderPaint);
                mTextPaint.getTextBounds(groupName, 0, groupName.length(), mTextRect);
                canvas.drawText(groupName + "   , Matthew", 100,
                        (y) + mItemHeaderHeight / 2, mTextPaint);
            } else {
                // 在这里绘制每一项的分割线
                canvas.drawRect(50, view.getTop() - 1, parent.getWidth(), view.getTop(), mLinePaint);
            }
        }
    }

    // 绘制的东西会在显示的 item 的上面, 也就说绘制的东西遮住 item 的显示
    // 在这里我们绘制在手机界面上可见的 item 上面画一个 header. 因为 header 需要在 item 的上面显示
    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        if (parent.getAdapter() instanceof WeeklyAdapter) {
            WeeklyAdapter adapter = (WeeklyAdapter) parent.getAdapter();
            // 当 RecyclerView 含有 HeaderView 的时候, 第一个可见的 View, 不是里面的填充item, 而是 eaderView
            // 因此绘制第一个 Group 的 headerView 时候, 需要在大的 HeaderView 的下方
            int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
            View view = parent.findViewHolderForAdapterPosition(position).itemView;
            // 如果不是 mHeaderView 的话(也就是头部 View) ,
            // 那么就在 RecycleView 里列表的第一个可以看见的 View 的顶部画一个固定栏
            // 怎样找到第一个可见的 View, 以及在第一个可见的 View 的顶部x, y 坐标值

            boolean isHeader = adapter.isItemHeader(position);
            // position 为零表示, 这个是 HeaderView, 不需要再 HeaderView 上面画一个 itemHeader
            if (position == 0) {
                return;
            }
            Log.d("Matthew", "onDrawOver: position = " +
                    position + ", isHeader = " +
                    isHeader + ", adapter.mArticleList = " +
                    adapter.mArticleList.get(position).title
                    +
                    ", section = " + adapter.mArticleList.get(position).section
                    + ", ");

            String groupName = adapter.getGroupName(position-1);
            int y = mItemHeaderHeight / 2 + mTextRect.height() / 2;
            if (isHeader) {
                int bottom = Math.min(mItemHeaderHeight, view.getBottom());
                canvas.drawRect(0, view.getTop() - mItemHeaderHeight, parent.getWidth(), bottom, mItemHeaderPaint);
                canvas.drawText(groupName, 80,
                        y - (mItemHeaderHeight - bottom), mTextPaint);
            } else {
                // 如果把下面的注释掉, 会出现即使下一个分类小组没有滑动到顶部, 顶部的stick header 已经变成了下一个分类小组的了
                canvas.drawRect(0, 0, parent.getWidth(), mItemHeaderHeight, mItemHeaderPaint);
                canvas.drawText(groupName, 80, y, mTextPaint);
            }
        }
    }

    private int getPinnedHeaderViewPosition(int adapterFirstVisible, RecyclerView.Adapter adapter) {
        for (int index = adapterFirstVisible; index >= 0; index--) {
            if (headerInterface.isItemHeader(index)) {
                return index;
            }
        }
        return -1;
    }

    // 遵守了 measure --->  layout  --->  draw
    private void ensurePinnedHeaderViewLayout(View pinView, RecyclerView recyclerView) {
        if (pinView.isLayoutRequested()) {
            /**
             * 用的是RecyclerView的宽度测量，和RecyclerView的宽度一样
             */
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) pinView.getLayoutParams();
            if (layoutParams == null) {
                throw new NullPointerException("PinnedHeaderItemDecoration");
            }
            int widthSpec = View.MeasureSpec.makeMeasureSpec(
                    recyclerView.getMeasuredWidth() - layoutParams.leftMargin - layoutParams.rightMargin,
                    View.MeasureSpec.EXACTLY);

            int heightSpec;
            if (layoutParams.height > 0) {
                heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY);
            } else {
                heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            }
            pinView.measure(widthSpec, heightSpec);
            pinView.layout(0, 0, pinView.getMeasuredWidth(), pinView.getMeasuredHeight());
        }
    }
}
