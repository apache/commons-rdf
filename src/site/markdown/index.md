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

# Apache Commons RDF

<div class="alert alert-info" role="alert">
  <p>
    <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
    2017-12-23:
    <a href="download.html">Commons RDF 0.5.0</a> released.
  </p>
</div>

Commons RDF aims to provide a common library for [RDF 1.1](http://www.w3.org/TR/rdf11-concepts/)
that could be implemented by systems on the Java Virtual Machine.

<div style="float: right; margin-left: 6em; margin-right: 2em;">
    <a href="images/class-diagram.png">
        <img src="images/class-diagram.png" alt="Class diagram" style="height: 48em" />
    </a>
</div>

The main motivation behind this simple library is revise an historical incompatibility
issue. This library does not pretend to be a generic api wrapping those libraries,
but a set of interfaces for the RDF 1.1 concepts that can be used to expose common
RDF-1.1 concepts using common Java interfaces. In the initial phase commons-rdf
is focused on a subset of the core concepts defined by RDF-1.1 (URI/IRI, Blank Node,
Literal, Triple, and Graph). In particular, commons RDF aims to provide a type-safe,
non-general API that covers RDF 1.1. In a future phase we may define interfaces
for Datasets and Quads.

## API

The <a href="images/class-diagram.png">class diagram</a> on the right depicts the main
[interfaces](apidocs/index.html?org/apache/commons/rdf/api/package-summary.html)
which may be included in Commons RDF, specifically:

* [Graph](apidocs/index.html?org/apache/commons/rdf/api/Graph.html): a graph,
  a set of RDF triples.
* [Triple](apidocs/index.html?org/apache/commons/rdf/api/Triple.html): a
  RDF triple with `getSubject()`, `getPredicate()`, `getObject()`.
* [Dataset](apidocs/index.html?org/apache/commons/rdf/api/Dataset.html): a dataset,
  of RDF quads (or if you like, a set of named graphs).
* [Quad](apidocs/index.html?org/apache/commons/rdf/api/Quad.html): a
  RDF quad with with `getGraphName()`, `getSubject()`, `getPredicate()`, `getObject()`.
* [RDFTerm](apidocs/index.html?org/apache/commons/rdf/api/RDFTerm.html): any RDF 1.1
  Term which can be part of a Triple or Quad.
  IRIs, literals and blank nodes are collectively known as RDF terms.
* [IRI](apidocs/index.html?org/apache/commons/rdf/api/IRI.html): an
  Internationalized Resource Identifier (e.g. representing  `<http://example.com/>`)
* [BlankNode](apidocs/index.html?org/apache/commons/rdf/api/BlankNode.html): a
   RDF-1.1 Blank Node, e.g. representing `_:b1`. Disjoint from IRIs and literals.
* [BlankNodeOrIRI](apidocs/index.html?org/apache/commons/rdf/api/BlankNodeOrIRI.html):
  this interface represents the RDF Terms that may be used in the subject position
  of an RDF 1.1 `Triple`, including `BlankNode` and `IRI`.
* [Literal](apidocs/index.html?org/apache/commons/rdf/api/Literal.html): a RDF-1.1 literal, e.g.
  representing `"Hello there"@en`.

The design of the [API](apidocs/index.html?org/apache/commons/rdf/api/package-summary.html)
follows the terminology as defined by [RDF 1.1 Concepts and Abstract Syntax](http://www.w3.org/TR/rdf11-concepts/),
a W3C Recommendation published on 25 February 2014. The idea is that Commons RDF
provide a common library for RDF 1.1 with multiple implementions for
the Java Virtual Machine, allowing the portability across different
Commons RDF implementations.


Commons RDF is designed for compatibility between different
[implementations](implementations.html), e.g. by defining
strong equality and hash code semantics (e.g. for
[triple](apidocs/org/apache/commons/rdf/api/Triple.html#equals-java.lang.Object-)
and [literals](fapidocs/org/apache/commons/rdf/api/Literal.html#equals-java.lang.Object-) );
this allows users of Commons RDF to "mix and match", for instance querying a `FooGraphImpl`
and directly adding its `FooTripleImpl`s to a `BarGraphImpl` without any
explicit convertion.

To create such instances without hard-coding an implementation, one can use:

* [RDF](apidocs/index.html?org/apache/commons/rdf/api/RDF.html):
  interface for creating instances of the above types
  (e.g. `LiteralImpl` and `GraphImpl`) as well as converting from/to
  the underlying framework's API.


The API also includes a couple of "upper" interfaces  which do not have
the above equality semantics and bridge the graph/quad duality:

* [TripleLike](apidocs/index.html?org/apache/commons/rdf/api/TripleLike.html):
  common super-interface of `Triple` and `Quad` (also a generalised triple).
* [QuadLike](apidocs/index.html?org/apache/commons/rdf/api/QuadLike.html):
  a `TripleLike` that also has `getGraphName()` (a generalized quad)
* [GraphLike](apidocs/index.html?org/apache/commons/rdf/api/GraphLike.html):
  common super-interface of `Graph` and `Dataset`.


See the the [user guide](userguide.html) for examples of how to interact with these interfaces.

## Modules

The project is composed by two modules:

* [API](apidocs/index.html?org/apache/commons/rdf/api/package-summary.html) defines
  a common library of RDF 1.1 concepts.
* [Simple](apidocs/index.html?org/apache/commons/rdf/simple/package-summary.html)
  provides a simple implementation, mainly for internal validation and very simple
  scenarios.
* [jena](apidocs/index.html?org/apache/commons/rdf/jena/package-summary.html)
    provides an Apache Jena-backed implementation
* [rdf4j](apidocs/index.html?org/apache/commons/rdf/rdf4j/package-summary.html)
    provides an Eclipse RDF4J-backed implementation
* [jsonld-java](apidocs/index.html?org/apache/commons/rdf/jsonldjava/package-summary.html)
    provides an JSONLD-Java-backed implementation    

These modules follow the [semantic versioning](http://semver.org/) principles,
where version `x.y.z` of `Simple` implements version `x.y` of
the `API`; i.e., the version `z` are backwards-compatible patches of the
implementation.

For more details, read about the
[implementations of the Commons RDF API](implementations.html).


## Contributing

Everybody is welcomed to [join the project](mail-lists.html) and
[contribute](contributing.html)!

