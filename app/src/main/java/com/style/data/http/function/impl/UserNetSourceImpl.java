package com.style.data.http.function.impl;


import com.style.data.http.function.UserNetSource;
import com.style.data.http.request.LoginRequest;
import com.style.data.http.response.LoginBean;
import com.style.data.http.response.TokenResponse;
import com.style.entity.KuaiDi;
import com.style.entity.UserInfo;
import com.style.http.core.RetrofitImpl;
import com.style.http.core.RxSchedulers;
import com.style.http.response.BaseDataResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public final class UserNetSourceImpl {
    private static UserNetSource mAPI;

    static {
        mAPI = RetrofitImpl.getInstance().getDefaultRetrofit().create(UserNetSource.class);
    }

    public static Observable<BaseDataResponse<LoginBean>> login(String userName, String password) {
        Observable<BaseDataResponse<LoginBean>> mObservable = mAPI.login(userName, password);
        return mObservable.compose(RxSchedulers.applySchedulers());
    }

    public static Observable<List<KuaiDi>> getkuaidi(String id) {
        JSONObject o = new JSONObject();
        try {
            o.put("Id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), o.toString());
        return mAPI.getkuaidi(requestBody);
    }

    public static Observable<BaseDataResponse<TokenResponse>> getToken() {
        return mAPI.getToken("client_credentials").compose(RxSchedulers.applySchedulers());
    }

    public static Observable<BaseDataResponse<UserInfo>> login2(String userName, String passWord) {
        return mAPI.login2(new LoginRequest(userName, passWord)).compose(RxSchedulers.applySchedulers());
    }

    public static Observable<ResponseBody> test() {
        return mAPI.test().compose(RxSchedulers.applySchedulers());
    }
}
