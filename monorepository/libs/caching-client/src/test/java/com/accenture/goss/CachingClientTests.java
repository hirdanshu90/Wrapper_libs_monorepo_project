package com.accenture.goss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.accenture.goss.caching.CachingClient;
import com.accenture.goss.caching.Exceptions.CacheNotBuiltException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;

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
        //cachingClient = cachingClient.withMaxSize(1).buildCache(k -> "Data for "+k+": "+k);
        Exception exception = assertThrows(CacheNotBuiltException.class, () -> {cachingClient.get("A");});

        String expectedMessage = "Please build a cache by supplying a lambda function first";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
