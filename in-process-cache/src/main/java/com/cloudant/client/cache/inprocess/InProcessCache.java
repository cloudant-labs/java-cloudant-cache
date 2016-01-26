/*
 * Copyright (c) 2016 IBM Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.cloudant.client.cache.inprocess;

import com.cloudant.client.cache.CacheEntry;
import com.cloudant.client.cache.CacheWithLifetimes;
import com.cloudant.client.cache.Util;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This cache implementation stores data in the same process as the executing program.
 *
 * @author ArunIyengar
 */
public class InProcessCache<K, V> implements CacheWithLifetimes<K, V> {

    private LoadingCache<K, CacheEntry<V>> cache;
    private long defaultLifetime;  // default object lifetime in millisecods

    /**
     * Construct a new instance.
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
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        cache.invalidateAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(K key) {
        cache.invalidate(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(List<K> keys) {
        cache.invalidateAll(keys);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public CacheEntry<V> getCacheEntry(K key) {
        return cache.getIfPresent(key);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public void put(K key, V value) {
        put(key, value, defaultLifetime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(K key, V value, long lifetime) {
        CacheEntry<V> cacheEntry = new CacheEntry<V>(value, lifetime
                + Util.getTime());
        cache.put(key, cacheEntry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<K, V> map) {
        putAll(map, defaultLifetime);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return cache.size();
    }

    /**
     * Return contents of entire cache in a string.
     *
     * @return string containing output
     */
    public String toString() {
        Map<K, CacheEntry<V>> cacheMap = cache.asMap();
        StringBuilder result = new StringBuilder("\nContents of Entire Cache\n\n");
        for (Map.Entry<K, CacheEntry<V>> entry : cacheMap.entrySet()) {
            result.append("Key: " + entry.getKey() + "\n");
            CacheEntry<V> cacheEntry = entry.getValue();
            if (cacheEntry == null) {
                result.append("CacheEntry is null\n");
            } else {
                result.append(cacheEntry.toString());
            }
            result.append("\n\n");
        }
        result.append("Cache size is: " + size() + "\n");
        return result.toString();
    }

}
