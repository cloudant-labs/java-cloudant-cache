/**
 *
 */
package client;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author ArunIyengar
 */

/*
 * This cache implementation stores data in the same process as the executing program
 */
public class InProcessCache<K, V> implements CacheWithLifetimes<K, V> {

    private LoadingCache<K, CacheEntry<V>> cache;
    private long defaultLifetime;  // default object lifetime in millisecods

    /**
     * Constructor
     *
     * @param maxObjects      maximum number of objects which can be stored before
     *                        replacement starts
     * @param defaultLifespan Default life time in milliseconds for cached objects
     */
    public InProcessCache(long maxObjects, long defaultLifespan) {
        cache = CacheBuilder.newBuilder().maximumSize(maxObjects)
                .build(new CacheLoader<K, CacheEntry<V>>() {
                    public CacheEntry<V> load(K key) throws Exception {
                        return null;
                    }
                });
        defaultLifetime = defaultLifespan;

    }

    /**
     * delete all key-value pairs from the cache
     */
    @Override
    public void clear() {
        cache.invalidateAll();
    }

    /**
     * delete a key-value pair from the cache
     *
     * @param key key corresponding to value
     */
    @Override
    public void delete(K key) {
        cache.invalidate(key);
    }

    /**
     * delete one or more key-value pairs from the cache
     *
     * @param keys iterable data structure containing the keys to delete
     */
    @Override
    public void deleteAll(List<K> keys) {
        cache.invalidateAll(keys);
    }

    /**
     * look up a value in the cache
     *
     * @param key key corresponding to value
     * @return value corresponding to key, null if key is not in cache or if
     * value is expired
     */
    @Override
    public V get(K key) {
        CacheEntry<V> cacheEntry = cache.getIfPresent(key);
        if (cacheEntry == null) {
            return null;
        }
        if (cacheEntry.getExpirationTime() >= Util.getTime()) {
            return cacheEntry.getValue();
        }
        return null;
    }

    /**
     * look up one or more values in the cache. Don't return expired values.
     *
     * @param keys iterable data structure containing the keys to look up
     * @return map containing key-value pairs corresponding to unexpired data in
     * the cache
     */
    @Override
    public Map<K, V> getAll(List<K> keys) {
        Map<K, CacheEntry<V>> cacheMap = cache.getAllPresent(keys);
        Map<K, V> hashMap = new HashMap<K, V>();
        long currentTime = Util.getTime();

        for (Map.Entry<K, CacheEntry<V>> entry : cacheMap.entrySet()) {
            CacheEntry<V> cacheEntry = entry.getValue();
            if (cacheEntry.getExpirationTime() >= currentTime) {
                hashMap.put(entry.getKey(), cacheEntry.getValue());
            }
        }
        return hashMap;
    }

    /**
     * look up a CacheEntry in the cache. The CacheEntry may correspond to
     * expired data. This method can be used to revalidate cached objects whose
     * expiration times have passed
     *
     * @param key key corresponding to value
     * @return value corresponding to key (may be expired), null if key is not
     * in cache
     */
    @Override
    public CacheEntry<V> getCacheEntry(K key) {
        return cache.getIfPresent(key);
    }

    /**
     * get cache statistics
     *
     * @return data structure containing statistics
     */
    @Override
    public InProcessCacheStats getStatistics() {
        return new InProcessCacheStats(cache.stats());
    }


    /**
     * Return string representing a cache entry corresponding to a key (or indicate if the
     * key is not in the cache).
     *
     * @param key key corresponding to value
     * @return string containing output
     */
    public String printCacheEntry(K key) {
        String result = "printCacheEntry: CacheEntry value for key: " + key + "\n";
        CacheEntry<V> cacheEntry = cache.getIfPresent(key);
        if (cacheEntry == null) {
            result = result + "Key " + key + " not in cache\n";
        } else {
            result = result + cacheEntry.toString();
        }
        return result;
    }

    /**
     * cache a key-value pair
     *
     * @param key   key associated with value
     * @param value value associated with key
     */
    @Override
    public void put(K key, V value) {
        put(key, value, defaultLifetime);
    }

    /**
     * cache a key-value pair
     *
     * @param key      key associated with value
     * @param value    value associated with key
     * @param lifetime lifetime in milliseconds associated with data
     */
    @Override
    public void put(K key, V value, long lifetime) {
        CacheEntry<V> cacheEntry = new CacheEntry<V>(value, lifetime
                + Util.getTime());
        cache.put(key, cacheEntry);
    }

    /**
     * cache one or more key-value pairs
     *
     * @param map      map containing key-value pairs to cache
     * @param value    value associated with each key-value pair
     * @param lifetime lifetime in milliseconds associated with each key-value pair
     */
    @Override
    public void putAll(Map<K, V> map) {
        putAll(map, defaultLifetime);
    }

    /**
     * cache one or more key-value pairs
     *
     * @param map      map containing key-value pairs to cache
     * @param value    value associated with each key-value pair
     * @param lifetime lifetime in milliseconds associated with each key-value pair
     */
    @Override
    public void putAll(Map<K, V> map, long lifetime) {
        long expirationTime = Util.getTime() + lifetime;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            CacheEntry<V> cacheEntry = new CacheEntry<V>(entry.getValue(),
                    expirationTime);
            cache.put(entry.getKey(), cacheEntry);
        }

    }

    /**
     * Return number of objects in cache
     */
    @Override
    public long size() {
        return cache.size();
    }

    /**
     * Return contents of entire cache in a string
     *
     * @return string containing output
     */
    public String toString() {
        Map<K, CacheEntry<V>> cacheMap = cache.asMap();
        String result = "\nContents of Entire Cache\n\n";
        for (Map.Entry<K, CacheEntry<V>> entry : cacheMap.entrySet()) {
            result = result + "Key: " + entry.getKey() + "\n";
            CacheEntry<V> cacheEntry = entry.getValue();
            if (cacheEntry == null) {
                result = result + "CacheEntry is null\n";
            } else {
                result = result + cacheEntry.toString();
            }
            result = result + "\n\n";
        }
        result = result + "Cache size is: " + size() + "\n";
        return result;
    }

}
