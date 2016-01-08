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

package com.cloudant.client.cache.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.cloudant.client.cache.CacheWithLifetimes;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ArunIyengar
 */
public abstract class CacheTests {

    protected static final int DEFAULT_EXPIRATION = 6000;
    protected static final int DEFAULT_NUM_OBJECTS = 2000;

    protected final CacheWithLifetimes<String, Integer> cacheWithLifetimes;

    protected CacheTests(CacheWithLifetimes<String, Integer> cacheWithLifetimes) {
        this.cacheWithLifetimes = cacheWithLifetimes;
    }

    @Before
    public void clearCache() {
        cacheWithLifetimes.clear();
    }

    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";
    long lifetime = 3000;

    @Test
    public void testPutGetGetStatistics() {
        cacheWithLifetimes.clear();
        cacheWithLifetimes.put(key1, 42, lifetime);
        cacheWithLifetimes.put(key2, 43, lifetime);
        cacheWithLifetimes.put(key3, 44, lifetime);
        assertEquals("Cache size should be 3", 3, cacheWithLifetimes.size());
    }

    @Test
    public void testClear() {
        cacheWithLifetimes.clear();
        cacheWithLifetimes.put(key1, 42, lifetime);
        cacheWithLifetimes.put(key2, 43, lifetime);
        cacheWithLifetimes.put(key3, 44, lifetime);
        assertEquals("Cache size should be 3", 3, cacheWithLifetimes.size());
        cacheWithLifetimes.clear();
        assertEquals("Cache size should be 0", 0, cacheWithLifetimes.size());
    }

    @Test
    public void testDelete() {
        cacheWithLifetimes.clear();
        cacheWithLifetimes.put(key1, 42, lifetime);
        cacheWithLifetimes.put(key2, 43, lifetime);
        cacheWithLifetimes.put(key3, 44, lifetime);
        assertEquals("Cache size should be 3", 3, cacheWithLifetimes.size());
        cacheWithLifetimes.delete(key2);
        assertEquals("Cache size should be 2", 2, cacheWithLifetimes.size());
        cacheWithLifetimes.put(key2, 50, lifetime);
        cacheWithLifetimes.put("key4", 59, lifetime);
        cacheWithLifetimes.put("key5", 80, lifetime);

        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        cacheWithLifetimes.deleteAll(list);
        assertEquals("Cache size should be 3", 3, cacheWithLifetimes.size());
        cacheWithLifetimes.delete("adjkfjadfjdf");
        cacheWithLifetimes.delete("adfkasdklfjil");
        assertEquals("Cache size should be 3", 3, cacheWithLifetimes.size());
    }

    @Test
    public void testPutAll() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(key1, 42);
        map.put(key2, 43);
        map.put(key3, 44);
        cacheWithLifetimes.clear();
        cacheWithLifetimes.putAll(map, lifetime);
        assertEquals("Cache size should be 3", 3, cacheWithLifetimes.size());
    }

    @Test
    public void testGetAll() {
        cacheWithLifetimes.put(key1, 42, lifetime);
        cacheWithLifetimes.put(key2, 43, lifetime);
        cacheWithLifetimes.put(key3, 44, lifetime);
        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        list.add(key3);
        Map<String, Integer> map = cacheWithLifetimes.getAll(list);
        assertEquals("Returned map size should be 3", 3, map.size());
    }

    @Test
    public void testUpdate() {
        Integer val1;

        cacheWithLifetimes.put(key1, 42, lifetime);
        val1 = cacheWithLifetimes.get(key1);
        assertEquals("Val1 should be 42, actual value is " + val1, 42, val1.intValue());
        cacheWithLifetimes.put(key1, 43, lifetime);
        val1 = cacheWithLifetimes.get(key1);
        assertEquals("Val1 should be 43, actual value is " + val1, 43, val1.intValue());
        cacheWithLifetimes.put(key1, 44, lifetime);
        val1 = cacheWithLifetimes.get(key1);
        assertEquals("Val1 should be 44, actual value is " + val1, 44, val1.intValue());
    }

    @Test
    public void testExpiration() {
        long lifespan = 1000;
        Integer val1;

        cacheWithLifetimes.clear();
        val1 = cacheWithLifetimes.get(key1);
        assertNull("Val1 should be null, value is " + val1, val1);
        cacheWithLifetimes.put(key1, 42, lifespan);
        val1 = cacheWithLifetimes.get(key1);
        assertNotNull("Val1 should not be null, value is " + val1, val1);
        try {
            Thread.sleep(lifespan + 200);
        } catch (Exception e) {
        }
        val1 = cacheWithLifetimes.get(key1);
        assertNull("Val1 should be null, value is " + val1, val1);
    }

}
