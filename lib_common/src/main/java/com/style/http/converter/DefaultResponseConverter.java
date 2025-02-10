package com.style.http.converter;

import com.google.gson.Gson;
import com.style.http.exception.HttpResultException;
import com.style.http.response.BaseDataResponse;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Converter;

public class DefaultResponseConverter<T> implements Converter<ResponseBody, T> {
    private Type type;

    public DefaultResponseConverter(Type type) {
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        BufferedSource bufferedSource = Okio.buffer(value.source());
        String tempStr = bufferedSource.readUtf8();
        bufferedSource.close();
        //T baseRes = JSON.parseObject(tempStr, type);
        T t = new Gson().fromJson(tempStr, type);
        if (t instanceof BaseDataResponse && !((BaseDataResponse) t).isOk()) {
            throw new HttpResultException(((BaseDataResponse) t).getCode(), ((BaseDataResponse) t).getMsg());
        }
        return t;
    }
}