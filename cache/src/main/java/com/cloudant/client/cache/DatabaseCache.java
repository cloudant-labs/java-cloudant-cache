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

import java.io.InputStream;
import java.net.URI;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * A {@link Database} implementation with a cache.
 *
 * @author Arun Iyengar
 */
public class DatabaseCache implements Database {

    protected final Database db;
    protected final Cache<String, Object> cache;


    /**
     * Constructor which is designed to work with a variety of different caches.
     *
     * @param database      data structure with information about the database connection
     * @param cacheInstance cache instance which has already been created and initialized
     */
    public DatabaseCache(Database database, Cache<String, Object> cacheInstance) {
        this.db = database;
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
            value = db.find(classType, id);
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
            value = db.find(classType, id, params);
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
            value = db.findAny(classType, uri);
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
            return db.contains(id);
        }
    }

    /**
     * {@inheritDoc}
     * <P>
     * If the operation was successful then the object is also added to the cache.
     * </P>
     */
    public Response save(Object object) {
        Response response = db.save(object);
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
        Response response = db.save(object, writeQuorum);
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
        Response response = db.post(object);
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
        Response response = db.post(object, writeQuorum);
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
        Response response = db.update(object);
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
        Response response = db.update(object, writeQuorum);
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
        Response response = db.remove(object);
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
        List<Response> responses = db.bulk(list);
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

    /* Remainder of Database implementation follows, delegating calls to the Database instance
    specified on construction */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPermissions(String s, EnumSet<Permissions> enumSet) {
        db.setPermissions(s, enumSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, EnumSet<Permissions>> getPermissions() {
        return db.getPermissions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shard> getShards() {
        return db.getShards();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shard getShard(String s) {
        return db.getShard(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createIndex(String s, String s1, String s2, IndexField[] indexFields) {
        db.createIndex(s, s1, s2, indexFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createIndex(String s) {
        db.createIndex(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> findByIndex(String s, Class<T> aClass) {
        return db.findByIndex(s, aClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> findByIndex(String s, Class<T> aClass, FindByIndexOptions
            findByIndexOptions) {
        return db.findByIndex(s, aClass, findByIndexOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> listIndices() {
        return db.listIndices();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteIndex(String s, String s1) {
        db.deleteIndex(s, s1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Search search(String s) {
        return db.search(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DesignDocumentManager getDesignDocumentManager() {
        return db.getDesignDocumentManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewRequestBuilder getViewRequestBuilder(String s, String s1) {
        return db.getViewRequestBuilder(s, s1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllDocsRequestBuilder getAllDocsRequestBuilder() {
        return db.getAllDocsRequestBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Changes changes() {
        return db.changes();
    }

    /**
     * <P>
     * Note that cache implementations currently do not store multiple revisions, so this method
     * only operates remotely.
     * </P>
     * {@inheritDoc}
     */
    @Override
    public <T> T find(Class<T> aClass, String s, String s1) {
        return db.find(aClass, s, s1);
    }

    /**
     * <P>
     * Note that cache implementations only store objects, so InputStreams are returned from the
     * remote database.
     * </P>
     * {@inheritDoc}
     */
    @Override
    public InputStream find(String s) {
        return db.find(s);
    }

    /**
     * <P>
     * Note that cache implementations only store objects, so InputStreams are returned from the
     * remote database.
     * </P>
     * {@inheritDoc}
     */
    @Override
    public InputStream find(String s, String s1) {
        return db.find(s, s1);
    }

    /**
     * <P>
     * Note that cache implementations currently do not store multiple revisions, so this method
     * only operates remotely.
     * </P>
     * {@inheritDoc}
     */
    @Override
    public Response remove(String s, String s1) {
        return db.remove(s, s1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response saveAttachment(InputStream inputStream, String s, String s1) {
        return db.saveAttachment(inputStream, s, s1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response saveAttachment(InputStream inputStream, String s, String s1, String s2,
                                   String s3) {
        return db.saveAttachment(inputStream, s, s1, s2, s3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String invokeUpdateHandler(String s, String s1, Params params) {
        return db.invokeUpdateHandler(s, s1, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getDBUri() {
        return db.getDBUri();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DbInfo info() {
        return db.info();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ensureFullCommit() {
        db.ensureFullCommit();
    }
}
