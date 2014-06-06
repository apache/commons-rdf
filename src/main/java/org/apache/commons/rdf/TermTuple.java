package org.apache.commons.rdf;

// The common parent of Triple and Quad
public interface TermTuple {

    BlankNodeOrIRI getSubject();

    IRI getProperty();

    Resource getObject();

}
