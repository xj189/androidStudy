package com.style.http.core;

import android.text.TextUtils;
import android.util.Log;

import com.style.config.AssembleConfig;
import com.style.data.prefs.AppPrefsManager;
import com.style.http.converter.CustomConverterFactory;
import com.style.lib.common.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by xiajun on 2017/12/21.
 */

public final class RetrofitImpl {
    protected String TAG = this.getClass().getSimpleName();
    private static final long HTTP_TIME_OUT = 5;
    private final OkHttpClient mOkHttpClient;

    private static final Object mLock = new Object();
    private static RetrofitImpl mInstance;

    public static RetrofitImpl getInstance() {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new RetrofitImpl();
            }
            return mInstance;
        }
    }

    private RetrofitImpl() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(headerInterceptor)
                .build();
    }

    public Retrofit getDefaultRetrofit() {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(AssembleConfig.URL_BASE)
                .addConverterFactory(CustomConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
        return mRetrofit;
    }

    //日志拦截器
    public static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
        if (BuildConfig.DEBUG)
            Log.e("okhttp", message);
    }).setLevel(HttpLoggingInterceptor.Level.BODY);//设置打印数据的级别


    public static Interceptor headerInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder newBuilder = original.newBuilder();
            if (TextUtils.isEmpty(original.header("Authorization")))//这里没打印Authorization因为执行在日志拦截后
                newBuilder.addHeader("Authorization", AppPrefsManager.getInstance().getSignKey());
            //String language = Locale.getDefault().getLanguage();//服务器根据不同语言返回不同描述
            Request newRequest = newBuilder.build();
            return chain.proceed(newRequest);
        }
    };
}
