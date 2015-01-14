# Commons RDF

Working repository for experimenting with idea of providing a common library for [RDF 1.1](http://www.w3.org/TR/rdf11-concepts/) that could be implemented by the upcoming versions of the main Java toolkits ([Jena](http://jena.apache.org) 3.0 and [Sesame](http://openrdf.callimachus.net) 4.0) as well as wrappers for other JVM languages ([Banana RDF](https://github.com/w3c/banana-rdf) and so on).

The main motivation behind this simple library is revise an historical incompatibility issue. This library does not pretend to be a generic api wrapping those libraries, but a set of interfaces for the RDF 1.1 concepts that can be used to expose common RDF-1.1 concepts using common Java interfaces. In the initial phase commons-rdf is focused on a subset of the core concepts defined by RDF-1.1 (IRI, Blank Node, Literal, Triple, and Graph). In particular, commons RDF aims to provide a type-safe, non-general API that covers RDF 1.1. In a future phase we may define interfaces for Datasets and Quads.

## Status

[![Build Status](https://secure.travis-ci.org/commons-rdf/commons-rdf.svg?branch=master)](https://travis-ci.org/commons-rdf/commons-rdf)

This library is still work in progress.

A draft diagram of the interfaces which may be included in Commons RDF are:

![commons-rdf class diagram](src/main/resources/commons-rdf-class-diagram.png "commons-rdf class diagram")

## Building

Building has been tested with [Apache Maven 3.2](http://maven.apache.org/download.cgi) and [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/).

    $ mvn clean install
    [INFO] Scanning for projects...
    [INFO] 
    [INFO] Using the builder org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder with a thread count of 1
    [INFO]                                                                         
    [INFO] ------------------------------------------------------------------------
    [INFO] Building Commons RDF: API 0.0.3-SNAPSHOT
    ....
    [INFO] Installing /home/stain/src/commons-rdf/target/api-0.0.3-SNAPSHOT.jar to /home/stain/.m2/repository/com/github/commons-rdf/api/0.0.3-SNAPSHOT/api-0.0.3-SNAPSHOT.jar
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS

To then use from your project, add to Maven (update `<version>` to match the Maven output):

    <dependency>
        <groupId>com.github.commons-rdf</groupId>
        <artifactId>api</artifactId>
        <version>0.0.3-SNAPSHOT</version>
    </dependency>
    
    
### Java 6 compatibility

This API is targetting *Java 8*, as Java 7 is scheduled [EOL at April 2015](http://www.oracle.com/technetwork/java/javase/eol-135779.html).

For convenience, a patched version for Java 6 and 7 is however available.

To build with Java 1.6/1.7 compatibility, use the `java6` Maven profile:

    $ mvn clean install -Pjava6

To then depend on the Java 6 version in your Maven project, you need to use a special `classifier` to the dependency:

    <classifier>java6</classifier>

Note that the Java 6 version depends on the [Guava libraries](https://code.google.com/p/guava-libraries/) for providing the missing features.

## Example implementation

For a simple example of how to minimally implement this API, see
the [dummyimpl](src/test/java/com/github/commonsrdf/dummyimpl/)
package that is part of the unit tests.

Note that this is not to be considered as a 
reference implementation, although it
fully implements the commons-rdf API.


## Testing

The abstract classes
[AbstractGraphTest](src/test/java/com/github/commonsrdf/api/AbstractGraphTest.java)
and 
[AbstractRDFTermFactoryTest](src/test/java/com/github/commonsrdf/api/AbstractRDFTermFactoryTest.java)
can be realised as JUnit tests by implementations in order to verify that they
pass the minimal requirements of this API.

In order for this to work, your project will need to depend on the `tests`
classifier, for example (for Maven):

    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.11</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.github.commons-rdf</groupId>
        <artifactId>api</artifactId>
        <version>0.0.3-SNAPSHOT</version>
        <classifier>tests</classifier>
        <scope>test</scope>
    </dependency>

The extensions of each Test class needs to provide a 
[RDFTermFactory](src/test/java/com/github/commonsrdf/api/RDFTermFactory.java)
that can create the corresponding implementations of a `Graph`, `IRI`, etc.

For an example, see 
[DummyGraphTest](src/test/java/com/github/commonsrdf/dummyimpl/DummyGraphTest.java).


## Contributors

* Sergio Fernández ([Apache Marmotta](http://marmotta.apache.org))
* Andy Seaborne ([Apache Jena](http://jena.apache.org))
* Peter Ansell ([OpenRDF Sesame](http://openrdf.callimachus.net))
* Stian Soiland-Reyes (([Apache Taverna](http://taverna.incubator.apache.org))

See [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute. In short - raise a Github pull request.


## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

