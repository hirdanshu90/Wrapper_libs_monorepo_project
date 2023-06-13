package com.accenture.goss.caching;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.PolyNull;

import com.accenture.goss.caching.Exceptions.CacheNotBuiltException;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Weigher;

public class CachingClient<K,V> {
    private Caffeine<Object,Object> caffeine;
    private LoadingCache<K,V> loadingCache;
    private AsyncLoadingCache<K,V> asyncLoadingCache;

    public CachingClient() {
        caffeine = Caffeine.newBuilder();
    }

    public CachingClient<K,V> withMaxSize(int size){
        caffeine.maximumSize(size);
        return this;
    }

    public CachingClient<K,V> withMaxWeight(int weight){
        caffeine.maximumWeight(weight);
        return this;
    }

    public CachingClient<K,V> withWeigher(Weigher<? super K, ? super V> weigher){
        caffeine.weigher(weigher);
        return this;
    }

    public CachingClient<K,V> withWriteExpiryLimit(int timeInSeconds){
        caffeine.expireAfterWrite(timeInSeconds, TimeUnit.SECONDS);
        return this;
    }

    public CachingClient<K,V> withAccessExpiryLimit(int timeInSeconds){
        caffeine.expireAfterAccess(timeInSeconds, TimeUnit.SECONDS);
        return this;
    }

    public CachingClient<K,V> enableWeakKeys(){
        caffeine.weakKeys();
        return this;
    }

    public CachingClient<K,V> enableWeakValues(){
        caffeine.weakValues();
        return this;
    }

    public CachingClient<K,V> enableSoftValues(){
        caffeine.softValues();
        return this;
    }

    public CachingClient<K,V> buildCache(CacheLoader<? super K, V> loader){
        loadingCache = caffeine.build(loader);
        return this;
    }

    public CachingClient<K,V> buildAsyncCache(CacheLoader<? super K, V> loader){
        asyncLoadingCache = caffeine.buildAsync(loader);
        return this;
    }

    public void cleanUp() throws CacheNotBuiltException{
        if(loadingCache == null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }
        loadingCache.cleanUp();
    }

    public void put(K key, V value) throws CacheNotBuiltException{
        if(loadingCache == null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }
        loadingCache.put(key,value);
    }

    public void putAsunc(K key, CompletableFuture<? extends V> valueFuture) throws CacheNotBuiltException{
        if(loadingCache == null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }
        asyncLoadingCache.put(key,valueFuture);
    }

    public V get(K key) throws CacheNotBuiltException{
        if(loadingCache == null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }
        return loadingCache.get(key);
    }

    public V get(K key, Function<? super K, ? extends V> mappingFunction) throws CacheNotBuiltException{
        if(loadingCache == null){
            throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");
        }
        return loadingCache.get(key, mappingFunction);
    }

    public CompletableFuture<V> getAsync(K key) throws CacheNotBuiltException{
        if(asyncLoadingCache == null){
            throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");
        }
        return asyncLoadingCache.get(key);
    }

    public long cacheSize(){
        return loadingCache.estimatedSize();
    }

}
