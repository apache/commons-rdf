package org.apache.commons.rdf;

public interface Quad {

    Resource getSubject();

    IRI getProperty();

    RDFTerm getObject();

    IRI getGraph();

    /**
     * Convert to a Triple
     */
    Triple asTriple();

}
