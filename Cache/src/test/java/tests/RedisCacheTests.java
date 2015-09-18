/**
 * 
 */
package tests;


import org.junit.Test;

import client.RedisCache;
import client.Serializer;
import client.Util;

/**
 * @author ArunIyengar
 *
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
