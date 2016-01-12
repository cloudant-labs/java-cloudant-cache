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
 * Contains a Database Public API implementation with a cache.
 *
 * @author Arun Iyengar
 */
public class DatabaseCache {

    protected final Database db;
    protected final Cache<String, Object> cache;


    /**
     * Constructor which is designed to work with a variety of different caches.
     *
     * @param database      : data structure with information about the database connection
     * @param cacheInstance : cache instance which has already been created and initialized
     */
    public DatabaseCache(Database database, Cache<String, Object> cacheInstance) {
        this.db = database;
        this.cache = cacheInstance;
    }


    /**
     * Put an object into the cache
     *
     * @param id     The document id.
     * @param object : object to cache
     */
    protected void cachePut(String id, Object object) {
        cache.put(id, object);
    }

    /**
     * Return value of cached object (or null if not present)
     *
     * @param <T>       Object type.
     * @param id        The document id.
     * @param classType The class of type T.
     * @return value of object
     */
    protected <T> T cacheGet(Class<T> classType, String id) {
        return classType.cast(cache.get(id));
    }

    /**
     * Return an object from the cache, if present
     *
     * @param id The document id.
     */
    protected void cacheDelete(String id) {
        cache.delete(id);
    }

    /**
     * Returns the cache so that the application can mange it using the Cache
     * API methods
     */
    public Cache<String, Object> getCache() {
        return cache;
    }

    /**
     * Finds an Object of the specified type.
     *
     * @param <T>       Object type.
     * @param classType The class of type T.
     * @param id        The document id.
     * @return An object of type T.
     * @throws NoDocumentException If the document is not found in the database.
     */
    public <T> T find(Class<T> classType, String id) {
        T value = cacheGet(classType, id);
        if (value != null) {
            return value;
        } else {
            value = db.find(classType, id);
            cachePut(id, value);
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
     * @return An object of type T.
     * @throws NoDocumentException If the document is not found in the database.
     */
    public <T> T find(Class<T> classType, String id, Params params) {
        T value = cacheGet(classType, id);
        if (value != null) {
            return value;
        } else {
            value = db.find(classType, id, params);
            cachePut(id, value);
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
     * @return An object of type T.
     */
    public <T> T findAny(Class<T> classType, String uri) {
        T value = classType.cast(cache.get(uri));
        if (value != null) {
            return value;
        } else {
            value = db.findAny(classType, uri);
            cachePut(uri, value);
            return value;
        }
    }

    /**
     * Checks if a document exist in the database.
     *
     * @param id The document _id field.
     * @return true If the document is found, false otherwise.
     */
    public boolean contains(String id) {
        if (cache.get(id) != null) {
            return true;
        } else {
            return db.contains(id);
        }
    }

    /**
     * Saves an object in the database, using HTTP <tt>PUT</tt> request.
     * <p>
     * If the object doesn't have an <code>_id</code> value, the code will
     * assign a <code>UUID</code> as the document id.
     *
     * @param id     : This method caches "object" using key "id"
     * @param object The object to save
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the save.
     */
    public <T> Response save(String id, T object) {
        Response response = db.save(object);
        cachePut(id, object);
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
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the save.
     */
    public <T> Response save(String id, T object, int writeQuorum) {
        Response response = db.save(object, writeQuorum);
        cachePut(id, object);
        return response;
    }

    /**
     * Saves an object in the database using HTTP <tt>POST</tt> request.
     * <p>
     * The database will be responsible for generating the document id.
     *
     * @param id     : This method caches "object" using key "id"
     * @param object The object to save
     * @return {@link Response}
     */
    public <T> Response post(String id, T object) {
        Response response = db.post(object);
        cachePut(id, object);
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
     * @return {@link Response}
     */
    public <T> Response post(String id, T object, int writeQuorum) {
        Response cloudantResponse = db.post(object, writeQuorum);
        cachePut(id, object);
        return cloudantResponse;
    }

    /**
     * Updates an object in the database, the object must have the correct
     * <code>_id</code> and <code>_rev</code> values.
     *
     * @param id     : This method caches "object" using key "id"
     * @param object The object to update
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the update.
     */
    public <T> Response update(String id, T object) {
        Response response = db.update(object);
        cachePut(id, object);
        return response;
    }

    /**
     * Updates an object in the database, the object must have the correct
     * <code>_id</code> and <code>_rev</code> values.
     *
     * @param id          : This method caches "object" using key "id"
     * @param object      The object to update
     * @param writeQuorum the write Quorum
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the update.
     */
    public <T> Response update(String id, T object, int writeQuorum) {
        Response response = db.update(object, writeQuorum);
        cachePut(id, object);
        return response;
    }

    /**
     * Removes a document from the database.
     * <p>
     * The object must have the correct <code>_id</code> and <code>_rev</code>
     * values.
     *
     * @param object The document to remove as object.
     * @param id     : key identifying object to remove from cache
     * @return {@link Response}
     * @throws NoDocumentException If the document is not found in the database.
     */
    public <T> Response remove(String id, T object) {
        cache.delete(id);
        Response response = db.remove(object);
        return response;
    }

}
