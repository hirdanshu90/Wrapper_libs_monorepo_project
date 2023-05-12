package com.example.demo.http.interceptors;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * This is just a sample implementation of wrapper interface {@link com.example.demo.http.interceptors.Interceptor}
 */
public class SampleInterceptor implements com.example.demo.http.interceptors.Interceptor {

    public SampleInterceptor(){
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        System.out.println("**********    Printing from Sample Interceptor   *************");
        return response;
    }
}
