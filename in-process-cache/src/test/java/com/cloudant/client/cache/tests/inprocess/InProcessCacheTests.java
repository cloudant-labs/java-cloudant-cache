/**
 *
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
