package com.xuwanjin.inchoate.ui.article;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.ui.weekly.WeeklyAdapter;

import java.util.HashMap;
import java.util.List;

public class ArticleItemDecoration extends RecyclerView.ItemDecoration {

    public RecyclerView mRecyclerView;
    public Context mContext;
    private int mItemHeaderHeight = 0;
    Paint mItemHeaderPaint;
    private Rect mTextRect;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private int mArticleWordCount = -1;

    public ArticleItemDecoration(RecyclerView recyclerView, Context context) {
        this.mRecyclerView = recyclerView;
        this.mContext = context;
        this.mItemHeaderHeight = dip2px(mContext, 40);

        mItemHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // got the color of textview background, by color picker
        mItemHeaderPaint.setColor(Color.parseColor("#FBFBFB"));
//        mItemHeaderPaint.setAlpha(100);

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
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = parent.getChildAt(i);
            int position = parent.getChildLayoutPosition(childView);
            if (!isSkipDraw(position)) {
                int y = childView.getTop() - mItemHeaderHeight;
                canvas.drawRect(0, y, parent.getWidth(), childView.getTop(), mItemHeaderPaint);
                canvas.drawRect(50, childView.getTop() - 1, parent.getWidth(), childView.getTop(), mLinePaint);
            }

//            if (isSkipDraw(position)) {
//                return;
//            }
//            int y = childView.getTop() - dip2px(mContext, 40);
//            Paint spacePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            spacePaint.setColor(Color.RED);
//            canvas.drawRect(0, y, parent.getWidth(), childView.getTop(), spacePaint);
//            canvas.drawRect(50, childView.getTop() - 1, parent.getWidth(), childView.getTop(), mLinePaint);
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

        if (parent.getAdapter() instanceof ArticleContentAdapter) {
            ArticleContentAdapter adapter = (ArticleContentAdapter) parent.getAdapter();
            int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
            if (isSkipDraw(position)) {
                return;
            }
            View childView = parent.findViewHolderForAdapterPosition(position).itemView;
            int y = mItemHeaderHeight / 2 + mTextRect.height() / 2;
            String paragraph = adapter.getDataList().get(position - 1).paragraph.toString();
            int paragraphWordCount = getArticleWordCount(adapter.getDataList());

            // 如果把下面的注释掉, 会出现即使下一个分类小组没有滑动到顶部, 顶部的 stick header 会消失
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.RED);

            canvas.drawRect(0, 0, parent.getWidth(), mItemHeaderHeight, paint);
            int wordCount = paragraph.split(" ").length;
            String displayString = "段落单词数: " + wordCount +
                    "       文章总单词书: " + paragraphWordCount;
            canvas.drawText(displayString, 50, y, mTextPaint);

        }
    }

    public int getArticleWordCount(List<Paragraph> paragraphList) {
        int paragraphWordCount = 0;
        for (Paragraph p : paragraphList) {
            paragraphWordCount += p.paragraph.toString().split(" ").length;
        }
        mArticleWordCount = paragraphWordCount;
        return paragraphWordCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof ArticleContentAdapter) {
            int position = parent.getChildLayoutPosition(view);
            if (isSkipDraw(position)) {
                return;
            } else {
                outRect.top = mItemHeaderHeight;
            }
        }
    }
}

