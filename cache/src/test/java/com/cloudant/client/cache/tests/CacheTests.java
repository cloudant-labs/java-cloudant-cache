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
import static org.junit.Assert.assertNull;

import com.cloudant.client.cache.Cache;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <P>
 * A series of tests for the basic operations of a cache implementation. This class is intended
 * to be extended for each cache implementation, where additional implementation specific tests
 * may be added.
 * </P>
 * <P>
 * The tests use a cache of Integer values with String keys e.g. "key1" : 1
 * </P>
 * @param <T> the type of the cache implementation to be specified by the subclass.
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

    protected String key1 = "key1";
    protected String key2 = "key2";
    protected String key3 = "key3";
    protected List<String> keys1to3 = Arrays.asList(new String[]{key1, key2, key3});

    /**
     * Test that an entry can be added to the cache and assert that the cache size is 1 after
     * adding 1 entry.
     */
    @Test
    public void testCachePut() {
        populateCache(1);
        assertCacheSize(1);
    }

    /**
     * Test that an entry added to the cache can be retrieved and is correct.
     */
    @Test
    public void testCacheGet() {
        populateCache(1);
        assertCacheSize(1);
        assertEntries(1);
    }

    /**
     * Test that multiple entries can be added to and retrieved from the cache.
     */
    @Test
    public void testMultiplePutGet() {
        populateCache(3);
        assertCacheSize(3);
        assertEntries(1, 2, 3);
    }

    /**
     * Test that the clear method removes all values from the cache.
     */
    @Test
    public void testClear() {
        populateCache(3);
        assertCacheSize(3);
        cache.clear();
        assertNoEntries(1, 2, 3);
        assertCacheSize(0);
    }

    /**
     * Test that the delete method removes an entry from the cache.
     */
    @Test
    public void testDelete() {
        // Load the cache
        populateCache(3);
        assertCacheSize(3);

        // Remove an entry
        cache.delete(key2);
        assertCacheSize(2);
        assertNoEntries(2);
        assertEntries(1, 3);
    }

    /**
     * Test the deleteAll method removes the correct set of entries.
     */
    @Test
    public void testDeleteAll() {
        // Load the cache
        populateCache(5);

        // Delete the entries in the list
        cache.deleteAll(keys1to3);

        // Assert the remaining entries are as expected
        assertNoEntries(1, 2, 3);
        assertEntries(4, 5);
        assertCacheSize(2);
    }

    /**
     * Tests that attempting to delete entries for non-existent keys does not remove other entries.
     */
    @Test
    public void testDeleteNonExistentEntries() {
        populateCache(3);
        cache.delete("adjkfjadfjdf");
        cache.delete("adfkasdklfjil");
        assertCacheSize(3);
        assertEntries(1, 2, 3);
    }

    /**
     * Tests the putAll method correctly adds to the cache.
     */
    @Test
    public void testPutAll() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put(key1, 1);
        map.put(key2, 2);
        map.put(key3, 3);
        cache.putAll(map);
        assertCacheSize(3);
        assertEntries(1, 2, 3);
    }

    /**
     * Tests the getAll method correctly returns the specified keys.
     */
    @Test
    public void testGetAll() {
        populateCache(5);
        Map<String, Integer> map = cache.getAll(keys1to3);
        assertEquals("Returned map size should be 3", 3, map.size());
        assertEquals("The getAll returned map entry should be equal", 1, map.get(key1).intValue());
        assertEquals("The getAll returned map entry should be equal", 2, map.get(key2).intValue());
        assertEquals("The getAll returned map entry should be equal", 3, map.get(key3).intValue());
    }

    /**
     * Tests that a cache entry is correctly updated multiple times.
     */
    @Test
    public void testUpdate() {
        populateCache(3);
        assertCacheSize(3);

        // Check the initial value
        assertEntry(key1, 1);

        // Update the value and assert
        cache.put(key1, 42);
        assertEntry(key1, 42);

        // Update again
        cache.put(key1, 43);
        assertEntry(key1, 43);

        // Update a third time
        cache.put(key1, 44);
        assertEntry(key1, 44);

        // Finally assert that the cache is still the correct size
        assertCacheSize(3);
    }

    /**
     * Tests that the cache correctly handles deletions followed by puts with the same key.
     */
    @Test
    public void testDeleteAndReplace() {
        // Load the cache
        populateCache(3);
        assertCacheSize(3);

        // Delete the second entry
        cache.delete(key2);
        assertCacheSize(2);
        assertNoEntries(2);

        // Add a new value for key 2
        cache.put(key2, 72);
        assertCacheSize(3);
        assertEntry(key2, 72);
    }

    /**
     * Assert that the cache size is equal to the expected size
     *
     * @param expectedSize the expected size of the cache
     */
    protected void assertCacheSize(int expectedSize) {
        assertEquals("Cache size should be " + expectedSize, expectedSize, cache.size());
    }

    /**
     * Performs a get for each specified key "keyN" and asserts that the value is equal to N.
     *
     * @param entries the values to check
     */
    protected void assertEntries(Integer... entries) {
        for (Integer expectedValue : entries) {
            String key = "key" + expectedValue;
            assertEntry(key, expectedValue);
        }
    }

    /**
     * Performs a get for each specified key "keyN" and asserts that there is no entry in the cache.
     *
     * @param entries the values to check
     */
    protected void assertNoEntries(Integer... entries) {
        for (Integer entry : entries) {
            String key = "key" + entry;
            assertNull(String.format("The cache entry for key %s should be %s", key,
                    null), cache.get(key));
        }
    }

    /**
     * Assert that the entry for the specified key matches the expected value.
     *
     * @param key           the key for the cache entry
     * @param expectedValue the expected value for the cache entry
     */
    protected void assertEntry(String key, Integer expectedValue) {
        assertEquals(String.format("The cache entry for key %s should be %s", key,
                expectedValue), expectedValue, cache.get(key));
    }

    /**
     * Populate the cache with the specified number of entries of the form "keyN" : N for N=1 to
     * N = entries.
     *
     * @param numberOfEntries number of entries to put in the cache
     */
    protected void populateCache(int numberOfEntries) {
        int i = 1;
        do {
            cache.put("key" + i, i);
            i++;
        } while (i <= numberOfEntries);
    }
}
