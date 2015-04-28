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
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;

RDFTermFactory rdfTermFactory = new SimpleRDFTermFactory();
```

## Planned implementations

The information in this section should not be considered updated or
authorative as it relies on external project planning.


### Apache Jena

[Apache Jena](http://jena.apache.org/) is considering a compatibility interface
that provides and consumes Commons RDF objects.

### RDF4j Sesame

[Sesame](http://rdf4j.org/) is evolving Commons RDF as a basis for its
next version.

### Apache Clerezza

[Apache Clerezza](https://clerezza.apache.org/) is 
aligning its [RDF core](https://github.com/apache/clerezza-rdf-core) module
with Commons RDF.


