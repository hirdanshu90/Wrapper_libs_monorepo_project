package com.example.demo.http;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.example.demo.http.interceptors.RetryInterceptor;
import com.example.demo.http.progress.ProgressListenerImpl;
import com.example.demo.http.progress.ProgressResponseBody;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.Request.Builder;


/**
 * A call is a request that has been prepared for execution. A call can be canceled. As this object
 * represents a single request/response pair (stream), it cannot be executed twice.
 */
public class HttpClient {
    public MediaType MEDIA_TYPE;
    private Builder requestBuilder ; 
    private OkHttpClient.Builder clientBuilder;
    private OkHttpClient client ;
    
    public HttpClient(){
        clientBuilder = new OkHttpClient.Builder();
        MEDIA_TYPE = MediaType.parse("text/json; charset=utf-8");
        requestBuilder = new Request.Builder();
    }

    public HttpClient(String mediaType){
        clientBuilder = new OkHttpClient.Builder();
        MEDIA_TYPE = MediaType.parse(mediaType);
        requestBuilder = new Request.Builder();
    }

    public HttpClient withUrl(String url){
         requestBuilder.url(url);
         return this;
    }

    public HttpClient addQueryParam(String param, String value){
        requestBuilder.url(requestBuilder.getUrl$okhttp()
                                            .newBuilder()
                                            .addQueryParameter(param, value)
                                            .build().toString()
                            );
        return this;
    }

    public HttpClient withTag(Object tagObject){
        requestBuilder.tag(tagObject);
        return this;
    }

    public HttpClient withHeader(String name, String value){
        requestBuilder.addHeader(name, value);
        return this;
    }

    public HttpClient authenticateBasic(String username, String password){
        String credential = Credentials.basic(username, password);
        requestBuilder.addHeader("Authorization", credential);
        return this;
    }

    public HttpClient withMultipleHeaders(Map<String, String> headers){
        for(Map.Entry<String,String> entry : headers.entrySet()){
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public HttpClient post(String requestBody){
        requestBuilder.post(RequestBody.create(requestBody,MEDIA_TYPE));
        return this;
    }

    public HttpClient put(String requestBody){
        requestBuilder.put(RequestBody.create(requestBody,MEDIA_TYPE));
        return this;
    }

    public HttpClient delete(String requestBody){
        requestBuilder.delete(RequestBody.create(requestBody,MEDIA_TYPE));
        return this;
    }

    public String sendRequest(){
        Request request = requestBuilder.build();
        client =  clientBuilder.build();
        try (Response response = client.newCall(request).execute()){
            return response.body().string();
        }
        catch(SocketTimeoutException e){
                System.out.println("Request got timed out");
        }
        catch(IOException e){
            if(e.getMessage() == "Canceled")
                System.out.println("Request got canceled");
            else{
                e.printStackTrace();
            }
        }
        return "IOException";
    }

    public CompletableFuture<String> sendAsyncRequest(){
        Request request = requestBuilder.build();
        client =  clientBuilder.build();
        CompletableFuture<String> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                if(e instanceof SocketTimeoutException){
                    System.out.println("Request got timed out");
                    future.complete("IOException");
                }
                else{
                    if(e.getMessage() == "Canceled"){
                        System.out.println("Request got canceled");
                        future.complete("IOException");
                    } 
                    else{
                        future.completeExceptionally(e);
                    }
                }
            }
      
            @Override public void onResponse(Call call, Response response) throws IOException {
              
                if (!response.isSuccessful()){
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
                future.complete(response.body().string());
              
            }
          });
          return future;
    }

    public HttpClient withProgress(){
        clientBuilder.addNetworkInterceptor(chain -> {
           Response originalResponse = chain.proceed(chain.request());
           return originalResponse.newBuilder()
               .body(new ProgressResponseBody(originalResponse.body(), new ProgressListenerImpl()))
               .build();
         });

         return this;
   }

    public HttpClient withMaxRetries(int maxRetries){
        clientBuilder.addInterceptor(new RetryInterceptor(maxRetries));
        return this;
    }

    public HttpClient authenticateIfRequired(String username, String password){
        clientBuilder.authenticator(new Authenticator() {
            @Override public Request authenticate(Route route, Response response) throws IOException {
              if (response.request().header("Authorization") != null) {
                return null; // Give up, we've already attempted to authenticate.
              }
  
              System.out.println("Authenticating for response: " + response);
              System.out.println("Challenges: " + response.challenges());
              String credential = Credentials.basic(username, password);
              return response.request().newBuilder()
                  .header("Authorization", credential)
                  .build();
            }
          });
        return this;
    }

    public HttpClient addInterceptor(com.example.demo.http.interceptors.Interceptor interceptor){
        clientBuilder.addInterceptor(interceptor);
        return this;
    }

   public HttpClient withTimeOuts(int timeOut){
    clientBuilder.readTimeout(timeOut, TimeUnit.SECONDS);
    clientBuilder.writeTimeout(timeOut, TimeUnit.SECONDS);
    clientBuilder.connectTimeout(timeOut, TimeUnit.SECONDS);
    return this;
   }

    public void cancelAllRequests(){
        if(client != null)
            client.dispatcher().cancelAll();
    }

    public void cancelRequest(Object tagObject){
        if(client != null){
            for(Call call : client.dispatcher().queuedCalls()) {
                if(call.request().tag().equals(tagObject)){
                    call.cancel();
                }
            }
    
            for(Call call : client.dispatcher().runningCalls()) {
                if(call.request().tag().equals(tagObject)){
                    call.cancel();
                }
            }
        }
    }
}
