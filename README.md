# Apache Commons RDF (Incubating)

[![Build Status](https://travis-ci.org/apache/incubator-commonsrdf.svg?branch=master)](https://travis-ci.org/apache/incubator-commonsrdf)
[![Coverage Status](https://coveralls.io/repos/apache/incubator-commonsrdf/badge.svg)](https://coveralls.io/r/apache/incubator-commonsrdf)

Commons RDF aims to provide a common library for [RDF 1.1](http://www.w3.org/TR/rdf11-concepts/) 
that could be implemented by the upcoming versions of the main Java toolkits 
([Jena](http://jena.apache.org/) 3.x and [Sesame](http://openrdf.callimachus.net/) 
4.x) as well as for other libraries ([OWLAPI](http://owlapi.sourceforge.net/),
[Clerezza](http://clerezza.apache.org/)) and other JVM languages.

The main motivation behind this simple library is revise an historical incompatibility 
issue. This library does not pretend to be a generic api wrapping those libraries, 
but a set of interfaces for the RDF 1.1 concepts that can be used to expose common 
RDF-1.1 concepts using common Java interfaces. In the initial phase commons-rdf 
is focused on a subset of the core concepts defined by RDF-1.1 (URI/IRI, Blank Node, 
Literal, Triple, and Graph). In particular, commons RDF aims to provide a type-safe, 
non-general API that covers RDF 1.1. In a future phase we may define interfaces 
for Datasets and Quads.

A draft diagram of the interfaces which may be included in Commons RDF are:

![commons-rdf class diagram](api/src/main/resources/commons-rdf-class-diagram.png "commons-rdf class diagram")

This library is still <strong>work in progress</strong>. Therefore everybody is
welcomed to join the project and contribute!

## Disclaimer

Apache Commons RDF is an effort undergoing incubation at [The Apache Software Foundation
(ASF)](http://apache.org/) sponsored by the [Apache Incubator PMC](http://incubator.apache.org/).
Incubation is required of all newly accepted projects until a further review
indicates that the infrastructure, communications, and decision making process
have stabilized in a manner consistent with other successful ASF projects.
While incubation status is not necessarily a reflection of the completeness or
stability of the code, it does indicate that the project has yet to be fully
endorsed by the ASF.

## Building

Building has been tested with [Apache Maven 3.2](http://maven.apache.org/download.cgi) and [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/).

    $ mvn clean install
    [INFO] Scanning for projects...
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Build Order:
    [INFO] 
    [INFO] Commons RDF
    [INFO] Commons RDF: API
    [INFO] Commons RDF: Simple impl
    [INFO] 
    [INFO] Using the builder org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder with a thread count of 1
    [INFO]                                                                         
    [INFO] ------------------------------------------------------------------------
    [INFO] Building Commons RDF 0.0.3-SNAPSHOT
        ....
    [INFO] Installing /home/johndoe/src/commons-rdf/commons-rdf-api/target/commons-rdf-api-0.0.3-SNAPSHOT-javadoc.jar to /home/johndoe/.m2/repository/org/apache/commons/commons-rdf/commons-rdf-api/0.0.3-SNAPSHOT/commons-rdf-api-0.0.3-SNAPSHOT-javadoc.jar
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary:
    [INFO] 
    [INFO] Commons RDF ....................................... SUCCESS [  1.792 s]
    [INFO] Commons RDF: API .................................. SUCCESS [  2.676 s]
    [INFO] Commons RDF: Simple impl .......................... SUCCESS [  3.142 s]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 7.718 s
    [INFO] Finished at: 2015-01-26T02:09:10+00:00
    [INFO] Final Memory: 22M/309M
    [INFO] ------------------------------------------------------------------------


To then use from your project, add to Maven (update `<version>` to match the Maven output):

    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-rdf-api</artifactId>
        <version>0.0.3-SNAPSHOT</version>
    </dependency>
    
## Simple implementation

The [commons-rdf-simple](simple) module contains a 
simple (if not naive) implementation of the Commons RDF API 
using in-memory POJO objects.

Note that although this module fully implements the commons-rdf API,
it should *not*  be considered a reference implementation. 
It is not thread-safe nor scalable, but may be useful for testing
and simple usage (e.g. output from an independent RDF parser).

Projects including [Apache Jena](http://jena.apache.org/) 
and [OpenRDF Sesame](http://rdf4j.org/) aim to provide 
complete and scalable implementations of the Commons RDF API. 

## Testing

The abstract classes
[AbstractGraphTest](api/src/test/java/org/apache/commons/rdf/api/AbstractGraphTest.java)
and 
[AbstractRDFTermFactoryTest](api/src/test/java/org/apache/commons/rdf/api/AbstractRDFTermFactoryTest.java)
can be realised as JUnit tests by implementations in order to verify that they
pass the minimal requirements of this API.

In order for this to work, your project will need to depend on the `tests`
classifier for the commons-rdf-api module, for example (for Maven):

    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-rdf-api</artifactId>
        <version>0.0.3-SNAPSHOT</version>
        <classifier>tests</classifier>
        <scope>test</scope>
    </dependency>

The extensions of each Test class need to provide a 
[RDFTermFactory](api/src/main/java/org/apache/commons/rdf/api/RDFTermFactory.java)
that can create the corresponding implementations of a `Graph`, `IRI`, etc.

For an example, see 
[SimpleGraphTest](simple/src/test/java/org/apache/commons/rdf/simple/SimpleGraphTest.java).

## Contributors

* Sergio Fernández ([Apache Marmotta](http://marmotta.apache.org))
* Andy Seaborne ([Apache Jena](http://jena.apache.org))
* Peter Ansell ([OpenRDF Sesame](http://rdf4j.org/))
* Stian Soiland-Reyes ([Apache Taverna](http://taverna.incubator.apache.org))

See [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute. In short - raise a Github pull request.

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

