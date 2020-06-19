package com.xuwanjin.inchoate.ui.today;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.ui.BaseItemDecoration;

/**
 * @author Matthew Xu
 */
public class TodayItemDecoration extends BaseItemDecoration<TodayNewsAdapter> {
    public static final String TAG = "TodayItemDecoration";
    private int mItemHeaderHeight = 0;
    private Paint mItemHeaderPaint;
    private Rect mTextRect;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private Paint mPaintRed;
    private Paint mGreenPaint;
    private Paint mBlackPaint;


    public TodayItemDecoration(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.mItemHeaderHeight = dip2px(mContext, 40);

        mItemHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // got the color of textview background, by color picker
        mItemHeaderPaint.setColor(Color.parseColor("#FBFBFB"));
        Log.d(TAG, "TodayItemDecoration: ");
        mTextRect = new Rect();
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(46);
        mTextPaint.setColor(Color.BLACK);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRed.setColor(Color.RED);

        mGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGreenPaint.setColor(Color.GREEN);

        mBlackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlackPaint.setColor(Color.BLACK);
    }

    @Override
    public void onDrawImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent, View childView, int position) {
        TodayNewsAdapter adapter = (TodayNewsAdapter) parent.getAdapter();
        int y = childView.getTop() - mItemHeaderHeight;
        boolean isHeader = adapter.isItemHeader(position);
        if (isHeader) {
            String itemHeaderTitle = adapter.getGroupName(position);
            canvas.drawRect(0, y, parent.getWidth(), childView.getTop(), mPaintRed);
            canvas.drawText(itemHeaderTitle, 50, y + mItemHeaderHeight / 2, mTextPaint);
        }
    }

    @Override
    protected boolean isSkipDraw(int position, boolean isOver) {
        // 对 Today Page, onDraw 的第一个不需要绘制, 否则会导致和 onDrawImpl 的第一个相冲突
        if (position == 0) {
            if (!isOver) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDrawOverImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent, int position) {
        int y = mItemHeaderHeight / 2 + mTextRect.height() / 2;
        String itemHeaderTitle = mAdapter.getGroupName(position);
        canvas.drawRect(0, 0, parent.getWidth(), dip2px(mContext, 10), mBlackPaint);
        canvas.drawRect(0, 20, parent.getWidth(), mItemHeaderHeight, mGreenPaint);
        canvas.drawText(itemHeaderTitle, 50, y, mTextPaint);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof TodayNewsAdapter) {
            outRect.top = mItemHeaderHeight;
        }
    }
}
