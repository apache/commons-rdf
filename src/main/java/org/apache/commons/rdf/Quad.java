package org.apache.commons.rdf;

public interface Quad extends TermTuple {
    // Repeat for clarity

    BlankNodeOrIRI getSubject();

    IRI getProperty();

    Resource getObject();

    IRI getGraph();

    /** View as a Triple */
    Triple asTriple() ;
}
