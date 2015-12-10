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
    <version>0.1.0-incubating-SNAPSHOT</version>
</dependency>
```

```java
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;

RDFTermFactory rdfTermFactory = new SimpleRDFTermFactory();
Graph graph = rdfTermFactory.createGraph();
```

## Planned implementations

The information in this section should not be considered updated or
authoritative as it relies on external project planning.

Feel free to [suggest changes](http://commonsrdf.incubator.apache.org/contributing.html) to the
[source code for this page](https://github.com/apache/incubator-commonsrdf/blob/master/src/site/markdown/implementations.md).



### Apache Jena

[Apache Jena](http://jena.apache.org/) is considering a compatibility interface
that provides and consumes Commons RDF objects
([JENA-1015](https://issues.apache.org/jira/browse/JENA-1015)).


### RDF4j Sesame

[Sesame](http://rdf4j.org/) is planning to support Commons RDF
([SES-2091](https://openrdf.atlassian.net/browse/SES-2091)).


### Apache Clerezza

[Apache Clerezza](https://clerezza.apache.org/) is
aligning its [RDF core](https://github.com/apache/clerezza-rdf-core) module
with Commons RDF.


### OWL API

[OWL API](http://owlapi.sourceforge.net/) is considering the integration with
Commons RDF in OWLAPI 5
(<strike>[pull request #446](https://github.com/owlcs/owlapi/pull/446)</strike>,
[pull request #452](https://github.com/owlcs/owlapi/pull/452)).
