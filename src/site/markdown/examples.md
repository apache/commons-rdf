# Examples of using the Commons RDF API

This page shows some examples of a client using the Commons RDF API.

## Using Commons RDF from Maven

To use Commons RDF API from an 
[Apache Maven](http://maven.apache.org/) projects, 
add the following dependency to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-rdf-api</artifactId>
        <version>0.1-incubating-SNAPSHOT</version>
    </dependency>
</dependencies>
```

If you are using a `SNAPSHOT` version, then you will have to either build 
Commons RDF from [source](https://github.com/apache/incubator-commonsrdf), or 
add the [snapshot repository](https://github.com/apache/incubator-commonsrdf#snapshot-repository)

In the examples below we will use the 
[`simple` implementation](apidocs/org/apache/commons/rdf/simple/package-summary.html), but 
the examples should be equally applicable to other implementations. To add a dependency for the
`simple` implementation, add to your `pom.xml` section `<dependencies>`: 

```xml
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-rdf-simple</artifactId>
        <version>0.1.0-incubating-SNAPSHOT</version>
    </dependency>
```


## RDFTermFactory

To create instances of Commons RDF interfaces like 
[`Graph`](apidocs/org/apache/commons/rdf/api/Graph.html) and 
[`IRI`](apidocs/org/apache/commons/rdf/api/IRI.html) you will need a
[RDFTermFactory](/apidocs/org/apache/commons/rdf/api/RDFTermFactory.html).

How to get an instance of this factory is implementation specific, for the
`simple` implementation, you can construct the 
[SimpleRDFTermFactory](apidocs/org/apache/commons/rdf/simple/SimpleRDFTermFactory.html):

```java
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
// ..
RDFTermFactory rdfTermFactory = new SimpleRDFTermFactory();
```

Using the factory you can create 
any [RDFTerm](apidocs/org/apache/commons/rdf/api/RDFTerm.html), e.g. to create a
[BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html),
[IRI](apidocs/org/apache/commons/rdf/api/IRI.html) and
[Literal](apidocs/org/apache/commons/rdf/api/Literal.html):

```java
BlankNode blankNode = factory.createBlankNode();
IRI iri = factory.createIRI("http://example.com/name");
Literal literal = factory.createLiteral("Alice");
```

You can also create a stand-alone [Triple](apidocs/org/apache/commons/rdf/api/Triple.html):

```java
Triple triple = factory.createTriple(blankNode, iri, literal);
```

To keep your triples, you might want a [Graph](apidocs/org/apache/commons/rdf/api/Graph.html):

```java
Graph graph = factory.createGraph();
```

The [RDFTermFactory](apidocs/org/apache/commons/rdf/api/RDFTermFactory.html) also 
contains more specific variants of some of the methods above, e.g. to create a
typed literal.  

Note that for any given implementation, `RDFTerm` instances need not be created
using a `RDFTermFactory`. More likely, implementation-specific methods might create these 
objects as part of data parsing, storage lookup and queries.



## Graph

A [Graph](apidocs/org/apache/commons/rdf/api/Graph.html) is a collection of
[Triple](apidocs/org/apache/commons/rdf/api/Triple.html)s.

Given the [previous example](#RDFTermFactory), we can 
[add](apidocs/org/apache/commons/rdf/api/Graph.html#add-org.apache.commons.rdf.api.Triple-)
the `triple` to the `graph`:

```java
graph.add(triple);
```

And check that the graph 
[contains](apidocs/org/apache/commons/rdf/api/Graph.html#contains-org.apache.commons.rdf.api.Triple-) 
the triple:

```java
System.out.println(graph.contains(triple));
```
> true

As an alternative you can also use the expanded form of these methods:

```java
IRI bob = factory.createIRI("http://example.com/bob");
Literal bobName = factory.createLiteral("Bob");
graph.add(bob, iri, bobName);
```

and

```java
System.out.println(graph.contains(null, iri, bobName));
```
> true

Notice that the 
expanded [Graph.contains()](apidocs/org/apache/commons/rdf/api/Graph.html#contains-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-)
allow `null` as a wildcard parameters.




**TODO:** Remaining Graph operations, incl. `getTriples()` streams

**TODO:** [Types](apidocs/org/apache/commons/rdf/simple/Types.html)

**TODO:** Methods on `Literal`, `IRI`, `BlankNode` etc.

**TODO:** Serialize as n-triples example