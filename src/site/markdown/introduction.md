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

The Commons RDF JARs are [available from Maven Central](download.html#Maven).
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

* [commons-rdf-api-0.5.0.jar](https://repo.maven.apache.org/maven2/org/apache/commons/commons-rdf-api/0.5.0/commons-rdf-api-0.5.0.jar)
([signature](https://repo.maven.apache.org/maven2/org/apache/commons/commons-rdf-api/0.5.0/commons-rdf-api-0.5.0.jar.asc))
* [commons-rdf-simple-0.5.0.jar](https://repo.maven.apache.org/maven2/org/apache/commons/commons-rdf-simple/0.5.0/commons-rdf-simple-0.5.0.jar) ([signature](https://repo.maven.apache.org/maven2/org/apache/commons/commons-rdf-simple/0.5.0/commons-rdf-simple-0.5.0.jar.asc))

_Tip: If you prefer you can [verify the  signatures](https://www.apache.org/info/verification.html) using the Apache Commons [KEYS](https://www.apache.org/dist/commons/KEYS)._

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

> `<Alice>` <br />
> `<knows>` <br />
> `<Bob>`

_**Tip**: Instances from `SimpleRDF` can be printed directly, as
`System.out` would use their `.toString()`,
but for consistent behavior across implementations we use `.ntriplesString()` above._

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
_"Does Alice know anyone that plays Football?"_.

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

> `Who plays Tennis?` <br />
> `<Alice>` <br />
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

> `Who does Alice know?` <br />
> `<Bob>` <br />
> `<Charlie>`

Let's try to look up which of those friends play football:

```java
System.out.println("Does Alice know anyone that plays Football?");
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

Look at the method signature of [graph.contains(s,p,o)](apidocs/org/apache/commons/rdf/api/Graph.html#contains-org.apache.commons.rdf.api.BlankNodeOrIRI-org.apache.commons.rdf.api.IRI-org.apache.commons.rdf.api.RDFTerm-):

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

```turtle
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
System.out.println("Does Alice know anyone that plays Football?");
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

> `Does Alice know anyone that plays Football?` <br />
> `Yes, <Bob>`

## Literal values

We talked briefly about literals above as a way to represent _values_ in RDF.
What is a literal value? In a way you could think of a value as when you no longer
want to stay in graph-land of related resources, and just want to use primitive
types like `float`, `int` or `String` to represent values like
a player rating, the number of matches played, or the full name of a person
(including spaces and punctuation which don't work well in an identifier).

Such values are in Commons RDF represented as instances of `Literal`,
which we can create using `rdf.createLiteral(..)`. Strings are easy:

```java
Literal aliceName = rdf.createLiteral("Alice W. Land");
```

We can then add a triple that relates the resource `<Alice>`
to this value, let's use a new predicate `<name>`:

```java
IRI name = rdf.createIRI("name");
graph.add(alice, name, aliceName);
```

When you look up literal properties in a graph,
take care that in RDF a property is not necessarily _functional_, that is,
it would be perfectly valid RDF-wise for a person to have multiple names;
Alice might also be called _"Alice Land"_.  

Instead of using `graph.iterate()` and `break` in a for-loop, it might be easier to use the
Java 8 `Stream` returned from `.stream()` together with `.findAny()`
- which  return an `Optional` in case there is no `<name>`:

```java
System.out.println(graph.stream(alice, name, null).findAny());
```

> `Optional[<Alice> <name> "Alice W. Land" .]`

**Note:** Using `.findFirst()` will not returned the "first"
recorded triple, as triples in a graph are not necessarily
kept in order.

You can use `optional.isPresent()` and `optional.get()` to check if a
`Triple` matched the graph stream pattern:

```java
import java.util.Optional;
// ...
Optional<? extends Triple> nameTriple = graph.stream(alice, name, null).findAny();
if (nameTriple.isPresent()) {
    System.out.println(nameTriple.get());
}
```

If you feel adventerous, you can try the
[Java 8 functional programming](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html)
style to work with of `Stream` and `Optional` and get the literal value unquoted:

```java
graph.stream(alice, name, null)
        .findAny().map(Triple::getObject)
        .filter(obj -> obj instanceof Literal)
        .map(literalName -> ((Literal)literalName).getLexicalForm())
        .ifPresent(System.out::println);
```

> `Alice W. Land`

Notice how we here used a `.filter` to skip any non-`Literal` names
(which would not have the `.getLexicalForm()` method).



### Typed literals

Non-String value types are represented in RDF as _typed literals_;
which is similar to (but not the same as) Java native types. A
typed literal is a combination of a _string representation_
(e.g. "13.37") and a data type IRI, e.g. `<http://www.w3.org/2001/XMLSchema#float>`.
RDF reuse the XSD datatypes.

A collection of the standardized datatype `IRI`s
are provided in Simple's [Types](apidocs/org/apache/commons/rdf/simple/Types.html)
class, which we can use with `createLiteral` by adding the corresponding `import`:

```java
import org.apache.commons.rdf.simple.Types;
// ...
IRI playerRating = rdf.createIRI("playerRating");
Literal aliceRating = rdf.createLiteral("13.37", Types.XSD_FLOAT);
graph.add(alice, playerRating, aliceRating);
```

Note that Commons RDF does not currently provide converters
from/to native Java data types and the RDF string representations.

### Language-specific literals

We live in a globalized world, with many spoken and written languages.
While we can often agree about a concept like `<Football>`, different
languages might call it differently. The distinction in RDF
between identified resources and literal values, mean we can represent
names or labels for the same thing.

Rather than introducing language-specific predicates like
`<name_in_english>` and `<name_in_norwegian>`
it is usually better in RDF to use _language-typed literals_:

```java
Literal footballInEnglish = rdf.createLiteral("football", "en");
Literal footballInNorwegian = rdf.createLiteral("fotball", "no");

graph.add(football, name, footballInEnglish);
graph.add(football, name, footballInNorwegian);
```

The language tags like `"en"` and `"no"` are
identified by [BCP47](https://tools.ietf.org/html/bcp47) - you can't just make
up your own but must use one that matches the language. It is possible to use
localized languages as well, e.g.

```java
Literal footballInAmericanEnglish = rdf.createLiteral("soccer", "en-US");
graph.add(football, name, footballInAmericanEnglish);
```

Note that Commons RDF does not currently provide constants for
the standardized languages or methods to look up localized languages.

## Blank nodes - when you don't know the identity

Sometimes you don't know the identity of a resource. This can be the case
where you know the _existence_ of a resource, similar to "someone" or "some"
in English.  For instance,

```turtle
<Charlie> <knows> _:someone .
_:someone <plays> <Football> .
```

We don't know who this `_:someone` is, it could be `<Bob>` (which we know
plays football), it could be someone else, even `<Alice>`
(we don't know that she doesn't play football).

In RDF we represent `_:someone` as a _blank node_ - it's a resource without
a global identity.  Different RDF files can all talk about `_:blanknode`, but they
would all be different resources.  Crucially, a blank node can be used
in multiple triples within the same graph, so that we can relate
a subject to a blank node resource, and then describe the blank node further.

Let's add some blank node statements to our graph:

```turtle
BlankNode someone = rdf.createBlankNode();
graph.add(charlie, knows, someone);
graph.add(someone, plays, football);
BlankNode someoneElse = rdf.createBlankNode();
graph.add(charlie, knows, someoneElse);
```

Every call to `rdf.createBlankNode()` creates a new, unrelated blank node
with an internal identifier. Let's have a look:

```java
for (Triple heKnows : graph.iterate(charlie, knows, null)) {
    if (! (heKnows.getObject() instanceof BlankNodeOrIRI)) {
        continue;
    }
    BlankNodeOrIRI who = (BlankNodeOrIRI)heKnows.getObject();
    System.out.println("Charlie knows "+ who);
    for (Triple whoPlays : graph.iterate(who, plays, null)) {
        System.out.println("  who plays " + whoPlays.getObject());
    }
}      
```

> `Charlie knows _:ae4115fb-86bf-3330-bc3b-713810e5a1ea` <br />
> `  who plays <Football>` <br />
> `Charlie knows _:884d5c05-93a9-3709-b655-4152c2e51258`

As we see above, given a `BlankNode` instance it is perfectly
valid to ask the same `Graph` about further triples
relating to the `BlankNode`. (Asking any other graph will probably
not give any results).

### Blank node labels

In Commons RDF it is also possible to create a blank node from a
_name_ or _label_ - which can be useful if you don't want to keep or retrieve
the `BlankNode` instance to later add statements about the same node.

Let's first delete the old `BlankNode` statements:

```java
graph.remove(null,null,someone);
graph.remove(someone,null,null);
```

And now we'll try an alternate approach:

```java
// no Java variable for the new BlankNode instance
graph.add(charlie, knows, rdf.createBlankNode("someone"));        
// at any point later (with the same RDF instance)
graph.add(rdf.createBlankNode("someone"), plays, football);
```


Running the `"Charlie knows"` query again (try making it into a function)
should still work, but now return a different label for the football
player:


> `Charlie knows _:5e2a75b2-33b4-3bb8-b2dc-019d42c2215a` <br />
> `  who plays <Football>` <br />
> `Charlie knows _:884d5c05-93a9-3709-b655-4152c2e51258`


You may notice that with `SimpleRDF` the string `"someone"` does **not**
survive into the string representation of the `BlankNode` label as `_:someone`,
that is because unlike `IRI`s the label of a blank node carries no meaning
and does not need to be preserved.

Note that it needs to be the same `RDF` instance to recreate
the same _"someone"_ `BlankNode`.  This is a Commons RDF-specific
behavior to improve cross-graph compatibility, other RDF frameworks may save
the blank node using the provided label as-is with a `_:` prefix,
which in some cases could cause collisions (but perhaps more readable output).


### Open world assumption

How to interpret a blank node depends on the assumptions you build into your
RDF application - it could be thought of as a logical "there exists a resource that.."
or a more pragmatic "I don't know/care about the resource's IRI". Blank nodes can be
useful if your RDF model describes intermediate resources like
"a person's membership of an organization" or "a participant's result in a race"
which it often is not worth maintaining identifiers for.

It is common on the semantic web to use the
[open world assumption](http://wiki.opensemanticframework.org/index.php/Overview_of_the_Open_World_Assumption) -
if it is not stated as a _triple_ in your graph, then you don't know if
something is is true or false,
for instance if `<Alice> <plays> <Football> .`  

Note that the open world assumption applies both to `IRI`s and `BlankNode`s,
that is, you can't necessarily assume that the
resources `<Alice>` and `<Charlie>` describe
two different people just because they have
two different identifiers - in fact it is very common that different systems use
different identifiers to describe the same (or pretty much the same) thing in the
real world.

It is however common for applications to
"close the world"; saying "given this information I have
gathered as RDF, I'll assume these resources are all separate things in the world,
then do I then know if `<Alice> <plays> <Football>` is false?".

Using logical _inference rules_ and _ontologies_
is one method to get stronger assumptions and conclusions.
Note that building good rules or ontologies requires a fair
bit more knowledge than what can be conveyed in this short tutorial.

It is out of scope for Commons RDF to support the many ways to deal with
logical assumptions and conclusions, however you may find interest in using
[Jena implementation](implementations.html#Apache_Jena)
combined with Jena's [ontology API](https://jena.apache.org/documentation/ontology/).
