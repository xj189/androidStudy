package com.style.data.http.function;

import com.style.entity.KuaiDi;
import com.style.entity.UserInfo;
import com.style.data.http.request.LoginRequest;
import com.style.http.response.BaseDataResponse;
import com.style.data.http.response.TokenResponse;

import java.util.List;

import com.style.data.http.response.LoginBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by xiajun on 2017/12/21.
 */

public interface UserNetSource {

    @POST("/app/changePsd.html")
    Observable<BaseDataResponse<LoginBean>> login(@Field("userName") String name, @Field("password") String password);

    @POST("kuaidi")
    Observable<List<KuaiDi>> getkuaidi(@Body RequestBody requestBody);

    @Headers("Authorization:YW5kcm9pZGNsaWVudDo4QTcyOUZENC04NjdGLTREMTItOEE4Ri1CQTNFOEQ4MzhERjM=")
    @POST("https://192.168.0.3/OAuth/Token")
    @FormUrlEncoded
    Observable<BaseDataResponse<TokenResponse>> getToken(@Field("grant_type") String grant_type);

    @POST("guardian/login")
    Observable<BaseDataResponse<UserInfo>> login2(@Body LoginRequest requestBody);

    @POST("gsegserf")
    Observable<ResponseBody> test();
}
