package com.style.common_ui.refresh;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.style.lib.common.R;

public class MyAppRefreshLayout extends SmartRefreshLayout {
    public static void init() {
        // 指定全局的Header
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.pull_refresh_bg, R.color.pull_refresh_text);//全局设置主题颜色
            return (com.scwang.smart.refresh.layout.api.RefreshHeader) new SimpleRefreshHeader(context);
        });
        // 指定全局的footer
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.pull_refresh_bg, R.color.pull_refresh_text);//全局设置主题颜色
            return new SimpleRefreshFooter(context);
        });

        // 默认刷新加载布局
        //setDefaultRefreshHeaderCreator((context, layout) -> new ClassicsHeader(context));
        //setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context));
    }

    public MyAppRefreshLayout(Context context) {
        super(context);
        initView();
    }

    public MyAppRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setEnableClipFooterWhenFixedBehind(true);

    }

    //还有更多数据
    public void setHasMoreData() {
        setEnableLoadMore(true);
        setNoMoreData(false);
    }

    // 下拉/上拉完成
    public void complete() {
        if (mState == RefreshState.Loading) {
            finishLoadMore();
        } else {
            finishRefresh();
        }
    }

    /**
     * 完成刷新
     *
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return (SmartRefreshLayout) finishRefresh(Math.max(0, 500 - (int) passTime));//保证刷新动画有1000毫秒的时间
    }

    /**
     * 完成加载
     *
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return (SmartRefreshLayout) finishLoadMore(Math.max(0, 500 - (int) passTime));//保证加载动画有1000毫秒的时间
    }
}
