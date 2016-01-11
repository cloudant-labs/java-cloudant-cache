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
import com.cloudant.client.org.lightcouch.DocumentConflictException;
import com.cloudant.client.org.lightcouch.NoDocumentException;


/**
 * Contains a Database Public API implementation with a cache that supports expiration times.
 *
 * @author Arun Iyengar
 */

public class DatabaseCacheWithLifetimes extends DatabaseCache {

    /**
     * Constructor which is designed to work with a variety of different caches.
     *
     * @param database      : data structure with information about the database connection
     * @param cacheInstance : cache instance which has already been created and initialized
     */
    public DatabaseCacheWithLifetimes(Database database, Cache<String, Object> cacheInstance) {
        super(database, cacheInstance);
    }

    /**
     * put an object into the cache
     *
     * @param <T>      Object type.
     * @param id       The document id.
     * @param object   : object to cache
     * @param lifetime : lifetime of the object for the cache in milliseconds
     */
    public <T> void cachePut(String id, T object, long lifetime) {
        ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
    }

    /**
     * Finds an Object of the specified type.
     *
     * @param <T>       Object type.
     * @param classType The class of type T.
     * @param id        The document id.
     * @param lifetime  : lifetime of the object for the cache in milliseconds
     * @return An object of type T.
     * @throws NoDocumentException If the document is not found in the database.
     */
    public <T> T find(Class<T> classType, String id, long lifetime) {
        T value = classType.cast(cache.get(id));
        if (value != null) {
            return value;
        } else {
            value = db.find(classType, id);
            ((CacheWithLifetimes<String, Object>) cache).put(id, value, Util.getTime() + lifetime);
            return value;
        }
    }

    /**
     * Finds an Object of the specified type.
     *
     * @param <T>       Object type.
     * @param classType The class of type T.
     * @param id        The document id.
     * @param params    Extra parameters to append.
     * @param lifetime  : lifetime of the object for the cache in milliseconds
     * @return An object of type T.
     * @throws NoDocumentException If the document is not found in the database.
     */
    public <T> T find(Class<T> classType, String id, Params params, long lifetime) {
        T value = classType.cast(cache.get(id));
        if (value != null) {
            return value;
        } else {
            value = db.find(classType, id, params);
            ((CacheWithLifetimes<String, Object>) cache).put(id, value, Util.getTime() + lifetime);
            return value;
        }
    }

    /**
     * This method finds any document given a URI.
     * <p>
     * The URI must be URI-encoded.
     *
     * @param classType The class of type T.
     * @param uri       The URI as string.
     * @param lifetime  : lifetime of the object for the cache in milliseconds
     * @return An object of type T.
     */
    public <T> T findAny(Class<T> classType, String uri, long lifetime) {
        T value = classType.cast(cache.get(uri));
        if (value != null) {
            return value;
        } else {
            value = db.findAny(classType, uri);
            ((CacheWithLifetimes<String, Object>) cache).put(uri, value, Util.getTime() + lifetime);
            return value;
        }
    }

    /**
     * Saves an object in the database, using HTTP <tt>PUT</tt> request.
     * <p>
     * If the object doesn't have an <code>_id</code> value, the code will
     * assign a <code>UUID</code> as the document id.
     *
     * @param id       : This method caches "object" using key "id"
     * @param object   The object to save
     * @param lifetime : lifetime of the object for the cache in milliseconds
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the save.
     */
    public <T> Response save(String id, T object, long lifetime) {
        Response response = db.save(object);
        ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
        return response;
    }

    /**
     * Saves an object in the database, using HTTP <tt>PUT</tt> request.
     * <p>
     * If the object doesn't have an <code>_id</code> value, the code will
     * assign a <code>UUID</code> as the document id.
     *
     * @param id          : This method caches "object" using key "id"
     * @param object      The object to save
     * @param writeQuorum the write Quorum
     * @param lifetime    : lifetime of the object for the cache in milliseconds
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the save.
     */
    public <T> Response save(String id, T object, int writeQuorum, long lifetime) {
        Response response = db.save(object, writeQuorum);
        ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
        return response;
    }

    /**
     * Saves an object in the database using HTTP <tt>POST</tt> request.
     * <p>
     * The database will be responsible for generating the document id.
     *
     * @param id       : This method caches "object" using key "id"
     * @param object   The object to save
     * @param lifetime : lifetime of the object for the cache in milliseconds
     * @return {@link Response}
     */
    public <T> Response post(String id, T object, long lifetime) {
        Response response = db.post(object);
        ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
        return response;
    }

    /**
     * Saves an object in the database using HTTP <tt>POST</tt> request with
     * specificied write quorum
     * <p>
     * The database will be responsible for generating the document id.
     *
     * @param id          : This method caches "object" using key "id"
     * @param object      The object to save
     * @param writeQuorum the write Quorum
     * @param lifetime    : lifetime of the object for the cache in milliseconds
     * @return {@link Response}
     */
    public <T> Response post(String id, T object, int writeQuorum, long lifetime) {
        Response cloudantResponse = db.post(object, writeQuorum);
        ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
        return cloudantResponse;
    }

    /**
     * Updates an object in the database, the object must have the correct
     * <code>_id</code> and <code>_rev</code> values.
     *
     * @param id       : This method caches "object" using key "id"
     * @param object   The object to update
     * @param lifetime : lifetime of the object for the cache in milliseconds
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the update.
     */
    public <T> Response update(String id, T object, long lifetime) {
        Response response = db.update(object);
        ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
        return response;
    }

    /**
     * Updates an object in the database, the object must have the correct
     * <code>_id</code> and <code>_rev</code> values.
     *
     * @param id          : This method caches "object" using key "id"
     * @param object      The object to update
     * @param writeQuorum the write Quorum
     * @param lifetime    : lifetime of the object for the cache in milliseconds
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the update.
     */
    public <T> Response update(String id, T object, int writeQuorum, long lifetime) {
        Response response = db.update(object, writeQuorum);
        ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
        return response;
    }

}
