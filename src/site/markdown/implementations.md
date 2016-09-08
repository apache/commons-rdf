<!--

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements. See the NOTICE file
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

-->

# Implementations

As a set of Java interfaces, Commons RDF must be used with one or more
implementations.

## Current implementations

### org.apache.commons.rdf.simple

[org.apache.commons.rdf.simple](apidocs/org/apache/commons/rdf/simple/package-summary.html)
is maintained as part of Commons RDF, and its main purpose is to verify and
clarify the [test harness](testapidocs/org/apache/commons/rdf/api/package-summary.html).
It is backed by simple (if not naive) in-memory POJO objects and have no external
dependencies.

Note that although this module fully implements the commons-rdf API, it should
**not** be considered as a reference implementation. It is **not thread-safe** and
probably **not scalable**, however it may be useful for testing and simple
usage (e.g. prototyping).

**Usage:**

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-rdf-simple</artifactId>
    <version>0.2.0-incubating-SNAPSHOT</version>
</dependency>
```

```java
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;

RDFTermFactory rdfTermFactory = new SimpleRDFTermFactory();
Graph graph = rdfTermFactory.createGraph();
```

### OWL API

[OWL API](http://owlapi.sourceforge.net/) 5 extends Commons RDF
directly for its family of
[RDFNode](https://github.com/owlcs/owlapi/blob/version5/api/src/main/java/org/semanticweb/owlapi/io/RDFNode.java#L25)
implementations.

For details, see [pull request #446](https://github.com/owlcs/owlapi/pull/446),
and [pull request #452](https://github.com/owlcs/owlapi/pull/452)).



## Planned implementations

The information in this section should not be considered updated or
authoritative as it describes ongoing development.

Feel free to [suggest changes](http://commonsrdf.incubator.apache.org/contributing.html) to the
[source code for this page](https://github.com/apache/incubator-commonsrdf/blob/master/src/site/markdown/implementations.md).



### Apache Jena

An implementation that maps [Apache Jena](http://jena.apache.org/) types
to Commons RDF is being developed on
the [`jena`](https://github.com/apache/incubator-commonsrdf/tree/jena)
branch of Commons RDF.

For details, see [COMMONSRDF-33](https://issues.apache.org/jira/browse/COMMONSRDF-33),
[JENA-1015](https://issues.apache.org/jira/browse/JENA-1015) or contact
[dev@commonsrdf](mail-lists.html).


### Eclipse RDF4j (formerly Sesame)

An implementation that maps [RDF4J 2.0](http://rdf4j.org/)
to Commons RDF is being developed on
the [`rdf4j`](https://github.com/apache/incubator-commonsrdf/tree/rdf4j)
branch of Commons RDF.


For details, see [COMMONSRDF-35](https://issues.apache.org/jira/browse/COMMONSRDF-35),
[SES-2091](https://openrdf.atlassian.net/browse/SES-2091) or contact
[dev@commonsrdf](mail-lists.html).


### Eclipse RDF4j (formerly Sesame)

An implementation that maps [JSON-LD-Java](https://github.com/jsonld-java/jsonld-java)
to Commons RDF is being developed on
the [`jsonld-java`](https://github.com/apache/incubator-commonsrdf/tree/jsonld-java/jsonld-java)
branch of Commons RDF.

This aims to support [JSON-LD](http://json-ld.org/) parsing and writing by adding
new interfaces like
[RDFParserBuilder](https://github.com/apache/incubator-commonsrdf/pull/21).

For details, see [COMMONSRDF-36](https://issues.apache.org/jira/browse/COMMONSRDF-36) or contact
[dev@commonsrdf](mail-lists.html).


### Apache Clerezza

[Apache Clerezza](https://clerezza.apache.org/) is
aligning its [RDF core](https://github.com/apache/clerezza-rdf-core) module
with Commons RDF.
