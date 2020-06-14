package com.xuwanjin.inchoate.ui.today;

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

import com.xuwanjin.inchoate.model.Article;

public class TodayItemDecoration extends RecyclerView.ItemDecoration {
    public static final String TAG = "TodayItemDecoration";
    public Context mContext;
    private int mItemHeaderHeight = 0;
    Paint mItemHeaderPaint;
    private Rect mTextRect;
    private Paint mTextPaint;
    private Paint mLinePaint;


    public TodayItemDecoration(Context context) {
        this.mContext = context;
        this.mItemHeaderHeight = dip2px(mContext, 40);

        mItemHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // got the color of textview background, by color picker
        mItemHeaderPaint.setColor(Color.parseColor("#FBFBFB"));

        mTextRect = new Rect();
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(46);
        mTextPaint.setColor(Color.BLACK);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        if (parent.getAdapter() instanceof TodayNewsAdapter) {
            TodayNewsAdapter adapter = (TodayNewsAdapter) parent.getAdapter();
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = parent.getChildAt(i);
                int position = parent.getChildLayoutPosition(childView);
                int y = childView.getTop() - mItemHeaderHeight;
                if (position == 0) {
                    return;
                }
                boolean isHeader = adapter.isItemHeader(position);
                if (isHeader) {
                    String itemHeaderTitle = adapter.getGroupName(position);
                    Paint paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paintRed.setColor(Color.RED);
                    Paint paintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paintBlack.setColor(Color.BLACK);
                    canvas.drawRect(0, y, parent.getWidth(), childView.getTop(), paintRed);
                    canvas.drawText(itemHeaderTitle, 50, y + mItemHeaderHeight / 2 , mTextPaint);
                }
            }

        }
    }

    public boolean isSkipDraw(int position) {
        if (position == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        if (parent.getAdapter() instanceof TodayNewsAdapter) {
            TodayNewsAdapter adapter = (TodayNewsAdapter) parent.getAdapter();
            int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
            int y = mItemHeaderHeight / 2 + mTextRect.height() / 2;

            String itemHeaderTitle = adapter.getGroupName(position);
            // 如果把下面的注释掉, 会出现即使下一个分类小组没有滑动到顶部, 顶部的 stick header 会消失
            Paint greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            greenPaint.setColor(Color.GREEN);
            Paint paintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintBlack.setColor(Color.BLACK);
            canvas.drawRect(0, 0, parent.getWidth(), dip2px(mContext, 10), paintBlack);
            canvas.drawRect(0, 20, parent.getWidth(), mItemHeaderHeight, greenPaint);
            canvas.drawText(itemHeaderTitle, 50, y, mTextPaint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof TodayNewsAdapter) {
            outRect.top = mItemHeaderHeight;
        }
    }
}
