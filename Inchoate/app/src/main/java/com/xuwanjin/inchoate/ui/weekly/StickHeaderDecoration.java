package com.xuwanjin.inchoate.ui.weekly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
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
        boolean isStick(int position);
    }

    public StickHeaderDecoration(RecyclerView recyclerView, Context context) {
        this.adapter = (WeeklyAdapter) recyclerView.getAdapter();
        this.headerInterface = (StickHeaderInterface) adapter;
        this.recyclerView = recyclerView;
        this.manager = recyclerView.getLayoutManager();
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.GRAY);
        mItemHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mItemHeaderPaint.setColor(Color.RED);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(46);
        mTextPaint.setColor(Color.BLACK);
        this.mContext = context;
        mItemHeaderHeight = dip2px(mContext, 40);
        mTextRect = new Rect();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // 给item 设置间距的,
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof WeeklyAdapter) {
            WeeklyAdapter adapter = (WeeklyAdapter) parent.getAdapter();
            int position = parent.getChildLayoutPosition(view);
            boolean isHeader = adapter.isStick(position);
            if (isHeader) {
                outRect.top = mItemHeaderHeight;
            } else {
                outRect.bottom = 1;
            }
        }
    }

    // 绘制的东西会在显示的 item 的下面, 也就说被 item 遮住了
    // 在这里给每一个 item 画一个分割线, 然后再加一个头部的组别
    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        int count = parent.getChildCount();
        //每一项的后面画一个分割线
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildLayoutPosition(view);
            boolean isHeader = adapter.isStick(position);
            boolean isFirstItem = adapter.isFirstItem(position);
            if (isFirstItem){
                canvas.drawRect(0, view.getTop()-mItemHeaderHeight, parent.getHeight(), view.getTop(), mItemHeaderPaint);
            }
            if (isHeader) {
                canvas.drawRect(0, view.getTop() - mItemHeaderHeight, parent.getWidth(), view.getTop(), mItemHeaderPaint);
                mTextPaint.getTextBounds(adapter.getGroupName(position), 0, adapter.getGroupName(position).length(), mTextRect);
                canvas.drawText(adapter.getGroupName(position), 0, (view.getTop() - mItemHeaderHeight) + mItemHeaderHeight / 2, mTextPaint);
            } else {
                canvas.drawRect(0, view.getTop(), parent.getWidth(), view.getTop(), mLinePaint);
            }
        }

    }

    // 绘制的东西会在显示的 item 的上面, 也就说绘制的东西遮住 item 的显示
    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        if (parent.getAdapter() instanceof WeeklyAdapter){
            WeeklyAdapter adapter = (WeeklyAdapter) parent.getAdapter();
            int position = ((GridLayoutManager) Objects.requireNonNull(parent.getLayoutManager())).findFirstVisibleItemPosition();
            if (adapter.isHasHeader() && position == 0){
                return;
            }
            // 如果不是 mHeaderView 的话, 也就是头部 View, 那么就在 RecycleView
            // 里列表的第一个可以看见的 View 的顶部画一个固定栏
            // 怎样找到第一个可见的 View, 以及在第一个可见的 View 的顶部x, y 坐标值
            canvas.drawRect(0,0,parent.getWidth(),mItemHeaderHeight, mItemHeaderPaint);
            canvas.drawText(adapter.getGroupName(position), parent.getWidth()/2 - mTextRect.width()/2, mItemHeaderHeight/2+ mTextRect.height(), mTextPaint);
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
            if (headerInterface.isStick(index)) {
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
            int widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.getMeasuredWidth() - layoutParams.leftMargin - layoutParams.rightMargin,
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
