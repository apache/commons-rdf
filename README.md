# Commons RDF

Working repository for experimenting with idea of providing a common library for [RDF 1.1](http://www.w3.org/TR/rdf11-concepts/) that could be implemented by the upcoming versions of the main Java toolkits ([Jena](http://jena.apache.org) 3.0 and [Sesame](http://openrdf.callimachus.net) 4.0) as well as wrappers for other JVM languages ([Banana RDF](https://github.com/w3c/banana-rdf) and so on).

The main motivation behind this simple library is revise an historical incompatibility issue. This library does not pretend to be a generic api wrapping those libraries, but a set of interfaces for the RDF 1.1 concepts. In the initial phase just about a subset of the core concepts defined by RDF-1.1 (URI/IRI, Resource, Blank Node, Literal, Triple, Quad, etc). In a future phase we may define interfaces for Graphs and Datasets.

## Status

This library is still work in progress. From the very beginning it is using a package from [Apache Commons](http://commons.apache.org) without permission. That's because at some point we would like to submit it there, so we would like to avoid naming issues on such migration.

This is the current draft with the basic design:

![commons-rdf class diagram](src/main/resources/commons-rdf-class-diagram.png "commons-rdf class diagram")

## Contributors

* Sergio Fernández ([Apache Marmotta](http://marmotta.apache.org))
* Andy Seaborne ([Apache Jena](http://jena.apache.org))
* Peter Ansell ([OpenRDF Sesame](http://openrdf.callimachus.net))
* Alexandre Bertails ([W3C](http://www.w3.org))

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
