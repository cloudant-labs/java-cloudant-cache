/**
 *
 */
package com.cloudant.client.cache.inprocess;

import com.cloudant.client.cache.Stats;
import com.google.common.cache.CacheStats;

/**
 * @author ArunIyengar
 */
public class InProcessCacheStats implements Stats {
    private CacheStats cacheStats;

    InProcessCacheStats(CacheStats stats) {
        cacheStats = stats;
    }

    public CacheStats getStats() {
        return cacheStats;
    }

}
