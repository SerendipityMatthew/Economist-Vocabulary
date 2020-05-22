package com.xuwanjin.inchoate.ui.bookmark;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StickHeaderDecoration extends RecyclerView.ItemDecoration {
    public StickHeaderInterface headerInterface;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private Rect mPinnedHeaderRect = null;
    private int mPinnedHeaderPosition = -1;

    public interface StickHeaderInterface {
        boolean isStick(int position);
    }

    public StickHeaderDecoration(RecyclerView recyclerView) {
        this.adapter = recyclerView.getAdapter();
        this.headerInterface = (StickHeaderInterface) adapter;
        this.recyclerView = recyclerView;
        this.manager = recyclerView.getLayoutManager();
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        //确保是 PinnedHeaderAdapter的adapter, 确保有 View
        if (parent.getChildCount() > 0) {
            //找到要固定的 pin view
            View firstView = parent.getChildAt(0);
            int firstAdapterPosition = parent.getChildAdapterPosition(firstView);
//            Log.d("Matthew", "onDrawOver: firstAdapterPosition = " + firstAdapterPosition);
            int pinnedHeaderPosition = getPinnedHeaderViewPosition(firstAdapterPosition, adapter);
            // pinnedHeaderPosition 是需要被放在 header 位置的 View 的在列表里的索引
            if (pinnedHeaderPosition != -1) {
                // onCreateViewHolder 返回的是 Adapter 里的 ViewHolder
                RecyclerView.ViewHolder headerViewHolder = adapter.onCreateViewHolder(parent,
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
//                pinnedHeaderView.draw(canvas);
                canvas.restoreToCount(saveCount);
            } else {
//                mPinnedHeaderRect = null;
            }

        }
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

    public Rect getPinnedHeaderRect() {
        return mPinnedHeaderRect;
    }


    public int getPinnedHeaderPosition() {
        return mPinnedHeaderPosition;
    }
}
