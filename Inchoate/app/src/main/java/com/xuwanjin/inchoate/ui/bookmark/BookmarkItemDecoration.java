package com.xuwanjin.inchoate.ui.bookmark;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.ui.BaseAdapter;
import com.xuwanjin.inchoate.ui.BaseItemDecoration;

/**
 * @author Matthew Xu
 */
public class BookmarkItemDecoration extends BaseItemDecoration<BookmarkAdapter> {
    private RecyclerView mRecyclerView;
    private BookmarkAdapter mAdapter;
    private RecyclerView.LayoutManager mManager;
    private int mItemHeaderHeight = -1;
    private Context mContext;
    private Paint mItemHeaderPaint;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private Rect mTextRect;

    public BookmarkItemDecoration(RecyclerView recyclerView, Context context) {
        this.mAdapter = (BookmarkAdapter) recyclerView.getAdapter();
        this.mRecyclerView = recyclerView;
        this.mManager = recyclerView.getLayoutManager();
        this.mContext = context;
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.GRAY);

        mItemHeaderHeight = dip2px(mContext, 40);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(46);
        mTextPaint.setColor(Color.BLACK);

        mItemHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mItemHeaderPaint.setColor(Color.RED);

        mTextRect = new Rect();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //Item header 按照 section, issueDate, bookmarkDate, Topic,
    // 给item 设置间距的,
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof BookmarkAdapter) {
            BookmarkAdapter adapter = (BookmarkAdapter) parent.getAdapter();
            int position = parent.getChildLayoutPosition(view);
            boolean isHeader = adapter.isItemHeader(position);
            // 第一个 item 的上面需要绘制一个 GroupHeader, 也就是 itemHeader
            if (isHeader) {
                // 这里是分组的 item, 这里要绘制 itemHeader
                outRect.top = mItemHeaderHeight;
            } else {
                outRect.bottom = 1;
            }
        }
    }

    @Override
    public void onDrawOverImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state, BookmarkAdapter adapter, int position) {
        // 当 RecyclerView 含有 HeaderView 的时候, 第一个可见的 View, 不是里面的填充item, 而是 HeaderView
        // 因此绘制第一个 Group 的 headerView 时候, 需要在大的 HeaderView 的下方
        View view = parent.findViewHolderForAdapterPosition(position).itemView;
        // 如果不是 mHeaderView 的话(也就是头部 View) ,
        // 那么就在 RecycleView 里列表的第一个可以看见的 View 的顶部画一个固定栏
        // 怎样找到第一个可见的 View, 以及在第一个可见的 View 的顶部x, y 坐标值

        boolean isHeader = adapter.isItemHeader(position);
        // position 为零表示, 这个是 HeaderView, 不需要再 HeaderView 上面画一个 itemHeader

        String groupName = adapter.getGroupName(position);
        int y = mItemHeaderHeight / 2 + mTextRect.height() / 2;
        if (isHeader) {
            int bottom = Math.min(mItemHeaderHeight, view.getBottom());
            canvas.drawRect(0, view.getTop() - mItemHeaderHeight, parent.getWidth(), bottom, mItemHeaderPaint);
            canvas.drawText(groupName, 50,
                    y - (mItemHeaderHeight - bottom), mTextPaint);
        } else {
            // 如果把下面的注释掉, 会出现即使下一个分类小组没有滑动到顶部, 顶部的 stick header 会消失
            canvas.drawRect(0, 0, parent.getWidth(), mItemHeaderHeight, mItemHeaderPaint);
            canvas.drawText(groupName, 50, y, mTextPaint);
        }
    }

    @Override
    public String getAdapterClassName() {
        return BookmarkAdapter.class.getName();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            // view 是 RecyclerView 里的每一项, 包括填充进去的 HeaderView
            int position = parent.getChildLayoutPosition(view);
            boolean isHeader = mAdapter.isItemHeader(position);
            if (isHeader) {
                //draw left 矩形的左边位置, top 矩形的上边位置, right 矩形的右边位置, bottom 矩形的下边位置
                int y = view.getTop() - mItemHeaderHeight;
                String groupName = mAdapter.getGroupName(position);
                canvas.drawRect(0, y, parent.getWidth(), view.getTop(), mItemHeaderPaint);
                mTextPaint.getTextBounds(groupName, 0, groupName.length(), mTextRect);
                canvas.drawText(groupName, 50,
                        (y) + mItemHeaderHeight / 2 + 15, mTextPaint);
            } else {
                // 在这里绘制每一项的分割线
                canvas.drawRect(50, view.getTop() - 1, parent.getWidth(), view.getTop(), mLinePaint);
            }
        }
    }

    @Override
    protected boolean isSkipDraw(int position, BaseAdapter adapter) {
        if (position == adapter.getItemCount() - 1) {
            return true;
        }
        return false;
    }
}
