package com.style.common_ui.refresh;

import static com.scwang.smart.refresh.layout.constant.RefreshState.Refreshing;

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

import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.style.lib.common.R;

/**
 * 自定义简洁上拉加载布局
 * Created by xiajun on 2018/11/13.
 */

@SuppressLint("RestrictedApi")
public class SimpleRefreshFooter extends RelativeLayout implements RefreshFooter {

    public static String REFRESH_FOOTER_PULLUP = "上拉加载更多";
    public static String REFRESH_FOOTER_RELEASE = "释放立即加载";
    public static String REFRESH_FOOTER_LOADING = "-------- 正在加载数据 --------";
    public static String REFRESH_FOOTER_REFRESHING = "正在刷新...";
    public static String REFRESH_FOOTER_FINISH = "加载完成";
    public static String REFRESH_FOOTER_FAILED = "加载失败";
    public static String REFRESH_FOOTER_ALLLOADED = "-------- 已经到底了 --------";

    protected final String TAG = getClass().getSimpleName();
    private TextView mTitleText;
    private boolean isShowText = true;
    protected int mFinishDuration = 500;
    protected boolean mNoMoreData = false;

    public SimpleRefreshFooter(Context context) {
        super(context);
        initView(context);
    }

    public SimpleRefreshFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SimpleRefreshFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SimpleRefreshFooter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        RelativeLayout popView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.xxrefresh_simple_footer, null);
        mTitleText = popView.findViewById(R.id.xxrefresh_simple_footer_tv_load_more);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 130);
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
        if (!mNoMoreData) {
            if (success) {
                mTitleText.setText(REFRESH_FOOTER_FINISH);
            } else {
                mTitleText.setText(REFRESH_FOOTER_FAILED);
            }
            return mFinishDuration;
        }
        return 0;
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
        if (!mNoMoreData) {
            switch (newState) {
                case None:
                case PullUpToLoad:
                    mTitleText.setText(REFRESH_FOOTER_PULLUP);
                    break;
                case Loading:
                case LoadReleased:
                    mTitleText.setText(REFRESH_FOOTER_LOADING);
                    break;
                case ReleaseToLoad:
                    mTitleText.setText(REFRESH_FOOTER_RELEASE);
                    break;
                case Refreshing:
                    mTitleText.setText(REFRESH_FOOTER_REFRESHING);
                    break;
            }
        }
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            if (noMoreData) {
                mTitleText.setText(REFRESH_FOOTER_ALLLOADED);
            } else {
                mTitleText.setText(REFRESH_FOOTER_PULLUP);
            }
        }
        return true;
    }

    public boolean isShowText() {
        return isShowText;
    }

    public void setShowText(boolean showText) {
        isShowText = showText;
    }
}
