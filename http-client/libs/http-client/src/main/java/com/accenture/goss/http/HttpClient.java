package com.accenture.goss.http;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.accenture.goss.http.interceptors.RetryInterceptor;
import com.accenture.goss.http.progress.ProgressListenerImpl;
import com.accenture.goss.http.progress.ProgressResponseBody;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.Request.Builder;

/**
 * HttpClient is a wrapper class that wraps OKHttp Library and simplifies
 * creating a client and a request.
 * It returns string as response. It can be used to send both sync and async
 * requests.
 * It supports GET, POST, PUT and DELETE requests.
 * <p>
 * It can configure Authorization, Request Headers, URL, Query Params, Timeouts.
 * It can cancel both sync/async requests, add interceptors and show progress of
 * operations.
 */
public class HttpClient {
    public MediaType MEDIA_TYPE;
    private Builder requestBuilder;
    private OkHttpClient.Builder clientBuilder;
    private OkHttpClient client;

    public HttpClient() {
        clientBuilder = new OkHttpClient.Builder();
        MEDIA_TYPE = MediaType.parse("text/json; charset=utf-8");
        requestBuilder = new Request.Builder();
    }

    /**
     * This sets media type, for sending Http requests
     */
    public HttpClient(String mediaType) {
        clientBuilder = new OkHttpClient.Builder();
        MEDIA_TYPE = MediaType.parse(mediaType);
        requestBuilder = new Request.Builder();
    }

    /**
     * This sets url for Http requests
     */
    public HttpClient withUrl(String url) {
        requestBuilder.url(url);
        return this;
    }

    /**
     * This sets query parameters for Http requests
     */
    public HttpClient addQueryParam(String param, String value) {
        requestBuilder.url(requestBuilder.getUrl$okhttp()
                .newBuilder()
                .addQueryParameter(param, value)
                .build().toString());
        return this;
    }

    /**
     * This sets tag for Http requests/call, can be used to identify a request/call.
     * This can be used for canceling a request/call.
     */
    public HttpClient withTag(Object tagObject) {
        requestBuilder.tag(tagObject);
        return this;
    }

    /**
     * This sets a single Header for Http requests.
     */
    public HttpClient withHeader(String name, String value) {
        requestBuilder.addHeader(name, value);
        return this;
    }

    /**
     * This sets a list of Header key and values for Http requests.
     * 
     * @param headersMap A map of Headers key and value
     */
    public HttpClient withMultipleHeaders(Map<String, String> headersMap) {
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * This sets a basic Authorization header for Http requests with username and
     * password.
     */
    public HttpClient authenticateBasic(String username, String password) {
        String credential = Credentials.basic(username, password);
        requestBuilder.addHeader("Authorization", credential);
        return this;
    }

    /**
     * This sets Http request as POST request.
     */
    public HttpClient post(String requestBody) {
        requestBuilder.post(RequestBody.create(requestBody, MEDIA_TYPE));
        return this;
    }

    /**
     * This sets Http request as PUT request.
     */
    public HttpClient put(String requestBody) {
        requestBuilder.put(RequestBody.create(requestBody, MEDIA_TYPE));
        return this;
    }

    /**
     * This sets Http request as DELETE request.
     */
    public HttpClient delete(String requestBody) {
        requestBuilder.delete(RequestBody.create(requestBody, MEDIA_TYPE));
        return this;
    }

    /**
     * This prepares request from request builder and client from client builder and
     * then sends
     * a sync request and returns String response
     */
    public String sendRequest() {
        Request request = requestBuilder.build();
        client = clientBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (SocketTimeoutException e) {
            System.out.println("Request got timed out");
        } catch (IOException e) {
            // Request got canceled by user
            if (e.getMessage() == "Canceled")
                System.out.println("Request got canceled");
            else {
                e.printStackTrace();
            }
        }
        return "IOException";
    }

    /**
     * This prepares request from request builder and client from client builder and
     * then sends
     * a sync request and returns {@link java.util.concurrent.CompletableFuture}
     * response
     */
    public CompletableFuture<String> sendAsyncRequest() {
        Request request = requestBuilder.build();
        client = clientBuilder.build();
        CompletableFuture<String> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    System.out.println("Request got timed out");
                    future.complete("IOException");
                } else {
                    if (e.getMessage() == "Canceled") {
                        // Request got canceled by user
                        System.out.println("Request got canceled");
                        future.complete("IOException");
                    } else {
                        future.completeExceptionally(e);
                    }
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
                future.complete(response.body().string());

            }
        });
        return future;
    }

    /**
     * This sets a Network Interceptor and prints the progress of a request using a
     * {@link com.example.demo.http.progress.ProgressListener}.
     */
    public HttpClient withProgress() {
        clientBuilder.addNetworkInterceptor(chain -> {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), new ProgressListenerImpl()))
                    .build();
        });

        return this;
    }

    /**
     * This sets max number of retries for a failing Http client.
     */
    public HttpClient withMaxRetries(int maxRetries) {
        clientBuilder.addInterceptor(new RetryInterceptor(maxRetries));
        return this;
    }

    /**
     * This sets basic authorization for Http client and uses those credential only
     * if required for a resource.
     */
    public HttpClient authenticateIfRequired(String username, String password) {
        clientBuilder.authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
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

    /**
     * This adds an interceptor.
     */
    public HttpClient addInterceptor(com.accenture.goss.http.interceptors.Interceptor interceptor) {
        clientBuilder.addInterceptor(interceptor);
        return this;
    }

    /**
     * This sets timeouts for a failing Http client.
     */
    public HttpClient withTimeOuts(int timeOut) {
        clientBuilder.readTimeout(timeOut, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(timeOut, TimeUnit.SECONDS);
        clientBuilder.connectTimeout(timeOut, TimeUnit.SECONDS);
        return this;
    }

    /**
     * This cancels all the ongoing and queued Http requests.
     */
    public void cancelAllRequests() {
        if (client != null)
            client.dispatcher().cancelAll();
    }

    /**
     * This cancels the Http request tagged with given tag Object.
     */
    public void cancelRequest(Object tagObject) {
        if (client != null) {
            for (Call call : client.dispatcher().queuedCalls()) {
                if (call.request().tag().equals(tagObject)) {
                    call.cancel();
                }
            }

            for (Call call : client.dispatcher().runningCalls()) {
                if (call.request().tag().equals(tagObject)) {
                    call.cancel();
                }
            }
        }
    }
}
