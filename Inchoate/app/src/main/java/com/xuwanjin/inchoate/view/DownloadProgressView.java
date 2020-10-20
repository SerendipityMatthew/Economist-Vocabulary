package com.xuwanjin.inchoate.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;

import com.xuwanjin.inchoate.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;


/**
 * @author Matthew Xu
 */
public class DownloadProgressView extends androidx.appcompat.widget.AppCompatButton {
    public static final String TAG = "DownloadProgressView";
    private PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    private int DEFAULT_HEIGHT_DP = 20;

    private int mBorderWidth;

    private static final float MAX_PROGRESS = 100f;

    private Paint mTextPaint;

    private Paint mBgPaint;

    private Paint mPgPaint;


    private Rect mTextRect;

    private RectF mBgRectf;

    /**
     * 左右来回移动的滑块
     */
    private Bitmap mFlickerBitmap;

    /**
     * 滑块移动最左边位置，作用是控制移动
     */
    private float mFlickerLeft;

    /**
     * 进度条 bitmap ，包含滑块
     */
    private Bitmap mPgBitmap;

    private Canvas mPgCanvas;

    /**
     * 当前进度
     */
    private float mProgress;

    private boolean isFinish;

    private boolean isStop;

    /**
     * 下载中颜色
     */
    private int mLoadingColor;

    /**
     * 暂停时颜色
     */
    private int mStopColor;

    /**
     * 进度文本、边框、进度条颜色
     */
    private int mProgressColor;

    private int mTextSize;

    private int mRadius;


    private String mProgressText;
    private BitmapShader mBitmapShader;
    private ArrayList<String> mColorList = new ArrayList<>();
    private Random mRandom = new Random();

    public DownloadProgressView(Context context) {
        this(context, null, 0);
    }

    public DownloadProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.DownloadProgressBar);
        try {
            mTextSize = (int) ta.getDimension(R.styleable.DownloadProgressBar_textSize, 50);
            mLoadingColor = ta.getColor(R.styleable.DownloadProgressBar_loadingColor, Color.parseColor("#40c4ff"));
            mStopColor = ta.getColor(R.styleable.DownloadProgressBar_stopColor, Color.parseColor("#ff9800"));
            mRadius = (int) ta.getDimension(R.styleable.DownloadProgressBar_radius, 0);
            mBorderWidth = (int) ta.getDimension(R.styleable.DownloadProgressBar_borderWidth, 1);
        } finally {
            ta.recycle();
        }
    }

    private void init() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeWidth(mBorderWidth);

        mPgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPgPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);

        mTextRect = new Rect();
        mBgRectf = new RectF(mBorderWidth, mBorderWidth, getMeasuredWidth() / 3, getMeasuredHeight() / 3);

        if (isStop) {
            mProgressColor = mStopColor;
        } else {
            mProgressColor = mLoadingColor;
        }

        mFlickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flicker);
        mFlickerLeft = -mFlickerBitmap.getWidth();

        initPgBimap();

        mColorList.add("#40c4ff");
        mColorList.add("#FF0000");
        mColorList.add("#03DAC5");
        mColorList.add("#3700B3");
        mColorList.add("#6200EE");
    }

    private void initPgBimap() {
        Log.d(TAG, "initPgBimap: getMeasuredWidth() = " + getMeasuredWidth());
        Log.d(TAG, "initPgBimap: getMeasuredHeight() = " + getMeasuredHeight());
        Log.d(TAG, "initPgBimap: borderWidth = " + mBorderWidth);
        Log.d(TAG, "initPgBimap: borderWidth = " + mBorderWidth);
        mPgBitmap = Bitmap.createBitmap(getMeasuredWidth() / 3, 50, Bitmap.Config.RGB_565);
        mPgCanvas = new Canvas(mPgBitmap);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                height = dp2px(DEFAULT_HEIGHT_DP);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSpecSize;
                break;
        }
        setMeasuredDimension(widthSpecSize, height);

        if (mPgBitmap == null) {
            init();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景
//        drawBackGround(canvas);

        //进度
//        drawProgress(canvas);

        //进度text
        drawProgressText(canvas);

        //变色处理
        drawColorProgressText(canvas);
    }

    /**
     * 边框
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        mBgPaint.setColor(mProgressColor);
        //left、top、right、bottom不要贴着控件边，否则border只有一半绘制在控件内,导致圆角处线条显粗
        canvas.drawRoundRect(mBgRectf, mRadius, mRadius, mBgPaint);
    }

    private void drawProgressText(Canvas canvas) {
        mTextPaint.setColor(mProgressColor);
        mProgressText = getProgressText();
        mTextPaint.getTextBounds(mProgressText, 0, mProgressText.length(), mTextRect);
        int tWidth = mTextRect.width();
        int tHeight = mTextRect.height();
        float xCoordinate = (getMeasuredWidth() - tWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + tHeight) / 2;
        canvas.drawText(mProgressText, xCoordinate, yCoordinate, mTextPaint);
    }

    private String getProgressText() {
        String text = "";
        DecimalFormat df = new DecimalFormat("00.00");
        String percentStr = df.format(mProgress);
        if (!isFinish) {
            if (!isStop) {
                text = "已下载" + percentStr + "%";
            } else {
                text = "继续";
            }
        } else {
            text = "下载完成";
        }

        return text;
    }

    /**
     * 进度
     */
    @SuppressLint("ResourceType")
    private void drawProgress(Canvas canvas) {
        Log.d(TAG, "drawProgress: ");
        int colorInt = mRandom.nextInt(mColorList.size() - 1);
        mProgressColor = Color.parseColor(mColorList.get(colorInt));
        mPgPaint.setColor(mProgressColor);

        float right = (mProgress / MAX_PROGRESS) * getMeasuredWidth();
        Log.d(TAG, "drawProgress: getMeasuredWidth() = " + getMeasuredWidth());
        mPgCanvas.save();
        mPgCanvas.clipRect(0, 0, right / 2, getMeasuredHeight() / 2);
        mProgressColor = Color.parseColor(getContext().getString(R.color.grey));
        mPgCanvas.drawColor(mProgressColor);
        mPgCanvas.restore();

        if (!isStop) {
            mPgPaint.setXfermode(mXfermode);
            mPgCanvas.drawBitmap(mFlickerBitmap, mFlickerLeft, 0, mPgPaint);
            mPgPaint.setXfermode(null);
        }

        //控制显示区域
        mBitmapShader = new BitmapShader(mPgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPgPaint.setShader(mBitmapShader);
        canvas.drawRoundRect(mBgRectf, mRadius, mRadius, mPgPaint);
    }

    /**
     * 变色处理
     *
     * @param canvas
     */
    private void drawColorProgressText(Canvas canvas) {
        mTextPaint.setColor(Color.WHITE);
        int tWidth = mTextRect.width();
        int tHeight = mTextRect.height();
        float xCoordinate = (getMeasuredWidth() - tWidth) / 2;
        float yCoordinate = (getMeasuredHeight() + tHeight) / 2;
        float progressWidth = (mProgress / MAX_PROGRESS) * getMeasuredWidth();
        if (progressWidth > xCoordinate) {
//            canvas.save();
//            float right = Math.min(progressWidth, xCoordinate + tWidth * 1.1f);
//            canvas.clipRect(xCoordinate, 0, right, getMeasuredHeight());
//            canvas.drawText("Downloading Audio", xCoordinate, yCoordinate, mTextPaint);
//            canvas.restore();
        }
    }

    public void setProgress(float mProgress) {
        if (!isStop) {
            if (mProgress < MAX_PROGRESS) {
                this.mProgress = mProgress;
            } else {
                this.mProgress = MAX_PROGRESS;
                finishLoad();
            }
            invalidate();
        }
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (isStop) {
            mProgressColor = mStopColor;
        } else {
            mProgressColor = mLoadingColor;
        }
        invalidate();
    }

    public void finishLoad() {
        isFinish = true;
        setStop(true);
    }

    public void toggle() {
        if (!isFinish) {
            if (isStop) {
                setStop(false);
            } else {
                setStop(true);
            }
        }
    }

    public boolean isStop() {
        return isStop;
    }

    public boolean isFinish() {
        return isFinish;
    }

    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
