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

package com.cloudant.client.cache.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Params;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.cache.Cache;
import com.cloudant.client.cache.DatabaseCache;
import com.cloudant.client.cache.LRUCache;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.util.UUID;

/**
 * This class provides tests for the DatabaseCache, validating that the cache can operate
 * transparently in front of a Database instance.
 */
public class DatabaseCacheTests {

    // Class resources
    private static CloudantClient client;

    // Test instance resources
    private Cache<String, Object> cache = null;
    private String dbName = null;
    private Database db = null;
    private Foo foo = null;

    /**
     * Create a client that connects to a local CouchDB instance before running any tests in the
     * class.
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setupClient() throws Exception {
        URL url = new URL(System.getProperty("test.couch.url", "http://localhost:5984"));
        client = ClientBuilder.url(url).build();
    }

    /**
     * After the tests shutdown the client.
     */
    @AfterClass
    public static void shutdownClient() {
        client.shutdown();
    }

    /**
     * Before each test:
     * <OL>
     * <LI>Create a cache instance (use the simplest cache implementation)</LI>
     * <LI>Generate a unique database name</LI>
     * <LI>Create a database using the name</LI>
     * <LI>Wrap the created database with a DatabaseCache using the created cache</LI>
     * <LI>Create a Foo document instance to work with</LI>
     * </OL>
     * Create a new unique database name before each test and initialize it wrapped with the
     * simplest cache implementation.
     */
    @Before
    public void setupForTest() {
        cache = new LRUCache<>(10);
        dbName = "database-cache-tests-" + UUID.randomUUID().toString();
        db = new DatabaseCache(client.database(dbName, true), cache);
        foo = new Foo(UUID.randomUUID().toString());
    }

    @After
    public void deleteDatabase() {
        client.deleteDB(dbName);
    }

    /**
     * Test that a find retrieves a document from the cache
     */
    @Test
    public void testCacheGetByFind() {
        setupForGet();
        // Do a db.find, but expect the return from the cache
        Foo retrievedFoo = db.find(Foo.class, foo._id);
        assertCacheGet(retrievedFoo);
    }

    /**
     * Test that a find with read quorum params retrieves a document from the cache
     */
    @Test
    public void testCacheGetByFindWithParams() {
        setupForGet();
        // Do a db.find, but expect the return from the cache
        Foo retrievedFoo = db.find(Foo.class, foo._id, new Params().readQuorum(1));
        assertCacheGet(retrievedFoo);
    }

    /**
     * Test that retrieving a document by URI adds it to the cache and that subsequent retrieval
     * is from the cache.
     */
    @Test
    public void testCacheGetByFindAny() {
        // Create the document in the database
        Response r = db.post(foo);
        assertCachePut();

        // Ensure the rev is up-to-date
        foo._rev = r.getRev();

        String docUri = db.getDBUri() + "/" + foo._id;
        Foo retrievedFoo = db.findAny(Foo.class, docUri);
        // The entry should now be in the cache twice, once with ID as key, and once with url as key
        assertCacheSize(2);
        assertEquals("The retrieved foo should match the expected", foo, retrievedFoo);
        assertEquals("The cache should contain foo with uri as key", foo, cache.get(docUri));

        // Remove the connection to the database so we can only use the cache
        Database cacheOnly = new DatabaseCache(null, cache);
        // This will fail if the entry is not in the cache, because there is no database
        retrievedFoo = cacheOnly.findAny(Foo.class, docUri);
        assertEquals("The retrieved foo should match the expected", foo, retrievedFoo);
    }

    /**
     * Test that a contains check works using the cache
     */
    @Test
    public void testCacheGetContains() {
        setupForGet();
        assertTrue("The contains check should return true because the document is in the cache",
                db.contains(foo._id));
    }


    /**
     * Test that a save inserts a document into the cache
     */
    @Test
    public void testCachePutBySave() {
        db.save(foo);
        assertCachePut();
    }

    /**
     * Test that a save (with quorum) inserts a document into the cache
     */
    @Test
    public void testCachePutBySaveWithQuorum() {
        db.save(foo, 1);
        assertCachePut();
    }

    /**
     * Test that a post inserts a document into the cache
     */
    @Test
    public void testCachePutByPost() {
        db.post(foo);
        assertCachePut();
    }

    /**
     * Test that a post (with quorum) inserts a document into the cache
     */
    @Test
    public void testCachePutByPostWithQuorum() {
        db.post(foo, 1);
        assertCachePut();
    }

    /**
     * Test that an update updates a document in the cache
     */
    @Test
    public void testCacheUpdate() {
        // Since this test updates an object (which is referenced locally in the cache) we need to
        // perform the update using a separate object, not modifying the original object.

        // Create the document in the DB from foo and assert it is present in the cache
        Foo createdFoo = new Foo(foo._id);
        Response create = db.post(createdFoo);
        assertCachePut(createdFoo);

        // Store the rev for the update
        foo._rev = create.getRev();

        // Do an update
        foo.testField = "new";
        db.update(foo);

        // Assert that the object in the cache is foo, not the original createdFoo
        assertCachePut();
    }

    /**
     * Test that an update with quorum updates a document in the cache
     */
    @Test
    public void testCacheUpdateWithQuorum() {
        // Since this test updates an object (which is referenced locally in the cache) we need to
        // perform the update using a separate object, not modifying the original object.

        // Create the document in the DB from foo and assert it is present in the cache
        Foo createdFoo = new Foo(foo._id);
        Response create = db.post(createdFoo);
        assertCachePut(createdFoo);

        // Store the rev for the update
        foo._rev = create.getRev();

        // Do an update
        foo.testField = "new";
        db.update(foo, 1);

        // Assert that the object in the cache is foo, not the original createdFoo
        assertCachePut();
    }

    /**
     * Test that a db remove also removes the entry from the cache
     */
    @Test
    public void testCacheRemove() {
        // To remove the document it needs to exist in the DB; so post first
        Response r = db.post(foo);
        // Assert that the entry was cached
        assertCacheSize(1);
        // To remove we need a rev, so set it based on the response
        foo._rev = r.getRev();
        // Now call db.remove
        db.remove(foo);
        // Assert that the cache is now empty
        assertCacheSize(0);
    }

    /**
     * Assert that the cache contains a single entry and that it is the expected foo.
     */
    private void assertCachePut() {
        assertCacheSize(1);
        assertCachePut(foo);
    }

    /**
     * Assert that the cache contains the expected foo.
     *
     * @param expectedFoo the object expected in the cache
     */
    private void assertCachePut(Foo expectedFoo) {
        assertEquals("The object in the cache should match the one posted", expectedFoo, cache
                .get(expectedFoo._id));
    }

    /**
     * Assert that the cache size is equal to the expected size
     *
     * @param expectedSize the expected size of the cache
     */
    protected void assertCacheSize(int expectedSize) {
        assertEquals("Cache size should be " + expectedSize, expectedSize, cache.size());
    }

    /**
     * Put an object directly in the cache (not the DB) so we can validate that we actually
     * retrieve from the cache. By not being in the database if the cache missed then we would
     * get a 404 back from the database.
     */
    private void setupForGet() {
        cache.put(foo._id, foo);
    }

    /**
     * Assert that the retrieved foo matches the expected foo.
     *
     * @param retrievedFoo the Foo retrieved from the cache
     */
    private void assertCacheGet(Foo retrievedFoo) {
        assertEquals("The retrieved document should match", foo, retrievedFoo);
    }

    private static final class Foo {
        String _id;
        String _rev;
        String testField;

        Foo(String id) {
            this._id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Foo foo = (Foo) o;

            if (_id != null ? !_id.equals(foo._id) : foo._id != null) {
                return false;
            }
            if (_rev != null ? !_rev.equals(foo._rev) : foo._rev != null) {
                return false;
            }
            return !(testField != null ? !testField.equals(foo.testField) : foo.testField != null);

        }

        @Override
        public int hashCode() {
            int result = _id != null ? _id.hashCode() : 0;
            result = 31 * result + (_rev != null ? _rev.hashCode() : 0);
            result = 31 * result + (testField != null ? testField.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Foo{");
            sb.append("_id='").append(_id).append('\'');
            sb.append(", _rev='").append(_rev).append('\'');
            sb.append(", testField='").append(testField).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
