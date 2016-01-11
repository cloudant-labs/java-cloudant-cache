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

package com.cloudant.client.cache.tests.inprocess;


import com.cloudant.client.cache.inprocess.InProcessCache;
import com.cloudant.client.cache.tests.CacheTests;

import org.junit.Test;

/**
 * @author ArunIyengar
 */
public class InProcessCacheTests {

    long defaultExpiration = 6000;
    int numObjects = 2000;
    InProcessCache<String, Integer> spc = new InProcessCache<String, Integer>(
            numObjects, defaultExpiration);
    CacheTests cacheTests = new CacheTests();


    @Test
    public void testPutGetGetStatistics() {
        cacheTests.testPutGetGetStatistics(spc, true);
    }

    @Test
    public void testClear() {
        cacheTests.testClear(spc);
    }

    @Test
    public void testDelete() {
        cacheTests.testDelete(spc);
    }

    @Test
    public void testPutAll() {
        cacheTests.testPutAll(spc);
    }

    @Test
    public void testGetAll() {
        cacheTests.testGetAll(spc);
    }

    @Test
    public void testUpdate() {
        cacheTests.testUpdate(spc);
    }

    @Test
    public void testExpiration() {
        cacheTests.testExpiration(spc);
    }

}
