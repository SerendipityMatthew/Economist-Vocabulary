package com.xuwanjin.inchoate.ui.article;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuwanjin.inchoate.model.Paragraph;
import com.xuwanjin.inchoate.ui.BaseItemDecoration;

import java.util.List;

/**
 * @author Matthew Xu
 */
public class ArticleItemDecoration extends BaseItemDecoration<ArticleContentAdapter> {
    private int mItemHeaderHeight = 0;
    Paint mItemHeaderPaint;
    private Rect mTextRect;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private int mArticleWordCount = -1;
    private Paint mPaint;

    public ArticleItemDecoration(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);

        this.mItemHeaderHeight = dip2px(mContext, 40);

        mItemHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // got the color of textview background, by color picker
        mItemHeaderPaint.setColor(Color.parseColor("#FBFBFB"));
        mTextRect = new Rect();
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(46);
        mTextPaint.setColor(Color.BLACK);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);

        mArticleWordCount = getArticleWordCount(mAdapter.getDataList());
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onDrawImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent, View childView, int position) {
        int y = childView.getTop() - mItemHeaderHeight;
        canvas.drawRect(0, y, parent.getWidth(), childView.getTop(), mItemHeaderPaint);
        canvas.drawRect(50, childView.getTop() - 1, parent.getWidth(), childView.getTop(), mLinePaint);
    }

    @Override
    protected boolean isSkipDraw(int position, boolean isOver) {
        if (position == 0) {
            return true;
        }
        if (position == mAdapter.getItemCount() - 2) {
            return true;
        }
        return false;
    }

    @Override
    public void onDrawOverImpl(@NonNull Canvas canvas, @NonNull RecyclerView parent, int position) {
        int y = mItemHeaderHeight / 2 + mTextRect.height() / 2;
        String paragraph = mAdapter.getDataList().get(position).paragraph.toString();
        canvas.drawRect(0, 0, parent.getWidth(), mItemHeaderHeight, mPaint);
        int wordCount = paragraph.split(" ").length;
        String displayString = "段落单词数: " + wordCount +
                "       文章总单词书: " + mArticleWordCount;
        canvas.drawText(displayString, 50, y, mTextPaint);
    }

    public int getArticleWordCount(List<Paragraph> paragraphList) {
        int paragraphWordCount = 0;
        for (Paragraph p : paragraphList) {
            paragraphWordCount += p.paragraph.toString().split(" ").length;
        }
        mArticleWordCount = paragraphWordCount;
        return mArticleWordCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getAdapter() instanceof ArticleContentAdapter) {
            int position = parent.getChildLayoutPosition(view);
            if (isSkipDraw(position, false)) {
                return;
            } else {
                outRect.top = mItemHeaderHeight;
            }
        }
    }
}

