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

import com.cloudant.client.cache.LRUCache;

import org.junit.Test;

public class LRUCacheTests extends CacheTests<LRUCache<String, Integer>> {

    @Override
    protected LRUCache<String, Integer> getNewCacheInstance() {
        return new LRUCache<>(5);
    }

    /**
     * Tests that the LRU cache is bounded by its maximum number of entries
     */
    @Test
    public void testMaxEntries() {
        populateCache(6);
        assertEquals("The cache should not exceed the maximum size", 5, cache.size());
    }

    /**
     * Test that the least recently used entry is the one removed from the LRU cache.
     */
    @Test
    public void testLeastAccessedRemoved() {
        // Fill the cache to maximum
        populateCache(5);
        // Now access 4 of the 5 entries
        int i = 1;
        do {
            assertNotNull("The entry should be present in the cache", cache.get("key" + i));
            i++;
        } while (i <= 4);
        // Now add a sixth entry, which should mean entry 5 is no longer in the cache
        cache.put("key6", 6);
        assertEquals("The cache should not exceed the maximum size", 5, cache.size());
        assertNull("The entry key5 should not be present in the cache", cache.get("key5"));
        assertEquals("The entry key6 should be present in the cache", 6, cache.get("key6")
                .intValue());
    }
}
