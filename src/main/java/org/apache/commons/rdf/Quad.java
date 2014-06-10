package org.apache.commons.rdf;

public interface Quad {

    BlankNodeOrIRI getSubject();

    IRI getProperty();

    Resource getObject();

    IRI getGraph();

    /** Project to a Triple */
    Triple toTriple() ;
}
