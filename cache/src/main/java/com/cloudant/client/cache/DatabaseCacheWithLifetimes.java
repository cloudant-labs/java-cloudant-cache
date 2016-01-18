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

import com.cloudant.client.api.Database;

import java.util.concurrent.TimeUnit;


/**
 * A {@link Database} implementation with a cache that supports expiration times.
 *
 * @author Arun Iyengar
 */

public class DatabaseCacheWithLifetimes extends DatabaseCache {

    // Although this is stored in super.cache, we store it as the more specific
    // CacheWithLifetimes so it can be used with lifetimes more readily in this class
    private CacheWithLifetimes<String, Object> lifetimeCache;
    protected long lifetime;

    /**
     * Constructor which is designed to work with a variety of different caches.
     *
     * @param database      data structure with information about the database connection
     * @param cacheInstance cache instance which has already been created and initialized
     * @param lifetime      lifetime for objects in this cache
     * @param lifetimeUnit  TimeUnit to use for the lifetime
     */
    public DatabaseCacheWithLifetimes(Database database, CacheWithLifetimes<String, Object>
            cacheInstance, long lifetime, TimeUnit lifetimeUnit) {
        super(database, cacheInstance);
        this.lifetimeCache = cacheInstance;
        this.lifetime = lifetimeUnit.toMillis(lifetime);
    }

    /**
     * Put an object into the cache.
     *
     * @param id     the document id
     * @param object object to cache
     */
    @Override
    protected void cachePut(String id, Object object) {
        lifetimeCache.put(id, object, System.currentTimeMillis() + lifetime);
    }

    @Override
    public CacheWithLifetimes<String, Object> getCache() {
        return this.lifetimeCache;
    }
}
