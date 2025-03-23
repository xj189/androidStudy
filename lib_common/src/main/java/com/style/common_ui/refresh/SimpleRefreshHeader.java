package com.style.common_ui.refresh;

import static com.scwang.smart.refresh.layout.constant.RefreshState.PullDownToRefresh;
import static com.scwang.smart.refresh.layout.constant.RefreshState.ReleaseToRefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.style.common_ui.xxrefreshview.header.RefreshCircleAProgressBar;
import com.style.lib.common.R;

/**
 * 自定义简洁下拉刷新头
 * Created by xiajun on 2018/11/13.
 */

@SuppressLint("RestrictedApi")
public class SimpleRefreshHeader extends RelativeLayout implements RefreshHeader {
    protected final String TAG = getClass().getSimpleName();
    private TextView mTextView;
    private RefreshCircleAProgressBar progressBar;
    //假设旋转一度需要下拉的距离，单位：px
    int distanceOfAngle = 1;
    private int mStartAngle;
    private boolean isShowText = true;

    public SimpleRefreshHeader(Context context) {
        super(context);
        initView(context);
    }

    public SimpleRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SimpleRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SimpleRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        RelativeLayout popView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.xxrefresh_default_header, null);
        progressBar = popView.findViewById(R.id.xxrefresh_default_progress_bar);
        mTextView = popView.findViewById(R.id.xxrefresh_default_header_tv);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 240);
        addView(popView, params);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        Log.e(TAG, "onInitialized height--" + height + "  extendHeight--" + extendHeight);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        Log.e(TAG, "onPulling percent--" + percent + "  offset--" + offset);
        progressBar.setmIndeterminate(false);
        mStartAngle = -offset / distanceOfAngle % 360;
        progressBar.setStartAngle(mStartAngle);
    }

    @Override
    public boolean autoOpen(int duration, float dragRate, boolean animationOnly) {
        return false;
    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
        Log.e(TAG, "onReleased");

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        Log.e(TAG, "onStartAnimator");

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        return 500;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None: // 无状态
                break;
            case PullDownToRefresh: // 下拉即可刷新
                if (isShowText())
                    mTextView.setText("");
                break;
            case Refreshing: // 刷新中状态
                if (isShowText())
                    mTextView.setText("正在刷新...");
                progressBar.setmIndeterminate(true);
                break;
            case ReleaseToRefresh:  // 释放就开始刷新状态
                if (isShowText())
                    mTextView.setText("松开刷新");
                break;
        }
    }


    public boolean isShowText() {
        return isShowText;
    }

    public void setShowText(boolean showText) {
        isShowText = showText;
    }
}
