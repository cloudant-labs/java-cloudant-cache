Contributing
=======

The java-cloudant-cache project is written in Java and uses gradle as its build tool and the maven central
repository for dependencies.

## Developer Certificate of Origin

In order for us to accept pull-requests, the contributor must sign-off a
[Developer Certificate of Origin (DCO)](DCO1.1.txt). This clarifies the
intellectual property license granted with any contribution. It is for your
protection as a Contributor as well as the protection of IBM and its customers;
it does not change your rights to use your own Contributions for any other purpose.

Please read the agreement and acknowledge it by ticking the appropriate box in the PR
 text, for example:

- [x] Tick to sign-off your agreement to the Developer Certificate of Origin (DCO) 1.1

## Requirements

- gradle
- Java 1.8

## Installing requirements

### Java

Follow the instructions for your platform.

### Gradle

The project uses the gradle wrapper to download  specified version of gradle.
The gradle wrapper is run by using the following command:

```bash
$ ./gradlew
```
Note: on windows the command to run is gradlew.bat rather than gradlew

## Coding guidelines

The project uses the [Google Java Style](https://google-styleguide.googlecode.com/svn/trunk/javaguide.html)
with the following changes:

```
4.2
    Our block indent is +4 characters

4.4
    Our line length is 100 characters.

4.5.2
    Indent continuation of +4 characters fine, but I think
    IDEA defaults to 8, which is okay too.
```

### Code Style

An IDEA code style matching these guidelines is included in the project,
in the `.idea` folder.

If you already have the project, to enable the code style follow these steps:

1. Go to _Preferences_ -> _Editor_ -> _Code Style_.
2. In the _Scheme_ dropdown, select _Project_.

IDEA will then use the style when reformatting, refactoring and so on.

## Building the library

The project should build in a cloned repository with:

```bash
$ ./gradlew assemble
```

## Running the tests

The tests run as part of Travis CI when branches are pushed to the github repository.

If you want to run the tests locally then note that some tests require additional services:
* `DatabaseCacheTests` need an Apache CouchDB instance or Cloudant instance and if it
is not at http://localhost:5789 you should set the system property `test.couch.url` with the correct
url e.g. (http://your.example:1234).
* `RedisCacheTests` require a running Redis instance on the default localhost:6379

Run the tests using:
```bash
$ ./gradlew test
```
