package client;

import java.util.List;
import java.util.Map;

/**
 * @author ArunIyengar 
 * 
 */

/*
 * Interface for cache
 */
public interface Cache<K, V> {

    /**
     * delete all entries from the cache
     * 
     * */
    public void clear();

    /**
     * delete a key-value pair from the cache
     * 
     * @param key
     *            key corresponding to value
     * 
     * */
    public void delete(K key);

    /**
     * delete one or more key-value pairs from the cache
     * 
     * @param keys
     *            iterable data structure containing the keys to delete
     * 
     * */
    public void deleteAll(List<K> keys);

    /**
     * look up a value in the cache
     * 
     * @param key
     *            key corresponding to value
     * @return value corresponding to key, null if key is not in cache or if
     *         value is expired
     * 
     * */
    public V get(K key);

    /**
     * look up one or more values in the cache. Don't return expired values.
     * 
     * @param keys
     *            iterable data structure containing the keys to look up
     * @return map containing key-value pairs corresponding to unexpired data in
     *         the cache
     * 
     * */
    public Map<K, V> getAll(List<K> keys);

    /**
     * look up a CacheEntry in the cache. The CacheEntry may correspond to
     * expired data. This method can be used to revalidate cached objects whose
     * expiration times have passed
     * 
     * @param key
     *            key corresponding to value
     * @return value corresponding to key (may be expired), null if key is not
     *         in cache
     * 
     * */
    public CacheEntry<V> getCacheEntry(K key);

    /**
     * get cache statistics
     * 
     * @return data structure containing statistics
     * 
     * */
    public Stats getStatistics();

    /**
     * cache a key-value pair
     * 
     * @param key
     *            key associated with value
     * @param value
     *            value associated with key
     * 
     * */
    public void put(K key, V value);

    /**
     * cache one or more key-value pairs
     * 
     * @param map
     *            map containing key-value pairs to cache
     * @param value
     *            value associated with each key-value pair
     * 
     * */
    public void putAll(Map<K, V> map);

    /**
     * Return number of objects in cache
     * 
     * */
    public long size();

}
