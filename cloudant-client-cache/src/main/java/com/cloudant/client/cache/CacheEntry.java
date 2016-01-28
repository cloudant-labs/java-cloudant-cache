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

package com.cloudant.client.cache;

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
