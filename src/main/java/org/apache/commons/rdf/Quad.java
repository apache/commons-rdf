package org.apache.commons.rdf;

public interface Quad {

    BlankNodeOrIRI getSubject();

    IRI getPredicate();

    RDFTerm getObject();

    IRI getGraph();

    /** Convert to a Triple */
    Triple toTriple() ;
}
