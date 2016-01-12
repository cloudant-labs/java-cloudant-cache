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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.cloudant.client.cache.CacheWithLifetimes;

import org.junit.Test;

public abstract class CacheWithLifetimesTests extends CacheTests<CacheWithLifetimes<String,
        Integer>> {

    protected static final int DEFAULT_EXPIRATION = 6000;

    @Test
    public void testExpiration() throws Exception {
        long lifespan = 1000;
        Integer val1;

        cache.clear();
        val1 = cache.get(key1);
        assertNull("Val1 should be null, value is " + val1, val1);
        cache.put(key1, 42, lifespan);
        val1 = cache.get(key1);
        assertNotNull("Val1 should not be null, value is " + val1, val1);
        Thread.sleep(lifespan + 200);
        val1 = cache.get(key1);
        assertNull("Val1 should be null, value is " + val1, val1);
    }

}
