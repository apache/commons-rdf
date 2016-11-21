# Introduction to RDF using Commons RDF

This page is a tutorial to introduce programming with the
[Resource Description Framework (RDF)](https://www.w3.org/TR/rdf11-concepts/)
using Java and [Apache Commons RDF](index.md).
If you already know RDF, you may instead jump ahead to the
[Commons RDF user guide](userguide.html).

This is not meant as an extensive RDF tutorial, for that please consult the
[W3C RDF 1.1 Primer](https://www.w3.org/TR/rdf11-primer/). You may also like
the
[Apache Jena introduction to RDF](https://jena.apache.org/tutorials/rdf_api.html)
which uses the [Apache Jena](https://jena.apache.org/) implementation directly.

This tutorial attempts to show the basic concepts of RDF and how you can
work with RDF programmatically using the
[Apache Commons RDF API](index.md) in a simple Java program.


## Getting started with Commons RDF

This tutorial will assume you already know
a bit of [Java programming](http://docs.oracle.com/javase/tutorial/)
and that you use an _IDE_ like
[Eclipse](http://www.eclipse.org/) or
[Netbeans](https://netbeans.org/).  Note that Commons RDF requires
Open JDK 8, [Java 8](https://www.java.com/) or equivalent.

The Commons RDF JARs are [available from Maven Central](download.html).
While there are multiple [Commons RDF implementations](implementations.html),
this tutorial will use the built-in _simple_ implementation as it requires no
additional dependencies.

First, create a new Java project for this tutorial, say `rdftutorial`.

**Tip**: Check that your IDE project is using the **Java 8** syntax and compiler.

We'll create the package name
`org.example`, but you can use whatever you prefer. Then create `RdfTutorial.java`
with a static `main()` method we can run:

```java
package org.example;

import org.apache.commons.rdf.api.*;
import org.apache.commons.rdf.simple.SimpleRDF;

public class RdfTutorial {
    public static void main(String[] args) {
      // ...
    }    
}
```


### Adding Commons RDF to the class path

Above we added the `import` for the Commons RDF API, but the library
is not yet on your class path.

**Note**: If you are already familiar with
_Maven_, then see instead
[how to use Commons RDF from Maven](userguide.html#Using_Commons_RDF_from_Maven) and add
the `commons-rdf-simple` dependency to your project. This will make it easier later
to share your project or to use newer versions of Commons RDF.

This tutorial assumes a classic Java project with local `.jar` files (say in your project's `lib/` folder), so download and add to your project's class path:

* [commons-rdf-api-0.3.0-incubating.jar](https://repo.maven.apache.org/maven2/org/apache/commons/commons-rdf-api/0.3.0-incubating/commons-rdf-api-0.3.0-incubating.jar)
([signature](https://repo.maven.apache.org/maven2/org/apache/commons/commons-rdf-api/0.3.0-incubating/commons-rdf-api-0.3.0-incubating.jar.asc))
* [commons-rdf-simple-0.3.0-incubating.jar](https://repo.maven.apache.org/maven2/org/apache/commons/commons-rdf-simple/0.3.0-incubating/commons-rdf-simple-0.3.0-incubating.jar) ([signature](https://repo.maven.apache.org/maven2/org/apache/commons/commons-rdf-simple/0.3.0-incubating/commons-rdf-simple-0.3.0-incubating.jar.asc))

_Tip: If you prefer you can [verify the  signatures](https://www.apache.org/info/verification.html) using the Commons RDF [KEYS](http://www.apache.org/dist/incubator/commonsrdf/KEYS)._

As there are [multiple Commons RDF implementations](implementations.html),
we have to say which one we want to use. Add to your `RdfTutorial` class:

```java
RDF rdf = new SimpleRDF();
```

If you have the classpath set up correctly, you should now
be able to compile `RdfTutorial` without warnings.


## RDF resources

"The clue is in the name"; the _Resource Description Framework_ (RDF) is
for describing **resources**. But what is a resource?

Anything can be a resource, it is just a concept we want to describe,
like computer _files_ (text document, image, database),
_physical things_ (person, place, cat),
_locations_ (city, point on a map), or more _abstract concepts_ (organization,
disease, theatre play).

To know which concept we mean, in RDF the resource needs to either:

* have a global _identifier_; we call this an **IRI**
* be used _indirectly_ in a statement; we call this a **blank node**
* be a _value_, we call this a **literal**

In this tutorial we'll use the _IRI_ syntax `<identifier>` to indicate an identified resource,
the _blank node_ syntax `_:it` to indicate an indirectly referenced
resource, and the _literal_ syntax `"Hello"` to indicate a value.

Don't worry about this syntax, RDF is a **model** with several
ways to represent it when saved to a file; the [Commons RDF API](apidocs/index.html?org/apache/commons/rdf/api/package-summary.html) directly
reflects the RDF model in a syntax-neutral way.

Let's create our first identified resource, an `IRI` instance:

```java
IRI alice = rdf.createIRI("Alice");
System.out.println(alice.ntriplesString());
```

This should print out:

> `<Alice>`


**Note**: For simplicity this tutorial use _relative IRI references_ which
are not really global identifiers. While this is supported by
`SimpleRDF`, some implementations will require
_absolute IRIs_ like `<http://example.com/Alice>`.

### Triples


To describe a resource in RDF we provide one or more statements,
which are called _triples_ of 3 resources
(_subject_, _predicate_, _object_):

```turtle
<Alice> <knows> <Bob> .
```

![Alice knows Bob](images/rdf-01.svg)

This RDF statement is a relationship between the **subject** `<Alice>`
and the **object** `<Bob>`, not dissimilar from the subject and direct object
of the similar English sentence _"Alice knows Bob"_.

What kind of relationship? Well, that is
identified with the **predicate** `<knows>`.
The relationship is _directional_, from the subject to the object;
although _Alice knows Bob_, we don't know if Bob really knows Alice!
In RDF the predicate is also called a _property_ as it is
describing the subject.

You may have noticed that properties are also resources -
to understand
the kind of relationship we also need a description of it's concept.
More about this later!

Let's try to create the above statement
in Commons RDF; first we'll create the remaining
resources `<knows>` and `<Bob>`:

```java
IRI knows = rdf.createIRI("knows");        
IRI bob = rdf.createIRI("Bob");
```

Note that the Java variable names `alice`, `knows` and `bob`
are not important to Commons RDF,
we could as well have called these `a`, `k`, `b`,
but to not confuse yourself it's good to keep the variable names
somewhat related to the captured identifiers.

Next we'll create a `Triple`:

```java
Triple aliceKnowsBob = rdf.createTriple(alice, knows, bob);
```

We can access `.getSubject()`, `.getPredicate()` and `.getObject()` from a `Triple`:

```java
System.out.println(aliceKnowsBob.getSubject().ntriplesString());
System.out.println(aliceKnowsBob.getPredicate().ntriplesString());
System.out.println(aliceKnowsBob.getObject().ntriplesString());
```

> `<Alice>` <br>
> `<knows>` <br>
> `<Bob>`

_**Tip**: Instances from `SimpleRDF` can be printed directly, as
`System.out` would use their `.toString()`,
but for consistent behaviour across implementations we use `.ntriplesString()` above._

With `SimpleRDF` we can also print the `Triple` for debugging:

```java
System.out.println(aliceKnowsBob);
```

> `<Alice> <knows> <Bob> .`


### Graph

By using the same identified resources in multiple triples, you can
create a _graph_. For instance, this graph shows multiple relations
of `<knows>` and `<plays>`:

```turtle
<Alice> <knows> <Bob> .
<Alice> <knows> <Charlie> .
<Alice> <plays> <Tennis> .
<Bob> <knows> <Charlie> .
<Bob> <plays> <Football> .
<Charlie> <plays> <Tennis> .
```

The power of a graph as a data structure is that you don't have to decide a
hierarchy. The statements of an RDF graph can be listed in any order, and so
we should not consider the `<Alice>` resource as anything more special
than `<Bob>` or `<Tennis>`.

![Graph of Alice knows Bob and Charlie, Alice and Charlie play Tennis, Bob plays Football](images/rdf-02.svg)

It is therefore possible to _query_ the graph, such as _"Who plays Tennis?_ or
_"Who does Alice know?"_, but also more complex, like
_"Does Alice anyone that plays Football?"_.

Let's try that now using Commons RDF. To keep the triples we'll need a `Graph`:

```java
Graph graph = rdf.createGraph();
```

We already have the first triple, so we'll `.add()` it to the `graph`:

```java
graph.add(aliceKnowsBob);
```

Before adding the remaining statements we need a few more resources:

```java
IRI charlie = rdf.createIRI("Charlie");

IRI plays = rdf.createIRI("plays");

IRI football = rdf.createIRI("Football");        
IRI tennis = rdf.createIRI("Tennis");
```

Now we use the `graph.add(subj,pred,obj)` shorthand which creates the
`Triple` instances and add them to the graph.

```java
graph.add(alice, knows, charlie);
graph.add(alice, plays, tennis);
graph.add(bob, knows, charlie);
graph.add(bob, plays, football);
graph.add(charlie, plays, tennis);
```

Next we'll ask the graph those questions using `.iterate(s,p,o)` and
`null` as the wildcard.

```java
System.out.println("Who plays Tennis?");
for (Triple triple : graph.iterate(null, plays, tennis)) {
    System.out.println(triple.getSubject());
}
```

> `Who plays Tennis?` <br>
> `<Alice>` <br>
> `<Charlie>`


Notice how we only print out the `.getSubject()` (our wildcard), if you
check `.getPredicate()` or `.getObject()` you will find they are equal to
`plays` and `tennis`:

```java
System.out.println("Who plays Tennis?");
for (Triple triple : graph.iterate(null, plays, tennis)) {
    System.out.println(triple.getSubject());
    System.out.println(plays.equals(triple.getPredicate()));
    System.out.println(tennis.equals(triple.getObject()));
}
```

We can query with wildcards in any positions, for instance for
the _object_:

```java
System.out.println("Who does Alice know?");
for (Triple triple : graph.iterate(alice, knows, null)) {
    System.out.println(triple.getObject());
}
```

> `Who does Alice know?` <br>
> `<Bob>` <br>
> `<Charlie>`

Let's try to look up which of those friends play football:

```java
System.out.println("Does Alice anyone that plays Football?");
for (Triple triple : graph.iterate(alice, knows, null)) {
    RDFTerm aliceFriend = triple.getObject();
    if (graph.contains(aliceFriend, plays, football)) {
        System.out.println("Yes, " + aliceFriend);
    }
}
```

You will get a compiler error:

> `RDFTerm` cannot be converted to `BlankNodeOrIRI`

This is because in an RDF triple, not all kind of resources can be used in all
positions, and the kind of resource in Commons RDF is indicated by the
interfaces:

* [IRI](apidocs/org/apache/commons/rdf/api/IRI.html) (identified)
* [BlankNode](apidocs/org/apache/commons/rdf/api/BlankNode.html) (indirect)
* [Literal](apidocs/org/apache/commons/rdf/api/Literal.html) (value)

Look at the method signature of [graph.contains(s,p,o)](http://commonsrdf.incubator.apache.org/apidocs/org/apache/commons/rdf/api/Graph.html#contains-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-):

```java
boolean contains(BlankNodeOrIRI subject,
                 IRI predicate,
                 RDFTerm object)
```

In short, for any RDF triple:

* The **subject** must be a [BlankNodeOrIRI](apidocs/org/apache/commons/rdf/api/BlankNodeOrIRI.html), that is either a `BlankNode` or `IRI`
* The **predicate** must be a [IRI](apidocs/org/apache/commons/rdf/api/IRI.html) (so we can look up what it means)
* The **object** must be a [RDFTerm](apidocs/org/apache/commons/rdf/api/RDFTerm.html), that is either a `BlankNode`, `IRI` or `Literal`

As we are retrieving triples from the graph, the `triple.getObject()` is only known
to be an RDFTerm if we use it as a Java variable - there could in theory be triples
in the graph with `Literal` and `BlankNode` objects:

```
<Alice> <knows> "Santa Claus".
<Alice> <knows> _:someone.
```


In this case we could have done a naive casting like `(IRI)aliceFriend`; we
inserted her `IRI`-represented friends right before, but this is a toy
example - there's no need to use RDF if you already know the answer!

So unless you know for sure in your graph that `<knows>` is never used with a
literal value as object, this would not be safe. So we'll do an `instanceof`
check (skipping any literals) and cast to `BlankNodeOrIRI`:


```java
System.out.println("Does Alice anyone that plays Football?");
for (Triple triple : graph.iterate(alice, knows, null)) {
    RDFTerm aliceFriend = triple.getObject();
    if (! (aliceFriend instanceof BlankNodeOrIRI)) {
        continue;
    }
    if (graph.contains( (BlankNodeOrIRI)aliceFriend, plays, football)) {
        System.out.println("Yes, " + aliceFriend);
    }
}
```

> `Does Alice anyone that plays Football?`
> `Yes, <Bob>`

## Literal values

We talked briefly about literals above as a way to represent values in RDF.
What is a value? In a way you could a value is when we no longer want to
stay in graph land and just want to use primitive types like `long`,
`int` or `String`.  
