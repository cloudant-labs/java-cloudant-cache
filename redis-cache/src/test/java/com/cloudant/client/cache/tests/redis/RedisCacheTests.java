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

package com.cloudant.client.cache.tests.redis;


import com.cloudant.client.cache.redis.RedisCache;
import com.cloudant.client.cache.tests.CacheTests;

import org.junit.Test;

/**
 * @author ArunIyengar
 */
public class RedisCacheTests {

    long defaultExpiration = 6000;
    RedisCache<String, Integer> opc = new RedisCache<String, Integer>
            ("localhost", 6379, 60, defaultExpiration);
    CacheTests cacheTests = new CacheTests();


    @Test
    public void testPutGetGetStatistics() {
        cacheTests.testPutGetGetStatistics(opc, false);
    }

    @Test
    public void testClear() {
        cacheTests.testClear(opc);
    }

    @Test
    public void testDelete() {
        cacheTests.testDelete(opc);
    }

    @Test
    public void testPutAll() {
        cacheTests.testPutAll(opc);
    }

    @Test
    public void testGetAll() {
        cacheTests.testGetAll(opc);
    }

    @Test
    public void testUpdate() {
        cacheTests.testUpdate(opc);
    }

    @Test
    public void testExpiration() {
        cacheTests.testExpiration(opc);
    }

}
