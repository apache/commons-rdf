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

# Apache Commons RDF (incubating)

<div class="alert alert-info" role="alert">
  <p>
    <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
    Commons RDF has transitioned to the
    <a class="alert-link" href="http://incubator.apache.org/">Apache Incubator</a>, with an aim to
    become part of
    <a class="alert-link" href="http://commons.apache.org/">Apache Commons</a>.
    This project
    was previously hosted at
    <a class="alert-link" href="http://commons-rdf.github.io/">commons-rdf.github.io</a>,
    which is not affiliated with the Apache Software Foundation.
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
  `(subject, predicate, object)` RDF triple.
* [RDFTerm](apidocs/index.html?org/apache/commons/rdf/api/RDFTerm.html): a RDF 1.1
  Term, where IRIs, literals and blank nodes are collectively known as RDF terms.
* [IRI](apidocs/index.html?org/apache/commons/rdf/api/IRI.html): an
  Internationalized Resource Identifier.
* [BlankNode](apidocs/index.html?org/apache/commons/rdf/api/BlankNode.html): a
   RDF-1.1 Blank Node, where they are disjoint from IRIs and literals.
* [BlankNodeOrIRI](apidocs/index.html?org/apache/commons/rdf/api/BlankNodeOrIRI.html):
  this interface represents the RDF Terms that may be used in the subject position
  of an RDF 1.1 `Triple`, including `BlankNode` and `IRI`.
* [Literal](apidocs/index.html?org/apache/commons/rdf/api/Literal.html): a RDF-1.1 literal.
* [RDFTermFactory](apidocs/index.html?org/apache/commons/rdf/api/RDFTermFactory.html):
  factory for creating `RDFTerm` and `Graph` instances.

The design of the [API](apidocs/index.html?org/apache/commons/rdf/api/package-summary.html)
follows the terminology as defined by [RDF 1.1 Concepts and Abstract Syntax](http://www.w3.org/TR/rdf11-concepts/),
a W3C Recommendation published on 25 February 2014. The idea is that Commons RDF
will provide a common library for RDF 1.1 that could be implemented by systems
on the Java Virtual Machine, allowing the portability across different implementations.

See the the [user guide](userguide.html) for examples of how to interact with these interfaces.

## Modules

The project is composed by two modules:

* [API](apidocs/index.html?org/apache/commons/rdf/api/package-summary.html) defines
  a common library of RDF 1.1 concepts.
* [Simple](apidocs/index.html?org/apache/commons/rdf/simple/package-summary.html)
  provides a simple implementation, mainly for internal validation and very simple
  scenarios.

Both modules follow the [semantic versioning](http://semver.org/) principles,
where version `x.y.z` of `Simple` implements version `x.y` of
the `API`; i.e., the version `z` are backwards-compatible patches of the
implementation.

External [implementations of the Commons RDF API](implementations.html) are
being developed as part of their retrospective projects.
[Contributions welcome!](contributing.html)


## Contributing

Please, take into account that this library is still <strong>work in progress</strong>,
this set of interfaces are still under discussion and evolution. Therefore everybody
is welcomed to [join the project](mail-lists.html) and [contribute](contributing.html)!

## Disclaimer

Apache Commons RDF is an effort undergoing incubation at [The Apache Software Foundation
(ASF)](http://apache.org/) sponsored by the [Apache Incubator PMC](http://incubator.apache.org/).
Incubation is required of all newly accepted projects until a further review
indicates that the infrastructure, communications, and decision making process
have stabilized in a manner consistent with other successful ASF projects.
While incubation status is not necessarily a reflection of the completeness or
stability of the code, it does indicate that the project has yet to be fully
endorsed by the ASF.

<a href="http://incubator.apache.org/"><img
  alt="Apache Incubator" src="images/apache-incubator-logo.png" height="57" width="229" /></a>
