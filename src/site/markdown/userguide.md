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

# User Guide

This page shows some examples of a client using the Commons RDF API.
It was last updated for version `0.5.0` of the
Commons RDF [API](apidocs/).

* [Introduction](#Introduction)
    * [RDF concepts](#RDF_concepts)
* [Using Commons RDF from Maven](#Using_Commons_RDF_from_Maven)
* [Creating Commons RDF instances](#Creating_Commons_RDF_instances)
  * [Finding an RDF implementation](#Finding_an_RDF_implementation)
  * [Using an RDF implementation](#Using_an_RDF_implementation)
* [RDF terms](#RDF_terms)
    * [N-Triples string](#N-Triples_string)
    * [IRI](#IRI)
    * [Blank node](#Blank_node)
        * [Blank node reference](#Blank_node_reference)
    * [Literal](#Literal)
        * [Datatype](#Datatype)
            * [Types](#Types)
        * [Language](#Language)
* [Triple](#Triple)
* [Quad](#Quad)
* [Graph](#Graph)
    * [Adding triples](#Adding_triples)
    * [Finding triples](#Finding_triples)
    * [Size](#Size)
    * [Iterating over triples](#Iterating_over_triples)
    * [Stream of triples](#Stream_of_triples)
    * [Removing triples](#Removing_triples)
* [Dataset](#Dataset)
    * [Dataset operations](#Dataset_operations)
* [Mutability and thread safety](#Mutability_and_thread_safety)
* [Implementations](#Implementations)
    * [Cross-compatibility](#Cross-compatibility)
* [Complete example](#Complete_example)

## Introduction

[Commons RDF](index.html) is an API that intends to directly describe
[RDF 1.1 concepts](http://www.w3.org/TR/rdf11-concepts/) as a set
of corresponding interfaces and methods.

### RDF concepts

RDF is a [graph-based data model](http://www.w3.org/TR/rdf11-concepts/#data-model), where
a _graph_ contains a series of _triples_, each containing the node-arc-node link
_subject_ -> _predicate_ -> _object_.  Nodes in the graph are represented either as _IRIs_, _literals_ and _blank nodes_.
:
This user guide does not intend to give a detailed description of RDF as a data
model. To fully understand this user guide, you should have a brief
understanding of the core RDF concepts mentioned above.

For more information on RDF, see the
[RDF primer](http://www.w3.org/TR/rdf11-primer/) and the [RDF
concepts](http://www.w3.org/TR/rdf11-concepts/#data-model) specification from
W3C.



## Using Commons RDF from Maven

To use Commons RDF API from an
[Apache Maven](http://maven.apache.org/) project,
add the following dependency to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-rdf-api</artifactId>
        <version>0.5.0</version>
    </dependency>
</dependencies>
```

_The `<version>` above might not be up to date,
see the [download page](download.html) for the latest version._

If you are testing a `SNAPSHOT` version, then you will have to either build
Commons RDF from [source](https://github.com/apache/commons-rdf), or
add this [snapshot repository](https://github.com/apache/commons-rdf#snapshot-repository):

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
[_simple_ implementation](apidocs/org/apache/commons/rdf/simple/package-summary.html),
but the examples should be equally applicable to other
[implementations](implementations.html). To add a dependency for the
_simple_ implementation, add to your `<dependencies>`:

```xml
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-rdf-simple</artifactId>
        <version>0.5.0</version>
    </dependency>
```

_The `<version>` above might not be up to date,
see the [download page](download.html) for the latest version._


## Creating Commons RDF instances

To create instances of Commons RDF interfaces like
[`Graph`](apidocs/org/apache/commons/rdf/api/Graph.html) and
[`IRI`](apidocs/org/apache/commons/rdf/api/IRI.html) you will need a
[RDF](apidocs/org/apache/commons/rdf/api/RDF.html) implementation.

### Finding an RDF implementation

The [implementations](implementations.html) of `RDF` can usually be
created using a normal Java constructor, for instance the
_simple_ implementation from
[SimpleRDF](apidocs/org/apache/commons/rdf/simple/SimpleRDF.html):

```java
import org.apache.commons.rdf.api.*;
import org.apache.commons.rdf.simple.SimpleRDF;
// ...
RDF rdf = new SimpleRDF();
```

If you don't want to depend on instantiating a concrete `RDF`
implementation, you can alternatively use the
[ServiceLoader](http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html)
to lookup any `RDF` implementations found on the classpath:

```java
import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.commons.rdf.api.*;
// ...
ServiceLoader<RDF> loader = ServiceLoader.load(RDF.class);
Iterator<RDF> iterator = loader.iterator();
RDF rdf = iterator.next();
```

Note that the `ServiceLoader` approach above might not work well within
split classloader systems like OSGi.

When using the factory method
[createBlankNode(String)](apidocs/org/apache/commons/rdf/api/RDF.html#createBlankNode-java.lang.String-)
from different sources,
care should be taken to create correspondingly different `RDF` instances.


### Using an RDF implementation

Using the `RDF` implementation you can construct
any [RDFTerm](apidocs/org/apache/commons/rdf/api/RDFTerm.html), e.g. to create a
[BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html),
[IRI](apidocs/org/apache/commons/rdf/api/IRI.html) and
[Literal](apidocs/org/apache/commons/rdf/api/Literal.html):

```java
BlankNode aliceBlankNode = rdf.createBlankNode();
IRI nameIri = rdf.createIRI("http://example.com/name");
Literal aliceLiteral = rdf.createLiteral("Alice");
```

You can also create a stand-alone [Triple](apidocs/org/apache/commons/rdf/api/Triple.html):

```java
Triple triple = rdf.createTriple(aliceBlankNode, nameIri, aliceLiteral);
```

The [RDF](apidocs/org/apache/commons/rdf/api/RDF.html) interface also
contains more specific variants of some of the methods above, e.g. to create a
typed literal.

In addition, the [implementations](implementations.html) of `RDF` may add
specific converter methods and implementation-specific
subtypes for interoperability with their underlying RDF framework's API.


## RDF terms

[RDFTerm](apidocs/org/apache/commons/rdf/api/RDFTerm.html) is
the super-interface for instances that can be used as subject, predicate and
object of a [Triple](apidocs/org/apache/commons/rdf/api/Triple.html).

The RDF term interfaces are arranged in this type hierarchy:

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
of the term, which can be useful for debugging or simple serialization.

_Note: The `.toString()` of the `simple` implementation used in
some of these examples use `ntriplesString()` internally, but Commons RDF
places no such formal requirement on the `.toString()` method. Clients that
rely on a canonical N-Triples-compatible string should instead use
`ntriplesString()`._

<!-- Not relevant here

As an example of using `ntriplesString()`, here is how one could write a basic
N-Triples-compatible serialization of a
[Graph](apidocs/org/apache/commons/rdf/api/Graph.html):

```java
public class NTriplesSerializer {
    public static String tripleAsString(Triple t) {
        return t.getSubject().ntriplesString() + " " +
               t.getPredicate().ntriplesString() + " " +
               t.getObject().ntriplesString() + " .";
    }
    public static void writeGraph(Graph graph, Path graphFile) throws Exception {
        Stream<CharSequence> stream = graph.stream().map(NTriplesSerializer::tripleAsString);
        Files.write(graphFile, stream::iterator, StandardCharsets.UTF_8);
    }
}
```

Example output:

> `_:ef136d20-f1ee-3830-a54b-cd5e489d50fb <http://example.com/name> "Alice" .`
>
> `<http://example.com/bob> <http://example.com/name> "Bob" .`
-->

### IRI

An [IRI](apidocs/org/apache/commons/rdf/api/IRI.html)
is a representation of an
[Internationalized Resource Identifier](http://www.w3.org/TR/rdf11-concepts/#dfn-iri),
e.g. `http://example.com/alice` or `http://ns.example.org/vocab#term`.

> IRIs in the RDF abstract syntax MUST be absolute, and MAY contain a fragment identifier.

An IRI identifies a resource that can be used as a _subject_,
_predicate_ or _object_ of a [Triple](apidocs/org/apache/commons/rdf/api/Triple.html) or
[Quad](apidocs/org/apache/commons/rdf/api/Quad.html),
where it can also be used a _graph name_.

To create an `IRI` instance from an `RDF` implementation, use [createIRI](apidocs/org/apache/commons/rdf/api/RDF.html#createIRI-java.lang.String-):

```java
IRI iri = rdf.createIRI("http://example.com/alice");
```

You can retrieve the IRI string using [getIRIString](apidocs/org/apache/commons/rdf/api/IRI.html#getIRIString--):

```java
System.out.println(iri.getIRIString());
```

> `http://example.com/alice`

_Note: The **IRI** string might contain non-ASCII characters which must be
%-encoded for applications that expect an **URI**. It is currently
out of scope for Commons RDF to perform such a conversion,
however implementations might provide separate methods for that purpose._

Two IRI instances can be compared using the
[equals](apidocs/org/apache/commons/rdf/api/IRI.html#equals-java.lang.Object-)
method, which uses [simple string comparison](http://tools.ietf.org/html/rfc3987#section-5.3.1):

```java
IRI iri2 = rdf.createIRI("http://example.com/alice");
System.out.println(iri.equals(iri2));
```

> `true`

```java
IRI iri3 = rdf.createIRI("http://example.com/alice/./");
System.out.println(iri.equals(iri3));
```

> `false`

Note that IRIs are never equal to objects which are not themselves
instances of [IRI](apidocs/org/apache/commons/rdf/api/IRI.html):


```java
System.out.println(iri.equals("http://example.com/alice"));
System.out.println(iri.equals(rdf.createLiteral("http://example.com/alice")));
```
> `false`
>
> `false`


### Blank node

A [blank node](http://www.w3.org/TR/rdf11-concepts/#section-blank-nodes) is a
resource which, unlike an IRI, is not directly identified. Blank nodes can be
used as _subject_ or _object_ of a
[Triple](apidocs/org/apache/commons/rdf/api/Triple.html) or
[Quad](apidocs/org/apache/commons/rdf/api/Quad.html),
where it can also be used a _graph name_.

To create a new
[BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html) instance from a
`RDF` implementation, use
[createBlankNode](apidocs/org/apache/commons/rdf/api/RDF.html#createBlankNode--):

```java
BlankNode bnode = rdf.createBlankNode();
```

Every call to `createBlankNode()` returns a brand new blank node
which can be used in multiple triples in multiple graphs. Thus
every such blank node can only be
[equal](apidocs/org/apache/commons/rdf/api/BlankNode.html#equals-java.lang.Object-)
to itself:

```java
System.out.println(bnode.equals(bnode));
System.out.println(bnode.equals(rdf.createBlankNode()));
```

> `true`
>
> `false`

Sometimes it can be beneficial to create a blank node based on a
localized _name_, without needing to keep object references
to earlier `BlankNode` instances. For that purpose, the
`RDF` interface provides the
[expanded createBlankNode](apidocs/org/apache/commons/rdf/api/RDF.html#createBlankNode-java.lang.String-)
method:

```java
BlankNode b1 = rdf.createBlankNode("b1");
```

Note that there is no requirement for the
[ntriplesString()](apidocs/org/apache/commons/rdf/api/RDFTerm.html#ntriplesString--)
of the BlankNode to reflect the provided `name`:

```java
System.out.println(b1.ntriplesString());
```

> `_:6c0f628f-02cb-3634-99db-0e1e99d2e66d`



Any later `createBlankNode("b1")` **on the same `RDF` instance**
returns a `BlankNode` which are
[equal](apidocs/org/apache/commons/rdf/api/BlankNode.html#equals-java.lang.Object-)
to the previous b1:

```java
System.out.println(b1.equals(rdf.createBlankNode("b1")));
```
> `true`

That means that care should be taken to create a new `RDF` instance
if making "different" blank nodes (e.g. parsed from a different RDF file)
which accidfentally might have the same name:

```java
System.out.println(b1.equals(new SimpleRDF().createBlankNode("b1")));
```
> `false`


#### Blank node reference

While blank nodes are distinct from IRIs, and don't have inherent
universal identifiers, it can nevertheless be useful
for debugging and testing to have a unique reference string for
a particular blank node.
For that purpose, BlankNode exposes the
[uniqueReference](apidocs/org/apache/commons/rdf/api/BlankNode.html#uniqueReference--)
method:

```java
System.out.println(bnode.uniqueReference());
```

> `735d5e63-96a4-488b-8613-7037b82c74a5`

While this reference string might for the _simple_
implementation also be seen within the `BlankNode.ntriplesString()`
result, there is no such guarantee from the Commons RDF API.
Clients who need a globally unique reference
for a blank node should therefore use the `uniqueReference()` method.

_Note: While it is recommended for this string to be (or contain) a
[UUID string](http://docs.oracle.com/javase/8/docs/api/java/util/UUID.html),
implementations are free to use any scheme to ensure their
blank node references are globally unique. Therefore no assumptions should
be made about this string except that it is unique per blank node._


### Literal

A [literal](http://www.w3.org/TR/rdf11-concepts/#section-Graph-Literal) in RDF
is a value such as a string, number or a date. A `Literal` can only be used as
an _object_ of a [Triple](apidocs/org/apache/commons/rdf/api/Triple.html#getObject--)
or [Quad](apidocs/org/apache/commons/rdf/api/Quad.html#getObject--)

To create a [Literal](apidocs/org/apache/commons/rdf/api/Literal.html) instance
from an `RDF` implementation, use
[createLiteral](apidocs/org/apache/commons/rdf/api/RDF.html#createLiteral-java.lang.String-):

```java
Literal literal = rdf.createLiteral("Hello world!");
System.out.println(literal.ntriplesString());
```

> `"Hello world!"`

The _lexical value_ (what is inside the quotes) can be retrieved
using [getLexicalForm()](apidocs/org/apache/commons/rdf/api/Literal.html#getLexicalForm--):

```java
String lexical = literal.getLexicalForm();
System.out.println(lexical);
```

> `Hello world!`


#### Datatype

All literals in RDF 1.1 have a
[datatype](http://www.w3.org/TR/rdf11-concepts/#dfn-datatype-iri) `IRI`, which
can be retrieved using
[Literal.getDatatype()](apidocs/org/apache/commons/rdf/api/Literal.html#getDatatype--):

```java
IRI datatype = literal.getDatatype();
System.out.println(datatype.ntriplesString());
```

> `<http://www.w3.org/2001/XMLSchema#string>`

In RDF 1.1, a [simple
literal](http://www.w3.org/TR/rdf11-concepts/#dfn-simple-literal) (as created
above) always have the type
`http://www.w3.org/2001/XMLSchema#string` (or
[xsd:string](apidocs/org/apache/commons/rdf/simple/Types.html#XSD_STRING) for
short).

<div class="alert alert-warn" role="alert"><p><span class="glyphicon glyphicon-warn-sign" aria-hidden="true"></span>
<!-- Markdown not supported inside HTML -->
<strong>Note:</strong>
RDF 1.0 had the datatype
<code>http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral</code> to
indicate <em>plain literals</em> (untyped), which were distinct from
<code>http://www.w3.org/2001/XMLSchema#string</code> (typed). Commons
RDF assumes RDF 1.1, which merges the two concepts as the second type, however
particular implementations might have explicit options for RDF 1.0 support, in
which case you might find <code>Literal</code> instances with the deprecated
<a href="apidocs/org/apache/commons/rdf/simple/Types.html#RDF_PLAINLITERAL">plain
literal</a> data type.
</p></div>


To create a literal with any other
[datatype](http://www.w3.org/TR/rdf11-concepts/#dfn-datatype-iri) (e.g. `xsd:double`),
then create the datatype `IRI` and pass it to the expanded
[createLiteral](apidocs/org/apache/commons/rdf/api/RDF.html#createLiteral-java.lang.String-org.apache.commons.rdf.api.IRI-):

```java
IRI xsdDouble = rdf.createIRI("http://www.w3.org/2001/XMLSchema#double");
Literal literalDouble = rdf.createLiteral("13.37", xsdDouble);
System.out.println(literalDouble.ntriplesString());
```

> `"13.37"^^<http://www.w3.org/2001/XMLSchema#double>`




##### Types

The class [Types](apidocs/org/apache/commons/rdf/simple/Types.html), which is
part of the _simple_ implementation, provides `IRI` constants for the standard
XML Schema datatypes like `xsd:dateTime` and `xsd:float`. Using `Types`,
the above example can be simplified to:

```java
Literal literalDouble2 = rdf.createLiteral("13.37", Types.XSD_DOUBLE);
```

As the constants in `Types` are all instances of `IRI`, so they can
also be used for comparisons:

```java
System.out.println(Types.XSD_STRING.equals(literal.getDatatype()));
```

> `true`

#### Language

Literals may be created with an associated
[language tag](http://www.w3.org/TR/rdf11-concepts/#dfn-language-tagged-string)
using the expanded [createLiteral](apidocs/org/apache/commons/rdf/api/RDF.html#createLiteral-java.lang.String-java.lang.String-):

```java
Literal inSpanish = rdf.createLiteral("¡Hola, Mundo!", "es");
System.out.println(inSpanish.ntriplesString());
System.out.println(inSpanish.getLexicalForm());
```
> `"¡Hola, Mundo!"@es`
>
> `¡Hola, Mundo!`

A literal with a language tag always have the
implied type `http://www.w3.org/1999/02/22-rdf-syntax-ns#langString`:

```java
System.out.println(inSpanish.getDatatype().ntriplesString());
```

> `<http://www.w3.org/1999/02/22-rdf-syntax-ns#langString>`

The language tag can be retrieved using
[getLanguageTag()](apidocs/org/apache/commons/rdf/api/Literal.html#getLanguageTag--):

```java
Optional<String> tag = inSpanish.getLanguageTag();
if (tag.isPresent()) {
    System.out.println(tag.get());
}
```

> `es`

The language tag is behind an
[Optional](http://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) as
it won't be present for any other datatypes than
`http://www.w3.org/1999/02/22-rdf-syntax-ns#langString`:

```java
System.out.println(literal.getLanguageTag().isPresent());
System.out.println(literalDouble.getLanguageTag().isPresent());
```

> `false`
>
> `false`


## Triple

A [triple](http://www.w3.org/TR/rdf11-concepts/#section-triples) in
RDF 1.1 consists of:

* The [subject](apidocs/org/apache/commons/rdf/api/Triple.html#getSubject--), which is an [IRI](apidocs/org/apache/commons/rdf/api/IRI.html) or a [BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html)
* The [predicate](apidocs/org/apache/commons/rdf/api/Triple.html#getPredicate--), which is an [IRI](apidocs/org/apache/commons/rdf/api/IRI.html)
* The [object](apidocs/org/apache/commons/rdf/api/Triple.html#getObject--), which is an [IRI](apidocs/org/apache/commons/rdf/api/IRI.html), a [BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html) or a [Literal](apidocs/org/apache/commons/rdf/api/Literal.html)


To construct a [Triple](apidocs/org/apache/commons/rdf/api/Triple.html) from an
`RDF` implementation, use
[createTriple](apidocs/org/apache/commons/rdf/api/RDF.html#createTriple-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-):

```java
BlankNodeOrIRI subject = rdf.createBlankNode();
IRI predicate = rdf.createIRI("http://example.com/says");
RDFTerm object = rdf.createLiteral("Hello");
Triple triple = rdf.createTriple(subject, predicate, object);
```

The subject of the triple can be retrieved
using [getSubject](apidocs/org/apache/commons/rdf/api/Triple.html#getSubject--):

```java
BlankNodeOrIRI subj = triple.getSubject();
System.out.println(subj.ntriplesString());
```

> `_:7b914fbe-aa2a-4551-b71c-8ac0e2b52b26`

Likewise the predicate using
[getPredicate](apidocs/org/apache/commons/rdf/api/Triple.html#getPredicate--):

```java
IRI pred = triple.getPredicate();
System.out.println(pred.getIRIString());
```
> `http://example.com/says`

Finally, the object of the triple is returned with
[getObject](apidocs/org/apache/commons/rdf/api/Triple.html#getObject--):

```java
RDFTerm obj = triple.getObject();
System.out.println(obj.ntriplesString());
```

> `"Hello"`

For the subject and object you might find it useful to do
Java type checking and casting from the types
[BlankNodeOrIRI](apidocs/org/apache/commons/rdf/api/BlankNodeOrIRI.html)
and [RDFTerm](apidocs/org/apache/commons/rdf/api/RDFTerm.html):

```java
if (subj instanceof IRI) {
    String s = ((IRI) subj).getIRIString();
    System.out.println(s);
}
// ..
if (obj instanceof Literal) {
    IRI type = ((Literal) obj).getDatatype();
    System.out.println(type);
}
```

In Commons RDF, `BlankNodeOrIRI` instances are always one of `BlankNode` or
`IRI`, and `RDFTerm` instances one of `BlankNode`, `IRI` or `Literal`.

A `Triple` is considered
[equal](apidocs/org/apache/commons/rdf/api/Triple.html#equals-java.lang.Object-)
to another `Triple` if each of their subject, predicate and object are
equal:

```java
System.out.println(triple.equals(rdf.createTriple(subj, pred, obj)));
```

> `true`

This equality is true even across implementations, as Commons RDF has
specified _equality semantics_ for
[Triples](apidocs/org/apache/commons/rdf/api/Triple.html#equals-java.lang.Object-),
[Quads](apidocs/org/apache/commons/rdf/api/Quad.html#equals-java.lang.Object-),
[IRIs](apidocs/org/apache/commons/rdf/api/IRI.html#equals-java.lang.Object-),
[Literals](apidocs/org/apache/commons/rdf/api/Literal.html#equals-java.lang.Object-)
and even [BlankNodes](apidocs/org/apache/commons/rdf/api/BlankNode.html#equals-java.lang.Object-).

## Quad
A _quad_ is a triple with an associated _graph name_, and can be a statement in a
[dataset](http://www.w3.org/TR/rdf11-concepts/#section-dataset).

Commons RDF represents such statements using the class [Quad](apidocs/org/apache/commons/rdf/api/Quad.html), which consists of:

* The [subject](apidocs/org/apache/commons/rdf/api/Quad.html#getSubject--), which is an [IRI](apidocs/org/apache/commons/rdf/api/IRI.html) or a [BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html)
* The [predicate](apidocs/org/apache/commons/rdf/api/Quad.html#getPredicate--), which is an [IRI](apidocs/org/apache/commons/rdf/api/IRI.html)
* The [object](apidocs/org/apache/commons/rdf/api/Quad.html#getObject--), which is an [IRI](apidocs/org/apache/commons/rdf/api/IRI.html), a [BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html) or a [Literal](apidocs/org/apache/commons/rdf/api/Literal.html)
* The [graph name](apidocs/org/apache/commons/rdf/api/Quad.html#getGraphName--), which is an [IRI](apidocs/org/apache/commons/rdf/api/IRI.html) or a [BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html); wrapped as an `java.util.Optional`


To create a `Quad`, use [createQuad](apidocs/org/apache/commons/rdf/api/RDF.html#createQuad-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-):

```
BlankNodeOrIRI graph = rdf.createIRI("http://example.com/graph");
BlankNodeOrIRI subject = rdf.createBlankNode();
IRI predicate = rdf.createIRI("http://example.com/says");
RDFTerm object = rdf.createLiteral("Hello");
Quad quad = rdf.createQuad(graph, subject, predicate, object);
```

The subject, predicate and object are accessible just like in a `Triple`:

```
IRI pred = quad.getPredicate();
System.out.println(pred.ntriplesString());
```

> `<http://example.com/says>`

### Graph name

The quad's _graph name_ is accessible using
[getGraphName()](apidocs/org/apache/commons/rdf/api/Quad.html#getGraphName--):

```
Optional<BlankNodeOrIRI> g = quad.getGraphName();
```

The graph name is represented as an [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html),
where `Optional.empty()` indicates that the quad is in the [default graph](https://www.w3.org/TR/rdf11-concepts/#dfn-default-graph), while
if the [Optional.isPresent()](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#isPresent--) then the
graph name `BlankNodeOrIRI` is accessible with `g.get()`:

```
if (g.isPresent()) {
  BlankNodeOrIRI graphName = g.get();
  System.out.println(graphName.ntriplesString());
}
```

> `<http://example.com/graph>`


To create a quad in the _default graph_, supply `null` as the graph name
to the factory method:

```
Quad otherQuad = rdf.createQuad(null, subject, predicate, object);
System.out.println(otherQuad.getGraphName().isPresent());
```

> `false`

Note that a `Quad` will never return `null` on any of its getters, which is why
the graph name is wrapped as an `Optional`. This also allows the use of
Java 8 functional programming patterns like:

```
String str = quad.map(BlankNodeOrIRI::ntriplesString).orElse("");
```

As the `createQuad` method does not expect an `Optional`, you might
use this `orElse` pattern to represent the default graph as `null`:

```
BlankNodeOrIRI g = quad.getGraphName().orElse(null);
if (g == null) {
  System.out.println("In default graph");
}
rdf.createQuad(g,s,p,o);
```


Care should be taken with regards when accessing
graph named with `BlankNode`s,
as the graph name will be compared using
[BlankNode's equality semantics](apidocs/org/apache/commons/rdf/api/BlankNode.html#equals-java.lang.Object-).


### Quad equality

A `Quad` is considered
[equal](apidocs/org/apache/commons/rdf/api/Quad.html#equals-java.lang.Object-)
to another `Quad` if each of the graph name, subject, predicate and
object are equal:

```
System.out.println(quad.equals(otherQuad));
```

> `false`


### Converting quads to triples

All quads can be viewed as triples - in a way "stripping" the graph name:

```
Triple quadAsTriple = quad.asTriple();
```

This can be utilized to compare quads at triple-level (considering just s/p/o):

```
System.out.println(quadAsTriple.equals(otherQuad.asTriple());
```

> `true`

To create a triple from a quad, you will need to use
[RDF.createQuad](apidocs/org/apache/commons/rdf/api/RDF.html#createQuad-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-)
providing the desired graph name:

```
Triple t; // ..
BlankNodeOrIRI g; // ..
Quad q = rdf.createQuad(g, t.getSubject(), t.getPredicate(), t.getObject());
```



### TripleLike

Note that the class [Quad](apidocs/org/apache/commons/rdf/api/Quad.html)
does **not** extend the class
[Triple](apidocs/org/apache/commons/rdf/api/Triple.html),
as they have different equality semantics.

Both `Triple` and `Quad` do however share a common "duck-typing" interface
[TripleLike](apidocs/org/apache/commons/rdf/api/TripleLike.html):


```
TripleLike a = quad;
TripleLike b = quad.asTriple();
```

Unlike `Triple` and `Quad`, `TripleLike` does not mandate any specific
`.equals()`, it just provides common access to
[getSubject()](apidocs/org/apache/commons/rdf/api/TripleLike.html#getSubject--)
[getPredicate()](apidocs/org/apache/commons/rdf/api/TripleLike.html#getPredicate--) and
[getObject()](apidocs/org/apache/commons/rdf/api/TripleLike.html#getObject--).


```
RDFTerm s = a.getSubject();
RDFTerm p = a.getPredicate();
RDFTerm o = a.getObject();
```

TripleLike can also be used for
[generalized RDF](https://www.w3.org/TR/rdf11-concepts/#section-generalized-rdf)
therefore all of its parts are returned as [RDFTerm](apidocs/org/apache/commons/rdf/api/RDFTerm.html).

For generalized quads the
[QuadLike](apidocs/org/apache/commons/rdf/api/QuadLike.html) interface extends `TripleLike` to
add
[getGraphName()](apidocs/org/apache/commons/rdf/api/QuadLike.html#getGraphName--)
as an `Optional<T extends RDFTerm>`.


## Graph

A [graph](http://www.w3.org/TR/rdf11-concepts/#section-rdf-graph)
is a collection of triples.

To create a [Graph](apidocs/org/apache/commons/rdf/api/Graph.html) instance
from a `RDF` implementation, use
[createGraph()](apidocs/org/apache/commons/rdf/api/RDF.html#createGraph--):

```java
Graph graph = rdf.createGraph();
```

Implementations will typically also have other ways of retrieving a `Graph`,
e.g. by parsing a Turtle file or connecting to a storage backend.

### Adding triples

Any [Triple](apidocs/org/apache/commons/rdf/api/Triple.html) can be added to the
graph using the
[add](apidocs/org/apache/commons/rdf/api/Graph.html#add-org.apache.commons.rdf.api.Triple-)
method:

```java
graph.add(triple);
```
As an alternative to creating the `Triple` first, you can use the expanded
_subject/predicate/object_ form of
[Graph.add](apidocs/org/apache/commons/rdf/api/Graph.html#add-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-):

```java
IRI bob = rdf.createIRI("http://example.com/bob");
IRI nameIri = rdf.createIRI("http://example.com/name");
Literal bobName = rdf.createLiteral("Bob");
graph.add(bob, nameIRI, bobName);
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

The expanded _subject/predicate/object_ call for [Graph.contains()](apidocs/org/apache/commons/rdf/api/Graph.html#contains-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-)
can be used without needing to create a `Triple` first, and also
allow `null` as a wildcard parameter:

```java
System.out.println(graph.contains(null, nameIri, bobName));
```
> `true`

### Size

The [size](apidocs/org/apache/commons/rdf/api/Graph.html#size--) of a graph is
the count of unique triples:

```java
System.out.println(graph.size());
```
> `2`


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
[stream](apidocs/org/apache/commons/rdf/api/Graph.html#stream--) method
return a Java 8
[Stream](http://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html).

Some of the implementations (e.g. [RDF4J](implementations.html#Closing_RDF4J_resources)) might
require resources to be closed after the stream
has been processed, so `.stream()` should be used within a
[try-with-resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html) block.

```java
try (Stream<? extends Triple> triples = graph.stream()) {
  Stream<RDFTerm> subjects = triples.map(t -> t.getObject());
  String s = subjects.map(RDFTerm::ntriplesString).collect(Collectors.joining(" "));
  System.out.println(s);
}
```
> ``"Alice" "Bob"``

For details about what can be done with a stream, see the
[java.util.stream documentation](http://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html).

Note that by default the stream will be parallel, use
[.sequential()](http://docs.oracle.com/javase/8/docs/api/java/util/stream/BaseStream.html#sequential--)
if your stream operations need to interact with objects that are not thread-safe.

Streams allow advanced [filter predicates](http://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#filter-java.util.function.Predicate-), but you may find that simple _subject/predicate/object_ patterns
are handled more efficiently by the implementation when using the expanded
[stream](http://commonsrdf.incubator.apache.org/apidocs/org/apache/commons/rdf/api/Graph.html#stream-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-) method. These can of course be combined:

```java
try (Stream<? extends Triple> named = graph.stream(null, nameIri, null)) {
   Stream<? extends Triple> namedB = named.filter(
       t -> t.getObject().ntriplesString().contains("B"));
   System.out.println(namedB.map(t -> t.getSubject()).findAny().get());
 }
```
> `<http://example.com/bob>`




### Removing triples

Triples can be [removed](apidocs/org/apache/commons/rdf/api/Graph.html#remove-org.apache.commons.rdf.api.Triple-) from a graph:

```java
graph.remove(triple);
System.out.println(graph.contains(triple));
```

> `false`

The expanded _subject/predicate/object_ form of
[remove()](apidocs/org/apache/commons/rdf/api/Graph.html#remove-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-)
can be used without needing to construct a `Triple` first. It also
allow `null` as a wildcard pattern:

```java
graph.remove(null, nameIri, null);
```

To remove all triples, use [clear](apidocs/org/apache/commons/rdf/api/Graph.html#clear--):

```java
graph.clear();
System.out.println(graph.contains(null, null, null));
```
> `false`

## Dataset

A [dataset](https://www.w3.org/TR/rdf11-concepts/#section-dataset)
is a collection of quads, or if you like, a collection of `Graph`s.

To create a [Dataset](apidocs/org/apache/commons/rdf/api/Dataset.html) instance
from a `RDF` implementation, use
[createDataset()](apidocs/org/apache/commons/rdf/api/RDF.html#createDataset--):

```java
Dataset dataset = rdf.createDataset();
```

Implementations will typically also have other ways of retrieving a `Dataset`,
e.g. by parsing a JSON-LD file or connecting to a storage backend.

### Dataset operations

`Dataset` operations match their equivalent operations on `Graph`, except that
methods like [add(q)](apidocs/org/apache/commons/rdf/api/Dataset.html#add-org.apache.commons.rdf.api.Quad-)
and [remove(q)](apidocs/org/apache/commons/rdf/api/Dataset.html#remove-org.apache.commons.rdf.api.Quad-)
use
[Quad](apidocs/org/apache/commons/rdf/api/Quad.html) instead of `Triple`.

```
dataset.add(quad);
System.out.println(dataset.contains(quad));
dataset.remove(quad);
```

> `true`


The convenience method [add(g,s,p,o)](apidocs/org/apache/commons/rdf/api/Dataset.html#add-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-) take an additional `BlankNodeOrIRI` parameter for the
graph name - matching `RDF.createQuad(g,s,p,o)`.

Note that the expanded pattern methods like [contains(g,s,p,o)](apidocs/org/apache/commons/rdf/api/Dataset.html#contains-java.util.Optional-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-) and
[stream(g,s,p,o)](apidocs/org/apache/commons/rdf/api/Dataset.html#stream-java.util.Optional-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-) uses `null` as a wildcard pattern, and
therefore an explicit _graph name_ parameter must be supplied as [Optional.empty()](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#empty--)  (default graph)
or wrapped using [Optional.of(g)](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#of-T-):

```
Literal foo = rdf.createLiteral("Foo");
// Match Foo in any graph, any subject, any predicate
if (dataset.contains(null, null, null, foo)) {
  System.out.println("Foo literal found");
}

// Match Foo in default graph, any subject, any predicate
if (dataset.contains(Optional.empty(), null, null, foo)) {
  System.out.println("Foo literal found in default graph");
}


BlankNodeOrIRI g1 = rdf.createIRI("http://example.com/graph1");
// Match Foo in named graph, any subject, any predicate
if (dataset.contains(Optional.of(g1), null, null, foo)) {
  System.out.println("Foo literal found in default graph");
}
```


### Graphs in the dataset

An [RDF Dataset](https://www.w3.org/TR/rdf11-concepts/#section-dataset)
is defined as:

> An RDF dataset is a collection of RDF graphs, and comprises:

> * Exactly one default graph, being an RDF graph. The default graph does not have a name and may be empty.
> * Zero or more named graphs. Each named graph is a pair consisting of an IRI or a blank node (the graph name), and an RDF graph. Graph names are unique within an RDF dataset.

It is possible to retrieve these graphs from a `Dataset` using:

* [getGraph()](apidocs/org/apache/commons/rdf/api/Dataset.html#getGraph--) for the _default graph_
* [getGraph(blankNodeOrIRI)](apidocs/org/apache/commons/rdf/api/Dataset.html#getGraph-org.apache.commons.rdf.api.BlankNodeOrIRI-) for a named graph

```
Graph defaultGraph = dataset.getGraph();
BlankNodeOrIRI graphName = rdf.createIRI("http://example.com/graph");
Optional<Graph> otherGraph = dataset.getGraph(graphName);
```

These provide a `Graph` **view** of the corresponding `Triple`s in the `Dataset`:

```
System.out.println(defaultGraph.contains(otherQuad.asTriple()));
System.out.println(defaultGraph.size());
```

> `true`
> `1`

It is unspecified if modifications to the returned Graph are
reflected in the Dataset.

Note that it is unspecified if requesting an unknown graph name will
return `Optional.empty()` or create a new (empty) `Graph`.

Some implementations may also support a _union graph_, a `Graph` that contains
all triples regardless of their graph names. _simple_ provides
[DatasetGraphView](apidocs/org/apache/commons/rdf/simple/DatasetGraphView.html)
which can be used with any `Dataset` for this purpose.



## Mutability and thread safety

_Note: This section is subject to change - see discussion on [COMMONSRDF-7](https://issues.apache.org/jira/browse/COMMONSRDF-7)_

In Commons RDF, all instances of `Triple` and `RDFTerm` (e.g. `IRI`,
`BlankNode`, `Literal`) are considered _immutable_. That is, their content does
not change, and so calling a method like
[IRI.getIRIString](apidocs/org/apache/commons/rdf/api/IRI.html#getIRIString--)
or
[Literal.getDatatype](apidocs/org/apache/commons/rdf/api/Literal.html#getDatatype--)
will have a return value which `.equals()` any earlier return
values. Being immutable, the `Triple` and `RDFTerm` types should be
considered thread-safe. Similarly their `hashCode()` should be
considered stable, so any `RDFTerm` or `Triple` can be used
in hashing collections like
[HashMap](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html).

A `Graph` may be _mutable_, particular if it supports methods like
[Graph.add](apidocs/org/apache/commons/rdf/api/Graph.html#add-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-)
and [Graph.remove](apidocs/org/apache/commons/rdf/api/Graph.html#remove-org.apache.commons.rdf.api.Triple-). That means that responses to methods like [size](apidocs/org/apache/commons/rdf/api/Graph.html#size--) and [contains](apidocs/org/apache/commons/rdf/api/Graph.html#contains-org.apache.commons.rdf.api.Triple-) might change during its lifetime. A mutable `Graph`
might also be modified by operations outside Commons RDF, e.g. because it is
backed by a shared datastore with multiple clients.

Implementations of Commons RDF may specify the (im)mutability of `Graph` in further details
in their documentation. If a graph is immutable, the methods `add` and `remove`
may throw a `UnsupportedOperationException`.

Commons RDF does not specify if methods on a `Graph` are thread-safe. Iterator
methods like [iterate](apidocs/org/apache/commons/rdf/api/Graph.html#iterate--)
and [stream](apidocs/org/apache/commons/rdf/api/Graph.html#stream-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-)
might throw a
[ConcurrentModificationException](http://docs.oracle.com/javase/8/docs/api/java/util/ConcurrentModificationException.html)
if it detects a thread concurrency modification, although this behavior is not guaranteed.
Implementations of Commons RDF may specify more specific thread-safety considerations.

If an implementation does not specify any thread-safety support, then all
potentially concurrent access to a `Graph` must be `synchronized`, e.g.:

```java
Graph graph;
// ...
synchronized(graph) {
    graph.add(triple);
}
// ...
synchronized(graph) {
    for (Triple t : graph) {
        // ...
    }
}
```



## Implementations

The [Commons RDF API](apidocs/org/apache/commons/rdf/api/package-summary.html)
is a set of Java interfaces, which can be implemented by several Java RDF
frameworks.  See the [implementations](implementations.html) page for an
updated list of providers.

Commons RDF defines a `RDF` interface as a factory for
using a particular implementations' `RDFTerm`, `Triple`, `Quad`,
`Graph` and `Dataset`. The `RDF` implementations
also add adapter/converter methods to facilitate interoperability with their
underlying framework's API.

Note that some RDF frameworks have several possibilities for creating a backend
for a `Graph` or `Dataset`, which configuration is implementation-specific.


### Cross-compatibility

While different frameworks have their own classes implementing the Commons
RDF interfaces, Commons RDF objects are cross-compatible. Thus a
client is able to mix and match objects from multiple implementations:

```java
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.jena.JenaRDF;

RDF rdf4j = new RDF4J();
JenaRDF jena = new JenaRDF();

JenaGraph jenaGraph = jena.createGraph();
// Jena-specific load method
jenaGraph.asJenaModel().read("dataset.ttl");


// Another Graph, from a different implementation
Graph rdf4jGraph = rdf4j.createGraph();  

// Any RDF implementation can make RDFTerms
IRI rdfIRI = rdf4j.createIRI("http://example.com/property1");
// and used added to a different implementation's
jenaGraph.add(rdfIRI,rdfIRI,rdfIRI);

// Both Triple and RDFTerm instances can be used
// with interoperability
for (Triple t1: g1.stream(null, iri1, null)) {
    if (g2.contains(t1.getSubject(), null, t1.getObject())) {
      g2.remove(t1);
    }
}
```

It is however generally recommended to use the matching `RDF` implementation
for operations on a `Graph` or `Dataset` as it avoids unnecessary
conversion round-trips.

_Note: The `Graph` implementation is not required to keep the JVM object
reference, e.g. after  `g2.add(subj1, pred, obj)` it is not required to later
return the same `subj1` implementation in `g2.stream()`. Special care
should be taken if returned values needs to be casted to implementation
specific types, e.g. using the appropriate adapter method from the
desired `RDF` implementation._

The `.equals()` methods of `RDFTerm`, `Triple` and `Quad`
are explicitly defined, so their instances can be compared
across implementations, and thus can safely
be used for instance as keys in a `java.util.Map` or `java.util.Set`.

_Note: Special care might need to be taken for cross-interoperability of
`BlankNode` instances. While multiple triples/quads with the same
"foreign" `BlankNode` can be added without breaking their
connections, the `Graph`/`Quad` is not required to
**return** blank node instances that `.equals()` those previously
inserted - specifically implementations are **not** expected to persist the
blank node [uniqueReference](apidocs/org/apache/commons/rdf/api/BlankNode.html#uniqueReference--)._


## Complete example

The complete source code for the examples used in this user guide can be
browsed in
[UserGuideTest.java](https://github.com/apache/commons-rdf/blob/master/examples/src/example/UserGuideTest.java)
within the
[examples](https://github.com/apache/commons-rdf/tree/master/examples)
folder of the
Commons RDF [source code repository](scm.html).
