package org.apache.commons.rdf;

public interface Quad {

    BlankNodeOrIRI getSubject();

    IRI getProperty();

    RDFTerm getObject();

    IRI getGraph();

    /**
     * Convert to a Triple
     */
    Triple toTriple();
}
