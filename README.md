# Apache Commons RDF (Incubating)

[![Build Status](https://travis-ci.org/apache/incubator-commonsrdf.svg?branch=master)](https://travis-ci.org/apache/incubator-commonsrdf)
[![Coverage Status](https://coveralls.io/repos/apache/incubator-commonsrdf/badge.svg)](https://coveralls.io/r/apache/incubator-commonsrdf)

[Commons RDF](http://commonsrdf.incubator.apache.org/) aims to provide a common
library for [RDF 1.1](http://www.w3.org/TR/rdf11-concepts/) that could be
implemented by [Jena](http://jena.apache.org/) as well as for other libraries
such as [OWLAPI](http://owlapi.sourceforge.net/), 
[Clerezza](http://clerezza.apache.org/) and other JVM languages.

The main motivation behind this simple library is to revise an historical incompatibility 
issue between these toolkits. This library does not pretend to be a generic API wrapping those libraries, 
but is a set of common Java interfaces for the RDF 1.1 concepts, e.g. `IRI`, `BlankNode`, 
`Graph`, accompanied with unit test cases for their expected behaviour, and a `simple` 
implementation, which main purpose is to clarify the tests and interfaces.

In particular, Commons RDF aims to provide a type-safe, 
non-general API that covers RDF 1.1. In a future phase we may define interfaces 
for Datasets and Quads.

A draft diagram of the interfaces which may be included in Commons RDF are:

<a href="src/site/resources/images/class-diagram.png"><img height="400" src="src/site/resources/images/class-diagram.png" alt="Class diagram" /></a>


This library is still <strong>work in progress</strong>. Therefore everybody is
welcomed to join the project and [contribute](http://commonsrdf.incubator.apache.org/contributing.html)!

See the [Commons RDF homepage](http://commonsrdf.incubator.apache.org/) for more details.

## License


Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the [NOTICE](NOTICE) file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


## Contributing

Feel free to subscribe to the 
[dev@commonsrdf](http://mail-archives.apache.org/mod_mbox/incubator-commonsrdf-dev/) 
mailing list to follow the ongoing development of Commons RDF, ask questions 
about its usage, or help shape Commons RDF by 
[contributing](http://commonsrdf.incubator.apache.org/contributing.html)
your ideas, code and use cases. 
 


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


To then use this build from your project, add to Maven (update `<version>` to match the Maven output):

    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-rdf-api</artifactId>
        <version>0.2.0-incubating-SNAPSHOT</version>
    </dependency>

See the [downloads](http://commonsrdf.incubator.apache.org/download.html) to 
use the latest stable release published in Maven Central.
    

## Snapshot repository

The Apache Commons RDF project is aiming to regularly release early 
previews releases (0.x.y versions) and publish these to Maven Central.
See the [downloads](http://commonsrdf.incubator.apache.org/download.html) to 
use the latest stable release.


However, if you are following the ongoing
development on [dev@commonsrdf](http://mail-archives.apache.org/mod_mbox/incubator-commonsrdf-dev/), 
you may want to try the [snapshot builds](https://builds.apache.org/job/incubator-commonsrdf/), 
which are automatically deployed to the 
[Apache snapshot repository](https://repository.apache.org/content/groups/snapshots/org/apache/commons/commons-rdf-api/).

To use these snapshots from your Maven project, depend on the latest `*-SNAPSHOT` version
as found in the current [pom.xml](pom.xml), and add to your own `pom.xml`:

```xml
<repositories>
  <repository>
    <id>apache.snapshots</id>
    <name>Apache Snapshot Repository</name>
    <url>http://repository.apache.org/snapshots</url>
    <releases>
      <enabled>false</enabled>
    </releases>
  </repository>
</repositories>
```
 

## Simple implementation

The [commons-rdf-simple](simple) module contains a 
simple (if not naive) implementation of the Commons RDF API 
using in-memory POJO objects.

Note that although this module fully implements the commons-rdf API,
it should *not*  be considered a reference implementation. 
It is not thread-safe nor scalable, but may be useful for testing
and simple usage (e.g. output from an independent RDF parser).

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
        <version>0.2.0-incubating-SNAPSHOT</version>
        <classifier>tests</classifier>
        <scope>test</scope>
    </dependency>

The extensions of each Test class need to provide a 
[RDFTermFactory](api/src/main/java/org/apache/commons/rdf/api/RDFTermFactory.java)
that can create the corresponding implementations of a `Graph`, `IRI`, etc.

For an example, see 
[SimpleGraphTest](simple/src/test/java/org/apache/commons/rdf/simple/SimpleGraphTest.java).



