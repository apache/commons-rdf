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

The Commons RDF API must be used with one or more
implementations.

The Apache Commons RDF distribution includes bindings for the implementations:

* [Commons RDF Simple](#Commons_RDF_Simple)
* [Apache Jena](#Apache_Jena)
* [Eclipse RDF4J](#Eclipse_RDF4J) (formerly Sesame)
* [JSONLD-Java](#JSONLD-Java)

In addition there can be [External implementations](#External-implementations)
which are released separately by their respective projects.

One goal of the Commons RDF API is to enable runtime cross-compatibility
of its implementations, therefore it is perfectly valid to combine them
and for instance do:

* Copy triples from a Jena `Model` to an RDF4J `Repository`  (e.g. copying between two Common RDF `Graph`s)
* Create an RDF4J-backed `Quad` that use a Jena-backed `BlankNode`
* Read an RDF file with Jena's parsers into an RDF4J-backed `Dataset`


### Commons RDF Simple

[org.apache.commons.rdf.simple](apidocs/org/apache/commons/rdf/simple/package-summary.html)
is part of Commons RDF, and its main purpose is to verify and
clarify the [test harness](testapidocs/org/apache/commons/rdf/api/package-summary.html).
It is backed by simple (if not naive) in-memory POJO objects and have no external
dependencies.

Note that although this module fully implements the commons-rdf API, it should
**not** be considered as a reference implementation. It is **not thread-safe** and
probably **not scalable**, however it may be useful for testing and simple
usage (e.g. prototyping and creating graph fragments).

**Usage:**

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-rdf-simple</artifactId>
    <version>0.5.0</version>
</dependency>
```

```java
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.simple.SimpleRDF;

RDF rdf = new SimpleRDF();
Graph graph = rdf.createGraph();
```

### Apache Jena

[org.apache.commons.rdf.jena](apidocs/org/apache/commons/rdf/jena/package-summary.html) is an implementation of the Commons RDF API backed by [Apache Jena](http://jena.apache.org/), including converters from/to Jena and Commons RDF.


**Usage:**

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-rdf-jena</artifactId>
    <version>0.5.0</version>
</dependency>
```

```java
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.jena.JenaRDF;

RDF rdf = new JenaRDF();
Graph graph = rdf.createGraph();
```

Objects created with  [JenaRDF](apidocs/org/apache/commons/rdf/jena/JenaRDF.html) implement interfaces like [JenaQuad](apidocs/org/apache/commons/rdf/jena/JenaQuad.html) and [JenaLiteral](apidocs/org/apache/commons/rdf/jena/JenaLiteral.html) which give access to the underlying Jena objects through methods like [asJenaNode()](apidocs/org/apache/commons/rdf/jena/JenaRDFTerm.html#asJenaNode--) and [asJenaGraph()](apidocs/org/apache/commons/rdf/jena/JenaGraph.html#asJenaGraph--).

`JenaRDF` includes additional methods for converting from/to Apache Jena and Commons RDF, like [asRDFTerm(Node)](apidocs/org/apache/commons/rdf/jena/JenaRDF.html#asRDFTerm-org.apache.jena.graph.Node-) and [asJenaNode(RDFTerm)](apidocs/org/apache/commons/rdf/jena/JenaRDF.html#asJenaNode-org.apache.commons.rdf.api.RDFTerm-).

#### Generalized RDF

Apache Jena can support [generalized RDF](https://www.w3.org/TR/rdf11-concepts/#section-generalized-rdf), e.g.:

> A generalized RDF triple is a triple having a subject, a predicate, and object, where each can be an IRI, a blank node or a literal.

Within Commons RDF it is possible to create [generalized triples](apidocs/org/apache/commons/rdf/jena/JenaRDF.html#createGeneralizedTriple-org.apache.commons.rdf.api.RDFTerm-org.apache.commons.rdf.api.RDFTerm-org.apache.commons.rdf.api.RDFTerm-) and [quads](apidocs/org/apache/commons/rdf/jena/JenaRDF.html#createGeneralizedQuad-org.apache.commons.rdf.api.RDFTerm-org.apache.commons.rdf.api.RDFTerm-org.apache.commons.rdf.api.RDFTerm-org.apache.commons.rdf.api.RDFTerm-) using `JenaRDF` - however note that the returned [JenaGeneralizedTripleLike](apidocs/org/apache/commons/rdf/jena/JenaGeneralizedTripleLike.html) and
[JenaGeneralizedQuadLike](apidocs/org/apache/commons/rdf/jena/JenaGeneralizedQuadLike.html)
 do not have the [equality semantics of Triple](apidocs/org/apache/commons/rdf/api/Triple.html#equals-java.lang.Object-) or [Quad](apidocs/org/apache/commons/rdf/api/Quad.html#equals-java.lang.Object-) and thus can't be used with the regular [Graph](apidocs/org/apache/commons/rdf/api/Graph.html) or [Dataset](apidocs/org/apache/commons/rdf/api/Dataset.html) methods.

The generalized triples/quads can be accessed as [org.apache.jena.graph.Triple](https://jena.apache.org/documentation/javadoc/jena/org/apache/jena/graph/Triple.html) and [org.apache.jena.sparql.core.Quad](https://jena.apache.org/documentation/javadoc/arq/org/apache/jena/sparql/core/Quad.html) - but can't currently be used with an equivalent _generalized graph_ or _generalized dataset_ within Commons RDF (see [COMMONSRDF-42](https://issues.apache.org/jira/browse/COMMONSRDF-42)).

### Eclipse RDF4J

[org.apache.commons.rdf.rdf4j](apidocs/org/apache/commons/rdf/rdf4j/package-summary.html) is an implementation of the Commons RDF API backed by Eclispe [RDF4J 2.0](http://rdf4j.org/) (formerly Sesame), including converters from/to RDF4J and Commons RDF.

**Usage:**

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-rdf-rdf4j</artifactId>
    <version>0.5.0</version>
</dependency>
```

```java
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.rdf4j.RDF4J;

RDF rdf = new RDF4J();
Graph graph = rdf.createGraph();
```

Objects created with  [RDF4J](apidocs/org/apache/commons/rdf/rdf4j/RDF4J.html) implement interfaces like [RDF4JTerm](apidocs/org/apache/commons/rdf/rdf4j/RDF4JTerm.html) and [RDF4JGraph](apidocs/org/apache/commons/rdf/rdf4j/RDF4JGraph.html) which give access to the underlying Jena objects through methods like [asValue()](apidocs/org/apache/commons/rdf/rdf4j/RDF4JTerm.html#asValue--) and [asRepository()](apidocs/org/apache/commons/rdf/rdf4j/RDF4JGraphLike.html#asRepository--).

`RDF4J` includes additional methods for converting from/to RDF4J and Commons RDF, like [asTriple(Statement)](apidocs/org/apache/commons/rdf/rdf4j/RDF4J.html#asTriple-org.eclipse.rdf4j.model.Statement-) and
[asRDFTerm(Value)](apidocs/org/apache/commons/rdf/rdf4j/RDF4J.html#asRDFTerm-org.eclipse.rdf4j.model.Value-).

#### Closing RDF4J resources

When using `RDF4J` with an RDF4J `Repository`, e.g. from [asRDFTermGraph(Repository)](apidocs/org/apache/commons/rdf/rdf4j/RDF4J.html#asGraph-org.eclipse.rdf4j.repository.Repository-org.apache.commons.rdf.rdf4j.RDF4J.Option...-), care must be taken to close underlying resources when using the methods [stream()](apidocs/org/apache/commons/rdf/rdf4j/RDF4JGraph.html#stream--) and [iterate()](apidocs/org/apache/commons/rdf/rdf4j/RDF4JGraph.html#iterate--) for both `Graph`s and `Dataset`s.

This can generally achieved using a [try-with-resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html) block, e.g.:


```java
int subjects;
try (Stream<RDF4JTriple> s : graph.stream(s,p,o)) {
  subjects = s.map(RDF4JTriple::getSubject).distinct().count()
}
```

This will ensure that the underlying RDF4J [RepositoryConnection](http://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/repository/RepositoryConnection.html) and [RepositoryResult](http://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/repository/RepositoryResult.html)
are closed after use.

Methods that return directly, like
[Graph.add()](apidocs/org/apache/commons/rdf/api/Graph.html#add-org.apache.commons.rdf.api.Triple-)
and [Dataset.size()](apidocs/org/apache/commons/rdf/api/Dataset.html#size--)
will use and close separate transactions per method calls and therefore do not need any special handling; however this will come with a performance hit when doing multiple graph/dataset modifications. (See [COMMONSRDF-45](https://issues.apache.org/jira/browse/COMMONSRDF-45))

Java's [java.util.Iteratable](http://docs.oracle.com/javase/8/docs/api/java/lang/Iterable.html) and [java.util.Iterator](http://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html) does not extend [`AutoClosable`](http://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html), and as there are many ways that a for-each loop may not run to exhaustion, Commons RDF introduces [ClosableIterable](apidocs/org/apache/commons/rdf/rdf4j/ClosableIterable.html), which can be used with RDF4J as:

```java
RDF4JGraph graph; // ...
try (ClosableIterable<Triple> s : graph.iterate()) {
 for (Triple t : triples) {
     return t; // OK to terminate for-loop early
 }
}
```

### JSONLD-Java

[org.apache.commons.rdf.jsonld](apidocs/org/apache/commons/rdf/jsonld/package-summary.html) is an implementation of the Commons RDF API backed by [JSON-LD-Java](https://github.com/jsonld-java/jsonld-java).

This is primarily intended to support [JSON-LD](http://json-ld.org/) parsing and writing.

**Usage:**

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-rdf-jsonld</artifactId>
    <version>0.5.0</version>
</dependency>
```

```java
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.jsonld.JsonLdFactory;

RDF rdf = new JsonLdRDF();
Graph graph = rdfTermFactory.createGraph();
```

## External implementations


### OWL API

[OWL API](http://owlapi.sourceforge.net/) 5 extends Commons RDF
directly for its family of
[RDFNode](https://github.com/owlcs/owlapi/blob/version5/api/src/main/java/org/semanticweb/owlapi/io/RDFNode.java#L25)
implementations. It is a partial compatibility implementation without its own `RDFTermFactory`, `Graph` or `Dataset`.



## Related implementations

### Apache Clerezza

[Apache Clerezza](https://clerezza.apache.org/) is
aligning its [RDF core](https://github.com/apache/clerezza-rdf-core) module with Commons RDF.
