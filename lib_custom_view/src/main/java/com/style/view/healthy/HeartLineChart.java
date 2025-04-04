package com.style.view.healthy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeartLineChart extends View {
    private final String TAG = this.getClass().getSimpleName();
    private static final float DEFAULT_MIN = 40F;
    private static final float DEFAULT_MAX = 120F;
    private static final float DEFAULT_MIN_SECOND = 0F;
    private static final float DEFAULT_MAX_SECOND = 200F;
    private float yMin = DEFAULT_MIN;
    private float yMax = DEFAULT_MAX;
    private int mViewHeight, mViewWidth;
    private float mPaddingLeft = 40, mPaddingTop = 15, mPaddingRight = 15, mPaddingBottom = 30,
            mInnerPadding = 15, mIntervalWidth;
    private float mYaxisHeight, mXaxisWidth;
    private int lineWidth = 2;
    private int lineColor = 0xFF45CE7B;
    private int colorLabelY = 0xffaaaaaa;
    private int colorGrid = 0x99CCCCCC;
    private Paint mAxisPaint;
    private TextPaint mLabelXPaint;
    private TextPaint mLabelYPaint;
    private Paint mChartPaint;
    private float mYTextWidth, labelYHeight, labelXHeight;
    private Path mLinePath;
    private ArrayList<HeartLineItem> mItemList = new ArrayList<>();

    public HeartLineChart(Context context) {
        this(context, null);
    }

    public HeartLineChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartLineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());

        mPaddingTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPaddingTop, getResources().getDisplayMetrics());
        mPaddingBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPaddingBottom, getResources().getDisplayMetrics());
        mPaddingLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPaddingLeft, getResources().getDisplayMetrics());
        mPaddingRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPaddingRight, getResources().getDisplayMetrics());
        lineWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lineWidth, getResources().getDisplayMetrics());
        mIntervalWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, getResources().getDisplayMetrics());
        mLeft = -2 * mIntervalWidth;
        mLinePath = new Path();
        initPaint(context);
    }

    private void initPaint(Context context) {
        mAxisPaint = new Paint();
        mAxisPaint.setAntiAlias(true);
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mAxisPaint.setDither(true);
        mAxisPaint.setShader(null);
        mAxisPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics()));
        mAxisPaint.setColor(colorGrid);

        mLabelYPaint = new TextPaint();
        mLabelYPaint.setAntiAlias(true);
        mLabelYPaint.setTextAlign(Paint.Align.CENTER);
        mLabelYPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mLabelYPaint.setColor(colorLabelY);
        labelYHeight = mLabelYPaint.getFontMetrics().bottom - mLabelYPaint.getFontMetrics().top;
        mYTextWidth = mLabelYPaint.measureText("999");

        mLabelXPaint = new TextPaint(mLabelYPaint);
        mLabelXPaint.setColor(colorLabelY);
        labelXHeight = mLabelXPaint.getFontMetrics().bottom - mLabelXPaint.getFontMetrics().top;

        mChartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChartPaint.setStrokeJoin(Paint.Join.ROUND);// 笔刷图形样式
        mChartPaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔转弯的连接风格
        mChartPaint.setDither(true);//防抖动
        mChartPaint.setShader(null);//设置渐变色
        mChartPaint.setStyle(Paint.Style.STROKE);
        mChartPaint.setColor(lineColor);
        mChartPaint.setStrokeWidth(lineWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        mXaxisWidth = mViewWidth - mPaddingLeft - mPaddingRight;
        mYaxisHeight = mViewHeight - mPaddingTop - mPaddingBottom;
        Log.e(TAG, "onMeasure--" + mViewWidth + "  " + mViewHeight);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawGridsAndYLabel(canvas);
        drawPathAndXLabel(canvas);
    }

    private void drawGridsAndYLabel(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.save();
        canvas.translate(mPaddingLeft, mPaddingTop);
        float xGridWidth = mXaxisWidth / 4;
        for (int i = 0; i <= 4; i++) {
            canvas.drawLine(xGridWidth * i, 0, xGridWidth * i, mYaxisHeight, mAxisPaint);
        }
        float yGridHeight = mYaxisHeight / 4;
        for (int i = 0; i <= 4; i++) {
            int y = (int) (yGridHeight * i);
            canvas.drawLine(0, y, mXaxisWidth, y, mAxisPaint);
            int yLabel = (int) (yMax - (yMax - yMin) / 4 * i);
            canvas.drawText(String.valueOf(yLabel), -mPaddingLeft / 2, y + labelYHeight / 3, mLabelYPaint);
        }
        canvas.restore();
    }

    private void drawPathAndXLabel(Canvas canvas) {
        if (mItemList.size() <= 1) {
            return;
        }
        canvas.save();
        canvas.translate(mPaddingLeft + mInnerPadding, mPaddingTop + mYaxisHeight);
        canvas.clipRect(0, -mPaddingTop - mYaxisHeight, mXaxisWidth - mInnerPadding * 2, mPaddingBottom);
        mLinePath.reset();
        boolean move = false;
        int x,y;
        for (int i = 0; i < mItemList.size(); i++) {
            x = (int) (mIntervalWidth * i - mOffset);
            if (x > mLeft && x < mMaxOffset) {
                int yValue = mItemList.get(i).yValue;
                y = (int) (-(mYaxisHeight * (yValue - yMin)) / (yMax - yMin));
                if (!move) {
                    mLinePath.moveTo(x, y);
                    move = true;
                } else {
                    mLinePath.lineTo(x, y);
                }
                if (i % 5 == 0)
                    canvas.drawText(mItemList.get(i).xLabel, x, mPaddingBottom / 2 + getBaseLine2CenterY(mLabelXPaint.getFontMetrics()), mLabelXPaint);
            }
            if (x >= mMaxOffset)
                break;
        }
        canvas.drawPath(mLinePath, mChartPaint);
        canvas.restore();
    }

    /**
     * 计算绘制文字时的基线到中轴线的距离
     *
     * @param fontMetrics
     * @return 基线和centerY的距离
     */
    public static float getBaseLine2CenterY(Paint.FontMetrics fontMetrics) {
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

    public void setData(List<HeartLineItem> list) {
        mScroller.abortAnimation();
        mItemList.clear();
        yMin = DEFAULT_MIN;
        yMax = DEFAULT_MAX;
        if (list != null && !list.isEmpty()) {
            for (HeartLineItem item : list) {
                if (item.yValue > DEFAULT_MAX || item.yValue < DEFAULT_MIN) {
                    yMax = DEFAULT_MAX_SECOND;
                    yMin = DEFAULT_MIN_SECOND;
                }
            }
            mItemList.addAll(list);
        }
        mMaxOffset = (int) (mIntervalWidth * (mItemList.size() - 1) - mXaxisWidth + mIntervalWidth * 2);
        mMaxOffset = Math.max(mMaxOffset, 0);
        mOffset = 0;
        Log.e(TAG, "mMaxOffset = " + mMaxOffset);
        requestLayout();
        invalidate();
    }

    public void scrollToRight() {
        post(() -> {
            mOffset = mMaxOffset;
            invalidate();
        });
    }

    private List<HeartLineItem> getData() {
        ArrayList<HeartLineItem> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            HeartLineItem b = new HeartLineItem(random.nextInt(40) + 60, String.valueOf(i));
            list.add(b);
        }
        return list;
    }

    private float mLeft = 0, mMaxOffset = 0, mOffset = 0;
    private float mLastX;
    private Scroller mScroller;
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private float mTouchSlop = 5, mTouchDownX, mTouchDownY, mTouchMoveX, mTouchMoveY;
    private boolean mIsBeingDragged = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Log.e(TAG, "onTouchEvent   " + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                velocityTracker.clear();
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                mLastX = event.getX();
                if (mScroller.computeScrollOffset())
                    mScroller.forceFinished(true);
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchMoveX = event.getX();
                mTouchMoveY = event.getY();
                int distanceX = (int) (mTouchMoveX - mTouchDownX);
                if (!mIsBeingDragged && Math.abs(distanceX) > mTouchSlop) {
                    mLastX = mTouchMoveX;
                    mIsBeingDragged = true;
                }
                if (mIsBeingDragged)
                    if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);

                if (mIsBeingDragged) {
                    velocityTracker.addMovement(event);
                    int rawXMove = (int) event.getX();
                    int move = (int) (mLastX - rawXMove);
                    mOffset += move;
                    refresh();
                    mLastX = rawXMove;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    velocityTracker.addMovement(event);
                    velocityTracker.computeCurrentVelocity(10, 50);
                    float mVelocityX = velocityTracker.getXVelocity();
                    Log.e(TAG, "mVelocityX = " + mVelocityX + " px/ms");
                    if (mOffset > 0 && mOffset < mMaxOffset && mVelocityX != 0) {
                        float dis;  //速度越大位移越大消耗时间越长,假设速度是5000px/1000ms时，位移是3000px，时间是2000ms.
                        int duration;
                        dis = mVelocityX / (5f / 3000f);
                        duration = (int) Math.abs(mVelocityX / (5f / 2000f));
                        Log.e(TAG, "dis = " + dis + " px" + "  duration = " + duration + " ms");
                        if (dis != 0) {
                            mScroller.startScroll((int) mOffset, 0, (int) -dis, 0, Math.max(duration, 500));  //duration太小会有跳动效果，不平滑
                        }
                    }
                }
                mIsBeingDragged = false;
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            mOffset = mScroller.getCurrX();
            Log.e(TAG, "computeScroll = " + mOffset);
            refresh();
        } else {
            mScroller.forceFinished(true);
        }
    }

    private void refresh() {
        if (mItemList.size() <= 1 || (mIntervalWidth * (mItemList.size() - 1) <= mXaxisWidth - mInnerPadding * 2)) {
            return;
        }
        mOffset = Math.min(Math.max(mOffset, 0), mMaxOffset);
        invalidate();
    }

    public static class HeartLineItem {
        public int yValue;
        public String xLabel;

        public HeartLineItem(int yValue, String xLabel) {
            this.yValue = yValue;
            this.xLabel = xLabel;
        }
    }

    public static int dp2px(Context context, float dpValue) {
        float pxValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
        return (int) (pxValue + 0.5f);//0.5f是为了四舍五入，但有时候四舍五入不一定就好
    }
}
