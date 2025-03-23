package com.style.common_ui.xxrefreshview.header;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.style.common_ui.xxrefreshview.Utils;
import com.style.lib_custom_view.R;

public class RefreshCircleAProgressBar extends BaseProgressBar {
    private Paint paint;
    protected Paint textPaint;

    private RectF rectF = new RectF();

    private float strokeWidth;

    private int max;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private float arcFinishedStartAngle;

    private final int default_finished_color = Color.WHITE;
    private final int default_unfinished_color = Color.rgb(72, 106, 176);

    private final float default_stroke_width;
    private final int default_max = 100;

    private float radius;
    private boolean mIndeterminate = true;
    private int mStartAngle;

    public RefreshCircleAProgressBar(Context context) {
        this(context, null);
    }

    public RefreshCircleAProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshCircleAProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        default_stroke_width = Utils.dp2px(context, 4);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initByAttributes(TypedArray attributes) {
        mIndeterminate = attributes.getBoolean(R.styleable.ArcProgress_arc_indeterminate, mIndeterminate);
        finishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_unfinished_color, default_unfinished_color);
        arcFinishedStartAngle = attributes.getFloat(R.styleable.ArcProgress_arc_finished_start_angle, arcFinishedStartAngle);
        setMax(attributes.getInt(R.styleable.ArcProgress_arc_max, default_max));
        setProgress(attributes.getInt(R.styleable.ArcProgress_arc_progress, 0));
        strokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_stroke_width, default_stroke_width);
    }

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(default_unfinished_color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public boolean ismIndeterminate() {
        return mIndeterminate;
    }

    public void setmIndeterminate(boolean mIndeterminate) {
        this.mIndeterminate = mIndeterminate;
        invalidate();
    }

    public void setStartAngle(int startAngle) {
        this.mStartAngle = startAngle;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        rectF.set(strokeWidth / 2f, strokeWidth / 2f, width - strokeWidth / 2f, View.MeasureSpec.getSize(heightMeasureSpec) - strokeWidth / 2f);
        radius = width / 2f;
        float angle = 180;
        float arcBottomHeight = radius * (float) (1 - Math.cos(Math.toRadians(angle)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //以画布中心点旋转坐标系
        canvas.rotate(arcFinishedStartAngle, getWidth() / 2, getHeight() / 2);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(unfinishedStrokeColor);
        canvas.drawArc(rectF, 0, 360, false, paint);
        paint.setColor(finishedStrokeColor);
        //不确定进度，不断改变起始角度mSweepAngle
        if (mIndeterminate) {
            mStartAngle += 5;
            if (mStartAngle > 359)//超过最大角度置0
                mStartAngle = 0;
            canvas.drawArc(rectF, mStartAngle, 60, false, paint);
            canvas.restore();
            invalidate();
            return;
        }
        //进度确定时
        float finishedSweepAngle = getProgress() / (float) getMax() * 360;
        canvas.drawArc(rectF, mStartAngle, finishedSweepAngle, false, paint);
        canvas.restore();
    }
}
