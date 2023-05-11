package com.example.demo.http.interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements com.example.demo.http.interceptors.Interceptor {
    private int maxRetries;

    public RetryInterceptor(int maxRetries){
        this.maxRetries = maxRetries;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response;
        int i = 0;
        for(i = 0; i < maxRetries; i++){
            try{
                response = chain.proceed(request);
                if(response.isSuccessful()){
                    return response;
                }
            }
            catch(IOException e) {
                if(i == maxRetries - 1)
                    throw e;
            }
        }
        throw new IOException("Max retry: "+ maxRetries + "retried");
    }
}
