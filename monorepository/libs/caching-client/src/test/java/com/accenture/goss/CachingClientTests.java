package com.accenture.goss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import com.accenture.goss.caching.CachingClient;
import com.accenture.goss.caching.Exceptions.CacheNotBuiltException;

public class CachingClientTests {

    @Test
    void testGetandMaxSize() {
        CachingClient<String,String> cachingClient = new CachingClient<>();
        cachingClient = cachingClient.withMaxSize(1).buildCache(k -> "Data for "+k+": "+k);
        System.out.println(cachingClient.get("A"));
        assertEquals(1,cachingClient.cacheSize());
        System.out.println(cachingClient.get("B"));
        cachingClient.cleanUp();
        assertEquals(1,cachingClient.cacheSize());
    }

    @Test
    void testCacheNotBuiltException() {
        CachingClient<String,String> cachingClient = new CachingClient<>();
        Exception exception = assertThrows(CacheNotBuiltException.class, () -> {cachingClient.get("A");});
        String expectedMessage = "Please build a cache by supplying a lambda function first";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testPut() {
        CachingClient<String,String> cachingClient = new CachingClient<>();
        cachingClient = cachingClient.buildCache(k -> "Data for "+k+": "+k);
        System.out.println(cachingClient.get("A"));
        assertEquals(1,cachingClient.cacheSize());
        cachingClient.put("B", "Value put for B");
        System.out.println(cachingClient.get("B"));
        assertEquals("Value put for B", cachingClient.get("B"));
    }

    @Test
    void testEvictionbyWriteTime() throws InterruptedException {
        CachingClient<String,String> cachingClient = new CachingClient<>();
        cachingClient = cachingClient.withWriteExpiryLimit(5).withMaxSize(1).buildCache(k -> "Data for "+k+": "+k);
        System.out.println(cachingClient.get("A"));
        cachingClient.cleanUp();
        assertEquals(1,cachingClient.cacheSize());
        Thread.sleep(5500);
        cachingClient.cleanUp();
        assertEquals(0,cachingClient.cacheSize());
    }

    @Test
    void testEvictionbyAccessTime() throws InterruptedException {
        CachingClient<String,String> cachingClient = new CachingClient<>();
        cachingClient = cachingClient.withAccessExpiryLimit(4).withMaxSize(1).buildCache(k -> "Data for "+k+": "+k);
        System.out.println(cachingClient.get("A"));
        Thread.sleep(3500);
        cachingClient.cleanUp();
        assertEquals(1,cachingClient.cacheSize());


        cachingClient.get("A");
        Thread.sleep(1500);
        cachingClient.cleanUp();
        assertEquals(1,cachingClient.cacheSize());

        Thread.sleep(4050);
        cachingClient.cleanUp();
        assertEquals(0,cachingClient.cacheSize());
    }

    @Test
    void testRemove() {
        CachingClient<String,String> cachingClient = new CachingClient<>();
        cachingClient = cachingClient.buildCache(k -> "Data for "+k+": "+k);
        System.out.println(cachingClient.get("A"));
        assertEquals(1,cachingClient.cacheSize());
        cachingClient.remove("A");
        assertEquals(0, cachingClient.cacheSize());
    }

    @Test
    void testRemoveAll() {
        CachingClient<String,String> cachingClient = new CachingClient<>();
        cachingClient = cachingClient.buildCache(k -> "Data for "+k+": "+k);
        System.out.println(cachingClient.get("A"));
        System.out.println(cachingClient.get("B"));
        assertEquals(2,cachingClient.cacheSize());
        cachingClient.removeAll();
        assertEquals(0, cachingClient.cacheSize());
    }

    @Test
    void testWeakEvictionAfterGC() {
        CachingClient<String,String> cachingClient = new CachingClient<>();
        cachingClient = cachingClient.enableWeakKeys().enableWeakValues().buildCache(k -> "Data for "+k+": "+k);
        System.out.println(cachingClient.get("A"));
        System.out.println(cachingClient.get("B"));
        assertEquals(2,cachingClient.cacheSize());
        System.gc();
        cachingClient.cleanUp();
        assertEquals(0, cachingClient.cacheSize());
    }

    @Test
    void testAsyncCache(){
        CachingClient<String,String> cachingClient = new CachingClient<>();
        cachingClient = cachingClient.buildAsyncCache(k -> "Data for "+k+": "+k);
        CompletableFuture<String> future = cachingClient.getAsync("AB");
        // future.thenAccept(cachedResponse -> System.out.println(cachedResponse));
        // System.out.println(future.get());
        future.thenAccept(cachedResponse -> assertEquals("Data for AB: AB",cachedResponse));
    }

    @Test
    void testAsyncRemoval(){
        CachingClient<String,String> cachingClient = new CachingClient<>();
        cachingClient = cachingClient.buildAsyncCache(k -> "Data for "+k+": "+k);
        CompletableFuture<String> future1 = cachingClient.getAsync("AB");
        CompletableFuture<String> future2 = cachingClient.getAsync("CD");
        assertEquals(2, cachingClient.cacheSize());
        cachingClient.remove("AB");
        assertEquals(1, cachingClient.cacheSize());
        cachingClient.removeAll();
        assertEquals(0, cachingClient.cacheSize());
    }
}
