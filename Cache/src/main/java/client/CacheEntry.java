/**
 *
 */
package client;

import java.io.Serializable;

/**
 * @author ArunIyengar
 */

/*
 * This class represents what is actually stored in the cache.
 */
public class CacheEntry<V> implements Serializable {

    private static final long serialVersionUID = 1L;
    private V value;
    private long expirationTime;

    public CacheEntry(V val, long expires) {
        value = val;
        expirationTime = expires;
    }

    public V getValue() {
        return value;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public String toString() {
        return ("CacheEntry value: " + value +
                "\nCacheEntry expiration time: " + expirationTime +
                "\nMilliseconds until expiration: " + (expirationTime - Util.getTime()));
    }

    public void print() {
        System.out.println(toString());
    }
}
