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
import com.cloudant.client.api.model.Params;
import com.cloudant.client.api.model.Response;

import java.util.List;

/**
 * A {@link Database} implementation with a cache.
 *
 * @author Arun Iyengar
 */
public class DatabaseCache extends Database {

    protected final Cache<String, Object> cache;
    
    /**
     * Constructor which is designed to work with a variety of different caches.
     *
     * @param database      non-null data structure with information about the database connection
     * @param cacheInstance non-null cache instance which has already been created and initialized
     */
    public DatabaseCache(Database database, Cache<String, Object> cacheInstance) {
        super(database);
        this.cache = cacheInstance;
    }


    /**
     * Put an object into the cache.
     *
     * @param id     the document id
     * @param object object to cache
     */
    protected void cachePut(String id, Object object) {
        cache.put(id, object);
    }

    /**
     * Return value of cached object (or null if not present).
     *
     * @param <T>       Object type
     * @param id        the document id
     * @param classType the class of type T
     * @return value of object
     */
    protected <T> T cacheGet(Class<T> classType, String id) {
        return classType.cast(cache.get(id));
    }

    /**
     * Return an object from the cache, if present.
     *
     * @param id The document id
     */
    protected void cacheDelete(String id) {
        cache.delete(id);
    }

    /**
     * Returns the cache so that the application can manage it using the Cache
     * API methods.
     *
     * @return the cache instance
     */
    public Cache<String, Object> getCache() {
        return cache;
    }

    /* Database methods follow that will interact with the cache */

    /**
     * <P>
     * Preferentially use the cache for the find operation. Adds the retrieved T to the cache if
     * it was not present and was found in the remote database.
     * </P>
     * {@inheritDoc}
     */
    public <T> T find(Class<T> classType, String id) {
        T value = cacheGet(classType, id);
        if (value != null) {
            return value;
        } else {
            value = super.find(classType, id);
            cachePut(id, value);
            return value;
        }
    }

    /**
     * <P>
     * Preferentially use the cache for the find operation. Adds the retrieved T to the cache if
     * it was not present and was found in the remote database.
     * </P>
     * {@inheritDoc}
     */
    public <T> T find(Class<T> classType, String id, Params params) {
        T value = cacheGet(classType, id);
        if (value != null) {
            return value;
        } else {
            value = super.find(classType, id, params);
            cachePut(id, value);
            return value;
        }
    }

    /**
     * <P>
     * Preferentially use the cache for the find operation. Adds the retrieved T to the cache if
     * it was not present and was found in the remote database.
     * </P>
     * <P>
     * Note that this method uses the URI as the cache key (and not document ID) since the
     * document may come from a different database or server. As a result even if the same object
     * is already stored in the cache under its document ID it would be retrieved remotely by
     * this method and re-stored in the cache with a URI key. This also prevents the remove
     * method from removing these objects from the cache so it is recommended to only use this
     * method with a {@link CacheWithLifetimes} cache to avoid the cache growing unbounded.
     * </P>
     * {@inheritDoc}
     */
    public <T> T findAny(Class<T> classType, String uri) {
        T value = classType.cast(cache.get(uri));
        if (value != null) {
            return value;
        } else {
            value = super.findAny(classType, uri);
            cachePut(uri, value);
            return value;
        }
    }

    /**
     * <P>
     * Checks if the cache contains the specified document. If it does not then checks if the
     * database contains the specified document.
     * </P>
     * {@inheritDoc}
     */
    public boolean contains(String id) {
        if (cache.get(id) != null) {
            return true;
        } else {
            return super.contains(id);
        }
    }

    /**
     * {@inheritDoc}
     * <P>
     * If the operation was successful then the object is also added to the cache.
     * </P>
     */
    public Response save(Object object) {
        Response response = super.save(object);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * {@inheritDoc}
     * <P>
     * If the operation was successful then the object is also added to the cache.
     * </P>
     */
    public Response save(Object object, int writeQuorum) {
        Response response = super.save(object, writeQuorum);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * {@inheritDoc}
     * <P>
     * If the operation was successful then the object is also added to the cache.
     * </P>
     */
    public Response post(Object object) {
        Response response = super.post(object);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * {@inheritDoc}
     * <P>
     * If the operation was successful then the object is also added to the cache.
     * </P>
     */
    public Response post(Object object, int writeQuorum) {
        Response response = super.post(object, writeQuorum);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * {@inheritDoc}
     * <P>
     * If the operation was successful then the object is also updated in the cache.
     * </P>
     */
    public Response update(Object object) {
        Response response = super.update(object);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * {@inheritDoc}
     * <P>
     * If the operation was successful then the object is also updated in the cache.
     * </P>
     */
    public Response update(Object object, int writeQuorum) {
        Response response = super.update(object, writeQuorum);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * {@inheritDoc}
     * <P>
     * If the operation was successful then the object is also removed from the cache.
     * </P>
     */
    public Response remove(Object object) {
        Response response = super.remove(object);
        cache.delete(response.getId());
        return response;
    }

    /**
     * {@inheritDoc}
     * <P>
     * Objects are added to or updated in the cache if their remote operation completes
     * successfully.
     * </P>
     */
    @Override
    public List<Response> bulk(List<?> list) {
        List<Response> responses = super.bulk(list);
        int index = 0;
        for (Object o : list) {
            Response response = responses.get(index);
            // Cache the object we just created/updated if the operation was successful
            if (response.getError() == null) {
                cachePut(response.getId(), o);
            }
            index++;
        }
        return responses;
    }
}
