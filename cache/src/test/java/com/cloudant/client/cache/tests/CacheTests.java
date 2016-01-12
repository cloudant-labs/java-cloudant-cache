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

import com.cloudant.client.cache.Cache;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ArunIyengar
 */
public abstract class CacheTests<T extends Cache<String, Integer>> {

    protected static final int DEFAULT_NUM_OBJECTS = 2000;

    protected T cache;

    protected abstract T getNewCacheInstance();

    @Before
    public void setupCache() {
        cache = getNewCacheInstance();
    }

    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";

    @Test
    public void testPutGetGetStatistics() {
        cache.clear();
        cache.put(key1, 42);
        cache.put(key2, 43);
        cache.put(key3, 44);
        assertEquals("Cache size should be 3", 3, cache.size());
    }

    @Test
    public void testClear() {
        cache.clear();
        cache.put(key1, 42);
        cache.put(key2, 43);
        cache.put(key3, 44);
        assertEquals("Cache size should be 3", 3, cache.size());
        cache.clear();
        assertEquals("Cache size should be 0", 0, cache.size());
    }

    @Test
    public void testDelete() {
        cache.clear();
        cache.put(key1, 42);
        cache.put(key2, 43);
        cache.put(key3, 44);
        assertEquals("Cache size should be 3", 3, cache.size());
        cache.delete(key2);
        assertEquals("Cache size should be 2", 2, cache.size());
        cache.put(key2, 50);
        cache.put("key4", 59);
        cache.put("key5", 80);

        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        cache.deleteAll(list);
        assertEquals("Cache size should be 3", 3, cache.size());
        cache.delete("adjkfjadfjdf");
        cache.delete("adfkasdklfjil");
        assertEquals("Cache size should be 3", 3, cache.size());
    }

    @Test
    public void testPutAll() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(key1, 42);
        map.put(key2, 43);
        map.put(key3, 44);
        cache.clear();
        cache.putAll(map);
        assertEquals("Cache size should be 3", 3, cache.size());
    }

    @Test
    public void testGetAll() {
        cache.put(key1, 42);
        cache.put(key2, 43);
        cache.put(key3, 44);
        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        list.add(key3);
        Map<String, Integer> map = cache.getAll(list);
        assertEquals("Returned map size should be 3", 3, map.size());
    }

    @Test
    public void testUpdate() {
        Integer val1;

        cache.put(key1, 42);
        val1 = cache.get(key1);
        assertEquals("Val1 should be 42, actual value is " + val1, 42, val1.intValue());
        cache.put(key1, 43);
        val1 = cache.get(key1);
        assertEquals("Val1 should be 43, actual value is " + val1, 43, val1.intValue());
        cache.put(key1, 44);
        val1 = cache.get(key1);
        assertEquals("Val1 should be 44, actual value is " + val1, 44, val1.intValue());
    }
}
