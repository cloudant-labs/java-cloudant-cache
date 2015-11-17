/**
 *
 */
package client;

import java.util.Map;

/**
 * @author ArunIyengar
 */
public interface CacheWithLifetimes<K, V> extends Cache<K, V> {
    /**
     * cache a key-value pair
     *
     * @param key      key associated with value
     * @param value    value associated with key
     * @param lifetime lifetime in milliseconds associated with data
     */
    public void put(K key, V value, long lifetime);

    /**
     * cache one or more key-value pairs
     *
     * @param map      map containing key-value pairs to cache
     * @param value    value associated with each key-value pair
     * @param lifetime lifetime in milliseconds associated with each key-value pair.
     *                 If the system supports revalidation of expired cache entries to determine if
     *                 expired entries are really obsolete, a value <= 0 indicates cached entry
     *                 should
     *                 always be revalidated before being returned to client
     */
    public void putAll(Map<K, V> map, long lifetime);


}
