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


import static org.junit.Assert.assertEquals;

import com.cloudant.client.cache.CacheWithLifetimes;
import com.cloudant.client.cache.Stats;
import com.cloudant.client.cache.inprocess.InProcessCache;
import com.cloudant.client.cache.inprocess.InProcessCacheStats;
import com.cloudant.client.cache.tests.CacheWithLifetimesTests;


/**
 * @author ArunIyengar
 */
public class InProcessCacheTests extends CacheWithLifetimesTests {

    @Override
    protected CacheWithLifetimes<String, Integer> getNewCacheInstance() {
        return new InProcessCache<>(DEFAULT_NUM_OBJECTS, DEFAULT_EXPIRATION);
    }

    @Override
    public void testPutGetGetStatistics() {
        super.testPutGetGetStatistics();
        Stats stats1 = cache.getStatistics();
        assertEquals("Hit rate should be 1.0", 1.0, ((InProcessCacheStats) stats1).getStats()
                .hitRate(), .0001);
    }

}
