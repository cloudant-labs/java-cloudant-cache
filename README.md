# THIS PROJECT IS NO LONGER MAINTAINED

# Java Cloudant Cache

A Java object cache for the [java-cloudant cloudant-client](https://github.com/cloudant/java-cloudant) and
 Cloudant databases.

This library is unsupported.

## Usage

The minimum supported runtime is Java 1.8.

### Add dependency from the maven central repository

Add a dependency on one of the three artifacts produced by the project. The choice depends on
 the cache implementation you require. The dependencies are published in maven central so can be
 added by group and artifact ID to either maven or gradle build files. The gradle dependency snippet
 for each is shown below.


* Cache: **cloudant-client-cache**. Provides all the cache interfaces and utility functions as well
as a very simple LRU `com.cloudant.client.cache.Cache` implementation.
```groovy
dependencies {
    compile group: 'com.cloudant', name: 'cloudant-client-cache', version: 'latest.release'
}
```
* In-process cache: **cloudant-client-cache-in-process** (depends on cloudant-client-cache). Provides a
`com.cloudant.client.cache.CacheWithLifetimes` implementation that runs in-process.
```groovy
dependencies {
    compile group: 'com.cloudant', name: 'cloudant-client-cache-in-process', version: 'latest.release'
}
```
In these examples, the cache will run in the same process as the application.
* Redis cache: **cloudant-client-cache-redis** (depends on cloudant-client-cache). Provides a
`com.cloudant.client.cache.CacheWithLifetimes` implementation that uses a Redis instance as the store.
```groovy
dependencies {
    compile group: 'com.cloudant', name: 'cloudant-client-cache-redis', version: 'latest.release'
}
```
In this example, Redis must be running in another process.  Information about Redis, including download information, is available from:
http://redis.io/
### Instantiate a cache

* `com.cloudant.client.cache.LRUCache`:
```java
// Example with a maximum capacity of 100 objects
Cache<String, Object> cache = new LRUCache<>(100);
```
* `com.cloudant.client.cache.inprocess.InProcessCache`:
```java
// Example with up to 100 objects with a default 1 minute lifetime:
CacheWithLifetimes<String, Object> cache = new InProcessCache<>(100, 60000);
```
* `com.cloudant.client.cache.redis.RedisCache`:
```java
// Example with a default 1 minute lifetime, connected to a local Redis instance:
CacheWithLifetimes<String, Object> cache = new RedisCache<>("localhost", 60000);
```

### Configure the cache with your `com.cloudant.client.api.Database` instance

The `com.cloudant.client.cache.DatabaseCache` and
`com.cloudant.client.cache.DatabaseCacheWithLifetimes` classes extend the
java-cloudant `com.cloudant.client.api.Database` so an existing application can
add cache functionality with minimal code changes.

```java
// Get the Cloudant client instance and database object.
CloudantClient client = ClientBuilder.account("example").build();
Database db = client.database("example-database", false);

// Create a new DatabaseCache with the database and cache instances.
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
### Directly accessing caches from an application program
Caches can be directly accessed and modified using the methods of com.cloudant.client.cache.Cache and com.cloudant.client.cache.CacheWithLifetimes.  For example, the following method call adds "object1" with key “key1” to the cache. “lifetime” is the lifetime of the cached value in milliseconds:
```java
       cache.put(key1, object1, lifetime);
```
The following method call deletes the object indexed by "key2" from the cache if it exists:
```java
       cache.delete(key2);
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
