package com.accenture.goss.caching;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.accenture.goss.caching.Exceptions.CacheNotBuiltException;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Weigher;


/**
 * CachingClient is a wrapper class that wraps Caffeine Library and simplifies
 * creating sync/async cache.
 * It stores both cache builder and the caches. It stores key-value pair and
 * supports sync and async retrieval and loading.
 * 
 * <p>
 * It can configure Maximum size, Maximum weight, Eviction based on write or access time.
 * It allows to manually put data in cache and evict them as well.
 */
public class CachingClient<K,V> {
    private Caffeine<Object,Object> caffeine;
    private LoadingCache<K,V> loadingCache;
    private AsyncLoadingCache<K,V> asyncLoadingCache;

    public CachingClient() {
        caffeine = Caffeine.newBuilder();
    }

    /**
     * This sets Maximum size for cache
     */
    public CachingClient<K,V> withMaxSize(int size){
        caffeine.maximumSize(size);
        return this;
    }

    /**
     * This sets Maximum weight for cache
     */
    public CachingClient<K,V> withMaxWeight(int weight){
        caffeine.maximumWeight(weight);
        return this;
    }

    /**
     * This sets Weigher function to calculate weight of cache
     */
    public CachingClient<K,V> withWeigher(Weigher<? super K, ? super V> weigher){
        caffeine.weigher(weigher);
        return this;
    }

    /**
     * This sets Write Expiry limit in seconds for cache
     */
    public CachingClient<K,V> withWriteExpiryLimit(int timeInSeconds){
        caffeine.expireAfterWrite(timeInSeconds, TimeUnit.SECONDS);
        return this;
    }

    /**
     * This sets Access Expiry limit in seconds for cache
     */
    public CachingClient<K,V> withAccessExpiryLimit(int timeInSeconds){
        caffeine.expireAfterAccess(timeInSeconds, TimeUnit.SECONDS);
        return this;
    }

    /**
     * This enable weak references of Keys in the data structure, making it eligible for GC
     */
    public CachingClient<K,V> enableWeakKeys(){
        caffeine.weakKeys();
        return this;
    }

    /**
     * This enable weak references of Values in the data structure, making it eligible for GC
     */
    public CachingClient<K,V> enableWeakValues(){
        caffeine.weakValues();
        return this;
    }

    /**
     * This enable soft references of Values in the data structure, making it eligible for GC
     */
    public CachingClient<K,V> enableSoftValues(){
        caffeine.softValues();
        return this;
    }

    /**
     * This builds the actual sync loading cache with a loader function
     */
    public CachingClient<K,V> buildCache(CacheLoader<? super K, V> loader){
        loadingCache = caffeine.build(loader);
        return this;
    }

    /**
     * This builds the actual async loading cache with a loader function
     */
    public CachingClient<K,V> buildAsyncCache(CacheLoader<? super K, V> loader){
        asyncLoadingCache = caffeine.buildAsync(loader);
        return this;
    }

    /**
     * This triggers clean up of loading cache 
     * @throws CacheNotBuiltException if {@code cache} is not built explicitly
     */
    public void cleanUp() throws CacheNotBuiltException{
        if(loadingCache == null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }
        loadingCache.cleanUp();
    }

    /**
     * This enters a key value pair in loading cache 
     * @throws CacheNotBuiltException if {@code cache} is not built explicitly
     */
    public void put(K key, V value) throws CacheNotBuiltException{
        if(loadingCache == null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }
        loadingCache.put(key,value);
    }

    /**
     * This enters a key value pair in async loading cache 
     * @throws CacheNotBuiltException if {@code cache} is not built explicitly
     */
    public void putAsync(K key, CompletableFuture<? extends V> valueFuture) throws CacheNotBuiltException{
        if(asyncLoadingCache == null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }
        asyncLoadingCache.put(key,valueFuture);
    }

    /**
     * This retrieves a key value pair from loading cache
     * @throws CacheNotBuiltException if {@code cache} is not built explicitly 
     */
    public V get(K key) throws CacheNotBuiltException{
        if(loadingCache == null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }
        return loadingCache.get(key);
    }

    /**
     * This retrieves a key value pair from loading cache, if not present, loads it using the mapper function
     * @throws CacheNotBuiltException if {@code cache} is not built explicitly
     */
    public V get(K key, Function<? super K, ? extends V> mappingFunction) throws CacheNotBuiltException{
        if(loadingCache == null){
            throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");
        }
        return loadingCache.get(key, mappingFunction);
    }

    /**
     * This retrieves a key value pair from async loading cache 
     * @throws CacheNotBuiltException if {@code cache} is not built explicitly
     */
    public CompletableFuture<V> getAsync(K key) throws CacheNotBuiltException{
        if(asyncLoadingCache == null){
            throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");
        }
        return asyncLoadingCache.get(key);
    }

    /**
     * This returns cache size 
     */
    public long cacheSize(){
        if(loadingCache != null){
            return loadingCache.estimatedSize();
        }
        if (asyncLoadingCache != null){
            return asyncLoadingCache.synchronous().estimatedSize();
        }
        return 0L;
    }

    /**
     * This removes a key value pair from cache
     * @throws CacheNotBuiltException if {@code cache} is not built explicitly 
     */
    public void remove(K key) throws CacheNotBuiltException{
        if(loadingCache == null && asyncLoadingCache ==null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }

        if(loadingCache !=null)
            loadingCache.invalidate(key);
        if(asyncLoadingCache !=null)
            asyncLoadingCache.synchronous().invalidate(key);
    }

    /**
     * This removes all key value pairs from cache 
     * @throws CacheNotBuiltException if {@code cache} is not built explicitly
     */
    public void removeAll() throws CacheNotBuiltException{
        if(loadingCache == null && asyncLoadingCache ==null){
          throw new CacheNotBuiltException("Please build a cache by supplying a lambda function first");  
        }

        if(loadingCache !=null)
            loadingCache.invalidateAll();
        if(asyncLoadingCache !=null)
            asyncLoadingCache.synchronous().invalidateAll();
    }
}
