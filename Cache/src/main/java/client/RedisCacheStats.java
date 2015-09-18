/**
 * 
 */
package client;

/**
 * @author ArunIyengar
 * 
 */

/*
 * This class implements cache statistics for Redis.  In Redis, statistics are returned as a string.
 */
public class RedisCacheStats implements Stats {
    private String cacheStats;

    RedisCacheStats(String stats) {
        cacheStats = stats;
    }

    public String getStats() {
        return cacheStats;
    }

}
