package com.accenture.goss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.accenture.goss.http.HttpClient;
import com.accenture.goss.http.interceptors.SampleInterceptor;

@SpringBootTest
public class HttpClientTests {
    HttpClient client = new HttpClient();

    String requestBody = """
            {
                "userId": 1,
                "id": 1,
                "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            }
            """;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Test
    void testGetandProgress() {
        String url = "https://publicobject.com/helloworld.txt";
        String response = client.withUrl(url).withProgress().sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("Test Get");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
    }

    @Test
    void testAsyncGet() {
        String url = "https://publicobject.com/helloworld.txt";
        CompletableFuture<String> future = client.withUrl(url).sendAsyncRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("Test Async Get");
        future.thenAccept(response -> System.out.println(response));
        System.out.println(future);
        System.out.println("-----------------------------------------------------");
    }

    @Test
    void testQueryParams() {
        String url = "https://jsonplaceholder.typicode.com/posts";
        String response = client.withUrl(url).addQueryParam("id", "1").sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("Test adding Query Params");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
    }

    @Test
    void testTimeout_Succeeds() {
        String url = "http://httpbin.org/delay/2";
        String response = client.withUrl(url).withTimeOuts(10).sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("Test TimeOut");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
    }

    @Test
    void testTimeout_Fails() {
        String url = "http://httpbin.org/delay/4";
        String response = client.withUrl(url).withTimeOuts(2).sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("Test TimeOut 2");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertEquals("IOException", response);
    }

    @Test
    void testPost() {
        String url = "https://jsonplaceholder.typicode.com/posts";
        String response = client.withUrl(url).post(requestBody).sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
    }

    @Test
    void testPut() {
        String url = "https://jsonplaceholder.typicode.com/posts/1";
        String response = client.withUrl(url).put(requestBody).sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
    }

    @Test
    void testDelete() {
        String url = "https://jsonplaceholder.typicode.com/posts/1";
        String response = client.withUrl(url).delete(requestBody).sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
    }

    @Test
    void testInterceptor() {
        String url = "https://publicobject.com/helloworld.txt";
        String response = client.withUrl(url)
                .addInterceptor(new SampleInterceptor())
                .sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("testInterceptor");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
    }

    @Test
    void testBasicAuthenticator() {
        String url = "https://publicobject.com/secrets/hellosecret.txt";
        String response = client.withUrl(url)
                .authenticateBasic("jesse", "password1")
                .sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("testBasicAuthenticator");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
    }

    @Test
    void testAuthenticator() {
        String url = "https://publicobject.com/secrets/hellosecret.txt";
        String response = client.withUrl(url)
                .authenticateIfRequired("jesse", "password1")
                .sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("testAuthenticator");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
    }

    @Test
    public void cancelTest() throws Exception {
        Object tagObject = new Object();

        final long startNanos = System.nanoTime();

        // Schedule a job to cancel the call in 1 second.
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.printf("%.2f Canceling call.%n", (System.nanoTime() - startNanos) / 1e9f);
                client.cancelRequest(tagObject);
                System.out.printf("%.2f Canceled call.%n", (System.nanoTime() - startNanos) / 1e9f);
            }
        }, 1, TimeUnit.SECONDS);

        System.out.printf("%.2f Executing call.%n", (System.nanoTime() - startNanos) / 1e9f);
        String response = client.withUrl("http://httpbin.org/delay/2").withTag(tagObject).sendRequest();
        // System.out.printf("%.2f Call was expected to fail, but completed: %s%n",
        // (System.nanoTime() - startNanos) / 1e9f, response);
        System.out.println("-----------------------------------------------------");
        System.out.println("testCancel");
        System.out.println(response);
        System.out.println("-----------------------------------------------------");

        assertEquals("IOException", response);
    }

}
