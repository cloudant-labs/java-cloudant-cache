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

package com.cloudant.client.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A Least Recently Used (LRU) cache. New objects are added to the cache up to the capacity after
 * which any further additions will result in the removal of the key-value pair that was accessed
 * least recently.
 *
 * @param <K> the type of the cache keys
 * @param <V> the type of the cache values
 */
public class LRUCache<K, V> implements Cache<K, V> {

    /*
    * LRU map of database object instances.
    * Use the default load factor of 0.75 and calculate initial capacity to avoid rehashing
    * and minimise clashes.
    * This is an access ordered map. The eldest entry is the one accessed least recently.
    */
    private final Map<K, V> lruMap;

    /**
     * Create a new LRUCache with the specified capacity.
     *
     * @param cacheCapacity maximum number of entries to store
     */
    public LRUCache(final int cacheCapacity) {
        this.lruMap = Collections.synchronizedMap(new LinkedHashMap<K, V>(
                (cacheCapacity * 4 / 3) + 1, 0.75f, true) {

            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > cacheCapacity;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        lruMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(K key) {
        lruMap.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(List<K> keys) {
        keys.stream().forEach(key -> lruMap.remove(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(K key) {
        return lruMap.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<K, V> getAll(List<K> keys) {
        synchronized (lruMap) {
            return lruMap.entrySet().parallelStream().filter(e -> keys.contains(e.getKey())).collect
                    (Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    /**
     * This cache implementation does not provide any statistics. Calling {@link Stats#getStats()
     * } will return {@code null}.
     *
     * @return a Stats implementation that always returns {@code null} for calls to getStats()
     */
    @Override
    public Stats<Void> getStatistics() {
        return () -> null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(K key, V value) {
        lruMap.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<K, V> map) {
        lruMap.putAll(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return lruMap.size();
    }

}
