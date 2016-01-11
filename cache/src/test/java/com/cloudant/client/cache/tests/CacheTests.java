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
import com.cloudant.client.cache.InProcessCacheStats;
import com.cloudant.client.cache.Stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ArunIyengar
 */
public class CacheTests {

    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";
    long lifetime = 3000;

    void testPutGetGetStatistics(CacheWithLifetimes<String, Integer> spc, boolean inProcess) {
        spc.clear();
        spc.put(key1, 42, lifetime);
        spc.put(key2, 43, lifetime);
        spc.put(key3, 44, lifetime);
        Stats stats1 = spc.getStatistics();
        assertEquals("Cache size should be 3", 3, spc.size());
        if (inProcess) {
            assertEquals("Hit rate should be 1.0", 1.0,
                    ((InProcessCacheStats) stats1).getStats().hitRate(), .0001);
        }
    }

    public void testClear(CacheWithLifetimes<String, Integer> spc) {
        spc.clear();
        spc.put(key1, 42, lifetime);
        spc.put(key2, 43, lifetime);
        spc.put(key3, 44, lifetime);
        assertEquals("Cache size should be 3", 3, spc.size());
        spc.clear();
        assertEquals("Cache size should be 0", 0, spc.size());
    }

    public void testDelete(CacheWithLifetimes<String, Integer> spc) {
        spc.clear();
        spc.put(key1, 42, lifetime);
        spc.put(key2, 43, lifetime);
        spc.put(key3, 44, lifetime);
        assertEquals("Cache size should be 3", 3, spc.size());
        spc.delete(key2);
        assertEquals("Cache size should be 2", 2, spc.size());
        spc.put(key2, 50, lifetime);
        spc.put("key4", 59, lifetime);
        spc.put("key5", 80, lifetime);

        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        spc.deleteAll(list);
        assertEquals("Cache size should be 3", 3, spc.size());
        spc.delete("adjkfjadfjdf");
        spc.delete("adfkasdklfjil");
        assertEquals("Cache size should be 3", 3, spc.size());
    }

    public void testPutAll(CacheWithLifetimes<String, Integer> spc) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(key1, 42);
        map.put(key2, 43);
        map.put(key3, 44);
        spc.clear();
        spc.putAll(map, lifetime);
        assertEquals("Cache size should be 3", 3, spc.size());
    }

    public void testGetAll(CacheWithLifetimes<String, Integer> spc) {
        spc.put(key1, 42, lifetime);
        spc.put(key2, 43, lifetime);
        spc.put(key3, 44, lifetime);
        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        list.add(key3);
        Map<String, Integer> map = spc.getAll(list);
        assertEquals("Returned map size should be 3", 3, map.size());
    }

    public void testUpdate(CacheWithLifetimes<String, Integer> spc) {
        Integer val1;

        spc.put(key1, 42, lifetime);
        val1 = spc.get(key1);
        assertEquals("Val1 should be 42, actual value is " + val1, 42, val1.intValue());
        spc.put(key1, 43, lifetime);
        val1 = spc.get(key1);
        assertEquals("Val1 should be 43, actual value is " + val1, 43, val1.intValue());
        spc.put(key1, 44, lifetime);
        val1 = spc.get(key1);
        assertEquals("Val1 should be 44, actual value is " + val1, 44, val1.intValue());
    }

    public void testExpiration(CacheWithLifetimes<String, Integer> spc) {
        long lifespan = 1000;
        Integer val1;

        spc.clear();
        val1 = spc.get(key1);
        assertNull("Val1 should be null, value is " + val1, val1);
        spc.put(key1, 42, lifespan);
        val1 = spc.get(key1);
        assertNotNull("Val1 should not be null, value is " + val1, val1);
        try {
            Thread.sleep(lifespan + 200);
        } catch (Exception e) {
        }
        val1 = spc.get(key1);
        assertNull("Val1 should be null, value is " + val1, val1);
    }

}
