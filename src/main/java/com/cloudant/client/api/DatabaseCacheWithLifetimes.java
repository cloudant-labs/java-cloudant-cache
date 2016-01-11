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

package com.cloudant.client.api;

import static org.lightcouch.internal.CouchDbUtil.assertNotEmpty;
import static org.lightcouch.internal.CouchDbUtil.close;
import static org.lightcouch.internal.CouchDbUtil.createPost;
import static org.lightcouch.internal.CouchDbUtil.getResponse;
import static org.lightcouch.internal.URIBuilder.buildUri;

import com.cloudant.client.api.model.Params;

import org.apache.http.HttpResponse;
import org.lightcouch.DocumentConflictException;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Response;

import client.Cache;
import client.CacheWithLifetimes;
import client.Util;

import java.net.URI;


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
        assertNotEmpty(params, "params");
        T value = classType.cast(cache.get(id));
        if (value != null) {
            return value;
        } else {
            value = db.find(classType, id, params.getInternalParams());
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
    public <T> com.cloudant.client.api.model.Response save(String id, T object,
                                                           long lifetime) {
        Response couchDbResponse = db.save(object);
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model
                .Response(
                couchDbResponse);
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
    public <T> com.cloudant.client.api.model.Response save(String id, T object, int writeQuorum,
                                                           long lifetime) {
        Response couchDbResponse = client.put(getDBUri(), object, true,
                writeQuorum, client.getGson());
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model
                .Response(
                couchDbResponse);
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
    public <T> com.cloudant.client.api.model.Response post(String id, T object,
                                                           long lifetime) {
        Response couchDbResponse = db.post(object);
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model
                .Response(
                couchDbResponse);
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
    public <T> com.cloudant.client.api.model.Response post(String id, T object, int writeQuorum,
                                                           long lifetime) {
        assertNotEmpty(object, "object");
        HttpResponse response = null;
        try {
            URI uri = buildUri(getDBUri()).query("w", writeQuorum).build();
            response = client.executeRequest(createPost(uri, client.getGson()
                    .toJson(object), "application/json"));
            Response couchDbResponse = getResponse(response, Response.class,
                    client.getGson());
            com.cloudant.client.api.model.Response cloudantResponse = new com.cloudant.client.api
                    .model.Response(
                    couchDbResponse);
            ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
            return cloudantResponse;
        } finally {
            close(response);
        }
    }

    /**
     * Saves a document with <tt>batch=ok</tt> query param.
     *
     * @param id       : This method caches "object" using key "id"
     * @param object   The object to save.
     * @param lifetime : lifetime of the object for the cache in milliseconds
     */
    public <T> void batch(String id, T object, long lifetime) {
        db.batch(object);
        ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
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
    public <T> com.cloudant.client.api.model.Response update(String id,
                                                             T object, long lifetime) {
        Response couchDbResponse = db.update(object);
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model
                .Response(
                couchDbResponse);
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
    public <T> com.cloudant.client.api.model.Response update(String id,
                                                             T object, int writeQuorum, long
                                                                     lifetime) {
        Response couchDbResponse = client.put(getDBUri(), object, false,
                writeQuorum, client.getGson());
        com.cloudant.client.api.model.Response response = new com.cloudant.client.api.model.Response(
                couchDbResponse);
        ((CacheWithLifetimes<String, Object>) cache).put(id, object, Util.getTime() + lifetime);
        return response;
    }

}
