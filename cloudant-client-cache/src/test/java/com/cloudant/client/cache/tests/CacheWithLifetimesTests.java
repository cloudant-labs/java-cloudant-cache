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

import com.cloudant.client.cache.CacheWithLifetimes;

import org.junit.Test;

/**
 * Extension of CacheTests that includes some additional testing related to lifetimes.
 *
 * @param <T> the CacheWithLifetimes implementation to be specified by the subclass
 */
public abstract class CacheWithLifetimesTests<T extends CacheWithLifetimes<String, Integer>>
        extends CacheTests<T> {

    protected static final int DEFAULT_EXPIRATION = 500;
    /**
     * Constant to use for lifespan testing. It is the time in milliseconds tests add to lifespan
     * before checking any expiry purging.
     */
    protected static final int LIFESPAN_TOLERANCE = 200;

    /**
     * Test that an entry with a specified lifespan is purged when that lifespan is exceeded.
     *
     * @throws InterruptedException if the sleep is interrupted
     */
    @Test
    public void testExpiration() throws InterruptedException {
        long lifespan = 1000;

        // Assert the cache is empty
        assertCacheSize(0);

        // Put an entry in the cache and check it
        cache.put(key1, 1, lifespan);
        assertEntries(1);

        // Sleep for longer than the lifespan
        Thread.sleep(lifespan + LIFESPAN_TOLERANCE);

        // Assert that the entry has been removed from the cache
        assertNoEntries(1);
    }

    /**
     * Tests than an entry added without a lifespan is purged after the default lifespan is
     * exceeded.
     *
     * @throws InterruptedException if the sleep is interrupted
     */
    @Test
    public void testDefaultExpiration() throws InterruptedException {
        // Assert the cache is empty
        assertCacheSize(0);

        // Put an entry in the cache and check it
        populateCache(1);
        assertEntries(1);

        // Sleep for longer than the lifespan
        Thread.sleep(DEFAULT_EXPIRATION + LIFESPAN_TOLERANCE);

        // Assert that the entry has been removed from the cache
        assertNoEntries(1);
    }

}
