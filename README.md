# Java Cloudant Cache

A Java object cache for the [java-cloudant cloudant-client](https://github.com/cloudant/java-cloudant) and
 Cloudant databases.

## Usage

The minimum supported runtime is Java 1.8.

### Add dependency from the maven central repository

Add a dependency on one of the three artifacts produced by the project. The choice depends on
 the cache implementation you require. The dependencies are published in maven central so can be
 added by group and artifact ID to either maven or gradle build files. The gradle dependency snippet
 for each is shown below.


* Cache: **cloudant-client-cache**. Provides all the cache interfaces and utility functions as well
as a very simple LRU `Cache` implementation.
```groovy
dependencies {
    compile group: 'com.cloudant', name: 'cloudant-client-cache', version: 'latest.release'
}
```
* In-process cache: **cloudant-client-cache-in-process** (depends on cloudant-client-cache). Provides a
`CacheWithLifetimes` implementation that runs in-process.
```groovy
dependencies {
    compile group: 'com.cloudant', name: 'cloudant-client-cache-in-process', version: 'latest.release'
}
```
* Redis cache: **cloudant-client-cache-redis** (depends on cloudant-client-cache). Provides a
`CacheWithLifetimes` implementation that uses a Redis instance as the store.
```groovy
dependencies {
    compile group: 'com.cloudant', name: 'cloudant-client-cache-redis', version: 'latest.release'
}
```

### Instantiate a cache

* `LRUCache`:
```java
// Example with a maximum capacity of 100 objects
Cache<String, Object> cache = new LRUCache<>(100);
```
* `InProcessCache`:
```java
// Example with up to 100 objects with a default 1 minute lifetime:
CacheWithLifetimes<String, Object> cache = new InProcessCache<>(100, 60000);
```
* `RedisCache`:
```java
// Example with a default 1 minute lifetime, connected to a local Redis instance:
CacheWithLifetimes<String, Object> cache = new RedisCache<>("localhost", 60000);
```

### Configure the cache with your `Database` instance

```java
// Get the Cloudant client instance and database object.
CloudantClient client = ClientBuilder.account("example").build();
Database db = client.database("example-database", false);

// Create a new DatabaseCache with the database and cache instances.
// The `DatabaseCache` and `DatabaseCacheWithLifetimes` classes implement the java-cloudant Database
// interface so an existing application can add a local cache with minimal code changes.
Database cachedDb = new DatabaseCache(db, cache);
// Use this cachedDb instance in place of your normal db instance to utilise the cache.
// Keep references to both instances to switch between cached and un-cached access to the database.

// Example 1: Get document with ID "abcdef" from the cache if available, or from the database if not
// yet cached.
MyDocument abc = cachedDb.find(MyDocument.class, "abcdef");

// Example 2: Use the original Database instance, db, to get document "abcdef" direct from the
// remote database, bypassing the cache.
MyDocument abc = db.find(MyDocument.class, "abcdef");

// Example 3: Use a lifetime cache with a 1 minute lifetime on objects
Database lifetimeCachedDb = new DatabaseCacheWithLifetimes(db, cache, 1, TimeUnit.MINUTES);
// Wrap the same database and cache instances multiple times with different lifetimes to easily set
// different lifetimes for different objects.
```

## Related documentation
* [API reference (javadoc)](http://www.javadoc.io/doc/com.cloudant/cloudant-client-cache/)
* [In-process cache API reference (javadoc)](http://www.javadoc.io/doc/com.cloudant/cloudant-client-cache-in-process/)
* [Redis cache API reference (javadoc)](http://www.javadoc.io/doc/com.cloudant/cloudant-client-cache-redis/)
* [Client (java-cloudant) API reference (javadoc)](http://www.javadoc.io/doc/com.cloudant/cloudant-client/)
* [Cloudant docs](http://docs.cloudant.com/)
* [Cloudant for developers](https://cloudant.com/for-developers/)

## Development

For information about contributing, building, and running tests see the [CONTRIBUTING.md](CONTRIBUTING.md).

## License

Copyright 2016 Cloudant, an IBM company.

Licensed under the apache license, version 2.0 (the "license"); you may not use this file except in compliance with the license. You may obtain a copy of the license at

    http://www.apache.org/licenses/LICENSE-2.0.html

Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "as is" basis, without warranties or conditions of any kind, either express or implied. See the license for the specific language governing permissions and limitations under the license.

## Issues

Please open issues [here in github](../../issues).
