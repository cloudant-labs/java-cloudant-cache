package com.cloudant.client.cache;

import java.util.List;
import java.util.Map;

/**
 * @author ArunIyengar
 */

/*
 * Interface for cache
 */
public interface Cache<K, V> {

    /**
     * delete all entries from the cache
     */
    void clear();

    /**
     * delete a key-value pair from the cache
     *
     * @param key key corresponding to value
     */
    void delete(K key);

    /**
     * delete one or more key-value pairs from the cache
     *
     * @param keys iterable data structure containing the keys to delete
     */
    void deleteAll(List<K> keys);

    /**
     * look up a value in the cache
     *
     * @param key key corresponding to value
     * @return value corresponding to key, null if key is not in cache or if
     * value is expired
     */
    V get(K key);

    /**
     * look up one or more values in the cache. Don't return expired values.
     *
     * @param keys iterable data structure containing the keys to look up
     * @return map containing key-value pairs corresponding to unexpired data in
     * the cache
     */
    Map<K, V> getAll(List<K> keys);

    /**
     * get cache statistics
     *
     * @return data structure containing statistics
     */
    Stats getStatistics();

    /**
     * cache a key-value pair
     *
     * @param key   key associated with value
     * @param value value associated with key
     */
    void put(K key, V value);

    /**
     * cache one or more key-value pairs
     *
     * @param map map containing key-value pairs to cache
     */
    void putAll(Map<K, V> map);

    /**
     * @return number of objects in cache
     */
    long size();

}
