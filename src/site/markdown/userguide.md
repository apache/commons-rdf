# User Guide

This page shows some examples of a client using the Commons RDF API.
It was last updated for version `0.1-incubating-SNAPSHOT` of the
Commons RDF [API](apidocs/).

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

If you are testing a `SNAPSHOT` version, then you will have to either build
Commons RDF from [source](https://github.com/apache/incubator-commonsrdf), or
add the [snapshot repository](https://github.com/apache/incubator-commonsrdf#snapshot-repository):

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

As Commons RDF requires Java 8 or later, you will also need:

```xml
<properties>
  <maven.compiler.source>1.8</maven.compiler.source>
  <maven.compiler.target>1.8</maven.compiler.target>
</properties>
```

In the examples below we will use the
[_simple_ implementation](apidocs/org/apache/commons/rdf/simple/package-summary.html), but
the examples should be equally applicable to other implementations. To add a dependency for the
_simple_ implementation, add to your `<dependencies>`:

```xml
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-rdf-simple</artifactId>
        <version>0.1.0-incubating-SNAPSHOT</version>
    </dependency>
```


## Creating RDFTerm instances

To create instances of Commons RDF interfaces like
[`Graph`](apidocs/org/apache/commons/rdf/api/Graph.html) and
[`IRI`](apidocs/org/apache/commons/rdf/api/IRI.html) you will need a
[RDFTermFactory](/apidocs/org/apache/commons/rdf/api/RDFTermFactory.html).

How to get an instance of this factory is implementation specific, for the
_simple_ implementation, you can construct the
[SimpleRDFTermFactory](apidocs/org/apache/commons/rdf/simple/SimpleRDFTermFactory.html):

```java
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
// ..
RDFTermFactory rdfTermFactory = new SimpleRDFTermFactory();
```

Using the factory you can construct
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



The [RDFTermFactory](apidocs/org/apache/commons/rdf/api/RDFTermFactory.html) also
contains more specific variants of some of the methods above, e.g. to create a
typed literal.  

Note that for any given implementation, `RDFTerm` instances need not be created
using a `RDFTermFactory`. More likely, implementation-specific methods might create these
objects as part of data parsing, storage lookup and queries.



## Graph

A [Graph](apidocs/org/apache/commons/rdf/api/Graph.html) is a collection of
[Triple](apidocs/org/apache/commons/rdf/api/Triple.html)s.

To create a graph from a `RDFTermFactory`, use [createGraph()](apidocs/org/apache/commons/rdf/api/RDFTermFactory.html#createGraph--):

```java
Graph graph = factory.createGraph();
```

Implementations will typically also have other ways of retrieving a `Graph`,
e.g. by parsing a Turtle file or connecting to a storage backend.

### Adding triples

_Note: Some `Graph` implementations are immutable, in which case the below
may throw an `UnsupportedOperationException`_.

Given the [previous example](#Creating_RDFTerm_instances), we can
[add](apidocs/org/apache/commons/rdf/api/Graph.html#add-org.apache.commons.rdf.api.Triple-)
the triple to the graph:

```java
graph.add(triple);
```
As an alternative to creating the `Triple` first, you can use the expanded
_subject/predicate/object_ form of
[Graph.add](apidocs/org/apache/commons/rdf/api/Graph.html#add-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-()):

```java
IRI bob = factory.createIRI("http://example.com/bob");
Literal bobName = factory.createLiteral("Bob");
graph.add(bob, iri, bobName);
```

It is not necessary to check if a triple already exist in the graph, as the
underlying implementation will ignore duplicate triples.


### Finding triples

You can check if the graph
[contains](apidocs/org/apache/commons/rdf/api/Graph.html#contains-org.apache.commons.rdf.api.Triple-)
a triple:

```java
System.out.println(graph.contains(triple));
```
> `true`

The expanded subject/predicate/object call for [Graph.contains()](apidocs/org/apache/commons/rdf/api/Graph.html#contains-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-)
can be used without needing to create a `Triple` first, and also
allow `null` as a wildcard parameters:

```java
System.out.println(graph.contains(null, iri, bobName));
```
> `true`

### Size

The [size](apidocs/org/apache/commons/rdf/api/Graph.html#size--) of a graph is
the count of unique triples:

```java
System.out.println(graph.size());
```
> `2`

_Note: In some implementations with large graphs, calculating the size can be an
expensive operation._

### Iterating over triples

The [iterate](apidocs/org/apache/commons/rdf/api/Graph.html#iterate--) method
can be used to sequentially iterate over all the triples of the graph:

```java
for (Triple t : graph.iterate()) {
  System.out.println(t.getObject());
}
```
> `"Alice"`
>
> `"Bob"`

The expanded
[iterate](apidocs/org/apache/commons/rdf/api/Graph.html#iterate-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-)
method takes a _subject/predicate/object_ filter which permits the `null` wildcard:

```java
for (Triple t : graph.iterate(null, null, bobName)) {
  System.out.println(t.getPredicate());
}
```
> `<http://example.com/name>`

### Stream of triples

For processing of larger graphs, and to access more detailed
filtering and processing, the
[getTriples](apidocs/org/apache/commons/rdf/api/Graph.html#getTriples--) method
return a Java 8
[Stream](http://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html).

```java
Stream<Object> subjects = graph.getTriples().map(t -> t.getSubject());
String s = subjects.map(Object::toString).collect(Collectors.joining(" "));
System.out.println(s);
```
> ``"Alice" "Bob"``

For details about what can be done with a stream, see the
[java.util.stream documentation](http://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html).

Note that by default the stream will be parallel, use
[.sequential()](http://docs.oracle.com/javase/8/docs/api/java/util/stream/BaseStream.html#sequential--)
if your stream operations need to interact with objects that are not thread-safe.

Streams allow advanced [filter predicates](http://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#filter-java.util.function.Predicate-), but you may find that simple _subject/predicate/object_ patterns
are handled more efficiently by the expanded
[getTriples](http://commonsrdf.incubator.apache.org/apidocs/org/apache/commons/rdf/api/Graph.html#getTriples-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-) method. These can of course be combined:

```java
Stream<? extends Triple> namedB = graph.getTriples(null, nameIri, null).
    filter(t -> t.getObject().ntriplesString().contains("B"));
System.out.println(namedB.map(t -> t.getSubject()).findAny().get());
```
> `<http://example.com/bob>`

### Removing triples

_Note: Some `Graph` implementations are immutable, in which case the below
may throw an `UnsupportedOperationException`_.

Triples can be [removed](apidocs/org/apache/commons/rdf/api/Graph.html#remove-org.apache.commons.rdf.api.Triple-) from a graph:

```java
graph.remove(triple);
System.out.println(graph.contains(triple));
```

The expanded subject/predicate/object form of
[remove()](apidocs/org/apache/commons/rdf/api/Graph.html#remove-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-)
can be used without needing to construct a `Triple` first. It also
allow `null` as a wildcard pattern:

```java
graph.remove(null, iri, null);
```

To remove all triples, use [clear](apidocs/org/apache/commons/rdf/api/Graph.html#clear--):

```java
graph.clear();
System.out.println(graph.contains(null, null, null));
```
> false


## IRI, Literal, BlankNode

The core RDF terms are arranged in this class hierarchy:

* [RDFTerm](apidocs/org/apache/commons/rdf/api/RDFTerm.html)
    * [BlankNodeOrIRI](apidocs/org/apache/commons/rdf/api/BlankNodeOrIRI.html)
          * [BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html)
          * [IRI](apidocs/org/apache/commons/rdf/api/IRI.html)
    * [Literal](apidocs/org/apache/commons/rdf/api/Literal.html)

### N-Triples string

All of the [RDFTerm](apidocs/org/apache/commons/rdf/api/RDFTerm.html) types
support the `ntriplesString()` method:

```java
System.out.println(aliceBlankNode.ntriplesString());
System.out.println(nameIri.ntriplesString());
System.out.println(aliceLiteral.ntriplesString());
```

> `_:ef136d20-f1ee-3830-a54b-cd5e489d50fb`
>
> ``<http://example.com/name>``
>
> ``"Alice"``

This returns the [N-Triples](http://www.w3.org/TR/n-triples) canonical form
of the term, which can be useful both for debugging and simple serialization.

_Note: The `.toString()` of the `simple` implementation used in
these examples use `ntriplesString()` internally, but Commons RDF places no such
formal requirement on the `.toString()` method. Clients that rely on
a canonical N-Triples-compatible string should instead use
`ntriplesString()`._

As an example of using `ntriplesString()`, here is how one could write a simple
N-Triples compatible serialization of a
[Graph](apidocs/org/apache/commons/rdf/api/Graph.html):

```java
public class NTriplesSerializer {
    public static String tripleAsString(Triple t) {
        return t.getSubject().ntriplesString() + " " +
               t.getPredicate().ntriplesString() + " " +
               t.getObject().ntriplesString() + " .";
    }
    public static void writeGraph(Graph graph, Path graphFile) throws Exception {
        Stream<CharSequence> stream = graph.getTriples().map(NTriplesSerializer::tripleAsString);
        Files.write(graphFile, stream::iterator, Charset.forName("UTF-8"));
    }
}
```

Example output:

```turtle
_:ef136d20-f1ee-3830-a54b-cd5e489d50fb <http://example.com/name> "Alice" .
<http://example.com/bob> <http://example.com/name> "Bob" .
```

### IRI

An [IRI](apidocs/org/apache/commons/rdf/api/IRI.html)
is a representation of an
[Internationalized Resource Identifier](http://www.w3.org/TR/rdf11-concepts/#dfn-iri),
e.g. `http://example.com/alice` or `http://ns.example.org/vocab#term`.

> IRIs in the RDF abstract syntax MUST be absolute, and MAY contain a fragment identifier.

In RDF, an IRI identifies a resource that can be used as a _subject_,
_predicate_ or _object_ of a `Triple`.

To create an `IRI` instance from a `RDFTermFactory`, use [createIRI](apidocs/org/apache/commons/rdf/api/RDFTermFactory.html#createIRI-java.lang.String-):

```java
IRI iri = factory.createIRI("http://example.com/alice");
```

You can retrieve the IRI string using [getIRIString](apidocs/org/apache/commons/rdf/api/IRI.html#getIRIString--):

```java
System.out.println(iri.getIRIString());
```

> `http://example.com/alice`

_Note: The IRI string might contain non-ASCII characters which must be
%-encoded for applications that expect an URI. It is currently
out of scope for Commons RDF to perform such a conversion,
however implementations might provide separate methods for that purpose._

Two IRI instances can be compared using the
[equals](apidocs/org/apache/commons/rdf/api/IRI.html#equals-java.lang.Object-)
method, which uses [simple string comparison](http://tools.ietf.org/html/rfc3987#section-5.3.1):

```java
IRI iri2 = factory.createIRI("http://example.com/alice");
System.out.println(iri.equals(iri2));
```

> `true`

```java
IRI iri3 = factory.createIRI("http://example.com/alice/./");
System.out.println(iri.equals(iri3));
```

> `false`

Note that IRIs are never equal to objects which are not themselves
instances of [IRI](apidocs/org/apache/commons/rdf/api/IRI.html):


```java
System.out.println(iri.equals("http://example.com/alice"));
System.out.println(iri.equals(factory.createLiteral("http://example.com/alice")));
```
> `false`
>
> `false`


### Blank node

A [blank node](http://www.w3.org/TR/rdf11-concepts/#section-blank-nodes) is a
resource which, unlike an IRI, is not directly identified. Blank nodes can be
used as _subject_ or _object_ of a `Triple`.

To create a new
[BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html) instance from a
`RDFTermFactory`, use
[createBlankNode](apidocs/org/apache/commons/rdf/api/RDFTermFactory.html#createBlankNode--):

```java
BlankNode bnode = factory.createBlankNode();
```

Every call to `createBlankNode()` returns a brand new blank node
which can be used in multiple triples in multiple graphs. Thus
every such blank node can only be
[equal](apidocs/org/apache/commons/rdf/api/BlankNode.html#equals-java.lang.Object-)
to itself:

```java
System.out.println(bnode.equals(bnode));
System.out.println(bnode.equals(factory.createBlankNode()))
```

> `true`
>
> `false`

Sometimes it can be beneficial to create a blank node based on a
localized _name_, without needing to keep object references
to earlier `BlankNode` instances. For that purpose, `RDFTermFactory`
may support the
[expanded createBlankNode](apidocs/org/apache/commons/rdf/api/RDFTermFactory.html#createBlankNode-java.lang.String-)
method:

```java
BlankNode b1 = factory.createBlankNode("b1");
```

Note that there is no requirement for the
[ntriplesString()](apidocs/org/apache/commons/rdf/api/RDFTerm.html#ntriplesString--)
of the BlankNode to reflect the provided `name`:

```java
System.out.println(b1.ntriplesString());
```

> `_:6c0f628f-02cb-3634-99db-0e1e99d2e66d`



Any later `createBlankNode("b1")` **on the same factory instance**
returns a `BlankNode` which are
[equal](apidocs/org/apache/commons/rdf/api/BlankNode.html#equals-java.lang.Object-)
to the previous b1:

```java
System.out.println(b1.equals(factory.createBlankNode("b1")))
System.out.println(b1.equals(new SimpleRDFTermFactory().createBlankNode("b1")));
```

> `true`
>
> `false`

#### Blank node reference

_Warning: This method is still under discussion and these details,
including the method name, are subject to change. See
[COMMONSRDF-6](https://issues.apache.org/jira/browse/COMMONSRDF-6)._

While blank nodes are distinct from IRIs, and don't have inherent
universal identifiers, it can nevertheless be useful
for debugging and testing to have a unique reference string for
a particular blank node.
For that purpose, BlankNode exposes the
[internalIdentifier](apidocs/org/apache/commons/rdf/api/BlankNode.html#internalIdentifier--)
method:

```java
System.out.println(bnode.internalIdentifier())
```

> `735d5e63-96a4-488b-8613-7037b82c74a5`

While this reference string might for the _simple_
implementation also be seen  within the `BlankNode.ntriplesString()`
result, there is no such guarantee from the Commons RDF API.
Clients who need a globally unique reference
for a blank node should therefore use the `internalIdentifier()` method.

_Note: While it is recommended for this string to be (or contain) a
[UUID string](http://docs.oracle.com/javase/8/docs/api/java/util/UUID.html),
implementations are free to use any scheme to ensure their
blank node references are globally unique. Therefore no assumptions should
be made about this string except that it is unique per blank node._


### Literal

**TODO:** [Literal](apidocs/org/apache/commons/rdf/api/Literal.html)

### Types

**TODO:** [Types](apidocs/org/apache/commons/rdf/simple/Types.html)

### Triple

**TODO:** [Triple](apidocs/org/apache/commons/rdf/api/Triple.html)
