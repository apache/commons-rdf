# Apache Commons RDF (incubating)

<div class="alert alert-info" role="alert">
  <p>
    <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
    Commons RDF is transitioning to the Apache Incubator; this website is for 
    the Apache Commons RDF project and is <strong>under development</strong>.
  </p>
  <p>
    You might want to visit the original 
    <a class="alert-link" href="http://commons-rdf.github.io/">commons-rdf.github.io</a> 
    website which is not affiliated with the Apache Software Foundation.
  </p>
</div>

Commons RDF aims to provide a common library for [RDF 1.1](http://www.w3.org/TR/rdf11-concepts/) 
that could be implemented by systems on the Java Virtual Machine.

The main motivation behind this simple library is revise an historical incompatibility 
issue. This library does not pretend to be a generic api wrapping those libraries, 
but a set of interfaces for the RDF 1.1 concepts that can be used to expose common 
RDF-1.1 concepts using common Java interfaces. In the initial phase commons-rdf 
is focused on a subset of the core concepts defined by RDF-1.1 (URI/IRI, Blank Node, 
Literal, Triple, and Graph). In particular, commons RDF aims to provide a type-safe, 
non-general API that covers RDF 1.1. In a future phase we may define interfaces 
for Datasets and Quads.

A draft diagram of the interfaces which may be included in Commons RDF are:

<a href="images/class-diagram.png"><img src="images/class-diagram.png" alt="Class diagram" style="height: 35em" /></a>

This library is still <strong>work in progress</strong>. Therefore everybody is
welcomed to [join the project](mail-lists.html) and contribute!

## Disclaimer

Apache Commons RDF is an effort undergoing incubation at [The Apache Software Foundation
(ASF)](http://apache.org/) sponsored by the [Apache Incubator PMC](http://incubator.apache.org/).
Incubation is required of all newly accepted projects until a further review
indicates that the infrastructure, communications, and decision making process
have stabilized in a manner consistent with other successful ASF projects.
While incubation status is not necessarily a reflection of the completeness or
stability of the code, it does indicate that the project has yet to be fully endorsed by the ASF.


