package org.apache.commons.rdf;

public interface Triple {

    BlankNodeOrIRI getSubject();

    IRI getProperty();

    RDFTerm getObject();
}
