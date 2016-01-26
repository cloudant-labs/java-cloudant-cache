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

import java.util.Map;

/**
 * @author ArunIyengar
 */
public interface CacheWithLifetimes<K, V> extends Cache<K, V> {
    /**
     * Cache a key-value pair.
     *
     * @param key      key associated with value
     * @param value    value associated with key
     * @param lifetime lifetime in milliseconds associated with data
     */
    void put(K key, V value, long lifetime);

    /**
     * Cache one or more key-value pairs.
     *
     * @param map      map containing key-value pairs to cache
     * @param lifetime lifetime in milliseconds associated with each key-value pair.
     *                 If the system supports revalidation of expired cache entries to determine if
     *                 expired entries are really obsolete, a value {@code <= 0} indicates cached
     *                 entry should always be revalidated before being returned to client
     */
    void putAll(Map<K, V> map, long lifetime);

    /**
     * Look up a CacheEntry in the cache. The CacheEntry may correspond to
     * expired data. This method can be used to revalidate cached objects whose
     * expiration times have passed.
     *
     * @param key key corresponding to value
     * @return value corresponding to key (may be expired), {@code null} if key is not
     * in cache
     */
    CacheEntry<V> getCacheEntry(K key);
}
