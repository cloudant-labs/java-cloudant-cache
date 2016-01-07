Contributing
=======

java-cloudant-cache is written in Java and uses gradle as its build tool and the maven central
repository for dependencies.


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

Adopting the [Google Java Style](https://google-styleguide.googlecode.com/svn/trunk/javaguide.html)
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

# Building the library

The project should build out of the box with:

```bash
$ ./gradlew compileJava
```
###Running the tests
#### Configuration

```bash
$ ./gradlew test
```
