package com.xuwanjin.inchoate.ui.weekly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class StickHeaderDecoration extends RecyclerView.ItemDecoration {
    public StickHeaderInterface headerInterface;
    private RecyclerView recyclerView;
    private WeeklyAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private Rect mPinnedHeaderRect = null;
    private int mPinnedHeaderPosition = -1;
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
        mItemHeaderPaint.setAlpha(100);
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
            if (isHeader) {
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
        //每一项的后面画一个分割线
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            // view 是 RecyclerView 里的每一项, 包括填充进去的 HeaderView
//            Log.d("Matthew", "onDraw: view = " + view + " mHeaderView = " + adapter.getHeaderView());
            int position = parent.getChildLayoutPosition(view);

            boolean isHeader = adapter.isItemHeader(position);
            if (isHeader) {
                //draw left 矩形的左边位置, top 矩形的上边位置, right 矩形的右边位置, bottom 矩形的下边位置
                canvas.drawRect(0, view.getTop() - mItemHeaderHeight, parent.getWidth(), view.getTop(), mItemHeaderPaint);
                mTextPaint.getTextBounds(adapter.getGroupName(position), 0, adapter.getGroupName(position).length(), mTextRect);
                canvas.drawText(adapter.getGroupName(position) + "   , Matthew", 0,
                        (view.getTop() - mItemHeaderHeight) + mItemHeaderHeight / 2, mTextPaint);

            } else {
                // 添加这段代码会和 onDrawOver 方法里的内容形成重复, item 项设置透明的就可以观察出来
//                canvas.drawRect(0, 0, parent.getWidth(), mItemHeaderHeight, mItemHeaderPaint);
                // text 参数, setText 的文本内容, 也就是要显示的文字
                //  x
                //  y
                //  paint 用来画画的画笔工具
//                canvas.drawText(adapter.getGroupName(position)+" , hello", 0, mItemHeaderHeight / 2 + mTextRect.height() / 2, mTextPaint);
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
            // 因此绘制第一个 Group 的 headerView 时候, 需要在大的 eaderView 的下方
            int position = ((GridLayoutManager) Objects.requireNonNull
                    (parent.getLayoutManager())).findFirstVisibleItemPosition();
            View view = Objects.requireNonNull(parent.findViewHolderForAdapterPosition(position)).itemView;
            // 如果不是 mHeaderView 的话(也就是头部 View) ,
            // 那么就在 RecycleView 里列表的第一个可以看见的 View 的顶部画一个固定栏
            // 怎样找到第一个可见的 View, 以及在第一个可见的 View 的顶部x, y 坐标值
            boolean isHeader = adapter.isItemHeader(position + 1);
//            Log.d("Matthew", "onDrawOver: isHeader = " + isHeader);
//            Log.d("Matthew", "onDrawOver: position = " + position);
            if (isHeader) {
                int bottom = Math.min(mItemHeaderHeight, view.getBottom());
                canvas.drawRect(0, view.getTop() - mItemHeaderHeight, parent.getWidth(), bottom, mItemHeaderPaint);
                canvas.drawText(adapter.getGroupName(position), 0,
                        mItemHeaderHeight / 2 + mTextRect.height() / 2 - (mItemHeaderHeight - bottom), mTextPaint);
            } else {
                // 如果把下面的注释掉, 会出现即使下一个分类小组没有滑动到顶部, 顶部的stick header 已经变成了下一个分类小组的了
                canvas.drawRect(0, 0, parent.getWidth(), mItemHeaderHeight, mItemHeaderPaint);
                canvas.drawText(adapter.getGroupName(position), 0, mItemHeaderHeight / 2 + mTextRect.height() / 2, mTextPaint);
            }
        }

        // 这个下面是之前仿制别人的做法, 绘制 StickHeader 的,
/*        //确保是 PinnedHeaderAdapter的adapter, 确保有 View
        if (parent.getChildCount() > 0) {
            //找到要固定的 pin view
            View firstView = parent.getChildAt(0);
            int firstAdapterPosition = parent.getChildAdapterPosition(firstView);
//            Log.d("Matthew", "onDrawOver: firstAdapterPosition = " + firstAdapterPosition);
            int pinnedHeaderPosition = getPinnedHeaderViewPosition(firstAdapterPosition, adapter);
            // pinnedHeaderPosition 是需要被放在 header 位置的 View 的在列表里的索引
            if (pinnedHeaderPosition != -1) {
                // onCreateViewHolder 返回的是 Adapter 里的 ViewHolder
                WeeklyAdapter.ViewHolder headerViewHolder = adapter.onCreateViewHolder(parent,
                        adapter.getItemViewType(pinnedHeaderPosition));

                adapter.onBindViewHolder(headerViewHolder, pinnedHeaderPosition);
                //要固定的view
                // itemView 是 Adapter 的布局文件的 View. 也就是 RecyclerView 需要填充的 item
                View pinnedHeaderView = headerViewHolder.itemView;
                ensurePinnedHeaderViewLayout(pinnedHeaderView, parent);
                int sectionPinOffset = 0;
//                Log.d("Matthew", "onDrawOver: parent.getChildCount() = " + parent.getChildCount());
                // 这一段似乎没什么作用
                for (int index = 0; index < parent.getChildCount(); index++) {
                    if (headerInterface.isStick(parent.getChildAdapterPosition(parent.getChildAt(index)))) {
                        View sectionView = parent.getChildAt(index);
                        int sectionTop = sectionView.getTop();
                        int pinViewHeight = pinnedHeaderView.getHeight();
                        if (sectionTop < pinViewHeight && sectionTop > 0) {
                            sectionPinOffset = sectionTop - pinViewHeight;
                        }
                    }
                }
                int saveCount = canvas.save();
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) pinnedHeaderView.getLayoutParams();
                if (layoutParams == null) {
                    throw new NullPointerException("Stick Header Decoration Exception");
                }
                canvas.translate(layoutParams.leftMargin, sectionPinOffset);
                canvas.clipRect(0, 0, parent.getWidth(), pinnedHeaderView.getMeasuredHeight());
                pinnedHeaderView.draw(canvas);
                canvas.restoreToCount(saveCount);
            } else {
//                mPinnedHeaderRect = null;
            }
        }*/
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
