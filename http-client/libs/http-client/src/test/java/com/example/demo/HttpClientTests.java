package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.http.HttpClient;
import com.example.demo.http.interceptors.SampleInterceptor;

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

     @Test
	void testGetandProgress() {
		String url = "https://publicobject.com/helloworld.txt";
        String url2 = "https://jsonplaceholder.typicode.com/todos/1";
        String url3 = "https://publicobject.com/2023/04/16/read-a-project-file-in-a-kotlin-multiplatform-test/";
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
        String url2 = "https://jsonplaceholder.typicode.com/todos/1";
        String url3 = "https://publicobject.com/2023/04/16/read-a-project-file-in-a-kotlin-multiplatform-test/";
		CompletableFuture<String> future = client.withUrl(url).sendAsyncRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("Test Async Get");
        future.thenAccept(response -> System.out.println(response));
		System.out.println(future);
        System.out.println("-----------------------------------------------------");
	}

    @Test
	void testQueryParams() {
		String url = "https://publicobject.com/helloworld.txt";
        String url2 = "https://jsonplaceholder.typicode.com/posts";
        String url3 = "https://publicobject.com/2023/04/16/read-a-project-file-in-a-kotlin-multiplatform-test/";
		String response = client.withUrl(url2).addQueryParam("id", "1").sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("Test adding Query Params");
		System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
	}

    @Test
	void testTimeout_Succeeds() {
		String url = "https://publicobject.com/helloworld.txt";
        String url2 = "https://jsonplaceholder.typicode.com/todos/1";
        String url3 = "http://httpbin.org/delay/2";
		String response = client.withUrl(url3).withTimeOuts(10).sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("Test TimeOut");
		System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
	}

    @Test
	void testTimeout_Fails() {
		String url = "https://publicobject.com/helloworld.txt";
        String url2 = "https://jsonplaceholder.typicode.com/todos/1";
        String url3 = "http://httpbin.org/delay/4";
		String response = client.withUrl(url3).withTimeOuts(2).sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("Test TimeOut 2");
		System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertEquals("IOException", response);
	}

    @Test
	void testPost() {
        String url2 = "https://jsonplaceholder.typicode.com/posts";
		String response = client.withUrl(url2).post(requestBody).sendRequest();
        System.out.println("-----------------------------------------------------");
		System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
	}
    @Test
	void testPut() {
        String url2 = "https://jsonplaceholder.typicode.com/posts/1";
		String response = client.withUrl(url2).put(requestBody).sendRequest();
        System.out.println("-----------------------------------------------------");
		System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
	}

    @Test
	void testDelete() {
        String url2 = "https://jsonplaceholder.typicode.com/posts/1";
		String response = client.withUrl(url2).delete(requestBody).sendRequest();
        System.out.println("-----------------------------------------------------");
		System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
	}

    @Test
	void testInterceptor() {
        String url2 = "https://publicobject.com/helloworld.txt";
		String response = client.withUrl(url2)
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
        String url2 = "https://publicobject.com/secrets/hellosecret.txt";
		String response = client.withUrl(url2)
                            .authenticateBasic("jesse","password1")
                            .sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("testBasicAuthenticator");
		System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
	}

    @Test
	void testAuthenticator() {
        String url2 = "https://publicobject.com/secrets/hellosecret.txt";
		String response = client.withUrl(url2)
                            .authenticateIfRequired("jesse","password1")
                            .sendRequest();
        System.out.println("-----------------------------------------------------");
        System.out.println("testAuthenticator");
		System.out.println(response);
        System.out.println("-----------------------------------------------------");
        assertNotEquals("IOException", response);
	}

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Test
    public void cancelTest() throws Exception {
    Object tagObject = new Object();

    final long startNanos = System.nanoTime();

    // Schedule a job to cancel the call in 1 second.
    executor.schedule(new Runnable() {
      @Override public void run() {
        System.out.printf("%.2f Canceling call.%n", (System.nanoTime() - startNanos) / 1e9f);
        client.cancelRequest(tagObject);
        System.out.printf("%.2f Canceled call.%n", (System.nanoTime() - startNanos) / 1e9f);
      }
    }, 1, TimeUnit.SECONDS);

    System.out.printf("%.2f Executing call.%n", (System.nanoTime() - startNanos) / 1e9f);
    String response = client.withUrl("http://httpbin.org/delay/2").withTag(tagObject).sendRequest();
    // System.out.printf("%.2f Call was expected to fail, but completed: %s%n",
    //     (System.nanoTime() - startNanos) / 1e9f, response);
    System.out.println("-----------------------------------------------------");
    System.out.println("testCancel");
    System.out.println(response);
    System.out.println("-----------------------------------------------------");

    assertEquals("IOException", response);
  }

    
}
