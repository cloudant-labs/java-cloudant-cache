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

package com.cloudant.client.cache.redis;


import com.cloudant.client.cache.CacheEntry;
import com.cloudant.client.cache.CacheWithLifetimes;
import com.cloudant.client.cache.Serializer;
import com.cloudant.client.cache.Util;

import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ArunIyengar
 */
public class RedisCache<K, V> implements CacheWithLifetimes<K, V> {

    private Jedis cache;
    private long defaultLifetime;  // default object lifetime in millisecods


    /**
     * Create a cache with the specified lifetime connected to Redis at the specified host.
     *
     * @param host            host where Redis is running
     * @param defaultLifespan default life time in milliseconds for cached objects
     */
    public RedisCache(String host, long defaultLifespan) {
        cache = new Jedis(host);
        defaultLifetime = defaultLifespan;
    }

    /**
     * Create a cache with the specified lifetime connected to Redis at the specified host &amp;
     * port.
     *
     * @param host            host where Redis is running
     * @param port            port number
     * @param defaultLifespan default life time in milliseconds for cached objects
     */
    public RedisCache(String host, int port, long defaultLifespan) {
        cache = new Jedis(host, port);
        defaultLifetime = defaultLifespan;
    }

    /**
     * Create a cache with the specified lifetime connected to Redis at the specified host &amp;
     * port.
     * <P>
     * Configure Jedis to close idle connections after the specified time.
     * </P>
     *
     * @param host            host where Redis is running
     * @param port            port number
     * @param timeout         number of seconds before Jedis closes an idle connection
     * @param defaultLifespan default life time in milliseconds for cached objects
     */
    public RedisCache(String host, int port, int timeout, long defaultLifespan) {
        cache = new Jedis(host, port, timeout);
        defaultLifetime = defaultLifespan;
    }

    /**
     * Constructor in which already-created Jedis instance is passed in to be used as underlying
     * cache.  This constructor is for situations in which application wants access to
     * Jedis instance so that it can directly make Jedis method calls on the Jedis instance.
     *
     * @param jedisCache      Existing Jedis instance to be used as underlying cache
     * @param defaultLifespan Default life time in milliseconds for cached objects
     */
    public RedisCache(Jedis jedisCache, long defaultLifespan) {
        cache = jedisCache;
        defaultLifetime = defaultLifespan;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        cache.flushDB();
    }

    /**
     * Close the Redis connection.
     */
    public void close() {
        cache.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(K key) {
        cache.del(Serializer.serializeToByteArray(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(List<K> keys) {
        for (K key : keys) {
            cache.del(Serializer.serializeToByteArray(key));
        }
    }

    /**
     * Delete all key-value pairs from all databases
     *
     * @return status code reply
     */
    public String flushAll() {
        return cache.flushAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(K key) {
        CacheEntry<V> cacheEntry = getCacheEntry(key);
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
        Map<K, V> hashMap = new HashMap<K, V>();
        for (K key : keys) {
            V value = get(key);
            if (value != null) {
                hashMap.put(key, value);
            }
        }
        return hashMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheEntry<V> getCacheEntry(K key) {
        byte[] rawValue = cache.get(Serializer.serializeToByteArray(key));
        if (rawValue == null) {
            return null;
        }
        return Serializer.deserializeFromByteArray(rawValue);
    }

    /**
     * {@inheritDoc}
     * <P>
     * For Redis, cache statistics are contained in a string.  The string is
     * returned by {@link RedisCacheStats#getStats()}.
     * </P>
     */
    @Override
    public RedisCacheStats getStatistics() {
        return new RedisCacheStats(cache.info());
    }

    /**
     * Return string representing a cache entry corresponding to a key (or indicate if the
     * key is not in the cache).
     *
     * @param key key corresponding to value
     * @return string containing output
     */
    public String printCacheEntry(K key) {
        String result = "lookup: CacheEntry value for key: " + key + "\n";
        CacheEntry<V> cacheEntry = getCacheEntry(key);
        if (cacheEntry == null) {
            result += "Key " + key + " not in cache" + "\n";
        } else {
            result += cacheEntry.toString();
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
        put(key, cacheEntry);
    }

    private void put(K key, CacheEntry<V> cacheEntry) {
        byte[] array1 = Serializer.serializeToByteArray(key);
        byte[] array2 = Serializer.serializeToByteArray(cacheEntry);
        cache.set(array1, array2);

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
            put(entry.getKey(), cacheEntry);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return cache.dbSize();
    }

    /**
     * Output contents of current database to a string.
     *
     * @return string containing output
     */
    public String toString() {
        StringBuilder result = new StringBuilder("\nContents of Entire Cache\n\n");
        StringRedisSerializer srs = new StringRedisSerializer();
        // If we know that keys are strings, we don't have to use
        // StringRedisSerializer
        Set<byte[]> keys = cache.keys(srs.serialize("*"));
        for (byte[] key : keys) {
            String keyString = Serializer.deserializeFromByteArray(key);
            result.append("Key: " + keyString + "\n");
            byte[] rawValue = cache.get(key);
            if (rawValue == null) {
                result.append("No value found in cache for keyString " + keyString + "\n\n");
                continue;
            }
            CacheEntry<V> cacheEntry = Serializer.deserializeFromByteArray(rawValue);
            if (cacheEntry == null) {
                result.append("CacheEntry is null for keyString " + keyString + "\n\n");
                continue;
            }
            result.append(cacheEntry.toString() + "\n\n");
        }
        result.append("Cache size is: " + size() + "\n");
        return result.toString();
    }

}
