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

import com.cloudant.client.api.Changes;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.DesignDocumentManager;
import com.cloudant.client.api.Search;
import com.cloudant.client.api.model.DbInfo;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.Index;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.Params;
import com.cloudant.client.api.model.Permissions;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.model.Shard;
import com.cloudant.client.api.views.AllDocsRequestBuilder;
import com.cloudant.client.api.views.ViewRequestBuilder;
import com.cloudant.client.org.lightcouch.DocumentConflictException;
import com.cloudant.client.org.lightcouch.NoDocumentException;

import java.io.InputStream;
import java.net.URI;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Contains a Database Public API implementation with a cache.
 *
 * @author Arun Iyengar
 */
public class DatabaseCache implements Database {

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

    /* Database methods follow that will interact with the cache */

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
     * @param object The object to save
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the save.
     */
    public Response save(Object object) {
        Response response = db.save(object);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * Saves an object in the database, using HTTP <tt>PUT</tt> request.
     * <p>
     * If the object doesn't have an <code>_id</code> value, the code will
     * assign a <code>UUID</code> as the document id.
     *
     * @param object      The object to save
     * @param writeQuorum the write Quorum
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the save.
     */
    public Response save(Object object, int writeQuorum) {
        Response response = db.save(object, writeQuorum);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * Saves an object in the database using HTTP <tt>POST</tt> request.
     * <p>
     * The database will be responsible for generating the document id.
     *
     * @param object The object to save
     * @return {@link Response}
     */
    public Response post(Object object) {
        Response response = db.post(object);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * Saves an object in the database using HTTP <tt>POST</tt> request with
     * specificied write quorum
     * <p>
     * The database will be responsible for generating the document id.
     *
     * @param object      The object to save
     * @param writeQuorum the write Quorum
     * @return {@link Response}
     */
    public Response post(Object object, int writeQuorum) {
        Response response = db.post(object, writeQuorum);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * Updates an object in the database, the object must have the correct
     * <code>_id</code> and <code>_rev</code> values.
     *
     * @param object The object to update
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the update.
     */
    public Response update(Object object) {
        Response response = db.update(object);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * Updates an object in the database, the object must have the correct
     * <code>_id</code> and <code>_rev</code> values.
     *
     * @param object      The object to update
     * @param writeQuorum the write Quorum
     * @return {@link Response}
     * @throws DocumentConflictException If a conflict is detected during the update.
     */
    public Response update(Object object, int writeQuorum) {
        Response response = db.update(object, writeQuorum);
        cachePut(response.getId(), object);
        return response;
    }

    /**
     * Removes a document from the database.
     * <p>
     * The object must have the correct <code>_id</code> and <code>_rev</code>
     * values.
     *
     * @param object The document to remove as object.
     * @return {@link Response}
     * @throws NoDocumentException If the document is not found in the database.
     */
    public Response remove(Object object) {
        Response response = db.remove(object);
        cache.delete(response.getId());
        return response;
    }

    /* Remainder of Database implementation follows, delegating calls to the Database instance
    specified on construction */

    @Override
    public void setPermissions(String s, EnumSet<Permissions> enumSet) {
        db.setPermissions(s, enumSet);
    }

    @Override
    public Map<String, EnumSet<Permissions>> getPermissions() {
        return db.getPermissions();
    }

    @Override
    public List<Shard> getShards() {
        return db.getShards();
    }

    @Override
    public Shard getShard(String s) {
        return db.getShard(s);
    }

    @Override
    public void createIndex(String s, String s1, String s2, IndexField[] indexFields) {
        db.createIndex(s, s1, s2, indexFields);
    }

    @Override
    public void createIndex(String s) {
        db.createIndex(s);
    }

    @Override
    public <T> List<T> findByIndex(String s, Class<T> aClass) {
        return db.findByIndex(s, aClass);
    }

    @Override
    public <T> List<T> findByIndex(String s, Class<T> aClass, FindByIndexOptions
            findByIndexOptions) {
        return db.findByIndex(s, aClass, findByIndexOptions);
    }

    @Override
    public List<Index> listIndices() {
        return db.listIndices();
    }

    @Override
    public void deleteIndex(String s, String s1) {
        db.deleteIndex(s, s1);
    }

    @Override
    public Search search(String s) {
        return db.search(s);
    }

    @Override
    public DesignDocumentManager getDesignDocumentManager() {
        return db.getDesignDocumentManager();
    }

    @Override
    public ViewRequestBuilder getViewRequestBuilder(String s, String s1) {
        return db.getViewRequestBuilder(s, s1);
    }

    @Override
    public AllDocsRequestBuilder getAllDocsRequestBuilder() {
        return db.getAllDocsRequestBuilder();
    }

    @Override
    public Changes changes() {
        return db.changes();
    }

    @Override
    public <T> T find(Class<T> aClass, String s, String s1) {
        return db.find(aClass, s, s1);
    }

    @Override
    public InputStream find(String s) {
        return db.find(s);
    }

    @Override
    public InputStream find(String s, String s1) {
        return db.find(s, s1);
    }

    @Override
    public Response remove(String s, String s1) {
        return db.remove(s, s1);
    }

    @Override
    public List<Response> bulk(List<?> list) {
        return db.bulk(list);
    }

    @Override
    public Response saveAttachment(InputStream inputStream, String s, String s1) {
        return db.saveAttachment(inputStream, s, s1);
    }

    @Override
    public Response saveAttachment(InputStream inputStream, String s, String s1, String s2,
                                   String s3) {
        return db.saveAttachment(inputStream, s, s1, s2, s3);
    }

    @Override
    public String invokeUpdateHandler(String s, String s1, Params params) {
        return db.invokeUpdateHandler(s, s1, params);
    }

    @Override
    public URI getDBUri() {
        return db.getDBUri();
    }

    @Override
    public DbInfo info() {
        return db.info();
    }

    @Override
    public void ensureFullCommit() {
        db.ensureFullCommit();
    }
}
